package com.example.weather7.view;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.weather7.R;
import com.example.weather7.databinding.FragmentLocationBinding;
import com.example.weather7.di.App;
import com.example.weather7.model.base.City;
import com.example.weather7.repository.location.LocationRepository;
import com.example.weather7.utils.ConnectionManager;
import com.example.weather7.utils.GeolocationManager;
import com.example.weather7.view.cities.adapters.DaysAdapter;
import com.example.weather7.viewmodel.LocationViewModel;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import javax.inject.Inject;

public class FragmentLocation extends Fragment {

    @Inject
    LocationViewModel locationViewModel;
    @Inject
    GeolocationManager geolocationManager;
    @Inject
    ConnectionManager connectionManager;
    FragmentLocationBinding binding;

    private final int LOCATION_ACCESS_REQUEST = 1;
    private MutableLiveData<Location> mLocation;
    private FusedLocationProviderClient fusedLocationProviderClient;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        App.getInstance().getComponentManager().getFLocationSubcomponent().inject(this);

        mLocation = new MutableLiveData<>();
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_location, container, false);
        binding.setViewModel(locationViewModel);

        initObservers();
        getLocation();

        return binding.getRoot();
    }

    private void getLocation() {

        if (!geolocationManager.geolocationPermissionEnable(getActivity())) {
            // просим разрешение у пользователя
            requestPermissions(geolocationManager.getLocationPermissions(), LOCATION_ACCESS_REQUEST);
        }

        if (!geolocationManager.geolocationEnable()) {
            geolocationManager.showOfferSetting(getContext());
        }

        setGetLastLocationListener();
    }

    private void initObservers(){
        // получение координат
        mLocation.observe(getViewLifecycleOwner(), location -> {
            locationViewModel.fillCityContent(location);
        });
         // уведомление, о необходимости наличия интернет-соединения
        locationViewModel.getNetworkConnection().observe(getViewLifecycleOwner(), connection->{
            if (connection) return;
            connectionManager.showOfferSetting(getContext());
        });
        // подписываемся на получение текста для Toast
        locationViewModel.getToastContent().observe(getViewLifecycleOwner(), content->Toast.makeText(getContext(), content, Toast.LENGTH_SHORT));
        // подписываемся на изменение информации шапки города
        locationViewModel.getAddCityRequest().observe(getViewLifecycleOwner(), city -> {
            locationViewModel.setCityName(city.getName());
            locationViewModel.setDescription(city.getCurrent_description());
            String[] temp=city.getCurrent_temp().split("/");
            String temperature = "Температура "+temp[0]+"°C\n"+
                    "Ощущается как "+temp[1];
            locationViewModel.setTemperature(temperature);
            locationViewModel.setIcon(city.getCurrent_icon());
        });
        // подписываемся на изменение адаптера дней
        locationViewModel.getAddDaysAdapterRequest().observe(getViewLifecycleOwner(), daysAdapter -> {
            binding.daysRecyclerView.setAdapter(daysAdapter);
            binding.daysRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            Log.println(Log.INFO, "fragmentLocation", "location days adapter was set");
        });
        // подписываемся на запрос на обновление координат и, в связи с этим, всего контента экрана
        locationViewModel.getRefreshContentRequest().observe(getViewLifecycleOwner(), integer -> getLocation());

    }
    @SuppressLint("MissingPermission")
    private void setGetLastLocationListener() {
        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(requireActivity(), location -> {
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
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    setGetLastLocationListener();
                } else {
                    Toast.makeText(getContext(), "Информация недоступна", Toast.LENGTH_SHORT).show();
                    mLocation.postValue(null);
                }
                break;
            }
        }
    }
}