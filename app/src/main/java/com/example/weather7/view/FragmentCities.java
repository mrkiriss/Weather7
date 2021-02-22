package com.example.weather7.view;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weather7.CityAdapter;
import com.example.weather7.R;
import com.example.weather7.databinding.FragmentCitiesBinding;
import com.example.weather7.model.City;
import com.example.weather7.model.ListCities;
import com.example.weather7.viewmodel.CitiesViewModel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class FragmentCities extends Fragment{

    private CitiesViewModel citiesViewModel;
    private FragmentCitiesBinding binding;

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
        citiesViewModel = new CitiesViewModel();
        binding.setViewModel(citiesViewModel);
        setupRecyclerView(binding.citiesRecyclerView);

        if (citiesViewModel.getMutableCities().getValue()==null){
            post.requestUpdateCities(citiesViewModel);
        }

        citiesViewModel.getMutableCities().observe(getViewLifecycleOwner(), new Observer<ArrayList<City>>() {
            @Override
            public void onChanged(ArrayList<City> cities) {
                onCitiesChanged(cities);
            }
        });

        return binding.getRoot();
    }

    private void onCitiesChanged(ArrayList<City> cities) {
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
        void requestUpdateCities(CitiesViewModel citiesViewModel);
    }
}