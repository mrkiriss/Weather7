package com.example.weather7.viewmodel.cities.items;

import android.graphics.Bitmap;
import android.widget.ImageView;

import androidx.databinding.BaseObservable;
import androidx.databinding.BindingAdapter;
import androidx.databinding.ObservableBoolean;
import androidx.lifecycle.MutableLiveData;

import com.example.weather7.model.base.City;
import com.example.weather7.repository.RepositoryRequest;
import com.example.weather7.repository.cities.CityRepository;

public class ItemCityViewModel extends BaseObservable {

    private City city;
    private String name_city_and_temp;
    private String name;
    private Bitmap icon;
    private String description;
    public ObservableBoolean expandable;

    private MutableLiveData<RepositoryRequest> request;


    public String getName_city_and_temp() {
        return name_city_and_temp;
    }
    public Bitmap getIcon() {
        return icon;
    }
    public ObservableBoolean getExpandable() {
        return expandable;
    }
    public String getDescription(){return description;}

    @BindingAdapter("android:src")
    public static void loadIcon(ImageView iv, Bitmap bitmap) {
        iv.setImageBitmap(bitmap);
    }

    public ItemCityViewModel(City city, MutableLiveData<RepositoryRequest> request){

        // значения для шапки
        this.city=city;
        this.name=city.getName();
        this.icon=city.getCurrent_icon();
        this.description=city.getCurrent_description();
        this.name_city_and_temp=city.getName()+": "+city.getCurrent_temp();

        // значения для адаптера дней
        this.expandable=new ObservableBoolean(false);

        // индикатор для удаления (так же отвечает за скрытие с экрана пользователя)
        this.request = request;

    }

    public void setCity(City city){
        this.city=city;
        notifyChange();
    }

    public void changeExpandable(){
        if (expandable.get()) {
            expandable.set(false);
        } else {
            expandable.set(true);
        }
    }

    public void openCityInMap(){
        request.setValue(new RepositoryRequest(CityRepository.REQUEST_OPEN_CITY_IN_MAP, city));
    }
    public void onDeleteClick(){
        request.setValue(new RepositoryRequest(CityRepository.REQUEST_DELETE, city));
    }
    public void openRainMap(){
        request.setValue(new RepositoryRequest(CityRepository.REQUEST_OPEN_RAIN_MAP, city));
    }
}
