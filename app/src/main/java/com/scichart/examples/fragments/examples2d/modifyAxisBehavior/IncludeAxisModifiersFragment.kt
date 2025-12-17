//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2025. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// IncludeAxisModifiersFragment.kt is part of SCICHART®, High Performance Scientific Charts
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

import com.scichart.charting.visuals.SciChartSurface
import com.scichart.charting.visuals.axes.AxisAlignment.Bottom
import com.scichart.charting.visuals.axes.AxisAlignment.Left
import com.scichart.charting.visuals.axes.AxisAlignment.Right
import com.scichart.data.model.DoubleRange
import com.scichart.drawing.utility.ColorUtil
import com.scichart.examples.R
import com.scichart.examples.data.DataManager
import com.scichart.examples.fragments.base.ExampleSingleChartBaseFragment
import com.scichart.examples.utils.Constant
import com.scichart.examples.utils.interpolator.DefaultInterpolator
import com.scichart.examples.utils.scichartExtensions.SolidPenStyle
import com.scichart.examples.utils.scichartExtensions.chartModifiers
import com.scichart.examples.utils.scichartExtensions.cursorModifier
import com.scichart.examples.utils.scichartExtensions.fastLineRenderableSeries
import com.scichart.examples.utils.scichartExtensions.numericAxis
import com.scichart.examples.utils.scichartExtensions.pinchZoomModifier
import com.scichart.examples.utils.scichartExtensions.renderableSeries
import com.scichart.examples.utils.scichartExtensions.setTextColor
import com.scichart.examples.utils.scichartExtensions.suspendUpdates
import com.scichart.examples.utils.scichartExtensions.sweepAnimation
import com.scichart.examples.utils.scichartExtensions.xAxes
import com.scichart.examples.utils.scichartExtensions.xAxisDragModifier
import com.scichart.examples.utils.scichartExtensions.xyDataSeries
import com.scichart.examples.utils.scichartExtensions.yAxes
import com.scichart.examples.utils.scichartExtensions.yAxisDragModifier
import com.scichart.examples.utils.scichartExtensions.zoomExtentsModifier
import com.scichart.examples.utils.scichartExtensions.zoomPanModifier

/**
 * Example demonstrating the includeAxis API for modifiers.
 * This example shows how to selectively apply modifiers to specific axes in a multi-axis chart.
 */
class IncludeAxisModifiersFragment : ExampleSingleChartBaseFragment() {

    override fun showDefaultModifiersInToolbar(): Boolean = false

    override fun initExample(surface: SciChartSurface) {
        surface.theme = R.style.SciChart_NavyBlue

        val leftData = DataManager.getInstance().getFourierSeries(1.0, 0.1, 5000)
        val rightData = DataManager.getInstance().getDampedSinewave(3.0, 0.005, 5000, 10)

        val leftColor = ColorUtil.argb(0xFF, 0x47, 0xBD, 0xE6)
        val rightColor = ColorUtil.argb(0xFF, 0xAE, 0x41, 0x8D)

        surface.suspendUpdates {
            xAxes {
                numericAxis {
                    axisId = X_AXIS_ID
                    axisAlignment = Bottom
                    axisTitle = "X Axis (Shared)"
                    growBy = DoubleRange(0.1, 0.1)
                }
            }

            yAxes {
                numericAxis {
                    axisId = Y_LEFT_AXIS_ID
                    axisAlignment = Left
                    axisTitle = "Left Y Axis (Not Fixed)"
                    growBy = DoubleRange(0.1, 0.1)
                    tickLabelStyle = sciChartBuilder.newFont().withTextSize(14f)
                        .withTextColor(ColorUtil.argb(0xFF, 0x47, 0xBD, 0xE6)).build()
                    setTextColor(leftColor.toLong())
                }

                numericAxis {
                    axisId = Y_RIGHT_AXIS_ID
                    axisAlignment = Right
                    axisTitle = "Right Y Axis (Fixed)"
                    growBy = DoubleRange(0.1, 0.1)
                    tickLabelStyle = sciChartBuilder.newFont().withTextSize(14f)
                        .withTextColor(ColorUtil.argb(0xFF, 0xAE, 0x41, 0x8D)).build()
                    setTextColor(rightColor.toLong())
                }
            }

            renderableSeries {
                fastLineRenderableSeries {
                    xAxisId = X_AXIS_ID
                    yAxisId = Y_LEFT_AXIS_ID
                    strokeStyle = SolidPenStyle(leftColor, 2f)
                    xyDataSeries<Double, Double> {
                        append(leftData.xValues, leftData.yValues)
                        seriesName = "leftData"
                    }

                    sweepAnimation {
                        duration = Constant.ANIMATION_DURATION
                        startDelay = Constant.ANIMATION_START_DELAY
                        interpolator = DefaultInterpolator.getInterpolator()
                    }
                }

                fastLineRenderableSeries {
                    xAxisId = X_AXIS_ID
                    yAxisId = Y_RIGHT_AXIS_ID
                    strokeStyle = SolidPenStyle(rightColor, 2f)
                    xyDataSeries<Double, Double> {
                        append(rightData.xValues, rightData.yValues)
                        seriesName = "rightData"
                    }

                    sweepAnimation {
                        duration = Constant.ANIMATION_DURATION
                        startDelay = Constant.ANIMATION_START_DELAY
                        interpolator = DefaultInterpolator.getInterpolator()
                    }
                }
            }

            chartModifiers {
                // ZoomPanModifier
                zoomPanModifier {
                    receiveHandledEvents = true
                    includeYAxis(surface.yAxes.first { it.axisId == Y_RIGHT_AXIS_ID }, false)
                }

                // PinchZoomModifier ONLY works on LEFT Y AXIS
                // This demonstrates selective axis inclusion
                pinchZoomModifier {
                    receiveHandledEvents = true
                    // Include only the left Y axis
                    includeYAxis(surface.yAxes.first { it.axisId == Y_LEFT_AXIS_ID }, true)
                }

                // Y Axis Drag Modifier that ONLY works on LEFT Y AXIS
                yAxisDragModifier {
                    // Include only the left Y axis
                    includeYAxis(surface.yAxes.first { it.axisId == Y_LEFT_AXIS_ID }, true)
                }

                // X Axis Drag Modifier (works on X axis by default)
                xAxisDragModifier()

                // Zoom Extents Modifier that EXCLUDES the RIGHT Y AXIS
                // This demonstrates selective axis exclusion
                zoomExtentsModifier()

                cursorModifier {
                    includeRenderableSeries(
                        surface.renderableSeries.first { it.yAxisId == Y_RIGHT_AXIS_ID },
                        false
                    )
                }
            }

            // Note: Annotations would be added here but require additional setup in Kotlin DSL
        }
    }

    companion object {
        private const val X_AXIS_ID = "xAxis"
        private const val Y_LEFT_AXIS_ID = "yLeftAxis"
        private const val Y_RIGHT_AXIS_ID = "yRightAxis"
    }
}

