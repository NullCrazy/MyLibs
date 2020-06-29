package com.lucky.lib.downloader.exception;

/**
 * @Description: 下载中的异常信息
 * @Author: xmq mingqiang.xu@luckincoffee.com
 * @Date: 2019/1/24 下午10:04
 */
public class DownloadException extends RuntimeException{
    public DownloadException(String message) {
        super(message);
    }
}
