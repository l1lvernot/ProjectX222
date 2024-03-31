package com.example.projectx.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.example.projectx.R;
import com.example.projectx.ui.home.MainActivity;
import com.example.projectx.ui.login.GetStartedActivity;
import com.example.projectx.ui.login.LoginActivity;
import com.example.projectx.utils.Utils;
import com.google.firebase.auth.FirebaseAuth;


/**
 * The type Splash activity.
 */
@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {

    private static final String TAG = "SplashActivity";

    private boolean opened = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        splashScreenTime();

    }

    private void splashScreenTime() {
        Log.e(TAG, "In Splash Screen");

        new Handler().postDelayed(() -> {
            String isLogin = Utils.getDataFromSharedPrefs(getApplicationContext(), "is_login");

            if (TextUtils.isEmpty(isLogin)) {
                // User is not logged in, navigate to GetStartedActivity or LoginActivity
                startActivity(new Intent(getApplicationContext(), GetStartedActivity.class));
            } else {
                // User is logged in, retrieve email and password from SharedPreferences
                String email = Utils.getDataFromSharedPrefs(getApplicationContext(), "email");
                String password = Utils.getDataFromSharedPrefs(getApplicationContext(), "password");


                FirebaseAuth mAuth = FirebaseAuth.getInstance();


                if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {
                    // Perform Firebase authentication with saved email and password
                    mAuth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(this, task -> {
                                if (task.isSuccessful()) {
                                    // Login successful, move to MainActivity
                                    startActivity(new Intent(this, MainActivity.class));
                                } else {
                                    // Login failed, navigate to LoginActivity
                                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                                }
                                finish();
                            });
                } else {
                    // Email or password not found in SharedPreferences, navigate to LoginActivity
                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                    finish();
                }
            }
        }, 3000); // 3000 milliseconds = 3 seconds delay for splash screen
    }

}

