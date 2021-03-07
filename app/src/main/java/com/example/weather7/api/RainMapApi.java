package com.example.weather7.api;

import android.util.Log;

import com.example.weather7.utils.ConverterDate;
import com.google.android.gms.maps.model.TileProvider;
import com.google.android.gms.maps.model.UrlTileProvider;

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
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;

public class RainMapApi{

    private final String url_for_paths="https://api.rainviewer.com/public/weather-maps.json";
    private final int number_of_old_photos=3;

    private String host="https://tilecache.rainviewer.com/";
    //private final String path="v2/radar/nowcast_b9b4c690bcc4";
    private final String size="/512/";
    private final String z="%d/";
    private final String x="%d/";
    private final String y="%d/";
    private final String color="2/";
    private final String options="1_0";
    private final String format=".png";


    public HashMap<Integer, HashMap<String, TileProvider>> downloadMasksOfRain() throws IOException, JSONException {

        HashMap<Integer, HashMap<String, TileProvider>> result = new HashMap<>();

        HashMap<Integer, String[]> times_paths = downloadTimesAndPaths();

        for (int i=0;i<times_paths.size();i++){
            String time = convertUnixTimeToFormatString(times_paths.get(i)[0]);
            TileProvider tileProvider = createTile(times_paths.get(i)[1]);


            HashMap<String, TileProvider> single_result=new HashMap<>();
            single_result.put(time, tileProvider);

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
    private String convertUnixTimeToFormatString(String stime) {
        long time = Long.parseLong(stime);

        return "  "+ ConverterDate.convertLongToHM(time);
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
    private TileProvider createTile(String path) throws IOException {
        String surl = host+path+size+z+x+y+color+options+format;

        TileProvider tileProvider = new UrlTileProvider(512, 512) {

            @Override
            public URL getTileUrl(int x, int y, int zoom) {

                String s = String.format(surl, zoom, x, y);

                try {
                    return new URL(s);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                    Log.println(Log.ASSERT, "rainmap api", "create tile failed");
                }
                return null;
            }
        };

        return tileProvider;
    }
}
