package com.example.weather7.api;

import com.example.weather7.model.cities.AutoEnteredCity;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;

public interface ICitiesNamesApi {
    ArrayList<AutoEnteredCity> downloadCities(String part_of_name) throws IOException, JSONException;
}
