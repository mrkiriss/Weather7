package com.example.weather7.model.adapters;


import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weather7.R;
import com.example.weather7.databinding.ItemDayBinding;
import com.example.weather7.model.WeatherOnDay;
import com.example.weather7.viewmodel.ItemDayViewModel;

import java.util.LinkedList;

public class DaysAdapter extends RecyclerView.Adapter<DaysAdapter.DayViewHolder> {

    private LinkedList<WeatherOnDay> days;

    public DaysAdapter(LinkedList<WeatherOnDay> days){
        this.days=days;
        System.out.println(days);
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
    public LinkedList<WeatherOnDay> getDays(){return days;}

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
