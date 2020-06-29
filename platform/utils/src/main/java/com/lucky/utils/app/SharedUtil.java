package com.lucky.utils.app;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

import com.alibaba.fastjson.JSON;

/**
 * 去除sp文件储存的加密，改成无加密模式
 *
 * @author xingguolei
 */
@SuppressLint("ApplySharedPref")
public class SharedUtil {
    private volatile static SharedUtil sharedUtil = null;
    private static SharedPreferences preferences;
    private final static String APP_SP_CACHE = "lucky_new_sp_cache";

    private SharedUtil(Context context) {
        SharedUtilProxy.instance(context);
        preferences = context.getSharedPreferences(APP_SP_CACHE, Context.MODE_PRIVATE);
    }

    public static SharedUtil instance(Context context) {
        if (sharedUtil == null) {
            synchronized (SharedUtil.class) {
                if (sharedUtil == null) {
                    sharedUtil = new SharedUtil(context);
                }
            }
        }
        return sharedUtil;
    }

    public static void saveString(String key, String value) {
        preferences.edit()
                .putString(key, value)
                .commit();
    }

    public static void saveInt(String key, int value) {
        preferences.edit()
                .putInt(key, value)
                .commit();
    }

    public static void saveBoolean(String key, boolean value) {
        preferences.edit()
                .putBoolean(key, value)
                .commit();
    }

    public static void saveFloat(String key, float value) {
        preferences.edit()
                .putFloat(key, value)
                .commit();
    }

    public static void saveLong(String key, long value) {
        preferences.edit()
                .putLong(key, value)
                .commit();
    }

    public static <T> void saveObject(String key, T clazz) {
        try {
            saveString(key, JSON.toJSONString(clazz));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getString(String key) {
        return getString(key, "");
    }

    public static String getString(String key, String defaultValue) {
        if (preferences.getAll().containsKey(key)) {
            return preferences.getString(key, defaultValue);
        } else {
            return SharedUtilProxy.getString(key, defaultValue);
        }
    }

    public static int getInt(String key) {
        return getInt(key, -1);
    }

    public static int getInt(String key, int defaultValue) {
        if (preferences.getAll().containsKey(key)) {
            return preferences.getInt(key, defaultValue);
        } else {
            return SharedUtilProxy.getInt(key, defaultValue);
        }
    }

    public static boolean getBoolean(String key) {
        return getBoolean(key, false);
    }

    public static boolean getBoolean(String key, boolean defaultValue) {
        if (preferences.getAll().containsKey(key)) {
            return preferences.getBoolean(key, defaultValue);
        } else {
            return SharedUtilProxy.getBoolean(key, defaultValue);
        }
    }

    public static float getFloat(String key) {
        return getFloat(key, 0);
    }

    public static float getFloat(String key, float defaultValue) {
        if (preferences.getAll().containsKey(key)) {
            return preferences.getFloat(key, defaultValue);
        } else {
            return SharedUtilProxy.getFloat(key, defaultValue);
        }
    }

    public static long getLong(String key) {
        return getLong(key, 0L);
    }

    public static long getLong(String key, long defaultValue) {
        if (preferences.getAll().containsKey(key)) {
            return preferences.getLong(key, defaultValue);
        } else {
            return SharedUtilProxy.getLong(key, defaultValue);
        }
    }

    public static <T> T getObject(String cacheKey, Class<T> clazz) {
        try {
            return JSON.parseObject(getString(cacheKey), clazz);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void remove(String key) {
        preferences.edit().remove(key).commit();
        SharedUtilProxy.remove(key);
    }
}
