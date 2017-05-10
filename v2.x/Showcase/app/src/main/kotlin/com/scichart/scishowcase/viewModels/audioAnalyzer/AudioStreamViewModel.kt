//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2017. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// AudioStreamViewModel.kt is part of the SCICHART® Showcases. Permission is hereby granted
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
import com.scichart.charting.visuals.axes.NumericAxis
import com.scichart.charting.visuals.renderableSeries.FastLineRenderableSeries
import com.scichart.data.model.DoubleRange
import com.scichart.drawing.common.SolidPenStyle
import com.scichart.scishowcase.model.audioAnalyzer.AudioData
import com.scichart.scishowcase.utils.XyDataSeries
import com.scichart.scishowcase.viewModels.ChartViewModel
import com.scichart.drawing.utility.ColorUtil.*
import com.scichart.scishowcase.utils.dip

class AudioStreamViewModel(context: Context, audioStreamBufferSize: Int) : ChartViewModel(context) {
    private val audioDS = XyDataSeries<Long, Short>().apply { fifoCapacity = audioStreamBufferSize }

    init {
        xAxes.add(NumericAxis(context).apply {
            autoRange = AutoRange.Always
            drawLabels = false
            drawMinorTicks = false
            drawMajorTicks = false
            drawMajorBands = false
            drawMinorGridLines = false
            drawMajorGridLines = false
        })

        yAxes.add(NumericAxis(context).apply {
            visibleRange = DoubleRange(Short.MIN_VALUE.toDouble(), Short.MAX_VALUE.toDouble())
            drawLabels = false
            drawMinorTicks = false
            drawMajorTicks = false
            drawMajorBands = false
            drawMinorGridLines = false
            drawMajorGridLines = false
        })

        val lineThickness = context.dip(1f)

        renderableSeries.add(FastLineRenderableSeries().apply {
            dataSeries = audioDS
            strokeStyle = SolidPenStyle(Grey, true, lineThickness, null)
        })
    }

    fun onNextAudioData(audioData: AudioData) {
        audioDS.append(audioData.xData, audioData.yData)
    }
}