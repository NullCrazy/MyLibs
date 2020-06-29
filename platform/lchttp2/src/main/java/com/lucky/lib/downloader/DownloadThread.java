package com.lucky.lib.downloader;

import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.text.TextUtils;


import com.lucky.lib.downloader.exception.DownloadException;

import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Collections;

import io.reactivex.rxjava3.core.ObservableEmitter;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;

import static com.lucky.lib.downloader.SingleDownloader.SERVER_NON_CONTENT_LENGTH;
import static com.lucky.lib.downloader.SingleDownloader.SERVER_NORMAL;

/**
 * @Description: DownLoad 单一线程。
 *  1.处理单一下载任务，可以单独使用
 *  2.内部仅仅处理下载的逻辑以及取消逻辑，无法暂停
 *  3.以{@link OkHttpClient} 为下载内核
 *  4.除单独使用也可配合{@link SingleDownloader},进行多线程下载逻辑
 * @Author: xmq mingqiang.xu@luckincoffee.com
 * @Date: 2019/1/24 下午10:03
 */
public class DownloadThread implements ObservableOnSubscribe<ProgressBean> {

    /**
     * 400的状态码： Bad Request
     */
    private static final int CODE_400 = 400;
    /**
     * 300状态码，重定向
     */
    private static final int CODE_300 = 300;
    /**
     * 200状态码，成功
     */
    static final int CODE_200 = 200;
    /**
     * 206状态码，断点续传
     */
    private static final int CODE_206 = 206;
    /**
     * 最多重定向次数，default：3
     * 因{@link OkHttpClient}本身已支持重定向，此处冗余
     */
    private static final int REDIRECT_TIME = 3;
    /**
     * 每次读取的buffer大小 ，default: 50k
     */
    private static final int BUFFER_SIZE = 1024 * 50;

    /**
     * 任务已建立，但未执行下载逻辑
     */
    public static final int STATE_IDLE = 0x01;
    /**
     * 任务正在请求服务器头部信息，未后续多线程下载，断点续下提供信息
     */
    public static final int STATE_CONNECTING = 0x02;
    /**
     * 任务正在下载中
     */
    public static final int STATE_DOWNLOADING = 0x03;
    /**
     * 任务已暂停，服务于多线程下载
     */
    public static final int STATE_PAUSED = 0x04;
    /**
     * 任务已取消
     */
    public static final int STATE_CANCEL = 0x05;
    /**
     * 任务下载完成
     */
    public static final int STATE_COMPLETED = 0x06;
    /**
     * 任务下载错误
     */
    public static final int STATE_ERROR = 0x07;

    /**
     * 任务下载时的状态
     */
    @Retention(RetentionPolicy.SOURCE)
    @IntDef({STATE_IDLE, STATE_CONNECTING, STATE_DOWNLOADING, STATE_PAUSED, STATE_COMPLETED, STATE_CANCEL, STATE_ERROR})
    public @interface DownloadState {
    }

    /**
     * 下载中用于更新的对象，调用{@link io.reactivex.Emitter#onNext(Object)}更新
     */
    private ProgressBean progressBean;
    /**
     * 下载的结束content-range
     */
    private long endPosition;
    /**
     * 当前的下载位置，为更新Process
     */
    private long currentPosition;

    /**
     * 网络请求client
     */
    private OkHttpClient okhttpClient;
    /**
     * 取消线程的标志量，调用{@link #cancel()}取消线程
     */
    private boolean isCancel;

    /**
     * 下载的请求头,承载断点续下的Range字段
     */
    private Request request;

    /**
     * 当前的下载状态，用于cancel thread
     */
    private int currentState;

    /**
     * 现在的目录
     */
    private String downloadFilePath;

    /**
     * 下载的文件名
     */
    private String downloadFileName;

    /**
     * 用于更新的Observable，{@link PublishSubject}在订阅时并不立即触发订阅事件，而是允许我们在任意时刻手动调用onNext(),onError(),onCompleted来触发事件
     */
    private ObservableEmitter<ProgressBean> emitter;
    /**
     * 当前重定向的次数
     */
    private int redirectTime = 0;

    /**
     * 构建一个下载线程
     * @param okHttpClient {@link OkHttpClient} 下载内核
     * @param requestUrl   下载的地址
     * @param filePath     文件保存目录
     * @param fileName     保存的文件的名字
     */
    public DownloadThread(@Nullable OkHttpClient okHttpClient, @NonNull String requestUrl, @NonNull String filePath, @NonNull String fileName) {
        this(okHttpClient, 0, SERVER_NORMAL, requestUrl, filePath, fileName, 0);
    }

