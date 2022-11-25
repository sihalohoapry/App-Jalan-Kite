package com.development.jalankite.ui.home.homefragment;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import com.development.jalankite.R;
import com.development.jalankite.databinding.FragmentHomeBinding;
import com.development.jalankite.network.ApiClient;
import com.development.jalankite.preference.PrefManager;
import com.development.jalankite.ui.AdapterLokasi;
import com.development.jalankite.ui.detail.DetailActivity;
import com.development.jalankite.ui.home.AllLokasiResponse;
import com.development.jalankite.ui.home.DataItem;
import com.development.jalankite.ui.home.MainActivity;

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
    private AlertDialog alertDialog;
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
        getLokasiByName("");
        list = new ArrayList<>();
        rvLokasi = view.findViewById(R.id.rv_list);
        rvLokasi.setLayoutManager(new LinearLayoutManager(getContext()));
        rvLokasi.setHasFixedSize(true);
        binding.etSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                getLokasiByName(s);
                return true;
            }
        });

        adapterLokasi = new AdapterLokasi(list, getContext(), new AdapterLokasi.AdapterListener() {
            @Override
            public void onClick(DataItem result) {
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
        rvLokasi.setAdapter(adapterLokasi);

        return view;
    }

    private void getLokasiByName(String s) {
        loading(true);
        Call<AllLokasiResponse> client = ApiClient.getApiService().responseLokasiByName(s);
        client.enqueue(new Callback<AllLokasiResponse>() {
            @Override
            public void onResponse(Call<AllLokasiResponse> call, Response<AllLokasiResponse> response) {
                if (response.isSuccessful()) {
                    if (response.body().getData() == null){
                        loading(false);
                    }else{
                        adapterLokasi.setdata(response.body().getData());
                        loading(false);
                    }

                }
            }

            @Override
            public void onFailure(Call<AllLokasiResponse> call, Throwable t) {
                Log.e("LokasiGagal", t.getMessage());
                loading(false);
            }
        });
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