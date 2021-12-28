//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2021. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// StackedMountainChartFragment.kt is part of SCICHART®, High Performance Scientific Charts
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
import com.scichart.examples.fragments.base.ExampleSingleChartBaseFragment
import com.scichart.examples.utils.scichartExtensions.*

class StackedMountainChartFragment: ExampleSingleChartBaseFragment() {
    override fun initExample(surface: SciChartSurface) {
        val yValues1 = doubleArrayOf(4.0, 7.0, 5.2, 9.4, 3.8, 5.1, 7.5, 12.4, 14.6, 8.1, 11.7, 14.4, 16.0, 3.7, 5.1, 6.4, 3.5, 2.5, 12.4, 16.4, 7.1, 8.0, 9.0)
        val yValues2 = doubleArrayOf(15.0, 10.1, 10.2, 10.4, 10.8, 1.1, 11.5, 3.4, 4.6, 0.1, 1.7, 14.4, 6.0, 13.7, 10.1, 8.4, 8.5, 12.5, 1.4, 0.4, 10.1, 5.0, 1.0)

        surface.suspendUpdates {
            xAxes { numericAxis() }
            yAxes { numericAxis() }
            renderableSeries {
                verticallyStackedMountainsCollection {
                    stackedMountainRenderableSeries {
                        xyDataSeries<Double, Double> {
                            for (i in yValues1.indices) append(i.toDouble(), yValues1[i])
                        }
                        areaStyle = LinearGradientBrushStyle(0xDDDBE0E1, 0x88B6C1C3)

                        waveAnimation { interpolator = DecelerateInterpolator() }
                    }
                    stackedMountainRenderableSeries {
                        xyDataSeries<Double, Double> {
                            for (i in yValues2.indices) append(i.toDouble(), yValues1[i])
                        }
                        areaStyle = LinearGradientBrushStyle(0xDDACBCCA, 0x88439AAF)

                        waveAnimation { interpolator = DecelerateInterpolator() }
                    }
                }
            }

            chartModifiers { tooltipModifier() }
        }
    }
}