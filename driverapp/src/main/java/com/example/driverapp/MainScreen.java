package com.example.driverapp;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.annotations.NotNull;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.plugins.markerview.MarkerView;
import com.mapbox.mapboxsdk.plugins.markerview.MarkerViewManager;

import java.util.ArrayList;
import java.util.List;

public class MainScreen<fusedLocationClient> extends AppCompatActivity implements PermissionsListener {
    public static final String API_KEY = "sk.eyJ1Ijoicml0aWt5YWRhdiIsImEiOiJja3BnamFkZXMwNGRyMndsbWJocDNhNHpsIn0.TpMQMP2IAVHKk65W9jyhuw";
    private static final String MAPBOX_ACCESS_TOKEN = "sk.eyJ1Ijoicml0aWt5YWRhdiIsImEiOiJja3BnamFkZXMwNGRyMndsbWJocDNhNHpsIn0.TpMQMP2IAVHKk65W9jyhuw";
    private static final int REQUEST_CHECK_SETTINGS = 23;
    private static final String ROUTE_LAYER_ID = "route-layer-id";
    private static final String ROUTE_SOURCE_ID = "route-source-id";
    private static final String ICON_LAYER_ID = "icon-layer-id";
    private static final String ICON_SOURCE_ID = "icon-source-id";
    private static final String RED_PIN_ICON_ID = "red-pin-icon-id";

    NavigationView navigationView;
    ActionBarDrawerToggle toggle;
    DrawerLayout drawerLayout;
    private MapView mapView;
    private MapboxMap mapboxMap;
    private PermissionsManager permissionsManager;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationRequest mlocationRequest;
    private LocationSettingsRequest.Builder settingBuilder;
    private LocationCallback locationCallback;
    private Location currentLocation;
    private MarkerViewManager markerViewManager;
    private MarkerView markerView;
    private View mView;
    private MapboxMap mapbox;
    private FirebaseAuth auth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Mapbox.getInstance(this, API_KEY);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        navigationView = findViewById(R.id.navmenu);
        drawerLayout = findViewById(R.id.drawer);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.menu_home:
                        Intent intent = new Intent(getApplicationContext(), MainScreen.class);
                        startActivity(intent);
                        Toast.makeText(getApplicationContext(), "Home Panel is Open", Toast.LENGTH_LONG).show();
                        drawerLayout.closeDrawer(GravityCompat.START);
                        break;

                    case R.id.menu_history:
                        Toast.makeText(getApplicationContext(), "Task history Panel is Open", Toast.LENGTH_LONG).show();
                        drawerLayout.closeDrawer(GravityCompat.START);
                        break;

                    case R.id.menu_payment:
                        Toast.makeText(getApplicationContext(), "payments  panel is Open", Toast.LENGTH_LONG).show();
                        drawerLayout.closeDrawer(GravityCompat.START);
                        break;

                    case R.id.menu_notification:
                        Toast.makeText(getApplicationContext(), "Notification panel is Open", Toast.LENGTH_LONG).show();
                        drawerLayout.closeDrawer(GravityCompat.START);
                        break;

