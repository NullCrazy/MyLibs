package com.lucky.utils.app;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Build;
import android.util.DisplayMetrics;

/**
 * @author tengxs
 * @date 2017/8/15
 * @Description: 应用工具类.
 */
public class AppUtil {


    /**
     * @param context 上下文
     * @return 获取屏幕尺寸与密度.
     */
    public static DisplayMetrics getDisplayMetrics(Context context) {
        Resources mResources;
        if (context == null) {
            mResources = Resources.getSystem();

        } else {
            mResources = context.getResources();
        }
        DisplayMetrics mDisplayMetrics = mResources.getDisplayMetrics();
        return mDisplayMetrics;
    }

    /**
     * 应用程序运行命令获取 Root权限，设备必须已破解(获得ROOT权限)
     *
     * @param context 上下文
     * @return 应用程序是/否获取Root权限，有则返回true,否则返回false
     * <p>
     * 此方法即将不可用，由于工信部打击获取用户隐私，故此方法不再判断root,直接返回false
     */
    @Deprecated
    public static boolean getRootPermission(Context context) {
        return false;
    }



    /**
     * @param context 上下文
     * @return 单个App版本号name
     **/
    public static String getAppVersion(Context context) {
        PackageManager manager = context.getPackageManager();
        PackageInfo info = null;
        try {
            info = manager.getPackageInfo(context.getPackageName(), 0);
            String version = info.versionName;
            return version;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "1.0.0";
    }

    /**
     * @param context 上下文
     * @return 单个App版本号code
     **/
    public static int getAppVersionCode(Context context) {
        PackageManager manager = context.getPackageManager();
        PackageInfo info = null;
        try {
            info = manager.getPackageInfo(context.getPackageName(), 0);
            int versionCode = info.versionCode;
            return versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static String getDeviceId() {
        return Build.SERIAL;
    }
}
