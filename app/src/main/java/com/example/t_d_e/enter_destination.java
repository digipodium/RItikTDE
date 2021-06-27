package com.example.t_d_e;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.ResultReceiver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.helper.widget.Layer;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.Task;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.api.geocoding.v5.models.CarmenFeature;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.plugins.places.autocomplete.PlaceAutocomplete;
import com.mapbox.mapboxsdk.plugins.places.autocomplete.model.PlaceOptions;

import java.util.List;

public class enter_destination extends AppCompatActivity implements PermissionsListener {
    private static final int REQUEST_CHECK_SETTINGS = 23;
    private static final int REQUEST_CODE_AUTOCOMPLETE = 21;
    private static final String DROPPED_MARKER_LAYER_ID = "DROPPED_MARKER_LAYER_ID";
    EditText Pickup;
    TextView destination;
    Button back, done;
    private PermissionsManager permissionsManager;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationRequest mlocationRequest;
    private MapboxMap mapboxMap;
    private MapView mapView;
    private ImageView hoveringMarker;
    private Layer droppedMarkerLayer;
    private LocationSettingsRequest.Builder settingBuilder;
    private LocationCallback locationCallback;
    private AddressReceiver receiver;
    private Location currentLocation;
    private Point destinationcoordinate;
    private String destaddr;


    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_enter_destination);
        back = findViewById(R.id.btn_back);
        back.setOnClickListener(v -> {
            finish();
        });
        done = findViewById(R.id.btn_done);
        done.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), Check_to_proceed.class);
            intent.putExtra("mylatitude", currentLocation.getLatitude());
            intent.putExtra("mylongitude", currentLocation.getLongitude());
            intent.putExtra("destlatitude", destinationcoordinate.latitude());
            intent.putExtra("destlongitude", destinationcoordinate.longitude());
            intent.putExtra("myaddress", Pickup.getText().toString());
            intent.putExtra("destaddress", destaddr);
            startActivity(intent);
        });
        Pickup = findViewById(R.id.pick_up);

        destination = findViewById(R.id.destination);
        destination.setOnClickListener(v -> {
            Intent intent = new PlaceAutocomplete.IntentBuilder()
                    .accessToken(Mapbox.getAccessToken() != null ? Mapbox.getAccessToken() : getString(R.string.accesstoken))
                    .placeOptions(PlaceOptions.builder()
                            .backgroundColor(Color.parseColor("#EEEEEE"))
                            .limit(10)
                            .build(PlaceOptions.MODE_CARDS))
                    .build(enter_destination.this);
            startActivityForResult(intent, REQUEST_CODE_AUTOCOMPLETE);
        });

        receiver = new AddressReceiver(new Handler());
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        createLocationRequest();
        settingBuilder = new LocationSettingsRequest.Builder().addLocationRequest(mlocationRequest);
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    Toast.makeText(enter_destination.this, "Current Location ", Toast.LENGTH_SHORT).show();
                } else if (Pickup.getText().length() == 0) {
                    for (Location location : locationResult.getLocations()) {
                        currentLocation = location;
                        try {
                            // service that get the data from background so we can have a readable address
                            Intent i = new Intent(enter_destination.this, AddressService.class);
                            i.putExtra("latitude", location.getLatitude());
                            i.putExtra("longitude", location.getLongitude());
                            i.putExtra("receiver", receiver);
                            startService(i);
                            Toast.makeText(enter_destination.this, "service called", Toast.LENGTH_SHORT).show();
                            break;
                        } catch (Exception e) {
                            Pickup.setText("could not get location");
                        }
                    }
                }
            }
        };
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_AUTOCOMPLETE) {
            CarmenFeature feature = PlaceAutocomplete.getPlace(data);
            destinationcoordinate = feature.center();
            destaddr = feature.text();
            destination.setText(feature.text());
            Toast.makeText(this, resultCode + " " + requestCode, Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {

    }

    @Override
    public void onPermissionResult(boolean granted) {

    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationClient.requestLocationUpdates(mlocationRequest, locationCallback, Looper.getMainLooper());
    }

    private void getLocationSettingStatus() {
        SettingsClient client = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(settingBuilder.build());
        task.addOnSuccessListener(this, locationSettingsResponse -> startLocationUpdates());

        task.addOnFailureListener(this, e -> {
            if (e instanceof ResolvableApiException) {
                try {
                    ResolvableApiException resolvable = (ResolvableApiException) e;
                    resolvable.startResolutionForResult(enter_destination.this, REQUEST_CHECK_SETTINGS);
                } catch (IntentSender.SendIntentException sendEx) {
                    Toast.makeText(enter_destination.this, "some error occurred", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            startLocationUpdates();
        } catch (Exception e) {
            // ignore
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    protected void createLocationRequest() {
        mlocationRequest = LocationRequest.create();
        mlocationRequest.setInterval(1000 * 60 * 24);
        mlocationRequest.setFastestInterval(5000 * 23);
        mlocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private void stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback);
    }

    @SuppressLint("RestrictedApi")
    class AddressReceiver extends ResultReceiver {

        public AddressReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            if (resultCode == RESULT_OK) {
                Address addr = resultData.getParcelable("address");
                if (addr != null) {

                    String addressLine = addr.getAddressLine(0);
                    Pickup.append(
                            String.format("\n%s\n", addressLine)
                    );

                } else {
                    Toast.makeText(enter_destination.this, "address could not found", Toast.LENGTH_SHORT).show();
                }
            }
            if (resultCode == RESULT_CANCELED) {
                Pickup.append("\n Could not get address ");
            }
        }
    }
}
