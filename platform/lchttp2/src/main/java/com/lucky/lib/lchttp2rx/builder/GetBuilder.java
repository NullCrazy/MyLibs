package com.lucky.lib.lchttp2rx.builder;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.lucky.lib.http.HttpBaseResponse;
import com.lucky.lib.http.HttpCacheLevel;
import com.lucky.lib.http.HttpClient;
import com.lucky.lib.http.HttpGetRequestBuilder;
import com.lucky.lib.lchttp2rx.result.ReactiveResult;
import com.lucky.lib.lchttp2rx.result.Result;

import java.util.List;
import java.util.Map;

import io.reactivex.rxjava3.core.Observable;

/**
 * @author: xingguo.lei
 * date: 2018/11/20
 * func:
 */
public class GetBuilder extends HttpGetRequestBuilder implements Result {
    private Result delegate;

    public GetBuilder(@NonNull HttpClient httpClient) {
        super(httpClient);
        delegate = new ReactiveResult(this);
    }

    @Override
    public GetBuilder tag(@NonNull Object o) {
        super.tag(o);
        return this;
    }

    @Override
    public GetBuilder param(@NonNull String key, @Nullable Object value) {
        super.param(key, value);
        return this;
    }

    @Override
    public GetBuilder params(@NonNull Map<String, Object> maps) {
        super.params(maps);
        return this;
    }

    @Override
    public GetBuilder paramObject(@NonNull Object value) {
        return (GetBuilder) super.paramObject(value);
    }

    @Override
    public GetBuilder url(@NonNull String url) {
        super.url(url);
        return this;
    }

    @Override
    public GetBuilder cache(@NonNull HttpCacheLevel level) {
        super.cache(level);
        return this;
    }

    @Override
    public GetBuilder header(@NonNull String key, String value) {
        super.header(key, value);
        return this;
    }

    @Override
    public <T> Observable<HttpBaseResponse<List<T>>> baseResponseObservableArray(Class<T> clazz) {
        return delegate.baseResponseObservableArray(clazz);
    }

    @Override
    public <T> Observable<HttpBaseResponse<T>> baseResponseObservable(Class<T> clazz) {
        return delegate.baseResponseObservable(clazz);
    }

    @Override
    public <T> Observable<T> observable(Class<T> clazz) {
        return delegate.observable(clazz);
    }

    @Override
    public <T> Observable<List<T>> observableArray(Class<T> clazz) {
        return delegate.observableArray(clazz);
    }
}
