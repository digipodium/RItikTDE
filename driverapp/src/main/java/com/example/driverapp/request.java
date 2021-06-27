package com.example.driverapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;

public class request extends AppCompatActivity {

    private FirebaseAuth instance;
    private FirebaseFirestore db;
    private String driverName;
    private String phone;
    private String did;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request);
        String jobid = getIntent().getStringExtra("id");
        instance = FirebaseAuth.getInstance();
        FirebaseUser currentUser = instance.getCurrentUser();
        String phoneNumber = currentUser.getPhoneNumber();
        String uid = currentUser.getUid();

        TextView tvPickup = findViewById(R.id.pickUpLocation);
        TextView tvDest = findViewById(R.id.destLocation);

        db = FirebaseFirestore.getInstance();
        db.collection("drivers").document(uid).get().addOnSuccessListener(documentSnapshot -> {
            driverName = documentSnapshot.getString("Name");
            phone = documentSnapshot.getString("dphonenumber");
            did = documentSnapshot.getString("did");

        }).addOnFailureListener(e -> {
            Toast.makeText(this, "data not found", Toast.LENGTH_SHORT).show();
        });
        db.collection("delivery").document(jobid).get().addOnSuccessListener(documentSnapshot -> {
            String receiver_name = documentSnapshot.getString("Receiver Name");
            String receiver_mobile_number = documentSnapshot.getString("Receiver Mobile Number");
            String sender_number = documentSnapshot.getString("Sender Number");
            String senderid = documentSnapshot.getString("senderid");
            String pick_up_instruction = documentSnapshot.getString("Pick up Instruction");
            String delivery_instruction = documentSnapshot.getString("Delivery Instruction");
            String package_size = documentSnapshot.getString("Package Size");
            String package_weight = documentSnapshot.getString("Package Weight");
            Double delivery_price = documentSnapshot.getDouble("Delivery Price");
            String delivery_vehicle = documentSnapshot.getString("Delivery Vehicle");
            String dp = documentSnapshot.getString("dp");
            String statue = documentSnapshot.getString("statue");
            Double mylatitude = documentSnapshot.getDouble("mylatitude");
            Double mylongitude = documentSnapshot.getDouble("mylongitude");
            Double destlatitude = documentSnapshot.getDouble("destlatitude");
            Double destlongitude = documentSnapshot.getDouble("destlongitude");
            String destaddr = documentSnapshot.getString("destaddr");
            String myaddr = documentSnapshot.getString("myaddr");
            tvPickup.setText(myaddr);
            tvDest.setText(destaddr);
        });
        findViewById(R.id.accept).setOnClickListener(view -> {
            DocumentReference delivery = db.collection("delivery").document(jobid);
            WriteBatch batch = db.batch();
            batch.update(delivery, "dp", driverName);
            batch.update(delivery, "duid", uid);
            batch.update(delivery, "dphonenumber", phone);
            batch.update(delivery, "statue", "taken");
            batch.commit().addOnSuccessListener(unused -> {
                Intent i = new Intent(this, completeTask.class);
                i.putExtra("jobid", jobid);
                startActivity(i);
            });
        });
        findViewById(R.id.decline).setOnClickListener(view -> {

        });

    }
}