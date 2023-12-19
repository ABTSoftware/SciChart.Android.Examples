//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2021. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// CreateMultiPaneStockChartsFragment.kt is part of SCICHART®, High Performance Scientific Charts
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

package com.scichart.examples.fragments.examples2d.createStockCharts.kt

import android.view.LayoutInflater
import android.view.View
import com.scichart.charting.Direction2D.XDirection
import com.scichart.charting.model.dataSeries.XyDataSeries
import com.scichart.charting.modifiers.AxisDragModifierBase.AxisDragMode.Pan
import com.scichart.charting.visuals.SciChartSurface
import com.scichart.charting.visuals.axes.AutoRange
import com.scichart.charting.visuals.axes.NumericAxis
import com.scichart.charting.visuals.renderableSeries.FastColumnRenderableSeries
import com.scichart.charting.visuals.renderableSeries.data.XSeriesRenderPassData
import com.scichart.charting.visuals.renderableSeries.paletteProviders.IFillPaletteProvider
import com.scichart.charting.visuals.renderableSeries.paletteProviders.IStrokePaletteProvider
import com.scichart.charting.visuals.renderableSeries.paletteProviders.PaletteProviderBase
import com.scichart.charting.visuals.synchronization.SciChartVerticalGroup
import com.scichart.core.model.IntegerValues
import com.scichart.data.model.DoubleRange
import com.scichart.examples.R
import com.scichart.examples.data.DataManager
import com.scichart.examples.data.MovingAverage
import com.scichart.examples.data.PriceSeries
import com.scichart.examples.databinding.ExampleMultipaneStockChartsFragmentBinding
import com.scichart.examples.fragments.base.ExampleBaseFragment
import com.scichart.examples.utils.scichartExtensions.SolidBrushStyle
import com.scichart.examples.utils.scichartExtensions.SolidPenStyle
import com.scichart.examples.utils.scichartExtensions.annotations
import com.scichart.examples.utils.scichartExtensions.axis
import com.scichart.examples.utils.scichartExtensions.axisMarkerAnnotation
import com.scichart.examples.utils.scichartExtensions.categoryDateAxis
import com.scichart.examples.utils.scichartExtensions.chartModifiers
import com.scichart.examples.utils.scichartExtensions.drawable
import com.scichart.examples.utils.scichartExtensions.fastBandRenderableSeries
import com.scichart.examples.utils.scichartExtensions.fastCandlestickRenderableSeries
import com.scichart.examples.utils.scichartExtensions.fastColumnRenderableSeries
import com.scichart.examples.utils.scichartExtensions.fastLineRenderableSeries
import com.scichart.examples.utils.scichartExtensions.legendModifier
import com.scichart.examples.utils.scichartExtensions.modifierGroup
import com.scichart.examples.utils.scichartExtensions.ohlcDataSeries
import com.scichart.examples.utils.scichartExtensions.pinchZoomModifier
import com.scichart.examples.utils.scichartExtensions.renderableSeries
import com.scichart.examples.utils.scichartExtensions.suspendUpdates
import com.scichart.examples.utils.scichartExtensions.xAxes
import com.scichart.examples.utils.scichartExtensions.xAxisDragModifier
import com.scichart.examples.utils.scichartExtensions.xyDataSeries
import com.scichart.examples.utils.scichartExtensions.xyyDataSeries
import com.scichart.examples.utils.scichartExtensions.yAxes
import com.scichart.examples.utils.scichartExtensions.zoomExtentsModifier
import com.scichart.examples.utils.scichartExtensions.zoomPanModifier
import java.util.Date

class CreateMultiPaneStockChartsFragment : ExampleBaseFragment<ExampleMultipaneStockChartsFragmentBinding>() {
    private val verticalGroup = SciChartVerticalGroup()
    private val sharedXRange = DoubleRange()

    override fun showDefaultModifiersInToolbar(): Boolean = false

    override fun inflateBinding(inflater: LayoutInflater): ExampleMultipaneStockChartsFragmentBinding {
        return ExampleMultipaneStockChartsFragmentBinding.inflate(inflater)
    }

    override fun initExample(binding: ExampleMultipaneStockChartsFragmentBinding) {
        binding.macdChart.theme = R.style.SciChart_NavyBlue
        binding.priceChart.theme = R.style.SciChart_NavyBlue
        binding.rsiChart.theme = R.style.SciChart_NavyBlue
        binding.volumeChart.theme = R.style.SciChart_NavyBlue

        val priceData = DataManager.getInstance().getPriceDataEurUsd(activity)

        initPriceChart(binding.priceChart, priceData)
        initMacdChart(binding.macdChart, priceData)
        initRsiChart(binding.rsiChart, priceData)
        initVolumeChart(binding.volumeChart, priceData)
    }

