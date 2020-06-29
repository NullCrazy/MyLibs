package com.lucky.utils.app;

import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.IntDef;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.lucky.utils.StringUtil;
import com.lucky.utils.log.LogUtil;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;


/**
 * @author xingguo.lei
 * @date 2018/12/26
 * @Description Toast工具类
 */
public final class ToastUtil {

    /**
     * 当前吐司对象Handler
     */
    private static ToastHandler mToastHandler;
    /**
     * 吐司对象
     */
    private volatile static Toast mToast;
    /**
     * 默认的类型
     */
    public static final int DEFAULT = 0;
    /**
     * 门店端
     */
    public static final int STORE = 1;
    /**
     * toast应用于app的类型
     */
    private static @TYPE
    int type = DEFAULT;

    /**
     * @Description: Toast应用的app
     * @Author: xmq mingqiang.xu@luckincoffee.com
     * @Date: 2019/3/28 下午1:25
     */
    @IntDef({DEFAULT, STORE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface TYPE {
    }

    /**
     * toast应用app的类型
     *
     * @param type app类型{@link #DEFAULT} 默认 {@link #STORE} 门店端
     */
    public static void setType(@TYPE int type) {
        ToastUtil.type = type;
    }

    /**
     * Toast 短时间Toast
     * 可在UI线程 跟子线程执行
     *
     * @param context activity
     * @param msg     弹出的消息文本
     */
    public static void showShortMsg(Context context, String... msg) {
        showMsg(context, StringUtil.switchToString(msg), Toast.LENGTH_SHORT, null);
    }

    public static void showShortMsg(Context context, int gravity, String... msg) {
        showMsg(context, StringUtil.switchToString(msg), gravity, Toast.LENGTH_SHORT, null);
    }

    /**
     * Toast 长时间Toast
     * 可在UI线程 跟子线程执行
     *
     * @param context activity
     * @param msg     弹出的消息文本
     */
    public static void showLongMsg(Context context, String... msg) {
        showMsg(context, StringUtil.switchToString(msg), Toast.LENGTH_LONG, null);
    }

    public static void showLongMsg(Context context, int gravity, String... msg) {
        showMsg(context, StringUtil.switchToString(msg), gravity, Toast.LENGTH_LONG, null);
    }

    /**
     * 自定义Toast ，可自定义View
     * 可在UI线程 跟子线程执行
     *
     * @param context  activity
     * @param duration 显示时长{@link Toast#LENGTH_LONG} {@link Toast#LENGTH_SHORT}
     * @param view     自定义Toast 的View
     */
    public static void showCustomMsg(Context context, String msg, int duration, View view) {
        showMsg(context, msg, Gravity.BOTTOM, duration, view);
    }

    private static void showMsg(final Context context, final String msg, final int duration, final View toastView) {
        showMsg(context, msg, -1, duration, toastView);
    }

    /**
     * 弹出信息
     *
     * @param context   上下文
     * @param msg       弹出信息
     * @param duration  时长
     * @param toastView 弹出框的view
     */
    private static void showMsg(final Context context, final String msg, final int gravity, final int duration, final View toastView) {
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.N_MR1) {
            // NOTE 7.1.X 需要做特殊处理
            // 入参校验
            if (context == null) {
                return;
            }
            if (!(context instanceof AppCompatActivity)) {
                // 除了activity以外的其他context 依然使用原来的代码逻辑
                showCustomToast(context, msg, gravity, duration);
                return;
            }
            // 校验activity的生命周期
            AppCompatActivity hostActivity = (AppCompatActivity) context;
            if (hostActivity.isFinishing()) {
                return;
            }

            if (hostActivity.getWindow() == null) {
                return;
            }
            if (hostActivity.getWindow().getDecorView() == null) {
                return;
            }
            if (hostActivity.getWindow().getDecorView().getRootView() == null) {
                return;
            }
            if (type == STORE) {
                Store7_1Toast.show(context, msg, duration);
                return;
            }
            Snackbar.make(hostActivity.getWindow().getDecorView().getRootView().findViewById(android.R.id.content), msg, Snackbar.LENGTH_SHORT)
                    .setDuration(duration)
                    .show();
        } else {
            // 除7.1.x 以外的系统版本，依然走原来的流程
            showCustomToast(context, msg, gravity, duration);
        }

    }

    /**
     * 弹出自定义的Toast
     *
     * @param context  上下文
     * @param msg      弹出信息
     * @param duration 时长
     */
    private static void showCustomToast(final Context context, final String msg, final int gravity, final int duration) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            show(context, msg, gravity, duration);
        } else {
            LogUtil.i("Toast exec in not  UI thread");
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    show(context, msg, gravity, duration);
                }
            });
        }
    }

    /**
     * 显示弹出信息view
     *
     * @param context  上下文
     * @param msg      弹出信息
     * @param duration 时长
     */
    private static void show(Context context, String msg, int gravity, int duration) {
        if (TextUtils.isEmpty(msg)) {
            return;
        }
        //8.0以上不需要使用单利模式Toast
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Toast toast = Toast.makeText(context, msg, duration);
            if (gravity != -1) {
                toast.setGravity(gravity, 0, 0);
            }
            toast.show();
        } else {
            checkToastState(context);
            mToast.setDuration(duration);
            mToast.setText(msg);
            if (gravity != -1) {
                mToast.setGravity(gravity, 0, 0);
            }

            mToast.show();
        }
    }

    /**
     * 检查Toast状态
     *
     * @param context 上下文
     */
    private static void checkToastState(Context context) {
        if (mToast == null) {
            synchronized (ToastUtil.class) {
                if (mToast == null) {
                    mToast = Toast.makeText(context.getApplicationContext(), "", Toast.LENGTH_SHORT);
                    mToastHandler = new ToastHandler(mToast);
                }
            }
        }
    }
}