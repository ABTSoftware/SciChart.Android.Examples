//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2021. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// MultipleXAxesFragment.kt is part of SCICHART®, High Performance Scientific Charts
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
import com.scichart.charting.model.dataSeries.IXyDataSeries
import com.scichart.charting.modifiers.SourceMode.AllSeries
import com.scichart.charting.visuals.SciChartSurface
import com.scichart.charting.visuals.axes.AxisAlignment.*
import com.scichart.core.model.DoubleValues
import com.scichart.data.model.DoubleRange
import com.scichart.examples.fragments.base.ExampleSingleChartBaseFragment
import com.scichart.examples.utils.Constant
import com.scichart.examples.utils.interpolator.DefaultInterpolator
import com.scichart.examples.utils.scichartExtensions.*
import java.util.*

class MultipleXAxesFragment: ExampleSingleChartBaseFragment() {
    override fun initExample(surface: SciChartSurface) {

        surface.suspendUpdates {
            xAxes {
                numericAxis { axisAlignment = Top; axisId = X_TOP_AXIS; setTextColor(0xFFAE418D) }
                numericAxis { axisAlignment = Bottom; axisId = X_BOTTOM_AXIS; setTextColor(0xFF47BDE6) }
            }
            yAxes {
                numericAxis {
                    axisAlignment = Left
                    axisId = Y_LEFT_AXIS
                    growBy = DoubleRange(0.1, 0.1)
                    textFormatting = "#.0"
                    setTextColor(0xFF68BCAE)
                }
                numericAxis {
                    axisAlignment = Right
                    axisId = Y_RIGHT_AXIS
                    growBy = DoubleRange(0.1, 0.1)
                    textFormatting = "#.0"
                    setTextColor(0xFFE97064)
                }
            }

            renderableSeries {
                fastLineRenderableSeries {
                    xAxisId = X_BOTTOM_AXIS
                    yAxisId = Y_LEFT_AXIS
                    strokeStyle = SolidPenStyle(0xFF47BDE6)
                    xyDataSeries<Double, Double>("Line 1") { fillDataSeries(this) }

                    sweepAnimation {
                        duration = Constant.ANIMATION_DURATION
                        startDelay = Constant.ANIMATION_START_DELAY
                        interpolator = DefaultInterpolator.getInterpolator()
                    }
                }
                fastLineRenderableSeries {
                    xAxisId = X_BOTTOM_AXIS
                    yAxisId = Y_LEFT_AXIS
                    strokeStyle = SolidPenStyle(0xFFAE418D)
                    xyDataSeries<Double, Double>("Line 2") { fillDataSeries(this) }

                    sweepAnimation {
                        duration = Constant.ANIMATION_DURATION
                        startDelay = Constant.ANIMATION_START_DELAY
                        interpolator = DefaultInterpolator.getInterpolator()
                    }
                }
                fastLineRenderableSeries {
                    xAxisId = X_TOP_AXIS
                    yAxisId = Y_RIGHT_AXIS
                    strokeStyle = SolidPenStyle(0xFF68BCAE)
                    xyDataSeries<Double, Double>("Line 3") { fillDataSeries(this) }

                    sweepAnimation {
                        duration = Constant.ANIMATION_DURATION
                        startDelay = Constant.ANIMATION_START_DELAY
                        interpolator = DefaultInterpolator.getInterpolator()
                    }
                }
                fastLineRenderableSeries {
                    xAxisId = X_TOP_AXIS
                    yAxisId = Y_RIGHT_AXIS
                    strokeStyle = SolidPenStyle(0xFFE97064)
                    xyDataSeries<Double, Double>("Line 4") { fillDataSeries(this) }

                    sweepAnimation {
                        duration = Constant.ANIMATION_DURATION
                        startDelay = Constant.ANIMATION_START_DELAY
                        interpolator = DefaultInterpolator.getInterpolator()
                    }
                }
            }

            chartModifiers {
                defaultModifiers()
                legendModifier { setSourceMode(AllSeries) }
                xAxisDragModifier { receiveHandledEvents = true }
                yAxisDragModifier { receiveHandledEvents = true }
            }
        }
    }

    private fun fillDataSeries(dataSeries: IXyDataSeries<Double, Double>) {
        val xValues = DoubleValues()
        val yValues = DoubleValues()

        var randomWalk = 10.0
        for (i in 0 until COUNT) {
            randomWalk += random.nextDouble() - 0.498
            xValues.add(i.toDouble())
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