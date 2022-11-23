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
import com.scichart.data.model.DoubleRange
import com.scichart.examples.data.DataManager
import com.scichart.examples.fragments.base.ExampleSingleChartBaseFragment
import com.scichart.examples.utils.scichartExtensions.*

class DigitalLineChartFragment : ExampleSingleChartBaseFragment() {

    override fun initExample(surface: SciChartSurface) {
        val fourierSeries = DataManager.getInstance().getFourierSeries(1.0, 0.1, 5000)

        surface.suspendUpdates {
            xAxes { numericAxis {
                growBy = DoubleRange(0.1, 0.1)
                visibleRange = DoubleRange(1.0, 1.25)
            }}
            yAxes { numericAxis {
                growBy = DoubleRange(0.5, 0.5)
                visibleRange = DoubleRange(2.3, 3.3)
            }}
            renderableSeries {
                fastLineRenderableSeries {
                    setIsDigitalLine(true)
                    xyDataSeries<Double, Double> {
                        append(fourierSeries.xValues, fourierSeries.yValues)
                    }
                    strokeStyle = SolidPenStyle(0xFFe97064)

                    waveAnimation { interpolator = DecelerateInterpolator() }
                }
            }
            chartModifiers { defaultModifiers() }
        }
    }
}