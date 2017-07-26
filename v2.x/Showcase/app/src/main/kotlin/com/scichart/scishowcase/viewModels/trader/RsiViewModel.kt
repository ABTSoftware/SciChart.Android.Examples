//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2017. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// RsiViewModel.kt is part of the SCICHART® Showcases. Permission is hereby granted
// to modify, create derivative works, distribute and publish any part of this source
// code whether for commercial, private or personal use.
//
// The SCICHART® Showcases are distributed in the hope that they will be useful, but
// without any warranty. It is provided "AS IS" without warranty of any kind, either
// expressed or implied.
//******************************************************************************

package com.scichart.scishowcase.viewModels.trader

import android.content.Context
import com.scichart.charting.visuals.axes.AutoRange
import com.scichart.charting.visuals.axes.CategoryDateAxis
import com.scichart.charting.visuals.axes.NumericAxis
import com.scichart.charting.visuals.renderableSeries.FastLineRenderableSeries
import com.scichart.scishowcase.model.trader.TradeDataPoints
import com.scichart.scishowcase.utils.MovingAverage
import com.scichart.scishowcase.utils.XyDataSeries
import com.scichart.scishowcase.viewModels.ChartViewModel
import java.util.*

class RsiViewModel (context: Context) : ChartViewModel(context) {

    private val rsiDataSeries = XyDataSeries<Date, Double>().apply { acceptsUnsortedData = true }

    init {
        xAxes.add(CategoryDateAxis(context).apply {
            autoRange = AutoRange.Always
        })
        yAxes.add(NumericAxis(context).apply {
            autoRange = AutoRange.Always
        })

        renderableSeries.add(FastLineRenderableSeries().apply {
            dataSeries = rsiDataSeries
        })
    }

    fun setData(data: TradeDataPoints) {
        rsiDataSeries.clear()

        rsiDataSeries.append(data.xValues, MovingAverage.rsi(data, 12))
    }
}