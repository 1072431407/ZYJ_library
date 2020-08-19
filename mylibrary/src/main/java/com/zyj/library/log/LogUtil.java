package com.zyj.library.log;

import android.util.Log;
import java.util.Locale;


/**
 * 日志工具类
 */

public class LogUtil {

    public static void setLogInit(boolean logInit) {
        LOG_INIT = logInit;
    }

    private static boolean LOG_INIT = true;//BuildConfig.DEBUG;

    private static boolean LOGV = LOG_INIT;
    private static boolean LOGD = LOG_INIT;
    private static boolean LOGI = LOG_INIT;
    private static boolean LOGW = LOG_INIT;
    private static boolean LOGE = LOG_INIT;

    /**
     * 获取到调用者的类名
     *
     * @return 调用者的类名
     */
    private static String getTag() {
        StackTraceElement[] trace = new Throwable().fillInStackTrace()
                .getStackTrace();
        String callingClass = "";
        for (int i = 2; i < trace.length; i++) {
            Class<?> clazz = trace[i].getClass();
            if (!clazz.equals(LogUtil.class)) {
                callingClass = trace[i].getClassName();
                callingClass = callingClass.substring(callingClass
                        .lastIndexOf('.') + 1);
                break;
            }
        }
        return callingClass;
    }

    //不需要再在类中定义TAG，直接打印日志信息
    public static void v(String mess) {
        if (LOGV) {
            Log.v(getTag(), mess);
        }
    }

    public static void d(Object mess) {
        if (LOGD) {
            Log.d(getTag(), String.valueOf(mess));
        }
    }

    public static void i(Object mess) {
        if (LOGI) {
            Log.i(getTag(), String.valueOf(mess));
        }
    }

    public static void w(String mess) {
        if (LOGW) {
            Log.w(getTag(), mess);
        }
    }

    public static void e(String mess) {
        if (LOGE) {
            Log.e(getTag(), mess);
        }
    }


    /**
     * 获取线程ID，方法名和输出信息
     *
     * @param msg
     * @return
     */
    private static String buildMessage(String msg) {
        StackTraceElement[] trace = new Throwable().fillInStackTrace()
                .getStackTrace();
        String caller = "";
        for (int i = 2; i < trace.length; i++) {
            Class<?> clazz = trace[i].getClass();
            if (!clazz.equals(LogUtil.class)) {
                caller = trace[i].getMethodName();
                break;
            }
        }
        return String.format(Locale.US, "[%d] %s: %s", Thread.currentThread()
                .getId(), caller, msg);
    }

    //不需要再在类中定义TAG，打印线程ID，方法名和输出信息
    public static void v1(String mess) {
        if (LOGV) {
            Log.v(getTag(), buildMessage(mess));
        }
    }

    public static void d1(String mess) {
        if (LOGD) {
            Log.d(getTag(), buildMessage(mess));
        }
    }

    public static void i1(String mess) {
        if (LOGI) {
            Log.i(getTag(), buildMessage(mess));
        }
    }

    public static void w1(String mess) {
        if (LOGW) {
            Log.w(getTag(), buildMessage(mess));
        }
    }

    public static void e1(String mess) {
        if (LOGE) {
            Log.e(getTag(), buildMessage(mess));
        }
    }


    //不需要再在类中定义TAG，打印类名,方法名,行号等.并定位行
    public static void v2(String mess) {
        if (LOGV) {
            Log.v(getTag(), getMsgFormat(mess));
        }
    }

    public static void d2(String mess) {
        if (LOGD) {
            Log.d(getTag(), getMsgFormat(mess));
        }
    }

    public static void i2(String mess) {
        if (LOGI) {
            Log.i(getTag(), getMsgFormat(mess));
        }
    }

    public static void w2(String mess) {
        if (LOGW) {
            Log.w(getTag(), getMsgFormat(mess));
        }
    }

    public static void e2(String mess) {
        if (LOGE) {
            Log.e(getTag(), getMsgFormat(mess));
        }
    }

    /**
     * 获取相关数据:类名,方法名,行号等.用来定位行
     *
     * @return
     */
    private static String getFunctionName() {
        StackTraceElement[] sts = Thread.currentThread().getStackTrace();
        if (sts != null) {
            for (StackTraceElement st : sts) {
                if (st.isNativeMethod()) {
                    continue;
                }
                if (st.getClassName().equals(Thread.class.getName())) {
                    continue;
                }
                if (st.getClassName().equals(LogUtil.class.getName())) {
                    continue;
                }
                return "[ Thread:" + Thread.currentThread().getName() + ", at " + st.getClassName() + "." + st.getMethodName()
                        + "(" + st.getFileName() + ":" + st.getLineNumber() + ")" + " ]";
            }
        }
        return null;
    }

    /**
     * 输出格式定义
     *
     * @param msg
     * @return
     */
    private static String getMsgFormat(String msg) {
        return msg + " ;" + getFunctionName();
    }
}