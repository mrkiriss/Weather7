
package com.example.weather7.viewmodel.cities;

import androidx.databinding.Observable;
import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableInt;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.weather7.repository.cities.RainMapRepository;
import com.google.android.gms.maps.model.TileProvider;

import java.util.HashMap;

public class RainMapViewModel extends ViewModel {

    private ObservableBoolean progress_loading;
    private ObservableInt progress_seekbar;

    private LiveData<HashMap<Integer, HashMap<String, TileProvider>>> mapData;
    private LiveData<Boolean> loading;
    private LiveData<String> error;
    private MutableLiveData<TileProvider> selectedProvider;

    public RainMapViewModel(RainMapRepository rep){


        this.progress_loading=new ObservableBoolean(false);
        this.progress_seekbar = new ObservableInt(0);
        this.selectedProvider=new MutableLiveData<>();

        // подписываемся на обновление данных модели
        this.mapData=rep.getMapData();
        this.loading=rep.getLoading();
        this.error=rep.getToastContent();

        // подписываемся на изменение выбранного времени
        initOnChangeProgressSeekbar();

        rep.downloadMapData();
    }

    private void initOnChangeProgressSeekbar(){
        progress_seekbar.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                int current_progress=progress_seekbar.get();
                HashMap<String, TileProvider> required_part_mapData = mapData.getValue().get(current_progress);

                TileProvider requiredProvider = null;
                for (String date: required_part_mapData.keySet()){
                    requiredProvider= required_part_mapData.get(date);
                }

                selectedProvider.setValue(requiredProvider);
            }
        });
    }

    public LiveData<HashMap<Integer, HashMap<String, TileProvider>>> getMapData(){return mapData;}
    public LiveData<Boolean> getLoading(){return loading;}
    public LiveData<String> getError(){return error;}
    public ObservableBoolean getProgress_loading(){return progress_loading;}
    public void setProgress_loading(Boolean progress_loading){this.progress_loading.set(progress_loading);}
    public ObservableInt getProgress_seekbar(){return progress_seekbar;}
    public void setProgress_seekbar(int progress_seekbar){this.progress_seekbar.set(progress_seekbar);}
    public MutableLiveData<TileProvider> getSelectedProvider() {
        return selectedProvider;
    }

}