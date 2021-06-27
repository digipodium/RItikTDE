package com.example.t_d_e;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import timber.log.Timber;

public class Deliver_Now extends AppCompatActivity {
    public static final String TAG = Deliver_Now.class.getName();
    private static final String TAG1 = Deliver_Now.class.getName();
    EditText receiver_name, receiver_mobile, pickup_inst, delivery_inst;
    RadioButton size1, size2, size3, weight1, weight2, weight3, weight4;
    Button deliver_Now;
    ProgressBar progressBar;
    String Package_Size = "";
    String Package_Weight = "";
    private double price;
    private String veh;
    private FirebaseAuth fAuth;
    private double mylatitude;
    private double mylongitude;
    private double destlatitude;
    private double destlongitude;
    private String destaddr;
    private String Pickup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deliver__now);
        receiver_name = findViewById(R.id.Receiver_Name);
        receiver_mobile = findViewById(R.id.Receiver_mobile);
        pickup_inst = findViewById(R.id.Pickup_inst);
        delivery_inst = findViewById(R.id.Delivery_inst);
        size1 = findViewById(R.id.Btn_size_1);
        size2 = findViewById(R.id.btn_size_2);
        size3 = findViewById(R.id.btn_size_3);
        weight1 = findViewById(R.id.Btn_weight_1);
        weight2 = findViewById(R.id.btn_weight_2);
        weight3 = findViewById(R.id.btn_weight_3);
        weight4 = findViewById(R.id.btn_weight_4);
        deliver_Now = findViewById(R.id.btn_delivery);
        price = getIntent().getExtras().getDouble("price");
        veh = getIntent().getExtras().getString("vehicle");
        mylatitude =  getIntent().getDoubleExtra("mylatitude", 0.0);
        mylongitude = getIntent().getDoubleExtra("mylongitude", 0.0);
        destlatitude = getIntent().getDoubleExtra("destlatitude", 0.0);
        destlongitude = getIntent().getDoubleExtra("destlongitude", 0.0);
        destaddr = getIntent().getStringExtra("destaddress");
        Pickup = getIntent().getStringExtra("myaddress");
        fAuth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        deliver_Now.setOnClickListener(v -> {
            String Receiver_name = receiver_name.getText().toString().trim();
            String Receiver_mobile = receiver_mobile.getText().toString().trim();
            String Pickup_instruction = pickup_inst.getText().toString().trim();
            String Delivery_instruction = delivery_inst.getText().toString().trim();

            if (TextUtils.isEmpty(Receiver_name)) {
                receiver_name.setError("Receivername is required");
                return;
            }
            if (TextUtils.isEmpty(Receiver_mobile)) {
                receiver_mobile.setError("Receiver mobile is required");
                return;
            }


            if (size1.isChecked()) {
                Package_Size = "Under 1sq Feet";

            }
            if (size2.isChecked()) {
                Package_Size = "1sq-2sq Feet";
                price += .25;
            }
            if (size3.isChecked()) {
                Package_Size = "More than 2sq Feet";
                price += .55;
            }
            if (weight1.isChecked()) {
                Package_Weight = "Under 500gm";
                price += .25;
            }
            if (weight2.isChecked()) {
                Package_Weight = "500gm-2kg";
                price += .35;
            }
            if (weight3.isChecked()) {
                Package_Weight = "2kg-5kg";
                price += .50;
            }
            if (weight4.isChecked()) {
                Package_Weight = "More than 5kg";
                price += .75;
            }



            // Create a new user with a first and last name
            Map<String, Object> user = new HashMap<>();
            user.put("Receiver Name", Receiver_name);
            user.put("Receiver Mobile Number", Receiver_mobile);
            user.put("Sender Number", fAuth.getCurrentUser().getPhoneNumber());
            user.put("senderid", fAuth.getCurrentUser().getUid());
            user.put("Pick up Instruction", Pickup_instruction);
            user.put("Delivery Instruction", Delivery_instruction);
            user.put("Package Size", Package_Size);
            user.put("Package Weight", Package_Weight);
            user.put("Delivery Price", price);
            user.put("Delivery Vehicle", veh);
            user.put("dp", null);
            user.put("statue", "active");
            user.put("mylatitude", mylatitude);
            user.put("mylongitude", mylongitude);
            user.put("destlatitude", destlatitude);
            user.put("destlongitude", destlongitude);
            user.put("destaddr", destaddr);
            user.put("myaddr", Pickup);

// Add a new document with a generated ID
            db.collection("delivery")
                    .add(user)
                    .addOnSuccessListener(documentReference -> {
                        startActivity(new Intent(getApplicationContext(), driverMov.class));
                    })
                    .addOnFailureListener(e -> Timber.tag(TAG1).w(e, "Error adding document"));
        });

    }
}









