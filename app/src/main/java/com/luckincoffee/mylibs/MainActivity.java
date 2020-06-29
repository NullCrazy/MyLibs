package com.luckincoffee.mylibs;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.lucky.lib.http.HttpClient;
import com.lucky.lib.http.HttpEventListenerImpl;
import com.lucky.lib.http.utils.HttpLog;
import com.lucky.lib.lchttp2rx.ReactiveHttp;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        HttpClient.Builder builder = new HttpClient.Builder();
        builder.appVersion("1100")
                .cid("101101")
                .baseUrl("https://capi.luckincoffee.com/")
                .sinKey("xvNWTwAkKfQ9sEUpy6kC")
                .retryTime(0)
                .openCache(false)
                .monitor(new HttpClient.LcMonitorListener() {
                    @Override
                    public void network(@androidx.annotation.NonNull HttpEventListenerImpl.NetWorkModel netWorkModel) {

                    }

                    @Override
                    public void dns(@androidx.annotation.NonNull String domain, @androidx.annotation.NonNull String ip, @androidx.annotation.NonNull String hijackIp, @androidx.annotation.NonNull String remark) {

                    }

                    @Override
                    public void busiException(@androidx.annotation.NonNull String exceptionCode, @Nullable Throwable exceptionStack, @androidx.annotation.NonNull String remark) {

                    }

                    @Override
                    public void networkException(@androidx.annotation.NonNull String exceptionCode, @Nullable Throwable exceptionStack, @androidx.annotation.NonNull String remark) {

                    }
                })
                .log(new HttpLog.ILog() {
                    @Override
                    public void d(String string) {
                        Log.d("lchttp", string);
                    }

                    @Override
                    public void e(String errMsg) {
                        Log.e("lchttp", errMsg);
                    }

                    @Override
                    public void wtf(Throwable tr) {
                    }
                })
                .readTimeOutSecond(30)
                .writeTimeOutSecond(30)
                .connectionTimeOutSecond(30);
        ReactiveHttp.init(builder.build());
        ReactiveHttp.getRequest()
                .param("version", 1410)
                .url("/resource/m/sys/app/start2")
                .observable(Object.class)
                .subscribe(new Observer<Object>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull Object o) {

                    }

                    @Override
                    public void onError(@NonNull Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }
}