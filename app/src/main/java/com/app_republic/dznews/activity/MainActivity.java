package com.app_republic.dznews.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;

import com.app_republic.dznews.R;
import com.app_republic.dznews.pojo.Article;
import com.app_republic.dznews.utils.AppSingleton;
import com.app_republic.dznews.utils.StaticConfig;
import com.app_republic.dznews.utils.Utils;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import static java.security.AccessController.getContext;

public class MainActivity extends AppCompatActivity {

    private TextView TV_title;
    private View layout_settings;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MobileAds.initialize(this, initializationStatus -> {
        });

        setContentView(R.layout.activity_main);

        TV_title = findViewById(R.id.title);
        layout_settings = findViewById(R.id.settings_layout);

        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setItemIconTintList(null);

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_recent, R.id.navigation_corona)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupWithNavController(navView, navController);

        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            switch (destination.getId()) {
                case R.id.navigation_home:
                    TV_title.setText(getString(R.string.app_name));
                    break;
                case R.id.navigation_recent:
                    TV_title.setText(getString(R.string.recent_news));

                    break;
                case R.id.navigation_corona:
                    TV_title.setText(getString(R.string.coronavirus));
                    break;
                case R.id.navigation_saved:
                    TV_title.setText(getString(R.string.saved));
                    break;
            }
        });


        layout_settings.setOnClickListener(view -> {
            startActivity(new Intent(this, SettingsActivity.class));
        });

        if (getIntent().getExtras() != null) {
            Gson gson = new Gson();
            Article article = gson.fromJson(getIntent().getExtras().getString("article"), Article.class);
            if (article != null) {
                getIntent().removeExtra("article");
                Intent intent = new Intent(MainActivity.this, NewsItemActivity.class);
                intent.putExtra(StaticConfig.ARTICLE, article);
                startActivity(intent);
            }
        }

        Utils.loadBannerAd(this, "main");


    }

    @Override
    protected void onResume() {
        super.onResume();
        AppSingleton.getInstance(this).getInterstitialAd().loadAd(new AdRequest.Builder().build());
    }

}
