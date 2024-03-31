package com.example.projectx.ui.login;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.projectx.R;
import com.example.projectx.remote.CurrentDatabase;
import com.example.projectx.remote.FirebaseDatabaseHelper;
import com.example.projectx.remote.models.User;
import com.example.projectx.ui.home.MainActivity;
import com.example.projectx.utils.PreferenceManager;
import com.example.projectx.utils.Utils;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthMultiFactorException;
import com.google.firebase.auth.MultiFactorAssertion;
import com.google.firebase.auth.MultiFactorResolver;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.auth.PhoneMultiFactorGenerator;
import com.google.firebase.auth.PhoneMultiFactorInfo;

import java.util.Objects;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * The type Login activity.
 */
public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "LoginActivity";
    private boolean doubleBackToExitPressedOnce = false;

    private EditText emailEditText;
    private EditText passwordEditText;
    private EditText captchaEditText;
    private Button loginButton;

    private TextView forgotPasswordTextView;
    private TextView createAccountTextView;
    private ImageView captchaImageView;
    private ImageView refreshImageView;

    private String generatedCaptcha = "";
    private MultiFactorResolver multiFactorResolver;

    private String email, password;
    private ProgressDialog progressDialog;
    private String verificationId;
    PhoneAuthProvider.OnVerificationStateChangedCallbacks callback = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
            Log.d(TAG, "onVerificationCompleted: ");

            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            moveToNextActivity(email, password, intent);
        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            Log.d(TAG, "onVerificationFailed: ");


        }

        @Override
        public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            Log.d(TAG, "onCodeSent: ");
            verificationId = s;
            super.onCodeSent(s, forceResendingToken);
            progressDialog.cancel();
            openAlertDialogForMfaCodeVerification(LoginActivity.this);

        }
    };

    public static String generateCaptcha(int length) {
        if (length <= 0) {
            throw new IllegalArgumentException("Captcha length must be greater than zero.");
        }

        // Define the characters allowed in the CAPTCHA
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

        // Create a random object
        Random random = new Random();

        // Initialize a StringBuilder to build the CAPTCHA
        StringBuilder captcha = new StringBuilder(length);

        // Generate the CAPTCHA by randomly selecting characters from the allowed set
        for (int i = 0; i < length; i++) {
            int randomIndex = random.nextInt(characters.length());
            char randomChar = characters.charAt(randomIndex);
            captcha.append(randomChar);
        }
        Log.d(TAG, "generateCaptcha: " + captcha);
        System.out.println(captcha.toString());
        return captcha.toString();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        new FirebaseDatabaseHelper();

        initializeWidgets();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initializeWidgets() {
        emailEditText = findViewById(R.id.edt_email);
        passwordEditText = findViewById(R.id.edt_password);
        captchaEditText = findViewById(R.id.edt_captcha);
        captchaImageView = findViewById(R.id.imageView2);

        loginButton = findViewById(R.id.btn_login);
        loginButton.setOnClickListener(this); // setting click listener to the imageview
        loginButton.setOnTouchListener((view, motionEvent) -> {
            Animation click = AnimationUtils.loadAnimation(getApplicationContext(),
                    R.anim.click);
            loginButton.startAnimation(click);
            return false;
        });

        forgotPasswordTextView = findViewById(R.id.btn_fpass);
        createAccountTextView = findViewById(R.id.txt_signup);
        refreshImageView = findViewById(R.id.refreshImageView);

        forgotPasswordTextView.setOnClickListener(this);
        createAccountTextView.setOnClickListener(this);

        generateImage();
        refreshImageView.setOnClickListener(view -> generateImage());

//        captchaEditText.setText(generatedCaptcha);
    }

    private void generateImage() {
        generatedCaptcha = generateCaptcha(6);

        // Create a Bitmap with the desired dimensions
        int width = 400; // Width of the image
        int height = 200; // Height of the image
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        // Create a Canvas to draw on the Bitmap
        Canvas canvas = new Canvas(bitmap);

        // Create a Paint object to specify the text appearance
        Paint paint = new Paint();
        paint.setColor(Color.BLACK);

        Random random = new Random();

        // Calculate the available width for each character segment
        int segmentWidth = width / generatedCaptcha.length();

        // Calculate the text size based on the segment width and a scale factor (adjust as needed)
        float textSize = segmentWidth * 1.5f; // Increase the scale factor for a larger font size

        // Set the adjusted font size
        paint.setTextSize(textSize);

        // Draw each character with a random rotation
        for (int i = 0; i < generatedCaptcha.length(); i++) {
            // Generate a random rotation angle between -15 and 15 degrees
            float rotation = random.nextInt(30) - 15;

            // Measure the text bounds to adjust the drawing position
            Rect textBounds = new Rect();
            paint.getTextBounds(String.valueOf(generatedCaptcha.charAt(i)), 0, 1, textBounds);
            int textWidth = textBounds.width();

            // Calculate the X position to center the character within the segment
            float x = (i * segmentWidth) + (segmentWidth - textWidth) / 2;

            // Calculate the Y position to vertically center the character
            float y = height / 2 + textSize / 2;

            // Save the current canvas state
            canvas.save();

            // Translate the canvas to the position where you want to draw the character
            canvas.translate(x, y);

            // Rotate the canvas by the specified angle
            canvas.rotate(rotation);

            // Draw the character on the rotated canvas
            canvas.drawText(String.valueOf(generatedCaptcha.charAt(i)), 0, 0, paint);

            // Restore the canvas state to the original
            canvas.restore();
        }

        // Set the Bitmap in the ImageView to display it
        captchaImageView.setImageBitmap(bitmap);
    }

    @Override
    public void onClick(View view) {
        /* If next Button is clicked this block of code will execute */
        if (view == loginButton) {

            email = emailEditText.getText().toString().trim(); // getting string inside the edit text and removing whitespaces
            password = passwordEditText.getText().toString().trim(); // same as above for password
            String captcha = captchaEditText.getText().toString().trim();

            /* Checks if the fields are empty or not, and prompt the user accordingly */
            if (Utils.validateEmail(email)
                    && Utils.validatePassword(password)) {
                if (captcha.equals(generatedCaptcha)) {
                    loginUser(email, password);
                } else {
                    captchaEditText.setError("Wrong captcha");
                }
            } else {
                if (!Utils.validateEmail(email))
                    emailEditText.setError("Invalid Email");
                else if (!Utils.validatePassword(password))
                    passwordEditText.setError("Invalid password, password should be atleast 7 characters long");
            }
        }

        if (view == forgotPasswordTextView) {
            AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
            builder.setTitle("Send reset email");

            LayoutInflater inflater = (LayoutInflater) LoginActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            assert inflater != null;
            @SuppressLint("InflateParams") View alertDialogView = inflater.inflate(R.layout.input_email, null);
            builder.setView(alertDialogView);
            builder.setIcon(R.mipmap.ic_launcher);
            builder.setCancelable(false);

            final EditText inputEditText = alertDialogView.findViewById(R.id.inputEditText);

            builder.setView(alertDialogView);

            builder.setPositiveButton(android.R.string.ok, (dialog, which) -> {
                String dialogEmailAddress = inputEditText.getText().toString();
                dialog.cancel();
                if (Utils.validateEmail(dialogEmailAddress)) {
                    Utils.showToast(getApplicationContext(), "Password reset email sent to: " + dialogEmailAddress);
                    FirebaseAuth.getInstance().sendPasswordResetEmail(dialogEmailAddress);
                } else
                    Utils.showToast(getApplicationContext(), "Incorrect email");
            });
            builder.show();
        }

        if (view == createAccountTextView) {
            Intent intent = new Intent(this, SignUpActivity.class);
            intent.putExtra("TYPE", false);
            startActivity(intent);
        }
    }

    private void loginUser(final String email, final String password) {
        progressDialog = new ProgressDialog(LoginActivity.this);
        progressDialog.setTitle("Account login in progress...");
        progressDialog.show();


        FirebaseDatabaseHelper databaseHelper = new FirebaseDatabaseHelper();
        databaseHelper.getUserByEmail(email, new FirebaseDatabaseHelper.OnUserFetchListener() {
            @Override
            public void onUserFetched(User user) {

                Intent intent;
                if (user != null) {
                    intent = new Intent(LoginActivity.this, MainActivity.class);
                    CurrentDatabase.setCurrentPublicUser(user);
                    Log.d(TAG, "Setting current user as: " + CurrentDatabase.getCurrentPublicUser().toString());

                    FirebaseAuth mAuth = FirebaseAuth.getInstance();

                    mAuth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(LoginActivity.this, task -> {
                                if (task.isSuccessful()) {
                                    progressDialog.cancel();
                                    PreferenceManager.saveEmailAndPassword(LoginActivity.this, email, password);
                                    LoginActivity.this.moveToNextActivity(email, password, intent);
                                } else if (task.getException() instanceof FirebaseAuthMultiFactorException) {
                                    LoginActivity.this.sendMfaCodeToUser(task);
                                } else {
                                    progressDialog.cancel();
                                    Utils.showToast(LoginActivity.this.getApplicationContext(), Objects.requireNonNull(task.getException()).getMessage());
                                }
                            });
                } else {
                    Utils.showToast(getApplicationContext(), "No user found");
                    progressDialog.cancel();
                }
            }

            @Override
            public void onUserNotFound() {
                Utils.showToast(getApplicationContext(), "No user found");
                progressDialog.cancel();
            }

            @Override
            public void onUserFetchError(String errorMessage) {
                Utils.showToast(getApplicationContext(), "Error fetching user: " + errorMessage);
                progressDialog.cancel();
            }
        });
    }


    private void moveToNextActivity(String email, String password, Intent finalIntent) {
        Utils.showToast(getApplicationContext(), "Login Successful!");
        startActivity(finalIntent);
        finish();

        Utils.saveDataInSharedPrefs(getApplicationContext(), "email", email);
        Utils.saveDataInSharedPrefs(getApplicationContext(), "password", password);

        Utils.saveDataInSharedPrefs(getApplicationContext(), "is_login", "USER");
    }

    private void sendMfaCodeToUser(Task<AuthResult> task) {
        FirebaseApp.initializeApp(this);

        multiFactorResolver = ((FirebaseAuthMultiFactorException) task.getException()).getResolver();

        PhoneAuthProvider.verifyPhoneNumber(
                PhoneAuthOptions.newBuilder()
                        .setActivity(this)
                        .setMultiFactorSession(multiFactorResolver.getSession())
                        .setMultiFactorHint((PhoneMultiFactorInfo) multiFactorResolver.getHints().get(0))
                        .setCallbacks(callback)
                        .setTimeout(30L, TimeUnit.SECONDS)
                        .build());

    }

    private void openAlertDialogForMfaCodeVerification(Context context) {
        // Create an AlertDialog.Builder instance
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        // Set the title and message
        builder.setTitle("MFA code sent");
        builder.setMessage("You have been sent MFA code on your registered phone number, please enter the code");

        // Create an EditText view for user input
        final EditText input = new EditText(context);
        builder.setView(input);


        // Set positive button action
        builder.setPositiveButton("Done", (dialog, which) -> {
            String authCode = input.getText().toString();
            PhoneAuthCredential credential =
                    PhoneAuthProvider.getCredential(verificationId, authCode);

            // Initialize a MultiFactorAssertion object with the
            // PhoneAuthCredential.
            MultiFactorAssertion multiFactorAssertion =
                    PhoneMultiFactorGenerator.getAssertion(credential);

            multiFactorResolver
                    .resolveSignIn(multiFactorAssertion)
                    .addOnCompleteListener(
                            task -> {
                                if (task.isSuccessful()) {
                                    Log.d(TAG, "onCodeSent: success");
                                    moveToNextActivity(email, password, new Intent(this, MainActivity.class));
                                } else {
                                    Log.d(TAG, "onCodeSent: failed");
                                    Utils.showToast(this, "Cannot login: " + task.getException().getMessage());
                                }
                            });
            // Close the dialog
            dialog.dismiss();
        });

        // Set negative button action
        builder.setNegativeButton("Cancel", (dialog, which) -> {
            // Cancel the dialog
            dialog.cancel();
        });

        // Create and show the AlertDialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            finish();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Utils.showToast(this, "Click BACK twice to exit");

        new Handler().postDelayed(() -> doubleBackToExitPressedOnce = false, 2000);
    }

}
