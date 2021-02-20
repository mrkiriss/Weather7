package com.example.weather7.model;

import java.util.ArrayList;

public class City{
    String name;
    ArrayList<WeatherOnDay> weather;

    public boolean isExpandable() {
        return expandable;
    }

    public void setExpandable(boolean expandable) {
        this.expandable = expandable;
    }

    boolean expandable;

    public City(String name) throws InterruptedException {
        this.name=name;
        this.expandable=false;

        WeatherDownloader downloader = new WeatherDownloader(name);
        downloader.start();
        downloader.join();
        this.weather=downloader.getWeather();
    }
}