    private fun initPriceChart(surface: SciChartSurface, prices: PriceSeries) {
        surface.suspendUpdates {
            initChart(this, true)

            val maLow = MovingAverage.movingAverage(prices.closeData, 50)
            val maHigh = MovingAverage.movingAverage(prices.closeData, 200)

            yAxes {
                axis(createNumericAxis(PRICES, "$0.0000", true))
            }
            renderableSeries {
                fastCandlestickRenderableSeries {
                    ohlcDataSeries<Date, Double>("EUR/USD") {
                        append(prices.dateData, prices.openData, prices.highData, prices.lowData, prices.closeData)
                    }
                    yAxisId = PRICES

                    strokeUpStyle = SolidPenStyle(0xFF67BDAF)
                    fillUpBrushStyle = SolidBrushStyle(0xFF447487)

                    strokeDownStyle = SolidPenStyle(0xFFDC7969)
                    fillDownBrushStyle = SolidBrushStyle(0x77DC7969)
                }
                fastLineRenderableSeries {
                    xyDataSeries<Date, Double>("Low Line") {
                        append(prices.dateData, maLow)
                    }
                    yAxisId = PRICES
                    strokeStyle = SolidPenStyle(0xFFEC0F6C)
                }
                fastLineRenderableSeries {
                    xyDataSeries<Date, Double>("High Line") {
                        append(prices.dateData, maHigh)
                    }
                    yAxisId = PRICES
                    strokeStyle = SolidPenStyle(0xFF50C7E0)
                }
            }

            annotations {
                axisMarkerAnnotation { y1 = prices.closeData.last(); yAxisId = PRICES; background = 0xFF67BDAF.drawable() }
                axisMarkerAnnotation { y1 = maLow.last(); yAxisId = PRICES; background = 0xFFEC0F6C.drawable() }
                axisMarkerAnnotation { y1 = maHigh.last(); yAxisId = PRICES; background = 0xFF50C7E0.drawable() }
            }
        }
    }

    private fun initVolumeChart(surface: SciChartSurface, prices: PriceSeries) {
        surface.suspendUpdates {
            initChart(surface, false)

            yAxes {
                axis(createNumericAxis(VOLUME, "###E+0", false))
            }
            renderableSeries {
                fastColumnRenderableSeries {
                    xyDataSeries<Date, Double>(VOLUME) {
                        append(prices.dateData, prices.volumeData.map(Long::toDouble))
                    }
                    yAxisId = VOLUME
                    paletteProvider = VolumePaletteProvider(prices)
                }
            }
            annotations {
                axisMarkerAnnotation { y1 = prices.volumeData.last().toDouble(); yAxisId = VOLUME }
            }
        }
    }

    private fun initRsiChart(surface: SciChartSurface, prices: PriceSeries) {
        surface.suspendUpdates {
            initChart(surface, false)
            val rsi = MovingAverage.rsi(prices, 14)

            yAxes {
                axis(createNumericAxis(RSI, "0.0", false))
            }
            renderableSeries {
                fastLineRenderableSeries {
                    xyDataSeries<Date, Double>(RSI) {
                        append(prices.dateData, rsi)
                    }
                    yAxisId = RSI
                    strokeStyle = SolidPenStyle(0xFF537ABD)
                }
            }
            annotations {
                axisMarkerAnnotation { y1 = rsi.last(); yAxisId = RSI }
            }
        }
    }

    private fun initMacdChart(surface: SciChartSurface, prices: PriceSeries) {
        surface.suspendUpdates {
            initChart(surface, false)
            val macd = MovingAverage.macd(prices.closeData, 12, 25, 9)

            yAxes {
                axis(createNumericAxis(MACD, "0.00", false))
            }
            renderableSeries {
                fastColumnRenderableSeries {
                    xyDataSeries<Date, Double>("Histogram") {
                        append(prices.dateData, macd.divergenceValues)
                    }
                    yAxisId = MACD
                    paletteProvider = MacdHistogramPaletteProvider()
                }
                fastBandRenderableSeries {
                    xyyDataSeries<Date, Double>(MACD) {
                        append(prices.dateData, macd.macdValues, macd.signalValues)
                    }
                    yAxisId = MACD

                    strokeStyle = SolidPenStyle(0xFF67BDAF)
                    strokeY1Style = SolidPenStyle(0xFFDC7969)

                    fillBrushStyle = SolidBrushStyle(0x77DC7969)
                    fillY1BrushStyle = SolidBrushStyle(0x7767BDAF)
                }
            }
            annotations {
                axisMarkerAnnotation { y1 = macd.divergenceValues.last(); yAxisId = MACD }
                axisMarkerAnnotation { y1 = macd.macdValues.last(); yAxisId = MACD }
            }
        }
    }

