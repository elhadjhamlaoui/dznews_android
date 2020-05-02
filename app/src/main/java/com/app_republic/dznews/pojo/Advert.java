package com.app_republic.dznews.pojo;

import android.os.Parcel;
import android.os.Parcelable;

public class Advert implements Parcelable {
    private String id, text, image, url, type, screen, webView, body;

    public Advert() {
    }

    public Advert(String id, String text, String image, String url, String type, String screen, String webView, String body) {
        this.id = id;
        this.text = text;
        this.image = image;
        this.url = url;
        this.type = type;
        this.screen = screen;
        this.webView = webView;
        this.body = body;
    }

    protected Advert(Parcel in) {
        id = in.readString();
        text = in.readString();
        image = in.readString();
        url = in.readString();
        type = in.readString();
        screen = in.readString();
        webView = in.readString();
        body = in.readString();
    }

    public static final Creator<Advert> CREATOR = new Creator<Advert>() {
        @Override
        public Advert createFromParcel(Parcel in) {
            return new Advert(in);
        }

        @Override
        public Advert[] newArray(int size) {
            return new Advert[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getScreen() {
        return screen;
    }

    public void setScreen(String screen) {
        this.screen = screen;
    }

    public String getWebView() {
        return webView;
    }

    public void setWebView(String webView) {
        this.webView = webView;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(text);
        parcel.writeString(image);
        parcel.writeString(url);
        parcel.writeString(type);
        parcel.writeString(screen);
        parcel.writeString(webView);
        parcel.writeString(body);
    }
}
