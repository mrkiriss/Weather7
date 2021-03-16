package com.example.weather7.model.factories;

import android.content.Context;

import com.example.weather7.model.notifications.AlarmRequest;
import com.example.weather7.model.notifications.Notification;

public class ThreadFactory implements java.util.concurrent.ThreadFactory {
    @Override
    public Thread newThread(Runnable r) {
        return new Thread(r);
    }

}
