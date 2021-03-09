package com.example.weather7.view;

import android.app.AlarmManager;
import android.content.Context;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Switch;

import com.example.weather7.R;
import com.example.weather7.databinding.MainActivityBinding;
import com.example.weather7.model.cities.City;
import com.example.weather7.utils.ConnectionManager;
import com.example.weather7.view.cities.FragmentCities;
import com.example.weather7.view.notifications.FragmentNotifications;
import com.example.weather7.viewmodel.MainActivityViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import java.util.LinkedList;

public class MainActivity extends AppCompatActivity implements FragmentNotifications.AlarmManagerGetter {

    private MainActivityViewModel mainViewModel;
    private MainActivityBinding binding;

    AlarmManager alarmManager;

    private FragmentHome fragmentHome;
    private FragmentCities fragmentCities;
    private FragmentNotifications fragmentNotifications;
    private Fragment activeFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.main_activity);
        mainViewModel = new MainActivityViewModel();
        binding.setViewModel(mainViewModel);

        createFragments();
        addFragments();
        setBottomNavigationListener();

        alarmManager  = (AlarmManager)getSystemService(
                Context.ALARM_SERVICE);
        ConnectionManager.setContext(this);


    }

    private void createFragments(){
        fragmentHome = new FragmentHome();
        fragmentCities = new FragmentCities();
        fragmentNotifications = new FragmentNotifications();
        activeFragment=fragmentHome;
    }
    private void addFragments(){
        getSupportFragmentManager().beginTransaction()
                .add(R.id.container, fragmentHome, "fragmentHome")
                .add(R.id.container, fragmentCities, "fragmentCities")
                .add(R.id.container, fragmentNotifications, "fragmentNotifications")
                .hide(fragmentCities)
                .hide(fragmentNotifications)
                .commit();
    }
    private void setBottomNavigationListener(){
        binding.navView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId()){
                    case R.id.navigation_home:
                        getSupportFragmentManager().beginTransaction().hide(activeFragment)
                                .show(fragmentHome).commit();
                        activeFragment=fragmentHome;
                        break;
                    case R.id.navigation_cities:
                        getSupportFragmentManager().beginTransaction().hide(activeFragment)
                                .show(fragmentCities).commit();
                        activeFragment=fragmentCities;
                        break;
                    case R.id.navigation_notifications:
                        getSupportFragmentManager().beginTransaction().hide(activeFragment)
                                .show(fragmentNotifications).commit();
                        activeFragment=fragmentNotifications;
                        break;
                }
                return true;
            }
        });
    }

    @Override
    public AlarmManager getAlarmManager() {
        return alarmManager;
    }
}