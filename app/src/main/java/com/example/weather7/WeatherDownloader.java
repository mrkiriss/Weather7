package com.example.weather7;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.lang.reflect.Type;
import java.util.List;

public class WeatherDownloader extends Thread{
    private String url_coord="https://api.openweathermap.org/data/2.5/weather?q=";
    private String url_weather="https://api.openweathermap.org/data/2.5/onecall?lat=";
    private String weather_api="&appid=1f81e7b2d7680c7aca83de0721ae05f2";
    private String metric="&units=metric";
    private String exclude="&exclude=minutely,hourly,alerts";
    private String content_coord="";
    private String content_weather="";
    private HashMap<String, Object> convert_content_coord;
    private HashMap<String, Object> convert_content_weather;
    private JSONArray week_weather;
    private JSONObject current_weather;
    private String lon="";
    private String lat="";
    private String timezone="";

    private ArrayList<Weather> result = new ArrayList<>();

    public WeatherDownloader (String city_name){
        url_coord=url_coord+city_name+weather_api+metric;
    }

    public void run() {  //тело потока
        // загрузка города для получения координат
        try{
            URL url = new URL(this.url_coord);
            URLConnection connection = url.openConnection();

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            while((line=reader.readLine()) != null){
                content_coord+=line;
            }
            reader.close();
            // преобразование
            convert_content_coord=jsonToHashForCoord(content_coord);
            // получение координат города
            parseCoord();
            url_weather=url_weather+lat+"&lon="+lon+metric+weather_api+exclude;
        } catch (MalformedURLException e){
            System.out.println("Ошибка программы при работе с url");
        } catch (IOException e) {
            System.out.println("Город не найден. Повторите попытку");
        }
        // загрузка города для получения погодных условий в данный моменти и на 7 дней вперёд
        try{
            URL url = new URL(this.url_weather);
            URLConnection connection = url.openConnection();

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            while((line=reader.readLine()) != null){
                content_weather+=line;
            }
            reader.close();
            // парс нынешних и недельных данных
            jsonForWeather(content_weather);
            System.out.println(content_weather);

            // заполнение массива result объектами Weather
             // объявление переменных, которые будут применяться в конструкторах
            String date;
            String[] temp = new String[4]; //{day, night, feels_like_day, feels_like_night}
            String wind_speed;
            Bitmap icon;
            String pressure;
            String humidity;
            String clouds;
             // current
            System.out.println(current_weather);
            long dt = Long.parseLong(current_weather.getString("dt"));
            String timezone= this.timezone;
            date = convertUnixToString(dt, timezone);
            temp[0]=toStr(Math.round(current_weather.getDouble("temp")));
            temp[2]=toStr(Math.round(current_weather.getDouble("feels_like")));
            wind_speed=current_weather.getString("wind_speed");
            icon = downloadUrlIcon(current_weather.getJSONArray("weather").getJSONObject(0).getString("icon"));

            result.add(new Weather(date,temp.clone(),wind_speed,icon));
             // week
            for (int j=0; j<week_weather.length();j++){
                JSONObject i = week_weather.getJSONObject(j);

                dt = Long.parseLong(toStr(i.getString("dt")));
                date = convertUnixToString(dt, timezone);
                JSONObject temp_data = i.getJSONObject("temp");
                JSONObject temp_feels_data = i.getJSONObject("feels_like");
                temp[0]=toStr(Math.round(temp_data.getDouble("day")));
                temp[1]=toStr(Math.round(temp_data.getDouble("night")));
                temp[2]=toStr(Math.round(temp_feels_data.getDouble("day")));
                temp[3]=toStr(Math.round(temp_feels_data.getDouble("night")));
                wind_speed=toStr(i.getDouble("wind_speed"));
                icon = downloadUrlIcon(i.getJSONArray("weather").getJSONObject(0).getString("icon"));
                pressure=i.getString("pressure");
                humidity=i.getString("humidity");
                clouds=i.getString("clouds");

                result.add(new Weather(date,temp.clone(),wind_speed,icon,pressure, humidity, clouds));
            }
        } catch (MalformedURLException e){
            System.out.println("Ошибка программы при работе с url");
        } catch (IOException e) {
            System.out.println("Город по координатам не найден. Повторите попытку");
            System.out.println(url_weather);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private HashMap<String, Object> jsonToHashForCoord(String data){
        Type type = new TypeToken<HashMap<String, Object>>(){}.getType();
        HashMap<String, Object> map = new Gson().fromJson(data, type);
        return map;
    }
    private void jsonForWeather(String data) throws JSONException {
        JSONObject obj = new JSONObject(data);
        current_weather=obj.getJSONObject("current");
        timezone=obj.getString("timezone");
        week_weather=obj.getJSONArray("daily");
    }

    private void parseCoord(){
        Type type = new TypeToken<HashMap<String, String>>(){}.getType();
        HashMap<String, String> map = new Gson().fromJson(String.valueOf(convert_content_coord.get("coord")), type);
        this.lon=map.get("lon");
        this.lat=map.get("lat");
    }
    private String convertUnixToString(long time, String time_zone){
        // перевод секунд в миллисекунды
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
        }catch (MalformedURLException e) {
            System.out.println("Ошибка программы при работе с url");
        }catch (IOException e) {
            System.out.println("Картинка по ссылке не найдена. Повторите попытку");
            System.out.println(surl);
        }
        return bmp;
    }
    private String toStr(Object str){
        return str.toString();
    }

    public synchronized ArrayList<Weather> getWeather() {
        return this.result;
    }
}