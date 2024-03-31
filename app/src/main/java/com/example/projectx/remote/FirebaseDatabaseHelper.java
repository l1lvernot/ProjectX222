package com.example.projectx.remote;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.projectx.remote.models.User;
import com.example.projectx.utils.Utils;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Objects;

public class FirebaseDatabaseHelper {

    private static final String TAG = "FirebaseDatabaseHelper";
    private static final String USER_IMAGES_FOLDER = "user_images/";

    private FirebaseAuth firebaseAuth;
    private DatabaseReference publicUserEndPoint;

    public FirebaseDatabaseHelper() {
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        publicUserEndPoint = firebaseDatabase.getReference("public_user_table");
    }

    public static Task<Uri> uploadImage(String userId, Uri imageUri) {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        StorageReference userImageRef = storageReference.child(USER_IMAGES_FOLDER + userId + ".jpg");

        // Upload file to Firebase Storage
        UploadTask uploadTask = userImageRef.putFile(imageUri);

        // Get download URL
        return uploadTask.continueWithTask(task -> {
            if (!task.isSuccessful()) {
                throw task.getException();
            }
            // Continue with the task to get the download URL
            return userImageRef.getDownloadUrl();
        });
    }

    public void createUser(final User user, Uri uri) {
        firebaseAuth.createUserWithEmailAndPassword(user.getEmailAddress(), user.getPassword()).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.e(TAG, "Auth Created");
                FirebaseUser firebaseUser = task.getResult().getUser();
                if (firebaseUser != null) {
                    user.setUserId(firebaseUser.getUid());
                    publicUserEndPoint.child(user.getUserId()).setValue(user).addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful()) {
                            if (uri != null) {
                                Utils.uploadImage(user.getUserId(), uri);
                                Log.e(TAG, "Database entry created");
                            }
                        } else {
                            Log.e(TAG, Objects.requireNonNull(task1.getException()).toString());
                            firebaseUser.delete();
                        }
                    });
                }
            } else {
                Log.e(TAG, Objects.requireNonNull(task.getException()).toString());
            }
        });
    }

    public void getUserByEmail(String email, final OnUserFetchListener listener) {
        publicUserEndPoint.orderByChild("emailAddress").equalTo(email)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                User user = snapshot.getValue(User.class);
                                if (user != null) {
                                    listener.onUserFetched(user);
                                    return;
                                }
                            }
                        }
                        listener.onUserNotFound();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        listener.onUserFetchError(databaseError.getMessage());
                    }
                });
    }

    public interface OnUserFetchListener {
        void onUserFetched(User user);

        void onUserNotFound();

        void onUserFetchError(String errorMessage);
    }

}
