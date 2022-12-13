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
import com.scichart.charting.modifiers.AxisDragModifierBase.AxisDragMode.Pan
import com.scichart.charting.visuals.SciChartSurface
import com.scichart.charting.visuals.axes.AutoRange
import com.scichart.charting.visuals.axes.NumericAxis
import com.scichart.charting.visuals.synchronization.SciChartVerticalGroup
import com.scichart.data.model.DoubleRange
import com.scichart.examples.R
import com.scichart.examples.data.DataManager
import com.scichart.examples.data.MovingAverage
import com.scichart.examples.data.PriceSeries
import com.scichart.examples.databinding.ExampleMultipaneStockChartsFragmentBinding
import com.scichart.examples.fragments.base.ExampleBaseFragment
import com.scichart.examples.utils.scichartExtensions.*
import java.util.*

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
                }
                fastLineRenderableSeries {
                    xyDataSeries<Date, Double>("Low Line") {
                        append(prices.dateData, maLow)
                    }
                    yAxisId = PRICES
                    strokeStyle = SolidPenStyle(0xFFFF3333)
                }
                fastLineRenderableSeries {
                    xyDataSeries<Date, Double>("High Line") {
                        append(prices.dateData, maHigh)
                    }
                    yAxisId = PRICES
                    strokeStyle = SolidPenStyle(0xFF33DD33)
                }
            }

            annotations {
                axisMarkerAnnotation { y1 = prices.closeData.last(); yAxisId = PRICES; background = 0xFFFF3333.drawable() }
                axisMarkerAnnotation { y1 = maLow.last(); yAxisId = PRICES; background = 0xFFFF3333.drawable() }
                axisMarkerAnnotation { y1 = maHigh.last(); yAxisId = PRICES; background = 0xFF33DD33.drawable() }
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
                    strokeStyle = SolidPenStyle(0xFFC6E6FF)
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
                }
                fastBandRenderableSeries {
                    xyyDataSeries<Date, Double>(MACD) {
                        append(prices.dateData, macd.macdValues, macd.signalValues)
                    }
                    yAxisId = MACD
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

    companion object {
        private const val VOLUME = "Volume"
        private const val PRICES = "Prices"
        private const val RSI = "RSI"
        private const val MACD = "MACD"
    }
}