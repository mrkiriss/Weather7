package com.example.weather7.repository;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.example.weather7.model.WeatherOnDay;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;

public class WeatherApi extends Thread{
    private final String url_for_coord="https://api.openweathermap.org/data/2.5/weather?q=";
    private final String url_for_weather="https://api.openweathermap.org/data/2.5/onecall?";
    private final String weather_api="&appid=1f81e7b2d7680c7aca83de0721ae05f2";
    private final String metric="&units=metric";
    private final String exclude="&exclude=minutely,hourly,alerts";
    private final String lon="&lon=";
    private final String lat="&lat=";

    public final static int MODE_ALL = 1;
    public final static int MODE_ONLY_WEATHER = 0;

    private String[] coordinate = null;
    private LinkedList<WeatherOnDay> weather = null;
    private String city_name="";

    private int mode;
    private String data;


    public WeatherApi(int mode, String data){
        this.mode=mode;
        this.data=data;
    }

    public void run() {  //тело потока
        switch (mode){
            case MODE_ALL:
                coordinate = getCoordinateByName(data);
                weather = getWeatherByCoordinate(coordinate[0], coordinate[1]);
                break;
            case MODE_ONLY_WEATHER:
                coordinate=data.split(" ");
                weather = getWeatherByCoordinate(coordinate[0], coordinate[1]);
                break;
        }
    }


    private String convertUnixToString(String stime, String time_zone){
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
    private Bitmap downloadUrlIcon(String id){
        String surl = "https://openweathermap.org/img/wn/"+id+"@2x.png";
        Bitmap bmp = null;
        try {
            URL url = new URL(surl);
            bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
            bmp = Bitmap.createScaledBitmap(bmp, 160, 160, false);
        }catch (MalformedURLException e) {
            Log.println(Log.WARN, "downloader/bitmap", "Ошибка программы при работе с url");
        }catch (IOException e) {
            Log.println(Log.WARN, "downloader/bitmap", "Картинка по ссылке не найдена");
        }
        return bmp;
    }
    private String toStr(Object str){
        return str.toString();
    }
    private String downloadContentByUrl(String url_request){
        String content = "";
        try {
            URL url = new URL(url_request);
            URLConnection connection = url.openConnection();

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                content += line;
            }
            reader.close();
        }catch (MalformedURLException e){
            Log.println(Log.WARN,"downloader/urlContent","Ошибка программы при работе с url");
        } catch (IOException e) {
            Log.println(Log.WARN,"downloader/urlContent",url_request);
        }
        return content;
    }
    private String[] parseCoord(String content) {
        String[] coord=new String[2];
        try{
            JSONObject obj = new JSONObject(content);
            coord[0]=obj.getJSONObject("coord").getString("lat");
            coord[1]=obj.getJSONObject("coord").getString("lon");
        }catch (JSONException e){
            Log.println(Log.WARN, "downloader/coordinate", "Ошибка преобразования json объекта координат");
        }
        return coord;
    }
    private String[] getCoordinateByName(String city_name){
        String coord_request = this.url_for_coord+city_name+weather_api+metric;
        String content = downloadContentByUrl(coord_request);
        String[] coord = parseCoord(content);
        return coord;
    }
    private LinkedList<WeatherOnDay> parseWeather(String content){
        LinkedList<WeatherOnDay> result = new LinkedList<>();
        String date;
        String[] temp = new String[4]; //{day, night, feels_like_day, feels_like_night}
        String description;
        String wind_speed;
        Bitmap icon;
        String pressure;
        String humidity;
        String clouds;
        try {
            JSONObject obj = new JSONObject(content);
            String timezone = obj.getString("timezone");
            JSONObject current_weather = obj.getJSONObject("current");
            JSONArray days = obj.getJSONArray("daily");
            days.put(0,current_weather);

            // запоминание имени города
            city_name=timezone;

            for (int i=0;i<days.length();i++){
                JSONObject day = days.getJSONObject(i);

                date = convertUnixToString(day.getString("dt"), timezone);
                // различное получение температуры и описания для нынешнего состояния и состояния каждого дня
                if (i==0){ // нынешнее состояние
                    temp[0]=toStr(Math.round(day.getDouble("temp")));
                    temp[2]=toStr(Math.round(day.getDouble("feels_like")));
                }else{
                    JSONObject temp_data = day.getJSONObject("temp");
                    JSONObject temp_feels_data = day.getJSONObject("feels_like");
                    temp[0]=toStr(Math.round(temp_data.getDouble("day")));
                    temp[1]=toStr(Math.round(temp_data.getDouble("night")));
                    temp[2]=toStr(Math.round(temp_feels_data.getDouble("day")));
                    temp[3]=toStr(Math.round(temp_feels_data.getDouble("night")));
                }
                wind_speed=day.getString("wind_speed");
                icon = downloadUrlIcon(day.getJSONArray("weather").getJSONObject(0).getString("icon"));
                pressure=day.getString("pressure");
                humidity=day.getString("humidity");
                clouds=day.getString("clouds");
                description=day.getJSONArray("weather").getJSONObject(0).getString("description");

                result.add(new WeatherOnDay(date, temp.clone(), wind_speed,icon, pressure, humidity, clouds, description));
            }
        }catch (JSONException e){
            Log.println(Log.WARN, "downloader/parseWeather", content);
        }
        return result;
    }
    private LinkedList<WeatherOnDay> getWeatherByCoordinate(String lat, String lon){
        String weather_request= url_for_weather+this.lat+lat+this.lon+lon+metric+weather_api+exclude;
        String content = downloadContentByUrl(weather_request);

        return parseWeather(content);
    }

    public synchronized String[] getCoordinate(){return coordinate;}
    public synchronized LinkedList<WeatherOnDay> getWeather(){return weather;}
    public synchronized String getCity_name(){return city_name;}

}