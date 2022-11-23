//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2021. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// DragAxisToScaleChartFragment.kt is part of SCICHART®, High Performance Scientific Charts
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

package com.scichart.examples.fragments.examples2d.zoomAndPanAChart.kt

import android.view.View
import android.widget.AdapterView
import android.widget.Spinner
import com.scichart.charting.ClipMode.None
import com.scichart.charting.Direction2D
import com.scichart.charting.Direction2D.XyDirection
import com.scichart.charting.modifiers.AxisDragModifierBase.AxisDragMode
import com.scichart.charting.modifiers.AxisDragModifierBase.AxisDragMode.Scale
import com.scichart.charting.modifiers.XAxisDragModifier
import com.scichart.charting.modifiers.YAxisDragModifier
import com.scichart.charting.visuals.SciChartSurface
import com.scichart.charting.visuals.axes.AxisAlignment.*
import com.scichart.data.model.DoubleRange
import com.scichart.examples.R
import com.scichart.examples.components.SpinnerStringAdapter
import com.scichart.examples.data.DataManager
import com.scichart.examples.fragments.base.ExampleSingleChartBaseFragment
import com.scichart.examples.utils.EnumUtils
import com.scichart.examples.utils.ItemSelectedListenerBase
import com.scichart.examples.utils.ViewSettingsUtil
import com.scichart.examples.utils.interpolator.CubicInOutInterpolator
import com.scichart.examples.utils.scichartExtensions.*
import com.scichart.examples.utils.widgetgeneration.ImageViewWidget
import com.scichart.examples.utils.widgetgeneration.Widget

class DragAxisToScaleChartFragment : ExampleSingleChartBaseFragment() {
    private lateinit var xAxisDragModifier: XAxisDragModifier
    private lateinit var yAxisDragModifier: YAxisDragModifier

    private var selectedDragMode = Scale
    private var selectedDirection = XyDirection

    override fun getToolbarItems(): List<Widget> = ArrayList<Widget>().apply {
        add(ImageViewWidget.Builder().setId(R.drawable.example_toolbar_settings).setListener { openSettingsDialog() }.build())
    }

    override fun initExample(surface: SciChartSurface) {
        val fourierSeries = DataManager.getInstance().getFourierSeries(1.0, 0.1, 5000)
        val dampedSinewave = DataManager.getInstance().getDampedSinewave(1500, 3.0, 0.0, 0.005, 5000, 10)

        surface.suspendUpdates {
            xAxes {
                numericAxis {
                    axisAlignment = Top
                    textFormatting = "0.0"
                    growBy = DoubleRange(0.1, 0.1)
                    visibleRange = DoubleRange(3.0, 6.0)
                }
            }
            yAxes {
                numericAxis {
                    axisId = "RightAxisId"
                    axisAlignment = Right
                    setTextColor(0xFF47bde6)
                    growBy = DoubleRange(0.1, 0.1)
                }
                numericAxis {
                    axisId = "LeftAxisId"
                    axisAlignment = Left
                    setTextColor(0xFFae418d)
                    growBy = DoubleRange(0.1, 0.1)
                }
            }

            renderableSeries {
                fastMountainRenderableSeries {
                    yAxisId = "LeftAxisId"
                    areaStyle = SolidBrushStyle(0x77ae418d)
                    strokeStyle = SolidPenStyle(0xFFc43360, 2f)
                    xyDataSeries<Double, Double> { append(fourierSeries.xValues, fourierSeries.yValues) }

                    scaleAnimation { interpolator = CubicInOutInterpolator() }
                }
                fastLineRenderableSeries {
                    yAxisId = "RightAxisID"
                    strokeStyle = SolidPenStyle(0xFF47bde6, 2f)
                    xyDataSeries<Double, Double> { append(dampedSinewave.xValues, dampedSinewave.yValues) }

                    sweepAnimation { interpolator = CubicInOutInterpolator() }
                }
            }

            chartModifiers {
                xAxisDragModifier { clipModeX = None; xAxisDragModifier = this }
                yAxisDragModifier { yAxisDragModifier = this }
                zoomPanModifier { receiveHandledEvents = true }
                zoomExtentsModifier()
            }
        }
    }

    private fun openSettingsDialog() {
        val dialog = ViewSettingsUtil.createSettingsPopup(activity, R.layout.example_drag_axis_to_scale_chart_layout_popup)
        val context = dialog.context

        val axisDragModeAdapter = SpinnerStringAdapter(context, EnumUtils.getEnumValuesArray(AxisDragMode::class.java))
        dialog.findViewById<Spinner>(R.id.axis_drag_mode_spinner).run {
            adapter = axisDragModeAdapter
            setSelection(axisDragModeAdapter.getPosition(selectedDragMode.toString()))
            onItemSelectedListener = object : ItemSelectedListenerBase() {
                override fun onItemSelected(parent: AdapterView<*>?, view: View, position: Int, id: Long) {
                    selectedDragMode = AxisDragMode.valueOf(axisDragModeAdapter.getItem(position)!!)
                    xAxisDragModifier.dragMode = selectedDragMode
                    yAxisDragModifier.dragMode = selectedDragMode
                }
            }
        }

        val xyDirectionAdapter = SpinnerStringAdapter(context, EnumUtils.getEnumValuesArray(Direction2D::class.java))
        dialog.findViewById<Spinner>(R.id.direction_spinner).run {
            adapter = xyDirectionAdapter
            setSelection(xyDirectionAdapter.getPosition(selectedDirection.toString()))
            onItemSelectedListener = object : ItemSelectedListenerBase() {
                override fun onItemSelected(parent: AdapterView<*>?, view: View, position: Int, id: Long) {
                    selectedDirection = Direction2D.valueOf(xyDirectionAdapter.getItem(position)!!)
                    updateAxesDragDirections(selectedDirection)
                }
            }
        }

        dialog.show()
    }

    private fun updateAxesDragDirections(direction: Direction2D) {
        when (direction) {
            Direction2D.XDirection -> {
                xAxisDragModifier.isEnabled = true
                yAxisDragModifier.isEnabled = false
            }
            Direction2D.YDirection -> {
                xAxisDragModifier.isEnabled = false
                yAxisDragModifier.isEnabled = true
            }
            XyDirection -> {
                xAxisDragModifier.isEnabled = true
                yAxisDragModifier.isEnabled = true
            }
        }
    }
}