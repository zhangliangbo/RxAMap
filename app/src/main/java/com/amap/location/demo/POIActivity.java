package com.amap.location.demo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.amap.api.location.AMapLocation;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.poisearch.PoiResult;
import com.mcivicm.amap.fragment.MapViewFragment;
import com.mcivicm.amap.RxAmap;

import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.ObservableSource;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

public class POIActivity extends AppCompatActivity {
    MapViewFragment mapViewFragment;

    @OnClick({R.id.keyword_poi, R.id.circle_poi, R.id.polygon_poi})
    void onClick(View v) {
        switch (v.getId()) {
            case R.id.keyword_poi:
                RxAmap.permission(POIActivity.this).subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        if (aBoolean) {
                            RxAmap.keywordPoi(POIActivity.this, "学校", 20, 1).subscribe(new Consumer<PoiResult>() {
                                @Override
                                public void accept(PoiResult poiResult) throws Exception {
                                    consume(poiResult);
                                }
                            });
                        }
                    }
                });

                break;
            case R.id.circle_poi:
                RxAmap.permission(POIActivity.this).subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        if (aBoolean) {
                            RxAmap.locateOnce(POIActivity.this).flatMap(new Function<AMapLocation, ObservableSource<PoiResult>>() {
                                @Override
                                public ObservableSource<PoiResult> apply(@NonNull AMapLocation aMapLocation) throws Exception {
                                    return RxAmap.circlePoi(POIActivity.this, new String[]{"学校"}, 20, 1, new LatLonPoint(aMapLocation.getLatitude(), aMapLocation.getLongitude()), 1000);
                                }
                            }).subscribe(new Consumer<PoiResult>() {
                                @Override
                                public void accept(PoiResult poiResult) throws Exception {
                                    consume(poiResult);
                                }
                            });

                        }
                    }
                });
                break;
            case R.id.polygon_poi:
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poi);
        ButterKnife.bind(this);
        mapViewFragment = (MapViewFragment) getSupportFragmentManager().findFragmentById(R.id.map_view);
    }

    private void consume(PoiResult poiResult) {
        mapViewFragment.getMapView().getMap().clear();
        LatLngBounds.Builder builder = LatLngBounds.builder();
        for (PoiItem poiItem : poiResult.getPois()) {
            LatLng ll = new LatLng(poiItem.getLatLonPoint().getLatitude(), poiItem.getLatLonPoint().getLongitude());
            MarkerOptions markerOptions = new MarkerOptions()
                    .title(poiItem.getTitle())
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.gps_point))
                    .position(ll);
            builder.include(ll);
            mapViewFragment.getMapView().getMap().addMarker(markerOptions);
        }
        mapViewFragment.getMapView().getMap().animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 40));
    }
}
