package com.example.weather7.di.modules.notifications;

import com.example.weather7.repository.notifications.NotificationRepository;
import com.example.weather7.viewmodel.notifications.NotificationsViewModel;

import dagger.Module;
import dagger.Provides;

@Module
public class NotificationsVMModule {

    @Provides
    @FNotificationsScope
    public NotificationsViewModel provideNotificationsVM(NotificationRepository rep){
        return new NotificationsViewModel(rep);
    }
}
