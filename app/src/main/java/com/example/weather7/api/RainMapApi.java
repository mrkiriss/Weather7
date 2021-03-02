package com.example.weather7.api;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

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
import java.util.HashMap;
import java.util.List;

public class RainMapApi{

    private final String url_for_paths="https://api.rainviewer.com/public/weather-maps.json";
    private final int number_of_old_photos=3;

    private String host="https://tilecache.rainviewer.com/";
    //private final String path="v2/radar/nowcast_b9b4c690bcc4";
    private final String size="/512/";
    private final String z="7/";
    private final String lat="59.939095/";
    private final String lon="30.315868/";
    private final String color="2/";
    private final String options="1_0";
    private final String format=".png";

    private final Context context;
    private final String timezone="Europe/Moscow";

    public RainMapApi(Context context) {
        this.context=context;
    }

    public HashMap<Integer, HashMap<String, Bitmap>> downloadMasksOfRain(String lat, String lon, String timezone) throws IOException, JSONException {
        HashMap<Integer, HashMap<String, Bitmap>> result = new HashMap<>();

        HashMap<Integer, String[]> times_paths = downloadTimesAndPaths();

        for (int i=0;i<times_paths.size();i++){
            String time = convertUnixTimeToFormatString(times_paths.get(i)[0], this.timezone);
            Bitmap bmp = downloadBitmap(times_paths.get(i)[1]);

            HashMap<String, Bitmap> single_result = new HashMap<>();
            single_result.put(time, bmp);

            result.put(i, single_result);
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
    private String convertUnixTimeToFormatString(String stime, String timezone) {

        long time = Long.parseLong(stime);
        Date date = new java.util.Date(time*1000L);

        SimpleDateFormat sdf = new java.text.SimpleDateFormat("H-m");

        sdf.setTimeZone(java.util.TimeZone.getTimeZone(timezone));
        return sdf.format(date);
    }
    private HashMap<Integer, String[]> downloadTimesAndPaths() throws IOException, JSONException {
        HashMap<Integer, String[]> result= new HashMap<>();
        String[] single_result = new String[2];
        int adding_index=0;

        JSONObject obj = new JSONObject(downloadContentByUrl(url_for_paths));
        host=obj.getString("host");

        JSONArray radar_past = obj.getJSONObject("radar").getJSONArray("past");
        for (int i=radar_past.length()-number_of_old_photos;i<radar_past.length();i++){
            JSONObject time_path=radar_past.getJSONObject(i);
            single_result[0]=time_path.getString("time");
            single_result[1]=time_path.getString("path");
            result.put(adding_index++, single_result.clone());
        }

        JSONArray radar_nowcast = obj.getJSONObject("radar").getJSONArray("nowcast");
        for (int i=0;i<radar_nowcast.length();i++){
            JSONObject time_path=radar_nowcast.getJSONObject(i);
            single_result[0]=time_path.getString("time");
            single_result[1]=time_path.getString("path");
            result.put(adding_index++, single_result.clone());
        }

        return result;
    }
    private Bitmap downloadBitmap(String path) throws IOException {
        String surl = host+path+size+z+lat+lon+color+options+format;

        URL url = new URL(surl); //java.net.MalformedURLException
        Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream()); //java.io.IOException
        //bmp = Bitmap.createScaledBitmap(bmp, 160, 160, false);

        return bmp;
    }
}
