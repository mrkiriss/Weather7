package com.example.weather7;


import android.opengl.Visibility;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weather7.databinding.ItemDayBinding;
import com.example.weather7.model.WeatherOnDay;
import com.example.weather7.viewmodel.ItemDayViewModel;

import java.util.Collections;
import java.util.List;

public class DayAdapter extends RecyclerView.Adapter<DayAdapter.DayViewHolder> {

    private List<WeatherOnDay> days;

    public DayAdapter(List<WeatherOnDay> days){
        this.days=days;
    }

    @Override
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

    public static class DayViewHolder extends RecyclerView.ViewHolder{
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
