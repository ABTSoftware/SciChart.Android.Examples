//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2021. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// OscilloscopeFragment.kt is part of SCICHART®, High Performance Scientific Charts
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

package com.scichart.examples.fragments.featuredApps.scientificCharts.kt

import android.view.View
import android.widget.AdapterView
import android.widget.Spinner
import com.scichart.charting.visuals.SciChartSurface
import com.scichart.charting.visuals.axes.AutoRange.Never
import com.scichart.charting.visuals.renderableSeries.FastLineRenderableSeries
import com.scichart.core.model.DoubleValues
import com.scichart.data.model.DoubleRange
import com.scichart.examples.R
import com.scichart.examples.components.SpinnerStringAdapter
import com.scichart.examples.data.DataManager
import com.scichart.examples.fragments.base.ExampleSingleChartBaseFragment
import com.scichart.examples.utils.EnumUtils
import com.scichart.examples.utils.ItemSelectedListenerBase
import com.scichart.examples.utils.ViewSettingsUtil
import com.scichart.examples.utils.scichartExtensions.*
import com.scichart.examples.utils.widgetgeneration.ImageViewWidget
import com.scichart.examples.utils.widgetgeneration.Widget
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit

class OscilloscopeFragment : ExampleSingleChartBaseFragment() {
    private val scheduledExecutorService = Executors.newSingleThreadScheduledExecutor()
    private lateinit var schedule: ScheduledFuture<*>

    private lateinit var rSeries: FastLineRenderableSeries
    private val dataSeries1 = XyDataSeries<Double, Double>().apply { acceptsUnsortedData = true }
    private val dataSeries2 = XyDataSeries<Double, Double>().apply { acceptsUnsortedData = true }

    private var phase0 = 0.0
    private var phase1 = 0.0
    private var phaseIncrement = Math.PI * 0.1
    private var selectedSource = DataSourceEnum.FourierSeries
    private var isDigitalLine = false

    override fun getToolbarItems(): List<Widget> = ArrayList<Widget>().apply {
        add(ImageViewWidget.Builder().setId(R.drawable.example_toolbar_settings).setListener { openSettingsDialog() }.build())
    }

    override fun initExample(surface: SciChartSurface) {
        surface.theme = R.style.SciChart_NavyBlue

        surface.suspendUpdates {
            xAxes { numericAxis {
                autoRange = Never
                axisTitle = "Time (ms)"
                visibleRange = DoubleRange(2.5, 4.5)
            }}
            yAxes { numericAxis {
                autoRange = Never
                axisTitle = "Voltage (mV)"
                visibleRange = DoubleRange(-12.5, 12.5)
            }}

            renderableSeries {
                fastLineRenderableSeries { dataSeries1; rSeries = this }
            }

            chartModifiers { defaultModifiers() }
        }

        schedule = scheduledExecutorService.scheduleWithFixedDelay(appendDataRunnable, 0, TIME_INTERVAL, TimeUnit.MILLISECONDS)
    }

    private fun openSettingsDialog() {
        val dialog = ViewSettingsUtil.createSettingsPopup(activity, R.layout.example_oscilloscope_demo_popup_layout)
        val context = dialog.context

        val dataSourceAdapter = SpinnerStringAdapter(context, EnumUtils.getEnumValuesArray(DataSourceEnum::class.java))
        val dataSourceSpinner = dialog.findViewById<View>(R.id.data_source_spinner) as Spinner
        dataSourceSpinner.adapter = dataSourceAdapter
        dataSourceSpinner.setSelection(dataSourceAdapter.getPosition(selectedSource.toString()))
        dataSourceSpinner.onItemSelectedListener = object : ItemSelectedListenerBase() {
            override fun onItemSelected(parent: AdapterView<*>?, view: View, position: Int, id: Long) {
                val surface = binding.surface

                selectedSource = DataSourceEnum.valueOf(dataSourceAdapter.getItem(position)!!)
                if (selectedSource == DataSourceEnum.FourierSeries) {
                    surface.xAxes[0].visibleRange = DoubleRange(2.5, 4.5)
                    surface.yAxes[0].visibleRange = DoubleRange(-12.5, 12.5)
                    phaseIncrement = Math.PI * 0.1
                } else {
                    surface.xAxes[0].visibleRange = DoubleRange(-1.2, 1.2)
                    surface.yAxes[0].visibleRange = DoubleRange(-1.2, 1.2)
                    phaseIncrement = Math.PI * 0.02
                }
            }
        }

        ViewSettingsUtil.setUpSwitchCompat(dialog, R.id.is_step_line_checkbox, isDigitalLine) { _, isChecked ->
            isDigitalLine = isChecked
            rSeries.setIsDigitalLine(isDigitalLine)
        }

        dialog.show()
    }

    private val appendDataRunnable: Runnable = object : Runnable {
        private var isSecondDataSeries = false
        private val xValues = DoubleValues()
        private val yValues = DoubleValues()

        override fun run() {
            if (selectedSource == DataSourceEnum.Lissajous) {
                DataManager.getInstance().setLissajousCurve(xValues, yValues, 0.12, phase1, phase0, 2500)
            } else {
                DataManager.getInstance().setFourierSeries(xValues, yValues, 2.0, phase0, 1000)
            }
            phase0 += phaseIncrement
            phase1 += phaseIncrement * 0.005

            // TODO - this code prevents blinking of series
            if (isSecondDataSeries) {
                dataSeries1.clear()
                dataSeries1.append(xValues, yValues)
                rSeries.dataSeries = dataSeries1
            } else {
                dataSeries2.clear()
                dataSeries2.append(xValues, yValues)
                rSeries.dataSeries = dataSeries2
            }

            isSecondDataSeries = !isSecondDataSeries
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        schedule.cancel(true)
    }

    internal enum class DataSourceEnum {
        FourierSeries, Lissajous
    }

    companion object {
        private const val TIME_INTERVAL: Long = 20
    }
}
