package com.example.weather7.di.components;

import com.example.weather7.di.modules.notifications.FNotificationsScope;
import com.example.weather7.di.modules.notifications.NotificationRepositoryModule;
import com.example.weather7.di.modules.notifications.NotificationsVMModule;
import com.example.weather7.view.notifications.FragmentNotifications;

import dagger.Subcomponent;

@Subcomponent(modules = {NotificationRepositoryModule.class, NotificationsVMModule.class})
@FNotificationsScope
public interface FNotificationsSubcomponent {

    void inject(FragmentNotifications fragmentNotifications);
}
