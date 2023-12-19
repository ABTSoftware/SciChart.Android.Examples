//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2021. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// ECGMonitorFragment.kt is part of SCICHART®, High Performance Scientific Charts
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

package com.scichart.examples.fragments.featuredApps.medicalCharts.kt

import android.os.Bundle
import com.scichart.charting.visuals.SciChartSurface
import com.scichart.charting.visuals.axes.AutoRange.Never
import com.scichart.data.model.DoubleRange
import com.scichart.data.model.ISciList
import com.scichart.examples.R
import com.scichart.examples.data.DataManager
import com.scichart.examples.fragments.base.ExampleSingleChartBaseFragment
import com.scichart.examples.utils.scichartExtensions.*
import com.scichart.examples.utils.widgetgeneration.ImageViewWidget
import com.scichart.examples.utils.widgetgeneration.Widget
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit

class ECGMonitorFragment : ExampleSingleChartBaseFragment() {
    private val series0 = XyDataSeries<Double, Double>().apply { fifoCapacity = FIFO_CAPACITY; acceptsUnsortedData = true }
    private val series1 = XyDataSeries<Double, Double>().apply { fifoCapacity = FIFO_CAPACITY; acceptsUnsortedData = true }

    private lateinit var sourceData: DoubleArray

    private var _currentIndex = 0
    private var _totalIndex = 0
    private var whichTrace = TraceAOrB.TraceA

    private val scheduledExecutorService = Executors.newSingleThreadScheduledExecutor()
    private lateinit var schedule: ScheduledFuture<*>

    @Volatile
    private var isRunning = true

    override fun showDefaultModifiersInToolbar(): Boolean = false

    override fun getToolbarItems(): List<Widget> = ArrayList<Widget>().apply {
        add(ImageViewWidget.Builder().setId(R.drawable.example_toolbar_play).setListener { isRunning = true }.build())
        add(ImageViewWidget.Builder().setId(R.drawable.example_toolbar_pause).setListener { isRunning = false }.build())
    }

    override fun initExample(surface: SciChartSurface) {
        surface.theme = R.style.SciChart_NavyBlue

        surface.suspendUpdates {
            xAxes { numericAxis {
                visibleRange = DoubleRange(0.0, 10.0)
                autoRange = Never
                axisTitle = "Time (seconds)"
            }}
            yAxes { numericAxis {
                visibleRange = DoubleRange(-0.5, 1.5)
                axisTitle = "Voltage (mV)"
            }}

            renderableSeries {
                fastLineRenderableSeries { dataSeries = series0 }
                fastLineRenderableSeries { dataSeries = series1 }
            }
        }

        schedule = scheduledExecutorService.scheduleWithFixedDelay(appendDataRunnable, 0, TIME_INTERVAL, TimeUnit.MILLISECONDS)
    }

    private val appendDataRunnable = Runnable {
        if (!isRunning) return@Runnable

        binding.surface.suspendUpdates {
            for (i in 0 until 10) {
                appendPoint()
            }
        }
    }

    @Synchronized
    private fun appendPoint() {
        if (_currentIndex >= sourceData.size) {
            _currentIndex = 0
        }

        // Get the next voltage and time, and append to the chart
        val voltage = sourceData[_currentIndex]
        val time = _totalIndex / SAMPLE_RATE % 10

        if (whichTrace == TraceAOrB.TraceA) {
            series0.append(time, voltage)
            series1.append(time, Double.NaN)
        } else {
            series0.append(time, Double.NaN)
            series1.append(time, voltage)
        }
        _currentIndex++
        _totalIndex++

        if (_totalIndex % 4000 == 0) {
            whichTrace = if (whichTrace == TraceAOrB.TraceA) TraceAOrB.TraceB else TraceAOrB.TraceA
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sourceData = DataManager.getInstance().loadWaveformData(requireContext())

        savedInstanceState?.run {
            _currentIndex = getInt("currentIndex")
            _totalIndex = getInt("totalIndex")
            val xValues0: ISciList<Double> = getParcelable("xValues0")!!
            val yValues0: ISciList<Double> = getParcelable("yValues0")!!
            val xValues1: ISciList<Double> = getParcelable("xValues1")!!
            val yValues1: ISciList<Double> = getParcelable("yValues1")!!
            series0.append(xValues0, yValues0)
            series1.append(xValues1, yValues1)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        isRunning = false

        outState.run {
            putInt("currentIndex", _currentIndex)
            putInt("totalIndex", _totalIndex)
            putParcelable("xValues0", series0.xValues)
            putParcelable("yValues0", series0.yValues)
            putParcelable("xValues1", series1.xValues)
            putParcelable("yValues1", series1.yValues)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        schedule.cancel(true)
    }

    companion object {
        private const val TIME_INTERVAL: Long = 20
        private const val FIFO_CAPACITY = 3850
        private const val SAMPLE_RATE = 400.0
    }

    internal enum class TraceAOrB {
        TraceA, TraceB
    }
}