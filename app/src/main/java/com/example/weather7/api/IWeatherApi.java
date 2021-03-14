package com.example.weather7.api;

import com.example.weather7.model.base.City;
import com.example.weather7.view.cities.adapters.DaysAdapter;

import org.json.JSONException;

import java.io.IOException;

public interface IWeatherApi {
    City getCityHead(String name) throws IOException, JSONException;
    DaysAdapter getCityDays(String name, String lat, String lon) throws IOException, JSONException;
    String[] getNotificationContent(String name) throws IOException, JSONException;
}
