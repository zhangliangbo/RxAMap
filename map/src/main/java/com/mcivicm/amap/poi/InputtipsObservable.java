package com.mcivicm.amap.poi;

import android.content.Context;

import com.amap.api.location.AMapLocation;
import com.amap.api.services.help.Inputtips;
import com.amap.api.services.help.InputtipsQuery;
import com.amap.api.services.help.Tip;
import com.mcivicm.amap.StateDisposable;
import com.mcivicm.amap.location.OncePositionObservable;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.functions.Consumer;

import static com.jakewharton.rxbinding2.internal.Preconditions.checkMainThread;

/**
 * 输入提示
 */

public class InputtipsObservable extends Observable<List<Tip>> {
    private Context context;
    private String queryText;

    public InputtipsObservable(Context context, String text) {
        this.context = context;
        this.queryText = text;
    }

    @Override
    protected void subscribeActual(final Observer<? super List<Tip>> observer) {
        if (!checkMainThread(observer)) {
            return;
        }
        //定位一次获取城市
        new OncePositionObservable(context).subscribe(new Consumer<AMapLocation>() {
            @Override
            public void accept(AMapLocation aMapLocation) throws Exception {
                InputtipsQuery inputtipsQuery = new InputtipsQuery(queryText, aMapLocation.getCity());
                inputtipsQuery.setCityLimit(true);//限制在当前城市
                Inputtips inputtips = new Inputtips(context, inputtipsQuery);
                Source source = new Source(inputtips, observer);
                inputtips.setInputtipsListener(source);
                observer.onSubscribe(source);
                inputtips.requestInputtipsAsyn();//开始查询
            }
        });

    }

    private class Source extends StateDisposable implements Inputtips.InputtipsListener {
        private final Inputtips inputtips;
        private final Observer<? super List<Tip>> observer;

        Source(Inputtips inputtips, Observer<? super List<Tip>> observer) {
            this.inputtips = inputtips;
            this.observer = observer;
        }

        @Override
        public void onGetInputtips(List<Tip> list, int i) {
            if (i == 1000) {
                observer.onNext(list);
                observer.onComplete();
            } else {
                observer.onError(new Exception("查询错误，错误码：" + i));
            }
            inputtips.setInputtipsListener(null);
        }

        @Override
        protected void onDispose() {
            inputtips.setInputtipsListener(null);
        }
    }
}
