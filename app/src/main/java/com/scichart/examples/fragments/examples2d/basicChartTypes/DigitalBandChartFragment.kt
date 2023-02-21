//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2021. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// DigitalBandChartFragment.kt is part of SCICHART®, High Performance Scientific Charts
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
import com.scichart.data.model.DoubleRange
import com.scichart.examples.data.DataManager
import com.scichart.examples.fragments.base.ExampleSingleChartBaseFragment
import com.scichart.examples.utils.Constant
import com.scichart.examples.utils.interpolator.DefaultInterpolator
import com.scichart.examples.utils.scichartExtensions.*

class DigitalBandChartFragment : ExampleSingleChartBaseFragment() {

    override fun initExample(surface: SciChartSurface) {
        val data = DataManager.getInstance().getDampedSinewave(1.0, 0.01, 1000, 10)
        val moreData = DataManager.getInstance().getDampedSinewave(1.0, 0.005, 1000, 12)

        surface.suspendUpdates {
            xAxes { numericAxis { visibleRange = DoubleRange(1.1, 2.7) } }
            yAxes { numericAxis { growBy = DoubleRange(0.1, 0.1) } }
            renderableSeries {
                fastBandRenderableSeries {
                    setIsDigitalLine(true)
                    xyyDataSeries<Double, Double> {
                        append(data.xValues, data.yValues, moreData.yValues)
                    }
                    fillBrushStyle = SolidBrushStyle(0x33F48420)
                    fillY1BrushStyle = SolidBrushStyle(0x3350C7E0)
                    strokeStyle = SolidPenStyle(0xFFF48420)
                    strokeY1Style = SolidPenStyle(0xFF50C7E0)

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