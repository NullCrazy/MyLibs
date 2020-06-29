package com.lucky.lib.lchttp2rx.builder;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.lucky.lib.http.HttpBaseResponse;
import com.lucky.lib.http.HttpCacheLevel;
import com.lucky.lib.http.HttpClient;
import com.lucky.lib.http.HttpPostRequestBuilder;
import com.lucky.lib.lchttp2rx.result.ReactiveResult;
import com.lucky.lib.lchttp2rx.result.Result;

import java.io.File;
import java.util.List;
import java.util.Map;

import io.reactivex.rxjava3.core.Observable;
import okhttp3.MediaType;

/**
 * @author: xingguo.lei
 * date: 2018/11/20
 * func:
 */
public class PostBuilder extends HttpPostRequestBuilder implements Result {
    private Result delegate;

    public PostBuilder(HttpClient httpClient) {
        super(httpClient);
        delegate = new ReactiveResult(this);
    }

    @Override
    public PostBuilder file(String key, File file, MediaType mediaType) {
        super.file(key, file, mediaType);
        return this;
    }

    @Override
    public PostBuilder tag(@NonNull Object o) {
        return (PostBuilder) super.tag(o);
    }

    @Override
    public PostBuilder param(@NonNull String key, @Nullable Object value) {
        return (PostBuilder) super.param(key, value);
    }

    @Override
    public PostBuilder params(@NonNull Map<String, Object> maps) {
        return (PostBuilder) super.params(maps);
    }

    @Override
    public PostBuilder paramObject(@NonNull Object value) {
        return (PostBuilder) super.paramObject(value);
    }

    @Override
    public PostBuilder paramGet(@NonNull String key, @Nullable Object value) {
        return (PostBuilder) super.paramGet(key, value);
    }

    @Override
    public PostBuilder paramsGet(@NonNull Map<String, Object> maps) {
        return (PostBuilder) super.paramsGet(maps);
    }

    @Override
    public PostBuilder paramObjectGet(@NonNull Object value) {
        return (PostBuilder) super.paramObjectGet(value);
    }

    @Override
    public PostBuilder url(@NonNull String url) {
        return (PostBuilder) super.url(url);
    }

    @Override
    public PostBuilder cache(@NonNull HttpCacheLevel level) {
        return (PostBuilder) super.cache(level);
    }

    @Override
    public PostBuilder header(@NonNull String key, String value) {
        return (PostBuilder) super.header(key, value);
    }

    @Override
    public <T> Observable<HttpBaseResponse<T>> baseResponseObservable(Class<T> clazz) {
        return delegate.baseResponseObservable(clazz);
    }

    @Override
    public <T> Observable<HttpBaseResponse<List<T>>> baseResponseObservableArray(Class<T> clazz) {
        return delegate.baseResponseObservableArray(clazz);
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