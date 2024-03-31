package com.example.projectx.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferenceManager {
    private static final String PREF_NAME = "ProjectX";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_PASSWORD = "password";

    private static SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public static void saveEmailAndPassword(Context context, String email, String password) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_PASSWORD, password);
        editor.apply();
    }

    public static String getEmail(Context context) {
        return getSharedPreferences(context).getString(KEY_EMAIL, "");
    }

    public static String getPassword(Context context) {
        return getSharedPreferences(context).getString(KEY_PASSWORD, "");
    }

    public static void clearSharedPreferences(Context context) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.clear();
        editor.apply();
    }
}
