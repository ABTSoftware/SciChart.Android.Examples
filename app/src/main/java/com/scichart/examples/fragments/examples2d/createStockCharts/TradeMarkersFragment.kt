//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2021. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// TradeMarkersFragment.kt is part of SCICHART®, High Performance Scientific Charts
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

import com.scichart.charting.model.dataSeries.IOhlcDataSeries
import com.scichart.charting.visuals.SciChartSurface
import com.scichart.charting.visuals.annotations.TradeMarkerAnnotation
import com.scichart.charting.visuals.axes.AutoRange
import com.scichart.data.model.DoubleRange
import com.scichart.examples.data.DataManager
import com.scichart.examples.fragments.base.ExampleSingleChartBaseFragment
import com.scichart.examples.utils.scichartExtensions.*
import java.util.*
import kotlin.math.floor

class TradeMarkersFragment : ExampleSingleChartBaseFragment() {

    override fun initExample(surface: SciChartSurface) {
        val priceSeries = DataManager.getInstance().getPriceDataIndu(activity)
        val size = priceSeries.size

        // Use a subset of data for better visualization
        val startIndex = maxOf(0, size - 100)
        val endIndex = size
        val dataSize = endIndex - startIndex

        // Create data series with subset of data
        val ohlcDataSeries = OhlcDataSeries<Date, Double>()
        val dateData = priceSeries.dateData
        val openData = priceSeries.openData
        val highData = priceSeries.highData
        val lowData = priceSeries.lowData
        val closeData = priceSeries.closeData

        for (i in startIndex until endIndex) {
            ohlcDataSeries.append(dateData[i], openData[i], highData[i],
                    lowData[i], closeData[i])
        }

        // Set visible range relative to the data series (0-based indices)
        // Show last 30 points, but ensure we don't go below 0
        val visibleStart = maxOf(0, dataSize - 30)
        val visibleEnd = dataSize

        surface.suspendUpdates {
            xAxes {
                categoryDateAxis {
                    visibleRange = DoubleRange(visibleStart.toDouble(), visibleEnd.toDouble())
                    growBy = DoubleRange(0.0, 0.1)
                }
            }
            yAxes {
                numericAxis {
                    growBy = DoubleRange(0.0, 0.1)
                    autoRange = AutoRange.Always
                }
            }
            renderableSeries {
                fastCandlestickRenderableSeries {
                    dataSeries = ohlcDataSeries
                    strokeUpStyle = SolidPenStyle(0xFF00AA00)
                    fillUpBrushStyle = SolidBrushStyle(0x8800AA00)
                    strokeDownStyle = SolidPenStyle(0xFFFF0000)
                    fillDownBrushStyle = SolidBrushStyle(0x88FF0000)
                }
            }
            chartModifiers {
                defaultModifiers()
            }

            // Simulate random trading algorithm
            simulateTradingAlgorithm(surface, ohlcDataSeries)
        }
    }

    /**
     * Simulates a trading algorithm and adds trade markers to the chart.
     */
    private fun simulateTradingAlgorithm(surface: SciChartSurface,
                                          dataSeries: IOhlcDataSeries<Date, Double>) {
        val random = Random(42)

        val dateValues = dataSeries.xValues
        val lowValues = dataSeries.lowValues
        val highValues = dataSeries.highValues
        val closeValues = dataSeries.closeValues

        // Initialize with some starting position so we can have sells from the beginning
        val firstPrice = if (closeValues.isNotEmpty()) closeValues[0] else 1000.0
        var balance = 5000000.0 // Cash available for buying
        var position = 500.0 // Starting with some shares
        var avgPrice = firstPrice // Average purchase price
        var equity = position * firstPrice // Initial equity value

        for (i in dateValues.indices) {
            val low = lowValues[i]
            val high = highValues[i]
            val close = closeValues[i]

            // Random price within the candle range
            val price = low + random.nextDouble() * (high - low)

            // 20% chance of trading
            if (random.nextDouble() < 0.2) {
                val t = equity / (equity + balance)

                if (random.nextDouble() > t) {
                    // Buy
                    val quantity = floor((random.nextDouble() * balance) / price)
                    val size = quantity * price
                    avgPrice = (avgPrice * position + size) / (position + quantity)
                    position += quantity
                    balance -= size

                    // Add buy marker at low price
                    // CategoryDateAxis uses numeric indices (0-based) for positioning
                    val buyMarker = TradeMarkerAnnotation(
                            context,
                            i.toDouble(),  // Use numeric index (0-based) for CategoryDateAxis
                            low,
                            true/*,
                            quantity,
                            price,
                            avgPrice*/
                    )
                    surface.annotations.add(buyMarker)
                } else {
                    // Sell
                    val quantity = floor((random.nextDouble() * equity) / price)
                    val size = quantity * price
                    position -= quantity
                    balance += size
                    //val pnl = (price - avgPrice) * quantity

                    // Add sell marker at high price
                    // CategoryDateAxis uses numeric indices (0-based) for positioning
                    val sellMarker = TradeMarkerAnnotation(
                            context,
                            i.toDouble(),  // Use numeric index (0-based) for CategoryDateAxis
                            high,
                            false/*,
                            quantity,
                            price,
                            pnl*/
                    )
                    surface.annotations.add(sellMarker)
                }
            }

            equity = position * close
        }
    }
}
