package com.mcivicm.map;

import android.content.Context;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.MainThreadDisposable;

import static com.jakewharton.rxbinding2.internal.Preconditions.checkMainThread;

/**
 * 一次位置监听器
 */

public class OncePositionObservable extends Observable<AMapLocation> {

    private AMapLocationClient client = null;

    public OncePositionObservable(Context context) {
        client = new AMapLocationClient(context);
        client.setLocationOption(getDefaultOption());
    }

    @Override
    protected void subscribeActual(Observer<? super AMapLocation> observer) {
        if (!checkMainThread(observer)) {
            return;
        }
        LocationDisposable disposable = new LocationDisposable(client, observer);
        client.setLocationListener(disposable);
        client.startLocation();
        observer.onSubscribe(disposable);
    }

    /**
     * 默认的定位参数
     *
     * @author hongming.wang
     * @since 2.8.0
     */
    private AMapLocationClientOption getDefaultOption() {
        AMapLocationClientOption options = new AMapLocationClientOption();
        options.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);//可选，设置定位模式，可选的模式有高精度、仅设备、仅网络。默认为高精度模式
        options.setGpsFirst(false);//可选，设置是否gps优先，只在高精度模式下有效。默认关闭
        options.setHttpTimeOut(30000);//可选，设置网络请求超时时间。默认为30秒。在仅设备模式下无效
        options.setInterval(800);//可选，设置定位间隔。默认为2秒
        options.setNeedAddress(true);//可选，设置是否返回逆地理地址信息。默认是true
        options.setOnceLocation(true);//可选，设置是否单次定位。默认是false
        options.setOnceLocationLatest(true);//可选，设置是否等待wifi刷新，默认为false.如果设置为true,会自动变为单次定位，持续定位时不要使用
        AMapLocationClientOption.setLocationProtocol(AMapLocationClientOption.AMapLocationProtocol.HTTP);//可选， 设置网络请求的协议。可选HTTP或者HTTPS。默认为HTTP
        options.setSensorEnable(false);//可选，设置是否使用传感器。默认是false
        options.setWifiScan(true); //可选，设置是否开启wifi扫描。默认为true，如果设置为false会同时停止主动刷新，停止以后完全依赖于系统刷新，定位位置可能存在误差
        options.setLocationCacheEnable(true); //可选，设置是否使用缓存定位，默认为true
        return options;
    }

    private static final class LocationDisposable extends MainThreadDisposable implements AMapLocationListener {
        private final AMapLocationClient client;
        private final Observer<? super AMapLocation> observer;

        LocationDisposable(AMapLocationClient client, Observer<? super AMapLocation> observer) {
            this.client = client;
            this.observer = observer;
        }

        @Override
        public void onLocationChanged(AMapLocation aMapLocation) {
            if (!isDisposed()) {
                observer.onNext(aMapLocation);
                client.stopLocation();//定位一次之后马上停止定位
            }
        }

        @Override
        protected void onDispose() {
            client.setLocationListener(null);
            client.stopLocation();//取消了监听后也停止定位
        }
    }
}
