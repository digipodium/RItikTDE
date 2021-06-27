package com.example.driverapp;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.api.directions.v5.DirectionsCriteria;
import com.mapbox.api.directions.v5.MapboxDirections;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.LineString;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.plugins.markerview.MarkerViewManager;
import com.mapbox.mapboxsdk.style.layers.LineLayer;
import com.mapbox.mapboxsdk.style.layers.Property;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.mapbox.mapboxsdk.utils.BitmapUtils;

import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

import static com.mapbox.core.constants.Constants.PRECISION_6;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconAllowOverlap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconIgnorePlacement;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconOffset;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineCap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineColor;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineJoin;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineWidth;

public class completeTask extends AppCompatActivity implements PermissionsListener {

    public static final String API_KEY = "sk.eyJ1Ijoicml0aWt5YWRhdiIsImEiOiJja3BnamFkZXMwNGRyMndsbWJocDNhNHpsIn0.TpMQMP2IAVHKk65W9jyhuw";
    private static final String MAPBOX_ACCESS_TOKEN = "sk.eyJ1Ijoicml0aWt5YWRhdiIsImEiOiJja3BnamFkZXMwNGRyMndsbWJocDNhNHpsIn0.TpMQMP2IAVHKk65W9jyhuw";
    private static final int REQUEST_CHECK_SETTINGS = 23;
    private static final String ROUTE_LAYER_ID = "route-layer-id";
    private static final String ROUTE_SOURCE_ID = "route-source-id";
    private static final String ICON_LAYER_ID = "icon-layer-id";
    private static final String ICON_SOURCE_ID = "icon-source-id";
    private static final String RED_PIN_ICON_ID = "red-pin-icon-id";
    private String jobid;
    private MapView mapView;

