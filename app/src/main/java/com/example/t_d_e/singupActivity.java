package com.example.t_d_e;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import timber.log.Timber;

public class singupActivity extends AppCompatActivity {

    public static final String TAG = singupActivity.class.getName();
    private static final String TAG1 = singupActivity.class.getName();

    EditText ed_firstname, ed_lastname, ed_username;
    Button btn_create_user;
    RadioButton male, female;
    String gender = "";


    FirebaseAuth fAuth;


    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_singup);
        male = (RadioButton) findViewById(R.id.btn_male);
        female = (RadioButton) findViewById(R.id.btn_female);

        ed_firstname = findViewById(R.id.ed_firstname);
        ed_username = findViewById(R.id.ed_username);
        ed_lastname = findViewById(R.id.ed_lastname);
        btn_create_user = findViewById(R.id.btn_create_user);


        fAuth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();


        progressBar = findViewById(R.id.progressBar);
        // if (fAuth.getCurrentUser() != null) {

        //   startActivity(new Intent(getApplicationContext(), Activity4.class));
        //  }
        btn_create_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String username = ed_username.getText().toString().trim();
                String firstname = ed_firstname.getText().toString().trim();
                String lastname = ed_lastname.getText().toString().trim();


                if (TextUtils.isEmpty(username)) {
                    ed_username.setError("username is required");
                    return;
                }
                if (TextUtils.isEmpty(firstname)) {
                    ed_firstname.setError("Firstname is required");
                    return;
                }
                if (TextUtils.isEmpty(lastname)) {
                    ed_lastname.setError("Lastname is required");
                    return;
                }
                if (male.isChecked()) {
                    gender = "Male";
                }
                if (female.isChecked()) {
                    gender = "Female";
                }


                progressBar.setVisibility(View.VISIBLE);
                btn_create_user.setVisibility(View.INVISIBLE);


                // Create a new user with a first and last name
                Map<String, Object> user = new HashMap<>();
                user.put("firstname", firstname);
                user.put("lastname", lastname);
                user.put("username", username);
                user.put("gender", gender);

                // Add a new document with a generated ID
                db.collection("users")
                        .add(user)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Timber.d("DocumentSnapshot added with : %s", documentReference.getId());
                                startActivity(new Intent(getApplicationContext(), Activity4.class));
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Timber.tag(TAG1).w(e, "Error adding document");
                            }
                        });


            }

        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (fAuth.getCurrentUser() != null) {
            startActivity(new Intent(this, MainPage.class));
            finish();
        }
    }
}





