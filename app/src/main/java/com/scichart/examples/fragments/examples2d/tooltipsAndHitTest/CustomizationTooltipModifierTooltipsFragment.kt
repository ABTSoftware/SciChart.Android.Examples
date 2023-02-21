//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2021. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// CustomizationTooltipModifierTooltipsFragment.kt is part of SCICHART®, High Performance Scientific Charts
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

import android.content.Context
import android.text.SpannableStringBuilder
import android.view.animation.DecelerateInterpolator
import com.scichart.charting.modifiers.TooltipModifier
import com.scichart.charting.visuals.SciChartSurface
import com.scichart.charting.visuals.renderableSeries.hitTest.DefaultXySeriesInfoProvider
import com.scichart.charting.visuals.renderableSeries.hitTest.XySeriesInfo
import com.scichart.charting.visuals.renderableSeries.tooltips.ISeriesTooltip
import com.scichart.charting.visuals.renderableSeries.tooltips.XySeriesTooltip
import com.scichart.core.utility.StringUtil
import com.scichart.drawing.utility.ColorUtil
import com.scichart.examples.data.RandomWalkGenerator
import com.scichart.examples.fragments.base.ExampleSingleChartBaseFragment
import com.scichart.examples.utils.Constant
import com.scichart.examples.utils.interpolator.DefaultInterpolator
import com.scichart.examples.utils.scichartExtensions.*

class CustomizationTooltipModifierTooltipsFragment : ExampleSingleChartBaseFragment() {

    override fun initExample(surface: SciChartSurface) {
        val randomWalkGenerator = RandomWalkGenerator()
        val data1 = randomWalkGenerator.getRandomWalkSeries(POINTS_COUNT)
        randomWalkGenerator.reset()
        val data2 = randomWalkGenerator.getRandomWalkSeries(POINTS_COUNT)

        surface.suspendUpdates {
            xAxes { numericAxis() }
            yAxes { numericAxis() }

            renderableSeries {
                fastLineRenderableSeries {
                    xyDataSeries<Double, Double>("Series #1") {
                        append(data1.xValues, data1.yValues)
                    }
                    strokeStyle = SolidPenStyle(0xff47bde6, 2f)
                    seriesInfoProvider = FirstCustomSeriesInfoProvider()

                    sweepAnimation {
                        duration = Constant.ANIMATION_DURATION
                        startDelay = Constant.ANIMATION_START_DELAY
                        interpolator = DefaultInterpolator.getInterpolator()
                    }
                }
                fastLineRenderableSeries {
                    xyDataSeries<Double, Double>("Series #2") {
                        append(data2.xValues, data2.yValues)
                    }
                    strokeStyle = SolidPenStyle(0xffae418d, 2f)
                    seriesInfoProvider = SecondCustomSeriesInfoProvider()

                    sweepAnimation {
                        duration = Constant.ANIMATION_DURATION
                        startDelay = Constant.ANIMATION_START_DELAY
                        interpolator = DefaultInterpolator.getInterpolator()
                    }
                }
            }

            chartModifiers { tooltipModifier() }
        }
    }

    private class FirstCustomSeriesInfoProvider : DefaultXySeriesInfoProvider() {
        override fun getSeriesTooltipInternal(context: Context, seriesInfo: XySeriesInfo<*>?, modifierType: Class<*>): ISeriesTooltip {
            return when (modifierType) {
                TooltipModifier::class.java -> { FirstCustomXySeriesTooltip(context, seriesInfo) }
                else -> { super.getSeriesTooltipInternal(context, seriesInfo, modifierType) }
            }
        }

        private class FirstCustomXySeriesTooltip(context: Context?, seriesInfo: XySeriesInfo<*>?) :
            XySeriesTooltip(context, seriesInfo) {
            override fun internalUpdate(seriesInfo: XySeriesInfo<*>) {
                val sb = SpannableStringBuilder()
                sb.append("X: ").append(seriesInfo.formattedXValue).append(StringUtil.NEW_LINE)
                sb.append("Y: ").append(seriesInfo.formattedYValue).append(StringUtil.NEW_LINE)

                seriesInfo.seriesName?.run {
                    sb.append(this).append(StringUtil.NEW_LINE)
                }
                sb.append(MODIFIER_NAME)
                text = sb

                setTooltipBackgroundColor(0xff47bde6.toInt())
                setTooltipStroke(0xff21a0d8.toInt())
                setTooltipTextColor(ColorUtil.White)
            }
        }
    }

    private class SecondCustomSeriesInfoProvider : DefaultXySeriesInfoProvider() {
        override fun getSeriesTooltipInternal(context: Context, seriesInfo: XySeriesInfo<*>?, modifierType: Class<*>): ISeriesTooltip {
            return when (modifierType) {
                TooltipModifier::class.java -> { SecondCustomXySeriesTooltip(context, seriesInfo) }
                else -> { super.getSeriesTooltipInternal(context, seriesInfo, modifierType) }
            }
        }

        private class SecondCustomXySeriesTooltip(context: Context?, seriesInfo: XySeriesInfo<*>?) : XySeriesTooltip(context, seriesInfo) {
            override fun internalUpdate(seriesInfo: XySeriesInfo<*>) {
                val sb = SpannableStringBuilder()
                sb.append(MODIFIER_NAME).append(StringUtil.NEW_LINE)

                seriesInfo.seriesName?.run {
                    sb.append(this).append(StringUtil.NEW_LINE)
                }
                sb.append("X: ").append(seriesInfo.formattedXValue)
                sb.append(" Y: ").append(seriesInfo.formattedYValue)
                text = sb

                setTooltipBackgroundColor(0xffae418d.toInt())
                setTooltipStroke(0xffc43360.toInt())
                setTooltipTextColor(ColorUtil.White)
            }
        }
    }

    companion object {
        private const val POINTS_COUNT = 200
        private const val MODIFIER_NAME = "TooltipModifier"
    }
}