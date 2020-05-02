package com.app_republic.dznews.activity;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.app_republic.dznews.utils.AppSingleton;
import com.app_republic.dznews.R;
import com.app_republic.dznews.utils.Utils;
import com.app_republic.dznews.pojo.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;


public class RegisterActivity extends AppCompatActivity implements View.OnClickListener, Utils.OnImageUpload {
    private static final int GALLERY_REQUEST_CODE = 11;
    private static final int GALLERY_PERMISSION = 10;


    EditText ET_name, ET_email, ET_password, ET_repeat_password;
    User user;
    Bitmap userPhoto;
    Bitmap bitmap;

    FirebaseAuth firebaseAuth;
    Button BT_next;
    ImageView IV_userPhoto;
    private ProgressDialog imageUploadDialog;
    FirebaseFirestore db;
    AppSingleton appSingleton;
    FirebaseUser firebaseUser;
    View V_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        appSingleton = AppSingleton.getInstance(this);
        db = appSingleton.getDb();
        firebaseAuth = appSingleton.getFirebaseAuth();

        user = new User();


        ET_name = findViewById(R.id.name);
        ET_email = findViewById(R.id.email);
        ET_password = findViewById(R.id.password);
        ET_repeat_password = findViewById(R.id.password_repeat);
        BT_next = findViewById(R.id.next);
        IV_userPhoto = findViewById(R.id.photo);


        V_back = findViewById(R.id.back_layout);
        V_back.setOnClickListener(this);

        BT_next.setOnClickListener(this);
        IV_userPhoto.setOnClickListener(this);

        imageUploadDialog = new ProgressDialog(this);
        imageUploadDialog.setCancelable(false);
        imageUploadDialog.setTitle(R.string.uploading_photo);


    }

    private boolean validateUserInfo() {
        boolean validate = true;

        String name = ET_name.getText().toString();
        String email = ET_email.getText().toString();
        String password = ET_password.getText().toString();
        String repeat_password = ET_repeat_password.getText().toString();


        user.setName(name);
        user.setEmail(email);

        if (password.length() < 6) {
            ET_password.setError(getString(R.string.password_error));
            validate = false;
        } else
            ET_password.setError(null);

        if (!repeat_password.equals(password)) {
            ET_repeat_password.setError(getString(R.string.password_error_repeat));
            validate = false;
        } else
            ET_repeat_password.setError(null);


        if (!Utils.isValidEmail(email)) {
            ET_email.setError(getString(R.string.email_error));
            validate = false;
        } else
            ET_email.setError(null);


        if (name.isEmpty()) {
            ET_name.setError(getString(R.string.field_required));
            validate = false;
        } else
            ET_name.setError(null);


        return validate;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.next:
                if (validateUserInfo()) {
                    addFirebaseUser(user.getEmail(), ET_password.getText().toString());
                }
                break;
            case R.id.photo:
                try {
                    if (ActivityCompat.checkSelfPermission(this,
                            Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(this,
                                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                                        Manifest.permission.WRITE_EXTERNAL_STORAGE}, GALLERY_PERMISSION);
                    } else {
                        pickFromGallery();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            case R.id.back_layout:
                onBackPressed();
                break;
        }
    }


    private void addFirebaseUser(String email, String password) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        firebaseUser = task.getResult().getUser();
                        if (bitmap != null) {
                            imageUploadDialog.show();
                            Utils.uploadBitmap(this, bitmap, firebaseUser.getUid());
                        }
                        else
                            Utils.saveUserProfile(this, user, firebaseUser.getUid());
                    } else {
                        Toast.makeText(RegisterActivity.this,
                                task.getException().getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void pickFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        String[] mimeTypes = {"image/jpeg", "image/png"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        startActivityForResult(intent, GALLERY_REQUEST_CODE);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK)
            switch (requestCode) {
                case GALLERY_REQUEST_CODE:
                    //data.getData returns the content URI for the selected Image
                    Uri selectedImage = data.getData();

                    String[] filePath = {MediaStore.Images.Media.DATA};
                    Cursor cursor = getContentResolver().query(selectedImage, filePath,
                            null, null, null);
                    cursor.moveToFirst();
                    String imagePath = cursor.getString(cursor.getColumnIndex(filePath[0]));

                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                    bitmap = BitmapFactory.decodeFile(imagePath, options);

                    IV_userPhoto.setImageBitmap(bitmap);


                    cursor.close();
                    break;

            }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case GALLERY_PERMISSION:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    pickFromGallery();
                } else {
                    //do something like displaying a message that he didn`t allow the app to access gallery and you wont be able to let him select from gallery
                }
                break;
        }
    }

    @Override
    public void imageUploaded(Uri uri) {
        user.setPhoto(uri.toString());
        Utils.saveUserProfile(this, user, firebaseUser.getUid());
        imageUploadDialog.hide();
    }

    @Override
    public void failed() {
        Utils.saveUserProfile(this, user, firebaseUser.getUid());
        imageUploadDialog.hide();

    }


}
