package com.example.weather7.api;

import com.google.android.gms.maps.model.TileProvider;

import org.json.JSONException;

import java.io.IOException;
import java.util.HashMap;

public interface IRainMapApi {
    HashMap<Integer, HashMap<String, TileProvider>> downloadMasksOfRain() throws IOException, JSONException;
}
