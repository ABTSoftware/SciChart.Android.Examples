//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2021. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// RealTimeUniformMeshChart3DFragment.kt is part of SCICHART®, High Performance Scientific Charts
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

package com.scichart.examples.fragments.examples3d.realtime3DCharts.kt

import com.scichart.charting.visuals.axes.AutoRange.Always
import com.scichart.charting3d.model.dataSeries.IndexCalculator
import com.scichart.charting3d.visuals.SciChartSurface3D
import com.scichart.charting3d.visuals.renderableSeries.data.GradientColorPalette
import com.scichart.core.model.DoubleValues
import com.scichart.data.model.DoubleRange
import com.scichart.drawing.utility.ColorUtil.*
import com.scichart.examples.fragments.base.ExampleSingleChart3DBaseFragment
import com.scichart.examples.utils.scichartExtensions.*
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit
import kotlin.math.sin
import kotlin.math.sqrt

class RealTimeUniformMeshChart3DFragment : ExampleSingleChart3DBaseFragment() {
    private val scheduledExecutorService = Executors.newSingleThreadScheduledExecutor()
    private lateinit var schedule: ScheduledFuture<*>

    val dataSeries = UniformGridDataSeries3D<Double, Double, Double>(WIDTH, HEIGHT)

    override fun initExample(surface3d: SciChartSurface3D) {
        surface3d.suspendUpdates {
            xAxis = numericAxis3D { autoRange = Always }
            yAxis = numericAxis3D { visibleRange = DoubleRange(0.0, 1.0) }
            zAxis = numericAxis3D { autoRange = Always }

            renderableSeries {
                surfaceMeshRenderableSeries3D {
                    dataSeries = this@RealTimeUniformMeshChart3DFragment.dataSeries
                    stroke = 0x7FFFFFFF
                    strokeThickness = 1f
                    drawSkirt = false
                    minimum = 0.0
                    maximum = 0.5
                    shininess = 64f
                    meshColorPalette = GradientColorPalette(
                        intArrayOf(0xFF1D2C6B.toInt(), Blue, Cyan, GreenYellow, Yellow, Red, DarkRed),
                        floatArrayOf(0f, .1f, .3f, .5f, .7f, .9f, 1f)
                    )
                }
            }

            chartModifiers { defaultModifiers3D() }
        }

        schedule = scheduledExecutorService.scheduleWithFixedDelay(scheduledRunnable, 0, 33, TimeUnit.MILLISECONDS)
    }

    private val scheduledRunnable: Runnable = object : Runnable {
        private val buffer = DoubleValues()
        private var frames = 0

        override fun run() {
            binding.surface3d.suspendUpdates {
                val wc = WIDTH * 0.5
                val hc = HEIGHT * 0.5
                val freq = sin(frames++ * 0.1) * 0.1 + 0.1

                val indexCalculator: IndexCalculator = dataSeries.indexCalculator
                buffer.setSize(indexCalculator.size)

                val items = buffer.itemsArray
                for (i in 0 until HEIGHT) {
                    for (j in 0 until WIDTH) {
                        val radius = sqrt((wc - i) * (wc - i) + (hc - j) * (hc - j))
                        val d = Math.PI * radius * freq
                        val value = sin(d) / d

                        val index = indexCalculator.getIndex(i, j)
                        items[index] = if (value.isNaN()) 1.toDouble() else value
                    }
                }

                dataSeries.copyFrom(buffer)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        schedule.cancel(true)
    }

    companion object {
        private const val WIDTH = 50
        private const val HEIGHT = 50
    }
}