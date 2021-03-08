package com.example.weather7.view.cities;

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
import com.example.weather7.api.WeatherApi;
import com.example.weather7.model.cities.AutoEnteredCity;
import com.example.weather7.repository.cities.CityRepositoryRequest;
import com.example.weather7.repository.cities.CityRepository;
import com.example.weather7.utils.ConnectionManager;
import com.example.weather7.view.FragmentRainMap;
import com.example.weather7.view.cities.adapters.CitiesAdapter;
import com.example.weather7.R;
import com.example.weather7.databinding.FragmentCitiesBinding;
import com.example.weather7.model.cities.City;
import com.example.weather7.database.AppDatabase;
import com.example.weather7.view.cities.adapters.DaysAdapter;
import com.example.weather7.viewmodel.cities.CitiesViewModel;

import java.util.ArrayList;
import java.util.LinkedList;

public class FragmentCities extends Fragment{

    private CitiesViewModel citiesViewModel;
    private FragmentCitiesBinding binding;
    private AppDatabase db;
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

        // создание экземпляра Погодного api
        WeatherApi api = new WeatherApi(getContext());
        // создание экземпляра api названий городов для поиска
        CitiesApi citiesApi=new CitiesApi();
        // создание ViewModel
        citiesViewModel = new CitiesViewModel(new CityRepository(db, api, citiesApi));
        binding.setViewModel(citiesViewModel);

        setupCitiesRecyclerView(binding.citiesRecyclerView);

        cities_adapter =
                (CitiesAdapter) binding.citiesRecyclerView.getAdapter();

        // подписываемся на обновление ГОРОДОВ
        citiesViewModel.getCities().observe(getViewLifecycleOwner(), this::onCitiesChanged);
        // подписываемся на обновление запроса на добавление ШАПКИ ГОРОДА
        citiesViewModel.getAddCityHeadRequest().observe(getViewLifecycleOwner(), this::onCityAdd);
        // подписываемся на обновление запроса на УДАЛЕНИЕ ГОРОДА
        citiesViewModel.getDeleteCityRequest().observe(getViewLifecycleOwner(), this::onCityDelete);
        // подписываемся на обновление запроса на добавление АДАПТЕРА ДНЕЙ ГОРОДА
        citiesViewModel.getAddDaysInCityRequest().observe(getViewLifecycleOwner(), this::setDaysAdapterInCity);
        // подписываемся на обновления состояния сети
        citiesViewModel.getConnection().observe(getViewLifecycleOwner(), connection -> {
            if (connection) return;
            ConnectionManager.showOfferSetting(getContext());
        });
        // подписываемся на обновление запроса на добавление КОНТЕЙНЕРА ОШИБКИ
        citiesViewModel.getError_content().observe(getViewLifecycleOwner(), content -> Toast.makeText(getContext(), content, Toast.LENGTH_SHORT).show());
        // подписываемся на вызов Intent-ов
        citiesViewModel.getStartIntent().observe(getViewLifecycleOwner(), new Observer<Intent>() {
            @Override
            public void onChanged(Intent intent) {
                switch (intent.getStringExtra("class")){
                    case "rain":
                        intent.setClass(getContext(), RainMapActivity.class);
                }
                startActivity(intent);
            }
        });
        // подписываемся на вызов фрагмента
        citiesViewModel.getOpenRainMap().observe(getViewLifecycleOwner(), this::showFragment);
        // подписываемся на обновление состояние загрузки городов
        citiesViewModel.getCities_loading().observe(getViewLifecycleOwner(), visible -> citiesViewModel.setProgress_visible(visible));
        // подписываемся на обновление состояние загрузки названий городов
        citiesViewModel.getNames_cities_loading().observe(getViewLifecycleOwner(), visible -> citiesViewModel.setNames_progress_visible(visible));
        // подписываемся на обновление списка названий городов
        citiesViewModel.getAuto_cities().observe(getViewLifecycleOwner(), names -> {
            ArrayList<AutoEnteredCity> arr = new ArrayList<>(names);
            binding.autoCompleteTextView.setAdapter(new ArrayAdapter<>(getContext(), android.R.layout.select_dialog_item, arr));
            //binding.autoCompleteTextView.showDropDown();
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
        adapter.getRequest().observe(getViewLifecycleOwner(), new Observer<CityRepositoryRequest>() {
            @Override
            public void onChanged(CityRepositoryRequest req) {
                citiesViewModel.processRequest(req);
            }
        });
    }

    private void showFragment(FragmentRainMap fragment){

    }

    @Override
    public void onDestroyView() {
        db.close();
        super.onDestroyView();
    }
}