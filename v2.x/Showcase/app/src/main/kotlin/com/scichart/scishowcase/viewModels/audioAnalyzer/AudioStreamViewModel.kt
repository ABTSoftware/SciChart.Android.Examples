package com.scichart.scishowcase.viewModels.audioAnalyzer

import android.content.Context
import com.scichart.charting.visuals.axes.AutoRange
import com.scichart.charting.visuals.axes.NumericAxis
import com.scichart.charting.visuals.renderableSeries.FastLineRenderableSeries
import com.scichart.data.model.DoubleRange
import com.scichart.drawing.common.SolidPenStyle
import com.scichart.scishowcase.model.audioAnalyzer.AudioData
import com.scichart.scishowcase.utils.XyDataSeries
import com.scichart.scishowcase.utils.init
import com.scichart.scishowcase.viewModels.ChartViewModel
import com.scichart.drawing.utility.ColorUtil.*
import com.scichart.scishowcase.utils.dip

class AudioStreamViewModel(context: Context, audioStreamBufferSize: Int) : ChartViewModel(context) {
    private val audioDS = XyDataSeries<Long, Short>().init { fifoCapacity = audioStreamBufferSize }

    init {
        xAxes.add(NumericAxis(context).init {
            autoRange = AutoRange.Always
            drawLabels = false
            drawMinorTicks = false
            drawMajorTicks = false
            drawMajorBands = false
            drawMinorGridLines = false
            drawMajorGridLines = false
        })

        yAxes.add(NumericAxis(context).init {
            visibleRange = DoubleRange(Short.MIN_VALUE.toDouble(), Short.MAX_VALUE.toDouble())
            drawLabels = false
            drawMinorTicks = false
            drawMajorTicks = false
            drawMajorBands = false
            drawMinorGridLines = false
            drawMajorGridLines = false
        })

        val lineThickness = context.dip(1f)

        renderableSeries.add(FastLineRenderableSeries().init {
            dataSeries = audioDS
            strokeStyle = SolidPenStyle(Grey, true, lineThickness, null)
        })
    }

    fun onNextAudioData(audioData: AudioData) {
        audioDS.append(audioData.xData, audioData.yData)
    }
}