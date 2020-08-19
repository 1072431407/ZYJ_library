package com.zyj.library.network;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import com.zyj.library.log.Log;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by adu on 2020/5/14.
 * 获取mac地址工具类
 * 需要做各种版本的兼容
 * 注意耗时问题，处理办法。
 * 1、启动就初始化
 * 2、网络变化重新初始化
 */
public class MacAddressUtil {
    static final String TAG = MacAddressUtil.class.getSimpleName();
    /**
     * 无效的mac地址
     */
    static LinkedHashMap<String, String> disMap = new LinkedHashMap<>();

    static {
        disMap.put("020000000000", "020000000000");
        disMap.put("000000000000", "000000000000");
    }

    /**
     * Android  6.0 之前（不包括6.0）
     * 必须的权限  <uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
     *
     * @param context
     * @return
     */
    private static String getMacDefault(Context context) {
        String mac = null;
        try {
            if (context == null) {
                return mac;
            }
            WifiManager wifi = (WifiManager) context.getApplicationContext()
                    .getSystemService(Context.WIFI_SERVICE);
            if (wifi == null) {
                return mac;
            }
            WifiInfo info = null;
            try {
                info = wifi.getConnectionInfo();
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (info == null) {
                return null;
            }
            mac = info.getMacAddress();
            String s = new String(mac);
            s = s.replaceAll(":", "");
            if (!disMap.containsKey(s)) {
                return mac;
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } catch (Error e) {
            e.printStackTrace();
        }
        return mac;
    }

    /**
     * Android 6.0（包括） - Android 7.0（不包括）
     *
     * @return
     */
    private static String getMacAddress() {
        String WifiAddress = null;
        try {
            WifiAddress = new BufferedReader(new FileReader(new File("/sys/class/net/wlan0/address"))).readLine();
        } catch (Exception e) {
            e.printStackTrace();
        } catch (Error e) {
            e.printStackTrace();
        }
        return WifiAddress;
    }

    /**
     * 遍历循环所有的网络接口，找到接口是 wlan0
     * 必须的权限 <uses-permission android:name="android.permission.INTERNET" />
     *
     * @return
     */
    private static String getMacFromHardware() {
        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            Log.d(TAG, "all:" + all.size());
            for (NetworkInterface nif : all) {
                if (!nif.getName().equalsIgnoreCase("wlan0")) continue;

                byte[] macBytes = nif.getHardwareAddress();
                if (macBytes == null) {
                    return null;
                }
                Log.d(TAG, "macBytes:" + macBytes.length + "," + nif.getName());

                StringBuilder res1 = new StringBuilder();
                for (byte b : macBytes) {
                    res1.append(String.format("%02X:", b));
                }

                if (res1.length() > 0) {
                    res1.deleteCharAt(res1.length() - 1);
                }
                return res1.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 尽量匹配获取到 wifimac
     * 注意耗时问题 1、启动就初始化 2、网络变化重新初始化
     *
     * @param context
     * @return
     */
    public static String getWifiMac(Context context) {
        String macAddress = null;
        try {
            long t = System.currentTimeMillis();
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {//5.0以下
                macAddress = getMacDefault(context);
                if (macAddress != null) {
                    Log.d(TAG, "android 5.0以前的方式获取mac" + macAddress);
                }
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                macAddress = getMacAddress();
                if (macAddress != null) {
                    Log.d(TAG, "android 6~7 的方式获取的mac" + macAddress);
                }
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                macAddress = getMacFromHardware();
                if (macAddress != null) {
                    Log.d(TAG, "android 7以后 的方式获取的mac" + macAddress);
                }
            }
            String s = new String(macAddress);
            s = s.replaceAll(":", "");
            Log.d(TAG, "take time =" + (System.currentTimeMillis() - t));
            if (!disMap.containsKey(s)) {
                return macAddress;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } catch (Error e) {
            e.printStackTrace();
        }
        Log.d(TAG, "没有获取到MAC");
        return null;
    }

    /**
     * wifiInfo.getMacAddress();
     * 如果能获取到就当成是本机的MAC地址
     *
     * @param activity
     * @return
     */
    public static String getMac(Context activity) {
        return getMacDefault(activity);
    }

}
