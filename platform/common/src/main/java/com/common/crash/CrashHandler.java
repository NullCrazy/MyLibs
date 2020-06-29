package com.common.crash;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.common.SnackCommon;

import java.lang.reflect.UndeclaredThrowableException;
import java.util.Arrays;

/**
 * 异常捕获
 *
 * @author jinyang.gao
 * @date 2019/07/16
 */
public final class CrashHandler implements Thread.UncaughtExceptionHandler {
    private static final String TAG = "CrashHandler";

    @SuppressLint("StaticFieldLeak")
    private static volatile CrashHandler instance = null;
    private Thread.UncaughtExceptionHandler mDefaultHandler;
    private Context mContext;

    public static CrashHandler getInstance() {
        if (instance != null) {
            return instance;
        }

        synchronized (CrashHandler.class) {
            if (instance == null) {
                instance = new CrashHandler();
            }
            return instance;
        }
    }

    private CrashHandler() {
    }

    public void init(Context context) {
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
        this.mContext = context;
    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {

        String msg = null;
        if (e instanceof UndeclaredThrowableException) {
            Throwable targetEx = ((UndeclaredThrowableException) e).getUndeclaredThrowable();
            if (targetEx != null) {
                msg = targetEx.getMessage();
            }
        } else {
            msg = e.getMessage();
        }

        Log.e(TAG,msg);

        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
        restartApp(mContext);
        mDefaultHandler.uncaughtException(t, e);
    }

    public static void restartApp(Context context) {
        Intent intent = new Intent(context, SnackCommon.INSTANCE.getDefaultClass());
        @SuppressLint("WrongConstant")
        PendingIntent restartIntent = PendingIntent.getActivity(context, 0, intent, Intent.FLAG_ACTIVITY_NEW_TASK);
        AlarmManager mgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 1000, restartIntent);
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    public String getMessage(Throwable e) {
        String msg = null;
        if (e instanceof UndeclaredThrowableException) {
            Throwable targetEx = ((UndeclaredThrowableException) e).getUndeclaredThrowable();
            if (targetEx != null) {
                msg = Arrays.toString(targetEx.getStackTrace());
            }
        } else {
            msg = Arrays.toString(e.getStackTrace());
        }
        return msg;
    }
}
