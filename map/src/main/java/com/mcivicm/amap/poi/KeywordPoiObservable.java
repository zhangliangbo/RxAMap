package com.mcivicm.amap.poi;

import android.content.Context;

import com.amap.api.location.AMapLocation;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.mcivicm.amap.location.OncePositionObservable;

import io.reactivex.Observer;
import io.reactivex.functions.Consumer;

import static com.jakewharton.rxbinding2.internal.Preconditions.checkMainThread;

/**
 * 关键词搜索
 */

public class KeywordPoiObservable extends PoiObservable<PoiResult> {

    private Context context;
    private PoiSearch poiSearch;
    private PoiSearch.Query query;
    private String[] keywords;
    private int pageSize;
    private int pageNum;

    public KeywordPoiObservable(Context context, String[] keywords, int pageSize, int pageNum) {
        this.context = context;
        this.keywords = keywords;
        this.pageSize = pageSize;
        this.pageNum = pageNum;
    }

    @Override
    protected void subscribeActual(final Observer<? super PoiResult> observer) {
        if (!checkMainThread(observer)) {
            return;
        }
        //先查查地区编码
        new OncePositionObservable(context).subscribe(new Consumer<AMapLocation>() {
            @Override
            public void accept(AMapLocation aMapLocation) throws Exception {
                //生成查询参数
                StringBuilder stringBuilder = new StringBuilder();
                for (int i = 0; i < keywords.length; i++) {
                    if (i == keywords.length - 1) {
                        stringBuilder.append(keywords[i]);
                    } else {
                        stringBuilder.append(keywords[i]).append("|");
                    }
                }
                PoiSearch.Query query = new PoiSearch.Query(stringBuilder.toString(), "", aMapLocation.getCityCode());//精确到城市
                query.setPageSize(pageSize);
                query.setPageNum(pageNum);
                //新建查询
                poiSearch = new PoiSearch(context, query);
                PoiObservable.Source source = new PoiObservable.Source(poiSearch, observer);
                poiSearch.setOnPoiSearchListener(source);
                observer.onSubscribe(source);
                //开始异步查询
                poiSearch.searchPOIAsyn();
            }
        });
    }

    protected String[] getKeywords() {
        return keywords;
    }

    protected int getPageSize() {
        return pageSize;
    }

    protected int getPageNum() {
        return pageNum;
    }

    protected Context getContext() {
        return context;
    }
}
