package com.example.projectx;

import android.app.Application;
import android.content.Context;

import com.example.projectx.remote.FirebaseDatabaseHelper;
import com.google.firebase.FirebaseApp;


/**
 * The type Single entry point.
 */
public class SingleEntryPoint extends Application {

    private static final String TAG = "ApplicationContext";

    private static Context context;

    /**
     * Gets app context.
     *
     * @return the app context
     */
    public static Context getAppContext() {
        return SingleEntryPoint.context;
    }

    public void onCreate() {
        super.onCreate();
        SingleEntryPoint.context = getApplicationContext();
        FirebaseApp.initializeApp(getApplicationContext());
        new FirebaseDatabaseHelper();
    }


    @Override
    public void onTerminate() {
        super.onTerminate();
    }

}