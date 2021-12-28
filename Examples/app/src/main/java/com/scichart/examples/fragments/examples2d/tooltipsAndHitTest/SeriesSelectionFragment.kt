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

import android.view.animation.DecelerateInterpolator
import com.scichart.charting.visuals.SciChartSurface
import com.scichart.charting.visuals.axes.AutoRange.Always
import com.scichart.charting.visuals.axes.AxisAlignment.*
import com.scichart.charting.visuals.pointmarkers.EllipsePointMarker
import com.scichart.charting.visuals.pointmarkers.IPointMarker
import com.scichart.charting.visuals.renderableSeries.IRenderableSeries
import com.scichart.charting.visuals.renderableSeries.StyleBase
import com.scichart.drawing.common.PenStyle
import com.scichart.drawing.utility.ColorUtil
import com.scichart.examples.data.DataManager
import com.scichart.examples.fragments.base.ExampleSingleChartBaseFragment
import com.scichart.examples.utils.scichartExtensions.*

class SeriesSelectionFragment : ExampleSingleChartBaseFragment() {

    override fun showDefaultModifiersInToolbar(): Boolean = false

    override fun initExample(surface: SciChartSurface) {
        surface.suspendUpdates {
            xAxes { numericAxis { axisAlignment = Bottom; autoRange = Always } }
            yAxes {
                numericAxis { axisAlignment = Left; axisId = Left.name}
                numericAxis { axisAlignment = Right; axisId = Right.name}
            }
            renderableSeries {
                var initialColor = ColorUtil.Blue
                for (i in 0 until SERIES_COUNT) {
                    val alignment = if (i % 2 == 0) Left else Right
                    val gradient = (if (alignment == Right) i else -i).toDouble()
                    val start = if (alignment == Right) 0.0 else 14000.0
                    val straightLine = DataManager.getInstance().getStraightLine(gradient, start, SERIES_POINT_COUNT)

                    fastLineRenderableSeries {
                        yAxisId = alignment.name
                        strokeStyle = SolidPenStyle(initialColor)
                        xyDataSeries<Double, Double>("Series $i") {
                            append(straightLine.xValues, straightLine.yValues)
                        }

                        sweepAnimation { interpolator = DecelerateInterpolator(); duration = 2000 }
                    }

                    val red = ColorUtil.red(initialColor)
                    val green = ColorUtil.green(initialColor)
                    val blue = ColorUtil.blue(initialColor)

                    val newR = if (red == 255) 255 else red + 5
                    val newB = if (blue == 0) 0 else blue - 2

                    initialColor = ColorUtil.rgb(newR, green, newB)
                }
            }

            chartModifiers {
                seriesSelectionModifier {
                    selectedSeriesStyle = object : StyleBase<IRenderableSeries>(IRenderableSeries::class.java) {
                        private val STROKE = "Stroke"
                        private val POINT_MARKER = "PointMarker"

                        private val selectedStrokeStyle = SolidPenStyle(ColorUtil.White, 4f)
                        private val selectedPointMarker: IPointMarker = EllipsePointMarker().apply {
                            setSize(10)
                            fillStyle = SolidBrushStyle(0xFFFF00DC)
                            strokeStyle = SolidPenStyle(ColorUtil.White)
                        }

                        override fun applyStyleInternal(renderableSeriesToStyle: IRenderableSeries) {
                            putPropertyValue(renderableSeriesToStyle, STROKE, renderableSeriesToStyle.strokeStyle);
                            putPropertyValue(renderableSeriesToStyle, POINT_MARKER, renderableSeriesToStyle.pointMarker);

                            renderableSeriesToStyle.strokeStyle = selectedStrokeStyle
                            renderableSeriesToStyle.pointMarker = selectedPointMarker
                        }

                        override fun discardStyleInternal(renderableSeriesToStyle: IRenderableSeries) {
                            renderableSeriesToStyle.strokeStyle = getPropertyValue(renderableSeriesToStyle, STROKE, PenStyle::class.java)
                            renderableSeriesToStyle.pointMarker = getPropertyValue(renderableSeriesToStyle, POINT_MARKER, IPointMarker::class.java)
                        }
                    }
                }
            }
        }
    }

    companion object {
        private const val SERIES_POINT_COUNT = 50
        private const val SERIES_COUNT = 80
    }
}