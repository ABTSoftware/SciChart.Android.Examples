//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2021. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// InteractionWithAnnotationsFragment.kt is part of SCICHART®, High Performance Scientific Charts
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

package com.scichart.examples.fragments.examples2d.chartAnnotations.kt

import android.view.Gravity
import com.scichart.charting.visuals.SciChartSurface
import com.scichart.charting.visuals.annotations.AnnotationCoordinateMode
import com.scichart.charting.visuals.annotations.AnnotationSurfaceEnum
import com.scichart.charting.visuals.annotations.HorizontalAnchorPoint
import com.scichart.charting.visuals.annotations.LabelPlacement
import com.scichart.data.model.DoubleRange
import com.scichart.drawing.common.FontStyle
import com.scichart.drawing.utility.ColorUtil
import com.scichart.examples.R
import com.scichart.examples.data.MarketDataService
import com.scichart.examples.fragments.base.ExampleSingleChartBaseFragment
import com.scichart.examples.utils.scichartExtensions.*
import java.util.*

class InteractionWithAnnotationsFragment : ExampleSingleChartBaseFragment() {

    override fun showDefaultModifiersInToolbar(): Boolean = false

    override fun initExample(surface: SciChartSurface) {
        surface.suspendUpdates {
            xAxes { categoryDateAxis { } }
            yAxes { numericAxis { visibleRange = DoubleRange(30.0, 37.0) } }
            renderableSeries {
                fastCandlestickRenderableSeries {
                    dataSeries = OhlcDataSeries<Date, Double>().apply {
                        val marketDataService = MarketDataService(Calendar.getInstance().time, 5, 5)
                        val data = marketDataService.getHistoricalData(200)

                        append(data.dateData, data.openData, data.highData, data.lowData, data.closeData)
                    }
                    opacity = 0.4f
                }
            }
            chartModifiers { zoomPanModifier() }

            annotations {
                textAnnotation {
                    x1 = 10; y1 = 30.5
                    setIsEditable(true)
                    text = "Buy!"
                    fontStyle = FontStyle(20f, ColorUtil.White)
                    zIndex = 1 // draw this annotation above other annotations
                }
                textAnnotation {
                    x1 = 50; y1 = 34.0
                    setBackgroundResource(R.drawable.example_text_annotation_background)
                    setIsEditable(true)
                    text = "Sell!"
                    fontStyle = FontStyle(20f, ColorUtil.White)
                    setPadding(8, 8, 8, 8)
                    zIndex = 1 // draw this annotation above other annotations
                }
                textAnnotation {
                    x1 = 80; y1 = 37
                    setIsEditable(true)
                    text = "Rotated text"
                    fontStyle = FontStyle(20f, ColorUtil.White)
                    rotationAngle = 30f
                    zIndex = 1 // draw this annotation above other annotations
                }
                boxAnnotation {
                    x1 = 50; y1 = 35.5; x2 = 120; y2 = 32
                    setIsEditable(true)
                    setBackgroundResource(R.drawable.example_box_annotation_background_4)
                }
                lineAnnotation {
                    x1 = 40; y1 = 30.5; x2 = 60; y2 = 33.5
                    setIsEditable(true)
                    stroke = SolidPenStyle(0xAAFF6600, 2f)
                }
                lineAnnotation {
                    x1 = 120; y1 = 30.5; x2 = 175; y2 = 36
                    setIsEditable(true)
                    stroke = SolidPenStyle(0xAAFF6600, 2f)
                }
                lineArrowAnnotation {
                    x1 = 50; y1 = 35; x2 = 80; y2 = 31.4
                    headLength = 8f
                    headWidth = 16f
                    setIsEditable(true)
                }
                axisMarkerAnnotation {
                    y1 = 32.7
                    annotationSurface = AnnotationSurfaceEnum.YAxis
                    setIsEditable(true)
                }
                axisMarkerAnnotation {
                    x1 = 100
                    annotationSurface = AnnotationSurfaceEnum.XAxis
                    formattedValue = "Horizontal"
                    setIsEditable(true)
                }
                horizontalLineAnnotation {
                    x1 = 150; y1 = 32.2
                    stroke = SolidPenStyle(ColorUtil.Red, 2f)
                    horizontalGravity = Gravity.END
                    setIsEditable(true)
                    annotationLabels {
                        annotationLabel { labelPlacement = LabelPlacement.Axis }
                    }
                }
                horizontalLineAnnotation {
                    x1 = 130; y1 = 33.9; x2 = 160
                    stroke = SolidPenStyle(ColorUtil.Blue, 2f)
                    horizontalGravity = Gravity.CENTER_HORIZONTAL
                    setIsEditable(true)
                    annotationLabels {
                        annotationLabel { labelPlacement = LabelPlacement.Left; labelValue = "Left" }
                        annotationLabel { labelPlacement = LabelPlacement.Top; labelValue = "Top" }
                        annotationLabel { labelPlacement = LabelPlacement.Right; labelValue = "Right" }
                    }
                }
                verticalLineAnnotation {
                    x1 = 20; y1 = 35; y2 = 33
                    stroke = SolidPenStyle(ColorUtil.DarkGreen, 2f)
                    verticalGravity = Gravity.CENTER_VERTICAL
                    setIsEditable(true)
                }
                verticalLineAnnotation {
                    x1 = 40; y1 = 34
                    stroke = SolidPenStyle(ColorUtil.Green, 2f)
                    verticalGravity = Gravity.TOP
                    setIsEditable(true)
                    annotationLabels {
                        annotationLabel { labelPlacement = LabelPlacement.Top; rotationAngle = 90f }
                    }
                }
                textAnnotation {
                    x1 = 0.5; y1 = 0.5
                    coordinateMode = AnnotationCoordinateMode.Relative
                    horizontalAnchorPoint = HorizontalAnchorPoint.Center
                    text = "EUR.USD"
                    fontStyle = FontStyle(72f, 0x77FFFFFF)
                    zIndex = -1 // draw this annotation below other annotations
                }
            }
        }
    }
}