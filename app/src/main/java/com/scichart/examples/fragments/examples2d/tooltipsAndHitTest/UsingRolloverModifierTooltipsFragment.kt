//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2021. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// UsingRolloverModifierTooltipsFragment.kt is part of SCICHART®, High Performance Scientific Charts
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
import com.scichart.charting.modifiers.RolloverModifier
import com.scichart.charting.modifiers.SourceMode
import com.scichart.charting.visuals.SciChartSurface
import com.scichart.data.model.DoubleRange
import com.scichart.drawing.utility.ColorUtil
import com.scichart.examples.R
import com.scichart.examples.components.SpinnerStringAdapter
import com.scichart.examples.fragments.base.ExampleSingleChartBaseFragment
import com.scichart.examples.utils.EnumUtils
import com.scichart.examples.utils.ItemSelectedListenerBase
import com.scichart.examples.utils.ViewSettingsUtil
import com.scichart.examples.utils.scichartExtensions.*
import com.scichart.examples.utils.widgetgeneration.ImageViewWidget
import com.scichart.examples.utils.widgetgeneration.Widget
import java.util.ArrayList
import kotlin.math.sin

class UsingRolloverModifierTooltipsFragment: ExampleSingleChartBaseFragment() {
    private val sourceModeValues = listOf(*SourceMode.values())
    private lateinit var rolloverModifier: RolloverModifier

    private var selectedSourceMode = 1
    private var showTooltip = true
    private var showAxisLabels = true
    private var drawVerticalLine = true

    override fun initExample(surface: SciChartSurface) {
        val ds1 = XyDataSeries<Int, Double>("SineWave A")
        val ds2 = XyDataSeries<Int, Double>("SineWave B")
        val ds3 = XyDataSeries<Int, Double>("SineWave C")

        val count = 100.0
        val k = 2 * Math.PI / 30.0
        for (i in 0 until count.toInt()) {
            val phi = k * i
            ds1.append(i, (1.0 + i / count) * sin(phi))
            ds2.append(i, (0.5 + i / count) * sin(phi))
            ds3.append(i, i / count * sin(phi))
        }

        surface.suspendUpdates {
            xAxes { numericAxis() }
            yAxes { numericAxis { growBy = DoubleRange(0.2, 0.2) } }

            renderableSeries {
                fastLineRenderableSeries {
                    dataSeries = ds1
                    strokeStyle = SolidPenStyle(ColorUtil.SteelBlue, 2f)
                    ellipsePointMarker { setSize(7); fillStyle = SolidBrushStyle(ColorUtil.Lavender) }

                    sweepAnimation { interpolator = DecelerateInterpolator(); duration = 2000 }
                }
                fastLineRenderableSeries {
                    dataSeries = ds2
                    strokeStyle = SolidPenStyle(ColorUtil.DarkGreen, 2f)
                    ellipsePointMarker { setSize(7); fillStyle = SolidBrushStyle(ColorUtil.Lavender) }

                    sweepAnimation { interpolator = DecelerateInterpolator(); duration = 2000 }
                }
                fastLineRenderableSeries {
                    dataSeries = ds3
                    strokeStyle = SolidPenStyle(ColorUtil.LightSteelBlue, 2f)

                    sweepAnimation { interpolator = DecelerateInterpolator(); duration = 2000 }
                }
            }

            chartModifiers {
                rolloverModifier {
                    showTooltip = this@UsingRolloverModifierTooltipsFragment.showTooltip
                    showAxisLabels = this@UsingRolloverModifierTooltipsFragment.showAxisLabels
                    drawVerticalLine = this@UsingRolloverModifierTooltipsFragment.drawVerticalLine
                    rolloverModifier = this
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
                    rolloverModifier.sourceMode = sourceModeValues[selectedSourceMode]
                }
            }
        }

        ViewSettingsUtil.setUpSwitchCompat(dialog, R.id.show_tooltips_checkbox, showTooltip) { _: CompoundButton?, isChecked: Boolean ->
            showTooltip = isChecked
            rolloverModifier.showTooltip = showTooltip
        }

        ViewSettingsUtil.setUpSwitchCompat(dialog, R.id.show_axis_labels_checkbox, showAxisLabels) { _: CompoundButton?, isChecked: Boolean ->
            showAxisLabels = isChecked
            rolloverModifier.showAxisLabels = showAxisLabels
        }

        ViewSettingsUtil.setUpSwitchCompat(dialog, R.id.draw_vertical_line_checkbox, drawVerticalLine) { _: CompoundButton?, isChecked: Boolean ->
            drawVerticalLine = isChecked
            rolloverModifier.drawVerticalLine = drawVerticalLine
        }

        dialog.show()
    }
}