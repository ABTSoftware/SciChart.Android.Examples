//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2021. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// AxisLabelProviderFragment.kt is part of SCICHART®, High Performance Scientific Charts
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

import android.graphics.Color
import android.view.animation.DecelerateInterpolator
import androidx.lifecycle.lifecycleScope
import com.scichart.charting.numerics.labelProviders.NumericLabelProvider
import com.scichart.charting.visuals.SciChartSurface
import com.scichart.charting.visuals.axes.NumericAxis
import com.scichart.data.model.DoubleRange
import com.scichart.examples.fragments.base.ExampleSingleChartBaseFragment
import com.scichart.examples.utils.Constant
import com.scichart.examples.utils.TextLabelFormatter
import com.scichart.examples.utils.interpolator.DefaultInterpolator
import com.scichart.examples.utils.scichartExtensions.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class AxisLabelProviderFragment : ExampleSingleChartBaseFragment() {


    override fun initExample(surface: SciChartSurface) {

        val textLabelFormatter = TextLabelFormatter()
        textLabelFormatter.setList(listOf(
            "Sun",
            "Mon",
            "Tue",
            "Wed",
            "Thu",
            "Fri",
            "Sat"
        ))


        binding.surface.suspendUpdates {
            xAxes {
                numericAxis {
                    maxAutoTicks = 7
                    visibleRange = DoubleRange(-0.5,6.5)
                    labelProvider = NumericLabelProvider(
                        textLabelFormatter
                    )
                }
            }
            yAxes {
                numericAxis { }
            }
            renderableSeries {
                fastColumnRenderableSeries {
                    xyDataSeries<Int, Int> {
                        append(0,5645)
                        append(1,1390)
                        append(2,2626)
                        append(3,9427)
                        append(4,513)
                        append(5,8737)
                        append(6,5987)
                    }
                    fillBrushStyle = SolidBrushStyle(Color.CYAN)
                    dataPointWidth = 0.75

                    waveAnimation {
                        duration = Constant.ANIMATION_DURATION
                        startDelay = Constant.ANIMATION_START_DELAY
                        interpolator = DefaultInterpolator.getInterpolator()
                    }
                }
            }

        }

    }

}