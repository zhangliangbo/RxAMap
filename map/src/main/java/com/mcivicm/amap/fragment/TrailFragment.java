package com.mcivicm.amap.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;

import com.amap.api.location.AMapLocation;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.PolylineOptions;
import com.amap.api.maps.model.animation.Animation;
import com.amap.api.maps.model.animation.TranslateAnimation;
import com.mcivicm.amap.R;
import com.mcivicm.amap.RxAmap;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

/**
 * 轨迹播放界面
 */

public class TrailFragment extends Fragment {

    private List<LatLng> list = new ArrayList<>(0);
    private MapViewFragment mapViewFragment;
    Marker marker;

    AppCompatButton play;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_trail, container, false);
        play = (AppCompatButton) v.findViewById(R.id.play);
        play.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                animateList(list, new int[]{0});
            }

            private void animateList(final List<LatLng> list, final int[] index) {
                final float distance = AMapUtils.calculateLineDistance(list.get(index[0]), list.get(index[0] + 1));
                marker.setPosition(list.get(index[0]));
                TranslateAnimation translateAnimation = new TranslateAnimation(list.get(index[0] + 1));
                final long time = (long) (distance * 10);
                translateAnimation.setDuration(time);
                translateAnimation.setInterpolator(new LinearInterpolator());
                marker.setAnimation(translateAnimation);
                marker.setAnimationListener(new Animation.AnimationListener() {
                    Disposable dispose;

                    @Override
                    public void onAnimationStart() {

                    }

                    @Override
                    public void onAnimationEnd() {

                        index[0]++;
                        if (index[0] + 1 < list.size()) {
                            animateList(list, index);
                        }
                    }
                });
                marker.startAnimation();
            }
        });
        mapViewFragment = (MapViewFragment) getChildFragmentManager().findFragmentById(R.id.map_view_fragment);
        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        simulate(100);

    }

    private void simulate(final int n) {

        RxAmap.locateOnce(getContext()).subscribe(new Consumer<AMapLocation>() {
            @Override
            public void accept(AMapLocation aMapLocation) throws Exception {
                list.clear();
                LatLng start = new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude());
                marker = mapViewFragment.getMapView().getMap().addMarker(new MarkerOptions().position(start).icon(BitmapDescriptorFactory.fromResource(R.drawable.gps_point)).anchor(0.5f, 0.5f));
                mapViewFragment.getMapView().getMap().animateCamera(CameraUpdateFactory.newLatLngZoom(start, 16f));
                list.add(start);
                Random r = new Random();
                for (int i = 0; i < n; i++) {
                    LatLng last = list.get(list.size() - 1);
                    LatLng ll = new LatLng(last.latitude + r.nextDouble() / 360D, last.longitude + r.nextDouble() / 360D);
                    list.add(ll);
                }
                drawLine(list);
            }
        });
    }

    private void drawLine(List<LatLng> ll) {
        PolylineOptions options = new PolylineOptions().color(Color.BLUE).width(4f);
        options.setPoints(ll);
        mapViewFragment.getMapView().getMap().addPolyline(options);
    }


}
