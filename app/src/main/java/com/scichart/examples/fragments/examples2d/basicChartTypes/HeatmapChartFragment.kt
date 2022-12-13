//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2021. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// HeatmapChartFragment.kt is part of SCICHART®, High Performance Scientific Charts
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
import com.scichart.charting.visuals.renderableSeries.ColorMap
import com.scichart.core.model.DoubleValues
import com.scichart.core.model.IValues
import com.scichart.drawing.utility.ColorUtil.*
import com.scichart.examples.databinding.ExampleHeatmapChartFragmentBinding
import com.scichart.examples.fragments.base.ExampleBaseFragment
import com.scichart.examples.utils.scichartExtensions.*
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit

class HeatmapChartFragment : ExampleBaseFragment<ExampleHeatmapChartFragmentBinding>() {

    private val scheduledExecutorService = Executors.newSingleThreadScheduledExecutor()
    private lateinit var schedule: ScheduledFuture<*>

    private val dataSeries = UniformHeatmapDataSeries<Int, Int, Double>(WIDTH, HEIGHT)

    private var timerIndex = 0
    private val valuesList: MutableList<IValues<Double>> = ArrayList(SERIES_PER_PERIOD)

    override fun inflateBinding(inflater: LayoutInflater): ExampleHeatmapChartFragmentBinding {
        return ExampleHeatmapChartFragmentBinding.inflate(inflater)
    }

    override fun initExample(binding: ExampleHeatmapChartFragmentBinding) {
        for (i in 0 until SERIES_PER_PERIOD) {
            valuesList.add(createValues(i))
        }

        binding.surface.suspendUpdates {
            xAxes { numericAxis() }
            yAxes { numericAxis() }
            renderableSeries {
                fastUniformHeatmapRenderableSeries {
                    minimum = 0.0
                    maximum = 200.0
                    colorMap = ColorMap(
                        // Colours in ARGB format
                        intArrayOf(0xFF14233C.toInt(),
                            0xFF264B93.toInt(),
                            0xFF50C7E0.toInt(),
                            0xFF67BDAF.toInt(),
                            0xFFDC7969.toInt(),
                            0xFFF48420.toInt(),
                            0xFFEC0F6C.toInt()
                        ),
                        floatArrayOf(0f, 0.2f, 0.3f, 0.5f, 0.7f, 0.9f, 1f)
                    )
                    dataSeries = this@HeatmapChartFragment.dataSeries

                    binding.heatmapColourMap.run {
                        minimum = this@fastUniformHeatmapRenderableSeries.minimum
                        maximum = this@fastUniformHeatmapRenderableSeries.maximum
                        colorMap = this@fastUniformHeatmapRenderableSeries.colorMap
                    }
                }
            }
            chartModifiers {
                defaultModifiers()
                cursorModifier {
                    showTooltip = true
                    receiveHandledEvents = true
                }
            }
        }

        schedule = scheduledExecutorService.scheduleWithFixedDelay({
            binding.surface.suspendUpdates {
                val values = valuesList[timerIndex % SERIES_PER_PERIOD]
                dataSeries.updateZValues(values)

                timerIndex++
            }
        }, 0, TIME_INTERVAL, TimeUnit.MILLISECONDS)
    }

    private fun createValues(index: Int): IValues<Double> {
        val values = DoubleValues(WIDTH * HEIGHT)

        val random = Random()
        val angle = Math.PI * 2 * index / SERIES_PER_PERIOD
        val cx = 150.0; val cy = 100.0
        val cpMax = 200.0
        // When appending data to DoubleValues for the heatmap, always go Y then X
        for (y in 0 until HEIGHT) {
            for (x in 0 until WIDTH) {
                val v =
                    (1 + Math.sin(x * 0.04 + angle)) * 50 + (1 + Math.sin(y * 0.1 + angle)) * 50 * (1 + Math.sin(
                        angle * 2
                    ))
                val r = Math.sqrt((x - cx) * (x - cx) + (y - cy) * (y - cy))
                val exp = Math.max(0.0, 1 - r * 0.008)
                val zValue = v * exp + Math.random() * 10
                values.add(if (zValue > cpMax) cpMax else zValue)
            }
        }

        return values
    }

    override fun onDestroyView() {
        super.onDestroyView()

        schedule?.cancel(true)
    }

    companion object {
        private const val WIDTH = 300
        private const val HEIGHT = 200
        private const val SERIES_PER_PERIOD = 30
        private const val TIME_INTERVAL: Long = 40
    }
}