package com.zyj.library.view;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.TextSwitcher;
import android.widget.ViewSwitcher;

import com.zyj.library.R;

import java.util.ArrayList;
import java.util.List;

/**
 * check by fangzhu
 * 直接使用ViewFlipper是最简单
 */
public class ScrollTextView extends TextSwitcher implements ViewSwitcher.ViewFactory {
    public static final int VERTICAL = 0;
    public static final int HORIZONTAL = 0;

    protected final int FLAG_START_AUTO_SCROLL = 0;
    protected final int FLAG_STOP_AUTO_SCROLL = 1;

    protected float mTextSize = 16;
    protected int mPadding = 5;
    protected int textColor = Color.GREEN;

    /**
     * @param textSize  字号
     * @param padding   内边距
     * @param textColor 字体颜色
     */
    public void setTextStyle(float textSize, int padding, int textColor) {
        mTextSize = textSize;
        mPadding = padding;
        this.textColor = textColor;
    }

    protected OnItemClickListener itemClickListener;
    protected Context mContext;
    protected int currentId = -1;
    protected ArrayList<String> textList;
    protected Handler handler;

    public ScrollTextView(Context context) {
        this(context, null);
        mContext = context;
    }

    public ScrollTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        textList = new ArrayList<String>();
    }

    /**
     * 设置动画
     * @param animDuration 移动时间
     * @param moveLength 移动距离
     * @param type 移动方向 VERTICAL、HORIZONTAL
     */
    public void setAnimTime(long animDuration, int moveLength,int type) {
        if (textList.size() < 2)
            setFactory(this);
        if (type != VERTICAL && type != HORIZONTAL)
            return;

        if (type == VERTICAL){
            Animation in = new TranslateAnimation(0, 0, moveLength, 0);
            in.setDuration(animDuration);
            in.setInterpolator(new AccelerateInterpolator());
            Animation out = new TranslateAnimation(0, 0, 0, -moveLength);
            out.setDuration(animDuration);
            out.setInterpolator(new AccelerateInterpolator());
            setInAnimation(in);
            setOutAnimation(out);
        }
        if (type == HORIZONTAL){
            Animation in = new TranslateAnimation(moveLength, 0, 0, 0);
            in.setDuration(animDuration);
            in.setInterpolator(new AccelerateInterpolator());
            Animation out = new TranslateAnimation(0, -moveLength, 0, 0);
            out.setDuration(animDuration);
            out.setInterpolator(new AccelerateInterpolator());
            setInAnimation(in);
            setOutAnimation(out);
        }

    }

    /**
     * 间隔时间
     *
     * @param time
     */
    public void setTextStillTime(final long time) {
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case FLAG_START_AUTO_SCROLL:
                        if (textList.size() > 0) {
                            currentId++;
                            setText(textList.get(currentId % textList.size()));
                            postInvalidate();
                        }
                        handler.sendEmptyMessageDelayed(FLAG_START_AUTO_SCROLL, time);
                        break;
                    case FLAG_STOP_AUTO_SCROLL:
                        handler.removeMessages(FLAG_START_AUTO_SCROLL);
                        break;
                }
            }
        };
    }

    protected void setText(String s) {
        super.setText(s);
    }

    /**
     * 设置数据源
     *
     * @param titles
     */
    public void setTextList(List<String> titles) {
        textList.clear();
        textList.addAll(titles);
        currentId = -1;
    }

    /**
     * 开始滚动
     */
    public void startAutoScroll() {
        handler.sendEmptyMessage(FLAG_START_AUTO_SCROLL);
    }

    /**
     * 停止滚动
     */
    public void stopAutoScroll() {
        handler.sendEmptyMessage(FLAG_STOP_AUTO_SCROLL);
    }

    //布局文件
    protected int getChildLayout(){
        return R.layout.scroll_textview;
    }
    //内部view 只能有一个
    protected SingleLineZoomTextView getChildLayoutItem(){
        View view = View.inflate(mContext, getChildLayout(), null);
        return view.findViewById(R.id.tv_msg);
    }
    @Override
    public View makeView() {
        //直接new 出来为什么没效果，  直接使用xml加载，只能有一个textview ，不能包含其他
        SingleLineZoomTextView t = getChildLayoutItem();
        t.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemClickListener != null && textList.size() > 0 && currentId != -1) {
                    itemClickListener.onItemClick(currentId % textList.size());
                }
            }
        });
        return t;
    }

    /**
     * 设置点击事件监听
     *
     * @param itemClickListener
     */
    public void setOnItemClickListener(OnItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    /**
     * 轮播文本点击监听器
     */
    public interface OnItemClickListener {
        /**
         * 点击回调
         *
         * @param position 当前点击ID
         */
        void onItemClick(int position);
    }

}