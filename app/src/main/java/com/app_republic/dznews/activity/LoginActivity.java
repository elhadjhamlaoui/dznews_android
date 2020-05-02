package com.app_republic.dznews.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.app_republic.dznews.utils.AppSingleton;
import com.app_republic.dznews.R;
import com.app_republic.dznews.utils.UserLocalStore;
import com.app_republic.dznews.utils.Utils;
import com.app_republic.dznews.pojo.User;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.Arrays;


public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int RC_SIGN_IN = 0;
    CallbackManager callbackManager;
    Button googleButton, facebookButton, login;
    GoogleSignInClient mGoogleSignInClient;
    TextView TV_signup;
    GoogleSignInAccount account;
    EditText ET_email, ET_password;

    FirebaseAuth firebaseAuth;
    UserLocalStore userLocalStore;
    User user;

    View V_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        userLocalStore = new UserLocalStore(this);

        user = new User();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setEnterTransition(null);
        }

        ET_password = findViewById(R.id.password);
        ET_email = findViewById(R.id.email);
        V_back = findViewById(R.id.back_layout);
        V_back.setOnClickListener(this);


        facebookButton = findViewById(R.id.btn_fb_login);
        googleButton = findViewById(R.id.sign_in_button);

        TV_signup = findViewById(R.id.signup);

        login = findViewById(R.id.login);

        googleButton.setOnClickListener(this);

        callbackManager = CallbackManager.Factory.create();

        firebaseAuth = AppSingleton.getInstance(this).getFirebaseAuth();

        GoogleSignInOptions gso = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.google_login_app_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        account = GoogleSignIn.getLastSignedInAccount(this);


        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        // TODO: 8/21/2019 send token to server
                        // TODO: 8/20/2019 retreive user data - > go to main screen
                        //sendTokenToServer(Constant.LOGIN_TYPE_FACEBOOK, loginResult.getAccessToken().getToken());

                        handleFacebookAccessToken(loginResult.getAccessToken());
                    }

                    @Override
                    public void onCancel() {
                        Toast.makeText(LoginActivity.this, "Login Cancel",
                                Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        Toast.makeText(LoginActivity.this, exception.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });


        facebookButton.setOnClickListener(this);
        login.setOnClickListener(this);
        TV_signup.setOnClickListener(this);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == RC_SIGN_IN) {

            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            if (task.isSuccessful()) {
                handleSignInResult(task);
            } else {
                Toast.makeText(this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        } else {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
        super.onActivityResult(requestCode, resultCode, data);

    }


    private void googleSignIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            // TODO(developer): send ID Token to server and validate
            //sendTokenToServer(Constant.LOGIN_TYPE_GOOGLE, account.getIdToken());
            firebaseAuthWithGoogle(account);
        } catch (ApiException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }


    private void startRegisterActivity() {
        Intent intent = new Intent(LoginActivity.this,
                RegisterActivity.class);
        startActivity(intent);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        startActivity(new Intent(this, MainActivity.class));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.sign_in_button:
                googleSignIn();
                break;

            case R.id.btn_fb_login:
                LoginManager.getInstance().logInWithReadPermissions(LoginActivity.this,
                        Arrays.asList("public_profile", "user_friends"));
                break;
            case R.id.login:
                if (validateUserInfo()) {
                    loginWithEmailAndPassword(ET_email.getText().toString(),
                            ET_password.getText().toString()
                    );
                }
                break;
            case R.id.signup:
                startRegisterActivity();
                break;
            case R.id.back_layout:
                onBackPressed();
                break;
        }
    }


    private boolean validateUserInfo() {
        boolean validate = true;
        String email = ET_email.getText().toString();
        String password = ET_password.getText().toString();

        if (password.length() < 6) {
            ET_password.setError(getString(R.string.password_error));
            validate = false;
        } else
            ET_password.setError(null);

        if (email.length() < 6) {
            ET_email.setError(getString(R.string.email_error));
            validate = false;
        } else
            ET_email.setError(null);


        return validate;
    }


    private void loginWithEmailAndPassword(String email, String password) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser firebaseUser = task.getResult().getUser();
                            Utils.getUserProfile(LoginActivity.this, firebaseUser.getUid());
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(LoginActivity.this,
                                    task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }

                    }
                });

    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser firebaseUser = task.getResult().getUser();
                            boolean isNewUser = task.getResult().getAdditionalUserInfo().isNewUser();
                            if (isNewUser) {
                                if (firebaseUser.getPhotoUrl() != null)
                                    user.setPhoto(firebaseUser.getPhotoUrl().toString());
                                user.setName(firebaseUser.getDisplayName());
                                user.setEmail(firebaseUser.getEmail());

                                Utils.saveUserProfile(LoginActivity.this, user, firebaseUser.getUid());

                            } else {
                                Utils.getUserProfile(LoginActivity.this, firebaseUser.getUid());
                            }

                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(LoginActivity.this,
                                    task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }

                    }
                });

    }

    private void handleFacebookAccessToken(AccessToken token) {
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        FirebaseUser firebaseUser = task.getResult().getUser();
                        boolean isNewUser = task.getResult().getAdditionalUserInfo().isNewUser();

                        if (isNewUser) {

                            if (firebaseUser.getPhotoUrl() != null)
                                user.setPhoto(firebaseUser.getPhotoUrl().toString());
                            user.setName(firebaseUser.getDisplayName());
                            user.setEmail(firebaseUser.getEmail());

                            Utils.saveUserProfile(LoginActivity.this, user, firebaseUser.getUid());
                        } else {
                            Utils.getUserProfile(LoginActivity.this, firebaseUser.getUid());
                        }
                    } else {
                        // If sign in fails, display a message to the user.
                        Toast.makeText(LoginActivity.this,
                                task.getException().getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }

                });

    }
}
