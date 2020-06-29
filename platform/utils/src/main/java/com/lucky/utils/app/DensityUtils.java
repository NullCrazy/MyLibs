package com.lucky.utils.app;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.os.Build;
import android.view.WindowManager;

import com.lucky.utils.LkUtils;

/**
 * @Description: 量度工具，后期会再优化
 * @Author: fengzeyuan
 * @Date: 2020/4/26 10:15 AM
 */
public class DensityUtils {


    /**
     * 根据手机的分辨率从dp -> px的转化
     *
     * @param context
     * @param dpValue
     * @return
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从px -> dp的转化
     *
     * @param context
     * @param pxValue
     * @return
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 将px值转换为sp值，保证文字大小不变
     *
     * @param pxValue
     * @param pxValue （DisplayMetrics类中属性scaledDensity）
     * @return
     */
    public static int px2sp(Context context, float pxValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

    /**
     * 将sp值转换为px值，保证文字大小不变
     *
     * @param spValue
     * @param spValue （DisplayMetrics类中属性scaledDensity）
     * @return
     */
    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    /**
     * 获取屏幕宽高信息
     * @return 
     */
    public static int[] getScreenSize() {
        Point point = new Point();
        try {
            WindowManager wm = (WindowManager) LkUtils.getApp().getSystemService(Context.WINDOW_SERVICE);
            if (wm == null){
                return new int[]{-1, -1};
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                wm.getDefaultDisplay().getRealSize(point);
            } else {
                wm.getDefaultDisplay().getSize(point);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new int[]{point.x, point.y};
    }

    /**
     * 获取屏幕高信息
     * @return
     */
    public static int getScreenHeight() {
        WindowManager wm = (WindowManager) LkUtils.getApp().getSystemService(Context.WINDOW_SERVICE);
        if (wm == null){
            return -1;
        }
        Point point = new Point();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            wm.getDefaultDisplay().getRealSize(point);
        } else {
            wm.getDefaultDisplay().getSize(point);
        }
        return point.y;
    }

    /**
     * 获取屏幕宽信息
     * @return
     */
    public static int getScreenWidth() {
        WindowManager wm = (WindowManager) LkUtils.getApp().getSystemService(Context.WINDOW_SERVICE);
        if (wm == null){
            return -1;
        } 
        Point point = new Point();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            wm.getDefaultDisplay().getRealSize(point);
        } else {
            wm.getDefaultDisplay().getSize(point);
        }
        return point.x;
    }


    /**
     * 状态栏的高度
     */
    private static int statusBarHeight = -1;
    /**
     * 获取顶部status bar 高度
     * @return status bar 高度
     */
    public static int getStatusBarHeight() {
        if (statusBarHeight <= 0) {
            Resources resources = LkUtils.getApp().getResources();
            int resourceId = resources.getIdentifier("status_bar_height", "dimen", "android");
            statusBarHeight = resources.getDimensionPixelSize(resourceId);
        }
        return statusBarHeight;
    }
}
