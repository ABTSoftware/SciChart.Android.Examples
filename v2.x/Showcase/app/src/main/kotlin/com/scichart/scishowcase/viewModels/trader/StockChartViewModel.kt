//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2017. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// StockChartViewModel.ktart of the SCICHART® Showcases. Permission is hereby granted
// to modify, create derivative works, distribute and publish any part of this source
// code whether for commercial, private or personal use.
//
// The SCICHART® examples are distributed in the hope that they will be useful, but
// without any warranty. It is provided "AS IS" without warranty of any kind, either
// expressed or implied.
//******************************************************************************

package com.scichart.scishowcase.viewModels.trader

import android.content.Context
import com.scichart.charting.visuals.axes.AutoRange
import com.scichart.charting.visuals.axes.CategoryDateAxis
import com.scichart.charting.visuals.axes.NumericAxis
import com.scichart.charting.visuals.renderableSeries.FastCandlestickRenderableSeries
import com.scichart.charting.visuals.renderableSeries.FastLineRenderableSeries
import com.scichart.charting.visuals.renderableSeries.FastMountainRenderableSeries
import com.scichart.scishowcase.model.trader.TradeDataPoints
import com.scichart.scishowcase.utils.MovingAverage
import com.scichart.scishowcase.utils.OhlcDataSeries
import com.scichart.scishowcase.utils.XyDataSeries
import com.scichart.scishowcase.viewModels.ChartViewModel
import java.util.*

class StockChartViewModel(context: Context) : ChartViewModel(context) {

    private val stockDataSeries = OhlcDataSeries<Date, Double>().apply { acceptsUnsortedData = true }
    private val volumeDataSeries = XyDataSeries<Date, Double>().apply { acceptsUnsortedData = true }
    private val maLowDataSeries = XyDataSeries<Date, Double>().apply { acceptsUnsortedData = true }
    private val maHighDataSeries = XyDataSeries<Date, Double>().apply { acceptsUnsortedData = true }

    init {
        xAxes.add(CategoryDateAxis(context).apply {
            autoRange = AutoRange.Always
        })
        yAxes.add(NumericAxis(context).apply {
            axisId = "StockAxis"
            autoRange = AutoRange.Always
        })
        yAxes.add(NumericAxis(context).apply {
            axisId = "VolumeAxis"
            autoRange = AutoRange.Always
            drawLabels = false
            drawMajorTicks = false
            drawMinorTicks = false
        })

        renderableSeries.add(FastCandlestickRenderableSeries().apply {
            dataSeries = stockDataSeries
            yAxisId = "StockAxis"
        })

        renderableSeries.add(FastLineRenderableSeries().apply {
            dataSeries = maLowDataSeries
            yAxisId = "StockAxis"
        })

        renderableSeries.add(FastLineRenderableSeries().apply {
            dataSeries = maHighDataSeries
            yAxisId = "StockAxis"
        })

        renderableSeries.add(FastMountainRenderableSeries().apply {
            dataSeries = volumeDataSeries
            yAxisId = "VolumeAxis"
        })
    }

    fun setData(data: TradeDataPoints) {
        stockDataSeries.clear()
        volumeDataSeries.clear()
        maLowDataSeries.clear()
        maHighDataSeries.clear()

        stockDataSeries.append(data.xValues, data.openValues, data.highValues, data.lowValues, data.closeValues)
        volumeDataSeries.append(data.xValues, data.volumeValues)
        maLowDataSeries.append(data.xValues, MovingAverage.movingAverage(data.closeValues, 50))
        maHighDataSeries.append(data.xValues, MovingAverage.movingAverage(data.closeValues, 200))
    }
}