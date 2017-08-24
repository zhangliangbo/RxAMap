package com.mcivicm.amap;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.util.SparseArray;

import com.amap.api.location.AMapLocation;
import com.amap.api.maps.model.LatLng;
import com.amap.api.navi.AMapNavi;
import com.amap.api.navi.model.AMapNaviPath;
import com.amap.api.navi.model.NaviLatLng;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.help.Tip;
import com.amap.api.services.poisearch.PoiResult;
import com.mcivicm.amap.location.OncePositionObservable;
import com.mcivicm.amap.location.PositionObservable;
import com.mcivicm.amap.navigation.MultiDriveRouteObservable;
import com.mcivicm.amap.navigation.NaviInitObservable;
import com.mcivicm.amap.navigation.SingleDriveRouteObservable;
import com.mcivicm.amap.navigation.WalkRouteObservable;
import com.mcivicm.amap.poi.CirclePoiObservable;
import com.mcivicm.amap.poi.IdPoiObservable;
import com.mcivicm.amap.poi.InputtipsObservable;
import com.mcivicm.amap.poi.KeywordPoiObservable;
import com.mcivicm.amap.poi.PolygonPoiObservable;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.internal.functions.ObjectHelper;

/**
 * 高德地图
 */

public final class RxAmap {

    public static Observable<Boolean> permission(Activity activity) {
        ObjectHelper.requireNonNull(activity, "activity==null");
        return new RxPermissions(activity).request(Manifest.permission.ACCESS_FINE_LOCATION);
    }

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
     *
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
     * 计算步行路径
     *
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
     *
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
     * 计算骑行路径
     *
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

    /**
     * 根据关键字查询附近的poi
     *
     * @param context
     * @param keyword
     * @return
     */
    public static Observable<PoiResult> keywordPoi(Context context, String keyword, int pageSize, int pageNum) {
        Checker.requireNoEmpty(keyword, "keyword");
        return keywordPoi(context, new String[]{keyword}, pageSize, pageNum);
    }

    /**
     * 根据多个关键字查询poi
     *
     * @param context
     * @param keywords
     * @param pageSize
     * @param pageNum
     * @return
     */
    public static Observable<PoiResult> keywordPoi(Context context, String[] keywords, int pageSize, int pageNum) {
        ObjectHelper.requireNonNull(context, "context==null");
        Checker.requireLengthPositive("keywords", keywords);
        ObjectHelper.verifyPositive(pageSize, "pageSize");
        ObjectHelper.verifyPositive(pageNum, "pageNum");
        return new KeywordPoiObservable(context, keywords, pageSize, pageNum);
    }

    /**
     * 搜索圆形范围的POI
     *
     * @param context
     * @param keywords
     * @param pageSize
     * @param pageNum
     * @param center
     * @param radius
     * @return
     */
    public static Observable<PoiResult> circlePoi(Context context, String[] keywords, int pageSize, int pageNum, LatLonPoint center, int radius) {
        ObjectHelper.requireNonNull(context, "context==null");
        Checker.requireLengthPositive("keywords", keywords);
        ObjectHelper.verifyPositive(pageSize, "pageSize");
        ObjectHelper.verifyPositive(pageNum, "pageNum");
        ObjectHelper.requireNonNull(center, "center==null");
        ObjectHelper.verifyPositive(radius, "radius");
        return new CirclePoiObservable(context, keywords, pageSize, pageNum, center, radius);
    }

    /**
     * 搜索圆形范围的POI
     *
     * @param context
     * @param keywords
     * @param pageSize
     * @param pageNum
     * @param center
     * @param radius
     * @return
     */
    public static Observable<PoiResult> circlePoi(Context context, String[] keywords, int pageSize, int pageNum, LatLng center, int radius) {
        ObjectHelper.requireNonNull(center, "center==null");
        LatLonPoint llp = new LatLonPoint(center.latitude, center.longitude);
        return new CirclePoiObservable(context, keywords, pageSize, pageNum, llp, radius);
    }

    /**
     * 单个关键字的圆形范围
     *
     * @param context
     * @param keyword
     * @param pageSize
     * @param pageNum
     * @param center
     * @param radius
     * @return
     */
    public static Observable<PoiResult> circlePoi(Context context, String keyword, int pageSize, int pageNum, LatLng center, int radius) {
        ObjectHelper.requireNonNull(keyword, "keyword==null");
        return circlePoi(context, new String[]{keyword}, pageSize, pageNum, center, radius);
    }

    /**
     * 单个关键字的圆形范围
     *
     * @param context
     * @param keyword
     * @param pageSize
     * @param pageNum
     * @param center
     * @param radius
     * @return
     */
    public static Observable<PoiResult> circlePoi(Context context, String keyword, int pageSize, int pageNum, LatLonPoint center, int radius) {
        ObjectHelper.requireNonNull(keyword, "keyword==null");
        return circlePoi(context, new String[]{keyword}, pageSize, pageNum, center, radius);
    }

    /**
     * 多边形范围的POI
     *
     * @param context
     * @param keywords
     * @param pageSize
     * @param pageNum
     * @param list
     * @return
     */
    public static Observable<PoiResult> polygonPoi(Context context, String[] keywords, int pageSize, int pageNum, List<LatLonPoint> list) {
        ObjectHelper.requireNonNull(context, "context==null");
        Checker.requireLengthPositive("keywords", keywords);
        ObjectHelper.verifyPositive(pageSize, "pageSize");
        ObjectHelper.verifyPositive(pageNum, "pageNum");
        ObjectHelper.requireNonNull(list, "list==null");
        return new PolygonPoiObservable(context, keywords, pageSize, pageNum, list);
    }

    /**
     * 单个关键字的多边形范围
     *
     * @param context
     * @param keyword
     * @param pageSize
     * @param pageNum
     * @param list
     * @return
     */
    public static Observable<PoiResult> polygonPoi(Context context, String keyword, int pageSize, int pageNum, List<LatLonPoint> list) {
        ObjectHelper.requireNonNull(keyword, "keyword==null");
        return polygonPoi(context, new String[]{keyword}, pageSize, pageNum, list);
    }

    /**
     * 根据ID查询POI
     *
     * @param context
     * @param id
     * @return
     */
    public static Observable<PoiItem> idPoi(Context context, String id) {
        ObjectHelper.requireNonNull(context, "context==null");
        ObjectHelper.requireNonNull(id, "id==null");
        return new IdPoiObservable(context, id);
    }

    /**
     *
     * @param context
     * @param query
     * @return
     */
    public static Observable<List<Tip>> poiTips(Context context, String query) {
        ObjectHelper.requireNonNull(context, "context==null");
        ObjectHelper.requireNonNull(query, "query==null");
        return new InputtipsObservable(context, query);
    }

}
