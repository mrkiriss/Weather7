package com.example.weather7.model;

public class RepositoryRequest {
    private String mode;
    private Object object;

    public RepositoryRequest(String mode, Object object){
        this.mode=mode;
        this.object=object;
    }

    public String getMode(){return mode;}
    public Object getObject(){return object;}
}
