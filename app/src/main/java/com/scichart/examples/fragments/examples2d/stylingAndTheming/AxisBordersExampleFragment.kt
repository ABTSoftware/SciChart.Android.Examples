//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2021. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// AxisBordersExampleFragment.kt is part of SCICHART®, High Performance Scientific Charts
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

package com.scichart.examples.fragments.examples2d.stylingAndTheming.kt

import com.scichart.charting.visuals.SciChartSurface
import com.scichart.charting.visuals.axes.AxisAlignment
import com.scichart.data.model.DoubleRange
import com.scichart.drawing.common.AxisBorderStyle
import com.scichart.drawing.common.FontStyle
import com.scichart.drawing.common.SolidPenStyle
import com.scichart.examples.fragments.base.ExampleSingleChartBaseFragment
import com.scichart.examples.utils.scichartExtensions.*
import kotlin.math.sin

class AxisBordersExampleFragment : ExampleSingleChartBaseFragment() {

    companion object {
        private val PRIMARY_COLORS = intArrayOf(
            0xFF4FBEE6.toInt(),
            0xFFAD3D8D.toInt(),
            0xFF6BBDAE.toInt(),
            0xFFE76E63.toInt(),
            0xFF2C4B92.toInt()
        )
    }

    override fun initExample(surface: SciChartSurface) {
        var axisBorderThickness = 3f
        val tickSize = 16f
        val tickThickness = 3f
        val axisTitleSize = 48
        val labelSize = 32

        surface.suspendUpdates {
            xAxes {
                numericAxis {
                    axisId = "xAxis1"
                    axisTitle = "X Axis"
                    axisAlignment = AxisAlignment.Bottom
                    drawMajorBands = false
                    drawMajorGridLines = true
                    drawMinorGridLines = false
                    drawMajorTicks = true
                    visibleRange = DoubleRange(-10.0, 110.0)
                    isPrimaryAxis = true
                }

                numericAxis {
                    axisId = "xAxis2"
                    axisTitle = "Flipped X Axis"
                    axisAlignment = AxisAlignment.Bottom
                    flipCoordinates = true
                    drawMajorBands = false
                    drawMajorGridLines = false
                    drawMinorGridLines = false
                    drawMajorTicks = true
                    visibleRange = DoubleRange(-10.0, 110.0)
                }

                numericAxis {
                    axisId = "xAxis3"
                    axisTitle = "Stacked X Axis"
                    axisAlignment = AxisAlignment.Right
                    drawMajorBands = false
                    drawMajorGridLines = false
                    drawMinorGridLines = false
                    drawMajorTicks = true
                    visibleRange = DoubleRange(-10.0, 110.0)
                }
            }

            yAxes {
                numericAxis {
                    axisId = "yAxis1"
                    axisTitle = "Flipped Y Axis - Left Aligned"
                    axisAlignment = AxisAlignment.Left
                    flipCoordinates = true
                    drawMajorBands = false
                    drawMajorGridLines = true
                    drawMinorGridLines = false
                    drawMajorTicks = true
                    visibleRange = DoubleRange(-10.0, 140.0)
                    isPrimaryAxis = true
                }

                numericAxis {
                    axisId = "yAxis2"
                    axisTitle = "Stacked Y Axis"
                    axisAlignment = AxisAlignment.Left
                    drawMajorBands = false
                    drawMajorGridLines = false
                    drawMinorGridLines = false
                    drawMajorTicks = true
                    visibleRange = DoubleRange(-10.0, 140.0)
                }

                numericAxis {
                    axisId = "yAxis3"
                    axisTitle = "Y Axis - Top Aligned"
                    axisAlignment = AxisAlignment.Top
                    drawMajorBands = false
                    drawMajorGridLines = false
                    drawMinorGridLines = false
                    drawMajorTicks = true
                    visibleRange = DoubleRange(-10.0, 140.0)
                }
            }

            val xAxisList = this@suspendUpdates.xAxes
            val yAxisList = this@suspendUpdates.yAxes

            renderableSeries {
                for (i in 0 until minOf(xAxisList.size, yAxisList.size, PRIMARY_COLORS.size)) {
                    val color = PRIMARY_COLORS[i]
                    val xAxis = xAxisList[i]
                    val yAxis = yAxisList[i]

                    xAxis.setAxisBorderStyle(AxisBorderStyle(color, true, axisBorderThickness++))
                    yAxis.setAxisBorderStyle(AxisBorderStyle(color, true, axisBorderThickness++))

                    val tickPenStyle = SolidPenStyle(color, true, tickThickness, null)
                    xAxis.setMajorTickLineStyle(tickPenStyle)
                    yAxis.setMajorTickLineStyle(tickPenStyle)
                    xAxis.setMajorTickLineLength(tickSize)
                    yAxis.setMajorTickLineLength(tickSize)

                    val titleStyle = FontStyle(axisTitleSize.toFloat(), color)
                    xAxis.setTitleStyle(titleStyle)
                    yAxis.setTitleStyle(titleStyle)

                    val labelStyle = FontStyle(labelSize.toFloat(), color)
                    xAxis.setTickLabelStyle(labelStyle)
                    yAxis.setTickLabelStyle(labelStyle)

                    fastLineRenderableSeries {
                        xyDataSeries<Double, Double> {
                            for (j in 0 until 100) {
                                val x = j.toDouble()
                                val y = sin(j * 0.1) * j + 50
                                append(x, y)
                            }
                        }
                        strokeStyle = sciChartBuilder.newPen()
                            .withColor(color)
                            .withThickness(3f)
                            .build()
                        xAxisId = xAxis.axisId
                        yAxisId = yAxis.axisId
                    }
                }
            }

            chartModifiers {
                defaultModifiers()
            }
        }
    }
}
