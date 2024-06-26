package com.hxjt.dqyt.base;

import com.hxjt.dqyt.utils.AppUtil;
import com.hxjt.dqyt.utils.SPUtil;

import java.util.Locale;
import java.util.Objects;

/**
 * @author by DELL
 * @date on 2017/11/29
 * @describe SharedPreferences 数据保存管理类
 */

public class BaseSPManager {
    public static final String FOLLOW_SYSTEM_LANGUAGE = "follow_system_language";//语言
    public static final String LANGUAGE = "language";//语言
    public static final String LANGUAGE_COUNTRY = "language_country";//语言_国家地区
    public static final String DAY_NIGHT_MODE = "day_night_mode";//日夜模式
    public static final String IS_FIRST_LAUNCH = "is_first_launch";//是否第一次启动
    public static final String VERSION_CODE = "version_code";//版本号


    /*** 是否跟随系统 */
    public static boolean isFollowSystemLanguage() {
        return (boolean) SPUtil.get(FOLLOW_SYSTEM_LANGUAGE, true);
    }

    public static void setFollowSystemLanguage(boolean isFollow) {
        SPUtil.put(FOLLOW_SYSTEM_LANGUAGE, isFollow);
    }

    /***  语言 默认系统语言*/
    public static Locale getLanguage() {
        boolean isSystem = isFollowSystemLanguage();
        if (isSystem) {
            return Locale.getDefault();
        }
        String language = (String) SPUtil.get(LANGUAGE, Locale.getDefault().getLanguage());//默认简体中文
        String country = (String) SPUtil.get(LANGUAGE_COUNTRY, Locale.getDefault().getCountry());//默认简体中文
        return new Locale(language, country);
    }


    public static void setLanguage(Locale language) {
        SPUtil.put(LANGUAGE, language.getLanguage());
        SPUtil.put(LANGUAGE_COUNTRY, language.getCountry());
    }


    /*** 日夜模式 */
    public static boolean isNightMode() {
        return (boolean) SPUtil.get(DAY_NIGHT_MODE, false);
    }

    public static void setNightMode(boolean nightMode) {
        SPUtil.put(DAY_NIGHT_MODE, nightMode);
    }

    /*** 是否第一次启动 */
    public static boolean isFirstLaunch() {
        return (boolean) SPUtil.get(IS_FIRST_LAUNCH, true);
    }

    public static void setIsFirstLaunch(boolean isFirstLunch) {
        SPUtil.put(IS_FIRST_LAUNCH, isFirstLunch);
    }

    /*** 是否新版本 */
    public static boolean isNewVersion() {
        int versionCode = (int) SPUtil.get(VERSION_CODE, 0);
        int curCode = Objects.requireNonNull(AppUtil.getPackageInfo( BaseApplication.getInstance().getApplicationContext())).versionCode;
        return curCode > versionCode;
    }

    public static void updateVersionCode() {
        int curCode = Objects.requireNonNull(AppUtil.getPackageInfo( BaseApplication.getInstance().getApplicationContext())).versionCode;
        SPUtil.put(VERSION_CODE, curCode);
    }
}
