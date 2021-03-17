package com.example.weather7.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.provider.Settings;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;

import com.example.weather7.view.FragmentLocation;
import com.example.weather7.view.notifications.FragmentNotifications;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executor;

public class GeolocationManager{

    public final static int LOCATION_ACCESS_REQUEST=1;

    private Geocoder geocoder;
    private Context context;

    private long delay_start_time ;
    private LocationManager locationManager;
    private Date date;
    private final String[] locationPermissions;

    public GeolocationManager(Geocoder geocoder, Context context) {
        this.geocoder = geocoder;
        this.context=context;

        this.locationPermissions=new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
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
    public String getCityNameByLocation(Location location) throws IOException {
        if (location==null) return "";

        String cityName = "";

        List<Address> adr = null;
        adr = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
        if (adr.size() > 0) {
            cityName = String.valueOf(adr.get(0).getLocality());
        }
        return cityName;
    }

    public boolean geolocationPermissionEnable(Activity activity){
        return ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }
    public boolean geolocationEnable(){
        if (locationManager==null) {
            locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        }
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            return false;
        }
        return true;
    }
    public void openGoogleMaps(Activity activity) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Intent mapIntent = new Intent(Intent.ACTION_VIEW);
        mapIntent.setPackage("com.google.android.apps.maps");
        activity.startActivity(mapIntent);
    }


    public void showOfferSetting(Context context){
        if (date==null){
            date = new Date();
        }
        // проверить время последнего откладывания
        long current_time = date.getTime();
        if (current_time-delay_start_time<=1000*60*30) return;

        MaterialAlertDialogBuilder dialog = new MaterialAlertDialogBuilder(context);
        dialog.setCancelable(false);
        dialog.setTitle("Требуется геолокация");
        CheckBox check = new CheckBox(dialog.getContext());
        check.setText("Не показывать следующие 30 минут");
        dialog.setView(check);

        dialog.setMessage("Геолокационные службы отключены.\n\nНевозможно получить местоположение");
        dialog.setNeutralButton("Продолжить", (dialog1, which) -> {
            if (check.isChecked()){
                delay_start_time=date.getTime();

            }
        });
        dialog.setPositiveButton("Настроить геолокацию", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                context.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            }
        });
        dialog.show();
    }

    public String[] getLocationPermissions() {
        return locationPermissions;
    }
}
