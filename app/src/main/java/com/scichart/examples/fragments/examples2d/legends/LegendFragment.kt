//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2021. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// LegendFragment.kt is part of SCICHART®, High Performance Scientific Charts
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

package com.scichart.examples.fragments.examples2d.legends.kt

import android.view.Gravity
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.AdapterView
import android.widget.CompoundButton
import android.widget.Spinner
import com.scichart.charting.modifiers.LegendModifier
import com.scichart.charting.modifiers.SourceMode
import com.scichart.charting.visuals.SciChartSurface
import com.scichart.charting.visuals.renderableSeries.IRenderableSeries
import com.scichart.charting.visuals.renderableSeries.StyleBase
import com.scichart.core.annotations.Orientation
import com.scichart.drawing.common.PenStyle
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

class LegendFragment : ExampleSingleChartBaseFragment() {
    private val sourceModeValues = listOf(*SourceMode.values())
    private lateinit var legendModifier: LegendModifier

    private var selectedOrientation = Orientation.VERTICAL
    private var selectedSourceMode = 0
    private var showLegend = true
    private var showCheckBoxes = true
    private var showSeriesMarkers = true

    override fun initExample(surface: SciChartSurface) {
        val ds1Points = DataManager.getInstance().getStraightLine(4000.0, 1.0, 10)
        val ds2Points = DataManager.getInstance().getStraightLine(3000.0, 1.0, 10)
        val ds3Points = DataManager.getInstance().getStraightLine(2000.0, 1.0, 10)
        val ds4Points = DataManager.getInstance().getStraightLine(1000.0, 1.0, 10)

        surface.suspendUpdates {
            xAxes { numericAxis() }
            yAxes { numericAxis() }
            renderableSeries {
                fastLineRenderableSeries {
                    xyDataSeries<Double, Double>("Curve A") { append(ds1Points.xValues, ds1Points.yValues) }
                    strokeStyle = SolidPenStyle(ColorUtil.argb(0xFF, 0x47, 0xBD, 0xE6))
                    sweepAnimation { interpolator = DecelerateInterpolator() }
                }
                fastLineRenderableSeries {
                    xyDataSeries<Double, Double>("Curve B") { append(ds2Points.xValues, ds2Points.yValues) }
                    strokeStyle = SolidPenStyle(ColorUtil.argb(0xFF, 0xAE, 0x41, 0x8D))
                    sweepAnimation { interpolator = DecelerateInterpolator() }
                }
                fastLineRenderableSeries {
                    xyDataSeries<Double, Double>("Curve C") { append(ds3Points.xValues, ds3Points.yValues) }
                    strokeStyle = SolidPenStyle(ColorUtil.argb(0xFF, 0x68, 0xBC, 0xA8))
                    sweepAnimation { interpolator = DecelerateInterpolator() }
                }
                fastLineRenderableSeries {
                    xyDataSeries<Double, Double>("Curve D") { append(ds4Points.xValues, ds4Points.yValues) }
                    strokeStyle = SolidPenStyle(ColorUtil.argb(0xFF, 0xE9, 0x70, 0x64))
                    sweepAnimation { interpolator = DecelerateInterpolator() }
                }
            }

            legendModifier = LegendModifier(context).apply {
                setLegendPosition(Gravity.TOP or Gravity.START, 16)
                setSourceMode(sourceModeValues[selectedSourceMode])
                setOrientation(selectedOrientation)
            }
            chartModifiers {
                modifier(legendModifier)
                seriesSelectionModifier {
                    selectedSeriesStyle = object : StyleBase<IRenderableSeries>(IRenderableSeries::class.java) {
                        override fun applyStyleInternal(renderableSeriesToStyle: IRenderableSeries) {
                            val currentStrokeStyle = renderableSeriesToStyle.strokeStyle
                            putPropertyValue(renderableSeriesToStyle, "Stroke", currentStrokeStyle)

                            val newStrokeStyle = SolidPenStyle(currentStrokeStyle.color, 3f)
                            renderableSeriesToStyle.strokeStyle = newStrokeStyle
                        }

                        override fun discardStyleInternal(renderableSeriesToStyle: IRenderableSeries) {
                            val stroke = getPropertyValue(renderableSeriesToStyle, "Stroke", PenStyle::class.java)
                            renderableSeriesToStyle.strokeStyle = stroke
                        }
                    }
                }
            }
        }
    }

    override fun getToolbarItems(): List<Widget> = ArrayList<Widget>().apply {
        add(ImageViewWidget.Builder().setId(R.drawable.example_toolbar_settings).setListener { openSettingsDialog() }.build())
    }

    private fun openSettingsDialog() {
        val dialog = ViewSettingsUtil.createSettingsPopup(activity, R.layout.example_performance_demo_popup_layout)
        val context = dialog.context

        dialog.findViewById<Spinner>(R.id.legend_orientation_spinner).run {
            adapter = SpinnerStringAdapter(context, R.array.legend_orientation)

            setSelection(selectedOrientation)
            onItemSelectedListener = object : ItemSelectedListenerBase() {
                override fun onItemSelected(parent: AdapterView<*>?, view: View, position: Int, id: Long) {
                    selectedOrientation = if (position == 0) Orientation.HORIZONTAL else Orientation.VERTICAL
                    legendModifier.setOrientation(selectedOrientation)
                }
            }
        }

        ViewSettingsUtil.setUpSwitchCompat(dialog, R.id.show_legend_checkbox, showLegend) { _: CompoundButton?, isChecked: Boolean ->
            showLegend = isChecked
            legendModifier.setShowLegend(showLegend)
        }
        ViewSettingsUtil.setUpSwitchCompat(dialog, R.id.show_checkboxes_checkbox, showCheckBoxes) { _: CompoundButton?, isChecked: Boolean ->
            showCheckBoxes = isChecked
            legendModifier.setShowCheckboxes(showCheckBoxes)
        }
        ViewSettingsUtil.setUpSwitchCompat(dialog, R.id.show_series_markers_checkbox, showSeriesMarkers) { _: CompoundButton?, isChecked: Boolean ->
            showSeriesMarkers = isChecked
            legendModifier.setShowSeriesMarkers(showSeriesMarkers)
        }

        dialog.findViewById<Spinner>(R.id.legend_source_spinner).run {
            adapter = SpinnerStringAdapter(context, EnumUtils.getEnumValuesArray(SourceMode::class.java))

            setSelection(selectedSourceMode)
            onItemSelectedListener = object : ItemSelectedListenerBase() {
                override fun onItemSelected(parent: AdapterView<*>?, view: View, position: Int, id: Long) {
                    selectedSourceMode = position
                    legendModifier.setSourceMode(sourceModeValues[selectedSourceMode])
                }
            }
        }

        dialog.show()
    }
}