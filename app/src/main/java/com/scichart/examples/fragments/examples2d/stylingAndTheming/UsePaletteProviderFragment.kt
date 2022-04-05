//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2021. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// UsePaletteProviderFragment.kt is part of SCICHART®, High Performance Scientific Charts
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

import com.scichart.charting.visuals.SciChartSurface
import com.scichart.charting.visuals.annotations.AnnotationCoordinateMode.RelativeY
import com.scichart.charting.visuals.annotations.BoxAnnotation
import com.scichart.charting.visuals.annotations.IAnnotation
import com.scichart.charting.visuals.annotations.OnAnnotationDragListener
import com.scichart.charting.visuals.axes.AutoRange
import com.scichart.charting.visuals.renderableSeries.OhlcRenderableSeriesBase
import com.scichart.charting.visuals.renderableSeries.XyRenderableSeriesBase
import com.scichart.charting.visuals.renderableSeries.data.OhlcRenderPassData
import com.scichart.charting.visuals.renderableSeries.data.XyRenderPassData
import com.scichart.charting.visuals.renderableSeries.paletteProviders.IFillPaletteProvider
import com.scichart.charting.visuals.renderableSeries.paletteProviders.IPointMarkerPaletteProvider
import com.scichart.charting.visuals.renderableSeries.paletteProviders.IStrokePaletteProvider
import com.scichart.charting.visuals.renderableSeries.paletteProviders.PaletteProviderBase
import com.scichart.core.model.IntegerValues
import com.scichart.data.model.DoubleRange
import com.scichart.drawing.utility.ColorUtil
import com.scichart.examples.R
import com.scichart.examples.data.DataManager
import com.scichart.examples.fragments.base.ExampleSingleChartBaseFragment
import com.scichart.examples.utils.ThousandsLabelProvider
import com.scichart.examples.utils.interpolator.ElasticOutInterpolator
import com.scichart.examples.utils.scichartExtensions.*
import kotlin.math.max
import kotlin.math.min

class UsePaletteProviderFragment : ExampleSingleChartBaseFragment() {

    override fun showDefaultModifiersInToolbar(): Boolean = false

    override fun initExample(surface: SciChartSurface) {
        val dataManager = DataManager.getInstance()
        val priceBars = dataManager.getPriceDataIndu(context)
        val dataOffset = -1000.0

        val boxAnnotation = BoxAnnotation(context).apply {
            x1 = 152.0; y1 = 0.0; x2 = 158.0; y2 = 1.0
            coordinateMode = RelativeY
            setBackgroundResource(R.drawable.example_box_annotation_background_1)
            setIsEditable(true)
            setOnAnnotationDragListener(object : OnAnnotationDragListener {
                override fun onDragStarted(annotation: IAnnotation) {
                    updateAnnotation(annotation)
                }

                fun updateAnnotation(annotation: IAnnotation) {
                    annotation.y1 = 0.0
                    annotation.y2 = 1.0
                    surface.invalidateElement()
                }

                override fun onDragEnded(annotation: IAnnotation) {
                    updateAnnotation(annotation)
                }

                override fun onDragDelta(annotation: IAnnotation, horizontalOffset: Float, verticalOffset: Float) {
                    updateAnnotation(annotation)
                }
            })
        }

        surface.suspendUpdates {
            xAxes {
                numericAxis { visibleRange = DoubleRange(150.0, 165.0) }
            }
            yAxes {
                numericAxis {
                    autoRange = AutoRange.Always
                    growBy = DoubleRange(0.0, 0.1)
                    drawMajorTicks = false
                    drawMinorTicks = false
                    labelProvider = ThousandsLabelProvider()
                }
            }
            renderableSeries {
                fastMountainRenderableSeries {
                    xyDataSeries<Double, Double>("Mountain Series") {
                        append(priceBars.indexesAsDouble, dataManager.offset(priceBars.lowData, dataOffset * 2))
                    }
                    areaStyle = SolidBrushStyle(0x9787CEEB)
                    strokeStyle = SolidPenStyle(ColorUtil.Magenta)
                    paletteProvider = XyCustomPaletteProvider(ColorUtil.Red, boxAnnotation)

                    scaleAnimation { zeroLine = 6000.0; interpolator = ElasticOutInterpolator() }
                }
                fastLineRenderableSeries {
                    xyDataSeries<Double, Double>("Line Series") {
                        append(priceBars.indexesAsDouble, dataManager.offset(priceBars.closeData, -dataOffset))
                    }
                    strokeStyle = SolidPenStyle(ColorUtil.Blue)
                    ellipsePointMarker {
                        fillStyle = SolidBrushStyle(ColorUtil.Red)
                        strokeStyle = SolidPenStyle(ColorUtil.Orange, 2f)
                        setSize(10)
                    }
                    paletteProvider = XyCustomPaletteProvider(ColorUtil.Red, boxAnnotation)

                    scaleAnimation { zeroLine = 12250.0; interpolator = ElasticOutInterpolator() }
                }
                fastOhlcRenderableSeries {
                    ohlcDataSeries<Double, Double>("Candlestick Series") {
                        append(priceBars.indexesAsDouble, priceBars.openData, priceBars.highData, priceBars.lowData, priceBars.closeData)
                    }
                    paletteProvider = OhlcCustomPaletteProvider(ColorUtil.CornflowerBlue, boxAnnotation)

                    scaleAnimation { zeroLine = 11750.0; interpolator = ElasticOutInterpolator() }
                }
                fastCandlestickRenderableSeries {
                    ohlcDataSeries<Double, Double>("Candlestick Series") {
                        append(priceBars.indexesAsDouble,
                            dataManager.offset(priceBars.openData, dataOffset),
                            dataManager.offset(priceBars.highData, dataOffset),
                            dataManager.offset(priceBars.lowData, dataOffset),
                            dataManager.offset(priceBars.closeData, dataOffset)
                        )
                    }
                    paletteProvider = OhlcCustomPaletteProvider(ColorUtil.Green, boxAnnotation)

                    scaleAnimation { zeroLine = 10750.0; interpolator = ElasticOutInterpolator() }
                }
                fastColumnRenderableSeries {
                    xyDataSeries<Double, Double>("Column Series") {
                        append(priceBars.indexesAsDouble, dataManager.offset(priceBars.closeData, dataOffset * 3))
                    }
                    strokeStyle = SolidPenStyle(ColorUtil.Blue)
                    zeroLineY = 6000.0
                    dataPointWidth = 0.8
                    fillBrushStyle = SolidBrushStyle(ColorUtil.Blue)
                    paletteProvider = XyCustomPaletteProvider(ColorUtil.Purple, boxAnnotation)

                    scaleAnimation { zeroLine = 6000.0; interpolator = ElasticOutInterpolator() }
                }
                xyScatterRenderableSeries {
                    xyDataSeries<Double, Double>("Scatter Series") {
                        append(priceBars.indexesAsDouble, dataManager.offset(priceBars.closeData, dataOffset * 2.5))
                    }
                    squarePointMarker {
                        fillStyle = SolidBrushStyle(ColorUtil.Red)
                        strokeStyle = SolidPenStyle(ColorUtil.Orange, 2f)
                        setSize(7)
                    }
                    paletteProvider = XyCustomPaletteProvider(ColorUtil.LimeGreen, boxAnnotation)

                    scaleAnimation { zeroLine = 9000.0; interpolator = ElasticOutInterpolator() }
                }
            }
            chartModifiers { defaultModifiers() }
            annotations { annotation(boxAnnotation) }
        }
    }

