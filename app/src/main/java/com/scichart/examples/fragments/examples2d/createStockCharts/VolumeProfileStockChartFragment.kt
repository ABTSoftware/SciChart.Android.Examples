//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2022. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// VolumeProfileStockChartFragment.kt is part of SCICHART®, High Performance Scientific Charts
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

import android.util.Log
import android.view.View
import com.scichart.charting.Direction2D
import com.scichart.charting.modifiers.AxisDragModifierBase
import com.scichart.charting.visuals.SciChartSurface
import com.scichart.charting.visuals.axes.AutoRange
import com.scichart.charting.visuals.axes.AxisAlignment
import com.scichart.charting.visuals.renderableSeries.FastColumnRenderableSeries
import com.scichart.core.model.DoubleValues
import com.scichart.core.utility.ListUtil
import com.scichart.data.model.DoubleRange
import com.scichart.examples.data.DataManager
import com.scichart.examples.data.PriceSeries
import com.scichart.examples.fragments.base.ExampleSingleChartBaseFragment
import com.scichart.examples.utils.scichartExtensions.SolidBrushStyle
import com.scichart.examples.utils.scichartExtensions.SolidPenStyle
import com.scichart.examples.utils.scichartExtensions.annotations
import com.scichart.examples.utils.scichartExtensions.axisMarkerAnnotation
import com.scichart.examples.utils.scichartExtensions.categoryDateAxis
import com.scichart.examples.utils.scichartExtensions.chartModifiers
import com.scichart.examples.utils.scichartExtensions.drawable
import com.scichart.examples.utils.scichartExtensions.fastCandlestickRenderableSeries
import com.scichart.examples.utils.scichartExtensions.legendModifier
import com.scichart.examples.utils.scichartExtensions.modifierGroup
import com.scichart.examples.utils.scichartExtensions.numericAxis
import com.scichart.examples.utils.scichartExtensions.ohlcDataSeries
import com.scichart.examples.utils.scichartExtensions.pinchZoomModifier
import com.scichart.examples.utils.scichartExtensions.renderableSeries
import com.scichart.examples.utils.scichartExtensions.suspendUpdates
import com.scichart.examples.utils.scichartExtensions.xAxes
import com.scichart.examples.utils.scichartExtensions.xAxisDragModifier
import com.scichart.examples.utils.scichartExtensions.yAxes
import com.scichart.examples.utils.scichartExtensions.zoomExtentsModifier
import com.scichart.examples.utils.scichartExtensions.zoomPanModifier
import com.scichart.extensions.builders.SciChartBuilder
import java.util.Collections
import java.util.Date

class VolumeProfileStockChartFragment : ExampleSingleChartBaseFragment() {
    companion object {
        private const val XAXIS_PRICES = "XAXIS_PRICES"
        private const val YAXIS_PRICES = "YAXIS_PRICES"
        private const val XAXIS_VOLUME = "XAXIS_VOLUME"
        private const val YAXIS_VOLUME = "YAXIS_VOLUME"
    }

    private lateinit var fastColumnRenderableSeries: FastColumnRenderableSeries
    val xValues = DoubleValues()
    val yValues = DoubleValues()

    override fun initExample(surface: SciChartSurface) {

        val priceData = DataManager.getInstance().getPriceDataEurUsd(activity)

        initPriceChart(binding.surface, priceData)
        initVolumeChart(binding.surface, priceData)
    }

