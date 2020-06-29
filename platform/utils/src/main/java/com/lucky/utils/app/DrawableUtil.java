package com.lucky.utils.app;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.widget.TextView;


/**
 * @author xingguolei
 * @date 2018/4/16
 * @Description 图片工具类
 */
public class DrawableUtil {

    /**
     * 动态设置textView的drawableRight属性 如果drawableResource=-1则不设置
     *
     * @param context          上下文
     * @param textView         当前需要处理的textView
     * @param drawableResource 图片资源
     */
    public static void setDrawableRight(Context context, TextView textView, int drawableResource) {
        try {
            if (drawableResource == -1) {
                textView.setCompoundDrawables(null, null, null, null);
            } else {
                //新API会引起兼容性问题，不建议修改
                Drawable drawable = context.getResources()
                        .getDrawable(drawableResource);
                // 设置边界
                drawable.setBounds(0, 0, drawable.getMinimumWidth(),
                        drawable.getMinimumHeight());
                // 画在右边
                textView.setCompoundDrawables(null, null, drawable, null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 动态设置textView的drawableLeft属性 如果drawableResource=-1则不设置
     *
     * @param context          上下文
     * @param textView         当前需要处理的textView
     * @param drawableResource 图片资源
     */
    public static void setDrawableLeft(Context context, TextView textView, int drawableResource) {
        try {
            if (drawableResource == -1) {
                textView.setCompoundDrawables(null, null, null, null);
            } else {
                Drawable drawable = context.getResources()
                        .getDrawable(drawableResource);
                // 设置边界
                drawable.setBounds(0, 0, drawable.getMinimumWidth(),
                        drawable.getMinimumHeight());
                // 画在左边
                textView.setCompoundDrawables(drawable, null, null, null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
