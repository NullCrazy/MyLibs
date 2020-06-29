package com.lucky.utils;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Environment;

/**
 * @author xiepengchong
 * @date 16/4/18
 * @Description: 系统信息包装类
 */
public class SystemWrapperUtil {

    /**
     * 最后一次点击的时间戳
     */
    private static long lastClickTime;

    /**
     * 防抖动
     *
     * @return true 表示没有达到间隔时间，false表示可以进行下一次点击操作
     */
    public static boolean isFastClick() {
        long time = System.currentTimeMillis();
        long timeD = time - lastClickTime;
        int maxTime = 800;
        int minTime = 0;

        if (minTime < timeD && timeD < maxTime) {
            return true;
        }
        lastClickTime = time;
        return false;
    }


    /**
     * 判断外部存储是否打开
     *
     * @return true 外部存储打开，false 外部存储关闭
     */
    public static boolean isExternalStorageEnable() {
        try {
            return isExternalStorageMounted() || !isExternalStorageRemovable();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 判断安装了外部存储
     *
     * @return true 安装了外部存储，false 不存在
     */
    public static boolean isExternalStorageMounted() {
        String state = Environment.getExternalStorageState();
        return state != null && state.equals(Environment.MEDIA_MOUNTED);
    }

    /**
     * Check if external storage is built-in or removable.
     *
     * @return True if external storage is removable (like an SD card), false
     * otherwise.
     */
    @SuppressLint("NewApi")
    public static boolean isExternalStorageRemovable() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            return Environment.isExternalStorageRemovable();
        }
        return true;
    }
}
