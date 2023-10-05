//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2022. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// MovingAverageTradesStockChartFragment.kt is part of SCICHART®, High Performance Scientific Charts
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

import com.scichart.charting.numerics.indexDataProvider.DataSeriesIndexDataProvider
import com.scichart.charting.numerics.indexDataProvider.IIndexDataProvider
import com.scichart.charting.visuals.SciChartSurface
import com.scichart.data.model.DateRange
import com.scichart.data.model.DoubleRange
import com.scichart.examples.data.DataManager
import com.scichart.examples.fragments.base.ExampleSingleChartBaseFragment
import com.scichart.examples.utils.scichartExtensions.*
import java.text.SimpleDateFormat
import java.util.*

class EasyStockChartFragment : ExampleSingleChartBaseFragment() {

    val dateFormat = SimpleDateFormat("yyyy.MM.dd", Locale.US)

    override fun initExample(surface: SciChartSurface) {
        val historicalData = OhlcDataSeries<Date, Double>()
        val movingAverageData = XyDataSeries<Date, Double>()
        val localMinMaxData = XyDataSeries<Date, Double>()

        val priceSeries = DataManager.getInstance().getPriceAAPL(activity)
        val movingAverages = DataManager.getInstance().computeMovingAverageInPriceSeries(priceSeries, 14)

        val size = priceSeries.size
        val dateData = priceSeries.dateData

        historicalData.append(dateData, priceSeries.openData, priceSeries.highData, priceSeries.lowData, priceSeries.closeData)
        movingAverageData.append(movingAverages.dateData, movingAverages.closeData)

        // append local min and max values
        try {
            localMinMaxData.append(dateFormat.parse("2023.01.03"), 124.17)
            localMinMaxData.append(dateFormat.parse("2023.02.03"), 157.38)
            localMinMaxData.append(dateFormat.parse("2023.03.02"), 143.90)
            localMinMaxData.append(dateFormat.parse("2023.03.06"), 156.30)
            localMinMaxData.append(dateFormat.parse("2023.03.13"), 147.70)
            localMinMaxData.append(dateFormat.parse("2023.03.22"), 162.14)
        } catch (e: Exception) {
            e.printStackTrace()
        }

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

                }

                fastLineRenderableSeries {
                    dataSeries = movingAverageData
                    strokeStyle = SolidPenStyle(0xFFF48420)
                }

                fastLineRenderableSeries {
                    dataSeries = localMinMaxData
                    strokeStyle = SolidPenStyle(0xFF50C7E0)

                }

            }
            chartModifiers { defaultModifiers() }
        }
    }

}
