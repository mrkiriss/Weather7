package com.example.weather7.viewmodel;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;

import androidx.databinding.BaseObservable;
import androidx.databinding.BindingAdapter;
import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;
import androidx.lifecycle.ViewModel;

import com.example.weather7.DayAdapter;
import com.example.weather7.model.City;

import java.util.Observable;

public class ItemCityViewModel extends BaseObservable {

    private City city;
    private ObservableField<String> name_city;
    private ObservableField<String> date;
    private ObservableField<Bitmap> icon;
    private ObservableField<String> temp;
    public ObservableBoolean expandable;


    public ObservableField<String> getName_city() {
        return name_city;
    }
    public ObservableField<String> getDate() {
        return date;
    }
    public ObservableField<String> getTemp() {
        return temp;
    }
    public ObservableField<Bitmap> getIcon() {
        return icon;
    }
    public ObservableBoolean getExpandable() {
        return expandable;
    }

    @BindingAdapter("android:src")
    public static void loadIcon(ImageView iv, Bitmap bitmap) {
        iv.setImageBitmap(bitmap);
    }

    public ItemCityViewModel(City city){

        // значения для шапки
        this.city=city;
        this.name_city=new ObservableField<>(city.getName());
        this.date=new ObservableField<>(city.getCurrent_date());
        this.icon=new ObservableField<>(city.getCurrent_icon());
        this.temp=new ObservableField<>(city.getCurrent_temp());

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
