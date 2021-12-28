//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2020. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// DataProviderBase.java is part of the SCICHART® Examples. Permission is hereby granted
// to modify, create derivative works, distribute and publish any part of this source
// code whether for commercial, private or personal use.
//
// The SCICHART® examples are distributed in the hope that they will be useful, but
// without any warranty. It is provided "AS IS" without warranty of any kind, either
// expressed or implied.
//******************************************************************************

package com.scichart.examples.fragments.base;

import android.util.Log;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

public abstract class DataProviderBase<E> implements IDataProvider<E> {

    private final Observable<E> dataObservable;

    private volatile boolean isStarted = false;

    protected DataProviderBase(long interval, TimeUnit timeUnit) {
        dataObservable = Observable.interval(interval, timeUnit)
                .map( l -> onNext())
                .doOnError(throwable -> Log.e("DataProvider", "onError", throwable))
                .doOnSubscribe(disposable -> tryStart())
                .doOnTerminate(this::tryStop)
                .doOnDispose(this::tryStop)
                .subscribeOn(Schedulers.single());
    }

    @Override
    public final Observable<E> getData() {
        return dataObservable;
    }

    private void tryStart() {
        if(isStarted) return;

        try {
            onStart();
        } finally {
            isStarted = true;
        }
    }

    protected void onStart() {

    }

    private void tryStop() {
        if(!isStarted)return;

        try {
            onStop();
        } finally {
            isStarted = false;
        }
    }

    protected void onStop() {

    }

    protected abstract E onNext();
}
