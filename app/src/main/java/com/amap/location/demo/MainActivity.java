package com.amap.location.demo;

import android.Manifest;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatTextView;
import android.util.SparseArray;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.navi.AMapNavi;
import com.amap.api.navi.enums.PathPlanningStrategy;
import com.amap.api.navi.model.AMapNaviPath;
import com.amap.api.navi.model.NaviLatLng;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.poisearch.PoiResult;
import com.jakewharton.rxbinding2.view.RxView;
import com.mcivicm.amap.RxAmap;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import butterknife.BindViews;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableObserver;


public class MainActivity extends AppCompatActivity {
    @BindViews({R.id.location, R.id.location_sequence, R.id.navi_state, R.id.multi_drive_path,
            R.id.single_drive_path, R.id.walk_path, R.id.ride_path})
    AppCompatTextView[] mLocation;

    @BindViews({R.id.locate, R.id.locate_sequence, R.id.navi_init, R.id.calculate_multi_drive,
            R.id.calculate_single_drive, R.id.calculate_walk, R.id.calculate_ride})
    AppCompatButton[] mLocate;


    SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss.SSS", Locale.CHINA);

    AMapNavi mapNavi = null;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        Observable.range(0, mLocation.length).subscribe(new Consumer<Integer>() {
            @Override
            public void accept(final Integer integer) throws Exception {
                RxView.clicks(mLocate[integer]).flatMap(new Function<Object, ObservableSource<Boolean>>() {
                    @Override
                    public Observable<Boolean> apply(@NonNull Object o) throws Exception {
                        return new RxPermissions(MainActivity.this).request(Manifest.permission.ACCESS_FINE_LOCATION);
                    }
                }).subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        if (aBoolean) {
                            if (integer == 0) {
                                RxAmap.locateOnce(MainActivity.this).subscribe(new Consumer<AMapLocation>() {
                                    @Override
                                    public void accept(AMapLocation aMapLocation) throws Exception {
                                        mLocation[integer].setText(format.format(new Date()) + "\n" + aMapLocation.toString());
                                    }
                                });
                            } else if (integer == 1) {
                                RxAmap.locate(MainActivity.this, 3000).subscribe(new DisposableObserver<AMapLocation>() {
                                    int need = 0;

                                    @Override
                                    public void onNext(@NonNull AMapLocation aMapLocation) {
                                        mLocation[integer].setText(format.format(new Date()) + "\n" + "num: " + need + "\n" + aMapLocation.toString());
                                        need++;
                                        if (need == 20) {//20个之后自动取消
                                            dispose();
                                        }
                                    }

                                    @Override
                                    public void onError(@NonNull Throwable e) {

                                    }

                                    @Override
                                    public void onComplete() {

                                    }
                                });
                            } else if (integer == 2) {
                                mLocation[integer].setText("状态");
                                RxAmap.navi(MainActivity.this).subscribe(new Consumer<AMapNavi>() {
                                    @Override
                                    public void accept(AMapNavi o) throws Exception {
                                        mLocation[integer].setText("初始化成功");
                                        mapNavi = o;
                                    }
                                });
                            } else if (integer == 3) {
                                if (mapNavi == null) {
                                    Toast.makeText(MainActivity.this, "尚未初始化导航器", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                NaviLatLng start = new NaviLatLng(39.989614, 116.481763);
                                NaviLatLng end = new NaviLatLng(39.983456, 116.3154950);
                                RxAmap.calculateMultiDrive(mapNavi, start, end, PathPlanningStrategy.DRIVING_MULTIPLE_ROUTES_AVOID_COST_CONGESTION)
                                        .subscribe(new Observer<SparseArray<AMapNaviPath>>() {
                                            @Override
                                            public void onSubscribe(@NonNull Disposable d) {

                                            }

                                            @Override
                                            public void onNext(@NonNull SparseArray<AMapNaviPath> aMapNaviPathSparseArray) {
                                                StringBuilder stringBuilder = new StringBuilder();
                                                stringBuilder.append(format.format(new Date())).append("\n");
                                                for (int i = 0; i < aMapNaviPathSparseArray.size(); i++) {
                                                    AMapNaviPath path = aMapNaviPathSparseArray.get(aMapNaviPathSparseArray.keyAt(i));
                                                    stringBuilder.append(String.valueOf("总长：" + path.getAllLength())).append("\n");
                                                }
                                                mLocation[integer].setText(stringBuilder.toString());
                                            }

                                            @Override
                                            public void onError(@NonNull Throwable e) {
                                                mLocation[integer].setText(e.toString());
                                            }

                                            @Override
                                            public void onComplete() {

                                            }
                                        });
                            } else if (integer == 4) {
                                if (mapNavi == null) {
                                    Toast.makeText(MainActivity.this, "尚未初始化导航器", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                NaviLatLng start = new NaviLatLng(39.989614, 116.481763);
                                NaviLatLng end = new NaviLatLng(39.983456, 116.3154950);
                                RxAmap.calculateSingleDrive(mapNavi, start, end, PathPlanningStrategy.DRIVING_SHORTEST_DISTANCE)
                                        .subscribe(new Consumer<AMapNaviPath>() {
                                            @Override
                                            public void accept(AMapNaviPath aMapNaviPath) throws Exception {
                                                mLocation[integer].setText(format.format(new Date()) + "\n" + "总长：" + aMapNaviPath.getAllLength());
                                            }
                                        });
                            } else if (integer == 5) {
                                if (mapNavi == null) {
                                    Toast.makeText(MainActivity.this, "尚未初始化导航器", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                NaviLatLng start = new NaviLatLng(39.989614, 116.481763);
                                NaviLatLng end = new NaviLatLng(39.983456, 116.3154950);
                                RxAmap.calculateWalk(mapNavi, start, end)
                                        .subscribe(new Consumer<AMapNaviPath>() {
                                            @Override
                                            public void accept(AMapNaviPath aMapNaviPath) throws Exception {
                                                mLocation[integer].setText(format.format(new Date()) + "\n" + "总长：" + aMapNaviPath.getAllLength());
                                            }
                                        });
                            } else if (integer == 6) {
                                if (mapNavi == null) {
                                    Toast.makeText(MainActivity.this, "尚未初始化导航器", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                NaviLatLng start = new NaviLatLng(39.989614, 116.481763);
                                NaviLatLng end = new NaviLatLng(39.983456, 116.3154950);
                                RxAmap.calculateRide(mapNavi, start, end)
                                        .subscribe(new Consumer<AMapNaviPath>() {
                                            @Override
                                            public void accept(AMapNaviPath aMapNaviPath) throws Exception {
                                                mLocation[integer].setText(format.format(new Date()) + "\n" + "总长：" + aMapNaviPath.getAllLength());
                                            }
                                        });
                            } else if (integer == 7) {
                                RxAmap.keywordPoi(MainActivity.this, new String[]{"餐厅", "学校"}, 10, 1).subscribe(new Consumer<PoiResult>() {
                                    @Override
                                    public void accept(PoiResult poiResult) throws Exception {
                                        StringBuilder stringBuilder = new StringBuilder();
                                        stringBuilder.append(format.format(new Date())).append("\n");
                                        if (poiResult.getSearchSuggestionKeywords() != null && poiResult.getSearchSuggestionKeywords().size() > 0) {
                                            stringBuilder.append("关键词：").append("\n");
                                            for (String kw : poiResult.getSearchSuggestionKeywords()) {
                                                stringBuilder.append(kw).append(", ");
                                            }
                                            stringBuilder.append("\n");
                                        }

                                        if (poiResult.getPois() != null && poiResult.getPois().size() > 0) {
                                            stringBuilder.append("Poi: ").append("\n");
                                            for (PoiItem pt : poiResult.getPois()) {
                                                stringBuilder.append(pt.getTitle()).append(", ");
                                            }
                                            stringBuilder.append("\n");
                                        }
                                        mLocation[integer].setText(stringBuilder.toString());
                                    }
                                });
                            }

                        }
                    }
                });
            }
        });
    }
}
