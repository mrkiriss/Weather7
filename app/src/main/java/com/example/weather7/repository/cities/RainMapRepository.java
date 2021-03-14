package com.example.weather7.repository.cities;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.weather7.api.RainMapApi;
import com.google.android.gms.maps.model.TileProvider;

import org.json.JSONException;

import java.io.IOException;
import java.util.HashMap;

public class RainMapRepository {

    private final RainMapApi rainMapApi;

    private MutableLiveData<Boolean> loading;
    private MutableLiveData<String> toastContent;
    private MutableLiveData<HashMap<Integer, HashMap<String, TileProvider>>> mapData;

    public RainMapRepository(RainMapApi rainMapApi){
        this.rainMapApi=rainMapApi;

        loading = new MutableLiveData<>();
        toastContent = new MutableLiveData<>();
        mapData = new MutableLiveData<>();
    }

    public void downloadMapData(){
        Runnable task = () -> {
            loading.postValue(true);
            try {
                mapData.postValue(rainMapApi.downloadMasksOfRain());
            } catch (IOException | JSONException e) {
                e.printStackTrace();
                toastContent.postValue("Не удалось загрузить карту дождя");
            }
            loading.postValue(false);
        };
        Thread thread = new Thread(task);
        thread.start();
    }

    public LiveData<Boolean> getLoading(){return loading;}
    public LiveData<HashMap<Integer, HashMap<String, TileProvider>>> getMapData(){return mapData;}
    public LiveData<String> getToastContent(){return toastContent;}
}
