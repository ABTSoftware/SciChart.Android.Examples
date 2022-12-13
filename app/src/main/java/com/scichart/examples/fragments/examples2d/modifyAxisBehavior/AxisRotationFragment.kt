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
import android.view.animation.DecelerateInterpolator
import com.scichart.charting.model.dataSeries.IXyDataSeries
import com.scichart.charting.modifiers.SourceMode
import com.scichart.charting.visuals.SciChartSurface
import com.scichart.charting.visuals.axes.AxisAlignment
import com.scichart.charting.visuals.axes.AxisTickLabelStyle
import com.scichart.core.model.DateValues
import com.scichart.core.model.DoubleValues
import com.scichart.data.model.DoubleRange
import com.scichart.examples.fragments.base.ExampleSingleChartBaseFragment
import com.scichart.examples.utils.scichartExtensions.*
import java.util.*

class AxisRotationFragment : ExampleSingleChartBaseFragment() {
    override fun initExample(surface: SciChartSurface) {

        surface.suspendUpdates {
            xAxes {
                dateAxis {
                    axisAlignment = AxisAlignment.Top;
                    axisId = X_TOP_AXIS;
                    setTextColor(0xFF279B27)
                    autoTicks = false
                    majorDelta = Date(20000000L)
                    minorDelta = Date(2000000L)
                    isLabelCullingEnabled = false
                    axisLabelRotation = 45
                    axisTickLabelStyle = AxisTickLabelStyle(
                        Gravity.RIGHT,
                        0,0,0,0
                    )
                }
                dateAxis {
                    axisAlignment = AxisAlignment.Bottom;
                    axisId = X_BOTTOM_AXIS;
                    setTextColor(0xFFFF1919)
                    autoTicks = false
                    majorDelta = Date(20000000L)
                    minorDelta = Date(2000000L)
                    isLabelCullingEnabled = false
                    axisLabelRotation = 45
                    axisTickLabelStyle = AxisTickLabelStyle(
                        Gravity.LEFT,
                        0,0,0,0
                    )
                }
            }
            yAxes {
                numericAxis {
                    axisAlignment = AxisAlignment.Left
                    axisId = Y_LEFT_AXIS
                    growBy = DoubleRange(0.1, 0.1)
                    textFormatting = "#.0"
                    setTextColor(0xFFFC9C29)
                }
                numericAxis {
                    axisAlignment = AxisAlignment.Right
                    axisId = Y_RIGHT_AXIS
                    growBy = DoubleRange(0.1, 0.1)
                    textFormatting = "#.0"
                    setTextColor(0xFF4083B7)
                }
            }

            renderableSeries {
                fastLineRenderableSeries {
                    xAxisId = X_BOTTOM_AXIS
                    yAxisId = Y_LEFT_AXIS
                    strokeStyle = SolidPenStyle(0xFFFF1919)
                    xyDataSeries<Date, Double>("Red Line") { fillDataSeries(this) }

                    sweepAnimation { interpolator = DecelerateInterpolator() }
                }
                fastLineRenderableSeries {
                    xAxisId = X_BOTTOM_AXIS
                    yAxisId = Y_LEFT_AXIS
                    strokeStyle = SolidPenStyle(0xFF279B27)
                    xyDataSeries<Date, Double>("Green Line") { fillDataSeries(this) }

                    sweepAnimation { interpolator = DecelerateInterpolator() }
                }
                fastLineRenderableSeries {
                    xAxisId = X_TOP_AXIS
                    yAxisId = Y_RIGHT_AXIS
                    strokeStyle = SolidPenStyle(0xFFFC9C29)
                    xyDataSeries<Date, Double>("Orange Line") { fillDataSeries(this) }

                    sweepAnimation { interpolator = DecelerateInterpolator() }
                }
                fastLineRenderableSeries {
                    xAxisId = X_TOP_AXIS
                    yAxisId = Y_RIGHT_AXIS
                    strokeStyle = SolidPenStyle(0xFF4083B7)
                    xyDataSeries<Date, Double>("Blue Line") { fillDataSeries(this) }

                    sweepAnimation { interpolator = DecelerateInterpolator() }
                }
            }

            chartModifiers {
                defaultModifiers()
                legendModifier { setSourceMode(SourceMode.AllSeries) }
                xAxisDragModifier { receiveHandledEvents = true }
                yAxisDragModifier { receiveHandledEvents = true }
            }
        }
    }

    private fun fillDataSeries(dataSeries: IXyDataSeries<Date, Double>) {
        val xValues = DateValues()
        val yValues = DoubleValues()

        var randomWalk = 10.0
        for (i in 0 until COUNT) {
            randomWalk += random.nextDouble() - 0.498
            xValues.add(Date(1663047026485L + i * 1000000L))
            yValues.add(randomWalk)
        }
        dataSeries.append(xValues, yValues)
    }

    companion object {
        private const val X_TOP_AXIS = "xTopAxis"
        private const val X_BOTTOM_AXIS = "xBottomAxis"
        private const val Y_LEFT_AXIS = "yLeftAxis"
        private const val Y_RIGHT_AXIS = "yRightAxis"

        private const val COUNT = 150
        private val random = Random(251916)
    }

}