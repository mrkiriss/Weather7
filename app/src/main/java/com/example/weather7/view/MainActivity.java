package com.example.weather7.view;

import android.app.AlarmManager;
import android.content.Context;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

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

    private FragmentLocation fragmentLocation;
    private FragmentCities fragmentCities;
    private FragmentNotifications fragmentNotifications;
    private Fragment activeFragment;

    private NavController navController;
    private NavOptions options;

    private ParticleView particleView;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.main_activity);
        mainViewModel = new MainActivityViewModel();
        binding.setViewModel(mainViewModel);

        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        options = new NavOptions.Builder()
                .setLaunchSingleTop(true)
                .setEnterAnim(android.R.anim.fade_in)
                .setExitAnim(android.R.anim.fade_out)
                .setPopEnterAnim(android.R.anim.fade_in)
                .setPopExitAnim(android.R.anim.fade_out)
                .setPopUpTo(navController.getGraph().getStartDestination(), false)
                .build();

        setBottomNavigationListener();

        alarmManager  = (AlarmManager)getSystemService(
                Context.ALARM_SERVICE);
        ConnectionManager.setContext(this);

        particleView = findViewById(R.id.particleView);
    }
    private void setBottomNavigationListener(){
        binding.navView.setOnNavigationItemSelectedListener(item -> {
            switch(item.getItemId()){
                case R.id.navigation_location:
                    navController.navigate(R.id.navigation_location, null, options);
                    break;
                case R.id.navigation_cities:
                    navController.navigate(R.id.navigation_cities, null, options);
                    break;
                case R.id.navigation_notifications:
                    navController.navigate(R.id.navigation_notifications, null, options);
                    break;
            }
            return true;
        });

        binding.navView.setOnNavigationItemReselectedListener(item -> {});
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