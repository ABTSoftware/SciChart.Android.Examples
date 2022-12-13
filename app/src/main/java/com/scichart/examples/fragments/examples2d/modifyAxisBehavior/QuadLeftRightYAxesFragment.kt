//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2021. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// QuadLeftRightYAxesFragment.kt is part of SCICHART®, High Performance Scientific Charts
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

import com.scichart.charting.model.dataSeries.IXyDataSeries
import com.scichart.charting.visuals.SciChartSurface
import com.scichart.charting.visuals.axes.AxisAlignment.*
import com.scichart.core.model.DoubleValues
import com.scichart.data.model.DoubleRange
import com.scichart.examples.R
import com.scichart.examples.fragments.base.ExampleSingleChartBaseFragment
import com.scichart.examples.utils.scichartExtensions.*
import java.util.*

class QuadLeftRightYAxesFragment : ExampleSingleChartBaseFragment() {

    override fun showDefaultModifiersInToolbar(): Boolean = false

    override fun initExample(surface: SciChartSurface) {
        surface.theme = R.style.SciChart_NavyBlue

        surface.suspendUpdates {
            xAxes {
                numericAxis { axisAlignment = Bottom; growBy = DoubleRange(0.2, 0.2) }
            }
            yAxes {
                numericAxis {
                    axisAlignment = Left
                    axisId = Y_LEFT_AXIS_1
                    growBy = DoubleRange(0.1, 0.1)
                    setTextColor(0xFFFF1919)
                    drawMajorTicks = false
                    drawMinorTicks = false
                }
                numericAxis {
                    axisAlignment = Left
                    axisId = Y_LEFT_AXIS_2
                    growBy = DoubleRange(0.1, 0.1)
                    setTextColor(0xFFCCCCCC)
                    drawMajorTicks = false
                    drawMinorTicks = false
                }
                numericAxis {
                    axisAlignment = Left
                    axisId = Y_LEFT_AXIS_3
                    growBy = DoubleRange(0.1, 0.1)
                    setTextColor(0xFFFC9C29)
                    drawMajorTicks = false
                    drawMinorTicks = false
                    setIsCenterAxis(true)
                }
                numericAxis {
                    axisAlignment = Left
                    axisId = Y_LEFT_AXIS_4
                    growBy = DoubleRange(0.1, 0.1)
                    setTextColor(0xFF4083B7)
                    drawMajorTicks = false
                    drawMinorTicks = false
                    setIsCenterAxis(true)
                }
                numericAxis {
                    axisAlignment = Right
                    axisId = Y_RIGHT_AXIS_1
                    growBy = DoubleRange(0.1, 0.1)
                    setTextColor(0xFF4083B7)
                    drawMajorTicks = false
                    drawMinorTicks = false
                }
                numericAxis {
                    axisAlignment = Right
                    axisId = Y_RIGHT_AXIS_2
                    growBy = DoubleRange(0.1, 0.1)
                    setTextColor(0xFF279B27)
                    drawMajorTicks = false
                    drawMinorTicks = false
                }
                numericAxis {
                    axisAlignment = Right
                    axisId = Y_RIGHT_AXIS_3
                    growBy = DoubleRange(0.1, 0.1)
                    setTextColor(0xFFFF1919)
                    drawMajorTicks = false
                    drawMinorTicks = false
                    setIsCenterAxis(true)
                }
                numericAxis {
                    axisAlignment = Right
                    axisId = Y_RIGHT_AXIS_4
                    growBy = DoubleRange(0.1, 0.1)
                    setTextColor(0xFFCCCCCC)
                    drawMajorTicks = false
                    drawMinorTicks = false
                    setIsCenterAxis(true)
                }
            }

            renderableSeries {
                fastLineRenderableSeries {
                    xyDataSeries<Double, Double>("Red line") { fillDataSeries(this) }
                    yAxisId = Y_LEFT_AXIS_1
                    strokeStyle = SolidPenStyle(0xFFFF1919)
                }
                fastLineRenderableSeries {
                    xyDataSeries<Double, Double>("Black line") { fillDataSeries(this) }
                    yAxisId = Y_LEFT_AXIS_2
                    strokeStyle = SolidPenStyle(0xFFCCCCCC)
                }
                fastLineRenderableSeries {
                    xyDataSeries<Double, Double>("Orange line") { fillDataSeries(this) }
                    yAxisId = Y_LEFT_AXIS_3
                    strokeStyle = SolidPenStyle(0xFFFC9C29)
                }
                fastLineRenderableSeries {
                    xyDataSeries<Double, Double>("Blue line") { fillDataSeries(this) }
                    yAxisId = Y_LEFT_AXIS_4
                    strokeStyle = SolidPenStyle(0xFF4083B7)
                }
                fastLineRenderableSeries {
                    xyDataSeries<Double, Double>("Another Blue") { fillDataSeries(this) }
                    yAxisId = Y_RIGHT_AXIS_1
                    strokeStyle = SolidPenStyle(0xFF4083B7)
                }
                fastLineRenderableSeries {
                    xyDataSeries<Double, Double>("Green line") { fillDataSeries(this) }
                    yAxisId = Y_RIGHT_AXIS_2
                    strokeStyle = SolidPenStyle(0xFF279B27)
                }
                fastLineRenderableSeries {
                    xyDataSeries<Double, Double>("Another Red") { fillDataSeries(this) }
                    yAxisId = Y_RIGHT_AXIS_3
                    strokeStyle = SolidPenStyle(0xFFFF1919)
                }
                fastLineRenderableSeries {
                    xyDataSeries<Double, Double>("Another Black") { fillDataSeries(this) }
                    yAxisId = Y_RIGHT_AXIS_4
                    strokeStyle = SolidPenStyle(0xFFCCCCCC)
                }
            }

            chartModifiers { defaultModifiers() }
        }
    }

    private fun fillDataSeries(dataSeries: IXyDataSeries<Double, Double>) {
        val xValues = DoubleValues()
        val yValues = DoubleValues()

        var randomWalk = 5.0
        for (i in 0 until COUNT) {
            randomWalk += random.nextDouble() - 0.498
            xValues.add(i.toDouble())
            yValues.add(randomWalk)
        }
        dataSeries.append(xValues, yValues)
    }

    companion object {
        private const val Y_LEFT_AXIS_1 = "yLeftAxis1"
        private const val Y_LEFT_AXIS_2 = "yLeftAxis2"
        private const val Y_LEFT_AXIS_3 = "yLeftAxis3"
        private const val Y_LEFT_AXIS_4 = "yLeftAxis4"
        private const val Y_RIGHT_AXIS_1 = "yRightAxis1"
        private const val Y_RIGHT_AXIS_2 = "yRightAxis2"
        private const val Y_RIGHT_AXIS_3 = "yRightAxis3"
        private const val Y_RIGHT_AXIS_4 = "yRightAxis4"

        private const val COUNT = 50
        private val random = Random(251916)
    }
}