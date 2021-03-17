package com.example.weather7.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.view.Gravity;
import android.widget.CheckBox;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.Date;

import javax.inject.Inject;

public class ConnectionManager {
    private long delay_start_time ;
    private Context context;

    private ConnectivityManager cm;
    private NetworkInfo netInfo;

    @Inject
    public ConnectionManager(Context context){
        this.context=context;
    }

    public boolean networkEnable() {
        if (cm ==null) {
            cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        }
        netInfo = cm.getActiveNetworkInfo();

        return (netInfo != null && netInfo.isConnected());
    }
    public void showOfferSetting(Context context) {
        Date date = new Date();
        // проверить время последнего откладывания
        long current_time = date.getTime();
        if (current_time-delay_start_time<=1000*60*30) return;

        MaterialAlertDialogBuilder dialog = new MaterialAlertDialogBuilder(context);
        dialog.setCancelable(false);
        dialog.setTitle("Требуется соединеие");
        CheckBox check = new CheckBox(dialog.getContext());
        check.setText("Не показывать следующие 30 минут");
        dialog.setView(check);

        dialog.setMessage("Отсутствует подключение к сети Интернет.\n\nФункционал приложения ограничен");
        dialog.setNeutralButton("Продолжить", (dialog12, which) -> {
            if (check.isChecked()){
                delay_start_time=date.getTime();

            }
        });
        dialog.setPositiveButton("Настроить подключение", (dialog1, which) -> context.startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS)));
        dialog.show();
    }
}
