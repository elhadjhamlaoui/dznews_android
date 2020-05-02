package com.app_republic.dznews.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.preference.PreferenceFragmentCompat;

import com.app_republic.dznews.R;
import com.app_republic.dznews.utils.AppSingleton;
import com.facebook.login.LoginManager;

import java.util.ArrayList;

public class SettingsActivity extends AppCompatActivity {

    private ConstraintLayout sources;
    private Button BT_login;
    private AppSingleton appSingleton;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private View notifications_layout;
    private Switch notifications;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);

        appSingleton = AppSingleton.getInstance(this);

        sources = findViewById(R.id.sources);
        BT_login = findViewById(R.id.login);

        notifications_layout = findViewById(R.id.notifications_layout);
        notifications = findViewById(R.id.notifications);


        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        if (appSingleton.getUserLocalStore().isLoggedIn()) {
            BT_login.setText(getString(R.string.logout_user));

        } else {
            BT_login.setText(getString(R.string.login_user));

        }

        sharedPreferences = getSharedPreferences("sources", MODE_PRIVATE);
        editor = sharedPreferences.edit();



        sources.setOnClickListener(view -> {
            multitpleChoiceListDialog();
        });

        BT_login.setOnClickListener(view -> {
            if (appSingleton.getUserLocalStore().isLoggedIn()) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(getString(R.string.want_logout));
                builder.setPositiveButton(getString(R.string.yes), (dialogInterface, i) -> {
                    appSingleton.getFirebaseAuth().signOut();
                    BT_login.setText(getString(R.string.login_user));
                    appSingleton.getUserLocalStore().clearUserData();
                    appSingleton.getUserLocalStore().setUserLoggedIn(false);
                    LoginManager.getInstance().logOut();
                });
                builder.setNeutralButton(getString(R.string.no), (dialogInterface, i) -> {
                    dialogInterface.dismiss();
                });

                builder.show();

            } else {
                finish();
                startActivity(new Intent(this, LoginActivity.class));
            }
        });

        SharedPreferences sharedPreferences = getSharedPreferences("notifications", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        notifications.setChecked(sharedPreferences.getBoolean("enabled", true));


        notifications_layout.setOnClickListener(view -> {
            notifications.setChecked(!notifications.isChecked());
        });

        notifications.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (!isChecked) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(getString(R.string.want_disable_notifications));
                builder.setNegativeButton(getString(R.string.yes), (dialogInterface, i) -> {
                    editor.putBoolean("enabled", isChecked);
                    editor.apply();
                    dialogInterface.dismiss();
                });
                builder.setPositiveButton(getString(R.string.no), (dialogInterface, i) -> {
                    notifications.setChecked(!isChecked);
                    dialogInterface.dismiss();
                });
                builder.setCancelable(false);

                builder.show();
            } else {
                editor.putBoolean("enabled", isChecked);
                editor.apply();
            }

        });
    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }
    public AlertDialog multitpleChoiceListDialog() {
        String[] sources = getResources().getStringArray(R.array.sources);
        final boolean[] selectedSources = {sharedPreferences.getBoolean(sources[0], true),
                sharedPreferences.getBoolean(sources[1], true),
                sharedPreferences.getBoolean(sources[2], true),
                sharedPreferences.getBoolean(sources[3], true)};

        final ArrayList<String> selectedItems = new ArrayList();
        final ArrayList<String> removedItems = new ArrayList();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("اختر المصادر")
                .setMultiChoiceItems(sources, selectedSources,
                        (dialog, which, isChecked) -> {
                            if (isChecked) {
                                selectedItems.add(sources[which]);
                                if (removedItems.contains(sources[which]))
                                removedItems.remove(sources[which]);
                            } else {
                                if (selectedItems.contains(sources[which]))
                                selectedItems.remove(sources[which]);
                                removedItems.add(sources[which]);
                            }
                        })
                .setPositiveButton("Ok", (dialog, id) -> {
                    for (String source : selectedItems) {
                        editor.putBoolean(source, true);
                    }
                    for (String source : removedItems) {
                        editor.putBoolean(source, false);
                    }

                    editor.apply();
                })
                .setNegativeButton("Cancel", (dialog, id) -> {
                    dialog.dismiss();
                });

        return builder.show();
    }

}