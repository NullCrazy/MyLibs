package com.lucky.lib.downloader;

import android.text.TextUtils;

import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.lucky.lib.downloader.exception.HttpException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Arrays;
import java.util.Collections;


import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableEmitter;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import io.reactivex.rxjava3.core.ObservableSource;
import io.reactivex.rxjava3.functions.Action;
import io.reactivex.rxjava3.functions.BiFunction;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.functions.Function;
import io.reactivex.rxjava3.plugins.RxJavaPlugins;
import io.reactivex.rxjava3.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;

import static com.lucky.lib.downloader.DownloadThread.CODE_200;

/**
 * @Description: 单一文件下载器，对应一个下载任务
 *  一个任务可对一个文件进行多线程下载
 *  注意：不能将同一文件名（除去后缀的name）的资源同时进行下载，因为下载器会将下载过程中的文件修改后缀名为{@link #SUFFIX_DOWNLOAD_FILE}，会造成文件冲突
 * @Author: xmq mingqiang.xu@luckincoffee.com
 * @Date: 2019/1/24 下午10:02
 */
public class SingleDownloader {

    /**
     * 下载中的文件后缀，表明此文件还没有下载完成
     */
    static final String SUFFIX_DOWNLOAD_FILE = ".download";
    /**
     * 下载中的临时文件
     */
    static final String SUFFIX_DOWNLOAD_TEMP = ".temp";
    /**
     * 服务器响应头包含Accept-Ranges bytes 才支持断点续下
     */
    private static final String BYTES = "bytes";
    /**
     * etag 用于鉴定文件的完整性
     */
    private static final String ETAG = "etag";
    /**
     * B 、KB、 M 、G 的计量单位 1024
     */
    private static final int UNIT = 1024;
    /**
     * 每次保存文件的间隔大小 100K
     */
    private static final long SAVE_SIZE_100K = 100 * UNIT;
    /**
     * 下载过程中超过1G的进行3个线程下载
     */
    private static final long LEVEL3_1G = 1024 * 1024 * 1024;
    /**
     * 下载过程中，30M以上使用2个线程下载
     */
    private static final long LEVEL2_30M = 30 * 1024 * 1024;
    /**
     * 服务器支持断点续传
     */
    public static final int SERVER_NORMAL = 0x00;
    /**
     * 服务器不支持断点续传
     */
    public static final int SERVER_NON_BREAK_POINT = -0x02;
    /**
     * 服务器支持断点续传，但无CONTENT_LENGTH返回，以CHUNK方式传输，此时无法进行多线程下载
     */
    public static final int SERVER_NON_CONTENT_LENGTH = -0x01;
    /**
     * 服务器状态注解
     */
    @Retention(RetentionPolicy.SOURCE)
    @IntDef({SERVER_NORMAL, SERVER_NON_BREAK_POINT, SERVER_NON_CONTENT_LENGTH})
    public @interface ServerState {
    }

    /**
     * 网路请求Client
     */
    private OkHttpClient okhttpClient;
    /**
     * 下载链接
     */
    private String requestUrl;
    /**
     * 下载时传入的下载目录
     */
    private String downloadFilePath;
    private String downloadFileName;
    /**
     * 下载中的文件目录
     */
    private String downloadingFileName;
    /**
     * 整个下载任务的进度回调对象
     */
    private ProgressBean progressBean;
    /**
     * 断点续下的线程信息，由二维数组包含每行有：0 startPosition ,1 currentPosition ,2 endPosition
     */
    private long[][] progress;
    /**
     * 标志此文件是否被初始化(下载)过
     */
    private boolean isOrigin;

    /**
     * 是否进行过meta元信息请求
     */
    private boolean isRequestMeta;

    /**
     * etag标志量，用于服务器检查文件是否被修改过
     */
    private String etag;

    /**
     * last-modified，文件最后修改的时间，用于服务器检查文件是否被修改过，一般与{@link #etag}共同使用，但没有它更精确
     */
    private String lastModified;
    /**
     * 下载线程
     */
    private DownloadThread[] downloadThreads;
    /**
     * 服务断点续传状态
     */
    private @ServerState
    int serverState;
    /**
     * 上一次写入的内容，避免频繁io
     */
    private long lastContentWhenWrite;

