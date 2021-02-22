package com.example.weather7.model;

import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;

public class ListCities implements Serializable {

    private ArrayList<City> list;

    public ListCities(ArrayList<City> list){
        this.list=list;
    }

    public ArrayList<City> getList() {
        return list;
    }
}
