package com.development.jalankite.ui.registrasi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.development.jalankite.R;
import com.development.jalankite.databinding.ActivityRegistrasiBinding;
import com.development.jalankite.network.ApiClient;
import com.development.jalankite.ui.login.LoginActivity;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegistrasiActivity extends AppCompatActivity {

    ActivityRegistrasiBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegistrasiBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.btnRigister.setOnClickListener(view -> {
            registrasi();
        });


    }
    private void registrasi() {
        loading(true);
        if (binding.etName.getText().toString().isEmpty() || binding.etEmail.getText().toString().isEmpty()
            || binding.etPass.getText().toString().isEmpty()
        ){
            loading(false);
            Toast.makeText(RegistrasiActivity.this, "Silahkan lengkapi data", Toast.LENGTH_SHORT).show();
        } else {
            Call<RegisterResponse> clinet = ApiClient.getApiService().responseRegistrasi(
                    Objects.requireNonNull(binding.etName.getText()).toString().trim(),
                    Objects.requireNonNull(binding.etEmail.getText()).toString().trim(),
                    Objects.requireNonNull(binding.etPass.getText()).toString().trim()
            );
            clinet.enqueue(new Callback<RegisterResponse>() {
                @Override
                public void onResponse(@NonNull Call<RegisterResponse> call, @NonNull Response<RegisterResponse> response) {
                    if (response.isSuccessful()){
                        Toast.makeText(RegistrasiActivity.this, response.body().getMsg().toString(), Toast.LENGTH_SHORT).show();
                        loading(false);
                        startActivity(new Intent(RegistrasiActivity.this, LoginActivity.class));
                        finish();
                    }
                    if (response.code() == 411) {
                        loading(false);
                        Toast.makeText(RegistrasiActivity.this, "Email sudah dipakai, silahkan pakai email lain.", Toast.LENGTH_LONG).show();

                    }
                }

                @Override
                public void onFailure(@NonNull Call<RegisterResponse> call, Throwable t) {
                    loading(false);
                    Toast.makeText(RegistrasiActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }


    }


    private void loading(Boolean loading) {
        if(loading) {
            binding.progressBar.setVisibility(View.VISIBLE);
            binding.btnRigister.setEnabled(false);
        } else {
            binding.progressBar.setVisibility(View.GONE);
            binding.btnRigister.setEnabled(true);

        }
    }


}