//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2021. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// UsingTooltipModifierTooltipsFragment.kt is part of SCICHART®, High Performance Scientific Charts
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

package com.scichart.examples.fragments.examples2d.tooltipsAndHitTest.kt

import com.scichart.charting.visuals.SciChartSurface
import com.scichart.charting.visuals.axes.AxisAlignment.Left
import com.scichart.core.model.DoubleValues
import com.scichart.data.model.DoubleRange
import com.scichart.drawing.utility.ColorUtil
import com.scichart.examples.R
import com.scichart.examples.data.DataManager
import com.scichart.examples.fragments.base.ExampleSingleChartBaseFragment
import com.scichart.examples.utils.Constant
import com.scichart.examples.utils.scichartExtensions.*

class UsingTooltipModifierTooltipsFragment: ExampleSingleChartBaseFragment() {

    override fun showDefaultModifiersInToolbar(): Boolean = false

    override fun initExample(surface: SciChartSurface) {
        surface.theme = R.style.SciChart_NavyBlue

        val ds1Points = DataManager.getInstance().getLissajousCurve(0.8, 0.2, 0.43, 500)
        val ds2Points = DataManager.getInstance().getSinewave(1.5, 1.0, 500)
        val scaledXValues = getScaledValues(ds1Points.xValues)

        surface.suspendUpdates {
            xAxes { numericAxis { growBy = DoubleRange(0.1, 0.1) } }
            yAxes { numericAxis { growBy = DoubleRange(0.1, 0.1); axisAlignment = Left } }
            renderableSeries {
                fastLineRenderableSeries {
                    xyDataSeries<Double, Double>("Lissajous Curve") {
                        acceptsUnsortedData = true
                        append(scaledXValues, ds1Points.yValues)
                    }
                    strokeStyle = SolidPenStyle(ColorUtil.SteelBlue)
                    ellipsePointMarker {
                        setSize(5)
                        strokeStyle = SolidPenStyle(0xFF47bde6)
                        fillStyle = SolidBrushStyle(0xFF47bde6)
                    }

                    opacityAnimation { duration = Constant.ANIMATION_DURATION }
                }
                fastLineRenderableSeries {
                    xyDataSeries<Double, Double>("SineWave") {
                        acceptsUnsortedData = true
                        append(ds2Points.xValues, ds2Points.yValues)
                    }
                    strokeStyle = SolidPenStyle(0xFFae418d)
                    ellipsePointMarker {
                        setSize(5)
                        strokeStyle = SolidPenStyle(0xFFae418d)
                        fillStyle = SolidBrushStyle(0xFFae418d)
                    }

                    opacityAnimation { duration = Constant.ANIMATION_START_DELAY }
                }
            }
            chartModifiers { tooltipModifier() }
        }
    }

    private fun getScaledValues(values: DoubleValues): DoubleValues? {
        val result = DoubleValues()
        for (value in values.itemsArray) {
            result.add((value + 1) * 5)
        }
        return result
    }
}