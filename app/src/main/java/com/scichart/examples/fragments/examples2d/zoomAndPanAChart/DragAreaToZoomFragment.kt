//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2021. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// DragAreaToZoomFragment.kt is part of SCICHART®, High Performance Scientific Charts
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

import android.view.animation.DecelerateInterpolator
import com.scichart.charting.modifiers.RubberBandXyZoomModifier
import com.scichart.charting.visuals.SciChartSurface
import com.scichart.data.model.DoubleRange
import com.scichart.drawing.utility.ColorUtil
import com.scichart.examples.R
import com.scichart.examples.data.RandomWalkGenerator
import com.scichart.examples.fragments.base.ExampleSingleChartBaseFragment
import com.scichart.examples.utils.ViewSettingsUtil
import com.scichart.examples.utils.scichartExtensions.*
import com.scichart.examples.utils.widgetgeneration.ImageViewWidget
import com.scichart.examples.utils.widgetgeneration.Widget

class DragAreaToZoomFragment : ExampleSingleChartBaseFragment() {
    private lateinit var rubberBandXyZoomModifier: RubberBandXyZoomModifier

    override fun showDefaultModifiersInToolbar(): Boolean = false

    override fun getToolbarItems(): List<Widget> = ArrayList<Widget>().apply {
        add(ImageViewWidget.Builder().setId(R.drawable.example_toolbar_settings).setListener { openSettingsDialog() }.build())
    }

    override fun initExample(surface: SciChartSurface) {
        val data = RandomWalkGenerator(0).setBias(0.0001).getRandomWalkSeries(10000)

        surface.suspendUpdates {
            xAxes { numericAxis() }
            yAxes { numericAxis { growBy = DoubleRange(0.1, 0.1) } }

            renderableSeries {
                fastLineRenderableSeries {
                    xyDataSeries<Double, Double> { append(data.xValues, data.yValues) }
                    strokeStyle = SolidPenStyle(ColorUtil.argb(255, 9, 68, 27))

                    sweepAnimation { interpolator = DecelerateInterpolator(); duration = 2000 }
                }
            }

            chartModifiers {
                zoomExtentsModifier()
                rubberBandXyZoomModifier {
                    isXAxisOnly = true
                    receiveHandledEvents = true
                    rubberBandXyZoomModifier = this
                }
            }
        }
    }

    private fun openSettingsDialog() {
        val dialog = ViewSettingsUtil.createSettingsPopup(activity, R.layout.example_drag_area_to_zoom_popop_layout)

        ViewSettingsUtil.setUpSwitchCompat(dialog, R.id.zoom_x_axis_only_checkbox, rubberBandXyZoomModifier.isXAxisOnly) { _, isChecked -> rubberBandXyZoomModifier.isXAxisOnly = isChecked }
        ViewSettingsUtil.setUpSwitchCompat(dialog, R.id.zoom_extents_y_axis_checkbox, rubberBandXyZoomModifier.zoomExtentsY) { _, isChecked -> rubberBandXyZoomModifier.zoomExtentsY = isChecked }
        ViewSettingsUtil.setUpSwitchCompat(dialog, R.id.use_animation_checkbox, rubberBandXyZoomModifier.isAnimated) { _, isChecked -> rubberBandXyZoomModifier.isAnimated = isChecked }

        dialog.show()
    }
}