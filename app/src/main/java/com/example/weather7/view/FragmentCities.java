package com.example.weather7.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.weather7.CityAdapter;
import com.example.weather7.R;
import com.example.weather7.databinding.FragmentCitiesBinding;
import com.example.weather7.model.City;
import com.example.weather7.viewmodel.CitiesViewModel;

import java.util.List;

public class FragmentCities extends Fragment {

    private CitiesViewModel citiesViewModel;
    private FragmentCitiesBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        citiesViewModel = new ViewModelProvider(this).get(CitiesViewModel.class);
        View root = inflater.inflate(R.layout.fragment_cities, container, false);

        return root;
    }

    public void onRepositoriesChanged(List<City> cities) {
        CityAdapter adapter =
                (CityAdapter) binding.citiesRecyclerView.getAdapter();
        adapter.setCities(cities);
        adapter.notifyDataSetChanged();
    }
}