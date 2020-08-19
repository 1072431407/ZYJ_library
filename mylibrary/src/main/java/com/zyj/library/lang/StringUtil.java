package com.zyj.library.lang;

import android.app.KeyguardManager;
import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;
import android.util.DisplayMetrics;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtil {
    public static final String TAG = "StringUtil";
    public static final int MAX_LENGTH_nikName = 20;

    public static boolean isNickName(Context context, String nikName) {
        if (StringUtil.isEmpty(nikName)) {
            return false;
        }

        if (nikName.length() > MAX_LENGTH_nikName) {
            return false;
        }
        return true;
    }

    public static boolean isEmpty(String str) {
        if (null == str)
            return true;
        if (str.trim().length() == 0)
            return true;
        if (str.trim().equalsIgnoreCase("null"))
            return true;
        return false;
    }

    /**
     * 获取字符串中第一串数字
     *
     * @param string
     * @return
     */
    public static String matcherNumber(String string) {
        if (string == null)
            return "";
        Pattern p = Pattern.compile("(\\d+)");
        Matcher m = p.matcher(string);
        while (m.find()) {
            String s = m.group(1);
//            if (s.length() == AppSetting.VAilCODE_LENGTH)
//                return s;
            return s;
        }
        return "";
    }

    /**
     * 手机号验证
     *
     * @param str
     * @return 验证通过返回true
     */
    public static boolean isMobile(String str) {
        try {
            Pattern p = null;
            Matcher m = null;
            boolean b = false;
//            p = Pattern.compile("^[1][3,4,5,7,8][0-9]{9}$"); // 验证手机号11位
            p = Pattern.compile("^[1][0-9]{10}$"); // 验证手机号11位
            m = p.matcher(str);
            b = m.matches();
            return b;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 手机号中间显示xxxx
     *
     * @param phone
     * @return
     */
    public static String delPhone(String phone) {
        try {
            if (isMobile(phone)) {
                String pho = phone.substring(0, 3) + "xxxx" + phone.substring(7, 11);
                return pho;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return phone;
    }

    /**
     * 电话号码验证
     *
     * @param str
     * @return 验证通过返回true
     */
    public static boolean isHomePhone(String str) {
        try {
            Pattern p1 = null, p2 = null;
            Matcher m = null;
            boolean b = false;
            p1 = Pattern.compile("^[0][1-9]{2,3}-[0-9]{5,10}$");  // 验证带区号的
            p2 = Pattern.compile("^[1-9]{1}[0-9]{5,8}$");         // 验证没有区号的
            if (str.length() > 9) {
                m = p1.matcher(str);
                b = m.matches();
            } else {
                m = p2.matcher(str);
                b = m.matches();
            }
            return b;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 密码是否合格
     * 字母或数子 或字母和组合组成 大于6位
     *
     * @param password
     * @return
     */
    public static boolean isPassWord(String password) {
        if (password == null)
            return false;
        String str = "[a-zA-Z0-9]{6,12}";
        Pattern p = Pattern.compile(str);
        Matcher m = p.matcher(password);
        return m.matches();
    }


    public static boolean isEmail(String email) {
        String str = "^([a-zA-Z0-9]*[-_]?[a-zA-Z0-9]+)*@([a-zA-Z0-9]*[-_]?[a-zA-Z0-9]+)+[\\.][A-Za-z]{2,3}([\\.][A-Za-z]{2})?$";
        Pattern p = Pattern.compile(str);
        Matcher m = p.matcher(email);
        return m.matches();
    }

    /**
     * 去掉影响居中的标签
     *
     * @param source
     * @return
     */
    public static String moveCenterTag(String source) {
        if (source == null)
            return null;
//        source = replace(source, "<p>", "");
        source = moveTag(source, "p");
        source = replace(source, "</p>", "");
//        source = replace(source, "<div>", "");
        source = moveTag(source, "div");
        source = replace(source, "</div>", "");
        source = moveTag(source, "span");
        source = replace(source, "</span>", "");
        source = replace(source, "</br>", "");
        source = replace(source, "<br>", "");
        source = replace(source, "<o:p>", "");
        source = replace(source, "</o:p>", "");
        source = replace(source, "\n", "");
        return source;
    }

    /**
     * 替换<a></>标签
     *
     * @param source
     * @return
     */
    public static String moveTagA(String source) {
        if (source == null)
            return null;
        Pattern p = Pattern.compile("<a[^>]*>");//<a>
        Matcher m = p.matcher(source);
        while (m.find()) {
            String find = m.group();
            source = source.replace(find, "");
        }
        source = replace(source, "</a>", "");
        return source;
    }

    /**
     * 替换指定标签标签
     *
     * @param source
     * @param tag
     * @return
     */
    public static String moveTag(String source, String tag) {
        if (tag == null)
            return source;
        if (source == null)
            return null;
        String reg = "<" + tag + "[^>]*>";
        Pattern p = Pattern.compile(reg);
        Matcher m = p.matcher(source);
        while (m.find()) {
            String find = m.group();
            source = source.replace(find, "");
        }
        source = replace(source, "</" + tag + ">", "");
        return source;
    }

    public static String replace(String source, String regex, String replacement) {
        int index = -1;
        StringBuffer buffer = new StringBuffer();
        while ((index = source.indexOf(regex)) >= 0) {
            buffer.append(source.substring(0, index));
            buffer.append(replacement);
            source = source.substring(index + regex.length());
        }
        buffer.append(source);
        return buffer.toString();
    }


    public static String urlEncode(String obj) {
        return urlEncode(obj, "GBK");
    }

    public static String urlEncode(String obj, String charset) {
        String result = null;
        if (obj != null) {
            try {
                result = URLEncoder.encode(obj, charset);
            } catch (UnsupportedEncodingException e) {
                // TODO Auto-generated catch block
                // e.printStackTrace();
                return result;
            }
        }
        return result;
    }


    public static String urlDecode(String obj) {
        return urlDecode(obj, "GBK");
    }

    public static String urlDecode(String obj, String charset) {
        String result = null;
        if (obj != null) {
            try {
                result = URLDecoder.decode(obj, charset);
            } catch (UnsupportedEncodingException e) {
                // TODO Auto-generated catch block
                // e.printStackTrace();
                return result;
            }
        }
        return result;
    }


    public static int numberOfStr(String str, String con) {
        str = " " + str;
        if (str.endsWith(con)) {
            return str.split(con).length;
        } else {
            return str.split(con).length - 1;
        }
    }


    public static String md5(String str) {
        StringBuffer buf = new StringBuffer("");
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(str.getBytes());
            byte b[] = md.digest();
            int i;
            for (int offset = 0; offset < b.length; offset++) {
                i = b[offset];
                if (i < 0)
                    i += 256;
                if (i < 16)
                    buf.append("0");
                buf.append(Integer.toHexString(i));
            }
            return buf.toString();
        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return buf.toString();
    }

    public static String getDomain(String url) {
        try {
            URI uri = new URI(url);
            return uri.getHost();
        } catch (URISyntaxException e) {
            return null;
        }
    }


    public static String bSubstring(String s, int length) {
        try {
            byte[] bytes = s.getBytes("Unicode");
            int n = 0;
            int i = 2;
            for (; i < bytes.length && n < length; i++) {
                if (i % 2 == 1) {
                    n++;
                } else {
                    if (bytes[i] != 0) {
                        n++;
                    }
                }
            }

            if (i % 2 == 1) {
                if (bytes[i - 1] != 0)
                    i = i - 1;
                else
                    i = i + 1;

            }

            return new String(bytes, 0, i, "Unicode");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return s;
    }

    public static String genMachineCode(String token) {
        String result = "";
        try {
            String t = Long.toString(Long.valueOf(token.substring(0, 5)), 32);
            result = t + String.valueOf(token).substring(5, String.valueOf(token).length());
        } catch (Exception e) {
        }
        return result;
    }


    public static String convertStreamToString(InputStream is) {
        /*
         * To convert the InputStream to String we use the BufferedReader.readLine()
         * method. We iterate until the BufferedReader return null which means
         * there's no more data to read. Each line will appended to a StringBuilder
         * and returned as String.
         */
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        String res = sb.toString();
        return res;
    }


    public static String urlDecoder(String obj) {
        return urlDecoder(obj, "gbk");
    }

    public static String urlDecoder(String obj, String charset) {
        String result = null;
        if (obj != null) {
            try {
                result = URLDecoder.decode(obj, charset);
            } catch (UnsupportedEncodingException e) {
                return result;
            }
        }
        return result;
    }

    public static String getConstellation(String birthday) {
        String str = "";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date date = sdf.parse(birthday);
            int month = date.getMonth() + 1;
            int day = date.getDate();
            switch (month) {
                case 1:
                    if (day <= 19) {
                        str = "魔蝎座";
                    } else {
                        str = "水瓶座";
                    }
                    break;
                case 2:
                    if (day <= 18) {
                        str = "水瓶座";
                    } else {
                        str = "双鱼座";
                    }
                    break;
                case 3:
                    if (day <= 20) {
                        str = "双鱼座";
                    } else {
                        str = "白羊座";
                    }
                    break;
                case 4:
                    if (day <= 19) {
                        str = "白羊座";
                    } else {
                        str = "金牛座";
                    }
                    break;
                case 5:
                    if (day <= 20) {
                        str = "金牛座";
                    } else {
                        str = "双子座";
                    }
                    break;
                case 6:
                    if (day <= 21) {
                        str = "双子座";
                    } else {
                        str = "巨蟹座";
                    }
                    break;
                case 7:
                    if (day <= 22) {
                        str = "巨蟹座";
                    } else {
                        str = "狮子座";
                    }
                    break;
                case 8:
                    if (day <= 22) {
                        str = "狮子座";
                    } else {
                        str = "处女座";
                    }
                    break;
                case 9:
                    if (day <= 22) {
                        str = "处女座";
                    } else {
                        str = "天秤座";
                    }
                    break;
                case 10:
                    if (day <= 23) {
                        str = "天秤座";
                    } else {
                        str = "天蝎座";
                    }
                    break;
                case 11:
                    if (day <= 22) {
                        str = "天蝎座";
                    } else {
                        str = "射手座";
                    }
                    break;
                case 12:
                    if (day <= 21) {
                        str = "射手座";
                    } else {
                        str = "魔蝎座";
                    }
                    break;
                default:
                    break;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return str;

    }

    /**
     * 获取androidid
     *
     * @param context
     * @return
     */
    public static String getAndroidId(Context context) {
        try {
            String id = null;
            if (Build.VERSION.SDK_INT >= 28) {
                //ANDROID_ID
                //On Android 8.
                id = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
            } else {
                id = Settings.System.getString(context.getContentResolver(), Settings.System.ANDROID_ID);
            }
            if (id != null && id.toLowerCase().replace("_", "").equals("androidid"))
                return "";
            return id;
        } catch (Exception e) {
            e.printStackTrace();
        } catch (Error e) {
            e.printStackTrace();
        }
        return "";
    }


    /**
     * 获取设备的mac地址和IP地址（android6.0以上专用）
     *
     * @return
     */
    public static String getMachineHardwareAddress() {
        try {
            Enumeration<NetworkInterface> interfaces = null;
            try {
                interfaces = NetworkInterface.getNetworkInterfaces();
            } catch (SocketException e) {
                e.printStackTrace();
            }
            String hardWareAddress = "";
            NetworkInterface iF = null;
            while (interfaces.hasMoreElements()) {
                iF = interfaces.nextElement();
                try {
                    hardWareAddress = bytesToString(iF.getHardwareAddress());
                    if (hardWareAddress == null) continue;
                } catch (SocketException e) {
                    e.printStackTrace();
                }
            }
            if (iF != null && iF.getName().equals("wlan0")) {
                hardWareAddress = hardWareAddress.replace(":", "");
            }
            return hardWareAddress;
        } catch (Exception e) {
            e.printStackTrace();
        } catch (Error e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getMac(Context activity) {
        try {
            WifiManager mWifiManager = (WifiManager) activity.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            assert mWifiManager != null;
            WifiInfo info = mWifiManager.getConnectionInfo();
            return info.getMacAddress();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /***
     * byte转为String
     * @param bytes
     * @return
     */
    private static String bytesToString(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return null;
        }
        StringBuilder buf = new StringBuilder();
        for (byte b : bytes) {
            buf.append(String.format("%02X:", b));
        }
        if (buf.length() > 0) {
            buf.deleteCharAt(buf.length() - 1);
        }
        return buf.toString();
    }



    /**
     * 获取系统分辨率
     *
     * @param c
     * @return
     */
    public static String getScreenPx(Context c) {
        try {
            DisplayMetrics dm = c.getResources().getDisplayMetrics();
            int screenWidth = dm.widthPixels;
            int screenHeight = dm.heightPixels;
            return screenWidth + "x" + screenHeight;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 是否开启密码锁
     *
     * @return
     */
    public static boolean checkPasswordToUnLock(Context context) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                KeyguardManager keyguardManager = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
                return keyguardManager.isKeyguardSecure();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static String formatDate(int hour) {
        String src = "";
        if (hour >= 23 || hour < 1) {
            src = "子时";
        } else if (hour >= 1 && hour < 3) {
            src = "丑时";
        } else if (hour >= 3 && hour < 5) {
            src = "寅时";
        } else if (hour >= 5 && hour < 7) {
            src = "卯时";
        } else if (hour >= 7 && hour < 9) {
            src = "辰时";
        } else if (hour >= 9 && hour < 11) {
            src = "巳时";
        } else if (hour >= 11 && hour < 13) {
            src = "午时";
        } else if (hour >= 13 && hour < 15) {
            src = "未时";
        } else if (hour >= 15 && hour < 17) {
            src = "申时";
        } else if (hour >= 17 && hour < 19) {
            src = "酉时";
        } else if (hour >= 19 && hour < 21) {
            src = "戌时";
        } else if (hour >= 21 && hour < 23) {
            src = "亥时";
        }
        return src;
    }

    public static String formatDate1(int position) {
        String src = "";
        switch (position) {
            case 0:
                src = "子时";
                break;
            case 1:
                src = "丑时";
                break;
            case 2:
                src = "寅时";
                break;
            case 3:
                src = "卯时";
                break;
            case 4:
                src = "辰时";
                break;
            case 5:
                src = "巳时";
                break;
            case 6:
                src = "午时";
                break;
            case 7:
                src = "未时";
                break;
            case 8:
                src = "申时";
                break;
            case 9:
                src = "酉时";
                break;
            case 10:
                src = "戌时";
                break;
            case 11:
                src = "亥时";
                break;
        }
        return src;
    }

    public static int formatDateIndex(int hour) {
        int position = -1;
        if (hour >= 23 && hour < 1) {
            position = 0;
        } else if (hour >= 1 && hour < 3) {
            position = 1;
        } else if (hour >= 3 && hour < 5) {
            position = 2;
        } else if (hour >= 5 && hour < 7) {
            position = 3;
        } else if (hour >= 7 && hour < 9) {
            position = 4;
        } else if (hour >= 9 && hour < 11) {
            position = 5;
        } else if (hour >= 11 && hour < 13) {
            position = 6;
        } else if (hour >= 13 && hour < 15) {
            position = 7;
        } else if (hour >= 15 && hour < 17) {
            position = 8;
        } else if (hour >= 17 && hour < 19) {
            position = 9;
        } else if (hour >= 19 && hour < 21) {
            position = 10;
        } else if (hour >= 21 && hour < 23) {
            position = 11;
        }
        return position;
    }

    /**
     * post参数拼接
     *
     * @param params
     * @return
     */
    public static StringBuffer getRequestData(Map<String, String> params) {
        // 存储封装好的请求体信息
        StringBuffer stringBuffer = new StringBuffer();
        try {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                String value = entry.getValue();
                if (value == null)
                    value = "";
                stringBuffer.append(entry.getKey()).append("=")
                        .append(URLEncoder.encode(value, "utf-8"))
                        .append("&");
            }
            // 删除最后的一个"&"
            stringBuffer.deleteCharAt(stringBuffer.length() - 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stringBuffer;
    }

    public static void main(String[] args) {

//        System.out.println(formatPlayCount(1000));
//        String sr = urlEncode("1","utf-8");19
//        System.out.println(sr);

    }
}