package com.app_republic.dznews.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.app_republic.dznews.pojo.User;
import com.google.gson.Gson;


public class UserLocalStore {

    public static final String SP_NAME = "userDetails";

    SharedPreferences userLocalDatabase;
    Gson gson;

    public UserLocalStore(Context context) {
        userLocalDatabase = context.getSharedPreferences(SP_NAME, 0);
        gson = AppSingleton.getInstance(context).getGson();
    }

    public void storeUserData(User user) {
        SharedPreferences.Editor userLocalDatabaseEditor = userLocalDatabase.edit();

        userLocalDatabaseEditor.putString("name", user.getName());
        userLocalDatabaseEditor.putString("email", user.getEmail());
        userLocalDatabaseEditor.putString("photo", user.getPhoto());


        userLocalDatabaseEditor.apply();
    }

    public void setUserLoggedIn(boolean loggedIn) {
        SharedPreferences.Editor userLocalDatabaseEditor = userLocalDatabase.edit();
        userLocalDatabaseEditor.putBoolean("loggedIn", loggedIn);
        userLocalDatabaseEditor.apply();
    }

    public boolean isLoggedIn() {
        return userLocalDatabase.getBoolean("loggedIn", false);
    }

    public void clearUserData() {
        SharedPreferences.Editor userLocalDatabaseEditor = userLocalDatabase.edit();
        userLocalDatabaseEditor.clear();
        userLocalDatabaseEditor.apply();
    }

    public User getLoggedInUser() {
        if (!isLoggedIn()) {
            return null;
        }

        String name = userLocalDatabase.getString("name", "");
        String email = userLocalDatabase.getString("email", "");
        String photo = userLocalDatabase.getString("photo", "");

        User user = new User(name, photo, email);
        return user;
    }
}
