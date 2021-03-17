package com.example.weather7.utils;

import com.example.weather7.model.factories.ThreadFactory;

public class DelayMessageManager {
    private final long MAX_DELAY_TIME=1000;
    private int countActiveMessage=0;



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


    public boolean someoneActive() {
        return (countActiveMessage>0?true:false);
    }
    public void addToCountActiveCity(int x){countActiveMessage+=x;}
}