    /**
     * 构建一个下载线程，下载 startPosition 和 endPosition 之间的内容，
     * 特别注意，包括 startPosition 但是不包括 endPosition
     * 另外需要注意 startPosition 与 currentPosition 的作用,startPosition : 开始下载 ，currentPosition ：正在下载
     * @param okHttpClient 下载的网络的client
     * @param startPostion 开始下载的位置
     * @param endPosition  下载的结束位置
     * @param requestUrl 下载链接
     * @param filePath 下载目录
     * @param fileName 下载文件名
     * @param tag 下载的线程标记，用作多线程时区分线程
     */
    public DownloadThread(@Nullable OkHttpClient okHttpClient, long startPostion, long endPosition, @NonNull String requestUrl, @NonNull String filePath, @NonNull String fileName, @NonNull int tag) {

        this.okhttpClient = okHttpClient;
        if (okHttpClient == null) {
            this.okhttpClient = new OkHttpClient.Builder().protocols(Collections.singletonList(Protocol.HTTP_1_1)).build();
        }
        this.endPosition = endPosition;
        this.currentPosition = startPostion;
        this.downloadFileName = fileName;
        if (filePath.endsWith(File.separator)) {
            this.downloadFilePath = filePath;
        } else {
            this.downloadFilePath = filePath + File.separator;
        }


        progressBean = new ProgressBean(tag);
        progressBean.startPosition = startPostion;
        progressBean.lastPosition = startPostion;

        currentState = STATE_IDLE;
        if (currentPosition >= endPosition && endPosition > 0) {
            updateState(STATE_COMPLETED);
            return;
        }
        Headers headers = null;

        if (endPosition == SERVER_NON_CONTENT_LENGTH) {
            //这个case是因为服务器支持，但是未返回content-length字段，即以chunk方式传输
            //此时不再使用断点续下的方式
            headers = new Headers.Builder().add("Range", "bytes=" + currentPosition + "-").build();
        }

        if (endPosition > 0) {
            //正常的Range断点续下
            //如此构造请求头之后，服务器应返回从current 到 end 且包含两端的字节
            //所以这里要使用endPosition-1，才能不包括最后的一个字节
            headers = new Headers.Builder().add("Range", "bytes=" + currentPosition + "-" + (endPosition - 1)).build();
        }

        Request.Builder builder = new Request.Builder().url(requestUrl);
        if (headers != null) {
            builder.headers(headers);
        }
        request = builder.build();
    }

    /**
     * cancel方法
     * 此方法仅仅在开始{@link #STATE_IDLE}以及下载{@link #STATE_DOWNLOADING}状态时生效
     * 多次调用不会有多次{@link io.reactivex.Observer#onNext(Object)}回调
     */
    public void cancel() {
        if (currentState != STATE_IDLE && currentState != STATE_DOWNLOADING) {
            return;
        }
        if (isCancel) {
            return;
        }
        isCancel = true;
        updateState(STATE_CANCEL, new DownloadException("下载线程：" + this + "主动调用cancel方法"));
    }

    /**
     * 订阅时调用此方法
     * @param emitter 上游流发送器
     * @throws Exception 订阅过程中的异常
     */
    @Override
    public void subscribe(ObservableEmitter<ProgressBean> emitter) throws Exception {
        this.emitter = emitter;
        run();
    }

    /**
     * 执行下载
     */
    private void run() {

        if (currentState != STATE_IDLE) {
            //判断标志为必须为默认初始值
            return;
        }
        realExec(request);
    }

    /**
     * 整整执行下载的方法
     * @param request 下载的请求体
     */
    private void realExec(Request request) {
        if (checkCancel()) {
            return;
        }

        //开始下载逻辑
        updateState(STATE_DOWNLOADING);
        Response response = null;
        InputStream inputStream = null;
        Closeable outputStream = null;
        try {
            response = okhttpClient.newCall(request).execute();

            int code = response.code();
            if (checkCode(response, code)) {
                return;
            }
            if (checkResponseBody(response)) {
                return;
            }
            if (checkCancel()) {
                return;
            }
            if (code >= CODE_300) {
                dealCode300(request, response);
                return;
            }

            if (endPosition == SERVER_NORMAL) {
                //当endPosition 为0时，需要获取下返回的content-length字段的header
                try {
                    this.endPosition = Long.parseLong(response.header("content-length"));
                } catch (Exception e) {
                    endPosition = SERVER_NON_CONTENT_LENGTH;
                }
            }
            if (code == CODE_200) {
                //未开启断点续下功能，返回全部数据，不要多线程使用此方式下载统一资源
                inputStream = response.body().byteStream();
                outputStream = new FileOutputStream(downloadFilePath + File.separator + downloadFileName);

                dealCode200(inputStream, (FileOutputStream) outputStream);
                return;
            }
            if (code == CODE_206) {
                if (dealRange(response)) {
                    return;
                }
                inputStream = response.body().byteStream();
                outputStream = new RandomAccessFile(downloadFilePath + File.separator + downloadFileName, "rw");
                ((RandomAccessFile) outputStream).seek(currentPosition);

                dealCode206(inputStream, (RandomAccessFile) outputStream);
            }

        } catch (IOException e) {
            updateState(STATE_ERROR, e);
            e.printStackTrace();
        } finally {
            DownloadUtil.close(inputStream);
            DownloadUtil.close(outputStream);
            DownloadUtil.close(response);
        }
    }

