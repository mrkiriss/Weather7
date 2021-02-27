package com.example.weather7.api;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.example.weather7.model.City;
import com.example.weather7.model.WeatherOnDay;
import com.example.weather7.view.adapters.DaysAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;

public class WeatherApi{
    private final String url_for_head="https://api.openweathermap.org/data/2.5/weather?q=";
    private final String url_for_days="https://api.openweathermap.org/data/2.5/onecall?";
    private final String weather_api="&appid=beb7c390d2db9cfa4d3b327035507589";
    private final String metric="&units=metric";
    private final String exclude="&exclude=minutely,hourly,alerts";
    private final String lon="&lon=";
    private final String lat="&lat=";
    private final String lang = "&lang=ru";

    public City startCityHeadDownload(String name) throws IOException, JSONException {
        String head_url_request = this.url_for_head+name+weather_api+metric+lang;
        String content = downloadContentByUrl(head_url_request);
        return parseCityHead(name, content);
    }

    public DaysAdapter startCityDaysDownload(String name, String lat, String lon) throws IOException, JSONException {
        String days_url_request = this.url_for_days+this.lat+lat+this.lon+lon+metric+weather_api+exclude+lang;
        String content = downloadContentByUrl(days_url_request);
        return parseDays(name, content);
    }

    private String convertUnixTimeToFormatString(String stime, String time_zone){
        // перевод секунд в миллисекунды
        long time = Long.valueOf(stime);
        Date date = new java.util.Date(time*1000L);
        // формат даты
        SimpleDateFormat sdf = new java.text.SimpleDateFormat("MM-dd");
        // give a timezone reference for formatting (see comment at the bottom)
        sdf.setTimeZone(java.util.TimeZone.getTimeZone(time_zone));
        String result = sdf.format(date);
        return result;
    }
    private Bitmap downloadUrlIcon(String id) throws IOException {
        String surl = "https://openweathermap.org/img/wn/"+id+"@2x.png";
        Bitmap bmp = null;

        URL url = new URL(surl); //java.net.MalformedURLException
        bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream()); //java.io.IOException
        bmp = Bitmap.createScaledBitmap(bmp, 160, 160, false);

        return bmp;
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
    private String toStr(Object object) {return String.valueOf(object);}
    private City parseCityHead(String name, String content) throws JSONException, IOException {
        JSONObject obj = new JSONObject(content);

        String lat = obj.getJSONObject("coord").getString("lat");
        String lon = obj.getJSONObject("coord").getString("lon");
        String description = obj.getJSONArray("weather").getJSONObject(0).getString("description");
        String icon_id = obj.getJSONArray("weather").getJSONObject(0).getString("icon");
        Bitmap icon = downloadUrlIcon(icon_id);
        String temp = toStr(Math.round(obj.getJSONObject("main").getDouble("temp")));
        String feels_like = toStr(Math.round(obj.getJSONObject("main").getDouble("feels_like")));
        String result_temp=temp+"/"+feels_like+"°C";

        return new City(name, lat, lon, result_temp, description, icon);
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

            date = convertUnixTimeToFormatString(day.getString("dt"), timezone);

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
}