package com.mcivicm.amap.poi;

import com.amap.api.services.core.PoiItem;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.MainThreadDisposable;

/**
 * 基本POI数据源
 */

abstract class PoiObservable<T> extends Observable<T> {

    static final class Source extends MainThreadDisposable implements PoiSearch.OnPoiSearchListener {

        private final PoiSearch search;
        private final Observer<? super PoiResult> observer;

        Source(PoiSearch search, Observer<? super PoiResult> observer) {
            this.search = search;
            this.observer = observer;
        }

        @Override
        public void onPoiSearched(PoiResult poiResult, int i) {
            if (!isDisposed()) {
                if (i == 1000) {//1000表示成功
                    observer.onNext(poiResult);
                } else {
                    observer.onError(new Exception("根据关键字搜索POI错误，错误码:" + i));
                }
                search.setOnPoiSearchListener(null);//得到结果后取消监听
            }
        }

        @Override
        public void onPoiItemSearched(PoiItem poiItem, int i) {

        }

        @Override
        protected void onDispose() {
            search.setOnPoiSearchListener(null);
        }
    }
}
