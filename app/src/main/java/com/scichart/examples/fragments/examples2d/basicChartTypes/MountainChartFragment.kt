//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2021. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// MountainChartFragment.kt is part of SCICHART®, High Performance Scientific Charts
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
import java.util.*

class MountainChartFragment : ExampleSingleChartBaseFragment() {

    override fun initExample(surface: SciChartSurface) {
        val priceData = DataManager.getInstance().getPriceDataIndu(activity)

        surface.suspendUpdates {
            xAxes { dateAxis { growBy = DoubleRange(0.1, 0.1) }}
            yAxes { numericAxis { growBy = DoubleRange(0.1, 0.1) }}
            renderableSeries {
                fastMountainRenderableSeries {
                    xyDataSeries<Date, Double> {
                        append(priceData.dateData, priceData.closeData)
                    }
                    strokeStyle = SolidPenStyle(0xAAFFC9A8)
                    areaStyle = LinearGradientBrushStyle(0xAAFF8D42, 0x88090E11)

                    waveAnimation { interpolator = DecelerateInterpolator() }
                }
            }
            chartModifiers { defaultModifiers() }
        }
    }
}