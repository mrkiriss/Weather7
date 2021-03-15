package com.example.weather7.view.cities;

import android.app.ActivityOptions;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weather7.di.App;
import com.example.weather7.model.cities.AutoEnteredCity;
import com.example.weather7.utils.ConnectionManager;
import com.example.weather7.view.cities.adapters.CitiesAdapter;
import com.example.weather7.R;
import com.example.weather7.databinding.FragmentCitiesBinding;
import com.example.weather7.model.base.City;
import com.example.weather7.view.cities.adapters.DaysAdapter;
import com.example.weather7.viewmodel.cities.CitiesViewModel;

import java.util.ArrayList;
import java.util.LinkedList;

import javax.inject.Inject;

public class FragmentCities extends Fragment{

    @Inject
    CitiesViewModel citiesViewModel;
    @Inject
    ConnectionManager connectionManager;
    @Inject
    CitiesAdapter cities_adapter;
    @Inject
    LinearLayoutManager citiesAdapterManager;

    private FragmentCitiesBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        App.getInstance().getComponentManager().getFCitiesSubcomponent().inject(this);

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_cities, container, false);
        binding.setViewModel(citiesViewModel);

        setupCitiesRecyclerView(binding.citiesRecyclerView);

        initObservers();

        return binding.getRoot();
    }

    private void initObservers(){
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
            connectionManager.showOfferSetting(getContext());
        });
        // подписываемся на обновление запроса на добавление КОНТЕЙНЕРА ОШИБКИ
        citiesViewModel.getError_content().observe(getViewLifecycleOwner(), content -> {
            Toast.makeText(getContext(), content, Toast.LENGTH_SHORT).show();
        });
        // подписываемся на вызов Intent-ов
        citiesViewModel.getStartIntent().observe(getViewLifecycleOwner(), intent -> {
            switch (intent.getStringExtra("class")){
                case "rain":
                    intent.setClass(getContext(), RainMapActivity.class);
            }
            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(getActivity()).toBundle());
        });
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
        cities_adapter.setDaysAdapterInCity(adapter);
    }

    private void onCitiesChanged(LinkedList<City> cities) {
        cities_adapter.setCities(cities);
        cities_adapter.notifyDataSetChanged();
    }

    private void setupCitiesRecyclerView(RecyclerView recyclerView) {
        recyclerView.setAdapter(cities_adapter);
        recyclerView.setLayoutManager(citiesAdapterManager);

        // подписываемся на обновление запроса от ОПРЕДЕЛЁННОГО ГОРОДА
        cities_adapter.getRequest().observe(getViewLifecycleOwner(), req -> citiesViewModel.processRequest(req));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        App.getInstance().getComponentManager().clearFCitiesSubcomponent();
    }
}