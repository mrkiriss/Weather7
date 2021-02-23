package com.example.weather7.view;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.example.weather7.model.CityRepository;
import com.example.weather7.model.adapters.CityAdapter;
import com.example.weather7.R;
import com.example.weather7.databinding.FragmentCitiesBinding;
import com.example.weather7.model.City;
import com.example.weather7.model.database.AppDatabase;
import com.example.weather7.viewmodel.CitiesViewModel;

import java.util.LinkedList;

public class FragmentCities extends Fragment{

    private CitiesViewModel citiesViewModel;
    private FragmentCitiesBinding binding;
    AppDatabase db;

    private onCitiesFragmentListener post;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try{
            post = (onCitiesFragmentListener) context;
        }catch(Exception e){
            Log.println(Log.ASSERT, "frCities/onAttach", context.toString());
        }
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_cities, container, false);

        // создание экземпляра ДБ
        db = Room.databaseBuilder(getContext(),
                AppDatabase.class, "database")
                .allowMainThreadQueries().build();
        //db.clearAllTables();
        // создание ViewModel
        citiesViewModel = new CitiesViewModel(new CityRepository(db));
        binding.setViewModel(citiesViewModel);

        setupRecyclerView(binding.citiesRecyclerView);

        // подписываемся на обновление городов
        citiesViewModel.getCities().observe(getViewLifecycleOwner(), new Observer<LinkedList<City>>() {
            @Override
            public void onChanged(LinkedList<City> cities) {
                onCitiesChanged(cities);
            }
        });
        // подписываемся на обновление загрузки
        citiesViewModel.getConnection().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean loading) {

            }
        });
        // подписываемся на обновления состояния сети
        citiesViewModel.getConnection().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean connection) {

            }
        });

        return binding.getRoot();
    }

    private void onCitiesChanged(LinkedList<City> cities) {
        CityAdapter adapter =
                (CityAdapter) binding.citiesRecyclerView.getAdapter();
        adapter.setCities(cities);
        adapter.notifyDataSetChanged();
    }

    private void setupRecyclerView(RecyclerView recyclerView) {
        CityAdapter adapter = new CityAdapter();
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    public interface onCitiesFragmentListener{
        // получает данные о городах для создания адаптера
        LinkedList<City> getLastCities();
        void bindActuallyCities(MutableLiveData<LinkedList<City>> live_cities);
    }

    @Override
    public void onDestroyView() {
        db.close();
        super.onDestroyView();
    }
}