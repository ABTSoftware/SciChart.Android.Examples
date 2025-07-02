//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2021. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// SeriesSelectionFragment.kt is part of SCICHART®, High Performance Scientific Charts
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

import android.annotation.SuppressLint
import android.graphics.PointF
import com.scichart.charting.visuals.SciChartSurface
import com.scichart.charting.visuals.renderableSeries.IRenderableSeries
import com.scichart.charting.visuals.renderableSeries.StyleBase
import com.scichart.charting.visuals.renderableSeries.hitTest.HitTestInfo
import com.scichart.data.model.DoubleRange
import com.scichart.drawing.utility.ColorUtil
import com.scichart.examples.R
import com.scichart.examples.data.DataManager
import com.scichart.examples.fragments.base.ExampleSingleChartBaseFragment
import com.scichart.examples.utils.scichartExtensions.SolidPenStyle
import com.scichart.examples.utils.scichartExtensions.chartModifiers
import com.scichart.examples.utils.scichartExtensions.fastLineRenderableSeries
import com.scichart.examples.utils.scichartExtensions.numericAxis
import com.scichart.examples.utils.scichartExtensions.pinchZoomModifier
import com.scichart.examples.utils.scichartExtensions.renderableSeries
import com.scichart.examples.utils.scichartExtensions.seriesSelectionModifier
import com.scichart.examples.utils.scichartExtensions.suspendUpdates
import com.scichart.examples.utils.scichartExtensions.xAxes
import com.scichart.examples.utils.scichartExtensions.xyDataSeries
import com.scichart.examples.utils.scichartExtensions.yAxes
import com.scichart.examples.utils.scichartExtensions.zoomExtentsModifier
import com.scichart.examples.utils.scichartExtensions.zoomPanModifier

class StyleSeriesOnSelectionFragment : ExampleSingleChartBaseFragment() {

    private val COLOR_WAVE_A: Int = ColorUtil.argb(0xFF, 0xFF, 0x41, 0x8D)
    private val COLOR_WAVE_B: Int = ColorUtil.argb(0xFF, 0xFB, 0xBE, 0x17)
    private val COLOR_WAVE_C: Int = ColorUtil.argb(0xFF, 0x68, 0xBC, 0xA8)
    private val COLOR_WAVE_D: Int = ColorUtil.argb(0xFF, 0xE9, 0x70, 0x64)

    private var previousSelectedSeries: IRenderableSeries? = null

    override fun showDefaultModifiersInToolbar(): Boolean = false

