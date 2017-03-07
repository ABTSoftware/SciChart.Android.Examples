package com.scichart.scishowcase.model

import android.util.Log
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

abstract class DataProviderBase<E>(interval: Long, timeUnit: TimeUnit) : IDataProvider<E> {

    private val dataObservable = Observable
            .interval(0, interval, timeUnit)
            .map { onNext() }
            .doOnError { Log.e("DataProviderBase", "onError", it) }
            .doOnSubscribe { tryStart() }
            .doOnTerminate { tryStop() }
            .doOnDispose { tryStop() }
            .subscribeOn(Schedulers.single())

    override fun getData(): Observable<E> = dataObservable

    @Volatile
    private var isStarted = false

    private fun tryStart() {
        if (isStarted) return
        try {
            onStart()
        } finally {
            isStarted = true
        }
    }

    private fun tryStop(){
        if(!isStarted) return
        try {
            onStop()
        } finally {
            isStarted = false
        }
    }

    open protected fun onStart() {}
    open protected fun onStop() {}

    abstract protected fun onNext() : E
}