package com.example.weather7.api;

import com.example.weather7.model.cities.AutoEnteredCity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

public class CitiesApi {

    private final String host = "https://api.vk.com/method/";
    private final String method = "database.getCities?";
    private final String version = "v=5.21";
    private final String lang="&lang=ru";
    private final String access_token = "&access_token=5d5d04ce5d5d04ce5d5d04ce115d2babc955d5d5d5d04ce3d631fbd900f16c5925896bc";
    private final String name = "&q=";
    private final String count = "&count="; // now only MAX_COUNT_OF_CITIES cities
    private final int MAX_COUNT_OF_CITIES=20;
    private final String country_id = "&country_id=1"; // now only RU
    private final String mode = "&need_all=0"; // now only main cities

    public ArrayList<AutoEnteredCity> downloadCities(String part_of_name) throws IOException, JSONException {
        ArrayList<AutoEnteredCity> result = new ArrayList<>();
        ArrayList<String> available_names = new ArrayList<>();;

        String request = host+method+version+lang+access_token+name+part_of_name+count+ MAX_COUNT_OF_CITIES +country_id+mode;
        String content = downloadContentByUrl(request);

        //System.out.println(content);
        //System.out.println(request);
        JSONObject obj = new JSONObject(content);
        JSONArray cities=obj.getJSONObject("response").getJSONArray("items");

        for (int i=0; i<MAX_COUNT_OF_CITIES && i<cities.length();i++){
            String name=cities.getJSONObject(i).getString("title");;
            if (name.length()>20 || available_names.contains(name)) continue;
            String description="";

            // отдельно, так как некоторые города могут идти без региона (Москва...)
            try{
                description = cities.getJSONObject(i).getString("region");
            }catch (JSONException e){
                e.printStackTrace();
            }

            result.add(new AutoEnteredCity(name, description));
            available_names.add(name);
        }

        return result;
    }

    private String downloadContentByUrl(String url_request) throws IOException {
        String content = "";

        URL url = new URL(url_request);
        URLConnection connection = url.openConnection();

        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String line;
        while ((line = reader.readLine()) != null) {
            content += line;
        }
        reader.close();

        return content;
    }

}
