package com.development.jalankite.ui.detail;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;

import com.bumptech.glide.Glide;
import com.development.jalankite.R;
import com.development.jalankite.databinding.ActivityDetailBinding;

public class DetailActivity extends AppCompatActivity {

    ActivityDetailBinding binding;
    double latitude;
    double longitude;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.tvNamaDetail.setText(getIntent().getStringExtra("intent_nama_lokasi"));
        binding.tvAlamatDetail.setText(getIntent().getStringExtra("intent_alamat_lokasi"));
        binding.tvSejarah.setText(getIntent().getStringExtra("intent_deskripsi_lokasi"));
        longitude = getIntent().getDoubleExtra("intent_long_lokasi", 0);
        latitude = getIntent().getDoubleExtra("intent_lat_lokasi", 0);
        Glide.with(this)
                .load("http://192.168.0.190:8000/storage/"+ getIntent().getStringExtra("intent_image") )
                .placeholder(R.drawable.broken_image)
                .error(R.drawable.broken_image)
                .into(binding.ivFoto);

        binding.ivBack.setOnClickListener(item->{
            super.onBackPressed();
        });

        binding.buttonMap.setOnClickListener(view -> {
            Intent intent = new Intent(this, MapsActivity.class);
            intent.putExtra("intent_nama_lokasi", getIntent().getStringExtra("intent_nama_lokasi"));
            intent.putExtra("intent_lat_lokasi", latitude);
            intent.putExtra("intent_long_lokasi", longitude);
            startActivity(intent);
        });
    }
}