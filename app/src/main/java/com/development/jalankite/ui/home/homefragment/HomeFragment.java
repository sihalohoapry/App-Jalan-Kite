package com.development.jalankite.ui.home.homefragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.development.jalankite.R;
import com.development.jalankite.databinding.FragmentHomeBinding;
import com.development.jalankite.preference.PrefManager;

import java.util.HashMap;


public class HomeFragment extends Fragment {

    FragmentHomeBinding binding;

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



        return view;
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