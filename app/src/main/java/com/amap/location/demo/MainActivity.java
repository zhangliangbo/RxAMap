package com.amap.location.demo;

import android.Manifest;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatTextView;

import com.amap.api.location.AMapLocation;
import com.jakewharton.rxbinding2.view.RxView;
import com.mcivicm.map.RxAmap;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import butterknife.BindViews;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableObserver;


public class MainActivity extends AppCompatActivity {
    @BindViews({R.id.location, R.id.location_sequence})
    AppCompatTextView[] mLocation;

    @BindViews({R.id.locate, R.id.locate_sequence})
    AppCompatButton[] mLocate;

    SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss.SSS", Locale.CHINA);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        Observable.range(0, mLocation.length).subscribe(new Consumer<Integer>() {
            @Override
            public void accept(final Integer integer) throws Exception {
                RxView.clicks(mLocate[integer]).flatMap(new Function<Object, ObservableSource<Boolean>>() {
                    @Override
                    public Observable<Boolean> apply(@NonNull Object o) throws Exception {
                        return new RxPermissions(MainActivity.this).request(Manifest.permission.ACCESS_FINE_LOCATION);
                    }
                }).subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        if (aBoolean) {
                            if (integer == 0) {
                                RxAmap.locateOnce(MainActivity.this).subscribe(new Consumer<AMapLocation>() {
                                    @Override
                                    public void accept(AMapLocation aMapLocation) throws Exception {
                                        mLocation[integer].setText(format.format(new Date()) + "\n" + aMapLocation.toString());
                                    }
                                });
                            } else if (integer == 1) {
                                RxAmap.locate(MainActivity.this, 1000).subscribe(new DisposableObserver<AMapLocation>() {
                                    int need = 0;//需要10个

                                    @Override
                                    public void onNext(@NonNull AMapLocation aMapLocation) {
                                        mLocation[integer].setText(format.format(new Date()) + "\n" + "num: " + need + "\n" + aMapLocation.toString());
                                        need++;
                                        if (need == 10) {//10个之后自动取消
                                            dispose();
                                        }
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
                    }
                });
            }
        });
    }
}
