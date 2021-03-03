package com.example.weather7.model;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.LinkedList;
import java.util.List;

public class DelayMessage {
    private long MAX_DELAY_TIME=1000;

    private MutableLiveData<List<AutoEnteredCity>> result;
    private MutableLiveData<Boolean> names_cities_loading;

    private boolean isWaiting;
    private boolean isDeprecated;

    public DelayMessage(){

    }

    public void processMessage(Runnable function){
        if (isWaiting){
            isDeprecated=true;
        }

        Runnable task = () -> {
            isWaiting=true;
            // ожидание
            try {
                Thread.sleep(MAX_DELAY_TIME);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            isWaiting=false;

            // есть ли аткуальный аналог?
            if (isDeprecated) {
                isDeprecated=false;
                return;
            }

            // запуск требуемой функции
            function.run();
        };

        Thread thr = new Thread(task);
        thr.start();
    }

    private LiveData<List<AutoEnteredCity>> getResult(){return result;}
    public MutableLiveData<Boolean> getNames_cities_loading() {
        return names_cities_loading;
    }
}
