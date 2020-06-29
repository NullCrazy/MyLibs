package com.lucky.utils.app;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.os.Build;
import android.os.PowerManager;
import android.provider.Settings;
import android.view.inputmethod.InputMethodManager;

import java.util.ArrayList;

public class OSUtils {

    protected static int mWifiStatus = -1;
    protected static ArrayList<PowerManager.WakeLock> mArrayCpuLock = null;



    /**
     * 隐藏软键盘
     *
     * @param context
     */
    public static void hideSoftInput(Context context) {
        try {
            if (context instanceof Activity) {
                hideSoftInput(context, (Activity) context);
            }
        } catch (Exception e) {
        }
    }

    /**
     * 隐藏软键盘
     *
     * @param context
     * @param activity
     */
    public static void hideSoftInput(Context context, Activity activity) {
        try {
            InputMethodManager imm = (InputMethodManager) context.getApplicationContext().
                    getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
        } catch (Exception e) {
        }
    }


    /**
     * 取消cpu一直唤醒
     */
    public static void keepCpuOff(PowerManager.WakeLock wl) {
        if (wl != null) {
            wl.release();
            wl = null;
        }
    }

    /**
     * 取消cpu一直唤醒
     */
    public static void keepCpuOff() {
        if (mArrayCpuLock != null) {
            for (int i = mArrayCpuLock.size() - 1; i >= 0; i--) {
                keepCpuOff(mArrayCpuLock.remove(i));
            }
        }
    }

    /**
     * 保持wifi一直唤醒
     */
    @SuppressWarnings("deprecation")
    public static int keepWifiOn(Context context) {
        try {
            ContentResolver resolver = context.getApplicationContext().getContentResolver();
            int value = Settings.System.getInt(resolver, Settings.System.WIFI_SLEEP_POLICY,
                    Settings.System.WIFI_SLEEP_POLICY_DEFAULT);
            if (Settings.System.WIFI_SLEEP_POLICY_NEVER != value) {
                Settings.System.putInt(resolver, Settings.System.WIFI_SLEEP_POLICY,
                        Settings.System.WIFI_SLEEP_POLICY_NEVER);
            }
            if (mWifiStatus == -1) {
                mWifiStatus = value;
            }
            return value;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 取消wifi一直唤醒
     */
    @SuppressWarnings("deprecation")
    public static void keepWifiOff(Context context, int newvalue) {
        ContentResolver resolver = context
                .getApplicationContext().getContentResolver();
        int value = Settings.System.getInt(resolver, Settings.System.WIFI_SLEEP_POLICY,
                Settings.System.WIFI_SLEEP_POLICY_DEFAULT);
        if (newvalue != value) {
            Settings.System.putInt(resolver, Settings.System.WIFI_SLEEP_POLICY,
                    newvalue);
        }
    }


    /**
     * Android 2.2.x
     *
     * @return
     */
    public static boolean hasFroyo() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO;
    }

    /**
     * Android 2.3
     * Android 2.3.1
     * Android 2.3.2
     *
     * @return
     */
    public static boolean hasGingerbread() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD;
    }
}
