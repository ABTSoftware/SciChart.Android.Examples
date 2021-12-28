//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2021. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// UsingCursorModifierTooltipsFragment.kt is part of SCICHART®, High Performance Scientific Charts
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

package com.scichart.examples.fragments.examples2d.tooltipsAndHitTest.kt

import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.AdapterView
import android.widget.CompoundButton
import android.widget.Spinner
import com.scichart.charting.modifiers.CursorModifier
import com.scichart.charting.modifiers.SourceMode
import com.scichart.charting.visuals.SciChartSurface
import com.scichart.charting.visuals.axes.AutoRange.Always
import com.scichart.data.model.DoubleRange
import com.scichart.drawing.utility.ColorUtil
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

class UsingCursorModifierTooltipsFragment : ExampleSingleChartBaseFragment() {
    private val sourceModeValues = listOf(*SourceMode.values())
    private lateinit var cursorModifier: CursorModifier

    private var selectedSourceMode = 1
    private var showTooltip = true
    private var showAxisLabels = true

    override fun initExample(surface: SciChartSurface) {
        val data1 = DataManager.getInstance().getNoisySinewave(300.0, 1.0, POINT_COUNT, 0.25)
        val data2 = DataManager.getInstance().getSinewave(100.0, 2.0, POINT_COUNT)
        val data3 = DataManager.getInstance().getSinewave(200.0, 1.5, POINT_COUNT)
        val data4 = DataManager.getInstance().getSinewave(50.0, 0.1, POINT_COUNT)

        surface.suspendUpdates {
            xAxes { numericAxis { visibleRange = DoubleRange(3.0, 6.0)} }
            yAxes { numericAxis { growBy = DoubleRange(0.05, 0.05); autoRange = Always } }

            renderableSeries {
                fastLineRenderableSeries {
                    xyDataSeries<Double, Double>("Green Series") {
                        append(data1.xValues, data1.yValues)
                    }
                    strokeStyle = SolidPenStyle(0xFF177B17, 2f)

                    sweepAnimation { interpolator = DecelerateInterpolator(); duration = 2000 }
                }
                fastLineRenderableSeries {
                    xyDataSeries<Double, Double>("Red Series") {
                        append(data2.xValues, data2.yValues)
                    }
                    strokeStyle = SolidPenStyle(0xFFDD0909, 2f)

                    sweepAnimation { interpolator = DecelerateInterpolator(); duration = 2000 }
                }
                fastLineRenderableSeries {
                    xyDataSeries<Double, Double>("Grey Series") {
                        append(data3.xValues, data3.yValues)
                    }
                    strokeStyle = SolidPenStyle(ColorUtil.Grey, 2f)

                    sweepAnimation { interpolator = DecelerateInterpolator(); duration = 2000 }
                }
                fastLineRenderableSeries {
                    xyDataSeries<Double, Double>("Gold Series") {
                        append(data4.xValues, data4.yValues)
                    }
                    strokeStyle = SolidPenStyle(ColorUtil.Gold, 2f)
                    isVisible = false

                    sweepAnimation { interpolator = DecelerateInterpolator(); duration = 2000 }
                }
            }

            chartModifiers {
                cursorModifier {
                    showTooltip = this@UsingCursorModifierTooltipsFragment.showTooltip
                    showAxisLabels = this@UsingCursorModifierTooltipsFragment.showAxisLabels
                    cursorModifier = this
                }
            }
        }
    }

    override fun getToolbarItems(): List<Widget> = ArrayList<Widget>().apply {
        add(ImageViewWidget.Builder().setId(R.drawable.example_toolbar_settings).setListener { openSettingsDialog() }.build())
    }

    private fun openSettingsDialog() {
        val dialog = ViewSettingsUtil.createSettingsPopup(activity, R.layout.example_using_rollover_modofier_tooltips_popup_layout)
        val context = dialog.context

        dialog.findViewById<Spinner>(R.id.legend_source_spinner_rollover).run {
            adapter = SpinnerStringAdapter(context, EnumUtils.getEnumValuesArray(SourceMode::class.java))
            setSelection(selectedSourceMode)
            onItemSelectedListener = object : ItemSelectedListenerBase() {
                override fun onItemSelected(parent: AdapterView<*>?, view: View, position: Int, id: Long) {
                    selectedSourceMode = position
                    cursorModifier.sourceMode = sourceModeValues[selectedSourceMode]
                }
            }
        }

        ViewSettingsUtil.setUpSwitchCompat(dialog, R.id.show_tooltips_checkbox, showTooltip) { _: CompoundButton?, isChecked: Boolean ->
            showTooltip = isChecked
            cursorModifier.showTooltip = showTooltip
        }

        ViewSettingsUtil.setUpSwitchCompat(dialog, R.id.show_axis_labels_checkbox, showAxisLabels) { _: CompoundButton?, isChecked: Boolean ->
            showAxisLabels = isChecked
            cursorModifier.showAxisLabels = showAxisLabels
        }

        dialog.show()
    }

    companion object {
        private const val POINT_COUNT = 500
    }
}