    /**
     * 单一下载任务
     * @param okHttpClient {@link OkHttpClient} 下载client
     * @param requestUrl 请求地址
     * @param downloadFilePath  下载文件目录
     * @param filename  下载的文件名字
     */
    public SingleDownloader(@Nullable OkHttpClient okHttpClient, @NonNull String requestUrl, @NonNull String downloadFilePath, @NonNull String filename) {
        this.okhttpClient = okHttpClient;
        if (okHttpClient == null) {
            this.okhttpClient = new OkHttpClient.Builder().protocols(Collections.singletonList(Protocol.HTTP_1_1)).build();
        }

        if (downloadFilePath.endsWith(File.separator)) {
            this.downloadFilePath = downloadFilePath;
        } else {
            this.downloadFilePath = downloadFilePath + File.separator;
        }

        this.requestUrl = requestUrl;
        this.downloadFileName = filename;
        this.progressBean = new ProgressBean();
        this.downloadingFileName = DownloadUtil.modifySuffix(downloadFileName, SUFFIX_DOWNLOAD_FILE);
        isOrigin = true;
        progressBean.state = DownloadThread.STATE_IDLE;
        try {
            File file = new File(downloadFilePath + filename);
            File parent = file.getParentFile();
            if (!parent.exists() && !parent.mkdirs()) {
                throw new FileNotFoundException("文件创建失败，请保证此文件存在");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        readFromFile(downloadFilePath, filename);
    }

    /**
     * 单一下载任务
     * @param okHttpClient 下载网络的client
     * @param fileName 下载的文件名
     */
    SingleDownloader(OkHttpClient okHttpClient, @NonNull String fileName) {
        this.progressBean = new ProgressBean();
        this.downloadingFileName = DownloadUtil.modifySuffix(fileName, SUFFIX_DOWNLOAD_FILE);
        progressBean.currentPosition = DownloadThread.STATE_IDLE;
        readFromFile(null, fileName);
    }

    /**
     *  开始下载文件 ，已实现断点续下在
     *  {@link DownloadThread#STATE_IDLE}       ：闲置
     *  {@link DownloadThread#STATE_CONNECTING} ：连接中
     *  {@link DownloadThread#STATE_DOWNLOADING}：下载中
     *  状态时可进行{@link #cancel()}{@link #pause()} 操作
     * @return 下载的observable
     */
    public Observable<ProgressBean> start() {
        if (checkDownloadState()) {
            return Observable.just(progressBean);
        }
        if (isOrigin) {
            progressBean.state = DownloadThread.STATE_CONNECTING;
            return requestMetaInfo();
        }

        //etag 或者last-modified 有一个为非空即可验证 ，并且此时是从文件中读取的etag last-modified 元信息
        boolean isNull = (!TextUtils.isEmpty(etag) || !TextUtils.isEmpty(lastModified));
        if (!isRequestMeta && isNull) {
            progressBean.state = DownloadThread.STATE_CONNECTING;
            return checkFileEtags();
        }


        progressBean.progressDetail = progress;
        downloadThreads = new DownloadThread[progress.length];
        Observable<ProgressBean>[] observables = new Observable[progress.length];

        for (int i = 0; i < progress.length; i++) {
            final DownloadThread downloadThread = new DownloadThread(okhttpClient, progress[i][1], progress[i][2], requestUrl, downloadFilePath, downloadingFileName, i + 1);
            downloadThreads[i] = downloadThread;
            observables[i] = Observable.create(downloadThread).subscribeOn(Schedulers.io());
        }
        progressBean.state = DownloadThread.STATE_DOWNLOADING;
        RxJavaPlugins.setErrorHandler(new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                throwable.printStackTrace();
            }
        });
        return Observable.mergeArray(observables)
                .scan(new BiFunction<ProgressBean, ProgressBean, ProgressBean>() {
                    @Override
                    public ProgressBean apply(ProgressBean progressBean, ProgressBean progressBean2) throws Exception {
                        //根据线程标记量更新每个下载线程的最新进度
                        if (progressBean.tag > 0) {
                            progress[progressBean.tag - 1][1] = progressBean.currentPosition;
                        }
                        if (progressBean2.tag > 0) {
                            progress[progressBean2.tag - 1][1] = progressBean2.currentPosition;
                        }

                        //进行合并操作，将每个线程的下载进度进行相加，告知外层最新下载进度
                        SingleDownloader.this.progressBean.currentPosition = progressBean.currentPosition + progressBean2.getInnerCurrentPosition();
                        return SingleDownloader.this.progressBean;
                    }
                }).doOnComplete(new Action() {
                    @Override
                    public void run() throws Exception {
                        //修正下载文件的名字，并删除临时进度文件
                        DownloadUtil.renameFile(downloadFilePath + downloadingFileName, downloadFilePath + downloadFileName);
                        progressBean.state = DownloadThread.STATE_COMPLETED;
                        deleteTempFile();

                    }
                }).observeOn(Schedulers.io())
                .doOnNext(new Consumer<ProgressBean>() {
                    @Override
                    public void accept(ProgressBean progressBean) throws Exception {
                        //每隔100K保存最新的下载信息，为断点续下做记录
                        if (SingleDownloader.this.progressBean.contentLength - lastContentWhenWrite > SAVE_SIZE_100K) {
                            writeToRequestFile(downloadFilePath, downloadFileName);
                            lastContentWhenWrite = progressBean.currentPosition;
                        }
                    }
                });
    }

