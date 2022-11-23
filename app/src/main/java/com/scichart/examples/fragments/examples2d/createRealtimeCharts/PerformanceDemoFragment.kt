//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2021. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// PerformanceDemoFragment.kt is part of SCICHART®, High Performance Scientific Charts
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
import android.os.Handler
import android.view.View
import android.widget.AdapterView
import android.widget.Spinner
import android.widget.TextView
import com.scichart.charting.model.dataSeries.IXyDataSeries
import com.scichart.charting.visuals.SciChartSurface
import com.scichart.charting.visuals.annotations.AnnotationCoordinateMode
import com.scichart.charting.visuals.axes.AutoRange
import com.scichart.charting.visuals.pointmarkers.CrossPointMarker
import com.scichart.charting.visuals.renderableSeries.FastLineRenderableSeries
import com.scichart.charting.visuals.renderableSeries.FastMountainRenderableSeries
import com.scichart.charting.visuals.renderableSeries.IRenderableSeries
import com.scichart.charting.visuals.renderableSeries.XyScatterRenderableSeries
import com.scichart.core.model.FloatValues
import com.scichart.core.model.IntegerValues
import com.scichart.core.utility.Dispatcher
import com.scichart.data.model.ISciList
import com.scichart.data.numerics.ResamplingMode
import com.scichart.examples.R
import com.scichart.examples.components.SpinnerStringAdapter
import com.scichart.examples.data.MovingAverage
import com.scichart.examples.fragments.base.ExampleSingleChartBaseFragment
import com.scichart.examples.utils.*
import com.scichart.examples.utils.scichartExtensions.*
import com.scichart.examples.utils.widgetgeneration.ImageViewWidget
import com.scichart.examples.utils.widgetgeneration.Widget
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit
import kotlin.math.roundToInt

class PerformanceDemoFragment : ExampleSingleChartBaseFragment() {
    private val pointCounts: List<Int> = ArrayList<Int>().apply {
        add(10)
        add(100)
        add(1000)
    }

    private var selectedSeriesType: String? = null
    private var selectedStrokeThickness = 1
    private var pointsCount = 100
    private var selectedResamplingMode = ResamplingMode.Auto

    private val random = Random()
    private var maLow = MovingAverage(MA_LOW)
    private var maHigh = MovingAverage(MA_HIGH)

    private val scheduledExecutorService = Executors.newSingleThreadScheduledExecutor()
    private lateinit var schedule: ScheduledFuture<*>

    @Volatile
    private var isRunning = true

    private var textView: TextView? = null

    override fun initExample(surface: SciChartSurface) {
        selectedSeriesType = resources.getStringArray(R.array.series_types)[0]

        initChart()

        // disable modifiers when updating in real-time
        updateModifiers(false)

        schedule = scheduledExecutorService.scheduleWithFixedDelay(appendDataRunnable, 0, TIME_INTERVAL, TimeUnit.MILLISECONDS)
    }

    private fun initChart() {
        textView = TextView(activity).apply { setPadding(20, 20, 20, 20) }

        binding.surface.suspendUpdates {
            xAxes { numericAxis  { autoRange = AutoRange.Always } }
            yAxes { numericAxis  { autoRange = AutoRange.Always } }
            renderableSeries {
                fastLineRenderableSeries { dataSeries = XyDataSeries<Int, Float>(); strokeStyle = SolidPenStyle(0xFFae418d) }
                fastLineRenderableSeries { dataSeries = XyDataSeries<Int, Float>(); strokeStyle = SolidPenStyle(0xFF68bcae) }
                fastLineRenderableSeries { dataSeries = XyDataSeries<Int, Float>(); strokeStyle = SolidPenStyle(0xFFe97064) }
            }
            chartModifiers { defaultModifiers() }
            annotations {
                customAnnotation {
                    x1 = 0; y1 = 0
                    coordinateMode = AnnotationCoordinateMode.Relative
                    setContentView(textView)
                    zIndex = -1
                }
            }
        }
    }

