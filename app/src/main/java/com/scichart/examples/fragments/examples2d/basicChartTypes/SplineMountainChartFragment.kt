//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2021. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// SplineMountainChartFragment.kt is part of SCICHART®, High Performance Scientific Charts
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

package com.scichart.examples.fragments.examples2d.basicChartTypes.kt

import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator
import com.scichart.charting.visuals.SciChartSurface
import com.scichart.data.model.DoubleRange
import com.scichart.examples.fragments.base.ExampleSingleChartBaseFragment
import com.scichart.examples.utils.Constant
import com.scichart.examples.utils.interpolator.DefaultInterpolator
import com.scichart.examples.utils.scichartExtensions.*

class SplineMountainChartFragment : ExampleSingleChartBaseFragment() {

    override fun initExample(surface: SciChartSurface) {
        surface.suspendUpdates {
            xAxes { numericAxis { growBy = DoubleRange(0.1, 0.1) }}
            yAxes { numericAxis { growBy = DoubleRange(0.0, 0.2) }}
            renderableSeries {
                splineMountainRenderableSeries {
                    xyDataSeries<Int, Int> {
                        val yValues = intArrayOf(50, 35, 61, 58, 50, 50, 40, 53, 55, 23, 45, 12, 59, 60)
                        for (i in yValues.indices) {
                            append(i, yValues[i])
                        }
                    }
                    strokeStyle = SolidPenStyle(0xFF50C7E0, 2f)
                    areaStyle = LinearGradientBrushStyle(0xAA83D2F5, 0x0083D2F5)
                    ellipsePointMarker {
                        setSize(7)
                        strokeStyle = SolidPenStyle(0xFF50C7E0)
                        fillStyle = SolidBrushStyle(0xFFFFFFFF)
                    }

                    scaleAnimation {
                        duration = Constant.ANIMATION_DURATION
                        startDelay = Constant.ANIMATION_START_DELAY
                        interpolator = DefaultInterpolator.getInterpolator()
                    }
                }
            }
            chartModifiers { defaultModifiers() }
        }
    }
}