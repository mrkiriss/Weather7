package com.example.weather7.api;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;

import com.example.weather7.model.cities.City;
import com.example.weather7.model.cities.WeatherOnDay;
import com.example.weather7.utils.DateConverter;
import com.example.weather7.view.cities.adapters.DaysAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.LinkedList;
import java.util.List;

public class WeatherApi implements IWeatherApi{
    private final String url_for_head="https://api.openweathermap.org/data/2.5/weather?q=";
    private final String url_for_days="https://api.openweathermap.org/data/2.5/onecall?";
    private final String apiKey ="&appid=beb7c390d2db9cfa4d3b327035507589";
    private final String metric="&units=metric";
    private final String exclude="&exclude=minutely,hourly,alerts";
    private final String lon="&lon=";
    private final String lat="&lat=";
    private final String lang = "&lang=ru";

    private final Context context;

    public WeatherApi(Context context) {
        this.context=context;
    }

    @Override
    public City getCityHead(String name) throws IOException, JSONException {
        String head_url_request = this.url_for_head+name+ apiKey +metric+lang;
        String content = downloadContentByUrl(head_url_request);
        return parseCityHead(name, content);
    }

    @Override
    public DaysAdapter getCityDays(String name, String lat, String lon) throws IOException, JSONException {
        String days_url_request = this.url_for_days+this.lat+lat+this.lon+lon+metric+ apiKey +exclude+lang;
        String content = downloadContentByUrl(days_url_request);
        return parseDays(name, content);
    }

    @Override
    // [0]-temp+"°C" [1]-"Ощущается как "+feels_like+"°C: "+description
    public String[] getNotificationContent(String name) throws IOException, JSONException  {
        String notificationContent_url_request = this.url_for_head+name+ apiKey +metric+lang;
        String content = downloadContentByUrl(notificationContent_url_request);
        return parseNotificationContent(content);

    }

    protected String convertUnixTimeToFormatString(String stime){
        long time = Long.valueOf(stime);
        return DateConverter.convertLongToMD(time);
    }

    protected String downloadContentByUrl(String url_request) throws IOException {
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
    private Bitmap downloadUrlIcon(String id) throws IOException {
        String surl = "https://openweathermap.org/img/wn/"+id+"@2x.png";
        Bitmap bmp = null;

        URL url = new URL(surl); //java.net.MalformedURLException
        bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream()); //java.io.IOException
        bmp = Bitmap.createScaledBitmap(bmp, 160, 160, false);

        return bmp;
    }
    private String toStr(Object object) {return String.valueOf(object);}
    private City parseCityHead(String name, String content) throws JSONException, IOException {
        JSONObject obj = new JSONObject(content);

        String[] coordinates = getCoordinate(name);
        String lat;
        String lon;
        if (coordinates[0]==null || coordinates[1]==null) {
            lat = obj.getJSONObject("coord").getString("lat");
            lon = obj.getJSONObject("coord").getString("lon");
        }else{
            lat=coordinates[0];
            lon=coordinates[1];
        }
        String description = obj.getJSONArray("weather").getJSONObject(0).getString("description");
        String icon_id = obj.getJSONArray("weather").getJSONObject(0).getString("icon");
        Bitmap icon = downloadUrlIcon(icon_id);
        String temp = toStr(Math.round(obj.getJSONObject("main").getDouble("temp")));
        String feels_like = toStr(Math.round(obj.getJSONObject("main").getDouble("feels_like")));
        String result_temp=temp+"/"+feels_like+"°C";
        String timezone = obj.getString("timezone");

        return new City(name, timezone, lat, lon, result_temp, description, icon);
    }
    private String[] getCoordinate(String location) throws IOException {
        String[] coordinates = new String[2];

        Geocoder geo = new Geocoder(context);
        List<Address> adr = null;
        adr = geo.getFromLocationName(location, 1);
        if (adr.size()>0){
            coordinates[0]=String.valueOf(adr.get(0).getLatitude());
            coordinates[1]=String.valueOf(adr.get(0).getLongitude());
        }
        return coordinates;
    }
    private DaysAdapter parseDays(String name, String content) throws JSONException, IOException {
        LinkedList<WeatherOnDay> result = new LinkedList<>();
        String date;
        String[] temp = new String[4]; //{day, night, feels_like_day, feels_like_night}
        String description;
        String wind_speed;
        Bitmap icon;
        String pressure;
        String humidity;
        String clouds;

        JSONObject obj = new JSONObject(content);
        String timezone = obj.getString("timezone");
        JSONArray days = obj.getJSONArray("daily");

        for (int i=0;i<days.length();i++){
            JSONObject day = days.getJSONObject(i);

            date = convertUnixTimeToFormatString(day.getString("dt"));

            JSONObject temp_data = day.getJSONObject("temp");
            JSONObject temp_feels_data = day.getJSONObject("feels_like");
            temp[0]=toStr(Math.round(temp_data.getDouble("day")));
            temp[1]=toStr(Math.round(temp_data.getDouble("night")));
            temp[2]=toStr(Math.round(temp_feels_data.getDouble("day")));
            temp[3]=toStr(Math.round(temp_feels_data.getDouble("night")));

            wind_speed=day.getString("wind_speed");
            icon = downloadUrlIcon(day.getJSONArray("weather").getJSONObject(0).getString("icon"));
            pressure=day.getString("pressure");
            humidity=day.getString("humidity");
            clouds=day.getString("clouds");
            description=day.getJSONArray("weather").getJSONObject(0).getString("description");

            result.add(new WeatherOnDay(date, temp.clone(), wind_speed,icon, pressure, humidity, clouds, description));
        }

        return new DaysAdapter(result, name);
    }
    private String[] parseNotificationContent(String content) throws JSONException {
        String[] result = new String[2];
        JSONObject obj=new JSONObject(content);

        String temp =toStr(Math.round(obj.getJSONObject("main").getDouble("temp")));
        result[0]=": "+temp+"°C";

        String description=obj.getJSONArray("weather").getJSONObject(0).getString("description");
        String feels_like = toStr(Math.round(obj.getJSONObject("main").getDouble("feels_like")));
        result[1]= "Ощущается как "+feels_like+"°C: "+description;

        return result;
    }
}