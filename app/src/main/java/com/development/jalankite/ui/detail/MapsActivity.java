package com.development.jalankite.ui.detail;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.animation.BounceInterpolator;
import android.widget.Toast;

import com.development.jalankite.R;
import com.development.jalankite.databinding.ActivityMapsBinding;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions;
import com.mapbox.mapboxsdk.location.LocationComponentOptions;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.plugins.annotation.SymbolManager;
import com.mapbox.mapboxsdk.plugins.annotation.SymbolOptions;
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncher;
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncherOptions;
import com.mapbox.services.android.navigation.ui.v5.route.NavigationMapRoute;
import com.mapbox.services.android.navigation.v5.navigation.NavigationRoute;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapsActivity extends AppCompatActivity {

    ActivityMapsBinding binding;
    private MapboxMap mapboxMap;
    private static final String ICON_ID = "ICON_ID";
    private SymbolManager symbolManager;
    private LocationComponent locationComponent;
    private PermissionsManager permissionsManager;
    private NavigationMapRoute navigationMapRoute;
    private DirectionsRoute currentRoute;
    private Double latLokasi;
    private Double longLokasi;
    private String namaLokasi;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, getString(R.string.mapbox_access_token));
        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        namaLokasi = getIntent().getStringExtra("intent_nama_lokasi");
        longLokasi = getIntent().getDoubleExtra("intent_long_lokasi", 0);
        latLokasi = getIntent().getDoubleExtra("intent_lat_lokasi", 0);

        // peta mapbox
        binding.mapViewDetail.onCreate(savedInstanceState);
        binding.mapViewDetail.getMapAsync(mapbox -> {
            this.mapboxMap = mapbox;
            mapbox.setStyle(Style.MAPBOX_STREETS, style -> {
                symbolManager = new SymbolManager(binding.mapViewDetail, mapbox, style);
                symbolManager.setIconAllowOverlap(true);
                style.addImage(ICON_ID, BitmapFactory.decodeResource(getResources(), com.mapbox.mapboxsdk.R.drawable.mapbox_marker_icon_default));
                navigationMapRoute = new NavigationMapRoute(
                        null,
                        binding.mapViewDetail,
                        mapbox,
                        com.mapbox.services.android.navigation.ui.v5.R.style.NavigationMapRoute
                );
                tujuan();
                showMyLocation(style);

            });
        });


    }



    @SuppressLint("MissingPermission")
    private void showMyLocation(Style style) {
        if (PermissionsManager.areLocationPermissionsGranted(this)) {
            LocationComponentOptions locationComponentOptions = LocationComponentOptions.builder(this)
                    .pulseEnabled(true)
                    .pulseColor(Color.BLUE)
                    .pulseAlpha(.4f)
                    .pulseInterpolator(new BounceInterpolator())
                    .build();
            LocationComponentActivationOptions locationComponentActivationOptions = LocationComponentActivationOptions
                    .builder(this, style)
                    .locationComponentOptions(locationComponentOptions)
                    .build();
            locationComponent = mapboxMap.getLocationComponent();
            locationComponent.activateLocationComponent(locationComponentActivationOptions);
            locationComponent.setLocationComponentEnabled(true);
            locationComponent.setCameraMode(CameraMode.TRACKING);
            locationComponent.setRenderMode(RenderMode.COMPASS);
            if (locationComponent.getLastKnownLocation() != null){
                LatLng myloc = new LatLng(locationComponent.getLastKnownLocation().getLatitude(),locationComponent.getLastKnownLocation().getLongitude());
                CameraUpdateFactory.newLatLngZoom(myloc,12.0);
                mapboxMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myloc,12.0));
                getRute(locationComponent.getLastKnownLocation().getLongitude(),locationComponent.getLastKnownLocation().getLatitude());
            } else {
                Toast.makeText(this, "Silahkan hidupkan GPS dan restart aplikasi", Toast.LENGTH_SHORT).show();
            }

        }
        else {
            permissionsManager = new PermissionsManager(new PermissionsListener() {
                @Override
                public void onExplanationNeeded(List<String> permissionsToExplain) {
                    Toast.makeText(getApplicationContext(), "Anda harus mengizinkan location permission untuk menggunakan aplikasi ini", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onPermissionResult(boolean granted) {
                    if (granted) {
                        mapboxMap.getStyle(new Style.OnStyleLoaded() {
                            @Override
                            public void onStyleLoaded(@NonNull Style style) {
                                showMyLocation(style);
                            }
                        });
                    } else {
                        Toast.makeText(getApplicationContext(), R.string.user_location_permission_not_granted, Toast.LENGTH_LONG).show();
                        finish();
                    }
                }
            });
            permissionsManager.requestLocationPermissions(this);

        }

    }

    private void getRute(double longitude, double latitude) {
        Point destination = Point.fromLngLat(longLokasi, latLokasi);
        Point origin = Point.fromLngLat(longitude, latitude);
        requestRoute(origin, destination);
    }

    private void requestRoute(Point origin, Point destination) {
        showNavigation();
        navigationMapRoute.updateRouteVisibilityTo(false);
        NavigationRoute.builder(this)
                .accessToken(getString(R.string.mapbox_access_token))
                .origin(origin)
                .destination(destination)
                .build()
                .getRoute(new Callback<DirectionsResponse>() {
                    @Override
                    public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
                        if (response.body() == null){
                            Toast.makeText(getApplicationContext(), "No routes found, make sure you set the right user and access token.",Toast.LENGTH_SHORT).show();
                            return;
                        }
                        else if (response.body().routes().size() == 0) {
                            Toast.makeText(getApplicationContext(), "No routes found.", Toast.LENGTH_SHORT)
                                    .show();
                            return;
                        }
                        currentRoute = response.body().routes().get(0);
                        navigationMapRoute.addRoute(currentRoute);
                    }

                    @Override
                    public void onFailure(Call<DirectionsResponse> call, Throwable t) {
                        Toast.makeText(getApplicationContext(), "Error : " + t, Toast.LENGTH_SHORT).show();

                    }
                });
    }

    private void showNavigation() {
        binding.btnNavigation.setVisibility(View.VISIBLE);
        binding.btnNavigation.setOnClickListener(view -> {
            NavigationLauncherOptions options = NavigationLauncherOptions.builder()
                    .directionsRoute(currentRoute)
                    .shouldSimulateRoute(true)
                    .build();
            NavigationLauncher.startNavigation(this,options);
        });
    }


    private void tujuan() {
        LatLng tujuanLokasi = new LatLng(latLokasi,longLokasi);
        symbolManager.create(new SymbolOptions()
                .withLatLng( new LatLng(latLokasi,longLokasi))
                .withIconImage(ICON_ID)
                .withIconSize(1.5f)
                .withTextField(namaLokasi)
                .withTextHaloColor("rgba(255, 255, 255, 100)")
                .withTextHaloWidth(5.0f)
                .withTextAnchor("top")
                .withDraggable(true)
        );
        mapboxMap.moveCamera(CameraUpdateFactory.newLatLngZoom(tujuanLokasi,8.0));
    }


    @Override
    protected void onStart() {
        super.onStart();
        binding.mapViewDetail.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        binding.mapViewDetail.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        binding.mapViewDetail.onPause();
    }
    @Override
    protected void onStop() {
        super.onStop();
        binding.mapViewDetail.onStop();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        binding.mapViewDetail.onSaveInstanceState(outState);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding.mapViewDetail.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        binding.mapViewDetail.onLowMemory();
    }
}