package com.lucky.utils.log;


/**
 * @author changshaowei
 * @Date 2017/7/26.
 * @Description: Log日志工具，封装logger
 */
public final class LogUtil {
    /**
     * 日志打印接口对象
     */
    private static ILogger mLogger;
    /**
     * 日志开关
     */
    private static boolean enable;

    /**
     * 构造器
     */
    private LogUtil() {
    }

    /**
     * 初始化log工具，在app入口处调用
     *
     * @param isLogEnable 是否打印log
     */
    public static void init(boolean isLogEnable) {
        enable = isLogEnable;
        mLogger = new DefaultLog();
        mLogger.logEnable(isLogEnable);
    }

    /**
     * 设置外部实现的日志打印
     * 注意调用初始化，开始打印，不然默认的是关闭打印
     *
     * @param logger 日志打印对象
     */
    public static void setLogger(ILogger logger) {
        mLogger = logger;
        mLogger.logEnable(enable);
    }

    /**
     * @return 判断日志打印对象是否合法
     */
    private static ILogger get() {
        if (mLogger == null) {
            throw new NullPointerException("Logger is null, Please call LogUtil init or setLogger！");
        }
        return mLogger;
    }

    /**
     * 向外暴露的静态打印方法级别d
     *
     * @param message 日志输出信息
     */
    public static void d(String message) {
        get().d(message);
    }

    /**
     * 向外暴露的静态打印方法级别d
     *
     * @param tag     日志标志
     * @param message 日志信息
     */
    public static void d(String tag, String message) {
        get().d(tag, message);
    }

    /**
     * 向外暴露的静态打印方法级别i
     *
     * @param message 日志信息
     */
    public static void i(String message) {
        get().i(message);
    }

    /**
     * 向外暴露的静态打印方法级别i
     *
     * @param tag     日志标志
     * @param message 日志信息
     */
    public static void i(String tag, String message) {
        get().i(tag, message);
    }

    /**
     * 向外暴露的静态打印方法级别w
     *
     * @param message 日志信息
     * @param e       异常对象
     */
    public static void w(String message, Throwable e) {
        String info = e != null ? e.toString() : "null";
        get().w(message + "：" + info);
    }

    /**
     * 向外暴露的静态打印方法级别w
     *
     * @param tag     日志标志
     * @param message 日志信息
     */
    public static void w(String tag, String message) {
        get().w(tag, message);
    }

    /**
     * 外暴露的静态打印方法级别v
     *
     * @param message 日志信息
     */
    public static void v(String message) {
        get().v(message);
    }

    /**
     * 外暴露的静态打印方法级别v
     *
     * @param tag     日志标志
     * @param message 日志信息
     */
    public static void v(String tag, String message) {
        get().v(tag, message);

    }

    /**
     * 向外暴露的静态打印方法级别e
     *
     * @param message 日志信息
     * @param e       异常对象
     */
    public static void e(String message, Throwable e) {
        get().e(message, e);
    }

    /**
     * 向外暴露的静态打印方法级别e
     *
     * @param message 日志信息
     */
    public static void e(String message) {
        get().e(message);
    }

    /**
     * 向外暴露的静态打印方法级别e
     *
     * @param tag     日志标志
     * @param message 日志信息
     */
    public static void e(String tag, String message) {
        get().e(tag, message);
    }

    /**
     * 打印json数据
     *
     * @param json 打印的json日志
     */
    public static void json(String json) {
        get().json(json);
    }
}