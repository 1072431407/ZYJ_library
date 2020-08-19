package com.zyj.libiary.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;

import androidx.appcompat.widget.AppCompatTextView;

import com.zyj.libiary.view.util.DimenUtil;

/**
 * 自动调整文字大小，只显示一行
 * 默认只能由打字号自动变为小字号
 * 如需要由小字号变为大字号需写入setTextSizeInfo（）
 */
public class SingleLineZoomTextView extends AppCompatTextView {

    private Paint mPaint;
    private float mTextSize;
    private float variation = 0;
    private float maxSize;
    private Context context;
    public SingleLineZoomTextView(Context context) {
        super(context);
        this.context = context;
    }
    public SingleLineZoomTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }
    public SingleLineZoomTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
    }
    /**
     * getTextSize 返回值是以像素(px)为单位的，而 setTextSize() 默认是 sp 为单位
     * 因此我们要传入像素单位 setTextSize(TypedValue.COMPLEX_UNIT_PX, size);
     */
    private void refitText(String text, int textWidth) {
        if (textWidth > 0) {
            mTextSize = this.getTextSize();//这个返回的单位为px
            mTextSize += variation;
            mPaint = new Paint();
            mPaint.set(this.getPaint());
            int drawWid = 0;//drawableLeft，Right，Top，Buttom 所有图片的宽
            Drawable[] draws = getCompoundDrawables();
            for (int i = 0; i < draws.length; i++) {
                if(draws[i]!= null){
                    drawWid += draws[i].getBounds().width();
                }
            }
            //获得当前TextView的有效宽度
            int availableWidth = textWidth - this.getPaddingLeft()
                    - this.getPaddingRight()- getCompoundDrawablePadding()- drawWid;
            //所有字符所占像素宽度
            float textWidths = getTextLength(mTextSize, text);
            while(textWidths > availableWidth){
                mPaint.setTextSize(--mTextSize);//这里传入的单位是 px
                textWidths = getTextLength(mTextSize, text);
            }
            if (variation != 0 && mTextSize > maxSize){
                mTextSize = maxSize;
            }
            this.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTextSize);//这里设置单位为 px
        }
    }

    /**
     *
     * @param variation 字号变大多少（这个变量就是为了少循环几次）
     * @param maxSize 最终显示字号上限(sp)
     */
    public void setTextSizeInfo(float variation,float maxSize){
        this.variation = variation;
        this.maxSize = DimenUtil.sp2px(context,maxSize);
    }
    /**
     * @param textSize
     * @param text
     * @return 字符串所占像素宽度
     */
    private float getTextLength(float textSize, String text){
        mPaint.setTextSize(textSize);
        return mPaint.measureText(text);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        refitText(getText().toString(), this.getWidth());
    }

}