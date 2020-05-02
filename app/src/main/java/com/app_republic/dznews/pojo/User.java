package com.app_republic.dznews.pojo;

import android.os.Parcel;
import android.os.Parcelable;

public class User implements Parcelable {
    private  String name, photo, email;

    public User(String name, String photo, String email) {
        this.name = name;
        this.photo = photo;
        this.email = email;
    }

    public User() {

    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    protected User(Parcel in) {
        name = in.readString();
        photo = in.readString();
        email = in.readString();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeString(photo);
        parcel.writeString(email);
    }
}
