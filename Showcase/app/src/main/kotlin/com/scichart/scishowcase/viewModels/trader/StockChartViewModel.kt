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
import android.databinding.Bindable
import android.support.v4.content.ContextCompat
import com.scichart.charting.modifiers.OnAnnotationCreatedListener
import com.scichart.charting.visuals.axes.AutoRange
import com.scichart.charting.visuals.axes.CategoryDateAxis
import com.scichart.charting.visuals.axes.NumericAxis
import com.scichart.charting.visuals.renderableSeries.FastCandlestickRenderableSeries
import com.scichart.charting.visuals.renderableSeries.FastColumnRenderableSeries
import com.scichart.charting.visuals.renderableSeries.FastLineRenderableSeries
import com.scichart.data.model.DoubleRange
import com.scichart.scishowcase.BR
import com.scichart.scishowcase.R
import com.scichart.scishowcase.model.trader.TradeDataPoints
import com.scichart.scishowcase.utils.MovingAverage
import com.scichart.scishowcase.utils.OhlcDataSeries
import com.scichart.scishowcase.utils.XyDataSeries
import io.reactivex.subjects.PublishSubject
import java.util.*

class StockChartViewModel(context: Context, sharedXRange: DoubleRange, ma50PublishSubject: PublishSubject<Boolean>, ma100PublishSubject: PublishSubject<Boolean>, listener: OnAnnotationCreatedListener)
    : BaseChartPaneViewModel(context, "StockAxis", listener) {

    private val volumeUpColor = ContextCompat.getColor(context, R.color.stock_chart_volume_up_color)
    private val volumeDownColor = ContextCompat.getColor(context, R.color.stock_chart_volume_down_color)

    private val stockDataSeries = OhlcDataSeries<Date, Double>().apply { acceptsUnsortedData = true }
    private val volumeDataSeries = XyDataSeries<Date, Double>().apply { acceptsUnsortedData = true }
    private val maLowDataSeries = XyDataSeries<Date, Double>().apply { acceptsUnsortedData = true }
    private val maHighDataSeries = XyDataSeries<Date, Double>().apply { acceptsUnsortedData = true }

    init {
        xAxes.add(CategoryDateAxis(context).apply {
            visibleRange = sharedXRange
        })
        yAxes.add(NumericAxis(context).apply {
            axisId = "StockAxis"
            autoRange = AutoRange.Always
        })
        yAxes.add(NumericAxis(context).apply {
            axisId = "VolumeAxis"
            autoRange = AutoRange.Always
            growBy = DoubleRange(0.0, 2.0)
            drawLabels = false
            drawMajorTicks = false
            drawMinorTicks = false
        })

        val stockRs = FastCandlestickRenderableSeries().apply {
            dataSeries = stockDataSeries
            yAxisId = "StockAxis"
        }

        val maLowRs = FastLineRenderableSeries().apply {
            dataSeries = maLowDataSeries
            yAxisId = "StockAxis"
        }

        val maHighRs = FastLineRenderableSeries().apply {
            dataSeries = maHighDataSeries
            yAxisId = "StockAxis"
        }

        val volumeRs = FastColumnRenderableSeries().apply {
            dataSeries = volumeDataSeries
            dataPointWidth = 1.0
            paletteProvider = VolumePaletteProvider(stockRs, volumeUpColor, volumeDownColor)
            yAxisId = "VolumeAxis"
        }

        renderableSeries.add(stockRs)
        renderableSeries.add(maLowRs)
        renderableSeries.add(maHighRs)
        renderableSeries.add(volumeRs)

        ma50PublishSubject.doOnNext { maLowRs.isVisible = it }.subscribe()
        ma100PublishSubject.doOnNext { maHighRs.isVisible = it }.subscribe()
    }

    fun setData(data: TradeDataPoints) {
        stockDataSeries.clear()
        volumeDataSeries.clear()
        maLowDataSeries.clear()
        maHighDataSeries.clear()

        stockDataSeries.append(data.xValues, data.openValues, data.highValues, data.lowValues, data.closeValues)
        volumeDataSeries.append(data.xValues, data.volumeValues)
        maLowDataSeries.append(data.xValues, MovingAverage.movingAverage(data.closeValues, 50))
        maHighDataSeries.append(data.xValues, MovingAverage.movingAverage(data.closeValues, 100))

        viewportManager.zoomExtentsX()
    }
}