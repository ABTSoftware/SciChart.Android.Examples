//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2021. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// HeatmapWithTextFragment.kt is part of SCICHART®, High Performance Scientific Charts
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

import android.view.LayoutInflater
import com.scichart.charting.model.dataSeries.IDataSeries
import com.scichart.data.model.DoubleRange
import com.scichart.drawing.common.FontStyle
import com.scichart.drawing.utility.ColorUtil
import com.scichart.examples.databinding.ExampleHeatmapWithTextFragmentBinding
import com.scichart.examples.fragments.base.ExampleBaseFragment
import com.scichart.examples.utils.scichartExtensions.*
import java.text.DecimalFormat
import java.util.*
import kotlin.math.pow

class HeatmapWithTextFragment : ExampleBaseFragment<ExampleHeatmapWithTextFragmentBinding>() {

    override fun inflateBinding(inflater: LayoutInflater): ExampleHeatmapWithTextFragmentBinding {
        return ExampleHeatmapWithTextFragmentBinding.inflate(inflater)
    }

    override fun initExample(binding: ExampleHeatmapWithTextFragmentBinding) {
        binding.surface.suspendUpdates {
            xAxes { numericAxis { growBy = DoubleRange(0.1, 0.1); flipCoordinates = true }}
            yAxes { numericAxis { growBy = DoubleRange(0.1, 0.1); flipCoordinates = true }}
            renderableSeries {
                fastUniformHeatmapRenderableSeries {
                    minimum = 0.0
                    maximum = 100.0
                    cellTextStyle = FontStyle(8f.toDip(), ColorUtil.White)
                    drawTextInCell = true
                    dataSeries = createDataSeries()
                    binding.heatmapColourMap.run {
                        minimum = this@fastUniformHeatmapRenderableSeries.minimum
                        maximum = this@fastUniformHeatmapRenderableSeries.maximum
                        colorMap = this@fastUniformHeatmapRenderableSeries.colorMap
                        textFormat = DecimalFormat("0.##")
                    }
                }
            }
            chartModifiers { defaultModifiers() }
        }
    }

    private fun createDataSeries(): IDataSeries<*, *> {
        val w = 12
        val h = 7
        val dataSeries = UniformHeatmapDataSeries<Int, Int, Double>(w, h)

        val random = Random()
        for (x in 0 until w) {
            for (y in 0 until h) {
                dataSeries.updateZAt(x, y, random.nextDouble().pow(0.15) * x / (w - 1) * y / (h - 1) * 100)
            }
        }
        return dataSeries
    }
}