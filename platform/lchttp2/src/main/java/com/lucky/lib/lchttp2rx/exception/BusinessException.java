package com.lucky.lib.lchttp2rx.exception;

/**
 * @author: xingguo.lei
 * date: 2018/11/20
 * func:
 */
public class BusinessException extends Throwable {
    /**
     * 错误码
     */
   private int errorCode;
    /**
     * 业务码
     */
   private String busiCode;

    public BusinessException(String message, Throwable cause, int errorCode, String busiCode) {
        super(message, cause);
        this.errorCode = errorCode;
        this.busiCode = busiCode;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public String getBusiCode() {
        return busiCode;
    }

    public void setBusiCode(String busiCode) {
        this.busiCode = busiCode;
    }
}
