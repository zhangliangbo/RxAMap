package com.mcivicm.amap;

import com.jakewharton.rxrelay2.BehaviorRelay;
import com.jakewharton.rxrelay2.ReplayRelay;

import org.junit.Test;

import io.reactivex.functions.Consumer;

/**
 * Created by zhang on 2017/8/20.
 */

public class RxRelayTest {
    @Test
    public void RepeatRelay() throws Exception {
        ReplayRelay<Integer> replayRelay = ReplayRelay.create();
        replayRelay.accept(1);
        replayRelay.accept(2);
        replayRelay.accept(3);
        replayRelay.subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
                System.out.println(integer);
            }
        });
        replayRelay.subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
                System.out.println(integer);
            }
        });
    }

    @Test
    public void behaviorRelay() throws Exception {
        BehaviorRelay<Object> relay = BehaviorRelay.create();
        relay.accept(0);
        relay.accept(1);
        relay.accept(2);
        relay.subscribe(new Consumer<Object>() {
            @Override
            public void accept(Object o) throws Exception {
                System.out.println(o);
            }
        });
        relay.accept(3);
        relay.accept(4);
        relay.accept(5);
        relay.accept(6);
    }
}
