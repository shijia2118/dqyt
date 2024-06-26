package com.hxjt.dqyt.utils;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;

/**
 * Created by allen on 2017/6/22.
 *
 * @author Allen
 * 网络请求
 */

public class RxHttpUtils {

    @SuppressLint("StaticFieldLeak")
    private static RxHttpUtils instance;
    @SuppressLint("StaticFieldLeak")
    private static Application context;

    public static RxHttpUtils getInstance() {
        if (instance == null) {
            synchronized (RxHttpUtils.class) {
                if (instance == null) {
                    instance = new RxHttpUtils();
                }
            }

        }
        return instance;
    }

    /**
     * 必须在全局Application先调用，获取context上下文，否则缓存无法使用
     *
     * @param app Application
     */
    public RxHttpUtils init(Application app) {
        context = app;
        return this;
    }

    /**
     * 获取全局上下文
     */
    public static Context getContext() {
        checkInitialize();
        return context;
    }

    /**
     * 检测是否调用初始化方法
     */
    private static void checkInitialize() {
        if (context == null) {
            throw new ExceptionInInitializerError("请先在全局Application中调用 RxHttpUtils.getInstance().init(this) 初始化！");
        }
    }

    public ApiFactory config() {
        checkInitialize();
        return ApiFactory.getInstance();
    }


    /**
     * 使用全局参数创建请求
     *
     * @param cls Class
     * @param <K> K
     * @return 返回
     */
    public static <K> K createApi(Class<K> cls) {
        return ApiFactory.getInstance().createApi(cls);
    }

    /**
     * 切换baseUrl
     *
     * @param baseUrlKey   域名的key
     * @param baseUrlValue 域名的url
     * @param cls          class
     * @param <K>          k
     * @return k
     */
    public static <K> K createApi(String baseUrlKey, String baseUrlValue, Class<K> cls) {
        return ApiFactory.getInstance().createApi(baseUrlKey, baseUrlValue, cls);
    }

}
