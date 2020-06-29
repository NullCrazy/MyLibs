package com.lucky.lib.downloader;


import androidx.annotation.CheckResult;
import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableSource;
import io.reactivex.rxjava3.core.ObservableTransformer;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Action;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;

import static com.lucky.lib.downloader.DownloadThread.STATE_CONNECTING;
import static com.lucky.lib.downloader.DownloadThread.STATE_DOWNLOADING;
import static com.lucky.lib.downloader.DownloadThread.STATE_IDLE;
import static com.lucky.lib.downloader.DownloadThread.STATE_PAUSED;
import static com.lucky.lib.downloader.SingleDownloader.SUFFIX_DOWNLOAD_FILE;
import static com.lucky.lib.downloader.SingleDownloader.SUFFIX_DOWNLOAD_TEMP;

/**
 * @Description: 下载管理器，可全局使用此下载器，需要传入{@link OkHttpClient}作为下载内核,不传入时使用默认的httpclient
 *  可同时下载不同的文件，文件个数为{@link #simultaneousSize},default: 5 ,可调用{@link #setSimultaneousSize(int)}进行自行设置
 * @Author: xmq mingqiang.xu@luckincoffee.com
 * @Date: 2019/1/24 下午10:03
 */
public class DownloadManager {

    /**
     * 默认同时下载的资源数
     */
    private static final int SIMULT_SIZE = 5;
    /**
     * 设置允许同时下载的资源数
     */
    private int simultaneousSize;
    /**
     * 此下载器的默认下载目录
     */
    private String defaultDownloadDirectory;
    /**
     * 此下载器所用的{@link OkHttpClient}，当外部传入为null时将自己创建
     */
    private OkHttpClient httpClient;
    /**
     * 下载中的列表，包含了pause状态的下载任务
     */
    private Map<SingleDownloader, Observer<ProgressBean>> singleDownloaderMap;

    /**
     * @param httpClient        {@link OkHttpClient} 下载Client
     * @param downloadDirectory 下载目录
     */
    public DownloadManager(@Nullable OkHttpClient httpClient, @NonNull String downloadDirectory) {

        this.simultaneousSize = SIMULT_SIZE;
        if (downloadDirectory.endsWith(File.separator)) {
            this.defaultDownloadDirectory = downloadDirectory;
        } else {
            this.defaultDownloadDirectory = downloadDirectory + File.separator;
        }

        if (httpClient == null) {
            this.httpClient = new OkHttpClient.Builder().protocols(Collections.singletonList(Protocol.HTTP_1_1)).build();
        } else {
            this.httpClient = httpClient;
        }

        singleDownloaderMap = new HashMap<>(4);

    }


    /**
     * 获得同时下载的资源数
     * @return  同时下载数量大小
     */
    @CheckResult
    public int getSimultaneousSize() {
        return simultaneousSize;
    }

    /**
     * 设置同时下载任务个数
     *
     * @param simultaneousSize 资源数
     */
    public void setSimultaneousSize(@IntRange(from = 1) int simultaneousSize) {
        this.simultaneousSize = simultaneousSize;
    }

    /**
     * 获得默认下载目录
     * @return 下载目录
     */
    public String getDefaultDownloadDirectory() {
        return defaultDownloadDirectory;
    }

    /**
     * 获得资源下载集合
     * @return 正在下载的资源集合
     */
    @CheckResult
    public Map<SingleDownloader, Observer<ProgressBean>> getSingleDownloaderMap() {
        return singleDownloaderMap;
    }


    /**
     * 暂停对应资源的下载任务
     *
     * @param resourceUrl 资源地址
     */
    public void pause(@NonNull String resourceUrl) {
        for (SingleDownloader downloader : singleDownloaderMap.keySet()) {
            if (downloader.getRequestUrl().equals(resourceUrl)) {
                downloader.pause();
                return;
            }
        }
    }


    /**
     * 开启对应资源的下载任务
     *
     * @param resourceUrl 资源地址
     */
    public void start(@NonNull String resourceUrl) {
        for (Map.Entry<SingleDownloader, Observer<ProgressBean>> entry : singleDownloaderMap.entrySet()) {
            SingleDownloader downloader = entry.getKey();
            if (downloader.getRequestUrl().equals(resourceUrl)) {
                downloader.start().compose(this.<ProgressBean>applySchedulers()).subscribe(entry.getValue());
                return;
            }
        }
    }

