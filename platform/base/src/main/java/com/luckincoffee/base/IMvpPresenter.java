package com.luckincoffee.base;

/**
 * @author xingguo.lei
 * date: 2018/10/31
 * explain: p层统一接口，内部方法便于Presenter的生命周期绑定，资源回收等操作
 */
public interface IMvpPresenter<V extends IMvpView> {
    /**
     * 获取当前绑定的view指针
     *
     * @param v
     */
    void create(V v);

    /**
     * 与生命周期onStart绑定
     */
    void start();

    /**
     * 与view生命周期onResume绑定
     */
    void resume();

    /**
     * 与view生命周期onPause绑定
     */
    void pause();

    /**
     * 与生命周期onStop绑定
     */
    void stop();

    /**
     * 与生命周期onDestroy绑定
     * 便于清理回收资源
     */
    void destroy();
}
