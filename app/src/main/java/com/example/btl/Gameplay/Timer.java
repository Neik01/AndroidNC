package com.example.btl.Gameplay;

import android.os.CountDownTimer;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Timer {

    private static Timer instance;
    private CountDownTimer countDownTimer;
    private long timeRemaining;

    private ScheduledExecutorService scheduler;

    private TimerListener listener;

    private Timer() {
        // Private constructor to prevent direct instantiation
    }

    public static synchronized Timer getInstance() {
        if (instance == null) {
            instance = new Timer();
        }
        return instance;
    }


    public void startTimer(long timeInMillis, TimerListener listener) {
        if (scheduler != null) {
            cancelTimer();
        }

        this.listener = listener;
        timeRemaining = timeInMillis;
        scheduler = Executors.newSingleThreadScheduledExecutor();

        scheduler.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                timeRemaining -= 1000;
                if (timeRemaining >=0) {
                    listener.onTick(timeRemaining);
                } else {
                    listener.onFinish();
                    cancelTimer();
                }
            }
        }, 0, 1000, TimeUnit.MILLISECONDS);
    }

    public void cancelTimer() {
        if (scheduler != null) {
            scheduler.shutdownNow();
            scheduler = null;
        }
    }


    public long getTimeRemaining() {
        return timeRemaining;
    }

    public interface TimerListener {
        void onTick(long millisUntilFinished);

        void onFinish();
    }
}