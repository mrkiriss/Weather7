package com.example.weather7.view;

import android.os.Bundle;
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
import com.example.weather7.viewmodel.CitiesViewModel;

import java.util.ArrayList;
import java.util.List;

public class FragmentCities extends Fragment implements CitiesViewModel.FragmentCitiesPostman{

    private CitiesViewModel citiesViewModel;
    private FragmentCitiesBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_cities, container, false);
        citiesViewModel = new CitiesViewModel(this);
        binding.setViewModel(citiesViewModel);
        setupRecyclerView(binding.citiesRecyclerView);

        return binding.getRoot();
    }

    @Override
    public void onCitiesChanged(List<City> cities) {
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

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        if (outState == null)
            outState = new Bundle();
        outState.putSerializable();
        super.onSaveInstanceState(outState);
    }
}