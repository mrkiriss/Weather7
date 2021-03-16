package com.example.weather7.view;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;

import com.example.weather7.R;
import com.example.weather7.databinding.MainActivityBinding;
import com.example.weather7.viewmodel.MainActivityViewModel;

import java.io.File;

import me.ibrahimsn.particle.ParticleView;

public class MainActivity extends AppCompatActivity {

    private MainActivityViewModel mainViewModel;
    private MainActivityBinding binding;

    private NavController navController;
    private NavOptions options;

    private ParticleView particleView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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

        particleView = findViewById(R.id.particleView);
    }

    private void setBottomNavigationListener() {

        binding.navView.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
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

        binding.navView.setOnNavigationItemReselectedListener(item -> {
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

}