package com.mcivicm.amap;

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
 * 位置源
 */

public class PositionObservable extends Observable<AMapLocation> {

    private AMapLocationClient client = null;
    private long interval = 800;//高德支持的最小间隔

    /**
     *
     * @param context
     * @param interval 毫秒
     */
    PositionObservable(Context context, long interval) {
        client = new AMapLocationClient(context);
        this.interval = interval;
        client.setLocationOption(getDefaultOption());
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
        options.setInterval(interval);//可选，设置定位间隔。默认为2秒
        options.setNeedAddress(true);//可选，设置是否返回逆地理地址信息。默认是true
        options.setOnceLocation(false);//可选，设置是否单次定位。默认是false
        options.setOnceLocationLatest(false);//可选，设置是否等待wifi刷新，默认为false.如果设置为true,会自动变为单次定位，持续定位时不要使用
        AMapLocationClientOption.setLocationProtocol(AMapLocationClientOption.AMapLocationProtocol.HTTP);//可选， 设置网络请求的协议。可选HTTP或者HTTPS。默认为HTTP
        options.setSensorEnable(false);//可选，设置是否使用传感器。默认是false
        options.setWifiScan(true); //可选，设置是否开启wifi扫描。默认为true，如果设置为false会同时停止主动刷新，停止以后完全依赖于系统刷新，定位位置可能存在误差
        options.setLocationCacheEnable(true); //可选，设置是否使用缓存定位，默认为true
        return options;
    }

    @Override
    protected void subscribeActual(Observer<? super AMapLocation> observer) {
        if (!checkMainThread(observer)) {//必须在主线程接收
            return;
        }
        Source source = new Source(client, observer);
        client.setLocationListener(source);
        client.startLocation();//先开始，随后的onSubscribe调用dispose可立即销毁
        observer.onSubscribe(source);//订阅时
    }

    /**
     * 可销毁的数据源（对Observer来说是数据源，对Client来说是消费者）
     */
    private static class Source extends MainThreadDisposable implements AMapLocationListener {

        private final AMapLocationClient client;
        private final Observer<? super AMapLocation> observer;

        Source(AMapLocationClient client, Observer<? super AMapLocation> observer) {
            this.client = client;
            this.observer = observer;
        }

        @Override
        public void onLocationChanged(AMapLocation aMapLocation) {
            if (!isDisposed()) {
                observer.onNext(aMapLocation);//这里不主动停止定位，因为要持续请求
            }
        }

        @Override
        protected void onDispose() {
            client.setLocationListener(null);
            client.stopLocation();//销毁的时候停止定位
        }
    }
}
