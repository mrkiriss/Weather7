package com.example.weather7.view.notifications;

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

import com.example.weather7.R;
import com.example.weather7.databinding.FragmentNotificationsBinding;
import com.example.weather7.di.App;
import com.example.weather7.model.notifications.Notification;
import com.example.weather7.repository.RepositoryRequest;
import com.example.weather7.view.notifications.adapters.NotificationsAdapter;
import com.example.weather7.viewmodel.notifications.NotificationsViewModel;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import java.util.ArrayList;

import javax.inject.Inject;

public class FragmentNotifications extends Fragment {

    @Inject
    NotificationsViewModel notificationsViewModel;

    private FragmentNotificationsBinding binding;
    private NotificationsAdapter notificationsAdapter;

    private MutableLiveData<Long> date =new MutableLiveData<>();

    private MaterialTimePicker timePicker;
    private MaterialDatePicker<Long> datePicker;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        App.getInstance().getComponentManager().getFNotificationsSubcomponent().inject(this);

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_notifications, container, false);
        binding.setViewModel(notificationsViewModel);

        setupNotificationsRecyclerView(binding.notificationsRecycleView);
        createDateAndTimePickers();
        setupRepeatModeSpinner();

        // подписываемся на изменение списка имён для spinner
        notificationsViewModel.getContentOfCitiesSpinner().observe(getViewLifecycleOwner(), this::setContentOfCitiesSpinner);
        // подписываемся на вызов окон для выбора даты и времени
        notificationsViewModel.getCategoryOfStartingPicker().observe(getViewLifecycleOwner(), this::showPicker);
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
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), R.layout.my_list_item, content);

        binding.cityName.setAdapter(adapter);
    }
    private void setupRepeatModeSpinner(){
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),R.layout.my_list_item, getResources().getStringArray(R.array.repeatArray));
        binding.repaetMode.setAdapter(adapter);
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // как фича: разрешить пользователю настраивать пересоздание экранов
        App.getInstance().getComponentManager().clearFNotificationsSubcomponent();
    }
}