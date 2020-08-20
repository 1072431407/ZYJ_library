package com.zyj.library.listnenr;

import android.view.View;

public abstract class onNoClickListener implements View.OnClickListener {
    private final long timeLength = 600;
    private long startTime = System.currentTimeMillis();
    private long overTime = 0;
    @Override
    public void onClick(View view) {
        startTime = overTime;
        overTime = System.currentTimeMillis();
        if (overTime-startTime>=timeLength) onNoDoubleClick(view);
    }

    protected abstract void onNoDoubleClick(View v);
}
