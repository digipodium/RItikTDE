package com.example.t_d_e;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.t_d_e.MainPage;
import com.example.t_d_e.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class enterOTP extends AppCompatActivity {
    EditText inputnumber1, inputnumber2, inputnumber3, inputnumber4, inputnumber5, inputnumber6;
    String getotpbackend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_o_t_p);
       final Button verifybuttonclick = findViewById(R.id.btnVerify);
        inputnumber1 = findViewById(R.id.inputOTP1);
        inputnumber2 = findViewById(R.id.inputOTP2);
        inputnumber3 = findViewById(R.id.inputOTP3);
        inputnumber4 = findViewById(R.id.inputOTP4);
        inputnumber5 = findViewById(R.id.inputOTP5);
        inputnumber6 = findViewById(R.id.inputOTP6);
        TextView textView = findViewById(R.id.textmobileshownumber);
        textView.setText(String.format(
                "+91 %s", getIntent().getStringExtra("mobile")
        ));
        getotpbackend=getIntent().getStringExtra("backendotp");
       final ProgressBar progressBarverifyotp=findViewById(R.id.progressBar_verify_otp);


        verifybuttonclick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (!inputnumber1.getText().toString().trim().isEmpty() && !inputnumber2.getText().toString().trim().isEmpty() && !inputnumber3.getText().toString().trim().isEmpty() && !inputnumber4.getText().toString().trim().isEmpty() && !inputnumber5.getText().toString().trim().isEmpty() && !inputnumber6.getText().toString().trim().isEmpty()) {
                String entercodeotp =inputnumber1.getText().toString()+
                        inputnumber2.getText().toString()+
                        inputnumber3.getText().toString()+
                        inputnumber4.getText().toString()+
                        inputnumber5.getText().toString()+
                        inputnumber6.getText().toString();
                if (getotpbackend!=null){
                    progressBarverifyotp.setVisibility(View.INVISIBLE);
                    verifybuttonclick.setVisibility(View.INVISIBLE);

                    PhoneAuthCredential phoneAuthCredential=PhoneAuthProvider.getCredential(
                            getotpbackend,entercodeotp
                    );
                    FirebaseAuth.getInstance().signInWithCredential(phoneAuthCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            progressBarverifyotp.setVisibility(View.GONE);
                            verifybuttonclick.setVisibility(View.VISIBLE);

                            if(task.isSuccessful()){
                                Intent intent=new Intent(getApplicationContext(), MainPage.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            }else{
                                Toast.makeText(enterOTP.this, "Enter the correct otp", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }else{
                    Toast.makeText(enterOTP.this, "Please check internet connection", Toast.LENGTH_SHORT).show();
                }


                 //   Toast.makeText(enterOTP.this, "OTP verify", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(enterOTP.this, "Please Enter all number", Toast.LENGTH_SHORT).show();
                }
            }

        });
        numberotpmove();

        TextView resendlabel=findViewById(R.id.textresendOTP);
        resendlabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PhoneAuthProvider.getInstance().verifyPhoneNumber(
                        "+91"  +getIntent().toString(), 60, TimeUnit.SECONDS, enterOTP.this,
                        new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                            @Override
                            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {


                            }

                            @Override
                            public void onVerificationFailed(@NonNull FirebaseException e) {

                                Toast.makeText(enterOTP.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                            }

                            @Override
                            public void onCodeSent(@NonNull String newbackendotp, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {

                                getotpbackend=newbackendotp;
                                Toast.makeText(enterOTP.this, "OTP sended successfully", Toast.LENGTH_SHORT).show();

                            }
                        }
                );

            }
        });

    }

    private void numberotpmove() {
        inputnumber1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().trim().isEmpty()){
                    inputnumber2.requestFocus();
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        inputnumber2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().trim().isEmpty()){
                    inputnumber3.requestFocus();
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        inputnumber3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().trim().isEmpty()){
                    inputnumber4.requestFocus();
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        inputnumber4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().trim().isEmpty()){
                    inputnumber5.requestFocus();
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        inputnumber5.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().trim().isEmpty()){
                    inputnumber6.requestFocus();
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
}
