package com.lucky.lib.lchttp2rx.result;


import androidx.annotation.Nullable;

import com.lucky.lib.http.AbstractHttpCallBack;
import com.lucky.lib.http.AbstractLcRequest;
import com.lucky.lib.http.HttpBaseResponse;
import com.lucky.lib.lchttp2rx.exception.NetWorkException;

import java.util.List;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableEmitter;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import io.reactivex.rxjava3.core.ObservableSource;
import io.reactivex.rxjava3.functions.Function;

/**
 * @author xingguolei
 */
public class ReactiveResult implements Result {
    private AbstractLcRequest request;

    public ReactiveResult(AbstractLcRequest delegate) {
        this.request = delegate;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T>  Observable<HttpBaseResponse<T>> baseResponseObservable(final Class<T> clazz) {
        return baseResponseObservable(clazz,false);
    }

    @Override
    public <T> Observable<HttpBaseResponse<List<T>>> baseResponseObservableArray(Class<T> clazz) {
        return baseResponseObservable(clazz,true)
                .map(new Function<HttpBaseResponse<T>, HttpBaseResponse<List<T>>>() {
                    @Override
                    public HttpBaseResponse<List<T>> apply(HttpBaseResponse<T> tHttpBaseResponse) throws Exception {
                        return (HttpBaseResponse<List<T>>) tHttpBaseResponse;
                    }
                });
    }

    private  <T>  Observable<HttpBaseResponse<T>> baseResponseObservable(final Class<T> clazz, final boolean isContentArray) {
        return Observable.create(new ObservableOnSubscribe<HttpBaseResponse<T>>() {
            @Override
            public void subscribe(final ObservableEmitter<HttpBaseResponse<T>> emitter) throws Exception {
                AbstractHttpCallBack httpCallBack = new AbstractHttpCallBack(clazz) {
                    @Override
                    public void onSuccess(HttpBaseResponse httpBaseResponse) {
                        emitter.onNext(httpBaseResponse);
                        emitter.onComplete();
                    }

                    @Override
                    public void onFailure(int i, String s, @Nullable Throwable throwable) {
                        NetWorkException netWorkException = new NetWorkException(s, throwable, i);
                        emitter.onError(netWorkException);
                    }
                };
                if (isContentArray) {
                    httpCallBack.setContentClazz(List.class);
                }
                request.enqueue(httpCallBack);
            }
        });
    }

    @Override
    public <T>  Observable<T> observable(final Class<T> clazz) {
        return baseResponseObservable(clazz,false)
                .flatMap(new Function<HttpBaseResponse, ObservableSource<T>>() {
                    @Override
                    public ObservableSource<T> apply(HttpBaseResponse httpBaseResponse) throws Exception {
                        return ResponseFlatResult.flatResult(httpBaseResponse);
                    }
                });
    }

    @Override
    public <T>  Observable<List<T>> observableArray(final Class<T> clazz) {
        return baseResponseObservable(clazz,true)
                .flatMap(new Function<HttpBaseResponse, ObservableSource<T>>() {
                    @Override
                    public ObservableSource<T> apply(HttpBaseResponse tHttpBaseResponse) throws Exception {
                        return ResponseFlatResult.flatResult(tHttpBaseResponse);
                    }
                })
                .map(new Function<T, List<T>>() {
                    @Override
                    public List<T> apply(T t) throws Exception {
                        return (List<T>) t;
                    }
                });
    }
}

