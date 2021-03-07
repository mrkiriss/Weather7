package com.example.weather7.viewmodel;

import android.content.Intent;
import android.view.View;
import android.widget.ArrayAdapter;

import androidx.databinding.ObservableField;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.weather7.R;
import com.example.weather7.repository.NotificationRepository;
import com.example.weather7.utils.ConverterDate;

import java.util.ArrayList;

public class NotificationsViewModel extends ViewModel {

    private NotificationRepository rep;

    private ObservableField<String> selectedDateContent;
    private ObservableField<String> selectedTimeContent;

    private MutableLiveData<ArrayList<String>> contentOfCitiesSpinner;
    private MutableLiveData<Integer> categoryOfStartingPicker;

    private LiveData<Intent> startIntent;

    public NotificationsViewModel(NotificationRepository rep){
        this.rep=rep;

        this.contentOfCitiesSpinner=new MutableLiveData<>();
        this.categoryOfStartingPicker=new MutableLiveData<>();
        this.selectedTimeContent=new ObservableField<>();
        this.selectedDateContent=new ObservableField<>();

        this.startIntent=rep.getStartIntent();

        resetFieldValues();
        contentOfCitiesSpinner.setValue(rep.getNamesOfCities());
    }

    public void onPickerClick(View view){
        categoryOfStartingPicker.setValue(view.getId());
    }
    public void onScheduleNotification(){
        rep.createIncompleteAlarmIntent(selectedDateContent.get(), selectedTimeContent.get());
    }

    private void resetFieldValues(){
        selectedDateContent.set(rep.getCurrentDate());
        selectedTimeContent.set(rep.getCurrentTime());
    }

    public MutableLiveData<ArrayList<String>> getContentOfCitiesSpinner(){return contentOfCitiesSpinner;}
    public ObservableField<String> getSelectedDateContent() {
        return selectedDateContent;
    }
    public void setSelectedDateContent(Long selectedDateContent) {
        this.selectedDateContent.set(ConverterDate.convertLongToDMY(selectedDateContent));
    }
    public ObservableField<String> getSelectedTimeContent() {
        return selectedTimeContent;
    }
    public void setSelectedTimeContent(int[] selectedTimeContent) {
        this.selectedTimeContent.set(ConverterDate.convertIntToHM(selectedTimeContent));
    }
    public MutableLiveData<Integer> getCategoryOfStartingPicker() {
        return categoryOfStartingPicker;
    }
    public LiveData<Intent> getStartIntent() {
        return startIntent;
    }

}