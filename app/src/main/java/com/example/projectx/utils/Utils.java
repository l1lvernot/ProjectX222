package com.example.projectx.utils;

import android.content.Context;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The type Helper class.
 */
public class Utils {


    /**
     * The constant INSERT.
     */
    public static final int INSERT = 22;
    /**
     * The constant UPDATE.
     */
    public static final int UPDATE = 33;
    /**
     * The constant DELETE.
     */
    public static final int DELETE = 44;
    /**
     * The constant DELETE_ALL.
     */
    public static final int DELETE_ALL = 55;

    /**
     * The constant PUBLIC_USER_TYPE.
     */
    public static final String PUBLIC_USER_TYPE = "PUBLIC_USER";

    /**
     * The constant ADMIN_TYPE.
     */
    public static final String ADMIN_TYPE = "ADMIN";

    private static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    private static final Pattern VALID_PASSWORD_REGEX =
            Pattern.compile("^(?=.*[0-9])(?=.*[a-z]).{6,}$", Pattern.CASE_INSENSITIVE);

    /**
     * Validate email boolean.
     *
     * @param emailStr the email str
     * @return the boolean
     */
    public static boolean validateEmail(CharSequence emailStr) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(emailStr);
        return matcher.find();
    }

    /**
     * Validate password boolean.
     *
     * @param passwordStr the password str
     * @return the boolean
     */
    public static boolean validatePassword(CharSequence passwordStr) {
        Matcher matcher = VALID_PASSWORD_REGEX.matcher(passwordStr);
        return matcher.find();
    }

    /**
     * Generate uuid string.
     *
     * @return the string
     */
    public static String generateUUID() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString();
    }

    /**
     * Load image.
     *
     * @param context   the context
     * @param userId    the user id
     * @param imageView the image view
     */
    public static void loadImage(Context context, String userId, ImageView imageView) {
        final StorageReference mStorageRef = FirebaseStorage.getInstance().getReference()
                .child("user_profile_pictures/" + userId + ".png");

        mStorageRef.getDownloadUrl().addOnSuccessListener(uri -> {
            Glide.with(context)
                    .load(uri.toString())
                    .into(imageView);
        });
    }

    /**
     * Upload image.
     *
     * @param id       the id
     * @param imageUri the image uri
     */
    public static void uploadImage(String id, Uri imageUri) {
        final StorageReference mStorageRef = FirebaseStorage.getInstance().getReference()
                .child("user_profile_pictures/" + id + ".png");

        mStorageRef.putFile(imageUri);
    }

    /**
     * Save data in shared prefs.
     *
     * @param context the context
     * @param key     the key
     * @param value   the value
     */
    public static void saveDataInSharedPrefs(Context context, String key, String value) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString(key, value).apply();
    }

    /**
     * Gets data from shared prefs.
     *
     * @param context the context
     * @param key     the key
     * @return the data from shared prefs
     */
    public static String getDataFromSharedPrefs(Context context, String key) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(key, "");
    }

    /**
     * Clear shared prefs.
     *
     * @param context the context
     */
    public static void clearSharedPrefs(Context context) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString("email", "").apply();
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString("password", "").apply();
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString("type", "").apply();
    }

    /**
     * Success.
     *
     * @param context the context
     * @param message the message
     */
    public static void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    /**
     * Round double.
     *
     * @param bmi the bmi
     * @return the double
     */
    public static double round(double bmi) {
        return Math.round(bmi * 100.0) / 100.0;
    }
}
