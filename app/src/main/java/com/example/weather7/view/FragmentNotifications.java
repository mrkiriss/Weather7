package com.example.weather7.view;

import android.app.UiAutomation;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.Observable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.weather7.R;
import com.example.weather7.databinding.FragmentNotificationsBinding;
import com.example.weather7.viewmodel.NotificationsViewModel;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class FragmentNotifications extends Fragment {

    private NotificationsViewModel notificationsViewModel;
    private FragmentNotificationsBinding binding;

    private MutableLiveData<Long> date =new MutableLiveData<>();
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        notificationsViewModel =
                new ViewModelProvider(this).get(NotificationsViewModel.class);
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_notifications, container, false);


        binding.selectedDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MaterialDatePicker.Builder<Long> builder = MaterialDatePicker.Builder.datePicker();
                builder.setTitleText("Title here");
                MaterialDatePicker<Long> picker = builder.build();
                picker.showNow(getChildFragmentManager(), "tag");
                picker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Long>() {
                    @Override
                    public void onPositiveButtonClick(Long selection) {
                        date.setValue(selection);
                    }
                });
            }
        });

        binding.selectedTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MaterialTimePicker picker1 = new MaterialTimePicker.Builder().setTimeFormat(TimeFormat.CLOCK_24H).
                        setInputMode(MaterialTimePicker.INPUT_MODE_CLOCK).setTitleText("Select Appointment time").build();

                picker1.showNow(getChildFragmentManager(), "tagT");

                picker1.addOnPositiveButtonClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        binding.selectedTime.setText(picker1.getHour()+":"+picker1.getMinute());
                    }
                });
            }
        });

        date.observe(getViewLifecycleOwner(), new Observer<Long>() {
            @Override
            public void onChanged(Long aLong) {
                if (aLong==null) return;
                Date date = new Date(aLong);
                SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd-MM-yyyy");

                sdf.setTimeZone(TimeZone.getDefault());
                String result = sdf.format(date);
                binding.selectedDate.setText(result);
            }
        });

        return binding.getRoot();
    }
}