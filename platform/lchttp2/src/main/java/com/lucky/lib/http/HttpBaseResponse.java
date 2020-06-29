package com.lucky.lib.http;

import java.io.Serializable;

/**
 * @Description: 请求最外层实体类 T ：content的类型
 * @Author: xmq mingqiang.xu@luckincoffee.com
 * @Date: 2019/3/27 下午5:25
 */
public class HttpBaseResponse<T> implements Serializable {
    /**
     * api远程数据码
     */
    private String busiCode;
    /**
     * 网路数据码
     */
    private int rtn;
    /**
     * 内容
     */
    private T data;
    /**
     * 数据消息
     */
    private String msg;
    /**
     * 网络状态
     */
    private String status;
    /**
     * 网络UID
     */
    private String uid;
    /**
     * 版本号
     */
    private String version;

    private PageInfo pageInfo;
    public long currentTime;

    public PageInfo getPageInfo() {
        return pageInfo;
    }

    public void setPageInfo(PageInfo pageInfo) {
        this.pageInfo = pageInfo;
    }

    /**
     * 获取api码
     * @return api远程数据码
     */
    public String getBusiCode() {
        return busiCode;
    }

    /**
     * 设置 api远程数据码
     * @param busiCode api远程数据码
     */
    public void setBusiCode(String busiCode) {
        this.busiCode = busiCode;
    }

    /**
     * 获取网路数据码
     * @return 网路数据码
     */
    public int getRtn() {
        return rtn;
    }

    /**
     * 设置网路数据码
     * @param rtn 网路数据码
     */
    public void setRtn(int rtn) {
        this.rtn = rtn;
    }

    /**
     * 获取内容
     * @return 内容
     */
    public T getData() {
        return data;
    }

    /**
     * 设置内容
     * @param data 内容
     */
    public void setData(T data) {
        this.data = data;
    }

    /**
     * 获取数据消息
     * @return 数据消息
     */
    public String getMsg() {
        return msg;
    }

    /**
     * 设置 数据消息
     * @param msg 数据消息
     */
    public void setMsg(String msg) {
        this.msg = msg;
    }

    /**
     * 获取网络状态
     * @return 网络状态
     */
    public String getStatus() {
        return status;
    }

    /**
     * 设置网络状态
     * @param status 网络状态
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * 获取网络UID
     * @return 网络UID
     */
    public String getUid() {
        return uid;
    }

    /**
     * 设置网络UID
     * @param uid 网络UID
     */
    public void setUid(String uid) {
        this.uid = uid;
    }

    /**
     * 获取版本号
     * @return 版本号
     */
    public String getVersion() {
        return version;
    }

    /**
     * 设置版本号
     * @param version 版本号
     */
    public void setVersion(String version) {
        this.version = version;
    }


    @Override
    public String toString() {
        return "LcNetBaseResponse{" +
                "busiCode='" + busiCode + '\'' +
                ", code=" + rtn +
                ", content=" + data +
                ", msg='" + msg + '\'' +
                ", status='" + status + '\'' +
                ", uid='" + uid + '\'' +
                ", version='" + version + '\'' +
                '}';
    }

    public static class PageInfo implements Serializable {
        public PageInfo() {
        }

        public boolean hasMore;
        public boolean hasContent;
        public int page;
        public int pageSize;

        public PageInfo(boolean hasMore, boolean hasContent, int page, int pageSize) {
            this.hasMore = hasMore;
            this.hasContent = hasContent;
            this.page = page;
            this.pageSize = pageSize;
        }
    }
}
