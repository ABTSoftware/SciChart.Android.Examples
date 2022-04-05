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
import kotlin.math.max
import kotlin.math.sin
import kotlin.math.sqrt

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
                        intArrayOf(DarkBlue, CornflowerBlue, DarkGreen, Chartreuse, Yellow, Red),
                        floatArrayOf(0f, 0.2f, 0.4f, 0.6f, 0.8f, 1f)
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
        for (y in 0 until HEIGHT) {
            for (x in 0 until WIDTH) {
                val v = (1 + sin(y * 0.04 + angle)) * 50 + (1 + sin(x * 0.1 + angle)) * 50 * (1 + sin(angle * 2))
                val r = sqrt((y - cx) * (y - cx) + (x - cy) * (x - cy))
                val exp = max(0.0, 1 - r * 0.008)

                values.add(v * exp + random.nextDouble() * 50)
            }
        }

        return values
    }

    override fun onDestroyView() {
        super.onDestroyView()

        schedule?.cancel(true)
    }

    companion object {
        private const val WIDTH = 200
        private const val HEIGHT = 300
        private const val SERIES_PER_PERIOD = 30
        private const val TIME_INTERVAL: Long = 40
    }
}