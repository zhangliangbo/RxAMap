package com.mcivicm.amap.navigation;

import android.util.SparseArray;

import com.amap.api.navi.AMapNavi;
import com.amap.api.navi.AMapNaviListener;
import com.amap.api.navi.enums.PathPlanningErrCode;
import com.amap.api.navi.model.AMapLaneInfo;
import com.amap.api.navi.model.AMapNaviCameraInfo;
import com.amap.api.navi.model.AMapNaviCross;
import com.amap.api.navi.model.AMapNaviInfo;
import com.amap.api.navi.model.AMapNaviLocation;
import com.amap.api.navi.model.AMapNaviPath;
import com.amap.api.navi.model.AMapNaviTrafficFacilityInfo;
import com.amap.api.navi.model.AMapServiceAreaInfo;
import com.amap.api.navi.model.AimLessModeCongestionInfo;
import com.amap.api.navi.model.AimLessModeStat;
import com.amap.api.navi.model.NaviInfo;
import com.amap.api.navi.model.NaviLatLng;
import com.autonavi.tbt.TrafficFacilityInfo;

import java.util.ArrayList;
import java.util.Arrays;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.MainThreadDisposable;

import static com.jakewharton.rxbinding2.internal.Preconditions.checkMainThread;

/**
 * 驾驶线路
 */

public class MultiDriveRouteObservable extends Observable<SparseArray<AMapNaviPath>> {
    private AMapNavi navi;
    private NaviLatLng sll = null;
    private NaviLatLng ell = null;
    private int strategy;

    public MultiDriveRouteObservable(AMapNavi navi, NaviLatLng start, NaviLatLng end, int strategy) {
        this.navi = navi;
        sll = start;
        ell = end;
        this.strategy = strategy;
    }

    @Override
    protected void subscribeActual(Observer<? super SparseArray<AMapNaviPath>> observer) {
        if (!checkMainThread(observer)) {
            return;
        }
        Source source = new Source(navi, observer);
        navi.addAMapNaviListener(source);
        observer.onSubscribe(source);//可以dispose解除监听
        navi.calculateDriveRoute(Arrays.asList(sll), Arrays.asList(ell), new ArrayList<NaviLatLng>(0), strategy);
    }


    private static final class Source extends MainThreadDisposable implements AMapNaviListener {

        private final AMapNavi mapNavi;
        private final Observer<? super SparseArray<AMapNaviPath>> observer;

        Source(AMapNavi navi, Observer<? super SparseArray<AMapNaviPath>> observer) {
            this.mapNavi = navi;
            this.observer = observer;
        }

        @Override
        public void onInitNaviFailure() {

        }

        @Override
        public void onInitNaviSuccess() {

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
            if (!isDisposed()) {
                observer.onError(new Exception("路线计算失败" + ", 错误码：" + i + ", 详情参见" + PathPlanningErrCode.class.getName()));
                if (mapNavi != null) {
                    mapNavi.removeAMapNaviListener(this);//取得数据之后立即解除该监听
                }
            }
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
            if (!isDisposed()) {
                SparseArray<AMapNaviPath> sparseArray = new SparseArray<>();
                if (ints != null && ints.length > 0 && mapNavi != null) {
                    for (int i : ints) {
                        AMapNaviPath path = mapNavi.getNaviPaths().get(i);
                        if (path != null) {
                            sparseArray.put(i, path);
                        }
                    }
                }
                observer.onNext(sparseArray);
                if (mapNavi != null) {
                    mapNavi.removeAMapNaviListener(this);//取得数据之后立即解除该监听
                }
            }
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
            if (mapNavi != null) {
                mapNavi.removeAMapNaviListener(this);
            }
        }
    }
}
