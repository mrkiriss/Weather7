package com.example.weather7.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.weather7.R;
import com.example.weather7.databinding.FragmentLocationBinding;
import com.example.weather7.viewmodel.LocationViewModel;

public class FragmentLocation extends Fragment {

    private LocationViewModel locationViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        locationViewModel = new LocationViewModel();
        FragmentLocationBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_location, container, false);
        binding.setViewModel(locationViewModel);

        return binding.getRoot();
    }
}