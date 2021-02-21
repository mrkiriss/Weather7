package com.example.weather7;


import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weather7.databinding.ItemCityBinding;
import com.example.weather7.model.City;
import com.example.weather7.viewmodel.ItemCityViewModel;

import java.util.Collections;
import java.util.List;

public class CityAdapter extends RecyclerView.Adapter<CityAdapter.CityViewHolder> {

    private List<City> cities;

    public CityAdapter(){
        this.cities= Collections.emptyList();
    }

    public void setCities(List<City> cities){
        this.cities=cities;
    }

    @Override
    public CityViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        ItemCityBinding binding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.item_city, parent, false);

        return new CityViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(CityViewHolder holder, int position){
        holder.bindCity(cities.get(position));
    }

    @Override
    public int getItemCount() {
        return cities.size();
    }

    public static class CityViewHolder extends RecyclerView.ViewHolder{
        ItemCityBinding binding;

        public CityViewHolder(ItemCityBinding binding) {
            super(binding.cardView);
            this.binding=binding;
        }

        void bindCity(City city){
            if (binding.getViewModel()==null){
                binding.setViewModel(new ItemCityViewModel(itemView.getContext(), city));
            }else{
                binding.getViewModel().setCity(city);
            }
            binding.daysRecycleView.setAdapter(city.getDays());
            binding.daysRecycleView.setLayoutManager(new LinearLayoutManager(itemView.getContext()));
        }


    }
}
