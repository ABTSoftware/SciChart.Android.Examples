//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2021. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// RealTimeMinMaxAnnotationFragment.kt is part of SCICHART®, High Performance Scientific Charts
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

package com.scichart.examples.fragments.examples2d.createRealtimeCharts.kt

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import com.scichart.charting.visuals.SciChartSurface
import com.scichart.charting.visuals.annotations.HorizontalAnchorPoint
import com.scichart.charting.visuals.annotations.TextAnnotation
import com.scichart.charting.visuals.annotations.VerticalAnchorPoint
import com.scichart.charting.visuals.axes.AutoRange
import com.scichart.data.model.DoubleRange
import com.scichart.data.model.ISciList
import com.scichart.drawing.common.FontStyle
import com.scichart.examples.fragments.base.ExampleSingleChartBaseFragment

import com.scichart.examples.utils.scichartExtensions.SolidPenStyle
import com.scichart.examples.utils.scichartExtensions.XyDataSeries
import com.scichart.examples.utils.scichartExtensions.fastLineRenderableSeries
import com.scichart.examples.utils.scichartExtensions.numericAxis
import com.scichart.examples.utils.scichartExtensions.renderableSeries
import com.scichart.examples.utils.scichartExtensions.suspendUpdates
import com.scichart.examples.utils.scichartExtensions.xAxes
import com.scichart.examples.utils.scichartExtensions.yAxes
import java.util.Random
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit
import kotlin.math.cos
import kotlin.math.sin

class RealTimeMinMaxAnnotationFragment : ExampleSingleChartBaseFragment() {

    private val random = Random()

    private val ds1 = XyDataSeries<Double, Double>().apply { fifoCapacity =
        FIFO_CAPACITY
    }

    private val xVisibleRange = DoubleRange(GROW_BY, VISIBLE_RANGE_MAX + GROW_BY)
    private val yVisibleRange = DoubleRange(0.0, 1.0)

    private val scheduledExecutorService = Executors.newSingleThreadScheduledExecutor()
    private lateinit var schedule: ScheduledFuture<*>
    private lateinit var minAnnotation: TextAnnotation
    private lateinit var maxAnnotation: TextAnnotation
    @Volatile
    private var isRunning = true

    override fun initExample(surface: SciChartSurface) {
        surface.suspendUpdates {
            xAxes { numericAxis {
                visibleRange = xVisibleRange
                autoRange = AutoRange.Never }
            }
            yAxes { numericAxis {
                //growBy = DoubleRange(0.1, 0.1)
                visibleRange = yVisibleRange
                autoRange = AutoRange.Never
            }
            }
            renderableSeries {
                fastLineRenderableSeries { dataSeries = ds1; strokeStyle = SolidPenStyle(0xFF634e96, 2f) }

            }
        }
        minAnnotation = sciChartBuilder.newTextAnnotation()
            .withText("Min: 0.0")
            .withX1(5.0)
            .withY1(5.0)
            .withHorizontalAnchorPoint(HorizontalAnchorPoint.Center)
            .withVerticalAnchorPoint(VerticalAnchorPoint.Bottom)
            .withFontStyle(FontStyle(40F, Color.RED))
            .build()

        maxAnnotation = sciChartBuilder.newTextAnnotation()
            .withText("Max: 0.0")
            .withX1(0.0)
            .withY1(0.0)
            .withHorizontalAnchorPoint(HorizontalAnchorPoint.Center)
            .withVerticalAnchorPoint(VerticalAnchorPoint.Top)
            .withFontStyle(FontStyle(40f, Color.GREEN))
            .build()

        surface.annotations.add(minAnnotation)
        surface.annotations.add(maxAnnotation)
        schedule = scheduledExecutorService.scheduleWithFixedDelay(insertRunnable, 0, TIME_INTERVAL, TimeUnit.MILLISECONDS)
    }

    var t = 0.0
    private val insertRunnable = Runnable {
        if (!isRunning) return@Runnable

        binding.surface.suspendUpdates {
            val y1 = 3.0 * sin(2 * Math.PI * 1.4 * t) + random.nextDouble() * 0.5
            val y2 = 2.0 * cos(2 * Math.PI * 0.8 * t) + random.nextDouble() * 0.5
            val y3 = sin(2 * Math.PI * 2.2 * t) + random.nextDouble() * 0.5
            val y4 = sin(2 * Math.PI * 2.2 * t) + random.nextDouble() * 0.5
            ds1.append(t, y1+y2+y3+y4)


            t += ONE_OVER_TIME_INTERVAL
            if (t > VISIBLE_RANGE_MAX) {
                xVisibleRange.setMinMax(xVisibleRange.min + ONE_OVER_TIME_INTERVAL, xVisibleRange.max + ONE_OVER_TIME_INTERVAL)
            }
            // Update annotations with new min and max values
            val minY = ds1.yRange.minAsDouble
            val maxY = ds1.yRange.maxAsDouble
            val minIndex = ds1.yValues.indexOf(minY)
            val maxIndex = ds1.yValues.indexOf(maxY)
            val minX = ds1.xValues[minIndex]
            val maxX = ds1.xValues[maxIndex]
            val minAnnotationY = minY - (maxY - minY) * 0.02// Move min annotation higher by 5% of the range
            val maxAnnotationY = maxY + (maxY - minY) * 0.02
            // Log values to debug
            Log.d("SciChart", "Min: x=$minX, y=$minY, Max: x=$maxX, y=$maxY")

            minAnnotation.text = String.format("Min: %.2f", minY)
            minAnnotation.x1 = minX
            minAnnotation.y1 = minAnnotationY

            maxAnnotation.text = String.format("Max: %.2f", maxY)
            maxAnnotation.x1 = maxX
            maxAnnotation.y1 = maxAnnotationY

            // Calculate the adjusted range
            // range adjustment = data Range / (axis length - 2 * annotation height) * annotation height.
            val dr = (maxY - minY) / (binding.surface.yAxes[0].currentCoordinateCalculator.viewportDimension - 80) * 40
            yVisibleRange.setMinMax(minY - dr, maxY + dr)

        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        isRunning = false

        outState.run {
            putDouble("time", t)
            putParcelable("xValues1", ds1.xValues)
            putParcelable("yValues1", ds1.yValues)

        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        savedInstanceState?.run {
            t = getDouble("time")
            val xValues1: ISciList<Double> = getParcelable("xValues1")!!
            val yValues1: ISciList<Double> = getParcelable("yValues1")!!
            ds1.append(xValues1, yValues1)

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        schedule.cancel(true)
    }
    private fun resetChart() {
        binding.surface.suspendUpdates {
            ds1.clear()
        }
    }

    companion object {
        private const val FIFO_CAPACITY = 50
        private const val TIME_INTERVAL: Long = 30
        private const val ONE_OVER_TIME_INTERVAL = 1.0 / TIME_INTERVAL
        private const val VISIBLE_RANGE_MAX = FIFO_CAPACITY * ONE_OVER_TIME_INTERVAL
        private const val GROW_BY = VISIBLE_RANGE_MAX * 0.1
    }
}
