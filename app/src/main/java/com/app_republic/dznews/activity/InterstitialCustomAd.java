package com.app_republic.dznews.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.app_republic.dznews.R;
import com.app_republic.dznews.pojo.Advert;
import com.app_republic.dznews.utils.AppSingleton;
import com.app_republic.dznews.utils.StaticConfig;
import com.app_republic.dznews.utils.Utils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;


public class InterstitialCustomAd extends DialogFragment {

    ImageView imageView;
    FloatingActionButton BT_close;
    Button BT_button, BT_next;
    TextView TV_body;
    AppSingleton appSingleton;
    Utils.InterstitialAdListener interstitialAdListener;

    public InterstitialCustomAd() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.DialogTheme);
    }

    @Override
    public void onStart() {

        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = getDialog();

        dialog = super.onCreateDialog(savedInstanceState);
        //   dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;

    }



    public InterstitialCustomAd(Utils.InterstitialAdListener interstitialAdListener) {
        super();
        this.interstitialAdListener = interstitialAdListener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.advert_interstitial, container, false);

        appSingleton = AppSingleton.getInstance(getContext());

        BT_button = view.findViewById(R.id.button);
        BT_next = view.findViewById(R.id.next);
        TV_body = view.findViewById(R.id.body);

        imageView = view.findViewById(R.id.imageView);
        BT_close = view.findViewById(R.id.close);

        Advert advert = getArguments().getParcelable(StaticConfig.ADVERT);


        BT_button.setText(advert.getText());
        if (!advert.getImage().isEmpty())
            appSingleton.getPicasso().load(advert.getImage()).into(imageView);

        BT_close.setOnClickListener(view1 -> {
            dismiss();
        });

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("custom_ads", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        BT_button.setOnClickListener(view1 -> {
            editor.putBoolean(advert.getId(), true);
            editor.apply();

            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(advert.getUrl()));
            startActivity(browserIntent);

        });

        BT_next.setOnClickListener(view1 -> {
            dismiss();
        });

        imageView.setOnClickListener(view1 -> {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(advert.getUrl()));
            startActivity(browserIntent);
        });

        WebView webView = view.findViewById(R.id.webview);

        if (advert.getWebView() != null && !advert.getWebView().isEmpty()) {
            webView.setVisibility(View.VISIBLE);
            webView.clearCache(true);
            webView.clearHistory();
            webView.getSettings().setJavaScriptEnabled(true);
            webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
            webView.loadUrl(advert.getWebView());
        }

        if (advert.getBody() != null && !advert.getBody().isEmpty()) {
            TV_body.setVisibility(View.VISIBLE);
            TV_body.setText(advert.getBody());
        }

        return view;
    }


    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        interstitialAdListener.done();
    }
}
