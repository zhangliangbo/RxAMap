package com.mcivicm.amap.poi;

import android.content.Context;

import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;

import io.reactivex.Observer;

import static com.jakewharton.rxbinding2.internal.Preconditions.checkMainThread;

/**
 * 周边POI
 */

public class CirclePoiObservable extends KeywordPoiObservable {
    private PoiSearch poiSearch;
    private LatLonPoint center;
    private int radius;

    public CirclePoiObservable(Context context, String[] keywords, int pageSize, int pageNum, LatLonPoint center, int radius) {
        super(context, keywords, pageSize, pageNum);
        this.center = center;
        this.radius = radius;
    }


    @Override
    protected void subscribeActual(Observer<? super PoiResult> observer) {
        if (!checkMainThread(observer)) {
            return;
        }
        //生成查询参数
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < getKeywords().length; i++) {
            if (i == getKeywords().length - 1) {
                stringBuilder.append(getKeywords()[i]);
            } else {
                stringBuilder.append(getKeywords()[i]).append("|");
            }
        }
        PoiSearch.Query query = new PoiSearch.Query(stringBuilder.toString(), "", "");//这里有中心点和半径，故不需要城市信息
        query.setPageSize(getPageSize());
        query.setPageNum(getPageNum());
        //新建查询
        poiSearch = new PoiSearch(getContext(), query);
        poiSearch.setBound(new PoiSearch.SearchBound(this.center, this.radius));//关键的一步
        PoiObservable.Source source = new PoiObservable.Source(poiSearch, observer);
        poiSearch.setOnPoiSearchListener(source);
        observer.onSubscribe(source);
        //开始异步查询
        poiSearch.searchPOIAsyn();
    }
}
