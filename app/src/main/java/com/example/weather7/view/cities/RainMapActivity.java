package com.example.weather7.view.cities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;

import com.example.weather7.R;
import com.example.weather7.api.RainMapApi;
import com.example.weather7.databinding.RainMapActivityBinding;
import com.example.weather7.repository.cities.RainMapRepository;
import com.example.weather7.viewmodel.cities.RainMapViewModel;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.android.gms.maps.model.TileProvider;

import java.util.HashMap;

public class RainMapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private MapView mapView;
    private GoogleMap map;
    private TileOverlay rainOverlay;

    private RainMapViewModel rainMapViewModel;
    private RainMapActivityBinding binding;

    private LatLng city_coordinate;
    private final int startZoomLevel=9;
    private String city_name;

    private static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // создаём экземпляр api
        RainMapApi rainMapApi = new RainMapApi();
        // преобразуем координаты из прошлой активити
        getCoordinate();

        rainMapViewModel = new RainMapViewModel(new RainMapRepository(rainMapApi));
        binding = DataBindingUtil.setContentView(this, R.layout.rain_map_activity);
        binding.setViewModel(rainMapViewModel);

        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
        }
        mapView = binding.map;
        mapView.onCreate(mapViewBundle);
        mapView.getMapAsync(this);

        // подписываемся на обновление запроса на добавление КОНТЕЙНЕРА ОШИБКИ
        rainMapViewModel.getError().observe(this, content -> Toast.makeText(this, content, Toast.LENGTH_SHORT).show());
        // подписываемся на обновление состояния загрузки
        rainMapViewModel.getLoading().observe(this, loading -> rainMapViewModel.setProgress_loading(loading));
        // подписываемся на обновление основного пакета данных карты
        rainMapViewModel.getMapData().observe(this, data -> {
            // создаём seekbar по полученному временному промежутку
            setupSeekBar(data);
            // устанавливаем начальное значение
            rainMapViewModel.setProgress_seekbar(0);

        });
        // подписываемся на обновление за TileProvider
        rainMapViewModel.getSelectedProvider().observe(this, tileProvider -> {
            if (map==null) return;
            if (rainOverlay!=null) rainOverlay.remove();
            rainOverlay = map.addTileOverlay(new TileOverlayOptions().tileProvider(tileProvider).transparency(0.4f));
        });

    }

    private void setupSeekBar(HashMap<Integer, HashMap<String, TileProvider>> data){
        // количество временных точек
        binding.seekBar.setMax(data.size()-1);
        // создание указателей на время
        for (int i=0;i<data.size();i++){
            TextView text_time = new TextView(this);

            for (String time : data.get(i).keySet()) {
                text_time.setText(time);
            }

            text_time.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, 1));
            text_time.setTextSize(18);

            binding.timeContainer.addView(text_time, i);
        }

    }

    private void getCoordinate(){
        Intent i = getIntent();
        city_coordinate=new LatLng(Double.parseDouble(i.getStringExtra("lat")), Double.parseDouble(i.getStringExtra("lon")));
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Bundle mapViewBundle = outState.getBundle(MAPVIEW_BUNDLE_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAPVIEW_BUNDLE_KEY, mapViewBundle);
        }

        mapView.onSaveInstanceState(mapViewBundle);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        map.animateCamera(CameraUpdateFactory.newLatLngZoom(city_coordinate, startZoomLevel));
    }

    @Override
    protected void onPause() {
        mapView.onPause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        mapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

}