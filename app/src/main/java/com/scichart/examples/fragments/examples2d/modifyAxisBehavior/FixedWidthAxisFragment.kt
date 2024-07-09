//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2021. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// LogarithmicAxisFragment.kt is part of SCICHART®, High Performance Scientific Charts
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

package com.scichart.examples.fragments.examples2d.modifyAxisBehavior.kt

import android.view.Gravity
import android.view.View
import android.widget.AdapterView
import android.widget.SeekBar
import com.scichart.charting.visuals.SciChartSurface
import com.scichart.charting.visuals.axes.AutoRange
import com.scichart.charting.visuals.axes.AxisTickLabelStyle
import com.scichart.charting.visuals.axes.NumericAxis
import com.scichart.core.framework.UpdateSuspender
import com.scichart.drawing.utility.ColorUtil
import com.scichart.examples.R
import com.scichart.examples.fragments.base.ExampleSingleChartBaseFragment
import com.scichart.examples.utils.ItemSelectedListenerBase
import com.scichart.examples.utils.SeekBarChangeListenerBase
import com.scichart.examples.utils.ViewSettingsUtil
import com.scichart.examples.utils.scichartExtensions.SolidBrushStyle
import com.scichart.examples.utils.scichartExtensions.SolidPenStyle
import com.scichart.examples.utils.scichartExtensions.XyDataSeries
import com.scichart.examples.utils.scichartExtensions.ellipsePointMarker
import com.scichart.examples.utils.scichartExtensions.fastLineRenderableSeries
import com.scichart.examples.utils.scichartExtensions.numericAxis
import com.scichart.examples.utils.scichartExtensions.renderableSeries
import com.scichart.examples.utils.scichartExtensions.setSize
import com.scichart.examples.utils.scichartExtensions.suspendUpdates
import com.scichart.examples.utils.scichartExtensions.xAxes
import com.scichart.examples.utils.scichartExtensions.yAxes
import com.scichart.examples.utils.widgetgeneration.ImageViewWidget
import com.scichart.examples.utils.widgetgeneration.Widget
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit

class FixedWidthAxisFragment : ExampleSingleChartBaseFragment() {

    private val ds1 = XyDataSeries<Double, Double>().apply { fifoCapacity = FIFO_CAPACITY }

    private val scheduledExecutorService = Executors.newSingleThreadScheduledExecutor()
    private lateinit var schedule: ScheduledFuture<*>

    private var index = 0.0
    private var value = 0.0
    private var isIncreasing = true

    private var yAxisAlignment = 0
    private var xAxisAlignment = 0

    private var xAxisSize = 200
    private var yAxisSize = 200
    private lateinit var xAxis: NumericAxis
    private lateinit var yAxis: NumericAxis
    override fun getToolbarItems(): List<Widget> = ArrayList<Widget>().apply {
        add(
            ImageViewWidget.Builder().setId(R.drawable.example_toolbar_settings)
                .setListener { openSettingsDialog() }.build()
        )
    }

    override fun initExample(surface: SciChartSurface) {
        val line1Color = ColorUtil.argb(0xFF, 0x47, 0xBD, 0xE6)

        binding.surface.suspendUpdates {
            xAxes {
                numericAxis {
//                    visibleRange = xVisibleRange
                    xAxis = this
                    fixedSize = xAxisSize
                    autoRange = AutoRange.Always
                    axisTickLabelStyle = AxisTickLabelStyle(
                        Gravity.TOP,
                        0,0,0,0
                    )
                }
            }
            yAxes {
                numericAxis {
                    yAxis = this
                    fixedSize = yAxisSize
                    autoRange = AutoRange.Always
                    axisTickLabelStyle = AxisTickLabelStyle(
                        Gravity.LEFT,
                        0,0,0,0
                    )
                }
            }
            renderableSeries {
                fastLineRenderableSeries {
                    dataSeries = ds1
                    strokeStyle = SolidPenStyle(line1Color)
                    ellipsePointMarker { setSize(5); fillStyle = SolidBrushStyle(line1Color) }
                }
            }

            schedule = scheduledExecutorService.scheduleWithFixedDelay(
                insertRunnable,
                0,
                TIME_INTERVAL,
                TimeUnit.MILLISECONDS
            )
        }
    }

