//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2021. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// ImpulseChartFragment.kt is part of SCICHART®, High Performance Scientific Charts
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
import com.scichart.charting.model.dataSeries.IXyDataSeries
import com.scichart.charting.visuals.SciChartSurface
import com.scichart.data.model.DoubleRange
import com.scichart.examples.data.DataManager
import com.scichart.examples.fragments.base.ExampleSingleChartBaseFragment
import com.scichart.examples.utils.Constant
import com.scichart.examples.utils.interpolator.DefaultInterpolator
import com.scichart.examples.utils.scichartExtensions.*
import kotlin.math.ln
import kotlin.math.sin

class ImpulseChartFragment : ExampleSingleChartBaseFragment() {

    override fun initExample(surface: SciChartSurface) {
//        val ds1Points = DataManager.getInstance().getDampedSinewave(1.0, 0.05, 50, 5)

        surface.suspendUpdates {
            xAxes { numericAxis { growBy = DoubleRange(0.1, 0.1) }}
            yAxes { numericAxis { growBy = DoubleRange(0.1, 0.1) }}
            renderableSeries {
                fastImpulseRenderableSeries {
                    xyDataSeries<Double, Double> {
                        for (i in 0..70) {
                            append(
                                i.toDouble(),
                                sin(i * 0.2) * -ln((i.toFloat() / 100).toDouble())
                            )
                        }
                    }
                    strokeStyle = SolidPenStyle(0xFFEC0F6C)
                    ellipsePointMarker {
                        setSize(10)
                        strokeStyle = SolidPenStyle(0xFFEC0F6C)
                        fillStyle = SolidBrushStyle(0xFFEC0F6C)
                    }

                    waveAnimation {
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