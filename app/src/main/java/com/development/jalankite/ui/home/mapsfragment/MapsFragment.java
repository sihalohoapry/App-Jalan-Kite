package com.development.jalankite.ui.home.mapsfragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.development.jalankite.R;
import com.development.jalankite.databinding.FragmentMapsBinding;
import com.development.jalankite.network.ApiClient;
import com.development.jalankite.ui.detail.DetailActivity;
import com.development.jalankite.ui.home.AllLokasiResponse;
import com.development.jalankite.ui.home.DataItem;
import com.google.gson.Gson;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.geometry.LatLngBounds;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.plugins.annotation.SymbolManager;
import com.mapbox.mapboxsdk.plugins.annotation.SymbolOptions;

import java.util.ArrayList;
import java.util.function.Consumer;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapsFragment extends Fragment {


    FragmentMapsBinding binding;
    private MapboxMap mapboxMapFrag;
    private ArrayList<DataItem> dataLokasi = new ArrayList<DataItem>();
    private static final String ICON_ID = "ICON_ID";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Mapbox.getInstance(requireContext(), getString(R.string.mapbox_access_token));
        binding = FragmentMapsBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        binding.mapView.onCreate(savedInstanceState);
        binding.mapView.getMapAsync(mapboxMap1 -> {
            this.mapboxMapFrag = mapboxMap1;
            getAllLocation();
        });
        return view;
    }
        private void getAllLocation() {
        Call<AllLokasiResponse> client = ApiClient.getApiService().getAllDataLokasi();
        client.enqueue(new Callback<AllLokasiResponse>() {
            @Override
            public void onResponse(@NonNull Call<AllLokasiResponse> call, Response<AllLokasiResponse> response) {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    if (response.body().getData() != null) {
                        mapboxMapFrag.setStyle(Style.MAPBOX_STREETS, new Style.OnStyleLoaded() {
                            @SuppressLint("NewApi")
                            @Override
                            public void onStyleLoaded(@NonNull Style style) {
                                style.addImage(ICON_ID, BitmapFactory.decodeResource(getResources(), com.mapbox.mapboxsdk.R.drawable.mapbox_marker_icon_default));
                                LatLngBounds.Builder latLngBoundsBuilder = new LatLngBounds.Builder();
                                SymbolManager symbolManager = new SymbolManager(binding.mapView, mapboxMapFrag, style);
                                symbolManager.setIconAllowOverlap(true);
                                ArrayList<SymbolOptions> option = new ArrayList<>();
                                response.body().getData().forEach(dataItem -> {
                                    latLngBoundsBuilder.include(new LatLng(dataItem.getLatitude(),dataItem.getLongitude()));
                                    option.add(new SymbolOptions()
                                            .withLatLng(new LatLng(dataItem.getLatitude(),dataItem.getLongitude()))
                                            .withIconImage(ICON_ID)
                                            .withTextField(dataItem.getNamaLokasi())
                                            .withData(new Gson().toJsonTree(dataItem))
                                    );
                                });
                                symbolManager.create(option);
                                LatLngBounds latLngBounds = latLngBoundsBuilder.build();
                                mapboxMapFrag.easeCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, 50), 5000);
                                symbolManager.addClickListener(symbol -> {
                                    DataItem data = new Gson().fromJson(symbol.getData(), DataItem.class);
                                    Intent intent = new Intent(requireActivity(), DetailActivity.class);
                                    intent.putExtra("intent_nama_lokasi", data.getNamaLokasi());
                                    intent.putExtra("intent_alamat_lokasi", data.getAlamatLokasi());
                                    intent.putExtra("intent_deskripsi_lokasi", data.getDeskripsiLokasi());
                                    intent.putExtra("intent_lat_lokasi", data.getLatitude());
                                    intent.putExtra("intent_long_lokasi", data.getLongitude());
                                    intent.putExtra("intent_image", data.getFoto());
                                    startActivity( intent );

                                });
                            }
                        });

                    } else {
                        Toast.makeText(requireContext(), "Data Lokasi Tidak Ada", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<AllLokasiResponse> call, Throwable t) {
                Toast.makeText(requireContext(), "Gagal menampilkan lokasi", Toast.LENGTH_SHORT).show();
            }
        });
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