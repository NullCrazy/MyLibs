package com.common;

import android.content.Context;

import com.common.crash.CrashHandler;

/**
 * @Description: 通用方法初始化类
 * @Author: xingguo.lei@luckincoffee.com
 * @Date: 2020-02-24 18:15
 */
public enum SnackCommon {
    /**
     * 唯一单例
     */
    INSTANCE;

    private Class defaultClass;
    private String packageName;

    public SnackCommon initCommonFunc(Class defaultClass, String packageName) {
        this.defaultClass = defaultClass;
        this.packageName = packageName;
        return this;
    }

    /**
     * @return 获取默认需要拉起的Activity类名
     */
    public Class getDefaultClass() {
        if (defaultClass == null) {
            throw new NullPointerException("请设置需要默认拉起的Activity");
        } else {
            return defaultClass;
        }
    }

    public String getPackageName() {
        return packageName;
    }

    public SnackCommon openCrashHandler(Context context) {
        return openCrashHandler(context, true);
    }

    public SnackCommon openCrashHandler(Context context, boolean isOpen) {
        if (isOpen) {
            CrashHandler.getInstance().init(context);
        }
        return this;
    }
}
