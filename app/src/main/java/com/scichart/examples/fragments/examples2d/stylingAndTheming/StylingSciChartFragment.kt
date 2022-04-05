//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2021. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// CustomThemeFragment.kt is part of SCICHART®, High Performance Scientific Charts
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

package com.scichart.examples.fragments.examples2d.stylingAndTheming.kt

import android.graphics.Color
import com.scichart.charting.visuals.SciChartSurface
import com.scichart.charting.visuals.axes.AutoRange
import com.scichart.charting.visuals.axes.AutoRange.Always
import com.scichart.charting.visuals.axes.AxisAlignment
import com.scichart.charting.visuals.axes.AxisAlignment.Right
import com.scichart.data.model.DoubleRange
import com.scichart.drawing.common.FontStyle
import com.scichart.drawing.common.SolidBrushStyle
import com.scichart.drawing.common.SolidPenStyle
import com.scichart.drawing.utility.ColorUtil
import com.scichart.examples.data.DataManager
import com.scichart.examples.fragments.base.ExampleSingleChartBaseFragment
import com.scichart.examples.utils.BillionsLabelProvider
import com.scichart.examples.utils.ThousandsLabelProvider
import com.scichart.examples.utils.interpolator.ElasticOutInterpolator
import com.scichart.examples.utils.scichartExtensions.*

