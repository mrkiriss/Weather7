package com.example.weather7.view;

import android.os.Bundle;
import android.view.MenuItem;

import com.example.weather7.R;
import com.example.weather7.databinding.MainActivityBinding;
import com.example.weather7.model.City;
import com.example.weather7.viewmodel.CitiesViewModel;
import com.example.weather7.viewmodel.MainActivityViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import java.util.ArrayList;
import java.util.LinkedList;

public class MainActivity extends AppCompatActivity implements FragmentCities.onCitiesFragmentListener {

    private MainActivityViewModel mainViewModel;
    private MainActivityBinding binding;

    private LinkedList<City> main_cities = new LinkedList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.main_activity);
        binding.setViewModel(new MainActivityViewModel());
        BottomNavigationView navView = binding.navView;


        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_cities, R.id.navigation_notifications)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);
    }

    @Override
    public LinkedList<City> getLastCities() {
        return main_cities;
    }
    @Override
    public void bindActuallyCities(MutableLiveData<LinkedList<City>> live_cities) {
        live_cities.observe(this, new Observer<LinkedList<City>>() {
            @Override
            public void onChanged(LinkedList<City> cities) {
                main_cities = cities;
            }
        });
    }
}