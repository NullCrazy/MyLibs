package com.lucky.lib.lchttp2rx;

import com.lucky.lib.http.HttpClient;
import com.lucky.lib.http.HttpPostRequestBuilder;
import com.lucky.lib.lchttp2rx.builder.GetBuilder;
import com.lucky.lib.lchttp2rx.builder.PostBuilder;

public final class ReactiveHttp {
    private static HttpClient mHttpClient;

    private ReactiveHttp() {
    }

    /**
     * 初始化操作
     *
     * @param httpClient 默认全局http请求
     */
    public static void init(HttpClient httpClient) {
        if (mHttpClient == null) {
            mHttpClient = httpClient;
        }
    }

    /**
     * get请求
     *
     * @return
     */
    public static GetBuilder getRequest() {
        return new GetBuilder(get());
    }

    /**
     * post请求
     */
    public static PostBuilder postRequest() {
        return new PostBuilder(get(), HttpPostRequestBuilder.POST);
    }

    private static HttpClient get() {
        if (mHttpClient == null) {
            throw new NullPointerException("HttpClient is NULL, Please call ReactiveHttp.init ！");
        }
        return mHttpClient;
    }
}

