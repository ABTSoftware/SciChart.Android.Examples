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

import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.AdapterView
import android.widget.CompoundButton
import android.widget.Spinner
import com.scichart.charting.visuals.SciChartSurface
import com.scichart.charting.visuals.axes.*
import com.scichart.charting.visuals.axes.ScientificNotation.LogarithmicBase
import com.scichart.charting.visuals.axes.ScientificNotation.Normalized
import com.scichart.data.model.DoubleRange
import com.scichart.drawing.utility.ColorUtil
import com.scichart.examples.R
import com.scichart.examples.components.SpinnerStringAdapter
import com.scichart.examples.data.DataManager
import com.scichart.examples.fragments.base.ExampleSingleChartBaseFragment
import com.scichart.examples.utils.ItemSelectedListenerBase
import com.scichart.examples.utils.ViewSettingsUtil
import com.scichart.examples.utils.scichartExtensions.*
import com.scichart.examples.utils.widgetgeneration.ImageViewWidget
import com.scichart.examples.utils.widgetgeneration.Widget
import java.util.*

class LogarithmicAxisFragment : ExampleSingleChartBaseFragment() {

    private var selectedLogBase = 10.0
    private var isXLogAxis = true
    private var isYLogAxis = true

    override fun initExample(surface: SciChartSurface) {
        val ds1Points = DataManager.getInstance().getExponentialCurve(1.8, 100)
        val ds2Points = DataManager.getInstance().getExponentialCurve(2.25, 100)
        val ds3Points = DataManager.getInstance().getExponentialCurve(3.59, 100)

        val line1Color = ColorUtil.argb(0xFF, 0xFF, 0xFF, 0x00)
        val line2Color = ColorUtil.argb(0xFF, 0x27, 0x9B, 0x27)
        val line3Color = ColorUtil.argb(0xFF, 0xFF, 0x19, 0x19)

        binding.surface.suspendUpdates {
            xAxes { generateAxis(isXLogAxis) }
            yAxes { generateAxis(isYLogAxis) }
            renderableSeries {
                fastLineRenderableSeries {
                    xyDataSeries<Double, Double>("Curve A") { append(ds1Points.xValues, ds1Points.yValues) }
                    strokeStyle = SolidPenStyle(line1Color)
                    ellipsePointMarker { setSize(5); fillStyle = SolidBrushStyle(line1Color) }

                    sweepAnimation { interpolator = DecelerateInterpolator() }
                }
                fastLineRenderableSeries {
                    xyDataSeries<Double, Double>("Curve B") { append(ds2Points.xValues, ds2Points.yValues) }
                    strokeStyle = SolidPenStyle(line2Color)
                    ellipsePointMarker { setSize(5); fillStyle = SolidBrushStyle(line2Color) }

                    sweepAnimation { interpolator = DecelerateInterpolator() }
                }
                fastLineRenderableSeries {
                    xyDataSeries<Double, Double>("Curve C") { append(ds3Points.xValues, ds3Points.yValues) }
                    strokeStyle = SolidPenStyle(line3Color)
                    ellipsePointMarker { setSize(5); fillStyle = SolidBrushStyle(line3Color) }

                    sweepAnimation { interpolator = DecelerateInterpolator() }
                }
            }

            chartModifiers { defaultModifiers() }
        }
    }

    private fun CollectionContext<IAxis>.generateAxis(isLogAxis: Boolean) {
        if (isLogAxis) logarithmicAxis {
            textFormatting = "#.#E+0"
            scientificNotation = LogarithmicBase
            logarithmicBase = selectedLogBase
            growBy = DoubleRange(0.1, 0.1)
            drawMajorBands = false
        }
        else numericAxis {
            textFormatting = "#.#E+0"
            scientificNotation = Normalized
            growBy = DoubleRange(0.1, 0.1)
            drawMajorBands = false
        }
    }

    override fun getToolbarItems(): List<Widget> = ArrayList<Widget>().apply {
        add(ImageViewWidget.Builder().setId(R.drawable.example_toolbar_settings).setListener { openSettingsDialog() }.build())
    }

    private fun openSettingsDialog() {
        val dialog = ViewSettingsUtil.createSettingsPopup(activity, R.layout.example_log_axis_popup_layout)
        val context = dialog.context

        dialog.findViewById<Spinner>(R.id.log_base_spinner).run {
            adapter = SpinnerStringAdapter(context, R.array.log_base_modes)
            setSelection(2)

            onItemSelectedListener = object : ItemSelectedListenerBase() {
                override fun onItemSelected(parent: AdapterView<*>?, view: View, position: Int, id: Long) {
                    when (position) {
                        0 -> selectedLogBase = 2.0
                        1 -> selectedLogBase = 5.0
                        2 -> selectedLogBase = 10.0
                        3 -> selectedLogBase = Math.E
                    }

                    trySetLogBaseForAxis(binding.surface.xAxes.first(), selectedLogBase)
                    trySetLogBaseForAxis(binding.surface.yAxes.first(), selectedLogBase)
                }
            }
        }

        ViewSettingsUtil.setUpSwitchCompat(dialog, R.id.use_log_on_x_axis_checkbox, isXLogAxis) { _: CompoundButton?, isChecked: Boolean ->
            isXLogAxis = isChecked
            binding.surface.suspendUpdates {
                xAxes(true) { generateAxis(isXLogAxis) }
            }
        }

        ViewSettingsUtil.setUpSwitchCompat(dialog, R.id.use_log_on_y_axis_checkbox, isYLogAxis) { _: CompoundButton?, isChecked: Boolean ->
            isYLogAxis = isChecked
            binding.surface.suspendUpdates {
                yAxes(true) { generateAxis(isYLogAxis) }
            }
        }

        dialog.show()
    }

    private fun trySetLogBaseForAxis(axis: IAxis, logBase: Double) {
        (axis as? ILogarithmicNumericAxis)?.logarithmicBase = logBase
    }
}