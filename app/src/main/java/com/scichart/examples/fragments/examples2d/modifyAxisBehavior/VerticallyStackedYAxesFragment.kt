//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2021. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// VerticallyStackedYAxesFragment.kt is part of SCICHART®, High Performance Scientific Charts
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
import android.view.animation.LinearInterpolator
import com.scichart.charting.layoutManagers.ChartLayoutState
import com.scichart.charting.layoutManagers.DefaultLayoutManager
import com.scichart.charting.layoutManagers.VerticalAxisLayoutStrategy
import com.scichart.charting.visuals.SciChartSurface
import com.scichart.charting.visuals.axes.AutoRange.Never
import com.scichart.charting.visuals.axes.AxisAlignment.Bottom
import com.scichart.charting.visuals.axes.AxisAlignment.Left
import com.scichart.data.model.DoubleRange
import com.scichart.examples.R
import com.scichart.examples.data.DataManager
import com.scichart.examples.fragments.base.ExampleSingleChartBaseFragment
import com.scichart.examples.utils.Constant
import com.scichart.examples.utils.interpolator.DefaultInterpolator
import com.scichart.examples.utils.scichartExtensions.*
import kotlin.math.max

class VerticallyStackedYAxesFragment: ExampleSingleChartBaseFragment() {

    override fun showDefaultModifiersInToolbar(): Boolean = false

    override fun initExample(surface: SciChartSurface) {
        surface.theme = R.style.SciChart_NavyBlue

        val colors = arrayListOf(0xFF47bde6, 0xFFe97064, 0xFF47bde6, 0xFFe97064, 0xFF274b92)

        surface.suspendUpdates {
            layoutManager = DefaultLayoutManager.Builder().setLeftOuterAxesLayoutStrategy(LeftAlignedOuterVerticallyStackedYAxisLayoutStrategy()).build()

            xAxes { numericAxis { axisAlignment = Bottom } }
            chartModifiers { defaultModifiers() }

            for (i in 0 until 5) {
                yAxes {
                    numericAxis {
                        axisId = "Ch$i"
                        axisAlignment = Left
                        visibleRange = DoubleRange(-2.0, 2.0)
                        autoRange = Never
                        drawMajorBands = false
                        drawMajorGridLines = false
                        drawMinorGridLines = false
                    }
                }

                renderableSeries {
                    fastLineRenderableSeries {
                        yAxisId = "Ch$i"
                        strokeStyle = SolidPenStyle(colors[i])
                        xyDataSeries<Double, Double> {
                            DataManager.getInstance().getSinewave(3.0, i.toDouble(), 1000).run {
                                append(xValues, yValues)
                            }
                        }

                        sweepAnimation {
                            duration = Constant.ANIMATION_DURATION
                            startDelay = Constant.ANIMATION_START_DELAY
                            interpolator = DefaultInterpolator.getInterpolator()
                        }
                    }
                }
            }
        }
    }

    private class LeftAlignedOuterVerticallyStackedYAxisLayoutStrategy : VerticalAxisLayoutStrategy() {
        override fun measureAxes(availableWidth: Int, availableHeight: Int, chartLayoutState: ChartLayoutState) {
            for (axis in axes) {
                axis.updateAxisMeasurements()
                chartLayoutState.leftOuterAreaSize = max(getRequiredAxisSize(axis.axisLayoutState), chartLayoutState.leftOuterAreaSize)
            }
        }

        override fun layoutAxes(left: Int, top: Int, right: Int, bottom: Int) {
            val size = axes.size
            val height = bottom - top
            val axisSize = height / size

            var topPlacement = top
            for (i in 0 until size) {
                val axis = axes[i]
                val axisLayoutState = axis.axisLayoutState
                val bottomPlacement = topPlacement + axisSize

                axis.layoutArea(
                    right - getRequiredAxisSize(axisLayoutState) + axisLayoutState.additionalLeftSize,
                    topPlacement,
                    right - axisLayoutState.additionalRightSize, bottomPlacement
                )
                topPlacement = bottomPlacement
            }
        }
    }
}