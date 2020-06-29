package com.lucky.lib.downloader.exception;
/**
 * @Description: 网络请求时的Exception
 * @Author: xmq mingqiang.xu@luckincoffee.com
 * @Date: 2019/1/24 下午10:04
 */
public class HttpException extends RuntimeException {
  public HttpException(String message) {
    super(message);
  }
}