class StylingSciChartFragment: ExampleSingleChartBaseFragment() {
    override fun initExample(surface: SciChartSurface) {
        // surface background. If you set color for chart background than it is color only for axes area
        surface.setBackgroundColor(ColorUtil.Orange)
        // chart area (viewport) background fill color
        surface.renderableSeriesAreaFillStyle = SolidBrushStyle(0xFFFFB6C1)
        // chart area border color and thickness
        surface.renderableSeriesAreaBorderStyle = sciChartBuilder.newPen().withColor(0xFF4682b4.toInt()).withThickness(2f).build()

        val dataManager = DataManager.getInstance()
        val priceBars = dataManager.getPriceDataIndu(context)

        surface.suspendUpdates {
            xAxes {
                // Create the XAxis
                numericAxis {
                    growBy = DoubleRange(0.1, 0.1)
                    visibleRange = DoubleRange(150.0, 180.0)
                    // Brushes and styles for the XAxis, vertical gridlines, vertical tick marks, vertical axis bands and xAxis labels
                    axisBandsStyle = SolidBrushStyle(0x55ff6655)
                    majorGridLineStyle = sciChartBuilder.newPen().withColor(ColorUtil.Green).build()
                    minorGridLineStyle = sciChartBuilder.newPen().withColor(ColorUtil.Yellow).withThickness(0.5f).withStrokeDashArray(floatArrayOf(10.0f, 3.0f, 10.0f, 3.0f)).build()
                    tickLabelStyle = sciChartBuilder.newFont().withTextColor(ColorUtil.Purple).withTextSize(14f).build()
                    drawMajorTicks = true
                    drawMinorTicks = true
                    drawMajorGridLines = true
                    drawMinorGridLines = true
                    drawLabels = true
                    majorTickLineLength = 5f
                    majorTickLineStyle = sciChartBuilder.newPen().withColor(ColorUtil.Green).build()
                    minorTickLineLength = 2f
                    minorTickLineStyle = sciChartBuilder.newPen().withColor(ColorUtil.Yellow).withThickness(0.5f).withStrokeDashArray(floatArrayOf(10.0f, 3.0f, 10.0f, 3.0f)).build()
                }
            }
            yAxes {
                // Create the Right YAxis
                numericAxis {
                    growBy = DoubleRange(0.1, 0.1)
                    axisAlignment = Right
                    autoRange = Always
                    axisId = "PrimaryAxisId"
                    // Brushes and styles for the Right YAxis, horizontal gridlines, horizontal tick marks, horizontal axis bands and right yAxis labels
                    axisBandsStyle = SolidBrushStyle(0x55ff6655)
                    majorGridLineStyle = sciChartBuilder.newPen().withColor(ColorUtil.Green).build()
                    minorGridLineStyle = sciChartBuilder.newPen().withColor(ColorUtil.Yellow).withThickness(0.5f).withStrokeDashArray(floatArrayOf(10.0f, 3.0f, 10.0f, 3.0f)).build()
                    labelProvider = ThousandsLabelProvider() // see LabelProvider API documentation for more info
                    tickLabelStyle = sciChartBuilder.newFont().withTextColor(ColorUtil.Green).withTextSize(14f).build()
                    drawMajorTicks = true
                    drawMinorTicks = true
                    drawLabels = true
                    drawMajorGridLines = true
                    drawMinorGridLines = true
                    majorTickLineLength = 3f
                    majorTickLineStyle = sciChartBuilder.newPen().withColor(ColorUtil.Purple).build()
                    minorTickLineLength = 2f
                    minorTickLineStyle = sciChartBuilder.newPen().withColor(ColorUtil.Red).withThickness(0.5f).build()
                }
                numericAxis {
                    growBy = DoubleRange(0.0, 3.0)
                    axisAlignment = AxisAlignment.Left
                    autoRange = AutoRange.Always
                    axisId = "SecondaryAxisId"
                    // Brushes and styles for the Left YAxis, horizontal gridlines, horizontal tick marks, horizontal axis bands and left yaxis labels
                    drawMajorBands = false
                    drawMajorGridLines = false
                    drawMinorGridLines = false
                    drawMajorTicks = true
                    drawMinorTicks = true
                    drawLabels = true
                    labelProvider = BillionsLabelProvider() // See LabelProvider API documentation
                    tickLabelStyle = sciChartBuilder.newFont().withTextColor(ColorUtil.Purple).withTextSize(12f).build()
                    majorTickLineLength = 3f
                    majorTickLineStyle = sciChartBuilder.newPen().withColor(ColorUtil.Black).build()
                    minorTickLineLength = 2f
                    minorTickLineStyle = sciChartBuilder.newPen().withColor(ColorUtil.Black).withThickness(0.5f).build()
                }
            }
            renderableSeries {
                fastMountainRenderableSeries {
                    yAxisId = "PrimaryAxisId"
                    xyDataSeries<Double, Double>("Mountain Series") {
                        append(priceBars.indexesAsDouble, dataManager.offset(priceBars.lowData, -1000.0))
                    }
                    // mountain series area fill
                    areaStyle = SolidBrushStyle(0xA000D0D0)
                    // mountain series line (just on top of mountain). If set to nil, there will be no line
                    strokeStyle = sciChartBuilder.newPen().withColor(0xFF00D0D0.toInt()).withThickness(2f).build()
                    // setting to true gives jagged mountains. set to false if you want regular mountain chart
                    setIsDigitalLine(true)

                    scaleAnimation { zeroLine = 10500.0; interpolator = ElasticOutInterpolator() }
                }

                fastLineRenderableSeries {
                    yAxisId = "PrimaryAxisId"
                    xyDataSeries<Double, Double>("Line Series") {
                        append(priceBars.indexesAsDouble, dataManager.computeMovingAverage(priceBars.closeData, 50))
                    }
                    // line series color and thickness
                    strokeStyle = sciChartBuilder.newPen().withColor(0xFF0000FF.toInt()).withThickness(3f).build()
                    // setting to true gives jagged line. set to false if you want regular line chart
                    setIsDigitalLine(false)
                    // one of the options for point markers.
                    // point marers at data points. set to nil if you don't need them
                    ellipsePointMarker {
                        setSize(7, 7)
                    }

                    scaleAnimation { zeroLine = 12250.0; interpolator = ElasticOutInterpolator() }
                }

                fastColumnRenderableSeries {
                    yAxisId = "SecondaryAxisId"
                    xyDataSeries<Double, Long>("Column Series") {
                        append(priceBars.indexesAsDouble, priceBars.volumeData)
                    }
                    // column series fill color
                    fillBrushStyle = SolidBrushStyle(0xE0D030D0)
                    // column series outline color and width. It is set to nil to disable outline
                    strokeStyle = sciChartBuilder.newPen().withColor(Color.TRANSPARENT).build()

                    scaleAnimation { zeroLine = 10500.0; interpolator = ElasticOutInterpolator() }
                }

                fastCandlestickRenderableSeries {
                    yAxisId = "PrimaryAxisId"
                    ohlcDataSeries<Double, Double>("Candlestick Series") {
                        append(priceBars.indexesAsDouble, priceBars.openData, priceBars.highData, priceBars.lowData, priceBars.closeData)
                    }
                    // candlestick series has separate color for data where close is higher that open value (up) and oposite when close is lower than open (down)
                    // candlestick stroke color and thicknes for "up" data
                    strokeUpStyle = sciChartBuilder.newPen().withColor(0xFF00FF00.toInt()).build()
                    // candlestick fill color for "up" data
                    fillUpBrushStyle = SolidBrushStyle(0x7000FF00)
                    // candlestick stroke color and thicknes for "down" data
                    strokeDownStyle = sciChartBuilder.newPen().withColor(0xFFFF0000.toInt()).build()
                    // candlestick fill color for "down" data
                    fillDownBrushStyle = SolidBrushStyle(0xFFFF0000)

                    scaleAnimation { zeroLine = 11700.0; interpolator = ElasticOutInterpolator() }
                }
            }

            chartModifiers {
                defaultModifiers()
            }
        }
    }
}