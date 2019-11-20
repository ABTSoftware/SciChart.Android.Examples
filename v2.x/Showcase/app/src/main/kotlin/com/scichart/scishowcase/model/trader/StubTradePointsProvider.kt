//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2017. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// StubTradePointsProvider.kt is part of the SCICHART® Showcases. Permission is hereby granted
// to modify, create derivative works, distribute and publish any part of this source
// code whether for commercial, private or personal use.
//
// The SCICHART® Showcases are distributed in the hope that they will be useful, but
// without any warranty. It is provided "AS IS" without warranty of any kind, either
// expressed or implied.
//******************************************************************************

package com.scichart.scishowcase.model.trader

import android.content.Context
import android.util.Log
import java.io.BufferedReader
import java.io.InputStreamReader
import java.text.SimpleDateFormat
import java.util.*

class StubTradePointsProvider(private val context: Context) : ITradePointsProvider {

    private val EURUSD_DAILY_PATH = "data/EURUSD_Daily.csv"
    private val DATE_FORMAT = "yyyy.MM.dd"

    private val tradePoints by lazy {
        val data = TradeDataPoints()
        try {
            val dateFormat = SimpleDateFormat(DATE_FORMAT, Locale.US)
            BufferedReader(InputStreamReader(context.assets.open(EURUSD_DAILY_PATH))).useLines {
                it.forEach {
                    val split = it.split(',')
                    val timeString = dateFormat.parse(split[0])
                    val open = split[1].toDouble()
                    val high = split[2].toDouble()
                    val low = split[3].toDouble()
                    val close = split[4].toDouble()
                    val volume = split[5].toDouble()

                    data.append(timeString.time, open, high, low, close, volume)
                }
            }
        } catch (ex: Exception) {
            Log.e("LOAD STUB TRADE POINTS", ex.message)
        }
        data
    }

    override fun getTradePoints(tradeConfig: TradeConfig): TradeDataPoints = tradePoints
}