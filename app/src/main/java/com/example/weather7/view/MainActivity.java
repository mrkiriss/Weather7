package com.example.weather7.view;

import android.app.AlarmManager;
import android.content.Context;
import android.os.Bundle;

import com.example.weather7.R;
import com.example.weather7.databinding.MainActivityBinding;
import com.example.weather7.model.cities.City;
import com.example.weather7.utils.ConnectionManager;
import com.example.weather7.viewmodel.MainActivityViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import java.util.LinkedList;

public class MainActivity extends AppCompatActivity implements FragmentNotifications.AlarmManagerGetter {

    private MainActivityViewModel mainViewModel;
    private MainActivityBinding binding;

    AlarmManager alarmManager;

    private LinkedList<City> main_cities = new LinkedList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        alarmManager  = (AlarmManager)getSystemService(
                Context.ALARM_SERVICE);

        binding = DataBindingUtil.setContentView(this, R.layout.main_activity);
        mainViewModel = new MainActivityViewModel();
        binding.setViewModel(mainViewModel);
        BottomNavigationView navView = binding.navView;

        ConnectionManager.setContext(this);

        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_cities, R.id.navigation_notifications)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

    }

    @Override
    public AlarmManager getAlarmManager() {
        return alarmManager;
    }
}