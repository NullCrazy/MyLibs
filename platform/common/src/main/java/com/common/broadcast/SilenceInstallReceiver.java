package com.common.broadcast;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.text.TextUtils;

import com.common.SnackCommon;

import java.util.List;

/**
 * @Description: app 覆盖安装后，需要自启动app
 * @Author: xingguo.lei@luckincoffee.com
 * @Date: 2019-12-05 17:14
 */
public class SilenceInstallReceiver extends BroadcastReceiver {
    /**
     * 覆盖安装的动作
     */
    private static final String ACTION = "android.intent.action.PACKAGE_REPLACED";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (!isAppLive(context, SnackCommon.INSTANCE.getPackageName()) && TextUtils.equals(intent.getAction(), ACTION)) {
            try {
                Intent intent2 = new Intent();
                ComponentName componentName = new ComponentName(SnackCommon.INSTANCE.getPackageName(), SnackCommon.INSTANCE.getDefaultClass().getName());
                intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent2.setComponent(componentName);
                context.startActivity(intent2);
            } catch (Exception e) {
                //TODO 最好这里定一下失败了上报一下
                e.printStackTrace();
            }
        }
    }

    /**
     * 判断当前APP是否还存活
     *
     * @param context 上下文
     * @param str     包名
     * @return true 代表APP还存活，否则代表不存在
     */
    public boolean isAppLive(Context context, String str) {
        ActivityManager am = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> list = am.getRunningTasks(100);
        boolean isAppRunning = false;
        //String MY_PKG_NAME = "你的包名";
        for (ActivityManager.RunningTaskInfo info : list) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                if (info.topActivity.getPackageName().equals(str)
                        || info.baseActivity.getPackageName().equals(str)) {
                    isAppRunning = true;
                    break;
                }
            }
        }
        return isAppRunning;
    }
}
