//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2021. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// CustomTooltipsWithModifiersFragment.kt is part of SCICHART®, High Performance Scientific Charts
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
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.widget.CompoundButton
import android.widget.RadioButton
import com.scichart.charting.modifiers.CursorModifier
import com.scichart.charting.modifiers.IChartModifier
import com.scichart.charting.modifiers.RolloverModifier
import com.scichart.charting.modifiers.TooltipModifier
import com.scichart.charting.visuals.renderableSeries.hitTest.DefaultXySeriesInfoProvider
import com.scichart.charting.visuals.renderableSeries.hitTest.XySeriesInfo
import com.scichart.charting.visuals.renderableSeries.tooltips.ISeriesTooltip
import com.scichart.charting.visuals.renderableSeries.tooltips.XySeriesTooltip
import com.scichart.core.utility.StringUtil
import com.scichart.drawing.utility.ColorUtil
import com.scichart.examples.R
import com.scichart.examples.data.RandomWalkGenerator
import com.scichart.examples.databinding.ExampleCustomTooltipWithModifiersFragmentBinding
import com.scichart.examples.fragments.base.ExampleBaseFragment
import com.scichart.examples.utils.scichartExtensions.*

class CustomTooltipsWithModifiersFragment: ExampleBaseFragment<ExampleCustomTooltipWithModifiersFragmentBinding>() {
    private lateinit var rolloverModifier: RolloverModifier
    private lateinit var cursorModifier: CursorModifier
    private lateinit var tooltipModifier: TooltipModifier

    override fun inflateBinding(inflater: LayoutInflater): ExampleCustomTooltipWithModifiersFragmentBinding {
        return ExampleCustomTooltipWithModifiersFragmentBinding.inflate(inflater)
    }

