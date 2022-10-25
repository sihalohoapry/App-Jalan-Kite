package com.development.jalankite.ui.home.terdekatfragment;

import static java.lang.Math.PI;
import static java.lang.Math.atan2;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.BounceInterpolator;
import android.widget.Toast;

import com.development.jalankite.R;
import com.development.jalankite.databinding.FragmentTerdekatBinding;
import com.development.jalankite.network.ApiClient;
import com.development.jalankite.ui.AdapterLokasiTerdekat;
import com.development.jalankite.ui.detail.DetailActivity;
import com.development.jalankite.ui.home.AllLokasiResponse;
import com.development.jalankite.ui.home.DataItemTerdekat;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions;
import com.mapbox.mapboxsdk.location.LocationComponentOptions;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.Style;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class TerdekatFragment extends Fragment {

    private FragmentTerdekatBinding binding;
    private RecyclerView rvLokasi;
    private ArrayList<DataItemTerdekat> list = new ArrayList<>();
    private PermissionsManager permissionsManager;
    private LocationComponent locationComponent;
    private MapboxMap mapboxMap;
    private AdapterLokasiTerdekat adapterLokasiTerdekat;
    private AlertDialog alertDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Mapbox.getInstance(requireContext(), getString(R.string.mapbox_access_token));
        binding = FragmentTerdekatBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        binding.mapView.onCreate(savedInstanceState);

        setAdapter();
        getMyLocation();


        return view;
    }
    @SuppressLint("MissingPermission")
    private void getMyLocation() {
        loading(true);
        binding.mapView.getMapAsync(mapboxMap -> {
            this.mapboxMap = mapboxMap;
            mapboxMap.setStyle(Style.MAPBOX_STREETS, style -> {
                if (PermissionsManager.areLocationPermissionsGranted(requireContext())) {
                    LocationComponentOptions locationComponentOptions = LocationComponentOptions.builder(requireContext())
                            .pulseEnabled(true)
                            .pulseColor(Color.BLUE)
                            .pulseAlpha(.4f)
                            .pulseInterpolator(new BounceInterpolator())
                            .build();
                    LocationComponentActivationOptions locationComponentActivationOptions = LocationComponentActivationOptions
                            .builder(requireContext(), style)
                            .locationComponentOptions(locationComponentOptions)
                            .build();
                    locationComponent = mapboxMap.getLocationComponent();
                    locationComponent.activateLocationComponent(locationComponentActivationOptions);
                    locationComponent.setLocationComponentEnabled(true);
                    if (locationComponent.getLastKnownLocation() != null){
                        Double latitude = locationComponent.getLastKnownLocation().getLatitude();
                        Double longitude = locationComponent.getLastKnownLocation().getLongitude();
                        getDataLokasi(latitude,longitude);
                    } else {
                        loading(false);
                        Toast.makeText(requireContext(), "Silahkan hidupkan GPS dan restart aplikasi", Toast.LENGTH_SHORT).show();
                    }

                }
                else {
                    permissionsManager = new PermissionsManager(new PermissionsListener() {
                        @Override
                        public void onExplanationNeeded(List<String> permissionsToExplain) {
                            loading(false);
                            Toast.makeText(requireContext(), "Anda harus mengizinkan location permission untuk menggunakan aplikasi ini", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onPermissionResult(boolean granted) {
                            if (granted) {
                                mapboxMap.getStyle(new Style.OnStyleLoaded() {
                                    @Override
                                    public void onStyleLoaded(@NonNull Style style) {
                                        loading(false);
                                        getMyLocation();
                                    }
                                });
                            } else {
                                loading(false);
                                Toast.makeText(requireContext(), R.string.user_location_permission_not_granted, Toast.LENGTH_LONG).show();
                                getActivity().finish();
                            }
                        }
                    });
                    permissionsManager.requestLocationPermissions(requireActivity());

                }

            });
        });
    }

    private void setAdapter() {
        rvLokasi = binding.rvJarakTerdekat;
        rvLokasi.setLayoutManager(new LinearLayoutManager(getContext()));
        rvLokasi.setHasFixedSize(true);

        adapterLokasiTerdekat = new AdapterLokasiTerdekat(list, getContext(), new AdapterLokasiTerdekat.AdapterListener() {
            @Override
            public void onClick(DataItemTerdekat result) {
                Intent intent = new Intent(requireActivity(), DetailActivity.class);
                intent.putExtra("intent_nama_lokasi", result.getNamaLokasi());
                intent.putExtra("intent_alamat_lokasi", result.getAlamatLokasi());
                intent.putExtra("intent_deskripsi_lokasi", result.getDeskripsiLokasi());
                intent.putExtra("intent_lat_lokasi", result.getLatitude());
                intent.putExtra("intent_long_lokasi", result.getLongitude());
                intent.putExtra("intent_image", result.getFoto());
                startActivity( intent );
            }
        });
        rvLokasi.setAdapter(adapterLokasiTerdekat);
    }
    private void getDataLokasi(Double latitude, Double longitude) {
        if (latitude != null && longitude != null){
            Call<AllLokasiResponse> client = ApiClient.getApiService().getAllDataLokasi();
            client.enqueue(new Callback<AllLokasiResponse>() {
                @Override
                public void onResponse(@NonNull Call<AllLokasiResponse> call, Response<AllLokasiResponse> response) {
                    if (response.isSuccessful()) {
                        assert response.body() != null;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            ArrayList<DataItemTerdekat> resultList = new ArrayList<>();

                            response.body().getData().forEach(dataItem -> {

                                double desLat = dataItem.getLatitude() * PI / 180;
                                double desLong = dataItem.getLongitude() * PI / 180;
                                double myLang = latitude * PI / 180;
                                double myLo = longitude * PI / 180;
                                double finalLat = desLat - myLang;
                                double finalLong = desLong - myLo;
                                double a =
                                        sin(finalLat / 2) * sin(finalLat / 2) + cos(desLat) * cos(latitude)* cos(longitude) * sin(finalLong / 2   ) * sin(finalLong / 2);

                                double c = 2 * atan2(sqrt(a), sqrt(1 - a));
                                double d = 6371 * c;

                                resultList.add(new DataItemTerdekat(
                                        dataItem.getNamaLokasi(),
                                        dataItem.getAlamatLokasi(),
                                        dataItem.getFoto(),
                                        dataItem.getDeskripsiLokasi(),
                                        dataItem.getLatitude(),
                                        dataItem.getLongitude(),
                                        d
                                ));

                            });
                            Collections.sort(resultList, new Comparator<DataItemTerdekat>() {
                                @Override
                                public int compare(DataItemTerdekat dataItemTerdekat, DataItemTerdekat t1) {
                                    return Double.valueOf(dataItemTerdekat.getJarak()).compareTo(Double.valueOf(t1.getJarak()));
                                }
                            });
                            adapterLokasiTerdekat.setdata(resultList);
                            loading(false);
                        }

                    }
                }

                @Override
                public void onFailure(Call<AllLokasiResponse> call, Throwable t) {
                    loading(false);
                    Log.e("LokasiGagal", t.getMessage());
                }
            });

        } else {
            Toast.makeText(requireContext(), "Silahkan aktifkan GPS dan restart aplikasi", Toast.LENGTH_SHORT).show();
        }
 }
    private void loading(Boolean loading){
        if (loading){
            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            LayoutInflater inflater = requireActivity().getLayoutInflater();
            builder.setView(inflater.inflate(R.layout.custom_dialog, null ));
            builder.setCancelable(false);
            alertDialog = builder.create();
            alertDialog.show();
        } else{
            alertDialog.dismiss();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        binding.mapView.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        binding.mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        binding.mapView.onPause();
    }
    @Override
    public void onStop() {
        super.onStop();
        binding.mapView.onStop();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        binding.mapView.onSaveInstanceState(outState);
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        binding.mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        binding.mapView.onLowMemory();
    }
}