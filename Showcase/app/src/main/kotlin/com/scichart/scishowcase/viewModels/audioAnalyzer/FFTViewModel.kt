//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2017. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// FFTViewModel.kt is part of the SCICHART® Showcases. Permission is hereby granted
// to modify, create derivative works, distribute and publish any part of this source
// code whether for commercial, private or personal use.
//
// The SCICHART® examples are distributed in the hope that they will be useful, but
// without any warranty. It is provided "AS IS" without warranty of any kind, either
// expressed or implied.
//******************************************************************************

package com.scichart.scishowcase.viewModels.audioAnalyzer

import android.content.Context
import com.scichart.charting.visuals.axes.AxisAlignment
import com.scichart.charting.visuals.axes.AxisTitleOrientation
import com.scichart.charting.visuals.axes.AxisTitlePlacement
import com.scichart.charting.visuals.axes.NumericAxis
import com.scichart.charting.visuals.renderableSeries.FastColumnRenderableSeries
import com.scichart.charting.visuals.renderableSeries.data.XyRenderPassData
import com.scichart.charting.visuals.renderableSeries.paletteProviders.IFillPaletteProvider
import com.scichart.charting.visuals.renderableSeries.paletteProviders.IStrokePaletteProvider
import com.scichart.charting.visuals.renderableSeries.paletteProviders.PaletteProviderBase
import com.scichart.core.model.DoubleValues
import com.scichart.core.model.IntegerValues
import com.scichart.core.utility.NumberUtil
import com.scichart.data.model.DoubleRange
import com.scichart.scishowcase.utils.XyDataSeries
import com.scichart.scishowcase.viewModels.ChartViewModel
import com.scichart.drawing.utility.ColorUtil.*

class FFTViewModel(context: Context, fftSize: Int, hzPerDataPoint: Double) : ChartViewModel(context) {
    private val fftDS = XyDataSeries<Double, Double>().apply { fifoCapacity = fftSize }

    init {
        xAxes.add(NumericAxis(context).apply {
            drawMajorBands = false
            maxAutoTicks = 5
            axisTitle = "Hz"
            axisTitlePlacement = AxisTitlePlacement.Right
            axisTitleOrientation = AxisTitleOrientation.Horizontal
        })

        yAxes.add(NumericAxis(context).apply {
            axisAlignment = AxisAlignment.Left
            visibleRange = DoubleRange(-30.0, 70.0)
            growBy = DoubleRange(0.1, 0.1)
            drawMinorTicks = false
            drawMajorBands = false
            drawMinorGridLines = false
            axisTitle = "dB"
            axisTitlePlacement = AxisTitlePlacement.Top
            axisTitleOrientation = AxisTitleOrientation.Horizontal
        })

        renderableSeries.add(FastColumnRenderableSeries().apply {
            dataSeries = fftDS
            paletteProvider = FFTPaletteProvider()
            zeroLineY = -30.0 // set zero line equal to VisibleRange.Min
        })

        for (index: Int in 0 until fftSize) {
            fftDS.append(index * hzPerDataPoint, 0.0)
        }
    }

    fun onNextFFT(fftData: DoubleValues) {
        fftDS.updateRangeYAt(0, fftData)
    }

    private class FFTPaletteProvider : PaletteProviderBase<FastColumnRenderableSeries>(FastColumnRenderableSeries::class.java), IFillPaletteProvider, IStrokePaletteProvider {
        private val colors = IntegerValues()

        private val minColor = Green
        private val maxColor = Red

        private val minColorRed = red(minColor)
        private val minColorGreen = green(minColor)
        private val minColorBlue = blue(minColor)

        private val maxColorRed = red(maxColor)
        private val maxColorGreen = green(maxColor)
        private val maxColorBlue = blue(maxColor)

        private val diffRed = maxColorRed - minColorRed
        private val diffGreen = maxColorGreen - minColorGreen
        private val diffBlue = maxColorBlue - minColorBlue

        override fun update() {
            val currentRenderPassData = renderableSeries.currentRenderPassData
            if (currentRenderPassData is XyRenderPassData) {
                val yCoordinateCalculator = currentRenderPassData.yCoordinateCalculator
                val min = yCoordinateCalculator.minAsDouble
                val max = yCoordinateCalculator.maxAsDouble
                val diff = max - min

                val yValues = currentRenderPassData.yValues

                val size = yValues.size()
                colors.setSize(size)

                val yValuesItems = yValues.itemsArray
                val colorItems = colors.itemsArray

                for (index in 0..size - 1) {
                    val yValue = yValuesItems[index]
                    val fraction = (yValue - min) / diff

                    val red = lerp(minColorRed, diffRed, fraction)
                    val green = lerp(minColorGreen, diffGreen, fraction)
                    val blue = lerp(minColorBlue, diffBlue, fraction)

                    colorItems[index] = rgb(red, green, blue)
                }

            }
        }

        private fun lerp(minColor: Int, diffColor: Int, fraction: Double) : Int {
            val interpolatedValue = minColor + fraction * diffColor
            return NumberUtil.constrain(interpolatedValue, 0.0, 255.0).toInt()
        }

        override fun getFillColors(): IntegerValues = colors

        override fun getStrokeColors(): IntegerValues = colors
    }
}