    /**
     * 取消对应资源的下载任务
     *
     * @param resourceUrl 资源地址
     */
    public void cancel(@NonNull String resourceUrl) {
        Iterator<Map.Entry<SingleDownloader, Observer<ProgressBean>>> iterator = singleDownloaderMap.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry<SingleDownloader, Observer<ProgressBean>> next = iterator.next();
            SingleDownloader downloader = next.getKey();
            if (downloader.getRequestUrl().equals(resourceUrl)) {
                iterator.remove();
                downloader.cancel();
                return;
            }
        }
    }

    /**
     * 立即开始下载，此方法并不能加入{@link #singleDownloaderMap}队列
     *
     * @param resourceUrl 下载连接
     * @param fileName    下载名字
     * @return 被观察者，需要{@link Observer} 进行订阅操作
     */
    public Observable<ProgressBean> startDownloadWithInstantRun(@NonNull String resourceUrl, @NonNull String fileName) {
        return new SingleDownloader(httpClient, resourceUrl, defaultDownloadDirectory, fileName).start().compose(this.<ProgressBean>applySchedulers());
    }

    /**
     * 搜寻指定某个文件夹中的临时下载文件，并将其加入到下载队列，此方法的下载进度外界无法得知
     *
     * @param requestDirectoryPath 指定文件夹，将从这个文件夹中搜索临时文件，以后缀搜索
     */
    public void enqueueRequestDirectory(@NonNull String requestDirectoryPath) {
        File requestDir = new File(requestDirectoryPath);
        if (!requestDir.exists()) {
            return;
        }

        //noinspection ConstantConditions
        for (File requestFile : requestDir.listFiles(new FilenameFilter() {

            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(SUFFIX_DOWNLOAD_TEMP);
            }

        })) {
            SingleDownloader singleDownloader = new SingleDownloader(httpClient, requestFile.getAbsolutePath());
            enqueue(singleDownloader, new Observer<ProgressBean>() {
                @Override
                public void onSubscribe(Disposable d) {

                }

                @Override
                public void onNext(ProgressBean progressBean) {

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
    }

    /**
     * 将指定资源的下载任务加入到下载队列中
     *
     * @param resourceUrl 资源地址
     * @param observer    下载的回调，注意在取消下载时进行取消订阅，防止内存泄漏
     * @return 是否加入成功，如果下载列表中有该资源的下载任务，则返回false
     */
    @CheckResult
    public boolean enqueue(@NonNull String resourceUrl, String fileName, @NonNull Observer<ProgressBean> observer) {
        if (isExistInList(resourceUrl)) {
            return false;
        }

        SingleDownloader singleDownloader = new SingleDownloader(httpClient, resourceUrl,
                defaultDownloadDirectory, fileName);
        enqueue(singleDownloader, observer);
        return true;
    }

    /**
     * 进入下载队列，但是此时的下载无observer观察者
     * @param resourceUrl 下载链接
     * @param fileName 下载文件名字
     * @return 入下载队列是否成功：false 失败，true 成功
     */
    @CheckResult
    public boolean enqueue(@NonNull String resourceUrl, @NonNull String fileName) {
        if (isExistInList(resourceUrl)) {
            return false;
        }

        SingleDownloader singleDownloader = new SingleDownloader(httpClient, resourceUrl,
                defaultDownloadDirectory, fileName);
        enqueue(singleDownloader, new Observer<ProgressBean>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(ProgressBean progressBean) {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
        return true;
    }


    /**
     * 加入已经构造的下载任务
     *
     * @param observer   下载的回调，注意在取消下载时进行取消订阅，防止内存泄漏
     * @param downloader 构造完成的单任务下载器
     */
    public void enqueue(@NonNull SingleDownloader downloader, @NonNull Observer<ProgressBean> observer) {
        singleDownloaderMap.put(downloader, observer);
        checkAndStart(true);
    }

    /**
     * 检查下载列表中是否存在指定资源地址的下载任务
     *
     * @param resourceUrl 资源地址
     */
    public boolean isExistInList(@NonNull String resourceUrl) {
        for (SingleDownloader downloader : singleDownloaderMap.keySet()) {
            if (resourceUrl.equals(downloader.getRequestUrl())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 删除下载文件
     *
     * @param downloadFileName 文件名
     */
    public boolean deleteDownloadFile(@NonNull String downloadFileName) {
        File file = new File(defaultDownloadDirectory + downloadFileName);
        return file.exists() && file.delete();
    }

    /**
     * 删除文件
     * @param name 文件名
     * @return 是否删除成功 false：删除失败   true：删除成功
     */
    public boolean deleteTempFile(@NonNull String name) {
        File temp = new File(defaultDownloadDirectory + DownloadUtil.modifySuffix(name, SUFFIX_DOWNLOAD_TEMP));
        File file = new File(defaultDownloadDirectory + DownloadUtil.modifySuffix(name, SUFFIX_DOWNLOAD_FILE));
        return (temp.exists() && temp.isFile() && temp.delete()) && (file.exists() && file.isFile() && file.delete());
    }

    /**
     * 检查下载状态
     * @param checkPause 是否检查pause状态
     */
    private void checkAndStart(boolean checkPause) {
        //检查并按照顺序进行下载
        int downloadingCount = 0;
        for (SingleDownloader downloader : singleDownloaderMap.keySet()) {
            if (downloader.getDownloadState() == STATE_CONNECTING
                    || downloader.getDownloadState() == STATE_DOWNLOADING) {
                downloadingCount++;
            }
        }

        if (downloadingCount >= simultaneousSize) {
            return;
        }

        Iterator<Map.Entry<SingleDownloader, Observer<ProgressBean>>> iterator = singleDownloaderMap.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry<SingleDownloader, Observer<ProgressBean>> next = iterator.next();
            SingleDownloader downloader = next.getKey();
            if (downloader.isStop() && downloader.getDownloadState() != STATE_PAUSED) {
                iterator.remove();
                continue;
            }

            //判断当前状态是否为：初始化且非暂停状态
            boolean state = (downloader.getDownloadState() == STATE_IDLE)
                    || (checkPause && downloader.getDownloadState() == DownloadThread.STATE_PAUSED);
            if (state) {
                downloader.start()
                        .compose(this.<ProgressBean>applySchedulers())
                        .subscribe(next.getValue());
                downloadingCount++;
                if (downloadingCount >= simultaneousSize) {
                    return;
                }
            }
        }

    }

    /**
     * 仅仅在下载某一个资源成功之时才进行列表的重试，onError时暂不处理
     * @param <T> 转换的类型
     * @return  ObservableTransformer转换后的former
     */
    private <T> ObservableTransformer<T, T> applySchedulers() {
        return new ObservableTransformer<T, T>() {
            @Override
            public ObservableSource<T> apply(Observable<T> upstream) {
                return upstream.observeOn(AndroidSchedulers.mainThread())
                        .doOnComplete(new Action() {
                            @Override
                            public void run() throws Exception {
                                checkAndStart(false);
                            }
                        });
            }
        };
    }

}
