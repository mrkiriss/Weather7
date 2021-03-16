package com.example.weather7.utils;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.weather7.model.factories.ThreadFactory;
import com.example.weather7.model.cities.AutoEnteredCity;

import java.util.List;

public class DelayMessageManager {
    private final long MAX_DELAY_TIME=650;
    private int countActiveMessage=0;

    private MutableLiveData<List<AutoEnteredCity>> result;
    private MutableLiveData<Boolean> names_cities_loading;

    private boolean isWaiting;
    private volatile int numberOfDeprecated;

    private ThreadFactory threadFactory;

    public DelayMessageManager(ThreadFactory threadFactory){this.threadFactory=threadFactory;}

    public void processMessage(Runnable function){
        if (isWaiting){
            numberOfDeprecated++;
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
            if (numberOfDeprecated>0) {
                numberOfDeprecated--;
                return;
            }
            // запуск требуемой функции
            function.run();
        };

        threadFactory.newThread(task).start();
    }

    private LiveData<List<AutoEnteredCity>> getResult(){return result;}
    public MutableLiveData<Boolean> getNames_cities_loading() {
        return names_cities_loading;
    }
    public boolean someoneActive() {
        return (countActiveMessage>0?true:false);
    }
    public void addToCountActiveCity(int x){countActiveMessage+=x;}
}
