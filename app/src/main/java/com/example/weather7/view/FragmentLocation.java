package com.example.weather7.view;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.weather7.R;
import com.example.weather7.databinding.FragmentLocationBinding;
import com.example.weather7.utils.GeolocationManager;
import com.example.weather7.viewmodel.LocationViewModel;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.util.concurrent.Executor;

import javax.inject.Inject;

public class FragmentLocation extends Fragment {

    @Inject
    LocationViewModel locationViewModel;

    private final int LOCATION_ACCESS_REQUEST=1;
    private MutableLiveData<Location> mLocation;
    private FusedLocationProviderClient fusedLocationProviderClient;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        mLocation = new MutableLiveData<>();
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());

        locationViewModel = new LocationViewModel();
        FragmentLocationBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_location, container, false);
        binding.setViewModel(locationViewModel);

        mLocation.observe(getViewLifecycleOwner(), location -> {

        });

        getLocationAsync();

        return binding.getRoot();
    }

    private void getLocationAsync(){

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            // просим разрешение у пользователя
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    LOCATION_ACCESS_REQUEST);
        }

        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(getActivity(), location -> {
            if (location != null) {
                mLocation.postValue(location);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case GeolocationManager.LOCATION_ACCESS_REQUEST: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                {
                    fusedLocationProviderClient.getLastLocation().addOnSuccessListener(getActivity(), location -> {
                        if (location != null) {
                            mLocation.postValue(location);
                        }
                    });

                } else {
                    Toast.makeText(getContext(), "Разрешение отклонено. Информация недоступна", Toast.LENGTH_SHORT).show();
                }
                break;
            }
        }
    }
}