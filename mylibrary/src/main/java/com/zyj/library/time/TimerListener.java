package com.zyj.library.time;

/**
 * 给Timer用
 */
public interface TimerListener {
    void onTimerStart();//开始
    void onTimerPause();//暂停
    void onTimerContinues();//继续
    void onUpDate(final long data);//数据更新单位ms
    void onTimerEnd();//计时结束
}
