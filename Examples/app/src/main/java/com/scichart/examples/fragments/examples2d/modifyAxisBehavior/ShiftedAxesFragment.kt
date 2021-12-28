//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2021. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// ShiftedAxesFragment.kt is part of SCICHART®, High Performance Scientific Charts
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

import com.scichart.charting.layoutManagers.DefaultLayoutManager
import com.scichart.charting.layoutManagers.DefaultLayoutManager.Builder
import com.scichart.charting.layoutManagers.ILayoutManager
import com.scichart.charting.layoutManagers.LeftAlignmentInnerAxisLayoutStrategy
import com.scichart.charting.layoutManagers.TopAlignmentInnerAxisLayoutStrategy
import com.scichart.charting.visuals.SciChartSurface
import com.scichart.charting.visuals.axes.AxisAlignment
import com.scichart.charting.visuals.axes.AxisAlignment.Left
import com.scichart.charting.visuals.axes.AxisAlignment.Top
import com.scichart.charting.visuals.axes.IAxis
import com.scichart.core.IServiceContainer
import com.scichart.core.common.Size
import com.scichart.data.model.DoubleRange
import com.scichart.examples.data.DataManager
import com.scichart.examples.fragments.base.ExampleSingleChartBaseFragment
import com.scichart.examples.utils.scichartExtensions.*

class ShiftedAxesFragment : ExampleSingleChartBaseFragment() {

    override fun showDefaultModifiersInToolbar(): Boolean = false

    override fun initExample(surface: SciChartSurface) {
        val butterflyCurve = DataManager.getInstance().getButterflyCurve(20000)

        surface.suspendUpdates {
            xAxes { numericAxis {
                axisAlignment = Top
                drawMajorTicks = false
                majorTickLineStyle = SolidPenStyle(0xFFFFFFFF, 2f)
                setIsCenterAxis(true)
                growBy = DoubleRange(0.1, 0.1)
                textFormatting = "0.0"
            }}
            yAxes { numericAxis {
                axisAlignment = Left
                drawMajorTicks = false
                majorTickLineStyle = SolidPenStyle(0xFFFFFFFF, 2f)
                setIsCenterAxis(true)
                growBy = DoubleRange(0.1, 0.1)
                textFormatting = "0.0"
            }}
            renderableSeries {
                fastLineRenderableSeries {
                    xyDataSeries<Double, Double> {
                        acceptsUnsortedData = true
                        append(butterflyCurve.xValues, butterflyCurve.yValues)
                    }

                    sweepAnimation { duration = 20000 }
                }
            }
            chartModifiers { defaultModifiers() }
        }
    }

    private class CenterLayoutManager(xAxis: IAxis, yAxis: IAxis) : ILayoutManager {
        private var isFirstLayout = false

        // need to override default inner layout strategies for bottom and right aligned axes
        // because xAxis has right axis alignment and yAxis has bottom axis alignment
        private val defaultLayoutManager: DefaultLayoutManager = Builder()
            .setLeftInnerAxesLayoutStrategy(CenteredLeftAlignmentInnerAxisLayoutStrategy(xAxis))
            .setTopInnerAxesLayoutStrategy(CenteredTopAlignmentInnerAxisLayoutStrategy(yAxis))
            .build()

        override fun attachAxis(axis: IAxis, isXAxis: Boolean) {
            defaultLayoutManager.attachAxis(axis, isXAxis)
        }

        override fun detachAxis(axis: IAxis) {
            defaultLayoutManager.detachAxis(axis)
        }

        override fun onAxisPlacementChanged(axis: IAxis, oldAxisAlignment: AxisAlignment, oldIsCenterAxis: Boolean, newAxisAlignment: AxisAlignment, newIsCenterAxis: Boolean) {
            defaultLayoutManager.onAxisPlacementChanged(axis, oldAxisAlignment, oldIsCenterAxis, newAxisAlignment, newIsCenterAxis)
        }

        override fun attachTo(services: IServiceContainer) {
            defaultLayoutManager.attachTo(services)

            // need to perform 2 layout passes during first layout of chart
            isFirstLayout = true
        }

        override fun detach() {
            defaultLayoutManager.detach()
        }

        override fun isAttached(): Boolean {
            return defaultLayoutManager.isAttached
        }

        override fun onLayoutChart(width: Int, height: Int): Size {
            // need to perform additional layout pass if it is a first layout pass
            // because we don't know correct size of axes during first layout pass
            if (isFirstLayout) {
                defaultLayoutManager.onLayoutChart(width, height)
                isFirstLayout = false
            }

            return defaultLayoutManager.onLayoutChart(width, height)
        }
    }

    private class CenteredTopAlignmentInnerAxisLayoutStrategy(private val yAxis: IAxis) : TopAlignmentInnerAxisLayoutStrategy() {
        override fun layoutAxes(left: Int, top: Int, right: Int, bottom: Int) {
            // find the coordinate of 0 on the Y Axis in pixels
            // place the stack of the top-aligned X Axes at this coordinate
            val topCoord = yAxis.currentCoordinateCalculator.getCoordinate(0.0)
            layoutFromTopToBottom(left, topCoord.toInt(), right, axes)
        }
    }

    private class CenteredLeftAlignmentInnerAxisLayoutStrategy(private val xAxis: IAxis) : LeftAlignmentInnerAxisLayoutStrategy() {
        override fun layoutAxes(left: Int, top: Int, right: Int, bottom: Int) {
            // find the coordinate of 0 on the X Axis in pixels
            // place the stack of the left-aligned Y Axes at this coordinate
            val leftCoord = xAxis.currentCoordinateCalculator.getCoordinate(0.0)
            layoutFromLeftToRight(leftCoord.toInt(), top, bottom, axes)
        }
    }
}