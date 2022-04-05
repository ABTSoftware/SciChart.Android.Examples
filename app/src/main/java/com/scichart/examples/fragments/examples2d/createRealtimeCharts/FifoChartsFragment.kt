//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2021. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// FifoChartsFragment.kt is part of SCICHART®, High Performance Scientific Charts
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

import android.os.Bundle
import com.scichart.charting.visuals.SciChartSurface
import com.scichart.charting.visuals.axes.AutoRange
import com.scichart.data.model.DoubleRange
import com.scichart.data.model.ISciList
import com.scichart.examples.R
import com.scichart.examples.fragments.base.ExampleSingleChartBaseFragment
import com.scichart.examples.utils.scichartExtensions.*
import com.scichart.examples.utils.widgetgeneration.ImageViewWidget
import com.scichart.examples.utils.widgetgeneration.Widget
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit
import kotlin.math.cos
import kotlin.math.sin

class FifoChartsFragment : ExampleSingleChartBaseFragment() {
    private val random = Random()

    private val ds1 = XyDataSeries<Double, Double>().apply { fifoCapacity = FIFO_CAPACITY }
    private val ds2 = XyDataSeries<Double, Double>().apply { fifoCapacity = FIFO_CAPACITY }
    private val ds3 = XyDataSeries<Double, Double>().apply { fifoCapacity = FIFO_CAPACITY }

    private val xVisibleRange = DoubleRange(GROW_BY, VISIBLE_RANGE_MAX + GROW_BY)

    private val scheduledExecutorService = Executors.newSingleThreadScheduledExecutor()
    private lateinit var schedule: ScheduledFuture<*>

    @Volatile
    private var isRunning = true

    override fun getToolbarItems(): List<Widget> = ArrayList<Widget>().apply {
        add(ImageViewWidget.Builder().setId(R.drawable.example_toolbar_play).setListener { isRunning = true }.build())
        add(ImageViewWidget.Builder().setId(R.drawable.example_toolbar_pause).setListener { isRunning = false }.build())
        add(ImageViewWidget.Builder().setId(R.drawable.example_toolbar_stop).setListener {
            isRunning = false
            resetChart()
        }.build())
    }

    override fun initExample(surface: SciChartSurface) {
        surface.suspendUpdates {
            xAxes { numericAxis {
                visibleRange = xVisibleRange
                autoRange = AutoRange.Never }
            }
            yAxes { numericAxis {
                growBy = DoubleRange(0.1, 0.1)
                autoRange = AutoRange.Always }
            }
            renderableSeries {
                fastLineRenderableSeries { dataSeries = ds1; strokeStyle = SolidPenStyle(0xFF4083B7, 2f) }
                fastLineRenderableSeries { dataSeries = ds2; strokeStyle = SolidPenStyle(0xFFFFA500, 2f) }
                fastLineRenderableSeries { dataSeries = ds3; strokeStyle = SolidPenStyle(0xFFE13219, 2f) }
            }
        }

        schedule = scheduledExecutorService.scheduleWithFixedDelay(insertRunnable, 0, TIME_INTERVAL, TimeUnit.MILLISECONDS)
    }

    var t = 0.0
    private val insertRunnable = Runnable {
        if (!isRunning) return@Runnable

        binding.surface.suspendUpdates {
            val y1 = 3.0 * sin(2 * Math.PI * 1.4 * t) + random.nextDouble() * 0.5
            val y2 = 2.0 * cos(2 * Math.PI * 0.8 * t) + random.nextDouble() * 0.5
            val y3 = sin(2 * Math.PI * 2.2 * t) + random.nextDouble() * 0.5

            ds1.append(t, y1)
            ds2.append(t, y2)
            ds3.append(t, y3)

            t += ONE_OVER_TIME_INTERVAL
            if (t > VISIBLE_RANGE_MAX) {
                xVisibleRange.setMinMax(xVisibleRange.min + ONE_OVER_TIME_INTERVAL, xVisibleRange.max + ONE_OVER_TIME_INTERVAL)
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        isRunning = false

        outState.run {
            putDouble("time", t)
            putParcelable("xValues1", ds1.xValues)
            putParcelable("yValues1", ds1.yValues)
            putParcelable("xValues2", ds2.xValues)
            putParcelable("yValues2", ds2.yValues)
            putParcelable("xValues3", ds3.xValues)
            putParcelable("yValues3", ds3.yValues)
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        savedInstanceState?.run {
            t = getDouble("time")
            val xValues1: ISciList<Double> = getParcelable("xValues1")!!
            val yValues1: ISciList<Double> = getParcelable("yValues1")!!
            val xValues2: ISciList<Double> = getParcelable("xValues2")!!
            val yValues2: ISciList<Double> = getParcelable("yValues2")!!
            val xValues3: ISciList<Double> = getParcelable("xValues3")!!
            val yValues3: ISciList<Double> = getParcelable("yValues3")!!
            ds1.append(xValues1, yValues1)
            ds2.append(xValues2, yValues2)
            ds3.append(xValues3, yValues3)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        schedule.cancel(true)
    }
    private fun resetChart() {
        binding.surface.suspendUpdates {
            ds1.clear()
            ds2.clear()
            ds3.clear()
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
