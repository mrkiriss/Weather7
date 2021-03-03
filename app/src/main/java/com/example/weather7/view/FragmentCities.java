package com.example.weather7.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.example.weather7.api.CitiesApi;
import com.example.weather7.api.RainMapApi;
import com.example.weather7.api.WeatherApi;
import com.example.weather7.model.AutoEnteredCity;
import com.example.weather7.model.RepositoryRequest;
import com.example.weather7.repository.CityRepository;
import com.example.weather7.utils.ConnectionManager;
import com.example.weather7.view.adapters.CitiesAdapter;
import com.example.weather7.R;
import com.example.weather7.databinding.FragmentCitiesBinding;
import com.example.weather7.model.City;
import com.example.weather7.database.AppDatabase;
import com.example.weather7.view.adapters.DaysAdapter;
import com.example.weather7.viewmodel.cities.CitiesViewModel;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class FragmentCities extends Fragment{

    private CitiesViewModel citiesViewModel;
    private FragmentCitiesBinding binding;
    private AppDatabase db;
    private WeatherApi api;
    private CitiesAdapter cities_adapter;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_cities, container, false);

        // создание экземпляра ДБ
        db = Room.databaseBuilder(getContext(),
                AppDatabase.class, "database")
                .allowMainThreadQueries()
                .fallbackToDestructiveMigration()
                .build();

        // создание экземпляра api дождя
        RainMapApi rain_api = new RainMapApi(getContext());
        // создание экземпляра Погодного api
        api = new WeatherApi(getContext());
        // создание экземпляра api названий городов для поиска
        CitiesApi citiesApi=new CitiesApi();
        // создание ViewModel
        citiesViewModel = new CitiesViewModel(new CityRepository(db, api, rain_api, citiesApi));
        binding.setViewModel(citiesViewModel);

        setupCitiesRecyclerView(binding.citiesRecyclerView);
        cities_adapter =
                (CitiesAdapter) binding.citiesRecyclerView.getAdapter();

        // подписываемся на обновление ГОРОДОВ
        citiesViewModel.getCities().observe(getViewLifecycleOwner(), new Observer<LinkedList<City>>() {
            @Override
            public void onChanged(LinkedList<City> cities) {
                onCitiesChanged(cities);
            }
        });
        // подписываемся на обновление запроса на добавление ШАПКИ ГОРОДА
        citiesViewModel.getAddCityHeadRequest().observe(getViewLifecycleOwner(), new Observer<City>() {
            @Override
            public void onChanged(City city) {
                System.out.println("+++++++++++++++ "+city.getName());
                onCityAdd(city);
            }
        });
        // подписываемся на обновление запроса на УДАЛЕНИЕ ГОРОДА
        citiesViewModel.getDeleteCityRequest().observe(getViewLifecycleOwner(), new Observer<City>() {
            @Override
            public void onChanged(City city) {
                onCityDelete(city);
            }
        });
        // подписываемся на обновление запроса на добавление АДАПТЕРА ДНЕЙ ГОРОДА
        citiesViewModel.getAddDaysInCityRequest().observe(getViewLifecycleOwner(), new Observer<DaysAdapter>() {
            @Override
            public void onChanged(DaysAdapter adapter) {
                setDaysAdapterInCity(adapter);
            }
        });
        // подписываемся на обновления состояния сети
        citiesViewModel.getConnection().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean connection) {
                if (connection) return;
                ConnectionManager.showOfferSetting(getContext());
            }
        });
        // подписываемся на обновление запроса на добавление КОНТЕЙНЕРА ОШИБКИ
        citiesViewModel.getError_content().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String content) {
                Toast.makeText(getContext(), content, Toast.LENGTH_SHORT).show();
            }
        });
        // подписываемся на вызов Intent-ов
        citiesViewModel.getStartIntent().observe(getViewLifecycleOwner(), new Observer<Intent>() {
            @Override
            public void onChanged(Intent intent) {
                startActivity(intent);
            }
        });
        // подписываемся на вызов фрагмента
        citiesViewModel.getOpenRainMap().observe(getViewLifecycleOwner(), new Observer<FragmentRainMap>() {
            @Override
            public void onChanged(FragmentRainMap fragment) {
                showFragment(fragment);
            }
        });
        // подписываемся на обновление состояние загрузки городов
        citiesViewModel.getCities_loading().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean visible) {
                citiesViewModel.setProgress_visible(visible);
            }
        });
        // подписываемся на обновление состояние загрузки названий городов
        citiesViewModel.getNames_cities_loading().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean visible) {
                citiesViewModel.setNames_progress_visible(visible);
            }
        });
        // подписываемся на обновление списка названий городов
        citiesViewModel.getAuto_cities().observe(getViewLifecycleOwner(), new Observer<List<AutoEnteredCity>>() {
            @Override
            public void onChanged(List<AutoEnteredCity> names) {
                ArrayList<AutoEnteredCity> arr = new ArrayList<>(names);
                binding.autoCompleteTextView.setAdapter(new ArrayAdapter<>(getContext(), android.R.layout.select_dialog_item, arr));
            }
        });

        return binding.getRoot();
    }

    private void onCityAdd(City city){
        int index = cities_adapter.addCity(city);
        cities_adapter.notifyItemChanged(index);
    }
    private void onCityDelete(City city){
        int index = cities_adapter.deleteCity(city);
        cities_adapter.notifyItemRemoved(index);
    }

    private void setDaysAdapterInCity(DaysAdapter adapter){
        System.out.println("Дни пришли для "+adapter.getCity_name());
        cities_adapter.setDaysAdapterInCity(adapter);
    }

    private void onCitiesChanged(LinkedList<City> cities) {
        cities_adapter.setCities(cities);
        cities_adapter.notifyDataSetChanged();
    }

    private void setupCitiesRecyclerView(RecyclerView recyclerView) {
        CitiesAdapter adapter = new CitiesAdapter();
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // подписываемся на обновление запроса от ОПРЕДЕЛЁННОГО ГОРОДА
        adapter.getRequest().observe(getViewLifecycleOwner(), new Observer<RepositoryRequest>() {
            @Override
            public void onChanged(RepositoryRequest req) {
                citiesViewModel.processRequest(req);
            }
        });
    }

    private void showFragment(FragmentRainMap fragment){

    }

    @Override
    public void onDestroyView() {
        api=null;
        super.onDestroyView();
    }
}