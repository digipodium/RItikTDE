package com.example.t_d_e;

import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationSettingsRequest;
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
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.plugins.markerview.MarkerView;
import com.mapbox.mapboxsdk.plugins.markerview.MarkerViewManager;
import com.mapbox.mapboxsdk.style.layers.LineLayer;
import com.mapbox.mapboxsdk.style.layers.Property;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.mapbox.mapboxsdk.utils.BitmapUtils;
import com.mapbox.services.android.navigation.v5.navigation.MapboxNavigation;

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

public class Check_to_proceed extends AppCompatActivity {
    public static final String API_KEY = "sk.eyJ1Ijoicml0aWt5YWRhdiIsImEiOiJja3BnamFkZXMwNGRyMndsbWJocDNhNHpsIn0.TpMQMP2IAVHKk65W9jyhuw";
    private static final String MAPBOX_ACCESS_TOKEN = "sk.eyJ1Ijoicml0aWt5YWRhdiIsImEiOiJja3BnamFkZXMwNGRyMndsbWJocDNhNHpsIn0.TpMQMP2IAVHKk65W9jyhuw";
    private static final int REQUEST_CHECK_SETTINGS = 23;
    private static final String ROUTE_LAYER_ID = "route-layer-id";
    private static final String ROUTE_SOURCE_ID = "route-source-id";
    private static final String ICON_LAYER_ID = "icon-layer-id";
    private static final String ICON_SOURCE_ID = "icon-source-id";
    private static final String RED_PIN_ICON_ID = "red-pin-icon-id";
    Button check_to_proceed;
    MapboxNavigation navigation;
    private MapView mapView;
    private MapboxMap mapboxMap;
    private DirectionsRoute currentRoute;
    private MapboxDirections client;
    private Point origin;
    private Point destination;
    private PermissionsManager permissionsManager;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationRequest mlocationRequest;
    private LocationSettingsRequest.Builder settingBuilder;
    private LocationCallback locationCallback;
    private Location currentLocation;
    private MarkerViewManager markerViewManager;
    private MarkerView markerView;
    private MapboxMap mapbox;
    private View mView;
    private Intent intent;
    private double mylatitude;
    private double mylongitude;
    private double destlatitude;
    private double destlongitude;
    private String destaddress;
    private String myaddress;
    private String destaddr;
    private String Pickup;
    private Double distance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Mapbox.getInstance(this, API_KEY);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_to_proceed);

        intent = getIntent();
        mylatitude = intent.getDoubleExtra("mylatitude", 0.0);
        mylongitude = intent.getDoubleExtra("mylongitude", 0.0);
        destlatitude = intent.getDoubleExtra("destlatitude", 0.0);
        destlongitude = intent.getDoubleExtra("destlongitude", 0.0);
        destaddr = intent.getStringExtra("destaddress");
        Pickup = intent.getStringExtra("myaddress");

        Mapbox.getInstance(this, getString(R.string.access_token));

        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull MapboxMap mapboxMap) {
                mapboxMap.setStyle(Style.MAPBOX_STREETS, new Style.OnStyleLoaded() {
                    @Override
                    public void onStyleLoaded(@NonNull Style style) {
                        // Set the origin location to the Alhambra landmark in Granada, Spain.
                        origin = Point.fromLngLat(mylongitude, mylatitude);

                        // Set the destination location to the Plaza del Triunfo in Granada, Spain.
                        destination = Point.fromLngLat(destlongitude, destlatitude);

                        initSource(style);

                        initLayers(style);
                        mapboxMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mylatitude, mylongitude), 14), 4000);

                        // Get the directions route from the Mapbox Directions API
                        getRoute(mapboxMap, origin, destination);
                    }
                });
            }
        });
    }


    private void initSource(@NonNull Style loadedMapStyle) {
        loadedMapStyle.addSource(new GeoJsonSource(ROUTE_SOURCE_ID));

        GeoJsonSource iconGeoJsonSource = new GeoJsonSource(ICON_SOURCE_ID, FeatureCollection.fromFeatures(new Feature[]{
                Feature.fromGeometry(Point.fromLngLat(origin.longitude(), origin.latitude())),
                Feature.fromGeometry(Point.fromLngLat(destination.longitude(), destination.latitude()))}));
        loadedMapStyle.addSource(iconGeoJsonSource);
    }

    /**
     * Add the route and marker icon layers to the map
     */
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
                .accessToken(getString(R.string.access_token))
                .build();

        client.enqueueCall(new Callback<DirectionsResponse>() {
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
                Toast.makeText(Check_to_proceed.this, "distance" + distance, Toast.LENGTH_SHORT).show();

                if (mapboxMap != null) {
                    mapboxMap.getStyle(new Style.OnStyleLoaded() {
                        @Override
                        public void onStyleLoaded(@NonNull Style style) {

                            // Retrieve and update the source designated for showing the directions route
                            GeoJsonSource source = style.getSourceAs(ROUTE_SOURCE_ID);

                            // Create a LineString with the directions route's geometry and
                            // reset the GeoJSON source for the route LineLayer source
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
                Toast.makeText(Check_to_proceed.this, "Error: " + throwable.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private double distance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1))
                * Math.sin(deg2rad(lat2))
                + Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        return (dist);
        // Toast.makeText(this, "distance" +, Toast.LENGTH_SHORT).show();
    }


    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
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

    public void useBike(View view) {
        double price = distance * .1225;
        Intent intent = new Intent(getApplicationContext(), Deliver_Now.class);
        intent.putExtra("mylatitude", mylatitude);
        intent.putExtra("mylongitude", mylongitude);
        intent.putExtra("destlatitude", destlatitude);
        intent.putExtra("destlongitude", destlongitude);
        intent.putExtra("destaddr", getIntent().getStringExtra("destaddress"));
        intent.putExtra("myaddr", getIntent().getStringExtra("myaddress"));
        intent.putExtra("price", price);
        intent.putExtra("vehicle", "bike");
        new AlertDialog.Builder(this).setTitle("continue >>").setMessage(String.format("the base cost will be Rs %.2f", price)).setPositiveButton("confirm", (dialogInterface, i) -> {
            startActivity(intent);
        }).create().show();
    }

    public void useRick(View view) {
        double price = distance * .125;

        Intent intent = new Intent(getApplicationContext(), Deliver_Now.class);
        intent.putExtra("mylatitude", mylatitude);
        intent.putExtra("mylongitude", mylongitude);
        intent.putExtra("destlatitude", destlatitude);
        intent.putExtra("destlongitude", destlongitude);
        intent.putExtra("destaddr", getIntent().getStringExtra("destaddress"));
        intent.putExtra("myaddr", getIntent().getStringExtra("myaddress"));
        intent.putExtra("price", price);
        intent.putExtra("vehicle", "rickshaw");
        new AlertDialog.Builder(this).setTitle("continue >>").setMessage(String.format("the base cost will be Rs %.2f", price)).setPositiveButton("confirm", (dialogInterface, i) -> {
            startActivity(intent);
        }).create().show();
    }

    public void useMini(View view) {
        double price = distance * .09;
        Intent intent = new Intent(getApplicationContext(), Deliver_Now.class);
        intent.putExtra("mylatitude", mylatitude);
        intent.putExtra("mylongitude", mylongitude);
        intent.putExtra("destlatitude", destlatitude);
        intent.putExtra("destlongitude", destlongitude);
        intent.putExtra("destaddr", getIntent().getStringExtra("destaddress"));
        intent.putExtra("myaddr", getIntent().getStringExtra("myaddress"));
        intent.putExtra("price", price);
        intent.putExtra("vehicle", "van");
        new AlertDialog.Builder(this).setTitle("continue >>").setMessage(String.format("the base cost will be Rs %.2f", price)).setPositiveButton("confirm", (dialogInterface, i) -> {
            startActivity(intent);
        }).create().show();
    }
}