package com.zyj.library.designpatterns;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 单例模式
 * 懒汉式加锁
 * @param <T>
 */
public class Singleton<T> {
//        UserCenter userCenter1 = Singleton.getSingleton(UserCenter.class);
    private static final ConcurrentMap<Class,Object> INSTANCES_MAP = new ConcurrentHashMap<>();
    private Singleton(){
    }
    public static <T> T getSingleton(Class<T> type){
        Object ob = INSTANCES_MAP.get(type);
        try{
            if (ob == null){
                synchronized (INSTANCES_MAP){
                    ob = type.newInstance();
                    INSTANCES_MAP.put(type,ob);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return (T)ob;
    }
}
