package com.lucky.utils.log;

import android.util.Log;

import com.orhanobut.logger.LogLevel;
import com.orhanobut.logger.Logger;

/**
 * @author: xingguo.lei
 * date: 2018/11/7
 * @Description: 默认的日志输出
 */
public class DefaultLog implements ILogger {
    /**
     * 日志开关，true为打开
     */
    private boolean mLogEnable;
    /**
     * 每次打印日志的最大长度
     */
    private static final int MAX_LENGTH = 2000;

    /**
     * 构造器
     */
    DefaultLog() {
    }

    @Override
    public void i(String message) {
        Logger.i(message);
    }

    @Override
    public void i(String tag, String message) {
        if (mLogEnable) {
            logMode(tag, get(message), "i");
        }
    }

    @Override
    public void d(String message) {
        Logger.d(message);
    }

    @Override
    public void d(String tag, String message) {
        if (mLogEnable) {
            logMode(tag, get(message), "d");
        }
    }

    @Override
    public void e(String message) {
        Logger.e(message);
    }

    @Override
    public void e(String tag, String message) {
        if (mLogEnable) {
            logMode(tag, get(message), "e");
        }
    }

    @Override
    public void e(String message, Throwable e) {
        Logger.e(e, message);
    }

    @Override
    public void w(String message) {
        Logger.w(message);
    }

    @Override
    public void w(String tag, String message) {
        if (mLogEnable) {
            logMode(tag, get(message), "w");
        }
    }

    @Override
    public void v(String message) {
        Logger.v(message);
    }

    @Override
    public void v(String tag, String message) {
        if (mLogEnable) {
            logMode(tag, get(message), "v");
        }
    }

    /**
     * 判断日志以何种形式输出
     *
     * @param tag     日志的tag
     * @param message 日志输出的数据
     * @param mode    输出的类型
     */
    private void logMode(String tag, String message, String mode) {
        if (message.length() > MAX_LENGTH) {
            for (int i = 0; i < message.length(); i += MAX_LENGTH) {
                //当前截取的长度<总长度则继续截取最大的长度来打印
                if (i + MAX_LENGTH < message.length()) {
                    printLog(tag + i, message.substring(i, i + MAX_LENGTH), mode);
                } else {
                    //当前截取的长度已经超过了总长度，则打印出剩下的全部信息
                    printLog(tag + i, message.substring(i), mode);
                }
            }
        } else {
            //直接打印
            printLog(tag, message, mode);
        }
    }

    /**
     * 打印日志
     *
     * @param tag     日志的tag
     * @param message 日志输出的数据
     * @param mode    输出的类型
     */
    private void printLog(String tag, String message, String mode) {
        switch (mode.toLowerCase()) {
            case "v":
                Log.v(tag, message);
                break;
            case "w":
                Log.w(tag, message);
                break;
            case "d":
                Log.d(tag, message);
                break;
            case "i":
                Log.i(tag, message);
                break;
            case "e":
                Log.e(tag, message);
                break;
            default:
                Log.i(tag, message);
                break;
        }
    }

    @Override
    public void json(String json) {
        Logger.json(json);
    }

    @Override
    public void logEnable(boolean enable) {
        mLogEnable = enable;
        Logger.init("luckyclient")
                .logLevel(enable ? LogLevel.FULL : LogLevel.NONE);
    }

    /**
     * 判断当前输出的日志是否是null,如果是null返回默认值
     *
     * @param message 日子输出的数据
     * @return 返回判断后的数据
     */
    private String get(String message) {
        if (message == null) {
            return "";
        } else {
            return message;
        }
    }
}
