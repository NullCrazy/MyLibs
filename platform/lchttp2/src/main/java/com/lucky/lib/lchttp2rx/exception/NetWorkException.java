package com.lucky.lib.lchttp2rx.exception;

/**
 * @author xingguo.lei
 * @data 2018/11/29
 * 网络异常
 */
public class NetWorkException extends Throwable {
    /**
     * 错误吗
     */
    private int code;

    /**
     * @param message 错误信息
     * @param cause
     * @param code 错误码
     */
    public NetWorkException(String message, Throwable cause, int code) {
        super(message, cause);
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
