package com.example.weather7.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.weather7.R;
import com.example.weather7.viewmodel.MyLocationViewModel;

public class FragmentHome extends Fragment {

    private MyLocationViewModel homeViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel = new ViewModelProvider(this).get(MyLocationViewModel.class);
        View root = inflater.inflate(R.layout.fragment_my_location, container, false);

        return root;
    }
}