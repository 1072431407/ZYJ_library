package com.zyj.library.window;

import android.app.Activity;
import android.os.Build;
import android.view.Window;
import android.view.WindowManager;

import com.zyj.library.lang.StringUtil;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * Created by zhang on 2020/5/7.
 * <p>
 * 刘海屏判断工具类
 */
public class NotchUtil {
    /**
     * 页面收否为异形屏幕，是否使用刘海屏
     *
     * @param activity
     * @return
     */
    public static boolean isNotchAndFullScreen(Activity activity) {
        //vivo oppo Build.VERSION_CODES.P以下不能完全适配 这里判断为到p以上
        String manufacturer = Build.MANUFACTURER;
        if (StringUtil.isEmpty(manufacturer)) {
            return false;
        } else if (manufacturer.equalsIgnoreCase("HUAWEI")) {
            return hasNotchHw(activity);
        } else if (manufacturer.equalsIgnoreCase("xiaomi")) {
            return hasNotchXiaoMi(activity);
        } else if (manufacturer.equalsIgnoreCase("oppo")) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                return hasNotchOPPO(activity);
            }
            return false;
        } else if (manufacturer.equalsIgnoreCase("vivo")) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                return hasNotchVIVO(activity);
            }
            return false;
        } else {
            return false;
        }
    }

    /**
     * 刘海屏去除全屏模式
     *
     * @param activity
     */
    public static void clearFullScreen(Activity activity) {
//        if (!AppSetting.SWITCH_NOTCH_USE_STTUS_BAR) return;
//        SPUtils.setSP(activity, SpConstans.SP_PHONE_IS_NOTCH, NotchUtil.hasNotchInScreen(activity));
//        //是刘海屏,去除全屏模式
//        if (NotchUtil.hasNotchInScreen(activity))
//            activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    /**
     * 是否有刘海屏
     *
     * @return
     */
    public static boolean hasNotchInScreen(Activity activity) {

        // android  P 以上有标准 API 来判断是否有刘海屏
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
//            try{
//                DisplayCutout displayCutout = activity.getWindow().getDecorView().getRootWindowInsets().getDisplayCutout();
//                if (displayCutout != null) {
//                    // 说明有刘海屏
//                    return true;
//                }
//            }catch (Exception e){
//                return false;
//            }
//        } else {
        // 通过其他方式判断是否有刘海屏  目前官方提供有开发文档的就 小米，vivo，华为（荣耀），oppo
        String manufacturer = Build.MANUFACTURER;

        if (StringUtil.isEmpty(manufacturer)) {
            return false;
        } else if (manufacturer.equalsIgnoreCase("HUAWEI")) {
            return hasNotchHw(activity);
        } else if (manufacturer.equalsIgnoreCase("xiaomi")) {
            return hasNotchXiaoMi(activity);
        } else if (manufacturer.equalsIgnoreCase("oppo")) {
            return hasNotchOPPO(activity);
        } else if (manufacturer.equalsIgnoreCase("vivo")) {
            return hasNotchVIVO(activity);
        } else {
            return false;
        }
//        }
//        return false;
    }

    /**
     * 判断vivo是否有刘海屏
     * https://swsdl.vivo.com.cn/appstore/developer/uploadfile/20180328/20180328152252602.pdf
     *
     * @param activity
     * @return
     */
    private static boolean hasNotchVIVO(Activity activity) {
        try {
            Class<?> c = Class.forName("android.util.FtFeature");
            Method get = c.getMethod("isFeatureSupport", int.class);
            return (boolean) (get.invoke(c, 0x20));
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 判断oppo是否有刘海屏
     * https://open.oppomobile.com/wiki/doc#id=10159
     *
     * @param activity
     * @return
     */
    private static boolean hasNotchOPPO(Activity activity) {
        try {
            return activity.getPackageManager().hasSystemFeature("com.oppo.feature.screen.heteromorphism");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 判断xiaomi是否有刘海屏
     * https://dev.mi.com/console/doc/detail?pId=1293
     *
     * @param activity
     * @return
     */
    private static boolean hasNotchXiaoMi(Activity activity) {
        try {
            Class<?> c = Class.forName("android.os.SystemProperties");
            Method get = c.getMethod("getInt", String.class, int.class);
            return (int) (get.invoke(c, "ro.miui.notch", 0)) == 1;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    public void setXiaoMiDisplayInNotch(Activity activity) {
        int flag = 0x00000100 | 0x00000200 | 0x00000400;
        try {
            Method method = Window.class.getMethod("addExtraFlags",
                    int.class);
            method.invoke(activity.getWindow(), flag);
        } catch (Exception ignore) {
        }
    }


    /**
     * 判断华为是否有刘海屏
     * https://devcenter-test.huawei.com/consumer/cn/devservice/doc/50114
     *
     * @param activity
     * @return
     */
    private static boolean hasNotchHw(Activity activity) {

        try {
            ClassLoader cl = activity.getClassLoader();
            Class HwNotchSizeUtil = cl.loadClass("com.huawei.android.util.HwNotchSizeUtil");
            Method get = HwNotchSizeUtil.getMethod("hasNotchInScreen");
            return (boolean) get.invoke(HwNotchSizeUtil);
        } catch (Exception e) {
            return false;
        }
    }

    /*刘海屏全屏显示FLAG*/
    public static final int FLAG_NOTCH_SUPPORT = 0x00010000;

    /**
     * 设置应用窗口在华为刘海屏手机使用刘海区
     *
     * @param window 应用页面window对象
     */
    public static void setHWFullScreenWindowLayoutInDisplayCutout(Window window) {
        if (window == null) {
            return;
        }
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        try {
            Class layoutParamsExCls = Class.forName("com.huawei.android.view.LayoutParamsEx");
            Constructor con = layoutParamsExCls.getConstructor(WindowManager.LayoutParams.class);
            Object layoutParamsExObj = con.newInstance(layoutParams);
            Method method = layoutParamsExCls.getMethod("addHwFlags", int.class);
            method.invoke(layoutParamsExObj, FLAG_NOTCH_SUPPORT);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setHWNotFullScreenWindowLayoutInDisplayCutout(Window window) {
        if (window == null) {
            return;
        }
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        try {
            Class layoutParamsExCls = Class.forName("com.huawei.android.view.LayoutParamsEx");
            Constructor con = layoutParamsExCls.getConstructor(WindowManager.LayoutParams.class);
            Object layoutParamsExObj = con.newInstance(layoutParams);
            Method method = layoutParamsExCls.getMethod("clearHwFlags", int.class);
            method.invoke(layoutParamsExObj, FLAG_NOTCH_SUPPORT);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 28api才能使用 但是手机小于28的都出现了notch，如vivo1816
     *
     * @param window
     */
    public void useNotch(Window window) {
//        WindowManager.LayoutParams lp = window.getAttributes();
//        lp.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
//        window.attributes = lp
    }

    //    /**
//     * vivo 8.1.0无效
//     *
//     * @param dialog
//     * @param context
//     */
//    public static void setDlgFull(Dialog dialog, Activity context) {
//        // 延伸显示区域到刘海
//        try {
//            if (NotchUtil.hasNotchInScreen(context)) {
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
//                    WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
//                    lp.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
//                    dialog.getWindow().setAttributes(lp);
//                } else {
//                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
//                        dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
//                    } else {
//                        dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//                        dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION, WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
//                    }
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
    public static boolean isFullScreen(Activity activity) {
        if ((activity.getWindow().getAttributes().flags & WindowManager.LayoutParams.FLAG_FULLSCREEN)
                == WindowManager.LayoutParams.FLAG_FULLSCREEN) {
            return true;
        }
        return false;
    }

}
