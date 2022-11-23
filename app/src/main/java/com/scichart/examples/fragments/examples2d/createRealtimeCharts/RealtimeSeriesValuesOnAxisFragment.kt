//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2021. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// RealtimeSeriesValuesOnAxisFragment.kt is part of SCICHART®, High Performance Scientific Charts
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
import android.view.Gravity
import com.scichart.charting.visuals.SciChartSurface
import com.scichart.charting.visuals.axes.AutoRange
import com.scichart.data.model.DoubleRange
import com.scichart.data.model.ISciList
import com.scichart.examples.fragments.base.ExampleSingleChartBaseFragment
import com.scichart.examples.utils.scichartExtensions.*
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit
import kotlin.math.cos
import kotlin.math.sin

class RealtimeSeriesValuesOnAxisFragment : ExampleSingleChartBaseFragment() {
    private val ds1 = XyDataSeries<Double, Double>().apply { seriesName = "Orange Series"; fifoCapacity = FIFO_CAPACITY }
    private val ds2 = XyDataSeries<Double, Double>().apply { seriesName = "Blue Series"; fifoCapacity = FIFO_CAPACITY }
    private val ds3 = XyDataSeries<Double, Double>().apply { seriesName = "Green Series"; fifoCapacity = FIFO_CAPACITY }

    private val scheduledExecutorService = Executors.newSingleThreadScheduledExecutor()
    private lateinit var schedule: ScheduledFuture<*>

    override fun initExample(surface: SciChartSurface) {
        surface.suspendUpdates {
            xAxes { numericAxis  {
                autoRange = AutoRange.Always
                axisTitle = "Time (Seconds)"
                textFormatting = "0.0"
            }}
            yAxes { numericAxis  {
                autoRange = AutoRange.Always
                axisTitle = "Amplitude (Volts)"
                textFormatting = "0.0"
                cursorTextFormatting = "0.00"
                growBy = DoubleRange(0.1, 0.1)
            }}
            renderableSeries {
                fastLineRenderableSeries { dataSeries = ds1; strokeStyle = SolidPenStyle(0xFFe97064, 2f) }
                fastLineRenderableSeries { dataSeries = ds2; strokeStyle = SolidPenStyle(0xFF47bde6, 2f) }
                fastLineRenderableSeries { dataSeries = ds3; strokeStyle = SolidPenStyle(0xFF68bcae, 2f) }
            }
            chartModifiers {
                seriesValueModifier()
                legendModifier { setLegendPosition(Gravity.TOP or Gravity.START, 16) }
            }
        }

        schedule = scheduledExecutorService.scheduleWithFixedDelay(insertRunnable, 0, TIME_INTERVAL, TimeUnit.MILLISECONDS)
    }

    var t = 0.0
    private val insertRunnable = Runnable {
        binding.surface.suspendUpdates {
            val y1 = 3.0 * sin(2 * Math.PI * 1.4 * t * 0.02)
            val y2 = 2.0 * cos(2 * Math.PI * 0.8 * t * 0.02)
            val y3 = sin(2 * Math.PI * 2.2 * t * 0.02)

            ds1.append(t, y1)
            ds2.append(t, y2)
            ds3.append(t, y3)

            t += TIME_INTERVAL / 1000.0
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

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

    companion object {
        private const val FIFO_CAPACITY = 100
        private const val TIME_INTERVAL: Long = 50
    }
}