    private val appendDataRunnable: Runnable = object : Runnable {
        private val xValues = IntegerValues(pointsCount)
        private val firstYValues = FloatValues(pointsCount)
        private val secondYValues = FloatValues(pointsCount)
        private val thirdYValues = FloatValues(pointsCount)

        override fun run() {
            val maxPointsCount = calcMaxPointCountToDisplay()
            if (!isRunning || getPointsCount() > maxPointsCount) return

            xValues.clear()
            firstYValues.clear()
            secondYValues.clear()
            thirdYValues.clear()

            binding.surface.suspendUpdates {
                val mainSeries = renderableSeries[0].dataSeries as IXyDataSeries<Int, Float>
                val maLowSeries = renderableSeries[1].dataSeries as IXyDataSeries<Int, Float>
                val maHighSeries = renderableSeries[2].dataSeries as IXyDataSeries<Int, Float>

                var xValue = if (mainSeries.count > 0) mainSeries.xValues[mainSeries.count - 1] else 0
                var yValue: Float = if (mainSeries.count > 0) mainSeries.yValues[mainSeries.count - 1] else 10f
                for (i in 0 until pointsCount) {
                    xValue++
                    yValue += random.nextFloat() - 0.5f
                    xValues.add(xValue)
                    firstYValues.add(yValue)
                    secondYValues.add(maLow.push(yValue.toDouble()).current.toFloat())
                    thirdYValues.add(maHigh.push(yValue.toDouble()).current.toFloat())
                }

                mainSeries.append(xValues, firstYValues)
                maLowSeries.append(xValues, secondYValues)
                maHighSeries.append(xValues, thirdYValues)

                val count = (mainSeries.count + maLowSeries.count + maHighSeries.count).toLong()
                val text = "Amount of points: $count"
                Dispatcher.postOnUiThread { textView!!.text = text }
            }
        }
    }

    override fun getToolbarItems(): List<Widget> = ArrayList<Widget>().apply {
        add(ImageViewWidget.Builder().setId(R.drawable.example_toolbar_play).setListener { updateRunningState(true) }.build() )
        add(ImageViewWidget.Builder().setId(R.drawable.example_toolbar_pause).setListener { updateRunningState(false) }.build() )
        add(ImageViewWidget.Builder().setId(R.drawable.example_toolbar_stop).setListener {
            updateRunningState(false)
            resetChart()
        }.build())
        add(ImageViewWidget.Builder().setId(R.drawable.example_toolbar_settings).setListener { openSettingsDialog() }.build() )
    }

    private fun updateRunningState(isRunning: Boolean) {
        this.isRunning = isRunning
        updateAutoRangeBehavior(isRunning)
        updateModifiers(!isRunning)
    }

    private fun updateAutoRangeBehavior(isEnabled: Boolean) {
        binding.surface.run {
            val autoRangeMode = if (isEnabled) AutoRange.Always else AutoRange.Never
            xAxes.first().autoRange = autoRangeMode
            yAxes.first().autoRange = autoRangeMode
        }
    }

    private fun updateModifiers(isEnabled: Boolean) {
        binding.surface.chartModifiers.forEach { it.isEnabled = isEnabled }
    }

    private fun openSettingsDialog() {
        val dialog = ViewSettingsUtil.createSettingsPopup(activity, R.layout.example_performance_demo_popup_layout)
        val context = dialog.context

        with(dialog.findViewById<View>(R.id.series_types_spinner) as Spinner) {
            val seriesTypesAdapter = SpinnerStringAdapter(context, R.array.series_types)
            adapter = seriesTypesAdapter

            setSelection(seriesTypesAdapter.getPosition(selectedSeriesType))
            onItemSelectedListener = object : ItemSelectedListenerBase() {
                override fun onItemSelected(parent: AdapterView<*>?, view: View, position: Int, id: Long) {
                    selectedSeriesType = seriesTypesAdapter.getItem(position)
                    onChangeSeriesType()
                }
            }
        }

        with(dialog.findViewById<View>(R.id.stroke_spinner) as Spinner) {
            val strokeAdapter = SpinnerStringAdapter(context, R.array.stroke)
            adapter = strokeAdapter
            setSelection(strokeAdapter.getPosition(selectedStrokeThickness.toString()))
            onItemSelectedListener = object : ItemSelectedListenerBase() {
                override fun onItemSelected(parent: AdapterView<*>?, view: View, position: Int, id: Long) {
                    selectedStrokeThickness = strokeAdapter.getItem(position)!!.toInt()
                    onChangeStroke()
                }
            }
        }

        with(dialog.findViewById<View>(R.id.points_spinner) as Spinner) {
            val pointsAdapter = SpinnerStringAdapter(context, R.array.points)
            adapter = pointsAdapter
            setSelection(pointsAdapter.getPosition(resources.getStringArray(R.array.points)[pointCounts.indexOf(pointsCount)]))
            onItemSelectedListener = object : ItemSelectedListenerBase() {
                override fun onItemSelected(parent: AdapterView<*>?, view: View, position: Int, id: Long) {
                    pointsCount = pointCounts[position]
                    onChangeStroke()
                }
            }
        }

        with(dialog.findViewById<View>(R.id.resampling_mode_spinner) as Spinner) {
            val resamplingModeAdapter = SpinnerStringAdapter(context, R.array.resampling_mode)
            adapter = resamplingModeAdapter
            setSelection(resamplingModeAdapter.getPosition(selectedResamplingMode.toString()))
            onItemSelectedListener = object : ItemSelectedListenerBase() {
                override fun onItemSelected(parent: AdapterView<*>?, view: View, position: Int, id: Long) {
                    selectedResamplingMode = ResamplingMode.valueOf(resamplingModeAdapter.getItem(position)!!)
                    onResamplingMode()
                }
            }
        }

        dialog.show()
    }

