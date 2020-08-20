package com.zyj.library.time;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import java.util.concurrent.TimeUnit;

public class Timer {
    private final int TIMER_END = 1;
    private final int THE_CURRENT_TIME = 2;
    private long timeLength;//时长 秒钟
    private Thread thread;
    private boolean isLock;//线程锁
    private boolean isTimerEnd;//计时是否结束
    private TimerListener timerListener;
    private long step;
    public Timer(){
        step = 1000;
        isLock = false;
    }

    public Timer(long initLength){
        step = 1000;
        isLock = false;
        if (initLength > 0) {
            isTimerEnd = false;
            timeLength = initLength;
        }else{
            isTimerEnd = true;
            timeLength = -1;
        }
    }
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case TIMER_END:
                    thread = null;
                    timeLength = 0;
                    if (timerListener != null)
                        timerListener.onTimerEnd();
                    break;
                case THE_CURRENT_TIME:
                    if (timerListener != null){
                        Bundle bundle = msg.getData();
                        timerListener.onUpDate(bundle.getLong("data"));
                    }
                    break;
            }

        }
    };

    public boolean isTimerEnd() {
        return isTimerEnd;
    }

    public void setTimerListener(TimerListener timerListener) {
        this.timerListener = timerListener;
    }

    //开始
    public void start(){
        if (thread != null) return;
        isLock = true;
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                if (timerListener != null) timerListener.onTimerStart();
                while (true){
                    if (isLock){
                        long startTime = System.currentTimeMillis();
                        Message message = new Message();
                        if (timeLength <= 0){
                            isTimerEnd = true;
                            message.what = TIMER_END;
                            handler.sendMessage(message);
                            break;
                        }else{
                            message.what = THE_CURRENT_TIME;
                            Bundle bundle = new Bundle();
                            bundle.putLong("data",timeLength);
                            message.setData(bundle);
                            handler.sendMessage(message);
                        }
                        long overTime = System.currentTimeMillis();
                        try {
                            if (step-(overTime - startTime) > 0)
                                TimeUnit.MILLISECONDS.sleep(step-(overTime - startTime));//毫秒
                        } catch (InterruptedException e) { e.printStackTrace();}
                        timeLength -= step;
                    }else{
                        try {
                            TimeUnit.MILLISECONDS.sleep(50);//毫秒
                        } catch (InterruptedException e) {e.printStackTrace();}
                    }
                }
            }
        });

        thread.start();
    }
    //设置步长 默认1000ms
    public void setStep(long step){
        this.step = step;
    }
    //写入计时总长度
    public void setTimeLength(long timeLength) {
        if (timeLength > 0) {
            isTimerEnd = false;
            this.timeLength = timeLength;
        }else{
            isTimerEnd = true;
            this.timeLength = -1;
        }
    }
    //添加计时总长度
    public void addTimerLength(long addLength){
        timeLength += addLength;
    }
    //暂停
    public void pause(){
        isLock = false;
        if (timerListener != null) timerListener.onTimerPause();
    }
    //继续
    public void continues(){
        isLock = true;
        if (timerListener != null) timerListener.onTimerContinues();
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        thread = null;
    }
}