    override fun initExample(binding: ExampleCustomTooltipWithModifiersFragmentBinding) {
        val randomWalkGenerator = RandomWalkGenerator()
        val data1 = randomWalkGenerator.getRandomWalkSeries(POINTS_COUNT)
        randomWalkGenerator.reset()
        val data2 = randomWalkGenerator.getRandomWalkSeries(POINTS_COUNT)

        binding.surface.suspendUpdates {
            xAxes { numericAxis() }
            yAxes { numericAxis() }

            renderableSeries {
                fastLineRenderableSeries {
                    xyDataSeries<Double, Double>("Series #1") {
                        append(data1.xValues, data1.yValues)
                    }
                    strokeStyle = SolidPenStyle(0xff6495ed, 2f)
                    seriesInfoProvider = FirstCustomSeriesInfoProvider()
                }
                fastLineRenderableSeries {
                    xyDataSeries<Double, Double>("Series #2") {
                        append(data2.xValues, data2.yValues)
                    }
                    strokeStyle = SolidPenStyle(0xffe2460c, 2f)
                    seriesInfoProvider = SecondCustomSeriesInfoProvider()
                }
            }

            chartModifiers {
                rolloverModifier { rolloverModifier = this }
                cursorModifier {
                    isEnabled = false
                    cursorModifier = this
                }
                tooltipModifier {
                    isEnabled = false
                    tooltipModifier = this
                }
            }
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initializeRadioButtons()
    }

    private fun initializeRadioButtons() {
        activity?.run {
            findViewById<RadioButton>(R.id.rollover).setOnCheckedChangeListener(RadioButtonCheckedChangeListener(rolloverModifier))
            findViewById<RadioButton>(R.id.cursor).setOnCheckedChangeListener(RadioButtonCheckedChangeListener(cursorModifier))
            findViewById<RadioButton>(R.id.tooltip).setOnCheckedChangeListener(RadioButtonCheckedChangeListener(tooltipModifier))
        }
    }

    private class RadioButtonCheckedChangeListener(private val chartModifier: IChartModifier) : CompoundButton.OnCheckedChangeListener {
        override fun onCheckedChanged(buttonView: CompoundButton, isChecked: Boolean) {
            chartModifier.isEnabled = isChecked
        }
    }

    private class FirstCustomSeriesInfoProvider : DefaultXySeriesInfoProvider() {
        override fun getSeriesTooltipInternal(context: Context, seriesInfo: XySeriesInfo<*>?, modifierType: Class<*>): ISeriesTooltip {
            return when (modifierType) {
                TooltipModifier::class.java -> { FirstCustomXySeriesTooltip(context, seriesInfo, TOOLTIP_MODIFIER_NAME) }
                RolloverModifier::class.java -> { FirstCustomXySeriesTooltip(context, seriesInfo, ROLLOVER_MODIFIER_NAME) }
                CursorModifier::class.java -> { FirstCustomXySeriesTooltip(context, seriesInfo, CURSOR_MODIFIER_NAME) }
                else -> { super.getSeriesTooltipInternal(context, seriesInfo, modifierType) }
            }
        }

        private class FirstCustomXySeriesTooltip(context: Context?, seriesInfo: XySeriesInfo<*>?, private val modifierName: String) :
            XySeriesTooltip(context, seriesInfo) {
            override fun internalUpdate(seriesInfo: XySeriesInfo<*>) {
                val sb = SpannableStringBuilder()
                sb.append("X: ").append(seriesInfo.formattedXValue).append(StringUtil.NEW_LINE)
                sb.append("Y: ").append(seriesInfo.formattedYValue).append(StringUtil.NEW_LINE)

                if (seriesInfo.seriesName != null) {
                    val start = sb.length
                    sb.append(seriesInfo.seriesName)
                    sb.setSpan(ForegroundColorSpan(ColorUtil.White), start, sb.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                    sb.append(StringUtil.NEW_LINE)
                }

                sb.append(modifierName)
                text = sb

                setSeriesColor(0xff6495ed.toInt())
            }
        }
    }

    private class SecondCustomSeriesInfoProvider : DefaultXySeriesInfoProvider() {
        override fun getSeriesTooltipInternal(context: Context, seriesInfo: XySeriesInfo<*>?, modifierType: Class<*>): ISeriesTooltip {
            return when (modifierType) {
                TooltipModifier::class.java -> { SecondCustomXySeriesTooltip(context, seriesInfo, TOOLTIP_MODIFIER_NAME) }
                RolloverModifier::class.java -> { SecondCustomXySeriesTooltip(context, seriesInfo, ROLLOVER_MODIFIER_NAME) }
                CursorModifier::class.java -> { SecondCustomXySeriesTooltip(context, seriesInfo, CURSOR_MODIFIER_NAME) }
                else -> { super.getSeriesTooltipInternal(context, seriesInfo, modifierType) }
            }
        }

        private class SecondCustomXySeriesTooltip(context: Context?, seriesInfo: XySeriesInfo<*>?, private val modifierName: String) : XySeriesTooltip(context, seriesInfo) {
            override fun internalUpdate(seriesInfo: XySeriesInfo<*>) {
                val sb = SpannableStringBuilder()
                sb.append(modifierName).append(StringUtil.NEW_LINE)

                if (seriesInfo.seriesName != null) {
                    sb.append(seriesInfo.seriesName)
                    sb.setSpan(ForegroundColorSpan(ColorUtil.Black), 0, sb.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                    sb.append(StringUtil.NEW_LINE)
                }
                sb.append("X: ").append(seriesInfo.formattedXValue)
                sb.append(" Y: ").append(seriesInfo.formattedYValue)
                text = sb

                setSeriesColor(0xffe2460c.toInt())
            }
        }
    }

    companion object {
        private const val POINTS_COUNT = 200
        private const val ROLLOVER_MODIFIER_NAME = "RolloverModifier"
        private const val CURSOR_MODIFIER_NAME = "CursorModifier"
        private const val TOOLTIP_MODIFIER_NAME = "TooltipModifier"
    }
}