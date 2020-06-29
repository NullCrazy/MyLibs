package com.lucky.utils.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

/**
 *
 * @author zhanghongjun
 * @date 2017/9/12
 * activity之间跳转
 */

public class JumpUtil {
    private JumpUtil() {
    }

    public static void jumpTo(Context context, Class<?> pClass) {
        jumpTo(context, pClass, null);
    }

    public static void jumpTo(Context context, Class<?> pClass, Bundle bundle) {
        if (context instanceof Activity) {
            Activity activity = (Activity) context;
            Intent intent = new Intent(activity, pClass);
            if (bundle != null) {
                intent.putExtras(bundle);
            }
            activity.startActivity(intent);
        } else {
            Log.e("zh", "context not a activity instance");
        }
    }

    public static void jumpToForResult(Context context, Class<?> pClass, int pRequestCode) {
        jumpToForResult(context, pClass, pRequestCode, null);
    }

    public static void jumpToForResult(Context context, Class<?> pClass, int pRequestCode,
                                       Bundle pBundle) {
        if (context instanceof Activity) {
            Activity activity = (Activity) context;
            Intent intent = new Intent(activity, pClass);
            if (pBundle != null) {
                intent.putExtras(pBundle);
            }
            activity.startActivityForResult(intent, pRequestCode);
        } else {
            Log.e("wh", "context not a activity instance");
        }
    }
}
