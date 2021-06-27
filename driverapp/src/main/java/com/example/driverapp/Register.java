package com.example.driverapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Register extends AppCompatActivity {

    public static final String TAG = Register.class.getName();
    private static final String TAG1 = Register.class.getName();

    Button proceed;
    EditText name,city,vehiclenum;
    RadioButton two,three,four,yes,no;
    String vehicle_Type="";
    String Will_you_own_drive_vehicle="";
    FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        name=findViewById(R.id.name);
        vehiclenum=findViewById(R.id.Vehiclenumber);
        two=findViewById(R.id.TwoWheeler);
        three=findViewById(R.id.ThreeWheeler);
        four=findViewById(R.id.FourWheeler);
        yes=findViewById(R.id.Yes);
        no=findViewById(R.id.No);

        city=findViewById(R.id.city);

        proceed=findViewById(R.id.proceed);
        fAuth=FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String personname = name.getText().toString().trim();
                String personcity= city.getText().toString().trim();
               String vehicle_number=vehiclenum.getText().toString().trim();

                if (TextUtils.isEmpty(personname)) {
                    name.setError("Name is required");
                    return;
                }
                if (TextUtils.isEmpty(personcity)){
                    city.setError("city is required");
                    return;
                }

                if ((TextUtils.isEmpty(vehicle_number))){
                    vehiclenum.setError("vehicle no. is required");
                }
                if (two.isChecked()){
                    vehicle_Type="Two_Wheeler";
                }
                if (three.isChecked()){
                    vehicle_Type="Three_Wheeler";
                }if (four.isChecked()){
                    vehicle_Type="Four_Wheeler";
                }if (yes.isChecked()){
                    Will_you_own_drive_vehicle="Yes";
                }if (no.isChecked()){
                  Will_you_own_drive_vehicle="No";
                }


                Map<String, Object> drivers = new HashMap<>();
                drivers.put("Name", personname);
                drivers.put("City", personcity);
                drivers.put("Vehicle_number",vehicle_number);
                drivers.put("Vehicle Type",vehicle_Type);
                drivers.put("Will You Own Drive Vehicle",Will_you_own_drive_vehicle);



                db.collection("drivers")
                        .add(drivers)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Log.d(TAG, "DocumentSnapshot added with : " + documentReference.getId());
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG1, "Error adding document", e);
                            }
                        });

                startActivity(new Intent(getApplicationContext(), DriverId.class));

            }
        });
    }

    }
