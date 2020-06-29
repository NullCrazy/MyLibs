package com.lucky.utils.app;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.lucky.utils.aes.AesUtils;

/**
 * SharedPreferences 工具类
 */
public class SharedUtilProxy {
    private static SharedPreferences preferences;
    private static SharedUtilProxy sharedUtil = null;
    public static final String SECRET_KEY = "bmk6hcs3FKXUdsZG06lG";

    public final static String APP_SP_CACHE = "lucky_store_sp_cache";

    private SharedUtilProxy(Context context) {
        if (preferences == null) {
            context = context.getApplicationContext();
            preferences = context.getSharedPreferences(APP_SP_CACHE, Context.MODE_PRIVATE);
        }
    }

    public static SharedUtilProxy instance(Context context) {
        if (sharedUtil == null) {
            sharedUtil = new SharedUtilProxy(context);
        }
        return sharedUtil;
    }

    public static String getString(String key) {
        return getString(key, "");
    }

    public static String getString(String key, String defaultValue) {
        return getString(key, defaultValue, true);
    }

    public static String getString(String key, String defaultValue, boolean isDecrypt) {
        if (isDecrypt) {
            String value = AesUtils.decrypt(AesUtils.encoderKey(SECRET_KEY), preferences.getString(key, defaultValue));
            if (value == null) {
                value = preferences.getString(key, defaultValue);
                if (!TextUtils.isEmpty(value)) {
                    remove(key);
                    return defaultValue;
                }
            }
            return value;
        } else {
            return preferences.getString(key, defaultValue);
        }
    }

    public static void saveString(String key, String value) {
        saveString(key, value, true);
    }

    public static void saveString(String key, String value, boolean isEncoder) {
        if (isEncoder) {
            preferences.edit().putString(key, AesUtils.encrypt(AesUtils.encoderKey(SECRET_KEY), value)).commit();
        } else {
            preferences.edit().putString(key, value).commit();
        }
    }

    public static int getInt(String key) {
        return getInt(key, -1);
    }

    public static int getInt(String key, int defaultValue) {
        try {
            int value = Integer.parseInt(getString(key, String.valueOf(defaultValue)));
            return value;
        } catch (Exception ex) {
            return defaultValue;
        }
    }

    public static void saveInt(String key, int value) {
        saveString(key, String.valueOf(value));
    }

    public static boolean getBoolean(String key) {
        return preferences.getBoolean(key, false);
    }

    public static boolean getBoolean(String key, boolean defaultValue) {
        return preferences.getBoolean(key, defaultValue);
    }

    public static void saveBoolean(String key, boolean value) {
        preferences.edit().putBoolean(key, value).commit();
    }

    public static float getFloat(String key) {
        return getFloat(key, 0);
    }

    public static float getFloat(String key, float defaultValue) {
        try {
            float value = Float.parseFloat(getString(key, String.valueOf(defaultValue)));
            return value;
        } catch (Exception ex) {
            return 0f;
        }
    }

    public static void saveFloat(String key, float value) {
        saveString(key, String.valueOf(value));
    }

    public static void saveLong(String key, long value) {
        saveString(key, String.valueOf(value));
    }

    public static long getLong(String key) {
        return getLong(key, 0L);
    }

    public static long getLong(String key, long defaultValue) {
        try {
            long value = Long.parseLong(getString(key, String.valueOf(defaultValue)));
            return value;
        } catch (Exception ex) {
            return 0L;
        }
    }

    public static <T> void saveObject(String key, T clazz) {
        try {
            saveString(key, JSON.toJSONString(clazz));
        } catch (Exception e) {
            e.printStackTrace();
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
    }
}
