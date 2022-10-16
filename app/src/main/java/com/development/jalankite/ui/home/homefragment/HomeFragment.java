package com.development.jalankite.ui.home.homefragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.development.jalankite.R;
import com.development.jalankite.databinding.FragmentHomeBinding;
import com.development.jalankite.network.ApiClient;
import com.development.jalankite.preference.PrefManager;
import com.development.jalankite.ui.AdapterLokasi;
import com.development.jalankite.ui.home.AllLokasiResponse;
import com.development.jalankite.ui.home.DataItem;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class HomeFragment extends Fragment {

    FragmentHomeBinding binding;
    private RecyclerView rvLokasi;
    private ArrayList<DataItem> list = new ArrayList<>();
    private AdapterLokasi adapterLokasi;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        getDataSession();
        getDataLokasi();
        list = new ArrayList<>();
        rvLokasi = view.findViewById(R.id.rv_list);
        rvLokasi.setLayoutManager(new LinearLayoutManager(getContext()));
        rvLokasi.setHasFixedSize(true);

        adapterLokasi = new AdapterLokasi(list, getContext(), new AdapterLokasi.AdapterListener() {
            @Override
            public void onClick(DataItem result) {
                Toast.makeText(getContext(), result.getNamaLokasi(), Toast.LENGTH_SHORT);
            }
        });
        rvLokasi.setAdapter(adapterLokasi);

        return view;
    }



    private void getDataLokasi() {
        Call<AllLokasiResponse> client = ApiClient.getApiService().getAllDataLokasi();
        client.enqueue(new Callback<AllLokasiResponse>() {
            @Override
            public void onResponse(@NonNull Call<AllLokasiResponse> call, Response<AllLokasiResponse> response) {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    adapterLokasi.setdata(response.body().getData());
                }
            }

            @Override
            public void onFailure(Call<AllLokasiResponse> call, Throwable t) {
                Log.e("LokasiGagal", t.getMessage());
            }
        });
    }

    private void getDataSession() {
        PrefManager prefManager = new PrefManager(getActivity());
        HashMap<String, String> userDetail =  prefManager.getUser();
        String name =  userDetail.get(PrefManager.KEY_NAME);
        binding.textView2.setText("Selamat Datang " + " " + name);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}