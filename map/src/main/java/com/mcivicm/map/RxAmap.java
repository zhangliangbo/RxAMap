package com.mcivicm.map;

import android.content.Context;

import com.amap.api.location.AMapLocation;

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
     * @param context
     * @param interval
     * @return
     */
    public static Observable<AMapLocation> locate(Context context, long interval) {
        ObjectHelper.requireNonNull(context, "context==null");
        return new PositionObservable(context, interval);
    }
}
