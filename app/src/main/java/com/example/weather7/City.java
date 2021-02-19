package com.example.weather7;

import com.example.weather7.Weather;

import java.util.ArrayList;
import java.util.HashMap;

public class City{
    String name;
    ArrayList<Weather> weather;

    public City(String name) throws InterruptedException {
        this.name=name;

        WeatherDownloader downloader = new WeatherDownloader(name);
        downloader.start();
        downloader.join();
        this.weather=downloader.getWeather();

        System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
        System.out.println(weather.get(0).temp[2]);
        System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
    }
}