    private fun initChart(surface: SciChartSurface, isMainPane: Boolean) {
        surface.run {
            xAxes {
                categoryDateAxis {
                    visibility = if (isMainPane) View.VISIBLE else View.GONE
                    visibleRange = sharedXRange
                    growBy = DoubleRange(0.0, 0.05)
                }
            }

            chartModifiers { modifierGroup(context) {
                xAxisDragModifier { receiveHandledEvents = true; dragMode = Pan }
                pinchZoomModifier { receiveHandledEvents = true; direction = XDirection }
                zoomPanModifier { receiveHandledEvents = true }
                zoomExtentsModifier { receiveHandledEvents = true }
                legendModifier { setShowCheckboxes(false) }
            }}
        }

        verticalGroup.addSurfaceToGroup(surface)
    }

    fun createNumericAxis(title: String, yAxisTextFormatting: String, isMainPane: Boolean): NumericAxis {
        return NumericAxis(context).apply {
            axisId = title
            textFormatting = yAxisTextFormatting
            autoRange = AutoRange.Always
            minorsPerMajor = if (isMainPane) 4 else 2
            maxAutoTicks = if (isMainPane) 8 else 4
            growBy = if (isMainPane) DoubleRange(0.05, 0.05) else DoubleRange(0.0, 0.0)
        }
    }


    internal class VolumePaletteProvider constructor(private val prices: PriceSeries) :
        PaletteProviderBase<FastColumnRenderableSeries>(FastColumnRenderableSeries::class.java),
        IFillPaletteProvider, IStrokePaletteProvider {
        private val colors = IntegerValues()
        private val desiredColors = intArrayOf(-0x984251, -0x238697)
        override fun update() {
            val currentRenderPassData =
                renderableSeries!!.currentRenderPassData as XSeriesRenderPassData
            val size = currentRenderPassData.pointsCount()
            colors.setSize(size)
            val colorsArray = colors.itemsArray
            val indices = currentRenderPassData.xValues.itemsArray

            for (i in 0 until size) {
                val index = indices[i]
                val open = prices[index.toInt()].open
                val close = prices[index.toInt()].close

                if (close - open > 0) {
                    colorsArray[i] = desiredColors[0]
                } else {
                    colorsArray[i] = desiredColors[1]
                }
            }
        }

        override fun getFillColors(): IntegerValues {
            return colors
        }

        override fun getStrokeColors(): IntegerValues {
            return colors
        }
    }


    internal class MacdHistogramPaletteProvider:
        PaletteProviderBase<FastColumnRenderableSeries>(FastColumnRenderableSeries::class.java),
        IFillPaletteProvider, IStrokePaletteProvider {
        private val colors = IntegerValues()
        private val desiredColors = intArrayOf(-0x984251, -0x238697)
        override fun update() {
            val currentRenderPassData =
                renderableSeries!!.currentRenderPassData as XSeriesRenderPassData
            val size = currentRenderPassData.pointsCount()
            colors.setSize(size)
            val colorsArray = colors.itemsArray
            val indices = currentRenderPassData.xValues.itemsArray
            val dataSeries = renderableSeries!!.dataSeries as XyDataSeries<Date, Double>
            for (i in 0 until size) {
                val index = indices[i]
                val value = dataSeries.yValues[index.toInt()]
                if (value > 0) {
                    colorsArray[i] = desiredColors[0]
                } else {
                    colorsArray[i] = desiredColors[1]
                }
            }
        }

        override fun getFillColors(): IntegerValues {
            return colors
        }

        override fun getStrokeColors(): IntegerValues {
            return colors
        }
    }


    companion object {
        private const val VOLUME = "Volume"
        private const val PRICES = "Prices"
        private const val RSI = "RSI"
        private const val MACD = "MACD"
    }
}