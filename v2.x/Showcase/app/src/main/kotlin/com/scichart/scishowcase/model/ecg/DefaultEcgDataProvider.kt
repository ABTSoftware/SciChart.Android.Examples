//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2017. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// DefaultEcgDataProvider.kt is part of the SCICHART® Showcases. Permission is hereby granted
// to modify, create derivative works, distribute and publish any part of this source
// code whether for commercial, private or personal use.
//
// The SCICHART® examples are distributed in the hope that they will be useful, but
// without any warranty. It is provided "AS IS" without warranty of any kind, either
// expressed or implied.
//******************************************************************************

package com.scichart.scishowcase.model.ecg

import android.content.Context
import android.util.Log
import com.scichart.scishowcase.model.DataProviderBase
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.*
import java.util.concurrent.TimeUnit

open class DefaultEcgDataProvider(context: Context) : DataProviderBase<EcgData>(1000L, TimeUnit.MICROSECONDS) {
    //1. Heart rate or pulse rate (ECG HR)
    //2. Blood Pressure (NI BP)
    //3. Blood Volume (SV ml)
    //4. Blood Oxygenation (SPo2)
    private val ECG_TRACES = "data/EcgTraces.csv"
    private val SAMPLE_RATE = 800.0

    private var currentIndex: Int = 0
    private var totalIndex: Int = 0
    private var currentTrace = TraceAOrB.TraceA

    val xValues = ArrayList<Double>()
    val ecgHeartRate = ArrayList<Double>()
    val bloodPressure = ArrayList<Double>()
    val bloodVolume = ArrayList<Double>()
    val bloodOxygenation = ArrayList<Double>()

    init {
        try {
            val reader: BufferedReader = BufferedReader(InputStreamReader(context.assets.open(ECG_TRACES)))

            var line = reader.readLine()
            while (line != null) {
                val split = line.split(',')
                xValues.add(split[0].toDouble())
                ecgHeartRate.add(split[1].toDouble())
                bloodPressure.add(split[2].toDouble())
                bloodVolume.add(split[3].toDouble())
                bloodOxygenation.add(split[4].toDouble())

                line = reader.readLine()
            }
        } catch (ex: Exception) {
            Log.e("LOAD ECG", ex.message)
        }
    }

    override fun onNext(): EcgData {
        if (currentIndex >= xValues.size) {
            currentIndex = 0
        }

        val time = totalIndex / SAMPLE_RATE % 10
        val data = EcgData(time, ecgHeartRate[currentIndex], bloodPressure[currentIndex], bloodVolume[currentIndex], bloodOxygenation[currentIndex], currentTrace)

        currentIndex++
        totalIndex++

        if (totalIndex % 8000 == 0) {
            currentTrace = if (currentTrace == TraceAOrB.TraceA) TraceAOrB.TraceB else TraceAOrB.TraceA
        }

        return data
    }
}