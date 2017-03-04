package com.scichart.scishowcase.model.ecg

import io.reactivex.Flowable

interface IEcgDataProvider {
    fun start()

    fun stop()

    fun getEcgData(): Flowable<EcgData>
}