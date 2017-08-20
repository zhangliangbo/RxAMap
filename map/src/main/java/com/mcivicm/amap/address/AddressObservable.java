package com.mcivicm.amap.address;

import android.content.Context;

import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.MainThreadDisposable;

import static com.jakewharton.rxbinding2.internal.Preconditions.checkMainThread;

/**
 * 经纬度转坐标
 */

public class AddressObservable extends Observable<RegeocodeResult> {

    private GeocodeSearch geocodeSearch;
    private RegeocodeQuery query;

    public AddressObservable(Context context, LatLonPoint point, float radius, String type) {
        this.geocodeSearch = new GeocodeSearch(context);
        this.query = new RegeocodeQuery(point, radius, type);
    }

    @Override
    protected void subscribeActual(Observer<? super RegeocodeResult> observer) {
        if (!checkMainThread(observer)) {
            return;
        }
        Source source = new Source(geocodeSearch, observer);
        this.geocodeSearch.setOnGeocodeSearchListener(source);
        observer.onSubscribe(source);
        this.geocodeSearch.getFromLocationAsyn(this.query);
    }

    private static final class Source extends MainThreadDisposable implements GeocodeSearch.OnGeocodeSearchListener {

        private final GeocodeSearch search;
        private final Observer<? super RegeocodeResult> observer;

        Source(GeocodeSearch search, Observer<? super RegeocodeResult> observer) {
            this.search = search;
            this.observer = observer;
        }

        @Override
        public void onRegeocodeSearched(RegeocodeResult regeocodeResult, int i) {
            if (!isDisposed()) {
                if (i == 1000) {
                    observer.onNext(regeocodeResult);
                } else {
                    observer.onError(new Exception("根据坐标查询地址失败，错误码：" + i));
                }
                this.search.setOnGeocodeSearchListener(null);//用完丢弃
            }
        }

        @Override
        public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {

        }

        @Override
        protected void onDispose() {
            this.search.setOnGeocodeSearchListener(null);
        }
    }
}
