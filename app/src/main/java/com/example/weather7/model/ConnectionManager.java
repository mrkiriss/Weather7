package com.example.weather7.model;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.widget.CheckBox;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.util.Date;

public class ConnectionManager {
    private static long delay_start_time;
    private static Context context;
    public static boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();

        return (netInfo != null && netInfo.isConnected());
    }
    public static void showOfferSetting(Context context) {
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

        dialog.setMessage("Отсутствует подключение к сети Интернет.\nФункционал приложения ограничен");
        dialog.setNeutralButton("Продолжить", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (check.isChecked()){
                    delay_start_time=date.getTime();

                }
            }
        });
        dialog.setPositiveButton("Настроить подключение", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                context.startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
            }
        });
        dialog.show();
    }

    public static void setContext(Context context) {
        ConnectionManager.context=context;
    }
}
