package com.lucky.utils;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;

/**
 * @author mylo
 * @date 2017/9/22.
 * @Description: 动画工具类
 * <p>
 * 代码review于2019/5/29,更新:xingguo.lei
 */

public class AnimationUtil {

    /**
     * 起点
     */
    static int i = 1;

    /**
     * 画贝塞尔曲线
     *
     * @param context       上下文
     * @param startView     初始view
     * @param endView       结束view
     * @param parentView    父view
     * @param aniDrawable   动画背景
     * @param aniViewLength 动画长度
     * @param listener      回调监听
     */
    public static void drawBezier(Context context, View startView, View endView, final ViewGroup parentView, Drawable aniDrawable, int aniViewLength, final AnimationFinishListener listener) {
        //间隔时间
        int duration = 400;
        int frameDelay = 50;
        int arrayLength = 2;
        int average = 2;

        final PathMeasure mPathMeasure;
        //aniView
        final ImageView aniView = new ImageView(context);
        aniView.setImageDrawable(aniDrawable);
        //初始大小
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(aniViewLength, aniViewLength);
        parentView.addView(aniView, params);

        int[] parentLocation = new int[arrayLength];
        parentView.getLocationInWindow(parentLocation);
        //算起始坐标
        int[] startLocation = new int[arrayLength];
        startView.getLocationInWindow(startLocation);
        //结束坐标
        int[] endLocation = new int[arrayLength];
        endView.getLocationInWindow(endLocation);

        //中心往外走起
        float startX = startLocation[0] - parentLocation[0];
        float startY = startLocation[1] - parentLocation[1] - aniViewLength / average;

        //终点位置
        float toX = endLocation[0] - parentLocation[0] + (endView.getWidth() - aniViewLength) / average;
        float toY = endLocation[1] - parentLocation[1] + endView.getHeight();
        i = 1;
        Path path = new Path();
        path.moveTo(startX, startY);
        path.quadTo(toX, startY, toX, toY);
        mPathMeasure = new PathMeasure(path, false);

        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, mPathMeasure.getLength());
        valueAnimator.setDuration(duration);
        ValueAnimator.setFrameDelay(frameDelay);
        valueAnimator.setInterpolator(new LinearInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                if (i < 1) {
                    i++;
                } else {
                    int arrayLength = 2;
                    float[] currPostion = new float[arrayLength];
                    float value = (Float) animation.getAnimatedValue();
                    mPathMeasure.getPosTan(value, currPostion, null);
                    aniView.setTranslationX(currPostion[0]);
                    aniView.setTranslationY(currPostion[1]);
                    i = 0;
                }
            }
        });
        valueAnimator.start();

        setListener(valueAnimator, parentView, aniView, listener);
    }

    /**
     * 设置动画
     *
     * @param valueAnimator 动画
     * @param parentView    父布局
     * @param aniView       动画属性
     * @param listener      接口回调
     */
    private static void setListener(ValueAnimator valueAnimator, final ViewGroup parentView, final ImageView aniView, final AnimationFinishListener listener) {
        valueAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                // 动画结束后，自己做处理
                parentView.removeView(aniView);
                if (listener != null) {
                    listener.onAnimationFinished();
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    /**
     * 贝塞尔曲线 细节
     *
     * @param context       上下文
     * @param startView     初始view
     * @param endView       结束view
     * @param parentView    父view
     * @param aniDrawable   动画背景
     * @param aniViewLength 动画长度
     * @param listener      回调监听
     */
    public static void drawBezierInProductDetail(Context context, View startView, View endView, final ViewGroup parentView, Drawable aniDrawable, int aniViewLength, final AnimationFinishListener listener) {
        //间隔时间
        int duration = 300;
        int frameDelay = 30;
        int arrayLength = 2;
        int average = 2;
        final PathMeasure mPathMeasure;
        //aniView
        final ImageView aniView = new ImageView(context);
        aniView.setImageDrawable(aniDrawable);
        //初始大小
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(aniViewLength, aniViewLength);
        parentView.addView(aniView, params);

        int[] parentLocation = new int[arrayLength];
        parentView.getLocationInWindow(parentLocation);
        //算起始坐标
        int[] startLocation = new int[arrayLength];
        startView.getLocationInWindow(startLocation);
        //结束坐标
        int[] endLocation = new int[arrayLength];
        endView.getLocationInWindow(endLocation);

        //中心往外走起
        float startX = startLocation[0] - parentLocation[0] + (startView.getWidth() - aniViewLength) / average;
        float startY = startLocation[1] - parentLocation[1] + (startView.getHeight() - aniViewLength) / average;

        //终点位置
        float toX = endLocation[0] - parentLocation[0] + (endView.getWidth() - aniViewLength) / average;
        float toY = endLocation[1] - parentLocation[1] + (endView.getHeight() - aniViewLength) / average;

        Path path = new Path();
        path.moveTo(startX, startY);
        path.quadTo((startX + toX) / average, startY, toX, toY);
        mPathMeasure = new PathMeasure(path, false);

        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, mPathMeasure.getLength());
        valueAnimator.setDuration(duration);
        ValueAnimator.setFrameDelay(frameDelay);
        valueAnimator.setInterpolator(new LinearInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int arrayLength = 2;
                float[] currPostion = new float[arrayLength];
                float value = (Float) animation.getAnimatedValue();
                mPathMeasure.getPosTan(value, currPostion, null);
                aniView.setTranslationX(currPostion[0]);
                aniView.setTranslationY(currPostion[1]);
            }
        });
        valueAnimator.start();

        valueAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                // 动画结束后，自己做处理
                parentView.removeView(aniView);
                if (listener != null) {
                    listener.onAnimationFinished();
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    /**
     * @author mylo
     * @date 2017/9/22.
     * @Description: 动画回调接口
     */
    public interface AnimationFinishListener {
        /**
         * 动画结束时的回调
         */
        void onAnimationFinished();
    }
}
