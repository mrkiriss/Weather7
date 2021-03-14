package com.example.weather7.view.cities.adapters;


import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weather7.R;
import com.example.weather7.databinding.ItemCityBinding;
import com.example.weather7.model.base.City;
import com.example.weather7.repository.RepositoryRequest;
import com.example.weather7.viewmodel.cities.items.ItemCityViewModel;

import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;

public class CitiesAdapter extends RecyclerView.Adapter<CitiesAdapter.CityViewHolder> {

    private LinkedList<City> cities;
    private MutableLiveData<RepositoryRequest> request = new MutableLiveData<>();

    public CitiesAdapter(){
        this.cities= new LinkedList<>();
    }

    @NotNull
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

    public MutableLiveData<RepositoryRequest> getRequest(){return request;}
    public void setCities(LinkedList<City> cities){
        this.cities=cities;
    }
    public int addCity(City city){
        int index = findIndexCityByName(city.getName());
        if (index==-1) this.cities.add(city);
        return this.cities.size()-1;
    }
    public int deleteCity(City this_city){
        int index = findIndexCityByName(this_city.getName());
        if (index!=-1) cities.remove(index);
        return index;
    }
    private int findIndexCityByName(String name){
        for (int i=0;i<cities.size();i++){
            City city = cities.get(i);
            if (city.getName().equals(name)){
                return i;
            }
        }
        return -1;
    }
    public void setDaysAdapterInCity(DaysAdapter adapter){
        String city_name=adapter.getCity_name();
        // ищем экземпляр города по названию в адаптере
        for (City city: cities){
            if (city.getName().equals(city_name)){
                city.getDaysAdapter().setContent(adapter.getContent());
                city.getDaysAdapter().notifyDataSetChanged();
            }
        }
    }

    public class CityViewHolder extends RecyclerView.ViewHolder{
        ItemCityBinding binding;

        public CityViewHolder(ItemCityBinding binding) {
            super(binding.cardView);
            this.binding=binding;
        }

        void bindCity(City city){
            binding.setViewModel(new ItemCityViewModel(city, request));

            binding.daysRecycleView.setAdapter(city.getDaysAdapter());
            binding.daysRecycleView.setLayoutManager(new LinearLayoutManager(itemView.getContext()));

        }

    }

}