                    case R.id.menu_setting:
                        Toast.makeText(getApplicationContext(), "Setting Panel is Open", Toast.LENGTH_LONG).show();
                        drawerLayout.closeDrawer(GravityCompat.START);
                        break;
                    case R.id.menu_logout:
                        FirebaseAuth.getInstance().signOut();
                        Intent intent1 = new Intent(getApplicationContext(), page3.class);
                        startActivity(intent1);
                        Toast.makeText(getApplicationContext(), "Logout ", Toast.LENGTH_LONG).show();
                        drawerLayout.closeDrawer(GravityCompat.START);
                        break;
                }

                return true;
            }
        });
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        createLocationRequest();
        settingBuilder = new LocationSettingsRequest.Builder().addLocationRequest(mlocationRequest);
        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(mapboxMap -> {
            MainScreen.this.mapboxMap = mapboxMap;
            LatLng latlng = new LatLng(26, 80);
            CameraPosition camPos = new CameraPosition.Builder().target(latlng).zoom(10).build();
            mapboxMap.setCameraPosition(camPos);
            mapboxMap.setStyle(Style.MAPBOX_STREETS, style -> {
                Toast.makeText(MainScreen.this, "loaded map", Toast.LENGTH_SHORT).show();
                enableLocationComponent(style);
                markerViewManager = new MarkerViewManager(mapView, mapboxMap);
            });
            mapbox = mapboxMap;
        });
        locationCallback = new LocationCallback() {

            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    currentLocation = location;
                    if (mapbox != null) {
                        if (markerView != null) {
                            markerViewManager.removeMarker(markerView);
                        }
                        try {
                            markerView = new MarkerView(new LatLng(location.getLatitude(), location.getLongitude()), mView);
                            markerViewManager.addMarker(markerView);
                        } catch (Exception ignored) {

                        }
                    }
                }
            }
        };

        RecyclerView jobCycler = findViewById(R.id.jobRecycler);
        jobCycler.setLayoutManager(new LinearLayoutManager(this));
        List<Object> data = new ArrayList<>();
        JobAdapter adapter = new JobAdapter(this, R.layout.job_layout, data);
        jobCycler.setAdapter(adapter);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        db.collection("delivery")
                .whereEqualTo("statue", "active").get()
                .addOnFailureListener(e -> {
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                })
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    data.addAll(queryDocumentSnapshots.getDocuments());
                    adapter.notifyDataSetChanged();
                    Toast.makeText(this, "data loaded", Toast.LENGTH_SHORT).show();
                });
        for (Object e : data) {
            DocumentSnapshot o = (DocumentSnapshot) e;
            Double mylatitude = o.getDouble("mylatitude");
            Double mylongitude = o.getDouble("mylongitude");
            String jobfrom = o.getString("Sender Number") +" job";
            mapbox.addMarker(new MarkerOptions().position(new LatLng(mylatitude,mylongitude)).title(jobfrom));
        }
    }

    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {

    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationClient.requestLocationUpdates(mlocationRequest,
                locationCallback,
                Looper.getMainLooper());
    }

    private void getLocationSettingStatus() {
        SettingsClient client = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(settingBuilder.build());
        task.addOnSuccessListener(this, locationSettingsResponse -> startLocationUpdates());
        task.addOnFailureListener(this, e -> {
            if (e instanceof ResolvableApiException) {
                try {
                    ResolvableApiException resolvable = (ResolvableApiException) e;
                    resolvable.startResolutionForResult(MainScreen.this, REQUEST_CHECK_SETTINGS);
                } catch (IntentSender.SendIntentException sendEx) {
                    Toast.makeText(MainScreen.this, "some error occurred", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
        try {
            startLocationUpdates();
        } catch (Exception e) {
            // ignore
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
        stopLocationUpdates();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @SuppressWarnings({"MissingPermission"})
    private void enableLocationComponent(@NonNull Style loadedMapStyle) {
        if (PermissionsManager.areLocationPermissionsGranted(this)) {
            LocationComponent locationComponent = mapboxMap.getLocationComponent();
            locationComponent.activateLocationComponent(LocationComponentActivationOptions.builder(this, loadedMapStyle).build());
            locationComponent.setLocationComponentEnabled(true);
            locationComponent.setCameraMode(CameraMode.TRACKING);
            locationComponent.setRenderMode(RenderMode.COMPASS);

        } else {
            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(this);
        }
    }

    @Override
    public void onPermissionResult(boolean granted) {
        Toast.makeText(MainScreen.this, "status" + granted, Toast.LENGTH_SHORT).show();
    }

    protected void createLocationRequest() {
        mlocationRequest = LocationRequest.create();
        mlocationRequest.setInterval(1000 * 30);
        mlocationRequest.setFastestInterval(5000);
        mlocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private void stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback);
    }

    private class JobAdapter extends RecyclerView.Adapter<JobAdapter.ViewHolder> {

        private final LayoutInflater inflater;
        private final int job_layout;
        private final List<Object> objectList;
        private final Context context;

        public JobAdapter(Context context, int job_layout, List<Object> objectList) {
            inflater = LayoutInflater.from(context);
            this.job_layout = job_layout;
            this.objectList = objectList;
            this.context = context;
        }

        @NonNull
        @NotNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
            return new ViewHolder(inflater.inflate(job_layout, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull @NotNull JobAdapter.ViewHolder holder, int position) {
            DocumentSnapshot o = (DocumentSnapshot) objectList.get(position);
            String receiver_name = o.getString("Receiver Name");
            String receiver_mobile_number = o.getString("Receiver Mobile Number");
            String sender_number = o.getString("Sender Number");
            String senderid = o.getString("senderid");
            String pick_up_instruction = o.getString("Pick up Instruction");
            String delivery_instruction = o.getString("Delivery Instruction");
            String package_size = o.getString("Package Size");
            String package_weight = o.getString("Package Weight");
            Double delivery_price = o.getDouble("Delivery Price");
            String delivery_vehicle = o.getString("Delivery Vehicle");
            String dp = o.getString("dp");
            String statue = o.getString("statue");
            Double mylatitude = o.getDouble("mylatitude");
            Double mylongitude = o.getDouble("mylongitude");
            Double destlatitude = o.getDouble("destlatitude");
            Double destlongitude = o.getDouble("destlongitude");
            String destaddr = o.getString("destaddr");
            String myaddr = o.getString("myaddr");
            holder.dest.setText(String.format("%s \n%s", destaddr, receiver_mobile_number));
            holder.pick.setText(String.format("%s \n%s", myaddr, receiver_mobile_number));
            holder.details.setText(
                    String.format("destination %s\npickup %s\nstatus %s\ndelivery person %s\ndelivery vehicle %s\npackage size %s\npackage weight %s\npackage price %s\n", destaddr, myaddr, statue, dp, delivery_vehicle, package_size, package_weight, delivery_price)
            );
            holder.name.setText(String.format("delivery %s package", receiver_name));

        }

        @Override
        public int getItemCount() {
            return objectList.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView name, pick, dest, details;
            public ViewHolder(@NonNull @NotNull View v) {
                super(v);
                name = v.findViewById(R.id.name);
                pick = v.findViewById(R.id.testPick);
                dest = v.findViewById(R.id.textDest);
                details = v.findViewById(R.id.details);
                v.findViewById(R.id.buttonA).setOnClickListener(view -> {
                    DocumentSnapshot o = (DocumentSnapshot) objectList.get(getAdapterPosition());
                    Intent intent = new Intent(context, request.class);
                    intent.putExtra("id",o.getId());
                    startActivity(intent);
                });

            }
        }
    }
}