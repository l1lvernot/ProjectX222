package com.example.projectx.ui.login;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.example.projectx.R;
import com.example.projectx.remote.FirebaseDatabaseHelper;
import com.example.projectx.remote.models.User;
import com.example.projectx.utils.Utils;

/**
 * The type Sign up activity.
 */
public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {
    ProgressDialog progressDialog;
    private EditText edtEmail, edtPhone, edtFirstName, edtLastName, edtPassword, edtConfirmPassword;
    private Button btnSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        progressDialog = new ProgressDialog(SignUpActivity.this);

        new FirebaseDatabaseHelper();
        init();

    }

    private void init() {
        edtEmail = findViewById(R.id.edt_email);

        edtPhone = findViewById(R.id.edt_phone_number);

        edtFirstName = findViewById(R.id.edt_first_name);

        edtLastName = findViewById(R.id.edt_last_name);

        edtPassword = findViewById(R.id.edt_password);

        edtConfirmPassword = findViewById(R.id.edt_confirm_password);

        btnSignUp = findViewById(R.id.btn_signUp);
        btnSignUp.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view == btnSignUp) {

            String email = edtEmail.getText().toString().trim();
            String phone = edtPhone.getText().toString().trim();
            String password = edtPassword.getText().toString().trim();
            String confirmPassword = edtConfirmPassword.getText().toString().trim();
            String firstName = edtFirstName.getText().toString().trim();
            String lastName = edtLastName.getText().toString().trim();

            if (email.isEmpty()) {
                edtEmail.setError("Email is empty");
                return;
            }

            if (!Utils.validateEmail(email)) {
                edtEmail.setError("Invalid Email");
                return;
            }

            if (phone.isEmpty()) {
                edtPhone.setError("Invalid Phone number");
                return;
            }

            if (firstName.isEmpty()) {
                edtFirstName.setError("FirstName is empty");
                return;
            }

            if (lastName.isEmpty()) {
                edtLastName.setError("LastName is empty");
                return;
            }

            if (password.isEmpty()) {
                edtPassword.setError("Password is empty");
                return;
            }

            if (confirmPassword.isEmpty()) {
                edtConfirmPassword.setError("confirm password is empty");
                return;
            }

            if (password.length() < 6 || confirmPassword.length() < 6) {
                edtPassword.setError("Password length should be more than 6 characters");
                return;
            }

            if (!password.trim().equals(confirmPassword.trim())) {
                Utils.showToast(this, "Passwords doesn't match");
                edtPassword.setText("");
                edtConfirmPassword.setText("");
                return;
            }

            progressDialog.setTitle("Account creation in progress...");
            progressDialog.show();

            User user = new User(Utils.PUBLIC_USER_TYPE, "", email, password, firstName, lastName, phone, "", 5D);

            new FirebaseDatabaseHelper().createUser(user, null);

            new Handler().postDelayed(() -> {
                progressDialog.dismiss();
                finish();
            }, 5000);

        }
    }
}