    @SuppressLint("ClickableViewAccessibility")
    override fun initExample(surface: SciChartSurface) {
        surface.theme = R.style.SciChart_NavyBlue

        val sineA = DataManager.getInstance().getSinewave(100.0,1.0,POINTS_COUNT,8)
        val sineB = DataManager.getInstance().getSinewave(200.0,1.5, POINTS_COUNT,10)
        val sineC = DataManager.getInstance().getSinewave(150.0,2.0, POINTS_COUNT,12)
        val sineD = DataManager.getInstance().getSinewave(50.0,2.5, POINTS_COUNT,14)

        surface.suspendUpdates {
            xAxes {
                numericAxis {
                    visibleRange = DoubleRange(0.0, 2.0)
                }
            }
            yAxes {
                numericAxis {
                    growBy = DoubleRange(0.1,0.1)
                }
            }

            renderableSeries {
                fastLineRenderableSeries {
                    xyDataSeries<Double, Double>(SERIES_NAME_WAVE_A) {
                        append(sineA.xValues, sineA.yValues)
                    }
                    strokeStyle = SolidPenStyle(COLOR_WAVE_A, THICKNESS)
                }
                chartModifiers {
                    zoomPanModifier { }
                    zoomExtentsModifier { }
                    pinchZoomModifier { }
                }
                fastLineRenderableSeries {
                    xyDataSeries<Double, Double>(SERIES_NAME_WAVE_B) {
                        append(sineB.xValues, sineB.yValues)
                    }
                    strokeStyle = SolidPenStyle(COLOR_WAVE_B, THICKNESS)

                }
                fastLineRenderableSeries {
                    xyDataSeries<Double, Double>(SERIES_NAME_WAVE_C) {
                        append(sineC.xValues, sineC.yValues)
                    }
                    strokeStyle = SolidPenStyle(COLOR_WAVE_C, THICKNESS)

                }
                fastLineRenderableSeries {
                    xyDataSeries<Double, Double>(SERIES_NAME_WAVE_D) {
                        append(sineD.xValues, sineD.yValues)
                    }
                    strokeStyle = SolidPenStyle(COLOR_WAVE_D, THICKNESS)

                }
                chartModifiers {
                    seriesSelectionModifier {
                        // Set the selected style to know which series was hit.
                        selectedSeriesStyle =
                            object : StyleBase<IRenderableSeries>(IRenderableSeries::class.java) {

                                override fun applyStyleInternal(renderableSeriesToStyle: IRenderableSeries) {
                                    // if previously there was any series selected then discard it style and set it as unselected
                                    previousSelectedSeries?.let { discardStyleInternal(it) }
                                    adjustSeriesOpacity(renderableSeriesToStyle, true)
                                    // set current series selected to previous selected series so next time this can be discarded
                                    previousSelectedSeries = renderableSeriesToStyle
                                }

                                override fun discardStyleInternal(renderableSeriesToStyle: IRenderableSeries) {
                                    // Mark this series to unselected and adjusts its color
                                    adjustSeriesOpacity(renderableSeriesToStyle, false)
                                    renderableSeriesToStyle.isSelected = false
                                }
                            }
                    }
                }
            }
        }

        val touchPoint = PointF()
        val hitTestInfo = HitTestInfo()

        // Set the touch listener to find if a series is hit or not
        // If series if not hit then user tapped on the empty chart so reset the chart to default colors
        surface.setOnTouchListener { v, event ->
            touchPoint[event!!.x] = event.y
            surface.translatePoint(touchPoint, surface.renderableSeriesArea)

            // Check if any of the series was hit
            // If no hit then user tapped on the empty screen
            var isAnySeriesHit = false
            for (renderableSeries in surface.renderableSeries) {
                renderableSeries.hitTest(hitTestInfo, touchPoint.x, touchPoint.y)
                if(hitTestInfo.isHit){
                    isAnySeriesHit = true
                }
            }

            // If tap on empty screen reset opacity
            if(!isAnySeriesHit){
                resetSeriesOpacity()
            }
            return@setOnTouchListener false
        }
    }

    private fun resetSeriesOpacity(){
        // Reset every series to its original color and opacity
        for (series in binding.surface.renderableSeries) {
            series.isSelected = false
            series.opacity = 1.0f
            setDefaultColor(series)
        }
    }

    private fun adjustSeriesOpacity(selectedSeries: IRenderableSeries, isSelected: Boolean) {
        // Loop over every series to find the selected series and adjust color and opacity of the lines
        for (series in binding.surface.renderableSeries) {
            if (series === selectedSeries && isSelected) {
                series.opacity = 1.0f
                setDefaultColor(series)
            } else {
                series.opacity = 0.3f
                series.strokeStyle = SolidPenStyle(COLOR_DIM, THICKNESS)
            }
        }
    }

    private fun setDefaultColor(renderableSeries: IRenderableSeries) {
        // Set to default color by series name
        when (renderableSeries.dataSeries.seriesName) {
            SERIES_NAME_WAVE_A -> {
                renderableSeries.strokeStyle = SolidPenStyle(COLOR_WAVE_A, THICKNESS)
            }
            SERIES_NAME_WAVE_B -> {
                renderableSeries.strokeStyle = SolidPenStyle(COLOR_WAVE_B, THICKNESS)
            }
            SERIES_NAME_WAVE_C -> {
                renderableSeries.strokeStyle = SolidPenStyle(COLOR_WAVE_C, THICKNESS)
            }
            SERIES_NAME_WAVE_D -> {
                renderableSeries.strokeStyle = SolidPenStyle(COLOR_WAVE_D, THICKNESS)
            }
        }
    }

    companion object {
        const val POINTS_COUNT = 500
        const val THICKNESS = 2f

        const val COLOR_DIM: Int = ColorUtil.Grey

        const val SERIES_NAME_WAVE_A: String = "Wave A"
        const val SERIES_NAME_WAVE_B: String = "Wave B"
        const val SERIES_NAME_WAVE_C: String = "Wave C"
        const val SERIES_NAME_WAVE_D: String = "Wave D"
    }

}