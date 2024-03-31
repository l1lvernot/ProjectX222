package com.example.projectx.ui.login;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.projectx.R;


/**
 * The type Get started activity.
 */
public class GetStartedActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_started);

        findViewById(R.id.btn_started).setOnClickListener(view
                -> startActivity(new Intent(getApplicationContext(), LoginActivity.class)));
    }
}