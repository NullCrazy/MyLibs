package com.luckincoffee.base;

import androidx.annotation.UiThread;

/**
 * @author xingguo.lei
 * date: 2018/10/31
 * explain:view层通用接口，里面的方法都是一些通用的方法，便于统一处理
 */
public interface IMvpView {
    /**
     * 弹出提示框
     *
     * @param msg
     */
    @UiThread
    void showShortMsg(String msg);

    /**
     * 弹出提示框
     *
     * @param msg
     */
    @UiThread
    void showLongMsg(String msg);

    /**
     * 展示等待框
     */
    void showLoading();

    /**
     * 隐藏等待框
     */
    @UiThread
    void dismissLoading();
}
