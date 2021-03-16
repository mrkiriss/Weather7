package com.example.weather7.viewmodel;

import android.graphics.Bitmap;
import android.location.Location;
import android.widget.ImageView;

import androidx.databinding.BindingAdapter;
import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.weather7.model.base.City;
import com.example.weather7.repository.location.LocationRepository;
import com.example.weather7.view.cities.adapters.DaysAdapter;

import java.util.List;

public class LocationViewModel extends ViewModel {

    private LocationRepository rep;

    private ObservableField<String> cityName;
    private ObservableField<String> description;
    private ObservableField<String> temperature;
    private ObservableField<Bitmap> icon;
    private ObservableBoolean loading_progress;

    private LiveData<Boolean> networkConnection;
    private LiveData<String> toastContent;
    private LiveData<City> addCityRequest;
    private LiveData<DaysAdapter> addDaysAdapterRequest;
    private LiveData<Boolean> loadingProgressRequest;

    private MutableLiveData<Integer> refreshContentRequest;

    public LocationViewModel(LocationRepository rep){
        this.rep=rep;

        this.cityName=new ObservableField<>();
        this.description=new ObservableField<>();
        this.temperature=new ObservableField<>();
        this.icon=new ObservableField<>();
        this.loading_progress=new ObservableBoolean(false);

        this.networkConnection=rep.getNetworkConnection();
        this.toastContent=rep.getToastContent();
        this.addCityRequest=rep.getAddCityRequest();
        this.addDaysAdapterRequest=rep.getAddDaysAdapterRequest();
        this.loadingProgressRequest=rep.getLoadingProgressRequest();

        this.refreshContentRequest=new MutableLiveData<>(0);
    }

    public void fillCityContent(Location location){
        rep.fillCityContent(location);
    }

    @BindingAdapter("app:srcCompat")
    public static void loadIcon(ImageView iv, Bitmap bitmap) {
        iv.setImageBitmap(bitmap);
    }

    public void refreshContent(){
        refreshContentRequest.setValue(1);
    }

    public LiveData<Boolean> getNetworkConnection(){return networkConnection;}
    public ObservableField<String> getCityName() {
        return cityName;
    }
    public ObservableField<String> getDescription() {
        return description;
    }
    public ObservableField<String> getTemperature() {
        return temperature;
    }
    public ObservableField<Bitmap> getIcon() {
        return icon;
    }
    public LiveData<String> getToastContent() {
        return toastContent;
    }
    public LiveData<DaysAdapter> getAddDaysAdapterRequest() {
        return addDaysAdapterRequest;
    }
    public LiveData<City> getAddCityRequest() {
        return addCityRequest;
    }
    public MutableLiveData<Integer> getRefreshContentRequest() {
        return refreshContentRequest;
    }
    public ObservableBoolean getLoading_progress() {
        return loading_progress;
    }
    public LiveData<Boolean> getLoadingProgressRequest(){return loadingProgressRequest;}

    public void setLoading_progress(Boolean loading_progress) {
        this.loading_progress.set(loading_progress);
    }
    public void setCityName(String cityName) {
        this.cityName.set(cityName);
    }
    public void setDescription(String description) {
        this.description.set(description);
    }
    public void setTemperature(String temperature) {
        this.temperature.set(temperature);
    }
    public void setIcon(Bitmap icon) {
        this.icon.set(icon);
    }

}