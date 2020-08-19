package com.zyj.library.network;

import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.telephony.TelephonyManager;

import androidx.core.app.ActivityCompat;

import com.zyj.library.log.Log;


public class NetworkUtil {
    public static String TAG = "NetworkUtil";

    public static boolean isNetEnable(final Context app) {
        ConnectivityManager manager = (ConnectivityManager) app.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        if (networkInfo != null) {
            return networkInfo.isConnected();
        } else {

        }

        return false;
    }


    public static boolean isNetWifiEnable(final Context app) {
        ConnectivityManager manager = (ConnectivityManager) app.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        if (networkInfo != null) {
            if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI)
                return true;
        }

        return false;
    }

    /*手机移动网络*/
    public static boolean isNetMobileEnable(final Context app) {
        ConnectivityManager manager = (ConnectivityManager) app.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        if (networkInfo != null) {
            if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE)
                return true;
        }

        return false;
    }


    /**
     * wifi net work
     */
    public static final String NETWORK_WIFI = "1";

    /**
     * "2G" networks
     */
    public static final String NETWORK_CLASS_2_G = "2";

    /**
     * "3G" networks
     */
    public static final String NETWORK_CLASS_3_G = "3";

    /**
     * "4G" networks
     */
    public static final String NETWORK_CLASS_4_G = "4";
    /**
     * "5G" networks
     */
    public static final String NETWORK_CLASS_5_G = "5";
    /**
     * 未知
     */
    public static final String NETWORK_CLASS_UNKNOW = "6";

    /**
     * 1、优先wifi
     * 2、然后判断移动网络
     * <p>
     * WIFI(1, "WIFI"), NETWORN_2G(2, "2G"),
     * NETWORN_3G(3, "3G"), NETWORN_4G(4, "4G"),
     * NETWORN_5G(5, "5G"), NETWORN_OTHER(6, "other"),
     */
    public static String getNetWorkType(Context context) {
        if (isNetWifiEnable(context)) return NETWORK_WIFI;
        String type = NETWORK_CLASS_UNKNOW;
        //    public static final int NETWORK_TYPE_NR = TelephonyProtoEnums.NETWORK_TYPE_NR; //20. API >=29才有的
        try {
            if (Build.VERSION.SDK_INT >= 29) {
                //是不是5G
                if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
                    TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                    int networkType = telephonyManager.getNetworkType();
                    //TelephonyManager.NETWORK_TYPE_NR
                    if (networkType == 20) {
                        Log.v(TAG, "5G");
                        return NETWORK_CLASS_5_G;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } catch (Error e) {
            e.printStackTrace();
        }

        try {
            // 如果不是wifi，则判断当前连接的是运营商的哪种网络2g、3g、4g等
            ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetInfo = connManager.getActiveNetworkInfo();
            if (activeNetInfo == null || !activeNetInfo.isAvailable()) {
                return NETWORK_CLASS_UNKNOW;
            }
            NetworkInfo networkInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            if (null != networkInfo) {
                NetworkInfo.State state = networkInfo.getState();
                String strSubTypeName = networkInfo.getSubtypeName();
                if (null != state)
                    if (state == NetworkInfo.State.CONNECTED
                            || state == NetworkInfo.State.CONNECTING) {
                        switch (activeNetInfo.getSubtype()) {
                            //如果是2g类型
                            case TelephonyManager.NETWORK_TYPE_GPRS: // 联通2g
                            case TelephonyManager.NETWORK_TYPE_CDMA: // 电信2g
                            case TelephonyManager.NETWORK_TYPE_EDGE: // 移动2g
                            case TelephonyManager.NETWORK_TYPE_1xRTT:
                            case TelephonyManager.NETWORK_TYPE_IDEN:
                                return NETWORK_CLASS_2_G;
                            //如果是3g类型
                            case TelephonyManager.NETWORK_TYPE_EVDO_A: // 电信3g
                            case TelephonyManager.NETWORK_TYPE_UMTS:
                            case TelephonyManager.NETWORK_TYPE_EVDO_0:
                            case TelephonyManager.NETWORK_TYPE_HSDPA:
                            case TelephonyManager.NETWORK_TYPE_HSUPA:
                            case TelephonyManager.NETWORK_TYPE_HSPA:
                            case TelephonyManager.NETWORK_TYPE_EVDO_B:
                            case TelephonyManager.NETWORK_TYPE_EHRPD:
                            case TelephonyManager.NETWORK_TYPE_HSPAP:
                                return NETWORK_CLASS_3_G;
                            //如果是4g类型
                            case TelephonyManager.NETWORK_TYPE_LTE:
                                return NETWORK_CLASS_4_G;
                            default:
                                //中国移动 联通 电信 三种3G制式
                                if (strSubTypeName.equalsIgnoreCase("TD-SCDMA")
                                        || strSubTypeName.equalsIgnoreCase("WCDMA")
                                        || strSubTypeName.equalsIgnoreCase("CDMA2000")) {
                                    return NETWORK_CLASS_3_G;
                                } else {
                                    return NETWORK_CLASS_UNKNOW;
                                }
                        }
                    }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } catch (Error e) {
            e.printStackTrace();
        }
        return type;
    }


}
