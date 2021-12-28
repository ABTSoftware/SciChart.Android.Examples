//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2021. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// StackedBarChartFragment.kt is part of SCICHART®, High Performance Scientific Charts
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

package com.scichart.examples.fragments.examples2d.multiChart.kt

import android.view.animation.DecelerateInterpolator
import com.scichart.charting.visuals.SciChartSurface
import com.scichart.charting.visuals.axes.AxisAlignment.Bottom
import com.scichart.charting.visuals.axes.AxisAlignment.Left
import com.scichart.examples.fragments.base.ExampleSingleChartBaseFragment
import com.scichart.examples.utils.scichartExtensions.*

class StackedBarChartFragment: ExampleSingleChartBaseFragment() {
    override fun initExample(surface: SciChartSurface) {
        surface.suspendUpdates {
            xAxes { numericAxis { axisAlignment = Left } }
            yAxes { numericAxis { axisAlignment = Bottom; flipCoordinates = true} }

            renderableSeries {
                verticallyStackedColumnsCollection {
                    stackedColumnRenderableSeries {
                        xyDataSeries<Double, Double>("Data 1") {
                            for (i in yValues1.indices) append(i.toDouble(), yValues1[i])
                        }
                        dataPointWidth = 0.8
                        fillBrushStyle = LinearGradientBrushStyle(0xff567893, 0xff3D5568)
                        strokeStyle = SolidPenStyle(0xff567893, 0f)

                        waveAnimation { interpolator = DecelerateInterpolator() }
                    }
                    stackedColumnRenderableSeries {
                        xyDataSeries<Double, Double>("Data 2") {
                            for (i in yValues2.indices) append(i.toDouble(), yValues2[i])
                        }
                        dataPointWidth = 0.8
                        fillBrushStyle = LinearGradientBrushStyle(0xffACBCCA, 0xff439AAF)
                        strokeStyle = SolidPenStyle(0xffACBCCA, 0f)

                        waveAnimation { interpolator = DecelerateInterpolator() }
                    }
                    stackedColumnRenderableSeries {
                        xyDataSeries<Double, Double>("Data 3") {
                            for (i in yValues3.indices) append(i.toDouble(), yValues3[i])
                        }
                        dataPointWidth = 0.8
                        fillBrushStyle = LinearGradientBrushStyle(0xffDBE0E1, 0xffB6C1C3)
                        strokeStyle = SolidPenStyle(0xffDBE0E1, 0f)

                        waveAnimation { interpolator = DecelerateInterpolator() }
                    }
                }
            }

            chartModifiers {
                cursorModifier()
                zoomExtentsModifier()
            }
        }
    }

    companion object {
        var yValues1 = doubleArrayOf(0.0, 0.1, 0.2, 0.4, 0.8, 1.1, 1.5, 2.4, 4.6, 8.1, 11.7, 14.4, 16.0, 13.7, 10.1, 6.4, 3.5, 2.5, 5.4, 6.4, 7.1, 8.0, 9.0)
        var yValues2 = doubleArrayOf(2.0, 10.1, 10.2, 10.4, 10.8, 1.1, 11.5, 3.4, 4.6, 0.1, 1.7, 14.4, 16.0, 13.7, 10.1, 6.4, 3.5, 2.5, 1.4, 0.4, 10.1, 0.0, 0.0)
        var yValues3 = doubleArrayOf(20.0, 4.1, 4.2, 10.4, 10.8, 1.1, 11.5, 3.4, 4.6, 5.1, 5.7, 14.4, 16.0, 13.7, 10.1, 6.4, 3.5, 2.5, 1.4, 10.4, 8.1, 10.0, 15.0)
    }
}