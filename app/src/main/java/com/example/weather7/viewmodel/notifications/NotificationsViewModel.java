package com.example.weather7.viewmodel.notifications;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.databinding.BindingAdapter;
import androidx.databinding.InverseBindingAdapter;
import androidx.databinding.InverseBindingListener;
import androidx.databinding.Observable;
import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.weather7.model.notifications.AlarmRequest;
import com.example.weather7.model.notifications.Notification;
import com.example.weather7.repository.NotificationRepository;
import com.example.weather7.repository.RepositoryRequest;
import com.example.weather7.utils.DateConverter;

import java.util.ArrayList;

public class NotificationsViewModel extends ViewModel {

    public static final String REPEAT_MODE_NONREPEATING="Без повторений";
    public static final String REPEAT_MODE_CONCRETE_DATE="В определённый день";
    public static ObservableBoolean onNotificationsChanged = new ObservableBoolean(true);
    public static ObservableBoolean onCitiesSpinnerChanged = new ObservableBoolean(true);

    private NotificationRepository rep;

    private ObservableField<String> selectedCity;
    private ObservableField<String> selectedRepeatMode;
    private ObservableField<String> selectedDateContent;
    private ObservableField<String> selectedTimeContent;

    private MutableLiveData<ArrayList<String>> contentOfCitiesSpinner;
    private MutableLiveData<Integer> categoryOfStartingPicker;
    private MutableLiveData<AlarmRequest> startAlarmCreation;
    private MutableLiveData<ArrayList<Notification>> setArrayOfNotifications;

    private LiveData<String> toastContent;
    private LiveData<Notification> addNotificationDataRequest;
    private LiveData<Notification> deleteNotificationDataRequest;

    public NotificationsViewModel(NotificationRepository rep){
        this.rep=rep;

        this.selectedTimeContent=new ObservableField<>();
        this.selectedDateContent=new ObservableField<>();
        this.selectedCity=new ObservableField<>();
        this.selectedRepeatMode=new ObservableField<>();

        this.contentOfCitiesSpinner=new MutableLiveData<>();
        this.categoryOfStartingPicker=new MutableLiveData<>();
        this.startAlarmCreation=new MutableLiveData<>();
        this.setArrayOfNotifications=new MutableLiveData<>();

        this.toastContent=rep.getToastContent();
        this.addNotificationDataRequest=rep.getAddNotificationDataRequest();
        this.deleteNotificationDataRequest=rep.getDeleteNotificationDataRequest();

        initOnChangeNotifications();
        initOnChangeCitiesSpinner();

        resetFieldValues();
        fillCitiesSpinner();
        rep.fillingNotifications();
    }

    public void onPickerClick(View view){
        categoryOfStartingPicker.setValue(view.getId());
    }

    public void onScheduleNotification(){
        if(selectedCity.get()==null || rep.someoneAdderActive()) return;

        // получение актуальных данных
        String cityName = selectedCity.get();
        String repeatMode = selectedRepeatMode.get();
        String date = selectedDateContent.get();
        if (selectedRepeatMode.equals(REPEAT_MODE_NONREPEATING)) date = DateConverter.getCurrentDate();
        String time = selectedTimeContent.get();

        //resetFieldValues();

        // отправка запроса на создание уведомления
        AlarmRequest alarmRequest = new AlarmRequest(rep.getCountOfAlarmTasks());
        alarmRequest.setCityNameInIntent(cityName);
        String recycledData = alarmRequest.setIntervalAndTriggerTime(repeatMode, date, time);
        startAlarmCreation.setValue(alarmRequest);

        // создание макета запроса в базе данных, отображение на экране
        rep.addNotificationToViewAndBase(cityName, repeatMode, recycledData, time);
    }

    public void processRequest(RepositoryRequest req){
        rep.onRepositoryRequest(req);
    }

    private void fillCitiesSpinner(){
        contentOfCitiesSpinner.postValue(rep.getNamesOfCities());
    }
    private void resetFieldValues(){
        selectedDateContent.set(DateConverter.getCurrentDate());
        selectedTimeContent.set(DateConverter.getCurrentTime());
    }
    private void initOnChangeNotifications(){
        onNotificationsChanged.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                if (!onNotificationsChanged.get()) return;

                setArrayOfNotifications.setValue(new ArrayList<>());

                rep.fillingNotifications();
            }
        });
    }
    private void initOnChangeCitiesSpinner(){
        onCitiesSpinnerChanged.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                if (!onCitiesSpinnerChanged.get()) return;

                fillCitiesSpinner();
            }
        });
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
    public LiveData<String> getToastContent() {
        return toastContent;
    }
    public LiveData<Notification> getAddNotificationDataRequest() {
        return addNotificationDataRequest;
    }
    public LiveData<Notification> getDeleteNotificationDataRequest(){return deleteNotificationDataRequest;}
    public MutableLiveData<ArrayList<Notification>> getSetArrayOfNotifications(){return setArrayOfNotifications;}



}