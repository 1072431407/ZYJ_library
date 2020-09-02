package com.zyj.library.view.pulltorefresh;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.core.view.NestedScrollingParent2;

public class PullToRefreshLinearLayout extends PullToRefreshBase<LinearLayout> implements NestedScrollingParent2 {

    private boolean isReadyForPullDown;

    public PullToRefreshLinearLayout(Context context) {
        super(context);
    }

    public PullToRefreshLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PullToRefreshLinearLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected LinearLayout createRefreshableView(Context context, AttributeSet attrs) {
        return new LinearLayout(context, attrs);
    }

    @Override
    protected LoadingLayout createHeaderLoadingLayout(Context context, AttributeSet attrs) {
        return new RotateLoadingLayout(context);
    }

    @Override
    protected boolean isReadyForPullDown() {
        return mRefreshableView.getScrollY() == 0 && isReadyForPullDown;
    }

    @Override
    protected boolean isReadyForPullUp() {
        return false;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN)
            isReadyForPullDown = true;
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onStartNestedScroll(@NonNull View child, @NonNull View target, int axes, int type) {
        isReadyForPullDown = false;
        return true;
    }

    @Override
    public void onNestedScrollAccepted(@NonNull View child, @NonNull View target, int axes, int type) {

    }

    @Override
    public void onStopNestedScroll(@NonNull View target, int type) {

    }

    @Override
    public void onNestedScroll(@NonNull View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int type) {
        if (dyUnconsumed < 0) {
            requestDisallowInterceptTouchEvent(false);
            isReadyForPullDown = true;
        }
    }

    @Override
    public void onNestedPreScroll(@NonNull View target, int dx, int dy, @NonNull int[] consumed, int type) {
    }
}
