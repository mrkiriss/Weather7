package com.example.weather7.viewmodel;

import android.graphics.Bitmap;
import android.widget.ImageView;

import androidx.databinding.BaseObservable;
import androidx.databinding.BindingAdapter;
import androidx.databinding.ObservableBoolean;

import com.example.weather7.model.City;

public class ItemCityViewModel extends BaseObservable {

    private City city;
    private String name_city_and_temp;
    private Bitmap icon;
    private String description;
    public ObservableBoolean expandable;


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

    public ItemCityViewModel(City city){

        // значения для шапки
        this.city=city;
        this.icon=city.getCurrent_icon();
        this.description=city.getCurrent_description();
        this.name_city_and_temp=city.getName()+": "+city.getCurrent_temp();

        // значения для адаптера дней
        this.expandable=new ObservableBoolean(false);

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
}
