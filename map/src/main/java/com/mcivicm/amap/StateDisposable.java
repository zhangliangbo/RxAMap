package com.mcivicm.amap;

import java.util.concurrent.atomic.AtomicBoolean;

import io.reactivex.disposables.Disposable;

/**
 * 提供状态
 */

public abstract class StateDisposable implements Disposable {

    private final AtomicBoolean disposeQ = new AtomicBoolean();

    @Override
    public void dispose() {
        if (disposeQ.compareAndSet(false, true)) {
            onDispose();
        }
    }

    @Override
    public boolean isDisposed() {
        return disposeQ.get();
    }

    protected abstract void onDispose();
}
