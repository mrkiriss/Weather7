package com.example.weather7.model;

public class AutoEnteredCity {
    private final String name;
    private final String description;

    public AutoEnteredCity(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName(){return name;}
    public String getDescription(){return description;}

    public String toString(){
        return name;
    }
}
