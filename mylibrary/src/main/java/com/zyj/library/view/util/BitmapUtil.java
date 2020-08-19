package com.zyj.library.view.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Environment;
import android.view.View;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by adu on 2020/6/29.
 */
public class BitmapUtil {
    /**
     * View转Bitmap
     *
     * @param view
     * @return
     */
    public static Bitmap ViewToBitmap(View view) {
        view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
                ,  View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        view.layout(0,0,view.getMeasuredWidth(),view.getMeasuredHeight());
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap b = Bitmap.createBitmap(view.getDrawingCache());
        view.setDrawingCacheEnabled(false);
        view.buildDrawingCache(false);
        return b;
    }

    /**
     * 压缩bitmap 解决微信分享bitmap不能超过32kb的问题
     */
    public static byte[] BitmapToByteArray(final Bitmap bmp,
                                           final boolean needRecycle) {
        int i;
        int j;
        if (bmp.getHeight() > bmp.getWidth()) {
            i = bmp.getWidth();
            j = bmp.getWidth();
        } else {
            i = bmp.getHeight();
            j = bmp.getHeight();
        }

        Bitmap localBitmap = Bitmap.createBitmap(i, j, Bitmap.Config.RGB_565);
        Canvas localCanvas = new Canvas(localBitmap);

        while (true) {
            localCanvas.drawBitmap(bmp, new Rect(0, 0, i, j), new Rect(0, 0, i,
                    j), null);
            if (needRecycle)
                bmp.recycle();
            ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
            localBitmap.compress(Bitmap.CompressFormat.JPEG, 100,
                    localByteArrayOutputStream);
            localBitmap.recycle();
            byte[] arrayOfByte = localByteArrayOutputStream.toByteArray();
            try {
                localByteArrayOutputStream.close();
                return arrayOfByte;
            } catch (Exception e) {
                // F.out(e);
            }
            i = bmp.getHeight();
            j = bmp.getHeight();
        }
    }

    /**
     * 改变图片颜色 变成纯色图片
     *
     * @param mBitmap
     * @param mColor
     * @return
     */
    public static Bitmap getAlphaBitmap(Bitmap mBitmap, int mColor) {
        try {
            Bitmap mAlphaBitmap = Bitmap.createBitmap(mBitmap.getWidth(), mBitmap.getHeight(), Bitmap.Config.ARGB_8888);

            Canvas mCanvas = new Canvas(mAlphaBitmap);
            Paint mPaint = new Paint();

            mPaint.setColor(mColor);
            //从原位图中提取只包含alpha的位图
            Bitmap alphaBitmap = mBitmap.extractAlpha();
            //在画布上（mAlphaBitmap）绘制alpha位图
            mCanvas.drawBitmap(alphaBitmap, 0, 0, mPaint);

            return mAlphaBitmap;
        } catch (Exception e) {
            e.printStackTrace();
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @param drawableId R.drawable.xx
     * @param colorId    R.color.xx
     * @return
     */
    public static Bitmap getAlphaBitmap(Context context, int drawableId, int colorId) {
        return getAlphaBitmap(BitmapFactory.decodeResource(context.getResources(), drawableId), context.getResources().getColor(colorId));
    }

    public static File compressImage(Context context, Bitmap bitmap, String filename) {
        File file = null;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
            int options = 100;

//        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
//        Date date = new Date(System.currentTimeMillis());
//        //图片名
//        String filename = format.format(date);
            String dir = Environment.getExternalStorageDirectory() + "/" + context.getPackageName() + "/images/";
            if (!new File(dir).exists()) {
                new File(dir).mkdirs();
            }
            file = new File(dir, filename);
            try {
                FileOutputStream fos = new FileOutputStream(file);
                try {
                    fos.write(baos.toByteArray());
                    fos.flush();
                    fos.close();
                } catch (IOException e) {

                    e.printStackTrace();
                }
            } catch (FileNotFoundException e) {

                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file;
    }

    public static Bitmap drawBg4Bitmap(int color, Bitmap orginBitmap) {
        Paint paint = new Paint();
        paint.setColor(color);
        Bitmap bitmap = Bitmap.createBitmap(orginBitmap.getWidth(),
                orginBitmap.getHeight(), orginBitmap.getConfig());
        Canvas canvas = new Canvas(bitmap);
        canvas.drawRect(0, 0, orginBitmap.getWidth(), orginBitmap.getHeight(), paint);
        canvas.drawBitmap(orginBitmap, 0, 0, paint);
        return bitmap;
    }

}
