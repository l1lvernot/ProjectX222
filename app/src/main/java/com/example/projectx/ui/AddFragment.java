package com.example.projectx.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.projectx.R;
import com.example.projectx.remote.models.Party;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddFragment extends Fragment {

    private EditText partyTitleEditText, partyDetailsEditText, longitudeEditText, latitudeEditText, addressEditText, timeEditText;
    private Button addPartyButton;

    private FirebaseAuth mAuth;

    private DatabaseReference partyRef;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_add, container, false);

        partyTitleEditText = rootView.findViewById(R.id.edt_party_title);
        partyDetailsEditText = rootView.findViewById(R.id.edt_party_details);
        longitudeEditText = rootView.findViewById(R.id.edt_longitude);
        latitudeEditText = rootView.findViewById(R.id.edt_latitude);
        addressEditText = rootView.findViewById(R.id.edt_address);
        timeEditText = rootView.findViewById(R.id.edt_time);

        addPartyButton = rootView.findViewById(R.id.btn_add_party);
        addPartyButton.setOnClickListener(v -> addParty());

        partyRef = FirebaseDatabase.getInstance().getReference().child("parties");
        mAuth = FirebaseAuth.getInstance();

        return rootView;
    }

    private void addParty() {
        String partyTitle = partyTitleEditText.getText().toString().trim();
        String partyDetails = partyDetailsEditText.getText().toString().trim();
        Double longitude = Double.parseDouble(longitudeEditText.getText().toString().trim());
        Double latitude = Double.parseDouble(latitudeEditText.getText().toString().trim());
        String address = addressEditText.getText().toString().trim();
        String time = timeEditText.getText().toString().trim();
        String uid = mAuth.getCurrentUser().getUid();

        // Validate input fields
        if (partyTitle.isEmpty() || partyDetails.isEmpty() || address.isEmpty() || time.isEmpty()) {
            // Show error or prompt for valid input
            return;
        }

        // Assuming you have a Party class to represent party details
        Party party = new Party(partyTitle, partyDetails, longitude, latitude, address, time);
        partyRef.setValue(party);

        // Generate a unique key for the party
        String partyId = partyRef.push().getKey();
        if (partyId != null) {
            // Save party to Firebase Realtime Database
            partyRef.child(partyId).setValue(party).addOnSuccessListener(aVoid -> {
                // Party added successfully
                Toast.makeText(getContext(), "Party added successfully", Toast.LENGTH_SHORT).show();
                clearFields();
                // Show success message or navigate to another fragment/activity
            }).addOnFailureListener(e -> {
                // Handle failure
                Toast.makeText(getContext(), "unknown error occurred while adding", Toast.LENGTH_SHORT).show();
            });
        }
    }

    private void clearFields() {
        partyTitleEditText.setText("");
        partyDetailsEditText.setText("");
        longitudeEditText.setText("");
        latitudeEditText.setText("");
        addressEditText.setText("");
        timeEditText.setText("");
    }
}
