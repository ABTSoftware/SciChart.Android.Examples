//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2021. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// ColumnChartFragment.kt is part of SCICHART®, High Performance Scientific Charts
// For full terms and conditions of the license, see http://www.scichart.com/scichart-eula/
//
// This source code is protected by international copyright law. Unauthorized
// reproduction, reverse-engineering, or distribution of all or any portion of
// this source code is strictly prohibited.
//
// This source code contains confidential and proprietary trade secrets of
// SciChart Ltd., and should at no time be copied, transferred, sold,
// distributed or made available without express written permission.
//******************************************************************************

package com.scichart.examples.fragments.examples2d.basicChartTypes.kt

import android.view.animation.DecelerateInterpolator
import com.scichart.charting.visuals.SciChartSurface
import com.scichart.charting.visuals.renderableSeries.FastColumnRenderableSeries
import com.scichart.charting.visuals.renderableSeries.data.XSeriesRenderPassData
import com.scichart.charting.visuals.renderableSeries.paletteProviders.IFillPaletteProvider
import com.scichart.charting.visuals.renderableSeries.paletteProviders.PaletteProviderBase
import com.scichart.core.model.IntegerValues
import com.scichart.data.model.DoubleRange
import com.scichart.drawing.utility.ColorUtil
import com.scichart.examples.fragments.base.ExampleSingleChartBaseFragment
import com.scichart.examples.utils.scichartExtensions.*

class ColumnChartFragment : ExampleSingleChartBaseFragment() {

    override fun initExample(surface: SciChartSurface) {
        surface.suspendUpdates {
            xAxes { numericAxis { growBy = DoubleRange(0.1, 0.1) } }
            yAxes { numericAxis { growBy = DoubleRange(0.1, 0.1) } }
            renderableSeries {
                fastColumnRenderableSeries {
                    xyDataSeries<Int, Int> {
                        val yValues = intArrayOf(50, 35, 61, 58, 50, 50, 40, 53, 55, 23, 45, 12, 59, 60)
                        for (i in yValues.indices) {
                            append(i, yValues[i])
                        }
                    }
                    strokeStyle = SolidPenStyle(0xFF232323, 0.4f)
                    fillBrushStyle = LinearGradientBrushStyle(ColorUtil.LightSteelBlue, ColorUtil.SteelBlue)
                    dataPointWidth = 0.7
                    paletteProvider = ColumnsPaletteProvider()

                    waveAnimation { interpolator = DecelerateInterpolator() }
                }
            }
            chartModifiers { defaultModifiers() }
        }
    }

    private class ColumnsPaletteProvider : PaletteProviderBase<FastColumnRenderableSeries>(FastColumnRenderableSeries::class.java), IFillPaletteProvider {
        private val colors = IntegerValues()
        private val desiredColors = longArrayOf(0xFFa9d34f, 0xFFfc9930, 0xFFd63b3f)

        override fun update() {
            val currentRenderPassData = renderableSeries!!.currentRenderPassData as XSeriesRenderPassData

            val size = currentRenderPassData.pointsCount()
            colors.setSize(size)

            val colorsArray = colors.itemsArray
            val indices = currentRenderPassData.indices.itemsArray
            for (i in 0 until size) {
                val index = indices[i]
                colorsArray[i] = desiredColors[index % 3].toInt()
            }
        }

        override fun getFillColors(): IntegerValues = colors
    }
}