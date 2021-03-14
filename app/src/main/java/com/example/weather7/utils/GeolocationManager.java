package com.example.weather7.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;

import com.example.weather7.view.FragmentLocation;
import com.example.weather7.view.notifications.FragmentNotifications;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Executor;

public class GeolocationManager{

    public final static int LOCATION_ACCESS_REQUEST=1;

    private Geocoder geocoder;

    public GeolocationManager(Geocoder geocoder) {
        this.geocoder = geocoder;

    }

    public String[] getCoordinateByLocationName(String locationName) throws IOException {
        String[] coordinates = new String[2];

        List<Address> adr = null;
        adr = geocoder.getFromLocationName(locationName, 1);
        if (adr.size() > 0) {
            coordinates[0] = String.valueOf(adr.get(0).getLatitude());
            coordinates[1] = String.valueOf(adr.get(0).getLongitude());
        }
        return coordinates;
    }

}
