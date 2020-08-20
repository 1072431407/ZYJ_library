package com.zyj.library.time;

public abstract class onTimerListener implements TimerListener {
    private long hour = 0;
    private long minute = 0;
    private long second = 0;
    @Override
    public void onTimerStart() {
    }

    @Override
    public void onTimerPause() {
    }

    @Override
    public void onTimerContinues() {
    }

    @Override
    public void onUpDate(long data) {
        long ms = data/1000;
        if (ms != second) {
            second = ms;
            onSeconds(ms);
        }
        if (ms / 60 != minute) {
            minute = ms / 60;
            onMinutes(minute);
        }
        if (ms / 60 / 60 != hour) {
            hour = ms / 60 / 60;
            onHour(hour);
        }
    }

    @Override
    public void onTimerEnd() {
    }

    public abstract void onHour(long hour);
    public abstract void onMinutes(long minute);
    public abstract void onSeconds(long second);
}
