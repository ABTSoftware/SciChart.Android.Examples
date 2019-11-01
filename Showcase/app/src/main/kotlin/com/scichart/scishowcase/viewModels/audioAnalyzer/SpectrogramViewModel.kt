//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2017. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// SpectrogramViewModel.kt is part of the SCICHART® Showcases. Permission is hereby granted
// to modify, create derivative works, distribute and publish any part of this source
// code whether for commercial, private or personal use.
//
// The SCICHART® examples are distributed in the hope that they will be useful, but
// without any warranty. It is provided "AS IS" without warranty of any kind, either
// expressed or implied.
//******************************************************************************

package com.scichart.scishowcase.viewModels.audioAnalyzer

import android.content.Context
import com.scichart.charting.visuals.axes.AutoRange
import com.scichart.charting.visuals.axes.AxisAlignment
import com.scichart.charting.visuals.axes.NumericAxis
import com.scichart.charting.visuals.renderableSeries.ColorMap
import com.scichart.charting.visuals.renderableSeries.FastUniformHeatmapRenderableSeries
import com.scichart.core.model.DoubleValues
import com.scichart.scishowcase.utils.UniformHeatmapDataSeries
import com.scichart.scishowcase.viewModels.ChartViewModel
import com.scichart.drawing.utility.ColorUtil.*

class SpectrogramViewModel(context: Context, fftSize: Int, batchSize: Int) : ChartViewModel(context) {
    private val spectrogramDS = UniformHeatmapDataSeries<Long, Long, Double>(fftSize, batchSize)
    private val spectrogramValues = DoubleValues(fftSize * batchSize)

    init {
        xAxes.add(NumericAxis(context).apply {
            autoRange = AutoRange.Always
            drawLabels = false
            drawMinorTicks = false
            drawMajorTicks = false
            drawMajorBands = false
            drawMinorGridLines = false
            drawMajorGridLines = false
            axisAlignment = AxisAlignment.Left
            flipCoordinates = true
        })

        yAxes.add(NumericAxis(context).apply {
            autoRange = AutoRange.Always
            drawLabels = false
            drawMinorTicks = false
            drawMajorTicks = false
            drawMajorBands = false
            drawMinorGridLines = false
            drawMajorGridLines = false
            axisAlignment = AxisAlignment.Bottom
            flipCoordinates = true
        })

        renderableSeries.add(FastUniformHeatmapRenderableSeries().apply {
            dataSeries = spectrogramDS
            maximum = 70.0
            minimum = -30.0
            colorMap = ColorMap(
                    intArrayOf(Transparent, DarkBlue, Purple, Red, Yellow, White),
                    floatArrayOf(0f, 0.0001f, 0.25f, 0.50f, 0.75f, 1f)
            )
        })

        spectrogramValues.setSize(fftSize * batchSize)
    }

    fun onNextFFT(fftData: DoubleValues) {
        val spectrogramItems = spectrogramValues.itemsArray
        val fftItems = fftData.itemsArray

        val spectrogramSize = spectrogramValues.size()
        val fftSize = fftData.size()
        val offset = spectrogramSize - fftSize

        System.arraycopy(spectrogramItems, fftSize, spectrogramItems, 0, offset)
        System.arraycopy(fftItems, 0, spectrogramItems, offset, fftSize)

        spectrogramDS.updateZValues(spectrogramValues)
    }
}