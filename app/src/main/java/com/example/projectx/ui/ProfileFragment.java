package com.example.projectx.ui;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.projectx.R;
import com.example.projectx.remote.FirebaseDatabaseHelper;
import com.example.projectx.remote.models.User;
import com.example.projectx.utils.Utils;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ProfileFragment extends Fragment {
    public static final int REQUEST_IMAGE_CAPTURE = 1;
    public static final int REQUEST_IMAGE_PICK = 2;
    private static final int CAMERA_PERMISSION_REQUEST = 100;
    private static final String TAG = "ProfileFragment";
    private ImageView profileImageView;
    private EditText firstNameEditText;
    private EditText lastNameEditText;
    private EditText emailEditText;
    private EditText phoneNumberEditText;
    private RatingBar ratingBar;
    private Button updateProfile;
    private FirebaseDatabaseHelper firebaseDatabaseHelper;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseDatabaseHelper = new FirebaseDatabaseHelper();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);

        profileImageView = rootView.findViewById(R.id.profileImageView);
        firstNameEditText = rootView.findViewById(R.id.edt_first_name);
        lastNameEditText = rootView.findViewById(R.id.edt_last_name);
        emailEditText = rootView.findViewById(R.id.edt_email);
        phoneNumberEditText = rootView.findViewById(R.id.edt_phone_number);
        ratingBar = rootView.findViewById(R.id.ratingBar);
        updateProfile = rootView.findViewById(R.id.editProfileButton);

        updateProfile.setOnClickListener(v -> {
            // Get the text from EditText fields
            String firstName = firstNameEditText.getText().toString().trim();
            String lastName = lastNameEditText.getText().toString().trim();
            String phoneNumber = phoneNumberEditText.getText().toString().trim();

            // Update the user details in Firebase
            updateUserInfo(firstName, lastName, phoneNumber);
            Toast.makeText(getContext(), "User details updated", Toast.LENGTH_SHORT).show();
        });

        // Set click listener for edit photo button
        profileImageView.setOnClickListener(v -> ImagePicker.with(this)
                .crop()                    //Crop image(Optional), Check Customization for more option
                .compress(1024)            //Final image size will be less than 1 MB(Optional)
                .maxResultSize(1080, 1080)    //Final image resolution will be less than 1080 x 1080(Optional)
                .start());

        // Load user details from Firebase
        loadUserDetails();

        return rootView;
    }

    private void updateUserInfo(String firstName, String lastName, String phoneNumber) {
        // Get the current user's email from SharedPreferences or wherever it's stored
        String userEmail = Utils.getDataFromSharedPrefs(getContext(), "email");

        // Fetch the user details from Firebase and update them
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference publicUserEndPoint = firebaseDatabase.getReference("public_user_table");
        publicUserEndPoint.orderByChild("emailAddress").equalTo(userEmail).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        User user = snapshot.getValue(User.class);
                        if (user != null) {
                            // Update the user object with new data
                            user.setFirstName(firstName);
                            user.setLastName(lastName);
                            user.setPhoneNumber(phoneNumber);

                            // Update the user details in Firebase
                            publicUserEndPoint.child(user.getUserId()).setValue(user)
                                    .addOnCompleteListener(task -> {
                                        if (task.isSuccessful()) {
                                            // User details updated successfully
                                            Log.i(TAG, "updateUserInfo: User details updated successfully");
                                        } else {
                                            // Failed to update user details
                                            Log.e(TAG, "updateUserInfo: Failed to update user details");
                                        }
                                    });
                            return;
                        }
                    }
                }
                // Handle case when user is not found
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle database error
            }
        });
    }

    private void loadUserDetails() {
        // Get the current user's email from SharedPreferences or wherever it's stored
        String userEmail = Utils.getDataFromSharedPrefs(getContext(), "email");

        // Fetch the user details from Firebase
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference publicUserEndPoint = firebaseDatabase.getReference("public_user_table");
        publicUserEndPoint.orderByChild("emailAddress").equalTo(userEmail).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        User user = snapshot.getValue(User.class);
                        if (user != null) {
                            // Set the user details to the views
                            firstNameEditText.setText(user.getFirstName());
                            lastNameEditText.setText(user.getLastName());
                            emailEditText.setText(user.getEmailAddress());
                            phoneNumberEditText.setText(user.getPhoneNumber());
                            // Assume you have a method to calculate the rating based on user data
                            double userRating = calculateUserRating(user);
                            ratingBar.setRating(4.5f);
                            // Load the user profile image using a library like Glide or Picasso
                            Glide.with(requireContext()).load(user.getProfileImageUrl()).into(profileImageView);
                            return;
                        }
                    }
                }
                // Handle case when user is not found
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle database error
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            // Image Uri will not be null for RESULT_OK
            Uri uri = data.getData();

            try {
                uploadImageToFirebase(getBitmapFromUri(uri));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            // Use Uri object instead of File to avoid storage permissions
            profileImageView.setImageURI(uri);
        } else if (resultCode == ImagePicker.RESULT_ERROR) {
        } else {
        }
    }

    private Bitmap getBitmapFromUri(Uri uri) throws IOException {
        ContentResolver contentResolver = getContext().getContentResolver();
        Bitmap bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri);
        // Optionally, you can scale the bitmap to reduce size before uploading to Firebase
        // bitmap = scaleBitmap(bitmap, maxWidth, maxHeight);
        return bitmap;
    }

    private void uploadImageToFirebase(Bitmap imageBitmap) {
        // Convert Bitmap to byte array
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageData = baos.toByteArray();

        // Get current user ID
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Define storage reference for the image
        StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("user_images").child(userId + ".jpg");

        // Upload image to Firebase Storage
        UploadTask uploadTask = storageRef.putBytes(imageData);
        uploadTask.addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // Image uploaded successfully, get download URL
                storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    String imageUrl = uri.toString();
                    // Save imageUrl to user's profile in Firebase Realtime Database
                    Log.i(TAG, "uploadImageToFirebase: updated" + imageUrl);
                });
            } else {
                // Handle upload failure
                Log.e(TAG, "Upload failed: " + task.getException().getMessage());
            }
        });
    }

    // Example method to calculate user rating (replace with your own logic)
    private double calculateUserRating(User user) {
        // Calculate user rating based on user data (e.g., reviews, ratings, etc.)
        return 4.5; // Example rating
    }

    private void requestCameraPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST);
        } else {
            openCamera();
        }
    }

    private void openCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(requireActivity().getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                // Permission denied, handle accordingly
            }
        }
    }
}
