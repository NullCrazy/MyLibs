package com.lucky.lib.lchttp2rx.result;

import com.lucky.lib.http.HttpBaseResponse;

import java.util.List;

import io.reactivex.rxjava3.core.Observable;

/**
 * @author: xingguo.lei
 * date: 2018/11/20
 * func:
 */
public interface Result {
    /**
     * 获取具有原始HttpBaseResponse<T> 类型的网络返回数据 </>
     *
     * @param clazz 进行序列化的类。
     * @return api的整个数据类型
     */
    <T> Observable<HttpBaseResponse<T>> baseResponseObservable(Class<T> clazz);

    /**
     * 获取具有原始HttpBaseResponse<T> 类型的网络返回数据 </>
     *
     * @param clazz 当序列化的content为数组时为item的数据类型
     * @param <T>   进行序列化的类。
     * @return api的整个数据类型
     */
    <T> Observable<HttpBaseResponse<List<T>>> baseResponseObservableArray(Class<T> clazz);

    /**
     * 获取具有T 类型数据的 网络返回数据
     * 数据被处理:
     */
    <T> Observable<T> observable(Class<T> clazz);

    /**
     * 获取List<T> 结果</>
     */
    <T> Observable<List<T>> observableArray(Class<T> clazz);
}
