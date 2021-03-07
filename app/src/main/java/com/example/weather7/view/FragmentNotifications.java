package com.example.weather7.view;

import android.app.UiAutomation;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.Observable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.room.Room;

import com.example.weather7.R;
import com.example.weather7.database.AppDatabase;
import com.example.weather7.database.CityDao;
import com.example.weather7.databinding.FragmentNotificationsBinding;
import com.example.weather7.model.City;
import com.example.weather7.repository.NotificationRepository;
import com.example.weather7.viewmodel.NotificationsViewModel;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.TimeZone;

public class FragmentNotifications extends Fragment {

    private NotificationsViewModel notificationsViewModel;
    private FragmentNotificationsBinding binding;

    private MutableLiveData<Long> date =new MutableLiveData<>();

    private MaterialTimePicker timePicker;
    private MaterialDatePicker<Long> datePicker;

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
        notificationsViewModel.getStartIntent().observe(getViewLifecycleOwner(), new Observer<Intent>() {
            @Override
            public void onChanged(Intent intent) {

                startActivity(finishAlarmIntent(intent));
            }
        });

        return binding.getRoot();
    }

    private Intent finishAlarmIntent(Intent intent){

        return intent;
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
}