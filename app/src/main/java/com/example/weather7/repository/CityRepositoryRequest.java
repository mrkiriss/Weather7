package com.example.weather7.repository;

public class CityRepositoryRequest {
    private String mode;
    private Object object;

    public CityRepositoryRequest(String mode, Object object){
        this.mode=mode;
        this.object=object;
    }

    public String getMode(){return mode;}
    public Object getObject(){return object;}
}
