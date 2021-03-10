package com.example.weather7.view;

import android.app.AlarmManager;
import android.content.Context;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.example.weather7.R;
import com.example.weather7.databinding.MainActivityBinding;
import com.example.weather7.utils.ConnectionManager;
import com.example.weather7.view.cities.FragmentCities;
import com.example.weather7.view.notifications.FragmentNotifications;
import com.example.weather7.viewmodel.MainActivityViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import me.ibrahimsn.particle.ParticleView;

public class MainActivity extends AppCompatActivity implements FragmentNotifications.AlarmManagerGetter {

    private MainActivityViewModel mainViewModel;
    private MainActivityBinding binding;

    AlarmManager alarmManager;

    private FragmentHome fragmentHome;
    private FragmentCities fragmentCities;
    private FragmentNotifications fragmentNotifications;
    private Fragment activeFragment;

    private ParticleView particleView;

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

        particleView = findViewById(R.id.particleView);
    }

    private void createFragments(){
        fragmentHome = new FragmentHome();
        fragmentCities = new FragmentCities();
        fragmentNotifications = new FragmentNotifications();
        activeFragment=fragmentHome;
    }
    private void addFragments(){
        getSupportFragmentManager().beginTransaction()
                .add(R.id.nav_host_fragment, fragmentHome, "fragmentHome")
                .add(R.id.nav_host_fragment, fragmentCities, "fragmentCities")
                .add(R.id.nav_host_fragment, fragmentNotifications, "fragmentNotifications")
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
                        if (activeFragment==fragmentHome) return false;

                        getSupportFragmentManager().beginTransaction()
                                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                                .hide(activeFragment)
                                .show(fragmentHome).commit();
                        activeFragment=fragmentHome;
                        break;
                    case R.id.navigation_cities:
                        if (activeFragment==fragmentCities) return false;

                        getSupportFragmentManager().beginTransaction()
                                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                                .hide(activeFragment)
                                .show(fragmentCities).commit();
                        activeFragment=fragmentCities;
                        break;
                    case R.id.navigation_notifications:
                        if (activeFragment==fragmentNotifications) return false;

                        getSupportFragmentManager().beginTransaction()
                                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                                .hide(activeFragment)
                                .show(fragmentNotifications).commit();
                        activeFragment=fragmentNotifications;
                        break;
                }
                return true;
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        particleView.resume();
    }

    @Override
    public void onPause() {
        super.onPause();
        particleView.pause();
    }
    
    @Override
    public AlarmManager getAlarmManager() {
        return alarmManager;
    }
}