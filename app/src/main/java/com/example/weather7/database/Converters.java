package com.example.weather7.database;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import androidx.room.TypeConverter;

import com.example.weather7.view.cities.adapters.DaysAdapter;
import com.example.weather7.model.WeatherOnDay;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.LinkedList;

public class Converters {

    public static class BitmapConverter {

        @TypeConverter
        public static String bitmapToString(Bitmap bitmap){
            ByteArrayOutputStream baos=new  ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG,100, baos);
            byte [] b=baos.toByteArray();
            String temp= Base64.encodeToString(b, Base64.DEFAULT);
            return temp;
        }
        @TypeConverter
        public static Bitmap StringToBitmap(String encodedString){
            try {
                byte [] encodeByte=Base64.decode(encodedString,Base64.DEFAULT);
                Bitmap bitmap=BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
                return bitmap;
            } catch(Exception e) {
                e.getMessage();
                return null;
            }
        }
    }

    public static class DayAdapterConverter{
        @TypeConverter
        public static DaysAdapter stringToDayAdapter(String data){
            try {

                System.out.println(data);

                JSONObject obj = new JSONObject(data);

                LinkedList<WeatherOnDay> days= new LinkedList<>();
                JSONObject day;
                String city_name=obj.getString("city_name");
                String day_data;

                for (int i=0;i<obj.length()-1;i++){
                    day_data=obj.getString(String.valueOf(i));
                    day=new JSONObject(day_data);
                    days.add(WeatherOnDay.jsonToWeatherOnDay(day));
                }

                return new DaysAdapter(days, city_name);
            } catch (JSONException e) {
                e.printStackTrace();
                return new DaysAdapter(new LinkedList<>(), "");
            }
        }

        @TypeConverter
        public static String dayAdapterToJsonString(DaysAdapter adapter) {
            JSONObject obj = new JSONObject();

            try {
                if (adapter==null) return "------";
                obj.put("city_name", adapter.getCity_name());
            } catch (JSONException e) {
                e.printStackTrace();
                return "";
            }

            LinkedList<WeatherOnDay> days =adapter.getContent();
            for (int i=0;i<days.size();i++){
                try {
                    obj.put(String.valueOf(i), days.get(i).toJsonString());
                } catch (JSONException e) {
                    e.printStackTrace();
                    return "";
                }
            }
            return obj.toString();
        }
    }
}