    private fun initPriceChart(surface: SciChartSurface, prices: PriceSeries) {
        surface.suspendUpdates {
            xAxes {
                categoryDateAxis {
                    axisId = XAXIS_PRICES
                    growBy = DoubleRange(0.0, 0.05)
                }
            }

            yAxes {
                numericAxis {
                    axisId = YAXIS_PRICES
                    textFormatting = "$0.0000"
                    autoRange = AutoRange.Always
                    minorsPerMajor = 4
                    maxAutoTicks = 8
                    growBy = DoubleRange(0.05, 0.05)

                    setVisibleRangeChangeListener { axis, oldRange, newRange, isAnimating ->
                        visibleRangeChange(sciChartBuilder, prices, newRange.getMinAsDouble(), newRange.getMaxAsDouble())
                    }
                }
            }
            renderableSeries {
                fastCandlestickRenderableSeries {
                    ohlcDataSeries<Date, Double>("EUR/USD") {
                        append(prices.dateData, prices.openData, prices.highData, prices.lowData, prices.closeData)
                    }
                    xAxisId = XAXIS_PRICES
                    yAxisId = YAXIS_PRICES

                    strokeUpStyle = SolidPenStyle(0xFF67BDAF)
                    fillUpBrushStyle = SolidBrushStyle(0xFF447487)

                    strokeDownStyle = SolidPenStyle(0xFFDC7969)
                    fillDownBrushStyle = SolidBrushStyle(0x77DC7969)
                }
            }

            annotations {
                axisMarkerAnnotation {
                    y1 = prices.closeData.last()
                    xAxisId = XAXIS_PRICES
                    yAxisId = YAXIS_PRICES
                    background = 0xFF67BDAF.drawable()
                }
            }

            chartModifiers { modifierGroup(context) {
                xAxisDragModifier {
                    receiveHandledEvents = true;
                    dragMode = AxisDragModifierBase.AxisDragMode.Pan
                }
                pinchZoomModifier {
                    receiveHandledEvents = true;
                    direction = Direction2D.XDirection
                }
                zoomPanModifier { receiveHandledEvents = true }
                zoomExtentsModifier { receiveHandledEvents = true }
                legendModifier { setShowCheckboxes(false) }
            }}
        }
    }

    private fun initVolumeChart(surface: SciChartSurface, prices: PriceSeries) {
        fastColumnRenderableSeries = FastColumnRenderableSeries().apply {
            xAxisId = XAXIS_VOLUME
            yAxisId = YAXIS_VOLUME
            strokeStyle = SolidPenStyle(0x30FFFFFF)
            fillBrushStyle = SolidBrushStyle(0x30FFFFFF)
        }
        visibleRangeChange(sciChartBuilder, prices, 0.5, 1.5)

        surface.suspendUpdates {

            xAxes {
                numericAxis {
                    axisId = XAXIS_VOLUME
                    axisAlignment = AxisAlignment.Left
                    autoRange = AutoRange.Always
                    visibility = View.GONE
                }
            }

            yAxes {
                numericAxis {
                    axisId = YAXIS_VOLUME
                    textFormatting = "###E+0"
                    autoRange = AutoRange.Always
                    axisAlignment = AxisAlignment.Top
                    visibility = View.GONE
                }
            }
            Collections.addAll(binding.surface.renderableSeries, fastColumnRenderableSeries)

            annotations {
                axisMarkerAnnotation {
                    y1 = prices.volumeData.last().toDouble();
                    yAxisId = YAXIS_VOLUME
                    xAxisId = XAXIS_VOLUME
                }
            }
        }
    }

    private fun visibleRangeChange(
        builder: SciChartBuilder,
        prices: PriceSeries,
        min: Double,
        max: Double,
    ) {
        val volumePriceSeries = builder.newXyDataSeries(
            Double::class.javaObjectType,
            Double::class.javaObjectType
        ).withSeriesName("Volume").build()

        val closePrices = prices.getCloseData()
        val volumeData = ListUtil.select(
            prices.getVolumeData()
        ) { obj: Long -> obj.toDouble() }

        calculateVolumeProfile(closePrices, volumeData, min, max)

        volumePriceSeries.setAcceptsUnsortedData(true)
        volumePriceSeries.append(xValues, yValues)

        fastColumnRenderableSeries?.setDataSeries(volumePriceSeries)
    }

    private fun calculateVolumeProfile(
        prices: List<Double>,
        volumes: List<Double>,
        min: Double,
        max: Double,
    ) {
        val volumeProfile: MutableMap<Double, Double> = HashMap()
        val numBins = 50
        val stepSize = (max - min) / (numBins - 1)

        // Initialize bins
        val binRanges = DoubleArray(numBins)
        for (i in 0 until numBins) {
            binRanges[i] = min + i * stepSize
            volumeProfile[binRanges[i]] = 0.0
        }

        // Iterate over each price level
        for (i in prices.indices) {
            val price = prices[i]
            val volume = volumes[i]

            // Find the appropriate bin and update volume
            for (j in 0 until numBins) {
                val binStart = binRanges[j]
                var binEnd = 0.0
                binEnd = if (j < numBins - 1) {
                    binRanges[j + 1]
                } else {
                    100.0
                }
                if (price >= binStart && price < binEnd) {
                    volumeProfile[binStart] = volumeProfile[binStart]!! + volume
                    break
                }
            }
        }

        xValues.clear()
        yValues.clear()

        for ((key, value) in volumeProfile) {
            xValues.add(key)
            yValues.add(value)
        }
    }
}
