//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2021. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// VerticalChartsFragment.kt is part of SCICHART®, High Performance Scientific Charts
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

import android.view.animation.DecelerateInterpolator
import com.scichart.charting.visuals.SciChartSurface
import com.scichart.charting.visuals.axes.AxisAlignment.Left
import com.scichart.charting.visuals.axes.AxisAlignment.Top
import com.scichart.data.model.DoubleRange
import com.scichart.drawing.utility.ColorUtil
import com.scichart.examples.data.DataManager
import com.scichart.examples.fragments.base.ExampleSingleChartBaseFragment
import com.scichart.examples.utils.scichartExtensions.*

class VerticalChartsFragment: ExampleSingleChartBaseFragment() {

    override fun showDefaultModifiersInToolbar(): Boolean = false

    override fun initExample(surface: SciChartSurface) {
        val ds1Points = DataManager.getInstance().getRandomDoubleSeries(20)
        val ds2Points = DataManager.getInstance().getRandomDoubleSeries(20)

        surface.suspendUpdates {
            xAxes { numericAxis { axisTitle = "X-Axis"; axisAlignment = Left } }
            yAxes { numericAxis { axisTitle = "Y-Axis"; axisAlignment = Top; growBy = DoubleRange(0.0, 0.1) }               }

            renderableSeries {
                fastLineRenderableSeries {
                    xyDataSeries<Double, Double> { append(ds1Points.xValues, ds1Points.yValues) }
                    strokeStyle = SolidPenStyle(ColorUtil.SteelBlue, 2f)

                    sweepAnimation { interpolator = DecelerateInterpolator() }
                }
                fastLineRenderableSeries {
                    xyDataSeries<Double, Double> { append(ds2Points.xValues, ds2Points.yValues) }
                    strokeStyle = SolidPenStyle(ColorUtil.Lime, 2f)

                    sweepAnimation { interpolator = DecelerateInterpolator() }
                }
            }

            chartModifiers { defaultModifiers() }
        }
    }
}