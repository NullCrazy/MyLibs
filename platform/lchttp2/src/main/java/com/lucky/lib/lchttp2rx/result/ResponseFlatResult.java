package com.lucky.lib.lchttp2rx.result;


import com.lucky.lib.http.HttpBaseResponse;
import com.lucky.lib.lchttp2rx.exception.BusinessException;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableEmitter;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;

/**
 * @author: xingguo.lei
 * date: 2018/11/20
 * func:对返回的HttpBaseResponse进行结构的处理
 */
public final class ResponseFlatResult {
    private static final int SUCCESS_CODE = 0;

    /**
     * 通过状态码和返回值来判断，最后回调的接口
     *
     * @param result
     * @param <T>
     * @return
     */
    public static <T> Observable<T> flatResult(final HttpBaseResponse<T> result) {
        return Observable.create(new ObservableOnSubscribe<T>() {
            @Override
            public void subscribe(ObservableEmitter<T> subscriber) throws Exception {
                //如果网络返回code 为0，代表成功
                if (result.getRtn() == SUCCESS_CODE) {
                    //数据取出部分
                    if (result.getData() == null) {
                        subscriber.onNext((T) new Object());
                    } else {
                        subscriber.onNext(result.getData());
                    }
                    subscriber.onComplete();
                } else {
                    subscriber.onError(
                            new BusinessException(result.getMsg()
                                    , new Exception(result.getMsg()), result.getRtn()
                                    , result.getBusiCode())
                    );
                }
            }
        });
    }
}