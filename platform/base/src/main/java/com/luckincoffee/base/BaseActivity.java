package com.luckincoffee.base;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.CheckResult;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * @Description: Activity 抽象类，主要用于与对应的Presenter绑定，通用方法处理等
 */
public abstract class BaseActivity<P extends IMvpPresenter> extends AppCompatActivity implements IMvpView {
    protected P presenter;
    private Unbinder unbinder;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (beforeOnCreateBreak()) {
            return;
        }
        setContentView(getLayoutId());
        unbinder = ButterKnife.bind(this);
        presenter = createPresenter();
        if (presenter != null) {
            presenter.create(this);
        }
        init(savedInstanceState);
    }

    protected boolean beforeOnCreateBreak() {
        return false;
    }

    /**
     * 获取布局
     *
     * @return 布局文件id
     */
    protected abstract int getLayoutId();

    /**
     * 初始化操作
     *
     * @param savedInstanceState 临时数据
     */
    protected abstract void init(@Nullable Bundle savedInstanceState);

    /**
     * 创建presenter
     *
     * @return
     */
    protected abstract @CheckResult
    P createPresenter();

    @Override
    protected void onStart() {
        super.onStart();
        if (presenter != null) {
            presenter.start();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (presenter != null) {
            presenter.resume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (presenter != null) {
            presenter.pause();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (presenter != null) {
            presenter.stop();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (unbinder != null) {
            unbinder.unbind();
        }
        if (presenter != null) {
            presenter.destroy();
        }
    }
}

