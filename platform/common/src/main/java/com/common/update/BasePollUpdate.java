package com.common.update;

import android.annotation.SuppressLint;
import android.app.Application;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;

import com.lucky.lib.downloader.DownloadManager;
import com.lucky.lib.downloader.ProgressBean;

import java.io.File;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableEmitter;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import io.reactivex.rxjava3.core.ObservableSource;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Function;


/**
 * @Description: 轮询升级
 * @Author: xingguo.lei@luckincoffee.com
 * @Date: 2019-12-21 11:01
 */
public abstract class BasePollUpdate {
    protected final String TAG = "PollUpdate";
    /**
     * 文件缓存目录
     */
    protected String filePatch;
    /**
     * 文件名称
     */
    protected String fileName;
    /**
     * 是否继续拉取
     */
    private boolean isPoll = true;
    /**
     * 下载是否成功
     */
    protected boolean downSuccess;
    /**
     * 默认版本升级下载apk包名
     */
    private static final String DEFAULT_NAME = "lucky.apk";
    /**
     * 默认版本升级轮询时间间隔
     */
    private static final Long DEFAULT_PERIOD = 1L;
    /**
     * 轮询次数
     */
    private int count;

    private CompositeDisposable disposables = new CompositeDisposable();

    /**
     * @param application Application
     * @param filePatch   下载文件的目录
     * @param fileName    下载到文件目录的文件名称
     */
    public BasePollUpdate(Application application, String filePatch, String fileName) {
        this.filePatch = TextUtils.isEmpty(filePatch) ? application.getCacheDir() + File.separator : filePatch;
        this.fileName = checkFileName(fileName);
    }

    protected void startUpdate(long period) {
        this.startUpdate(1, period);
    }

    protected void startUpdate(long initialDelay, long period) {
        this.startUpdate(initialDelay, period, 0);
    }

    /**
     * 开始轮询升级
     *
     * @param initialDelay 调用此函数后，延时启动更新的时间间隔
     * @param period       间隔时间
     * @param times        轮询的次数，如果不填写此参数则无限轮询
     */
    protected void startUpdate(long initialDelay, long period, final long times) {
        if (initialDelay == 0) {
            initialDelay = DEFAULT_PERIOD;
        }
        if (period == 0) {
            period = DEFAULT_PERIOD;
        }
        count = 0;
        if (disposables != null) {
            disposables.clear();
        }
        Observable.interval(initialDelay, period, TimeUnit.MINUTES)
                .filter(aLong -> {
                    if (times == 0) {
                        return true;
                    } else {
                        count++;
                        return count <= times;
                    }
                })
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        disposables.add(d);
                    }

                    @Override
                    public void onNext(Long aLong) {
                        updateLocationApp();
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    /**
     * 升级本地APP
     */
    private void updateLocationApp() {
        Observable.just(isPoll)
                .filter(aBoolean -> {
                    //判断当前是否可继续下载
                    return aBoolean;
                })
                .flatMap((Function<Boolean, ObservableSource<UpdateBean>>) aBoolean -> {
                    isPoll = false;
                    return checkUpdateAction();
                })
                .filter(updateBean -> {
                    if (updateBean == null) {
                        return false;
                    }
                    Log.d(TAG, "isUpdate:" + updateBean.getVersion().isUpgrade()
                            + " downLoadAddress:" + updateBean.getVersion().getAddress());
                    Version version = updateBean.getVersion();
                    return version.isForce() || version.isUpgrade();
                })
                .map(updateBean -> {
                    Version version = updateBean.getVersion();
                    return version.getAddress();
                })
                .flatMap((Function<String, ObservableSource<ProgressBean>>) downLoadUrl -> downLoadFile(downLoadUrl))
                .subscribe(new Observer<ProgressBean>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(ProgressBean progressBean) {
                        Log.d(TAG, progressBean.toString());
                        //无需处理下载进度
                    }

                    @Override
                    public void onError(Throwable e) {
                        downLoadFail(e);
                        e.printStackTrace();
                        //如果失败可重试下载
                        isPoll = true;
                        Log.d(TAG, "下载失败");
                    }

                    @Override
                    public void onComplete() {
                        //如果没有下载成功，则需要继续轮询
                        if (!downSuccess) {
                            isPoll = true;
                        }
                    }
                });
    }

    /**
     * @param downLoadUrl 下载链接
     * @return 返回回调
     */
    private Observable<ProgressBean> downLoadFile(final String downLoadUrl) {
        return Observable.create(new ObservableOnSubscribe<ProgressBean>() {
            @SuppressLint("CheckResult")
            @Override
            public void subscribe(final ObservableEmitter<ProgressBean> emitter) throws Exception {
                File file = new File(filePatch + fileName);
                if (file.exists()) {
                    file.delete();
                }
                DownloadManager downloadManager = new DownloadManager(null, filePatch);
                downloadManager.enqueue(downLoadUrl, fileName, new Observer<com.lucky.lib.downloader.ProgressBean>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(com.lucky.lib.downloader.ProgressBean progressBean) {
                        emitter.onNext(progressBean);
                    }

                    @Override
                    public void onError(Throwable e) {
                        emitter.onError(e);
                    }

                    @Override
                    public void onComplete() {
                        //下载成功
                        downSuccess = true;
                        downLoadSuccess();
                        emitter.onComplete();
                    }
                });
            }
        });
    }

    protected void insertApk() {
        if (!filePatch.endsWith(File.separator)) {
            filePatch = filePatch + File.separator;
        }

        File file = new File(filePatch + fileName);
        //检查安装包是否存在
        if (!file.exists()) {
            //如果不存在，下载失败，需要重新轮询下载
            downSuccess = false;
            return;
        }
        Log.d(TAG, "缓存路径：" + file.getPath());
        Log.d(TAG, "是否root:" + UpdateUtils.isRoot());
        //判断设备是否root
        if (!UpdateUtils.isRoot()) {
            return;
        }
        Log.d(TAG, "开始静默安装");
        //静默安装
        UpdateUtils.slientInstall(file.getPath());
    }

    /**
     * 校验apk文件名是否合规
     *
     * @param name 待检验
     * @return 合规文件名
     */
    private String checkFileName(String name) {
        if (TextUtils.isEmpty(name)) {
            return DEFAULT_NAME;
        }

        String[] args = name.split("\\.");
        if (args[args.length - 1].equals("apk")) {
            return name;
        }
        return name + ".apk";
    }

    /**
     * 检查升级实现
     *
     * @return 检查升级操作
     */
    @NonNull
    protected abstract Observable<UpdateBean> checkUpdateAction();

    /**
     * 下载成功通知
     */
    protected abstract void downLoadSuccess();

    /**
     * 下载失败
     *
     * @param e 失败原因
     */
    protected void downLoadFail(Throwable e) {

    }
}
