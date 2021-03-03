package com.example.weather7.view.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.adapters.AdapterViewBindingAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weather7.R;
import com.example.weather7.databinding.ItemAutoEnteredCityBinding;
import com.example.weather7.databinding.ItemCityBinding;
import com.example.weather7.model.AutoEnteredCity;

import java.util.List;

public class CityAutoCompleteAdapter extends ArrayAdapter<AutoEnteredCity> {

    private List<AutoEnteredCity> cities;

    public CityAutoCompleteAdapter(@NonNull Context context, int resource, int textViewResourceId) {
        super(context, resource, textViewResourceId);
    }

    public void setCities(List<AutoEnteredCity> cities){
        this.cities=cities;
    }

    @Override
    public int getCount() {
        return cities.size();
    }

    @Override
    public AutoEnteredCity getItem(int position) {
        return cities.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

}
