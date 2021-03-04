package com.example.weather7.viewmodel.cities.items;

import com.example.weather7.model.AutoEnteredCity;

public class ItemCityAViewModel {

    private String name;
    private String description;

    public ItemCityAViewModel(AutoEnteredCity city){
        this.description=city.getDescription();
        this.name=city.getName();
    }

    public String getName(){return name;}
    public String getDescription(){return description;}
    public void setCity(AutoEnteredCity city){
        description=city.getDescription();
        name=city.getName();
    }
}