    private fun onChangeSeriesType() {
        binding.surface.suspendUpdates {
            for (i in renderableSeries.indices) {
                renderableSeries[i] = changeSeriesType(selectedSeriesType!!, renderableSeries[i])
            }
        }
    }

    private fun changeSeriesType(param: String, initSeries: IRenderableSeries): IRenderableSeries? {
        when (param) {
            resources.getStringArray(R.array.series_types)[0] -> {
                return FastLineRenderableSeries().apply {
                    dataSeries = initSeries.dataSeries
                    strokeStyle = initSeries.strokeStyle
                    resamplingMode = initSeries.resamplingMode
                }
            }
            resources.getStringArray(R.array.series_types)[1] -> {
                return FastMountainRenderableSeries().apply {
                    dataSeries = initSeries.dataSeries
                    strokeStyle = initSeries.strokeStyle
                    resamplingMode = initSeries.resamplingMode
                    areaStyle = SolidBrushStyle(initSeries.strokeStyle.color)
                }
            }
            resources.getStringArray(R.array.series_types)[2] -> {
                return XyScatterRenderableSeries().apply {
                    dataSeries = initSeries.dataSeries
                    strokeStyle = initSeries.strokeStyle
                    resamplingMode = initSeries.resamplingMode
                    crossPointMarker {
                        setSize(20)
                        strokeStyle = initSeries.strokeStyle
                    }
                }
            }
            else -> return null
        }
    }

    private fun onChangeStroke() {
        binding.surface.suspendUpdates {
            renderableSeries.forEach {
                val currentStyle = it.strokeStyle
                it.strokeStyle = SolidPenStyle(
                    currentStyle.color,
                    currentStyle.thickness,
                    currentStyle.antiAliasing
                )
            }
        }
    }

    private fun onResamplingMode() {
        binding.surface.suspendUpdates {
            renderableSeries.forEach { it.resamplingMode = selectedResamplingMode }
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        savedInstanceState?.run {
            val surface = binding.surface
            val seriesCount: Int = getInt("seriesCount")
            for (i in 0 until seriesCount) {
                surface.renderableSeries[i].dataSeries = XyDataSeries<Int, Float>().apply {
                    val xValues: ISciList<Int> = getParcelable("xValues$i")!!
                    val yValues: ISciList<Float> = getParcelable("yValues$i")!!
                    append(xValues, yValues)
                }
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        isRunning = false

        outState.run {
            val surface = binding.surface
            val size = surface.renderableSeries.size
            putInt("seriesCount", size)
            for (i in 0 until size) {
                val series = surface.renderableSeries[i].dataSeries as IXyDataSeries<Int, Float>
                putParcelable("xValues$i", series.xValues)
                putParcelable("yValues$i", series.yValues)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        schedule.cancel(true)
    }

    private fun resetChart() {
        Handler().postDelayed({
            binding.surface.suspendUpdates {
                for (i in renderableSeries.indices) {
                    renderableSeries[i].dataSeries.clear()
                }
            }
        }, 100)
        maLow = MovingAverage(MA_LOW)
        maHigh = MovingAverage(MA_HIGH)
    }

    private fun calcMaxPointCountToDisplay(): Int {
        // for resampling None need
        // 8 mb for data series (float, int)
        // 16 mb for resampling (double, double)
        // 4 mb for data indices (int)
        // 8 mb for coordinates (float, float)
        val oneMlnPointsRequirement = (8 + 16 + 4 + 8).toFloat()
        // need to reserve some memory for other needs ( 40 mb should be enough )
        val memorySize = (getMaxMemorySize() - 40).toFloat()
        // max amount of point on screen
        val maxPointCount = memorySize / oneMlnPointsRequirement * 1000000
        // we have 3 series in example
        return (maxPointCount / 3).roundToInt()
    }

    private fun getMaxMemorySize(): Int {
        // max memory size in megabytes
        return (Runtime.getRuntime().maxMemory() / 1024L / 1024L).toInt()
    }

    private fun getPointsCount(): Int {
        var result = 0
        binding.surface.renderableSeries.forEach { result += it.dataSeries.count }

        return result
    }

    companion object {
        private const val MA_LOW = 200
        private const val MA_HIGH = 1000
        private const val TIME_INTERVAL: Long = 10
    }
}
