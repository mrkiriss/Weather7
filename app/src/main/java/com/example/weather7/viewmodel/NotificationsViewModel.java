package com.example.weather7.viewmodel;

import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.databinding.BindingAdapter;
import androidx.databinding.InverseBindingAdapter;
import androidx.databinding.InverseBindingListener;
import androidx.databinding.ObservableField;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.weather7.R;
import com.example.weather7.model.notifications.AlarmRequest;
import com.example.weather7.repository.NotificationRepository;
import com.example.weather7.utils.DateConverter;

import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

public class NotificationsViewModel extends ViewModel {

    private NotificationRepository rep;

    private ObservableField<String> selectedCity;
    private ObservableField<String> selectedRepeatMode;
    private ObservableField<String> selectedDateContent;
    private ObservableField<String> selectedTimeContent;

    private MutableLiveData<ArrayList<String>> contentOfCitiesSpinner;
    private MutableLiveData<Integer> categoryOfStartingPicker;
    private MutableLiveData<AlarmRequest> startAlarmCreation;

    public NotificationsViewModel(NotificationRepository rep){
        this.rep=rep;

        this.selectedTimeContent=new ObservableField<>();
        this.selectedDateContent=new ObservableField<>();
        this.selectedCity=new ObservableField<>();
        this.selectedRepeatMode=new ObservableField<>();

        this.contentOfCitiesSpinner=new MutableLiveData<>();
        this.categoryOfStartingPicker=new MutableLiveData<>();
        this.startAlarmCreation=new MutableLiveData<>();

        resetFieldValues();
        contentOfCitiesSpinner.setValue(rep.getNamesOfCities());
    }

    public void onPickerClick(View view){
        categoryOfStartingPicker.setValue(view.getId());
    }
    public void onScheduleNotification(){
        resetFieldValues();

        AlarmRequest alarmRequest = new AlarmRequest(String.valueOf(rep.getCountOfAlarmTasks()+1));
        alarmRequest.setCityNameInIntent(selectedCity.get());
        alarmRequest.setIntervalAndTriggerTime(Objects.requireNonNull(selectedRepeatMode.get()), selectedDateContent.get(), selectedTimeContent.get());
        startAlarmCreation.setValue(alarmRequest);
    }

    private void resetFieldValues(){
        selectedDateContent.set(DateConverter.getCurrentDate());
        selectedTimeContent.set(DateConverter.getCurrentTime());
    }

    @BindingAdapter(value = {"selectedValue", "selectedValueAttrChanged"}, requireAll = false)
    public static void bindSpinnerData(Spinner spinner, String newSelectedValue, final InverseBindingListener newTextAttrChanged) {
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                newTextAttrChanged.onChange();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        if (newSelectedValue != null) {
            int pos = ((ArrayAdapter<String>) spinner.getAdapter()).getPosition(newSelectedValue);
            spinner.setSelection(pos, true);
        }
    }
    @InverseBindingAdapter(attribute = "selectedValue", event = "selectedValueAttrChanged")
    public static String getSelectedValue(Spinner spinner) {
        return (String) spinner.getSelectedItem();
    }

    public MutableLiveData<ArrayList<String>> getContentOfCitiesSpinner(){return contentOfCitiesSpinner;}
    public ObservableField<String> getSelectedDateContent() {
        return selectedDateContent;
    }
    public void setSelectedDateContent(Long selectedDateContent) {
        this.selectedDateContent.set(DateConverter.convertLongToDMY(selectedDateContent));
    }
    public ObservableField<String> getSelectedTimeContent() {
        return selectedTimeContent;
    }
    public void setSelectedTimeContent(int[] selectedTimeContent) {
        this.selectedTimeContent.set(DateConverter.convertIntToHM(selectedTimeContent));
    }
    public MutableLiveData<Integer> getCategoryOfStartingPicker() {
        return categoryOfStartingPicker;
    }
    public MutableLiveData<AlarmRequest> getStartAlarmCreation() {
        return startAlarmCreation;
    }
    public ObservableField<String> getSelectedCity() {
        return selectedCity;
    }
    public void setSelectedCity(ObservableField<String> selectedCity) {
        this.selectedCity = selectedCity;
    }
    public ObservableField<String> getSelectedRepeatMode() {
        return selectedRepeatMode;
    }
    public void setSelectedRepeatMode(ObservableField<String> selectedRepeatMode) {
        this.selectedRepeatMode = selectedRepeatMode;
    }

}