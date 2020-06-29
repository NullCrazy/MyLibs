package com.lucky.utils.app;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import com.lucky.utils.LkUtils;

/**
 * @author xingguolei
 * @date 2017/8/15
 * @Description: Application工具类
 */
public class ApplicationUtils {

    /**
     * 是否为主进程
     *
     * @param context 上下文
     * @return 如果是主进程返回true, 否则返回false
     */
    public static boolean isMainProcess(Context context) {
        String process = getProcessName(context);
        return process != null && process.equals(context.getPackageName());
    }

    /**
     * 获取当前进程名称
     *
     * @param context 上下文
     * @return 当前进程名字
     */
    public static String getProcessName(Context context) {
        int pid = android.os.Process.myPid();
        ActivityManager mActivityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo appProcess : mActivityManager
                .getRunningAppProcesses()) {
            if (appProcess.pid == pid) {
                return appProcess.processName;
            }
        }
        return null;
    }

    /**
     * 获取渠道号
     */
    public static String getAppChanel(){
        String appChannel = "channel";
        try {
            ApplicationInfo info = LkUtils.getApp().getPackageManager().getApplicationInfo(LkUtils.getApp().getPackageName(), PackageManager.GET_META_DATA);
            appChannel = info.metaData.getString("BUGLY_APP_CHANNEL");
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return appChannel;
    }
}
