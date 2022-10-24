package com.development.jalankite.ui.login;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.development.jalankite.R;
import com.development.jalankite.databinding.ActivityLoginBinding;
import com.development.jalankite.network.ApiClient;
import com.development.jalankite.preference.PrefManager;
import com.development.jalankite.ui.home.MainActivity;
import com.development.jalankite.ui.registrasi.RegistrasiActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    ActivityLoginBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        super.onCreate(savedInstanceState);
        setContentView(binding.getRoot());

        binding.tvRegistrasi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, RegistrasiActivity.class));
            }
        });

        binding.btnSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loading(true);
                if (binding.etEmail.getText().toString().isEmpty() ||
                binding.etPass.getText().toString().isEmpty()) {
                    loading(false);
                    Toast.makeText(LoginActivity.this, "Silahkan lengkapi data", Toast.LENGTH_SHORT).show();
                } else {
                    Call<LoginResponse> client = ApiClient.getApiService().geData(
                            binding.etEmail.getText().toString(),
                            binding.etPass.getText().toString());
                    client.enqueue(new Callback<LoginResponse>(){

                        @Override
                        public void onResponse(@NonNull Call<LoginResponse> call, @NonNull Response<LoginResponse> response) {
                            if (response.isSuccessful()){
                                if (response.body().isStatus()) {
                                    if (response.body().getData() != null) {
                                        PrefManager prefManager = new PrefManager(LoginActivity.this);
                                        prefManager.createLoginSession(
                                                response.body().getData().getName(),
                                                response.body().getData().getEmail());
                                        loading(false);
                                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                        finish();
                                    }
                                }else {

                                    loading(false);
                                    Toast.makeText(getBaseContext(),"gagal login", Toast.LENGTH_SHORT).show();
                                }

                            }else {
                                if (response.body() != null) {
                                    loading(false);
                                    Log.e(TAG, "onFailure: " + response.body().getMsg());
                                    Toast.makeText(getBaseContext(),"gagal", Toast.LENGTH_SHORT).show();

                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<LoginResponse> call, Throwable t) {
                            loading(false);
                            Log.e(TAG, "onFailure: " + t.getMessage());
                        }
                    });
                }

            }
        });

    }
    private void loading(Boolean loading) {
        if(loading) {
            binding.progressBarLogin.setVisibility(View.VISIBLE);
            binding.btnSignin.setEnabled(false);
        } else {
            binding.progressBarLogin.setVisibility(View.GONE);
            binding.btnSignin.setEnabled(true);

        }
    }

}