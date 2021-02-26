package com.example.weather7.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.example.weather7.api.WeatherApi;
import com.example.weather7.model.RepositoryRequest;
import com.example.weather7.model.WeatherOnDay;
import com.example.weather7.repository.CityRepository;
import com.example.weather7.utils.ConnectionManager;
import com.example.weather7.view.adapters.CitiesAdapter;
import com.example.weather7.R;
import com.example.weather7.databinding.FragmentCitiesBinding;
import com.example.weather7.model.City;
import com.example.weather7.database.AppDatabase;
import com.example.weather7.view.adapters.DaysAdapter;
import com.example.weather7.viewmodel.CitiesViewModel;

import java.util.LinkedList;

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
        db =Room.databaseBuilder(getContext(),
                AppDatabase.class, "database")
                .allowMainThreadQueries()
                .fallbackToDestructiveMigration()
                .build();
        // создание экземпляра Погодного api
        api = new WeatherApi();
        // создание ViewModel
        citiesViewModel = new CitiesViewModel(new CityRepository(db, api));
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

            }
        });

        return binding.getRoot();
    }

    private void onCityAdd(City city){
        // добавляем город
        int position = cities_adapter.addCity(city);
        cities_adapter.notifyItemInserted(position);
    }
    private void onCityDelete(City city){
        // добавляем город
        int position = cities_adapter.deleteCity(city);
        cities_adapter.notifyItemRemoved(position);
    }

    private void setContentToDaysAdapter(LinkedList<WeatherOnDay> content, String city_name){
        //cities_adapter.
    }

    private void setDaysAdapterInCity(DaysAdapter adapter){
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

        // подписываемся на обновление запроса от ОПРЕДЕЛЁННОГО ГОРОДА(по имени)
        adapter.getRequest().observe(getViewLifecycleOwner(), new Observer<RepositoryRequest>() {
            @Override
            public void onChanged(RepositoryRequest req) {
                citiesViewModel.processRequest(req);
            }
        });
    }

    // слаушетли на запросы добавления города и добавления дней в город по названию (исп. тэг)


    @Override
    public void onDestroyView() {
        db.close();
        api=null;
        super.onDestroyView();
    }
}