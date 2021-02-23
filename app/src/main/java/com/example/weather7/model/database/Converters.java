package com.example.weather7.model.database;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import androidx.room.TypeConverter;

import com.example.weather7.model.adapters.DayAdapter;
import com.example.weather7.model.WeatherOnDay;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.LinkedList;

public class Converters {

    public static class BitmapConverter {

        /*@TypeConverter
        public static Bitmap bytesToBitmap(byte[] data) {
            if (data == null) {
                return null;
            }
            return BitmapFactory.decodeByteArray(data, 0, data.length);
        }

        @TypeConverter
        public static String bitmapToBytes(Bitmap bitmap) {
            if (bitmap == null) {
                return null;
            }
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            byte[] byteArray = stream.toByteArray();
            bitmap.recycle();
            try {
                stream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return byteArray;
        }*/

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
        public static DayAdapter stringToDayAdapter(String data){
            try {

                JSONObject obj = new JSONObject(data);

                LinkedList<WeatherOnDay> days= new LinkedList<>();
                JSONObject day;

                System.out.println(data);
                for (int i=0;i<obj.length();i++){
                    day=obj.getJSONObject(String.valueOf(i));
                    days.add(WeatherOnDay.jsonToWeatherOnDay(day));
                }

                return new DayAdapter(days);
            } catch (JSONException e) {
                e.printStackTrace();
                return new DayAdapter(new LinkedList<>());
            }
        }

        @TypeConverter
        public static String dayAdapterToJsonString(DayAdapter adapter) {
            JSONObject obj = new JSONObject();

            LinkedList<WeatherOnDay> days =adapter.getDays();
            for (int i=0;i<days.size();i++){
                try {
                    obj.put(String.valueOf(i), days.get(i).toJsonString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return obj.toString();
        }
    }
}
