package com.example.weather7.view.adapters;


import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weather7.R;
import com.example.weather7.databinding.ItemDayBinding;
import com.example.weather7.model.WeatherOnDay;
import com.example.weather7.viewmodel.cities.items.ItemDayViewModel;

import java.util.LinkedList;

public class DaysAdapter extends RecyclerView.Adapter<DaysAdapter.DayViewHolder> {

    private LinkedList<WeatherOnDay> days;
    private String city_name;
    public DaysAdapter(LinkedList<WeatherOnDay> days, String city_name){
        this.days=days;
        this.city_name=city_name;
    }

    public DayViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        ItemDayBinding binding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.item_day, parent, false);

        return new DayViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(DayViewHolder holder, int position){
        holder.bindDay(days.get(position));
    }

    @Override
    public int getItemCount() {
        return days.size();
    }

    public LinkedList<WeatherOnDay> getContent(){return days;}
    public void setContent(LinkedList<WeatherOnDay> content){this.days=content;}
    public String getCity_name(){return city_name;}

    public class DayViewHolder extends RecyclerView.ViewHolder{
        ItemDayBinding binding;

        public DayViewHolder(ItemDayBinding binding) {
            super(binding.cardView);
            this.binding=binding;
        }

        void bindDay(WeatherOnDay day){
            if (binding.getViewModel()==null){
                binding.setViewModel(new ItemDayViewModel(day));
            }else{
                binding.getViewModel().setDay(day);
            }
        }


    }
}
