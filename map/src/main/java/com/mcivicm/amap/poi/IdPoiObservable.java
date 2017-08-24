package com.mcivicm.amap.poi;

import android.content.Context;

import com.amap.api.services.core.PoiItem;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.mcivicm.amap.StateDisposable;

import io.reactivex.Observable;
import io.reactivex.Observer;

import static com.jakewharton.rxbinding2.internal.Preconditions.checkMainThread;

/**
 * 根据POI的ID检索POI
 */

public class IdPoiObservable extends Observable<PoiItem> {
    private String id;
    private Context context;

    public IdPoiObservable(Context context, String id) {
        this.context = context;
        this.id = id;
    }

    @Override
    protected void subscribeActual(Observer<? super PoiItem> observer) {
        if (!checkMainThread(observer)) {
            return;
        }
        PoiSearch poiSearch = new PoiSearch(this.context, null);//直接空即可
        Source source = new Source(poiSearch, observer);
        poiSearch.setOnPoiSearchListener(source);
        observer.onSubscribe(source);
        poiSearch.searchPOIIdAsyn(this.id);
    }

    private class Source extends StateDisposable implements PoiSearch.OnPoiSearchListener {
        private final PoiSearch poiSearch;
        private final Observer<? super PoiItem> observer;

        Source(PoiSearch poiSearch, Observer<? super PoiItem> observer) {
            this.poiSearch = poiSearch;
            this.observer = observer;
        }

        @Override
        public void onPoiSearched(PoiResult poiResult, int i) {

        }

        @Override
        public void onPoiItemSearched(PoiItem poiItem, int i) {
            if (i == 1000) {
                observer.onNext(poiItem);//成功发送
                observer.onComplete();//完成
            } else {
                observer.onError(new Exception("根据关键字搜索POI错误，错误码:" + i));
            }
            poiSearch.setOnPoiSearchListener(null);
        }

        @Override
        protected void onDispose() {
            poiSearch.setOnPoiSearchListener(null);
        }
    }
}
