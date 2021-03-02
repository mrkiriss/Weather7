package com.example.weather7.view.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;

import androidx.databinding.DataBindingUtil;

import com.example.weather7.R;
import com.example.weather7.databinding.ItemAutoEnteredCityBinding;
import com.example.weather7.model.AutoEnteredCity;

import java.util.List;

public class CityAutoCompleteAdapter extends BaseAdapter {

    private List<AutoEnteredCity> cities;

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

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        AutoEnteredCity city = getItem(position);

        return convertView;
    }
}