    private PermissionsManager permissionsManager;
    private MapboxMap mapbox;
    private Point destination;
    private Point origin;
    private MapboxDirections client;
    private DirectionsRoute currentRoute;
    private MarkerViewManager markerViewManager;
    private DocumentReference delivery;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Mapbox.getInstance(this, API_KEY);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_task);

        jobid = getIntent().getStringExtra("jobid");
        TextView tvName = findViewById(R.id.recieverName);
        TextView tvAdd = findViewById(R.id.recieverAdd);
        TextView tvPhone = findViewById(R.id.recieverPhone);
        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(mapboxMap -> {
            LatLng latlng = new LatLng(26, 80);
            CameraPosition camPos = new CameraPosition.Builder().target(latlng).zoom(10).build();
            mapboxMap.setCameraPosition(camPos);
            mapboxMap.setStyle(Style.MAPBOX_STREETS, style -> {
                Toast.makeText(this, "loaded map", Toast.LENGTH_SHORT).show();
                enableLocationComponent(style);
                markerViewManager = new MarkerViewManager(mapView, mapboxMap);
            });
            mapbox = mapboxMap;
        });
        db = FirebaseFirestore.getInstance();
        delivery = db.collection("delivery").document(jobid);
        delivery.get()
                .addOnSuccessListener(documentSnapshot -> {
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
                    tvName.setText(receiver_name);
                    tvPhone.setText(receiver_mobile_number);
                    tvAdd.setText(destaddr);
                    mapbox.setStyle(Style.MAPBOX_STREETS, style -> {
                        // Set the origin location to the Alhambra landmark in Granada, Spain.
                        origin = Point.fromLngLat(mylongitude, mylatitude);

                        // Set the destination location to the Plaza del Triunfo in Granada, Spain.
                        destination = Point.fromLngLat(destlongitude, destlatitude);
                        initSource(style);
                        initLayers(style);
                        mapbox.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mylatitude, mylongitude), 14), 4000);
                        // Get the directions route from the Mapbox Directions API
                        getRoute(mapbox, origin, destination);
                    });
                    Button btn = findViewById(R.id.completeTask);
                    btn.setOnClickListener(view -> {
                        WriteBatch batch = db.batch();
                        batch.update(delivery, "statue", "delivered");
                        batch.commit().addOnSuccessListener(unused -> {
                            Intent intent = new Intent(this, collectFare.class);
                            intent.putExtra("price", delivery_price);
                            startActivity(intent);
                        });
                    });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void initSource(@NonNull Style loadedMapStyle) {
        loadedMapStyle.addSource(new GeoJsonSource(ROUTE_SOURCE_ID));

        GeoJsonSource iconGeoJsonSource = new GeoJsonSource(ICON_SOURCE_ID, FeatureCollection.fromFeatures(new Feature[]{
                Feature.fromGeometry(Point.fromLngLat(origin.longitude(), origin.latitude())),
                Feature.fromGeometry(Point.fromLngLat(destination.longitude(), destination.latitude()))}));
        loadedMapStyle.addSource(iconGeoJsonSource);
    }


    private void initLayers(@NonNull Style loadedMapStyle) {
        LineLayer routeLayer = new LineLayer(ROUTE_LAYER_ID, ROUTE_SOURCE_ID);
        // Add the LineLayer to the map. This layer will display the directions route.
        routeLayer.setProperties(
                lineCap(Property.LINE_CAP_ROUND),
                lineJoin(Property.LINE_JOIN_ROUND),
                lineWidth(5f),
                lineColor(Color.parseColor("#009688"))
        );
        loadedMapStyle.addLayer(routeLayer);
        // Add the red marker icon image to the map
        loadedMapStyle.addImage(RED_PIN_ICON_ID, Objects.requireNonNull(BitmapUtils.getBitmapFromDrawable(getResources().getDrawable(R.drawable.red_marker))));
        // Add the red marker icon SymbolLayer to the map
        loadedMapStyle.addLayer(new SymbolLayer(ICON_LAYER_ID, ICON_SOURCE_ID).withProperties(
                iconImage(RED_PIN_ICON_ID),
                iconIgnorePlacement(true),
                iconAllowOverlap(true),
                iconOffset(new Float[]{0f, -9f})));
    }

    private void getRoute(MapboxMap mapboxMap, Point origin, Point destination) {
        client = MapboxDirections.builder()
                .origin(origin)
                .destination(destination)
                .overview(DirectionsCriteria.OVERVIEW_FULL)
                .profile(DirectionsCriteria.PROFILE_DRIVING)
                .accessToken(MAPBOX_ACCESS_TOKEN)
                .build();

        client.enqueueCall(new Callback<DirectionsResponse>() {

            private Double distance;

            @Override
            public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
                // You can get the generic HTTP info about the response
                Timber.d("Response code: " + response.code());
                if (response.body() == null) {
                    Timber.e("No routes found, make sure you set the right user and access token.");
                    return;
                } else if (response.body().routes().size() < 1) {
                    Timber.e("No routes found");
                    return;
                }

                // Get the directions route
                currentRoute = response.body().routes().get(0);
                // Make a toast which displays the route's distance
                distance = currentRoute.distance();
                Toast.makeText(completeTask.this, "distance" + distance, Toast.LENGTH_SHORT).show();

                if (mapboxMap != null) {
                    mapboxMap.getStyle(new Style.OnStyleLoaded() {
                        @Override
                        public void onStyleLoaded(@NonNull Style style) {
                            GeoJsonSource source = style.getSourceAs(ROUTE_SOURCE_ID);
                            if (source != null) {
                                source.setGeoJson(LineString.fromPolyline(currentRoute.geometry(), PRECISION_6));
                            }
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<DirectionsResponse> call, Throwable throwable) {
                Timber.e("Error: " + throwable.getMessage());
                Toast.makeText(completeTask.this, "Error: " + throwable.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    @SuppressWarnings({"MissingPermission"})
    private void enableLocationComponent(@NonNull Style loadedMapStyle) {
        if (PermissionsManager.areLocationPermissionsGranted(this)) {
            LocationComponent locationComponent = mapbox.getLocationComponent();
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
    public void onExplanationNeeded(List<String> permissionsToExplain) {

    }

    @Override
    public void onPermissionResult(boolean granted) {
        Toast.makeText(this, "status" + granted, Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Cancel the Directions API request
        if (client != null) {
            client.cancelCall();
        }
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
}