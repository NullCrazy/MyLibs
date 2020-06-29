package com.common.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.common.SnackCommon;

/**
 * @Description: 开机自启动
 * @Author: xingguo.lei@luckincoffee.com
 * @Date: 2019-12-07 12:35
 */
public class BootUpReceiver extends BroadcastReceiver {
    private static final String ACTION_BOOT = "android.intent.action.BOOT_COMPLETED";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(ACTION_BOOT)) {
            try {
                Intent bootStartIntent = new Intent(context, SnackCommon.INSTANCE.getDefaultClass());
                bootStartIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(bootStartIntent);
            } catch (Exception e) {
                //TODO 最好这里定一下失败了上报一下
                e.printStackTrace();
            }
        }
    }
}
