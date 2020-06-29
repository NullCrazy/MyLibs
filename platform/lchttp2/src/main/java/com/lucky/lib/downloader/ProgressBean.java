package com.lucky.lib.downloader;

import androidx.annotation.CheckResult;

import java.util.Arrays;

/**
 * @Description: 此类的所有参数不允许不同包类修改数值，只能进行<code>get*()</code>操作
 * @Author: xmq mingqiang.xu@luckincoffee.com
 * @Date: 2019/1/24 下午10:03
 */
public class ProgressBean {

    /**
     * 下载中的状态 {@link DownloadThread.DownloadState}
     */
    @DownloadThread.DownloadState int  state;

    /**
     * 下载的开始位置
     */
    long startPosition;

    /**
     * 下载中的当前位置
     */
    long currentPosition;

    /**
     * 上次下载的位置
     */
    long lastPosition;

    /**
     * 下载的总长度，当chunk方式时，此时的长度为负数
     */
    long contentLength;

    /**
     * 下载的progress ，long[][] ,二维数组，position1：线程position， position2：下载的当前position
     */
    long[][] progressDetail;

    /**
     * 下载的线程标记，用作多线程时区分线程
     */
    int tag;

    /**
     * 下载的数据信息
     * @param tag 下载的线程标记，用作多线程时区分线程
     */
    public ProgressBean(int tag) {
        this.tag = tag;
    }

    /**
     * 下载的数据信息
     */
    public ProgressBean() {
    }

    /**
     * 获得下载状态
     * @return 下载状态
     */
    public @DownloadThread.DownloadState int getState() {
        return state;
    }

    /**
     * 获取下载当前位置
     * @return 当前位置
     */
    public long getCurrentPosition() {
        return currentPosition;
    }

    /**
     * 获取当前位置
     * 此方法不对外暴漏，库内部调用
     * @return 当前位置
     */
    long getInnerCurrentPosition() {
        long increase = currentPosition - lastPosition;
        lastPosition = currentPosition;
        return increase;
    }

    /**
     * 获取文件长度
     * @return 文件长度
     */
    @CheckResult
    public long getContentLength() {
        return contentLength;
    }

    /**
     * 获得进程信息
     * @return thread detail in multi thread ,null in single thread
     */
    @CheckResult
    public long[][] getProgressDetail() {
        if (progressDetail ==null || progressDetail.length ==0) {
            return new long[0][3];
        }
        long[][] temp = new long[progressDetail.length][3];
        System.arraycopy(progressDetail,0,temp,0,progressDetail.length);
        return temp;
    }

    /**
     * 将对象转为string
     * @return 转换之后的string
     */
    @Override
    public String toString() {
        return "ProgressBean{" +
                "state=" + state +
                ", currentPosition=" + currentPosition +
                ", contentLength=" + contentLength +
                ", progressDetail=" + Arrays.toString(progressDetail) +
                '}';
    }
}