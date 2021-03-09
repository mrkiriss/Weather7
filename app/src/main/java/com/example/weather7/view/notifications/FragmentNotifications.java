package com.example.weather7.view.notifications;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.example.weather7.R;
import com.example.weather7.database.AppDatabase;
import com.example.weather7.databinding.FragmentNotificationsBinding;
import com.example.weather7.model.notifications.Notification;
import com.example.weather7.model.notifications.WeatherNotificationReceiver;
import com.example.weather7.model.notifications.AlarmRequest;
import com.example.weather7.repository.NotificationRepository;
import com.example.weather7.repository.RepositoryRequest;
import com.example.weather7.view.cities.adapters.CitiesAdapter;
import com.example.weather7.view.notifications.adapters.NotificationsAdapter;
import com.example.weather7.viewmodel.notifications.NotificationsViewModel;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import java.util.ArrayList;
import java.util.Date;

public class FragmentNotifications extends Fragment {

    private NotificationsViewModel notificationsViewModel;
    private FragmentNotificationsBinding binding;
    private NotificationsAdapter notificationsAdapter;

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
        notificationsViewModel = new NotificationsViewModel(new NotificationRepository(db.getCityDao(), db.getNotificationDao()));
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_notifications, container, false);
        binding.setViewModel(notificationsViewModel);

        setupNotificationsRecyclerView(binding.notificationsRecycleView);
        createDateAndTimePickers();

        // подписываемся на изменение списка имён для spinner
        notificationsViewModel.getContentOfCitiesSpinner().observe(getViewLifecycleOwner(), this::setContentOfCitiesSpinner);
        // подписываемся на вызов окон для выбора даты и времени
        notificationsViewModel.getCategoryOfStartingPicker().observe(getViewLifecycleOwner(), this::showPicker);
        // подписываемся на переменную, для вызова intent
        notificationsViewModel.getStartAlarmCreation().observe(getViewLifecycleOwner(), this::createAlarmTask);
        // подписываемся на toast контент
        notificationsViewModel.getToastContent().observe(getViewLifecycleOwner(), content -> Toast.makeText(getContext(),content,Toast.LENGTH_SHORT).show());
        // подписываемся на добавление макета уведомления в пользовательский интерфейс
        notificationsViewModel.getAddNotificationDataRequest().observe(getViewLifecycleOwner(), this::addNotificationData);
        // подписываемся на удаление макета уведомления в пользовательский интерфейс
        notificationsViewModel.getDeleteNotificationDataRequest().observe(getViewLifecycleOwner(), this::deleteNotificationData);
        // подписываемся на установку списка с уведомлениями в адаптер
        notificationsViewModel.getSetArrayOfNotifications().observe(getViewLifecycleOwner(), this::setArrayOfNotifications);


        return binding.getRoot();
    }

    private void setArrayOfNotifications(ArrayList<Notification> notifications){
        notificationsAdapter.setNotifications(notifications);
    }
    private void addNotificationData(Notification notification){
        int index = notificationsAdapter.addNotification(notification);
        notificationsAdapter.notifyItemInserted(index);

    }
    private void deleteNotificationData(Notification notification){
        int index = notificationsAdapter.deleteNotification(notification);
        notificationsAdapter.notifyItemRemoved(index);

        cancelNotification(notification.getActionID());
    }
    private void cancelNotification(String actionID){
        Intent intent = new Intent(getContext(), WeatherNotificationReceiver.class);
        intent.setAction(actionID);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(),
                AlarmRequest.PENDING_INTENT_REQUEST_CODE_BASE, intent, AlarmRequest.PENDING_INTENT_FLAG);
        alarmManager.cancel(pendingIntent);
    }
    private void createAlarmTask(AlarmRequest alarmRequest){
        Intent intent = alarmRequest.getIntent();
        intent.setClass(getContext(), WeatherNotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent
                .getBroadcast(getContext(), alarmRequest.getRequestCode(), intent, AlarmRequest.PENDING_INTENT_FLAG);
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
                createDateAndTimePickers();
            }
        });

        datePicker.addOnDismissListener(dialog -> createDateAndTimePickers());

        timePicker = new MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_24H)
                .setInputMode(MaterialTimePicker.INPUT_MODE_CLOCK)
                .setTitleText("Выбор времени уведомления")
                .build();

        timePicker.addOnPositiveButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notificationsViewModel.setSelectedTimeContent(new int[]{timePicker.getHour(), timePicker.getMinute()});
                createDateAndTimePickers();
            }
        });

        timePicker.addOnDismissListener(dialog -> createDateAndTimePickers());

    }
    private void showPicker(Integer id){
        switch (id) {
            case R.id.selectedDate:
                if (datePicker.isAdded()) return;
                datePicker.show(getChildFragmentManager(), "datePicker");
                break;
            case R.id.selectedTime:
                if (timePicker.isAdded()) return;
                timePicker.show(getChildFragmentManager(), "timePicker");
                break;
        }
    }
    private void setContentOfCitiesSpinner(ArrayList<String> content){
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, content);

        binding.cityName.setAdapter(adapter);
    }

    private void setupNotificationsRecyclerView(RecyclerView recyclerView) {
        notificationsAdapter = new NotificationsAdapter();
        recyclerView.setAdapter(notificationsAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // подписываемся на обновление запроса от ОПРЕДЕЛЁННОГО ГОРОДА
        notificationsAdapter.getRequest().observe(getViewLifecycleOwner(), new Observer<RepositoryRequest>() {
            @Override
            public void onChanged(RepositoryRequest req) {
                notificationsViewModel.processRequest(req);
            }
        });
    }

    public static interface AlarmManagerGetter{
        AlarmManager getAlarmManager();
    }
}