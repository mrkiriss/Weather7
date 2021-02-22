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
import androidx.lifecycle.MutableLiveData;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements FragmentCities.onCitiesFragmentListener {

    private MainActivityViewModel mainViewModel;
    private MainActivityBinding binding;

    private MutableLiveData<ArrayList<City>> mutable_cities = new MutableLiveData<>();

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.main_activity);
        binding.setViewModel(new MainActivityViewModel());
        BottomNavigationView navView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_cities, R.id.navigation_notifications)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);
    }

    @Override
    public void requestUpdateCities(CitiesViewModel citiesViewModel) {
        ArrayList<City> result = new ArrayList<>();

        //
        if (mutable_cities.getValue()==null){
            // написать функци загрузки городов из базы данных

            // запоминание ссылки на объект livedata
            mutable_cities=citiesViewModel.getMutableCities();
            // внести данные

        }else{
            citiesViewModel.getMutableCities().setValue(mutable_cities.getValue());
        }
    }
}