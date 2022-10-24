package com.development.jalankite.network;


import com.development.jalankite.ui.home.AllLokasiResponse;
import com.development.jalankite.ui.login.LoginResponse;
import com.development.jalankite.ui.registrasi.RegisterResponse;
import com.development.jalankite.ui.registrasi.RegistrasiActivity;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface ApiEndpoint {
    @GET("all-lokasi")
    Call<AllLokasiResponse> getAllDataLokasi();

    @FormUrlEncoded
    @POST("login")
    Call<LoginResponse> geData(
            @Field("email") String email,
            @Field("password") String password
    );

    @FormUrlEncoded
    @POST("registrasi")
    Call<RegisterResponse> responseRegistrasi(
            @Field("name") String name,
            @Field("email") String email,
            @Field("password") String password
    );

    @FormUrlEncoded
    @POST("lokasi-by-name")
    Call<AllLokasiResponse> responseLokasiByName(
            @Field("nama_lokasi") String name
    );
}
