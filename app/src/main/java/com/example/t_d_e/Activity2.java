package com.example.t_d_e;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.OnApplyWindowInsetsListener;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.internal.ViewUtils;

public class Activity2 extends AppCompatActivity {
    private Button button;
    private Button btn;
    private LocationRequest locationRequest;
    public static final int REQUEST_CHECK_SETTING=1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_2);
        button = (Button) findViewById(R.id.button2);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
           public void onClick(View v) {
                openNext();
            }
        });
        btn=findViewById(R.id.location_btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                locationRequest = LocationRequest.create();
                locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
               locationRequest .setInterval(5000);
               locationRequest.setFastestInterval(2000);
                LocationSettingsRequest.Builder builder=new LocationSettingsRequest.Builder()
                        .addLocationRequest(locationRequest);
                builder.setAlwaysShow(true);
                Task<LocationSettingsResponse> result = LocationServices.getSettingsClient(getApplicationContext())
                        .checkLocationSettings(builder.build());
                result.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
                    @Override
                    public void onComplete(@NonNull Task<LocationSettingsResponse> task) {
                        try {
                            LocationSettingsResponse response = task.getResult(ApiException.class);
                            Toast.makeText(Activity2.this, "GPS IS ON", Toast.LENGTH_SHORT).show();
                        } catch (ApiException e) {
                            e.printStackTrace();
                            switch (e.getStatusCode()){
                                case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:

                                    try {
                                        ResolvableApiException resolvableApiException= (ResolvableApiException)e;
                                        resolvableApiException.startResolutionForResult(Activity2.this,REQUEST_CHECK_SETTING);
                                    } catch (IntentSender.SendIntentException sendIntentException) {
                                        sendIntentException.printStackTrace();
                                    }

                                    break;
                                case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                    break;
                            }

                        }
                    }
                });
            }
        });


    }

    private void openNext() {
        Intent intent = new Intent(this, Activity3.class);
       startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CHECK_SETTING){
            switch (resultCode){



                case Activity2.RESULT_OK:


                    Toast.makeText(this, "GPS IS TURNED ON", Toast.LENGTH_SHORT).show();

                    break;
                case Activity2.RESULT_CANCELED:

                    Toast.makeText(this, "GPS required to be turned on", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }
}
