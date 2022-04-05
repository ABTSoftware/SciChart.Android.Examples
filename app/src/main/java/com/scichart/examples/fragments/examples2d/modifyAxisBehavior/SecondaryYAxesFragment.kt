//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2021. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// SecondaryYAxesFragment.kt is part of SCICHART®, High Performance Scientific Charts
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
import com.scichart.charting.visuals.axes.AxisAlignment.*
import com.scichart.data.model.DoubleRange
import com.scichart.examples.data.DataManager
import com.scichart.examples.fragments.base.ExampleSingleChartBaseFragment
import com.scichart.examples.utils.scichartExtensions.*

class SecondaryYAxesFragment: ExampleSingleChartBaseFragment() {

    override fun showDefaultModifiersInToolbar(): Boolean = false

    override fun initExample(surface: SciChartSurface) {
        val ds1Points = DataManager.getInstance().getFourierSeries(1.0, 0.1, 5000)
        val ds2Points = DataManager.getInstance().getDampedSinewave(3.0, 0.005, 5000, 10)

        surface.suspendUpdates {
            xAxes {
                numericAxis {
                    growBy = DoubleRange(0.1, 0.1)
                    axisId = X_BOTTOM_AXIS
                    axisAlignment = Bottom
                    axisTitle = "Bottom Axis"
                }
            }
            yAxes {
                numericAxis {
                    growBy = DoubleRange(0.1, 0.1)
                    axisId = Y_LEFT_AXIS
                    axisAlignment = Left
                    axisTitle = "Left Axis"
                    setTextColor(0xFF4083B7)
                }

                numericAxis {
                    growBy = DoubleRange(0.1, 0.1)
                    axisId = Y_RIGHT_AXIS
                    axisAlignment = Right
                    axisTitle = "Right Axis"
                    setTextColor(0xFF279B27)
                }
            }

            renderableSeries {
                fastLineRenderableSeries {
                    xAxisId = X_BOTTOM_AXIS
                    yAxisId = Y_RIGHT_AXIS
                    strokeStyle = SolidPenStyle(0xFF279B27)
                    xyDataSeries<Double, Double> { append(ds1Points.xValues, ds1Points.yValues) }

                    sweepAnimation { interpolator = DecelerateInterpolator() }
                }
                fastLineRenderableSeries {
                    xAxisId = X_BOTTOM_AXIS
                    yAxisId = Y_LEFT_AXIS
                    strokeStyle = SolidPenStyle(0xFF4083B7)
                    xyDataSeries<Double, Double> { append(ds2Points.xValues, ds2Points.yValues) }

                    sweepAnimation { interpolator = DecelerateInterpolator() }
                }
            }

            chartModifiers { defaultModifiers() }
        }
    }

    companion object {
        private const val X_BOTTOM_AXIS = "xBottomAxis"
        private const val Y_LEFT_AXIS = "yLeftAxis"
        private const val Y_RIGHT_AXIS = "yRightAxis"
    }
}