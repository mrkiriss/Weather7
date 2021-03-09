package com.example.weather7.view.notifications.adapters;


import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weather7.R;
import com.example.weather7.databinding.ItemCityBinding;
import com.example.weather7.databinding.ItemNotificationBinding;
import com.example.weather7.model.cities.City;
import com.example.weather7.model.notifications.Notification;
import com.example.weather7.repository.RepositoryRequest;
import com.example.weather7.viewmodel.cities.items.ItemCityViewModel;
import com.example.weather7.viewmodel.notifications.items.ItemNotificationViewModel;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.LinkedList;

public class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.NotificationViewHolder> {

    private ArrayList<Notification> notifications;
    private MutableLiveData<RepositoryRequest> request;

    public NotificationsAdapter(){
        this.notifications= new ArrayList<>();
        this.request=new MutableLiveData<>();
    }

    @NotNull
    @Override
    public NotificationViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        ItemNotificationBinding binding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.item_notification, parent, false);

        return new NotificationViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(NotificationViewHolder holder, int position){
        holder.bindNotification(notifications.get(position));
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    public MutableLiveData<RepositoryRequest> getRequest(){return request;}
    public void setNotifications(ArrayList<Notification> notifications){
        this.notifications=notifications;
        notifyDataSetChanged();
    }

    public int addNotification(Notification notification){
        this.notifications.add(notification);
        return this.notifications.size()-1;
    }
    public int deleteNotification(Notification this_notification){
        for (int i=0;i<notifications.size();i++){
            Notification notification = notifications.get(i);
            if (notification.getActionID().equals(this_notification.getActionID())){
                notifications.remove(notification);
                return i;
            }
        }
        return -1;
    }

    public class NotificationViewHolder extends RecyclerView.ViewHolder{
        ItemNotificationBinding binding;

        public NotificationViewHolder(ItemNotificationBinding binding) {
            super(binding.cardView);
            this.binding=binding;
        }

        void bindNotification(Notification notification){
            binding.setViewModel(new ItemNotificationViewModel(notification, request));
        }

    }

}
