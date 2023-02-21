//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2021. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// ChangeColumnColor.kt is part of SCICHART®, High Performance Scientific Charts
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
import android.graphics.PointF
import android.text.SpannableStringBuilder
import android.view.MotionEvent
import android.view.View
import com.scichart.charting.model.dataSeries.XyDataSeries
import com.scichart.charting.modifiers.CursorModifier
import com.scichart.charting.modifiers.RolloverModifier
import com.scichart.charting.modifiers.TooltipModifier
import com.scichart.charting.visuals.SciChartSurface
import com.scichart.charting.visuals.axes.AxisAlignment.Left
import com.scichart.charting.visuals.renderableSeries.FastColumnRenderableSeries
import com.scichart.charting.visuals.renderableSeries.data.XSeriesRenderPassData
import com.scichart.charting.visuals.renderableSeries.hitTest.DefaultXySeriesInfoProvider
import com.scichart.charting.visuals.renderableSeries.hitTest.HitTestInfo
import com.scichart.charting.visuals.renderableSeries.hitTest.XySeriesInfo
import com.scichart.charting.visuals.renderableSeries.paletteProviders.IFillPaletteProvider
import com.scichart.charting.visuals.renderableSeries.paletteProviders.PaletteProviderBase
import com.scichart.charting.visuals.renderableSeries.tooltips.ISeriesTooltip
import com.scichart.charting.visuals.renderableSeries.tooltips.XySeriesTooltip
import com.scichart.core.model.IntegerValues
import com.scichart.core.utility.StringUtil
import com.scichart.data.model.DoubleRange
import com.scichart.drawing.utility.ColorUtil
import com.scichart.examples.R
import com.scichart.examples.data.DataManager
import com.scichart.examples.fragments.base.ExampleSingleChartBaseFragment
import com.scichart.examples.utils.Constant
import com.scichart.examples.utils.interpolator.DefaultInterpolator
import com.scichart.examples.utils.scichartExtensions.*

class ChangeColumnColor: ExampleSingleChartBaseFragment(), View.OnTouchListener {

    private val touchPoint = PointF()
    private val hitTestInfo = HitTestInfo()
    private val dataSeries1: XyDataSeries<Double, Double>? = null
    private var touchedIndex = -1

    override fun showDefaultModifiersInToolbar(): Boolean = false

    override fun initExample(surface: SciChartSurface) {
        surface.theme = R.style.SciChart_NavyBlue

        val yValues = intArrayOf(50, 35, 61, 58, 50, 50, 40, 53, 55, 23, 45, 12, 59, 60)

        surface.suspendUpdates {
            xAxes {
                numericAxis {
                    growBy = DoubleRange(0.1, 0.1)
                }
            }
            yAxes {
                numericAxis {
                    growBy = DoubleRange(0.0, 0.1)
                    axisAlignment = Left
                }
            }
            renderableSeries {
                fastColumnRenderableSeries {
                    xyDataSeries<Double, Double>() {
                        acceptsUnsortedData = true
                        for (i in yValues.indices){
                            append(i.toDouble(), yValues[i].toDouble())
                        }
                    }
                    dataPointWidth = 0.7

                    strokeStyle = SolidPenStyle(ColorUtil.SteelBlue)
                    paletteProvider = ColumnsPaletteProvider()
                    seriesInfoProvider = FirstCustomSeriesInfoProvider()

                }

            }
            chartModifiers { tooltipModifier() }
        }

        surface.setOnTouchListener(this)
    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        val surface = v as SciChartSurface
        touchPoint[event!!.x] = event.y
        surface.translatePoint(touchPoint, surface.renderableSeriesArea)

        for (renderableSeries in surface.renderableSeries) {
            renderableSeries.hitTest(hitTestInfo, touchPoint.x, touchPoint.y, 30f)
            touchedIndex = if (hitTestInfo.isHit) {
                hitTestInfo.dataSeriesIndex
            } else {
                -1
            }
            surface.invalidateElement()
        }
        return false
    }

    inner class ColumnsPaletteProvider :
        PaletteProviderBase<FastColumnRenderableSeries>(FastColumnRenderableSeries::class.java),
        IFillPaletteProvider {
        private val colors = IntegerValues()
        private val desiredColors = intArrayOf(-0xde5f28, -0x3bcca0)
        override fun update() {
            val currentRenderPassData =
                renderableSeries!!.currentRenderPassData as XSeriesRenderPassData
            val size = currentRenderPassData.pointsCount()
            colors.setSize(size)
            val colorsArray = colors.itemsArray
            for (i in 0 until size) {
                if (touchedIndex == i) {
                    colorsArray[i] = desiredColors[1]
                } else {
                    colorsArray[i] = desiredColors[0]
                }
            }
        }

        override fun getFillColors(): IntegerValues {
            return colors
        }
    }

    private class FirstCustomSeriesInfoProvider : DefaultXySeriesInfoProvider() {
        override fun getSeriesTooltipInternal(
            context: Context,
            seriesInfo: XySeriesInfo<*>?,
            modifierType: Class<*>,
        ): ISeriesTooltip {
            return if (modifierType == TooltipModifier::class.java) {
                FirstCustomXySeriesTooltip(context, seriesInfo)
            } else if (modifierType == RolloverModifier::class.java) {
                FirstCustomXySeriesTooltip(context, seriesInfo)
            } else if (modifierType == CursorModifier::class.java) {
                FirstCustomXySeriesTooltip(context, seriesInfo)
            } else {
                super.getSeriesTooltipInternal(context, seriesInfo, modifierType)
            }
        }

        private class FirstCustomXySeriesTooltip(context: Context?, seriesInfo: XySeriesInfo<*>?) :
            XySeriesTooltip(context, seriesInfo) {
            override fun internalUpdate(seriesInfo: XySeriesInfo<*>) {
                val sb = SpannableStringBuilder()
                sb.append("X: ").append(seriesInfo.formattedXValue).append(StringUtil.NEW_LINE)
                sb.append("Y: ").append(seriesInfo.formattedYValue)
                text = sb
                setSeriesColor(-0x1)
            }
        }
    }
}