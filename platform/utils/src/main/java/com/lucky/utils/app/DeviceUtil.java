package com.lucky.utils.app;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * @author zhanghongjun
 * @date 2017/10/12
 * @Description 设备信息工具类
 */
public class DeviceUtil {
    /**
     * 获取当前手机系统版本号
     *
     * @return 系统版本号
     */
    public static String getSystemVersion() {
        return android.os.Build.VERSION.RELEASE;
    }

    /**
     * 获取手机型号
     *
     * @return 手机型号
     */
    public static String getDeviceModel() {
        return android.os.Build.MODEL;
    }

    /**
     * 获取手机厂商
     *
     * @return 手机厂商
     */
    public static String getDeviceBrand() {
        return android.os.Build.BRAND;
    }

    /**
     * 获取网络类型
     * @param context 上下文
     * @return 网络类型
     */
    public static String getNetworkType(Context context) {
        try {
            context = context.getApplicationContext();
            ConnectivityManager connectMgr = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo info = connectMgr.getActiveNetworkInfo();
            if (info == null) {
                return "未联网";
            }

            //过时方法不能改，这里需要兼容低版本的API，改成替换后的API 会造成兼容性问题。
            if (info.getType() == ConnectivityManager.TYPE_WIFI) {
                return "WIFI网络";
            } else {
                return "移动网络";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "未知联网";
    }
}