    private class XyCustomPaletteProvider(private val color: Int, private val annotation: BoxAnnotation) : PaletteProviderBase<XyRenderableSeriesBase>(XyRenderableSeriesBase::class.java), IFillPaletteProvider, IStrokePaletteProvider, IPointMarkerPaletteProvider {
        private val colors = IntegerValues()

        override fun update() {
            val renderableSeries = renderableSeries
            val currentRenderPassData = renderableSeries!!.currentRenderPassData as XyRenderPassData
            val xValues = currentRenderPassData.xValues

            val size = currentRenderPassData.pointsCount()
            colors.setSize(size)

            val x1 = annotation.x1 as Double
            val x2 = annotation.x2 as Double

            val min = min(x1, x2)
            val max = max(x1, x2)

            val colorsArray = colors.itemsArray
            val valuesArray = xValues.itemsArray

            for (i in 0 until size) {
                val value = valuesArray[i]
                if (value > min && value < max) colorsArray[i] = color else colorsArray[i] = DEFAULT_COLOR
            }
        }

        override fun getFillColors(): IntegerValues = colors
        override fun getPointMarkerColors(): IntegerValues = colors
        override fun getStrokeColors(): IntegerValues = colors
    }

    private class OhlcCustomPaletteProvider(private val color: Int, private val annotation: BoxAnnotation) : PaletteProviderBase<OhlcRenderableSeriesBase>(OhlcRenderableSeriesBase::class.java), IFillPaletteProvider, IStrokePaletteProvider, IPointMarkerPaletteProvider {
        private val colors = IntegerValues()

        override fun update() {
            val renderableSeries = renderableSeries
            val currentRenderPassData = renderableSeries!!.currentRenderPassData as OhlcRenderPassData
            val xValues = currentRenderPassData.xValues

            val size = currentRenderPassData.pointsCount()
            colors.setSize(size)

            val x1 = annotation.x1 as Double
            val x2 = annotation.x2 as Double

            val min = min(x1, x2)
            val max = max(x1, x2)

            val colorsArray = colors.itemsArray
            val valuesArray = xValues.itemsArray

            for (i in 0 until size) {
                val value = valuesArray[i]
                if (value > min && value < max) {
                    colorsArray[i] = color
                } else {
                    colorsArray[i] = DEFAULT_COLOR
                }
            }
        }

        override fun getFillColors(): IntegerValues = colors
        override fun getPointMarkerColors(): IntegerValues = colors
        override fun getStrokeColors(): IntegerValues = colors
    }
}