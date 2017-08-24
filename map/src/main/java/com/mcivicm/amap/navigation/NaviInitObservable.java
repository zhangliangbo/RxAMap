package com.mcivicm.amap.navigation;

import android.content.Context;

import com.amap.api.navi.AMapNavi;
import com.amap.api.navi.AMapNaviListener;
import com.amap.api.navi.model.AMapLaneInfo;
import com.amap.api.navi.model.AMapNaviCameraInfo;
import com.amap.api.navi.model.AMapNaviCross;
import com.amap.api.navi.model.AMapNaviInfo;
import com.amap.api.navi.model.AMapNaviLocation;
import com.amap.api.navi.model.AMapNaviTrafficFacilityInfo;
import com.amap.api.navi.model.AMapServiceAreaInfo;
import com.amap.api.navi.model.AimLessModeCongestionInfo;
import com.amap.api.navi.model.AimLessModeStat;
import com.amap.api.navi.model.NaviInfo;
import com.autonavi.tbt.TrafficFacilityInfo;

import java.util.concurrent.TimeUnit;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.MainThreadDisposable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;

import static com.jakewharton.rxbinding2.internal.Preconditions.checkMainThread;

/**
 * 导航是否准备好
 */

public class NaviInitObservable extends Observable<AMapNavi> {
    private Context context;

    public NaviInitObservable(Context context) {
        this.context = context;
    }


    @Override
    protected void subscribeActual(Observer<? super AMapNavi> observer) {
        if (!checkMainThread(observer)) {
            return;
        }

        AMapNavi navi = AMapNavi.getInstance(context);
        Source source = new Source(navi, observer);
        navi.addAMapNaviListener(source);
        observer.onSubscribe(source);
    }

    private static class Source extends MainThreadDisposable implements AMapNaviListener {

        private final AMapNavi navi;
        private final Observer<? super AMapNavi> observer;
        private Disposable fixedNotifaction;

        Source(AMapNavi navi, Observer<? super AMapNavi> observer) {
            this.navi = navi;
            this.observer = observer;
            /**
             * 如果AMapNavi已经初始化成功，
             * 无论添加多少个监听器，{@link AMapNaviListener#onInitNaviFailure()}和{@link AMapNaviListener#onInitNaviSuccess()}都不会有回调数据，
             * 所以过1s秒后直接通知准备好。
             */
            fixedNotifaction = Completable.timer(1, TimeUnit.SECONDS).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action() {
                @Override
                public void run() throws Exception {
                    Source.this.observer.onNext(Source.this.navi);
                    Source.this.navi.removeAMapNaviListener(Source.this);
                }
            });
        }

        @Override
        public void onInitNaviFailure() {
            fixedNotifaction.dispose();//如果有数据过来，取消掉默认的通知
            if (!isDisposed()) {
                observer.onError(new Exception("导航初始化失败"));
                this.navi.removeAMapNaviListener(this);
            }
        }

        @Override
        public void onInitNaviSuccess() {
            fixedNotifaction.dispose();//如果有数据过来，取消掉默认的通知
            if (!isDisposed()) {
                observer.onNext(navi);
                this.navi.removeAMapNaviListener(this);
                observer.onComplete();
            }
        }

        @Override
        public void onStartNavi(int i) {

        }

        @Override
        public void onTrafficStatusUpdate() {

        }

        @Override
        public void onLocationChange(AMapNaviLocation aMapNaviLocation) {

        }

        @Override
        public void onGetNavigationText(int i, String s) {

        }

        @Override
        public void onGetNavigationText(String s) {

        }

        @Override
        public void onEndEmulatorNavi() {

        }

        @Override
        public void onArriveDestination() {

        }

        @Override
        public void onCalculateRouteFailure(int i) {

        }

        @Override
        public void onReCalculateRouteForYaw() {

        }

        @Override
        public void onReCalculateRouteForTrafficJam() {

        }

        @Override
        public void onArrivedWayPoint(int i) {

        }

        @Override
        public void onGpsOpenStatus(boolean b) {

        }

        @Override
        public void onNaviInfoUpdate(NaviInfo naviInfo) {

        }

        @Override
        public void onNaviInfoUpdated(AMapNaviInfo aMapNaviInfo) {

        }

        @Override
        public void updateCameraInfo(AMapNaviCameraInfo[] aMapNaviCameraInfos) {

        }

        @Override
        public void onServiceAreaUpdate(AMapServiceAreaInfo[] aMapServiceAreaInfos) {

        }

        @Override
        public void showCross(AMapNaviCross aMapNaviCross) {

        }

        @Override
        public void hideCross() {

        }

        @Override
        public void showLaneInfo(AMapLaneInfo[] aMapLaneInfos, byte[] bytes, byte[] bytes1) {

        }

        @Override
        public void hideLaneInfo() {

        }

        @Override
        public void onCalculateRouteSuccess(int[] ints) {

        }

        @Override
        public void notifyParallelRoad(int i) {

        }

        @Override
        public void OnUpdateTrafficFacility(AMapNaviTrafficFacilityInfo aMapNaviTrafficFacilityInfo) {

        }

        @Override
        public void OnUpdateTrafficFacility(AMapNaviTrafficFacilityInfo[] aMapNaviTrafficFacilityInfos) {

        }

        @Override
        public void OnUpdateTrafficFacility(TrafficFacilityInfo trafficFacilityInfo) {

        }

        @Override
        public void updateAimlessModeStatistics(AimLessModeStat aimLessModeStat) {

        }

        @Override
        public void updateAimlessModeCongestionInfo(AimLessModeCongestionInfo aimLessModeCongestionInfo) {

        }

        @Override
        public void onPlayRing(int i) {

        }

        @Override
        protected void onDispose() {
            fixedNotifaction.dispose();
            navi.removeAMapNaviListener(this);
        }
    }
}
