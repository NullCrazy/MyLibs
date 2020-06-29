package com.lucky.utils.log;

/**
 * @author: xingguo.lei
 * @date: 2018/11/7
 * @Description: 日志输出接口
 */
public interface ILogger {

    /**
     * 以i级别打印信息
     *
     * @param message 日志信息
     */
    void i(String message);

    /**
     * 以i级别打印信息
     *
     * @param tag     日志标志
     * @param message 日志信息
     */
    void i(String tag, String message);

    /**
     * 以d级别打印信息
     *
     * @param message 日志信息
     */
    void d(String message);

    /**
     * 以d级别打印信息
     *
     * @param tag     日志tag
     * @param message 日志信息
     */
    void d(String tag, String message);

    /**
     * 以e级别打印信息
     *
     * @param message 日志信息
     */
    void e(String message);

    /**
     * 以e级别打印信息
     *
     * @param tag     日志的标志
     * @param message 日志信息
     */
    void e(String tag, String message);

    /**
     * 以e级别打印信息
     *
     * @param message 日志信息
     * @param e       异常信息
     */
    void e(String message, Throwable e);

    /**
     * 以w级别打印日志
     *
     * @param message
     */
    void w(String message);

    /**
     * 以w级别打印日志
     *
     * @param tag     日志的标志
     * @param message 日志信息
     */
    void w(String tag, String message);

    /**
     * 以v级别打印
     *
     * @param message 日志信息
     */
    void v(String message);

    /**
     * 以v级别打印
     *
     * @param tag     日志的标志
     * @param message 日志信息
     */
    void v(String tag, String message);

    /**
     * 打印json字符串
     *
     * @param json 输出的json串
     */
    void json(String json);

    /**
     * 日志打印开关
     *
     * @param enable true打开日志，false关闭日志
     */
    void logEnable(boolean enable);
}
