//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2021. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// DigitalLineChartFragment.kt is part of SCICHART®, High Performance Scientific Charts
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
import com.scichart.charting.visuals.SciChartSurface
import com.scichart.charting.visuals.pointmarkers.EllipsePointMarker
import com.scichart.drawing.common.SolidBrushStyle
import com.scichart.drawing.common.SolidPenStyle
import com.scichart.examples.data.RandomWalkGenerator
import com.scichart.examples.fragments.base.ExampleSingleChartBaseFragment
import com.scichart.examples.utils.Constant
import com.scichart.examples.utils.interpolator.DefaultInterpolator
import com.scichart.examples.utils.scichartExtensions.*

class DigitalLineChartFragment : ExampleSingleChartBaseFragment() {

    override fun initExample(surface: SciChartSurface) {
//        val fourierSeries = DataManager.getInstance().getFourierSeries(1.0, 0.1, 5000)
        val randomWalk = RandomWalkGenerator(1337).getRandomWalkSeries(25)

        surface.suspendUpdates {
            xAxes { numericAxis {} }
            yAxes { numericAxis {} }
            renderableSeries {
                fastLineRenderableSeries {
                    setIsDigitalLine(true)
                    xyDataSeries<Double, Double> {
                        append(randomWalk.xValues, randomWalk.yValues)
                    }
                    ellipsePointMarker {
                        setSize(25,25)
                        fillStyle = SolidBrushStyle(0xFFEC0F6C)
                        strokeStyle = SolidPenStyle(0xFFE4F5FC.toInt(), true, 2f, null)
                    }
                    strokeStyle = SolidPenStyle(0xFFEC0F6C)

                    sweepAnimation {
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