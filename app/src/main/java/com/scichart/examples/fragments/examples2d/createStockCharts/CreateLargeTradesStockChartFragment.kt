//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2022. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// CreateLargeTradesStockChart.kt is part of SCICHART®, High Performance Scientific Charts
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

import android.graphics.Color
import android.view.animation.DecelerateInterpolator
import com.scichart.charting.model.dataSeries.XyzDataSeries
import com.scichart.charting.numerics.indexDataProvider.DataSeriesIndexDataProvider
import com.scichart.charting.numerics.indexDataProvider.IIndexDataProvider
import com.scichart.charting.visuals.SciChartSurface
import com.scichart.data.model.DateRange
import com.scichart.data.model.DoubleRange
import com.scichart.examples.data.DataManager
import com.scichart.examples.data.LargeTradeBar
import com.scichart.examples.data.LargeTradeGenerator
import com.scichart.examples.fragments.base.ExampleSingleChartBaseFragment
import com.scichart.examples.utils.scichartExtensions.*
import java.util.*

class CreateLargeTradesStockChartFragment : ExampleSingleChartBaseFragment() {

    override fun initExample(surface: SciChartSurface) {
        val historicalData = OhlcDataSeries<Date, Double>()
        val largeSaleTradesData = XyzDataSeries<Date, Double, Double>().apply { acceptsUnsortedData = true }
        val largeBuyTradesData = XyzDataSeries<Date, Double, Double>().apply { acceptsUnsortedData = true }

        val largeTradeGenerator = LargeTradeGenerator()

        val priceSeries = DataManager.getInstance().getPriceDataIndu(activity)
        val largeSaleTradesList = largeTradeGenerator.generatePricesSeriesWithLargeTrades(priceSeries)
        val largeBuyTradesList = largeTradeGenerator.generatePricesSeriesWithLargeTrades(priceSeries)

        val size = priceSeries.size
        val dateData = priceSeries.dateData

        historicalData.append(dateData, priceSeries.openData, priceSeries.highData, priceSeries.lowData, priceSeries.closeData)

        appendLargeTrades(largeSaleTradesData, largeSaleTradesList)
        appendLargeTrades(largeBuyTradesData, largeBuyTradesList)

        val indexDataProvider: IIndexDataProvider = DataSeriesIndexDataProvider(historicalData)

        surface.suspendUpdates {
            xAxes {
                indexDateAxis {
                    visibleRange = DateRange(dateData[size - 30], dateData[size - 1])
                    growBy = DoubleRange(0.1, 0.1)
                    setIndexDataProvider(indexDataProvider)
                }
            }
            yAxes { numericAxis { growBy = DoubleRange(0.1, 0.1) } }
            renderableSeries {
                fastCandlestickRenderableSeries {
                    dataSeries = historicalData

                    strokeUpStyle = SolidPenStyle(0xFF00AA00)
                    fillUpBrushStyle = SolidBrushStyle(0xAA00AA00)
                    strokeDownStyle = SolidPenStyle(0xFFFF0000)
                    fillDownBrushStyle = SolidBrushStyle(0xAAFF0000)

                    waveAnimation { interpolator = DecelerateInterpolator() }
                }

                fastBubbleRenderableSeries {
                    autoZRange = false
                    dataSeries = largeBuyTradesData
                    bubbleBrushStyle = SolidBrushStyle(0x774248F5)
                    strokeStyle = SolidPenStyle(Color.TRANSPARENT)

                    waveAnimation { interpolator = DecelerateInterpolator() }
                }

                fastBubbleRenderableSeries {
                    autoZRange = false
                    dataSeries = largeSaleTradesData
                    bubbleBrushStyle = SolidBrushStyle(0x77F542AA)
                    strokeStyle = SolidPenStyle(Color.TRANSPARENT)

                    waveAnimation { interpolator = DecelerateInterpolator() }
                }
            }
            chartModifiers { defaultModifiers() }
        }
    }

    private fun appendLargeTrades(
        largeTradesDS: XyzDataSeries<Date, Double, Double>,
        largeTradesList: List<LargeTradeBar>
    ) {
        for ((date, largeTrades) in largeTradesList) {
            for ((price, volume) in largeTrades) {
                largeTradesDS.append(date, price, volume)
            }
        }
    }
}
