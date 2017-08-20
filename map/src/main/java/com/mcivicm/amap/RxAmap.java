package com.mcivicm.amap;

import android.content.Context;
import android.util.SparseArray;

import com.amap.api.location.AMapLocation;
import com.amap.api.maps.model.LatLng;
import com.amap.api.navi.AMapNavi;
import com.amap.api.navi.model.AMapNaviPath;
import com.amap.api.navi.model.NaviLatLng;

import io.reactivex.Observable;
import io.reactivex.internal.functions.ObjectHelper;

/**
 * 高德地图
 */

public final class RxAmap {
    /**
     * 定位一次
     *
     * @param context
     * @return
     */
    public static Observable<AMapLocation> locateOnce(Context context) {
        ObjectHelper.requireNonNull(context, "context==null");
        return new OncePositionObservable(context);
    }

    /**
     * 根据制定定位间隔连续定位
     *
     * @param context
     * @param interval
     * @return
     */
    public static Observable<AMapLocation> locate(Context context, long interval) {
        ObjectHelper.requireNonNull(context, "context==null");
        return new PositionObservable(context, interval);
    }

    /**
     * 根据起点，终点和策略来计算多个驾车路径
     *
     * @param aMapNavi
     * @param s        起点
     * @param e        终点
     * @param strategy 可直接传入{@link com.amap.api.navi.enums.PathPlanningStrategy}，
     *                 也可使用{@link com.amap.api.navi.AMapNavi#strategyConvert(boolean, boolean, boolean, boolean, boolean)}来计算
     * @return
     */
    public static Observable<SparseArray<AMapNaviPath>> calculateMultiDrive(AMapNavi aMapNavi, NaviLatLng s, NaviLatLng e, int strategy) {
        ObjectHelper.requireNonNull(aMapNavi, "aMapNavi==null");
        ObjectHelper.requireNonNull(s, "s==null");
        ObjectHelper.requireNonNull(e, "e==null");
        StrategyHelper.requireMulti(strategy, "strategy非多线路策略");
        return new MultiDriveRouteObservable(aMapNavi, s, e, strategy);
    }


    /**
     * 根据起点，终点和策略来计算多个驾车路径
     *
     * @param aMapNavi
     * @param s
     * @param e
     * @param strategy
     * @return
     */
    public static Observable<SparseArray<AMapNaviPath>> calculateMultiDrive(AMapNavi aMapNavi, LatLng s, LatLng e, int strategy) {
        ObjectHelper.requireNonNull(s, "s==null");
        ObjectHelper.requireNonNull(e, "e==null");
        NaviLatLng ns = new NaviLatLng(s.latitude, s.longitude);
        NaviLatLng ne = new NaviLatLng(e.latitude, e.longitude);
        return calculateMultiDrive(aMapNavi, ns, ne, strategy);
    }

    /**
     * 给上下文初始化一个已准备好的导航器
     *
     * @param context
     * @return
     */
    public static Observable<AMapNavi> navi(Context context) {
        ObjectHelper.requireNonNull(context, "context==null");
        return new NaviInitObservable(context);
    }

    /**
     * 根据起点和终点计算单条驾驶路线
     *
     * @param aMapNavi
     * @param s
     * @param e
     * @param strategy
     * @return
     */
    public static Observable<AMapNaviPath> calculateSingleDrive(AMapNavi aMapNavi, NaviLatLng s, NaviLatLng e, int strategy) {
        ObjectHelper.requireNonNull(aMapNavi, "aMapNavi==null");
        ObjectHelper.requireNonNull(s, "s==null");
        ObjectHelper.requireNonNull(e, "e==null");
        StrategyHelper.requireSingle(strategy, "strategy非单线路策略");
        return new SingleDriveRouteObservable(aMapNavi, s, e, strategy);
    }

    /**
     * 根据起点和终点计算单条驾驶路线
     *
     * @param aMapNavi
     * @param s
     * @param e
     * @param strategy
     * @return
     */
    public static Observable<AMapNaviPath> calculateSingleDrive(AMapNavi aMapNavi, LatLng s, LatLng e, int strategy) {
        ObjectHelper.requireNonNull(s, "s==null");
        ObjectHelper.requireNonNull(e, "e==null");
        NaviLatLng ns = new NaviLatLng(s.latitude, s.longitude);
        NaviLatLng ne = new NaviLatLng(e.latitude, e.longitude);
        return calculateSingleDrive(aMapNavi, ns, ne, strategy);
    }

    /**
     * 计算步行路径
     * @param aMapNavi
     * @param s
     * @param e
     * @return
     */
    public static Observable<AMapNaviPath> calculateWalk(AMapNavi aMapNavi, NaviLatLng s, NaviLatLng e) {
        ObjectHelper.requireNonNull(aMapNavi, "aMapNavi==null");
        ObjectHelper.requireNonNull(s, "s==null");
        ObjectHelper.requireNonNull(e, "e==null");
        return new WalkRouteObservable(aMapNavi, s, e);
    }

    /**
     *  计算步行路径
     * @param aMapNavi
     * @param s
     * @param e
     * @return
     */
    public static Observable<AMapNaviPath> calculateWalk(AMapNavi aMapNavi, LatLng s, LatLng e) {
        ObjectHelper.requireNonNull(s, "s==null");
        ObjectHelper.requireNonNull(e, "e==null");
        NaviLatLng ns = new NaviLatLng(s.latitude, s.longitude);
        NaviLatLng ne = new NaviLatLng(e.latitude, e.longitude);
        return new WalkRouteObservable(aMapNavi, ns, ne);
    }

    /**
     * 计算骑行路径
     * @param aMapNavi
     * @param s
     * @param e
     * @return
     */
    public static Observable<AMapNaviPath> calculateRide(AMapNavi aMapNavi, NaviLatLng s, NaviLatLng e) {
        ObjectHelper.requireNonNull(aMapNavi, "aMapNavi==null");
        ObjectHelper.requireNonNull(s, "s==null");
        ObjectHelper.requireNonNull(e, "e==null");
        return new WalkRouteObservable(aMapNavi, s, e);
    }

    /**
     *  计算骑行路径
     * @param aMapNavi
     * @param s
     * @param e
     * @return
     */
    public static Observable<AMapNaviPath> calculateRide(AMapNavi aMapNavi, LatLng s, LatLng e) {
        ObjectHelper.requireNonNull(s, "s==null");
        ObjectHelper.requireNonNull(e, "e==null");
        NaviLatLng ns = new NaviLatLng(s.latitude, s.longitude);
        NaviLatLng ne = new NaviLatLng(e.latitude, e.longitude);
        return new WalkRouteObservable(aMapNavi, ns, ne);
    }

}
