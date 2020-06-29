package com.lucky.utils.app;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import androidx.annotation.StringRes;

import java.lang.reflect.Field;

public class Store7_1Toast {
    private static Field sField_TN;
    private static Field sField_TN_Handler;
    private static Toast mToast;


    static {
        try {
            sField_TN = Toast.class.getDeclaredField("mTN");
            sField_TN.setAccessible(true);
            sField_TN_Handler = sField_TN.getType().getDeclaredField("mHandler");
            sField_TN_Handler.setAccessible(true);
        } catch (Exception e) {

        }
    }

    private Store7_1Toast() {
    }


    public static void show(Context context, CharSequence message, int duration) {
        if (mToast == null) {
            mToast = Toast.makeText(context.getApplicationContext(), message, duration);
            hook(mToast);
        } else {
            mToast.setDuration(duration);
            mToast.setText(message);
        }
        mToast.show();
    }

    public static void show(Context context, @StringRes int resId, int duration) {
        if (mToast == null) {
            mToast = Toast.makeText(context.getApplicationContext(), resId, duration);
            hook(mToast);
        } else {
            mToast.setDuration(duration);
            mToast.setText(context.getString(resId));
        }
        mToast.show();
    }

    private static void hook(Toast toast) {
        try {
            Object tn = sField_TN.get(toast);
            Handler preHandler = (Handler) sField_TN_Handler.get(tn);
            sField_TN_Handler.set(tn, new SafeHandler(preHandler));
        } catch (Exception e) {
        }
    }


    private static class SafeHandler extends Handler {
        private Handler impl;

        public SafeHandler(Handler impl) {
            this.impl = impl;
        }

        @Override
        public void dispatchMessage(Message msg) {
            try {
                super.dispatchMessage(msg);
            } catch (Exception e) {
            }
        }

        @Override
        public void handleMessage(Message msg) {
            impl.handleMessage(msg);//需要委托给原Handler执行
        }
    }
}