    /**
     * 请求元信息{@link #etag} {@link #lastModified}{@link ProgressBean#contentLength}
     * @return 下载的Observable
     */
    private Observable<ProgressBean> requestMetaInfo() {
        return Observable.create(new ObservableOnSubscribe<Response>() {
            @Override
            public void subscribe(ObservableEmitter<Response> emitter) throws Exception {
                if (checkDownloadState()) {
                    emitter.onComplete();
                    return;
                }
                emitter.onNext(okhttpClient.newCall(new Request.Builder().url(requestUrl).build()).execute());
                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.io())
                .flatMap(new Function<Response, ObservableSource<ProgressBean>>() {
                    @Override
                    public ObservableSource<ProgressBean> apply(Response response) throws Exception {
                        if (checkDownloadState()) {
                            return Observable.just(progressBean);
                        }
                        //不能以200作为成功标志
                        if (!response.isSuccessful()) {
                            throw new HttpException("请求元信息时，服务器出错" + response.toString());
                        }
                        if (response.body() == null) {
                            throw new HttpException("请求元信息时，未返回body响应体" + response.toString());
                        }

                        return dealWithMetaInfo(response);
                    }
                });
    }

    /**
     * 检查文件是否修改过
     * 原理：
     * 在HTTP Request中加入If-None-Match信息（ETags的值）。
     * 如果服务器验证资源的ETags没有改变（该资源没有改变），将返回一个304状态；
     * 否则，服务器将返回200状态，并返回该资源和新的ETags
     * @return 下载的Observable
     */
    private Observable<ProgressBean> checkFileEtags() {
        Request.Builder reqBuilder = new Request.Builder();
        if (!TextUtils.isEmpty(etag)) {
            reqBuilder.header("If-None-Match", etag);
        }
        if (!TextUtils.isEmpty(lastModified)) {
            reqBuilder.header("If-Modified-Since", lastModified);
        }

        final Request request = reqBuilder.url(requestUrl).build();

        if (checkDownloadState()) {
            return Observable.just(progressBean);
        }
        return Observable.create(new ObservableOnSubscribe<Response>() {
            @Override
            public void subscribe(ObservableEmitter<Response> emitter) throws Exception {
                emitter.onNext(okhttpClient.newCall(request).execute());
                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.io())
                .flatMap(new Function<Response, ObservableSource<ProgressBean>>() {
                    @Override
                    public ObservableSource<ProgressBean> apply(Response response) throws Exception {
                        if (checkDownloadState()) {
                            return Observable.just(progressBean);
                        }
                        if (response.body() == null) {
                            throw new HttpException("请求元信息时，未返回body响应体" + response.toString());
                        }
                        if (response.code() != CODE_200) {
                            isRequestMeta = true;
                            response.close();
                            return start();
                        } else {
                            return dealWithMetaInfo(response);
                        }
                    }
                });
    }

    /**
     * 检查下载状态，暂时
     * {@link DownloadThread#STATE_CANCEL}
     * {@link DownloadThread#STATE_COMPLETED}
     * {@link DownloadThread#STATE_ERROR}
     * {@link DownloadThread#STATE_PAUSED}
     * 四种状态都是截止符，此时为停止状态
     * @return true:未停止  false：停止
     */
    boolean isStop() {
        return progressBean.state == DownloadThread.STATE_CANCEL
                || progressBean.state == DownloadThread.STATE_PAUSED
                || progressBean.state == DownloadThread.STATE_ERROR
                || progressBean.state == DownloadThread.STATE_COMPLETED;
    }

    /**
     * 检查state是否为调用c{@link #cancel()}或者{@link #pause()}方法
     * @return true:取消/停止 false:非取消/停止
     */
    private boolean checkDownloadState() {
        return progressBean.state == DownloadThread.STATE_CANCEL
                || progressBean.state == DownloadThread.STATE_PAUSED;
    }


    /**
     * 停止任务下载，会取消于此相关的所有任务
     */
    public void pause() {
        if (isStop()) {
            return;
        }

        progressBean.state = DownloadThread.STATE_PAUSED;
        if (downloadThreads != null) {
            for (DownloadThread downloadThread : downloadThreads) {
                downloadThread.cancel();
            }
        }
    }

    /**
     * 取消下载操作，会尝试进行文件的删除操作，并不保证删除文件成功
     */
    public void cancel() {
        if (isStop()) {
            return;
        }
        progressBean.state = DownloadThread.STATE_CANCEL;
        if (downloadThreads != null) {
            for (DownloadThread downloadThread : downloadThreads) {
                downloadThread.cancel();
            }
        }
        deleteTempFile();
    }

    /**
     * 删除临时文件，后缀为{@link #SUFFIX_DOWNLOAD_FILE} 跟{@link #SUFFIX_DOWNLOAD_TEMP}的文件
     * @return 删除文件是否成功 true:成功 false:失败
     */
    private boolean deleteTempFile() {
        File temp = new File(downloadFilePath + DownloadUtil.modifySuffix(downloadFileName, SUFFIX_DOWNLOAD_TEMP));
        File file = new File(downloadFilePath + downloadingFileName);
        if ((temp.exists() && temp.isFile() && temp.delete())) {
            return (file.exists() && file.isFile() && file.delete());
        }
        return false;
    }


    /**
     * 此方法做了如下的动作：
     * <p>
     * 1.处理元信息 <code>etag,last-modified,content-length,Accept-Range</code>
     * 2.创建一个下载文件，并占据下载的长度
     * 3.设置下载的线程个数
     * @param response 响应体
     * @throws IOException io读写异常
     */
    private Observable<ProgressBean> dealWithMetaInfo(@NonNull Response response) throws IOException {
        String acceptRanges = response.header("Accept-Ranges");
        isRequestMeta = true;
        isOrigin = false;
        if (!BYTES.equals(acceptRanges)) {
            //服务器不支持断点续下,只有服务器响应头包含Accept-Ranges bytes 才支持断点续下
            progress = new long[1][3];
            progress[0][0] = 0;
            progress[0][1] = 0;
            progress[0][2] = SERVER_NON_BREAK_POINT;
            progressBean.contentLength = SERVER_NON_BREAK_POINT;
        } else {

            etag = response.header(ETAG);
            lastModified = response.header("last-modified");

            try {
                progressBean.contentLength = Long.parseLong(response.header("content-length"));
            } catch (Exception e) {
                //chunk 方式，不支持断点续下，线程置为单线程
                progress = new long[1][3];
                progress[0][0] = 0;
                progress[0][1] = 0;
                progress[0][2] = SERVER_NON_CONTENT_LENGTH;
                progressBean.contentLength = SERVER_NON_CONTENT_LENGTH;
                response.close();
                return start();
            }

            RandomAccessFile randomAccessFile = new RandomAccessFile(downloadFilePath + downloadingFileName, "rw");
            randomAccessFile.setLength(progressBean.contentLength);
            randomAccessFile.close();

            if (progress == null || progress.length == 0) {
                //根据文件大小进行多线程下载
                //1G 以上进行3线程下载
                if (progressBean.contentLength >= LEVEL3_1G) {
                    progress = new long[3][3];
                    //30M以上使用2个线程下载
                } else if (progressBean.contentLength >= LEVEL2_30M) {
                    progress = new long[2][3];
                } else {
                    progress = new long[1][3];
                }

                for (int i = 0; i < progress.length; i++) {
                    progress[i][0] = (progressBean.contentLength / progress.length) * i;
                    progress[i][1] = (progressBean.contentLength / progress.length) * i;

                    if (i == progress.length - 1) {
                        progress[i][2] = progressBean.contentLength;
                    } else {
                        progress[i][2] = (progressBean.contentLength / progress.length) * (i + 1);
                    }
                }

            }
        }

        response.close();
        return start();
    }

    /**
     * 获取下载链接
     * @return 下载链接
     */
    public String getRequestUrl() {
        return requestUrl;
    }

    /**
     * 获得下载状态
     * @return 下载状态
     */
    @DownloadThread.DownloadState
    int getDownloadState() {
        return progressBean.state;
    }

    /**
     * 尝试将此下载任务以及当前的进度写入到文件中
     * 后续通过该文件构造下载器以实现断点续传功能
     * @param downloadFilePath 下载路径
     * @param downloadFileName 下载文件
     */
    void writeToRequestFile(String downloadFilePath, String downloadFileName) {
        PrintWriter out =null;
        try {
            out =  new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(String.format("%s%s", downloadFilePath, DownloadUtil.modifySuffix(downloadFileName, SUFFIX_DOWNLOAD_TEMP))),"UTF-8")));

            JSONObject jsonObject = new JSONObject();
            //是否被初始化过
            jsonObject.put("isOrigin", isOrigin);
            //下载链接
            jsonObject.put("requestUrl", requestUrl);
            //下载路径
            jsonObject.put("downloadFilePath", downloadFilePath);
            //下载文件名
            jsonObject.put("downloadFileName", downloadFileName);

            if (!isOrigin) {
                //etag标记量
                jsonObject.put("etag", etag);
                //是否支持断点
                jsonObject.put("serverState", serverState);
                //lastModified
                jsonObject.put("lastModified", lastModified);
                //文件大小
                jsonObject.put("contentLength", progressBean.contentLength);
                //当前下载位置
                jsonObject.put("currentContent", progressBean.currentPosition);
                //上一次写入的位置
                jsonObject.put("lastContentWhenWrite", lastContentWhenWrite);

                JSONArray jsonArray = new JSONArray();
                for (long[] singleProgress : progress) {
                    JSONArray threadJson = new JSONArray();
                    threadJson.put(singleProgress[0]);
                    threadJson.put(singleProgress[1]);
                    threadJson.put(singleProgress[2]);
                    jsonArray.put(threadJson);
                }
                jsonObject.put("progress", jsonArray);
            }

            out.write(jsonObject.toString());

        } catch (JSONException | IOException e) {
            e.printStackTrace();
        } finally {
            DownloadUtil.close(out);
        }
    }

    /**
     * 初始化时，读取下载信息
     * @param downloadFilePath 下载路径
     * @param downloadFileName 下载文件
     */
    private void readFromFile(String downloadFilePath, String downloadFileName) {
        BufferedReader bufferedReader =null;
        try {
            File file = new File(downloadFilePath + DownloadUtil.modifySuffix(downloadFileName, SUFFIX_DOWNLOAD_TEMP));
            if (!file.exists()) {
                return;
            }
            bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(file),"UTF-8"));
            String str = bufferedReader.readLine();
            if (TextUtils.isEmpty(str)) {
                return;
            }
            JSONObject json = new JSONObject(str);
            isOrigin = json.optBoolean("isOrigin");
            if (TextUtils.isEmpty(requestUrl)) {
                requestUrl = json.optString("requestUrl");
            }
            if (TextUtils.isEmpty(this.downloadFilePath)) {
                this.downloadFilePath = json.optString("downloadFilePath");
            }
            if (TextUtils.isEmpty(this.downloadFileName)) {
                this.downloadFileName = json.optString("downloadFileName");
            }

            if (!isOrigin) {
                etag = json.optString("etag");
                serverState = json.optInt("serverState");
                lastModified = json.optString("lastModified");
                progressBean.contentLength = json.optLong("contentLength");
                progressBean.currentPosition = json.optLong("currentContent");
                lastContentWhenWrite = json.optLong("lastContentWhenWrite");

                JSONArray jsonArray = json.optJSONArray("progress");
                progress = new long[jsonArray.length()][3];
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONArray array = jsonArray.getJSONArray(i);
                    progress[i][0] = array.getLong(0);
                    progress[i][1] = array.getLong(1);
                    progress[i][2] = array.getLong(2);
                }
            }


        } catch (IOException | JSONException e) {
            e.printStackTrace();
        } finally {
            DownloadUtil.close(bufferedReader);
        }
    }

    /**
     * equals 用于map中的对象比对
     * @param o 比对的对象
     * @return false：非同一对象  true：同一对象
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SingleDownloader)) {
            return false;
        }

        SingleDownloader that = (SingleDownloader) o;

        if (isOrigin != that.isOrigin) {
            return false;
        }
        if (isRequestMeta != that.isRequestMeta) {
            return false;
        }
        if (serverState != that.serverState) {
            return false;
        }
        if (lastContentWhenWrite != that.lastContentWhenWrite) {
            return false;
        }
        if (okhttpClient != null ? !okhttpClient.equals(that.okhttpClient) : that.okhttpClient != null) {
            return false;
        }
        if (getRequestUrl() != null ? !getRequestUrl().equals(that.getRequestUrl()) : that.getRequestUrl() != null) {
            return false;
        }
        if (downloadFilePath != null ? !downloadFilePath.equals(that.downloadFilePath) : that.downloadFilePath != null) {
            return false;
        }
        if (downloadFileName != null ? !downloadFileName.equals(that.downloadFileName) : that.downloadFileName != null) {
            return false;
        }
        if (downloadingFileName != null ? !downloadingFileName.equals(that.downloadingFileName) : that.downloadingFileName != null) {
            return false;
        }
        if (progressBean != null ? !progressBean.equals(that.progressBean) : that.progressBean != null) {
            return false;
        }
        if (!Arrays.deepEquals(progress, that.progress)) {
            return false;
        }
        if (etag != null ? !etag.equals(that.etag) : that.etag != null) {
            return false;
        }
        if (lastModified != null ? !lastModified.equals(that.lastModified) : that.lastModified != null) {
            return false;
        }
        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        return Arrays.equals(downloadThreads, that.downloadThreads);
    }

    /**
     * 计算对象的hashcode
     * @return hash码
     */
    @Override
    public int hashCode() {
        int result = okhttpClient != null ? okhttpClient.hashCode() : 0;
        result = 31 * result + (getRequestUrl() != null ? getRequestUrl().hashCode() : 0);
        result = 31 * result + (downloadFilePath != null ? downloadFilePath.hashCode() : 0);
        result = 31 * result + (downloadFileName != null ? downloadFileName.hashCode() : 0);
        result = 31 * result + (downloadingFileName != null ? downloadingFileName.hashCode() : 0);
        result = 31 * result + (progressBean != null ? progressBean.hashCode() : 0);
        result = 31 * result + Arrays.deepHashCode(progress);
        result = 31 * result + (isOrigin ? 1 : 0);
        result = 31 * result + (isRequestMeta ? 1 : 0);
        result = 31 * result + (etag != null ? etag.hashCode() : 0);
        result = 31 * result + (lastModified != null ? lastModified.hashCode() : 0);
        result = 31 * result + Arrays.hashCode(downloadThreads);
        result = 31 * result + serverState;
        result = 31 * result + (int) (lastContentWhenWrite ^ (lastContentWhenWrite >>> 32));
        return result;
    }
}
