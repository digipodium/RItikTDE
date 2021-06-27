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
        btn_create_user.setOnClickListener(v -> {
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

            db.collection("users")
                    .document(fAuth.getCurrentUser().getUid())
                    .set(user)
                    .addOnSuccessListener(unused -> startActivity(new Intent(getApplicationContext(), Activity4.class)));
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





