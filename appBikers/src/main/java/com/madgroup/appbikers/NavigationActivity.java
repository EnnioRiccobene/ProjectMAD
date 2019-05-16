package com.madgroup.appbikers;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

// classes needed to initialize map
import com.madgroup.sdk.SmartLogger;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.api.geocoding.v5.MapboxGeocoding;
import com.mapbox.geocoder.GeocoderCriteria;
import com.mapbox.geocoder.MapboxGeocoder;
import com.mapbox.geocoder.android.AndroidGeocoder;
import com.mapbox.geocoder.service.models.GeocoderResponse;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;

// classes needed to add the location component
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.modes.CameraMode;

// classes needed to add a marker
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;

import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconAllowOverlap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconIgnorePlacement;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage;

// classes to calculate a route
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncherOptions;
import com.mapbox.services.android.navigation.ui.v5.route.NavigationMapRoute;
import com.mapbox.services.android.navigation.v5.navigation.NavigationRoute;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// classes needed to launch navigation UI
import android.view.View;
import android.widget.Button;

import com.mapbox.services.android.navigation.ui.v5.NavigationLauncher;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

//todo: modificare il layout aggiungendo due button nascosti sotto StartNavigation (Restaurant e Customer). Al click su start navigation devono comparire o sparire questi due button, mentre al click su uno dei due faccio partire la navigazione verso il ristorante o il cliente
//todo: aggiungere la possibilità di scegliere percorsi per biker o in macchina

public class NavigationActivity extends AppCompatActivity implements OnMapReadyCallback, MapboxMap.OnMapClickListener, PermissionsListener {

    private MapView mapView;
    private MapboxMap mapboxMap;
    // variables for adding location layer
    private PermissionsManager permissionsManager;
    private LocationComponent locationComponent;

    // variables for calculating and drawing a route
    private DirectionsRoute currentRoute;
    private DirectionsRoute dummyRoute;
    private static final String TAG = "DirectionsActivity";
    private NavigationMapRoute navigationMapRoute;

    // variables needed to initialize navigation
    private Button button;

    private String restaurantAddress = "", customerAddress = "";
    private Point restaurantPoint;
    private Point customerPoint;
    MapboxGeocoding mapboxGeocoding;

    public static void start(Context context, String restaurantAddress, String customerAddress) {
        Intent starter = new Intent(context, NavigationActivity.class);
        starter.putExtra("restaurantAddress", restaurantAddress);
        starter.putExtra("customerAddress", customerAddress);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, "pk.eyJ1IjoiZW5uaW9yaWNjb2JlbmUiLCJhIjoiY2p2cDAwaWpiMWM2cTRhdm4xa2doMWR4aSJ9.13f4K6NH4ybrj9iPVzG7kA");
        setContentView(R.layout.my_activity_navigation);
        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        getIncomingIntent();

