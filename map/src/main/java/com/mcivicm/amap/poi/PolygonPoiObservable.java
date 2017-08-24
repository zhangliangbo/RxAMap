package com.mcivicm.amap.poi;

import android.content.Context;

import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;

import java.util.List;

import io.reactivex.Observer;

import static com.jakewharton.rxbinding2.internal.Preconditions.checkMainThread;

/**
 * 多边形搜索范围
 */

public class PolygonPoiObservable extends KeywordPoiObservable {

    private List<LatLonPoint> list;

    public PolygonPoiObservable(Context context, String[] keywords, int pageSize, int pageNum, List<LatLonPoint> list) {
        super(context, keywords, pageSize, pageNum);
        this.list = list;
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
        PoiSearch.Query query = new PoiSearch.Query(stringBuilder.toString(), "");//这里有中心点和半径，故不需要城市信息
        query.setPageSize(getPageSize());
        query.setPageNum(getPageNum());
        //新建查询
        PoiSearch poiSearch = new PoiSearch(getContext(), query);
        poiSearch.setBound(new PoiSearch.SearchBound(list));//关键的一步，设置中心点和半径
        //新建数据源
        Source source = new Source(poiSearch, observer);
        poiSearch.setOnPoiSearchListener(source);//设置查询监听
        observer.onSubscribe(source);//告知观察者开始数据发送
        poiSearch.searchPOIAsyn();//异步查询
    }
}