    private val insertRunnable = Runnable {
        binding.surface.suspendUpdates {
            ds1.append(index, value * value)
            index++
            if (value == 200.0) {
                isIncreasing = false
            }
            if (value == 0.0) {
                isIncreasing = true
            }
            if (isIncreasing) {
                value += 1.0
            } else {
                value -= 1.0
            }
        }
    }

    private fun openSettingsDialog() {
        val dialog = ViewSettingsUtil.createSettingsPopup(
            activity,
            R.layout.example_fixed_width_popup_layout
        )
        // For X Axis
        ViewSettingsUtil.setUpSpinner(
            dialog,
            R.id.xAxisAlignmentSelector,
            R.array.x_axis_alignment_list,
            xAxisAlignment,
            object : ItemSelectedListenerBase() {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View,
                    position: Int,
                    id: Long,
                ) {
                    UpdateSuspender.using(binding.surface) {
                        when (position) {
                            0 -> {
                                xAxis.axisTickLabelStyle = AxisTickLabelStyle(
                                    Gravity.TOP,
                                    0, 0, 0, 0
                                )
                                xAxisAlignment = 0
                            }

                            1 -> {
                                xAxis.axisTickLabelStyle = AxisTickLabelStyle(
                                    Gravity.CENTER,
                                    0, 0, 0, 0
                                )
                                xAxisAlignment = 1
                            }

                            2 -> {
                                xAxis.axisTickLabelStyle = AxisTickLabelStyle(
                                    Gravity.BOTTOM,
                                    0, 0, 0, 0
                                )
                                xAxisAlignment = 2
                            }

                            else -> {}
                        }
                    }
                }
            })
        ViewSettingsUtil.setUpSeekBar(
            dialog,
            R.id.x_axis_width_seek_bar,
            xAxisSize,
            object : SeekBarChangeListenerBase() {
                override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                    UpdateSuspender.using(binding.surface) {
                        xAxis.fixedSize = progress
                        xAxisSize = progress
                    }
                }
            })


        // For Y Axis
        ViewSettingsUtil.setUpSpinner(
            dialog,
            R.id.yAxisAlignmentSelector,
            R.array.y_axis_alignment_list,
            yAxisAlignment,
            object : ItemSelectedListenerBase() {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View,
                    position: Int,
                    id: Long,
                ) {
                    UpdateSuspender.using(binding.surface) {
                        when (position) {
                            0 -> {
                                yAxis.axisTickLabelStyle = AxisTickLabelStyle(
                                    Gravity.LEFT,
                                    0, 0, 0, 0
                                )
                                yAxisAlignment = 0
                            }

                            1 -> {
                                yAxis.axisTickLabelStyle = AxisTickLabelStyle(
                                    Gravity.CENTER,
                                    0, 0, 0, 0
                                )
                                yAxisAlignment = 1
                            }

                            2 -> {
                                yAxis.axisTickLabelStyle = AxisTickLabelStyle(
                                    Gravity.RIGHT,
                                    0, 0, 0, 0
                                )
                                yAxisAlignment = 2
                            }

                            else -> {}
                        }
                    }
                }
            })
        ViewSettingsUtil.setUpSeekBar(
            dialog,
            R.id.y_axis_width_seek_bar,
            yAxisSize,
            object : SeekBarChangeListenerBase() {
                override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                    UpdateSuspender.using(binding.surface) {
                        yAxis.fixedSize = progress
                        yAxisSize = progress
                    }
                }
            })

        dialog.show()
    }

    companion object {
        private const val FIFO_CAPACITY = 50
        private const val TIME_INTERVAL: Long = 500
    }
}