        new GeocodeTask().execute("url");
//        addressesGeocode(restaurantAddress, customerAddress);

//        drawRestaurantToClientRoute(restaurantPoint, customerPoint);
    }

    private void getIncomingIntent() {
        if (getIntent().hasExtra("restaurantAddress")) {
            restaurantAddress = getIntent().getStringExtra("restaurantAddress");
            customerAddress = getIntent().getStringExtra("customerAddress");
        }
    }

    private class GeocodeTask extends AsyncTask<String, Void, String> {
        private ProgressDialog pd;

        // onPreExecute called before the doInBackgroud start for display
        // progress dialog.
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = ProgressDialog.show(NavigationActivity.this, "", "Loading", true,
                    false); // Create and show Progress dialog
        }

        @Override
        protected String doInBackground(String... strings) {
            addressesGeocode(restaurantAddress, customerAddress);
            return null;
        }

        // onPostExecute displays the results of the doInBackgroud and also we
        // can hide progress dialog.
        @Override
        protected void onPostExecute(String result) {
            drawRestaurantToClientRoute(restaurantPoint, customerPoint);
            pd.dismiss();

        }
    }

    private Point getLocationFromAddress(String strAddress) {

        AndroidGeocoder geocoder = new AndroidGeocoder(this, Locale.getDefault());
        List<Address> address = null;
        geocoder.setAccessToken("pk.eyJ1IjoiZW5uaW9yaWNjb2JlbmUiLCJhIjoiY2p2cDAwaWpiMWM2cTRhdm4xa2doMWR4aSJ9.13f4K6NH4ybrj9iPVzG7kA");
        try {
            address = geocoder.getFromLocationName(strAddress, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (address == null) {
            return null;
        }
        Address location = address.get(0);
        location.getLatitude();
        location.getLongitude();

        Point p1 = Point.fromLngLat((double) (location.getLongitude()/* * 1E6*/), (double) (location.getLatitude()/* * 1E6*/));

        return p1;

//        // The geocoder client
//        MapboxGeocoder client = new MapboxGeocoder.Builder()
//                .setAccessToken("pk.eyJ1IjoiZW5uaW9yaWNjb2JlbmUiLCJhIjoiY2p2cDAwaWpiMWM2cTRhdm4xa2doMWR4aSJ9.13f4K6NH4ybrj9iPVzG7kA")
//                .setLocation(strAddress)
//                .setType(GeocoderCriteria.TYPE_POI)
//                .build();
//
//        retrofit.Response<GeocoderResponse> response;
//        try {
//            response = client.execute();
//            response.body().getFeatures().
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

//        Geocoder coder = new Geocoder(this, Locale.getDefault());
//        List<Address> address = null;
//
//        try {
//            address = coder.getFromLocationName(strAddress, 1);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        if (address == null) {
//            return null;
//        }
//        Address location = address.get(0);
//        location.getLatitude();
//        location.getLongitude();
//
//        Point p1 = Point.fromLngLat((double) (location.getLongitude()/* * 1E6*/), (double) (location.getLatitude()/* * 1E6*/));
//
//        return p1;

    }

    private void addressesGeocode(String restaurantAddress, String customerAddress) {

        restaurantPoint = getLocationFromAddress(restaurantAddress);
        customerPoint = getLocationFromAddress(customerAddress);
    }


    @Override
    public void onMapReady(@NonNull final MapboxMap mapboxMap) {
        this.mapboxMap = mapboxMap;
        mapboxMap.setStyle(getString(R.string.navigation_guidance_day), new Style.OnStyleLoaded() {
            @Override
            public void onStyleLoaded(@NonNull Style style) {
                enableLocationComponent(style);

                addDestinationIconSymbolLayer(style);

                mapboxMap.addOnMapClickListener(NavigationActivity.this);

                button = findViewById(R.id.startButton);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {



                        boolean simulateRoute = false;
                        NavigationLauncherOptions options = NavigationLauncherOptions.builder()
                                .directionsRoute(currentRoute)
                                .shouldSimulateRoute(simulateRoute)
                                .build();
                        // Call this method with Context from within an Activity
                        NavigationLauncher.startNavigation(NavigationActivity.this, options);
                    }
                });
            }
        });
    }

    private void addDestinationIconSymbolLayer(@NonNull Style loadedMapStyle) {
        loadedMapStyle.addImage("destination-icon-id",
                BitmapFactory.decodeResource(this.getResources(), R.drawable.mapbox_marker_icon_default));
        GeoJsonSource geoJsonSource = new GeoJsonSource("destination-source-id");
        loadedMapStyle.addSource(geoJsonSource);
        SymbolLayer destinationSymbolLayer = new SymbolLayer("destination-symbol-layer-id", "destination-source-id");
        destinationSymbolLayer.withProperties(
                iconImage("destination-icon-id"),
                iconAllowOverlap(true),
                iconIgnorePlacement(true)
        );
        loadedMapStyle.addLayer(destinationSymbolLayer);
    }

    @SuppressWarnings({"MissingPermission"})
    @Override
    public boolean onMapClick(@NonNull LatLng point) {

//        Point destinationPoint = Point.fromLngLat(point.getLongitude(), point.getLatitude());
//        Point originPoint = Point.fromLngLat(locationComponent.getLastKnownLocation().getLongitude(),
//                locationComponent.getLastKnownLocation().getLatitude());
//
//        GeoJsonSource source = mapboxMap.getStyle().getSourceAs("destination-source-id");
//        if (source != null) {
//            source.setGeoJson(Feature.fromGeometry(destinationPoint));
//        }
//        getRoute(originPoint, destinationPoint);
//
//        button.setEnabled(true);
//        button.setBackgroundResource(R.color.mapboxBlue);

        return true;
    }


    private void drawRestaurantToClientRoute(Point origin, Point destination) {

        NavigationRoute.builder(this)
                .accessToken(Mapbox.getAccessToken())
                .origin(origin)
                .destination(destination)
                .profile("cycling")
                .build()
                .getRoute(new Callback<DirectionsResponse>() {
                    @Override
                    public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
                        // You can get the generic HTTP info about the response
                        SmartLogger.d(TAG, "Response code: " + response.code());
                        if (response.body() == null) {
                            SmartLogger.e(TAG, "No routes found, make sure you set the right user and access token.");
                            return;
                        } else if (response.body().routes().size() < 1) {
                            SmartLogger.e(TAG, "No routes found");
                            return;
                        }

                        dummyRoute = response.body().routes().get(0);

                        GeoJsonSource source = mapboxMap.getStyle().getSourceAs("destination-source-id");
                        if (source != null) {
                            source.setGeoJson(Feature.fromGeometry(destination));
                        }

                        // Draw the route on the map
                        if (navigationMapRoute != null) {
                            navigationMapRoute.removeRoute();
                        } else {
                            navigationMapRoute = new NavigationMapRoute(null, mapView, mapboxMap, R.style.NavigationMapRoute);
                        }
                        navigationMapRoute.addRoute(dummyRoute);
                    }

                    @Override
                    public void onFailure(Call<DirectionsResponse> call, Throwable throwable) {
                        SmartLogger.e(TAG, "Error: " + throwable.getMessage());
                    }
                });
    }

    private void getRoute(Point origin, Point destination) {
        NavigationRoute.builder(this)
                .accessToken(Mapbox.getAccessToken())
                .origin(origin)
                .destination(destination)
                .profile("cycling")
                .build()
                .getRoute(new Callback<DirectionsResponse>() {
                    @Override
                    public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
                        // You can get the generic HTTP info about the response
                        SmartLogger.d(TAG, "Response code: " + response.code());
                        if (response.body() == null) {
                            SmartLogger.e(TAG, "No routes found, make sure you set the right user and access token.");
                            return;
                        } else if (response.body().routes().size() < 1) {
                            SmartLogger.e(TAG, "No routes found");
                            return;
                        }

                        currentRoute = response.body().routes().get(0);

                        // Draw the route on the map
                        if (navigationMapRoute != null) {
                            navigationMapRoute.removeRoute();
                        } else {
                            navigationMapRoute = new NavigationMapRoute(null, mapView, mapboxMap, R.style.NavigationMapRoute);
                        }
                        navigationMapRoute.addRoute(currentRoute);
                    }

                    @Override
                    public void onFailure(Call<DirectionsResponse> call, Throwable throwable) {
                        SmartLogger.e(TAG, "Error: " + throwable.getMessage());
                    }
                });
    }

    @SuppressWarnings( {"MissingPermission"})
    private void enableLocationComponent(@NonNull Style loadedMapStyle) {
        // Check if permissions are enabled and if not request
        if (PermissionsManager.areLocationPermissionsGranted(this)) {
            // Activate the MapboxMap LocationComponent to show user location
            // Adding in LocationComponentOptions is also an optional parameter
            locationComponent = mapboxMap.getLocationComponent();
            locationComponent.activateLocationComponent(this, loadedMapStyle);
            locationComponent.setLocationComponentEnabled(true);
            // Set the component's camera mode
            locationComponent.setCameraMode(CameraMode.TRACKING);
        } else {
            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(this);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {
        Toast.makeText(this, R.string.user_location_permission_explanation, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onPermissionResult(boolean granted) {
        if (granted) {
            enableLocationComponent(mapboxMap.getStyle());
        } else {
            Toast.makeText(this, R.string.user_location_permission_not_granted, Toast.LENGTH_LONG).show();
            finish();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onStop() {
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
}
