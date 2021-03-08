package com.example.weather7.view;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.room.Room;

import com.example.weather7.R;
import com.example.weather7.database.AppDatabase;
import com.example.weather7.databinding.FragmentNotificationsBinding;
import com.example.weather7.model.notifications.WeatherNotificationReceiver;
import com.example.weather7.model.notifications.AlarmRequest;
import com.example.weather7.repository.NotificationRepository;
import com.example.weather7.viewmodel.NotificationsViewModel;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import java.util.ArrayList;

public class FragmentNotifications extends Fragment {

    private NotificationsViewModel notificationsViewModel;
    private FragmentNotificationsBinding binding;

    private AlarmManager alarmManager;

    private MutableLiveData<Long> date =new MutableLiveData<>();

    private MaterialTimePicker timePicker;
    private MaterialDatePicker<Long> datePicker;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        alarmManager=((AlarmManagerGetter) context).getAlarmManager();
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        AppDatabase db = Room.databaseBuilder(getContext(),
                AppDatabase.class, "database")
                .allowMainThreadQueries()
                .fallbackToDestructiveMigration()
                .build();
        notificationsViewModel = new NotificationsViewModel(new NotificationRepository(db.getCityDao()));
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_notifications, container, false);
        binding.setViewModel(notificationsViewModel);

        createDateAndTimePickers();

        // подписываемся на изменение списка имён для spinner
        notificationsViewModel.getContentOfCitiesSpinner().observe(getViewLifecycleOwner(), this::setContentOfCitiesSpinner);
        // подписываемся на вызов окон для выбора даты и времени
        notificationsViewModel.getCategoryOfStartingPicker().observe(getViewLifecycleOwner(), this::showPicker);
        // подписываемся на переменную, для вызова intent
        notificationsViewModel.getStartAlarmCreation().observe(getViewLifecycleOwner(), this::createAlarmTask);

        return binding.getRoot();
    }

    private void createAlarmTask(AlarmRequest alarmRequest){
        Intent intent = alarmRequest.getIntent();
        intent.setClass(getContext(), WeatherNotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent
                .getBroadcast(getContext(), alarmRequest.getRequestCode(), intent, alarmRequest.getFlag());
        if (alarmRequest.getInterval() == AlarmRequest.INTERVAL_DAY) {
            alarmManager.setRepeating(alarmRequest.getAlarmType(),
                    alarmRequest.getTriggerTime(), alarmRequest.getInterval(), pendingIntent);
        } else if (alarmRequest.getInterval() == AlarmRequest.INTERVAL_NONE || alarmRequest.getInterval() == AlarmRequest.INTERVAL_SPECIFIC_DATE) {
            alarmManager.set(alarmRequest.getAlarmType(),
                    alarmRequest.getTriggerTime(), pendingIntent);
        }

    }
    private void createDateAndTimePickers(){
        datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Выбор даты уведомления")
                .build();

        datePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Long>() {
            @Override
            public void onPositiveButtonClick(Long selection) {
                notificationsViewModel.setSelectedDateContent(selection);
            }
        });

        timePicker = new MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_24H)
                .setInputMode(MaterialTimePicker.INPUT_MODE_CLOCK)
                .setTitleText("Выбор времени уведомления")
                .build();

        timePicker.addOnPositiveButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notificationsViewModel.setSelectedTimeContent(new int[]{timePicker.getHour(), timePicker.getMinute()});
            }
        });

    }
    private void showPicker(Integer id){
        switch (id) {
            case R.id.selectedDate:
                datePicker.show(getChildFragmentManager(), "datePicker");
                break;
            case R.id.selectedTime:
                timePicker.show(getChildFragmentManager(), "timePicker");
                break;
        }
    }
    private void setContentOfCitiesSpinner(ArrayList<String> content){
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, content);

        binding.cityName.setAdapter(adapter);
    }

    public static interface AlarmManagerGetter{
        AlarmManager getAlarmManager();
    }
}