    /**
     * 解析 206 的range头
     * @param response 响应体
     * @return true：服务器未返回range头， false：成功解析range头
     */
    private boolean dealRange(Response response) {
        //开启断点续下
        String range = response.header("Content-Range");
        if (TextUtils.isEmpty(range)) {
            updateState(STATE_ERROR, new DownloadException("下载线程" + this + "服务器返回异常，断点续下时未见Content-Range"));
            return true;
        }
        String[] split = range.split(" +")[1].split("/");
        String offsetStart = split[0].split("-")[0];
        currentPosition = Long.parseLong(offsetStart);
        return false;
    }

    /**
     * 检查响应体是否为null
     * @param response 响应体
     * @return true：响应体为null  false：响应体为false
     */
    private boolean checkResponseBody(Response response) {
        if (response.body() == null) {
            updateState(STATE_ERROR, new DownloadException("下载线程" + this + "未返回响应体"));
            return true;
        }
        return false;
    }

    /**
     * 检查code状态
     * @param response 响应体
     * @param code 响应码
     * @return true:返回结果code异常  false：code成功
     */
    private boolean checkCode(Response response, int code) {
        //非重定向 && 非普通200下载 && 非206 断点续下
        boolean cd = (code >= CODE_400) || (code < CODE_300 && code != CODE_200 && code != CODE_206);
        if (cd) {
            //return掉 重定向，成功，断点续下 的code
            updateState(STATE_ERROR, new DownloadException("下载线程" + this + "响应失败" + response.toString()));
            return true;
        }
        return false;
    }

    /**
     * 检查当前下载任务是否取消
     * @return true :取消 false：未取消
     */
    private boolean checkCancel() {
        if (isCancel) {
            //判断标志位是否位cancel状态
            updateState(STATE_CANCEL, new DownloadException("下载线程" + this + "用户主动cancel"));
            return true;
        }
        return false;
    }

    /**
     * 处理成功206码进行读写
     * @param inputStream 输入流
     * @param outputStream 输出流
     * @return true:下载任务取消    false:正常读写完成
     * @throws IOException 读写异常
     */
    private boolean dealCode206(InputStream inputStream, RandomAccessFile outputStream) throws IOException {
        int length;
        byte[] buffer = new byte[BUFFER_SIZE];
        while ((length = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, length);

            currentPosition += length;

            updateProgress();

            if (checkCancel()) {
                return true;
            }
        }
        updateState(STATE_COMPLETED);
        return false;
    }

    /**
     * 处理code 200 进行io
     * @param inputStream 输入流
     * @param outputStream 输出流
     * @throws IOException 读写异常
     */
    private void dealCode200(InputStream inputStream, FileOutputStream outputStream) throws IOException {
        byte[] buffer = new byte[BUFFER_SIZE];
        int length;
        while ((length = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, length);

            currentPosition += length;

            if (checkCancel()) {
                return;
            }
            updateProgress();
        }
        updateState(STATE_COMPLETED);
    }

    /**
     * 处理code 300 进行重定向操作
     * @param request 请求体
     * @param response 响应体
     */
    private void dealCode300(Request request, Response response) {
        //重定向
        String redirectUrl = response.header("Location");
        if (TextUtils.isEmpty(redirectUrl)) {
            updateState(STATE_ERROR, new DownloadException("下载线程" + this + "重定向时未返回Location"));
            return;
        }
        Request req = request.newBuilder()
                .url(redirectUrl).build();
        if (redirectTime++ < REDIRECT_TIME) {
            realExec(req);
        } else {
            updateState(STATE_ERROR, new DownloadException("下载线程" + this + "重定向次数过多,超过3次"));
        }
    }

    /**
     * 更新下载状态
     * @param state 下载状态
     */
    private void updateState(@DownloadState int state) {
        updateState(state, null);
    }

    /**
     * 更行下载状态
     * @param state 下载状态
     * @param tr 停止时的异常
     */
    private void updateState(@DownloadState int state, Throwable tr) {
        if (currentState == state) {
            return;
        }

        currentState = state;
        progressBean.state = state;
        onNext(state, tr);
    }

    /**
     * 更新下载进度
     */
    private void updateProgress() {
        progressBean.currentPosition = currentPosition;
        progressBean.contentLength = endPosition;
        progressBean.state = currentState;
        onNext(currentState, null);
    }

    /**
     * 下载进度回调
     * @param state 下载状态
     * @param tr 失败时异常
     */
    private void onNext(@DownloadState int state, Throwable tr) {
        emitter.onNext(progressBean);
        if (state == STATE_COMPLETED) {
            emitter.onComplete();
        }
        boolean s = (state == STATE_CANCEL || state == STATE_ERROR);
        if (s && tr != null) {
            emitter.onNext(progressBean);
            emitter.onError(tr);
        }
    }


}
