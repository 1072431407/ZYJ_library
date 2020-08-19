package com.zyj.library.window;

import android.app.Activity;
import android.os.Build;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;


/**
 * 6.0以上 才处理的逻辑  以下版本 还是由系统自己处理
 */

public class StatusBarUtil {

    /**
     * 设置状态栏的颜色 6.0以上
     * @param activity
     * @param colorResId　R.color.ss
     */
    public static void setWindowStatusBarColor(Activity activity, int colorResId) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Window window = activity.getWindow();
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(activity.getResources().getColor(colorResId));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 颜色为light模式  字体和icon为黑色
     * View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR  6.0以上才处理显示
     * @param activity
     */
    public static void statusBarLightMode(Activity activity) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } catch (Error e) {
            e.printStackTrace();
        }
    }

}
