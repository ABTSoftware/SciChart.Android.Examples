//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2021. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// CreateRealTimeTickingStockChartFragment.kt is part of SCICHART®, High Performance Scientific Charts
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

import android.os.Bundle
import android.view.Gravity.*
import android.view.LayoutInflater
import android.view.View.GONE
import android.view.View.INVISIBLE
import androidx.annotation.DrawableRes
import com.scichart.charting.Direction2D.XDirection
import com.scichart.charting.model.dataSeries.IDataSeriesCore
import com.scichart.charting.visuals.SciChartSurface
import com.scichart.charting.visuals.annotations.AnnotationCoordinateMode.RelativeY
import com.scichart.charting.visuals.annotations.AxisMarkerAnnotation
import com.scichart.charting.visuals.annotations.BoxAnnotation
import com.scichart.charting.visuals.annotations.VerticalLineAnnotation
import com.scichart.charting.visuals.axes.AutoRange.Always
import com.scichart.charting.visuals.axes.IAxis
import com.scichart.core.annotations.Orientation.HORIZONTAL
import com.scichart.core.common.Action1
import com.scichart.data.model.DoubleRange
import com.scichart.data.model.IRange
import com.scichart.drawing.utility.ColorUtil
import com.scichart.examples.R
import com.scichart.examples.data.*
import com.scichart.examples.databinding.ExampleRealTimeTickingStockChartFragmentBinding
import com.scichart.examples.fragments.base.ExampleBaseFragment
import com.scichart.examples.utils.scichartExtensions.*
import com.scichart.examples.utils.widgetgeneration.ImageViewWidget
import com.scichart.examples.utils.widgetgeneration.Widget
import java.util.*

class CreateRealTimeTickingStockChartFragment: ExampleBaseFragment<ExampleRealTimeTickingStockChartFragmentBinding>() {
    private val ohlcDataSeries = OhlcDataSeries<Date, Double>("Price Series")
    private val xyDataSeries = XyDataSeries<Date, Double>("50-Period SMA")

    private lateinit var smaAxisMarker: AxisMarkerAnnotation
    private lateinit var ohlcAxisMarker: AxisMarkerAnnotation

    private lateinit var marketDataService: IMarketDataService
    private lateinit var overviewPrototype: OverviewPrototype

    private val sma50 = MovingAverage(50)
    private var lastPrice: PriceBar? = null

    override fun inflateBinding(inflater: LayoutInflater): ExampleRealTimeTickingStockChartFragmentBinding {
        return ExampleRealTimeTickingStockChartFragmentBinding.inflate(inflater)
    }

    override fun getToolbarItems(): List<Widget> = ArrayList<Widget>().apply {
        add(ImageViewWidget.Builder().setId(R.drawable.example_toolbar_play).setListener { marketDataService.subscribePriceUpdate(onNewPrice()) }.build())
        add(ImageViewWidget.Builder().setId(R.drawable.example_toolbar_pause).setListener { marketDataService.clearSubscriptions() }.build())
    }

    override fun initExample(binding: ExampleRealTimeTickingStockChartFragmentBinding) {
        // Market data service simulates live ticks. We want to load the chart with 150 historical bars then later do real-time ticking as new data comes in
        marketDataService = MarketDataService(Date(2000, 8, 1, 12, 0, 0), 5, 20)
        initChart()
    }

    private fun initChart() {
        initMainChart(binding.surface)
        overviewPrototype = OverviewPrototype(binding.surface, binding.overview)
    }

    private fun initMainChart(surface: SciChartSurface) {
        surface.suspendUpdates {
            xAxes { categoryDateAxis {
                barTimeFrame = SECONDS_IN_FIVE_MINUTES.toDouble()
                drawMajorGridLines = false
                growBy = DoubleRange(0.0, 0.1)
            }}
            yAxes { numericAxis { autoRange = Always } }

            renderableSeries {
                fastOhlcRenderableSeries {
                    strokeUpStyle = SolidPenStyle(STROKE_UP_COLOR, STROKE_THICKNESS)
                    strokeDownStyle = SolidPenStyle(STROKE_DOWN_COLOR, STROKE_THICKNESS)
                    strokeStyle = SolidPenStyle(STROKE_UP_COLOR)
                    dataSeries = ohlcDataSeries
                }
                fastLineRenderableSeries {
                    strokeStyle = SolidPenStyle(SMA_SERIES_COLOR, STROKE_THICKNESS)
                    dataSeries = xyDataSeries
                }
            }
            smaAxisMarker = AxisMarkerAnnotation(context).apply { y1 = 0.0; background = SMA_SERIES_COLOR.drawable() }
            ohlcAxisMarker = AxisMarkerAnnotation(context).apply { y1 = 0.0; background = STROKE_UP_COLOR.drawable() }

            annotations {
                annotation(smaAxisMarker)
                annotation(ohlcAxisMarker)
            }

            chartModifiers { modifierGroup(context) {
                xAxisDragModifier()
                zoomPanModifier { receiveHandledEvents = true; direction = XDirection }
                zoomExtentsModifier()
                legendModifier {
                    receiveHandledEvents = true
                    setOrientation(HORIZONTAL)
                    setLegendPosition(CENTER_HORIZONTAL or BOTTOM, 20)
                }
            }}
        }
    }

    override fun onSaveInstanceState(savedInstanceState: Bundle) {
        super.onSaveInstanceState(savedInstanceState)

        savedInstanceState.putInt("count", ohlcDataSeries.count)

        val range = binding.surface.xAxes[0].visibleRange
        savedInstanceState.putDouble("rangeMin", range.minAsDouble)
        savedInstanceState.putDouble("rangeMax", range.maxAsDouble)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        binding.surface.suspendUpdates {
            var count = DEFAULT_POINT_COUNT
            savedInstanceState?.run {
                count = getInt("count")
                val rangeMin = getDouble("rangeMin")
                val rangeMax = getDouble("rangeMax")

                xAxes[0].visibleRange.setMinMaxDouble(rangeMin, rangeMax)
            }

            val prices = marketDataService.getHistoricalData(count)
            ohlcDataSeries.append(prices.dateData, prices.openData, prices.highData, prices.lowData, prices.closeData)
            xyDataSeries.append(prices.dateData, getSmaCurrentValues(prices))

            overviewPrototype.overviewDataSeries.append(prices.dateData, prices.closeData)
            marketDataService.subscribePriceUpdate(onNewPrice())
        }
    }

    private fun getSmaCurrentValues(prices: PriceSeries): List<Double> {
        val result = ArrayList<Double>()
        for (close in prices.closeData) {
            result.add(sma50.push(close).current)
        }

        return result
    }

    @Synchronized
    private fun onNewPrice(): Action1<PriceBar> {
        return Action1 { price: PriceBar ->
            // Update the last price, or append?
            val smaLastValue: Double
            val overviewDataSeries = overviewPrototype.overviewDataSeries


            if (lastPrice?.date === price.date) {
                ohlcDataSeries.update(ohlcDataSeries.count - 1, price.open, price.high, price.low, price.close)

                smaLastValue = sma50.update(price.close).current
                xyDataSeries.updateYAt(xyDataSeries.count - 1, smaLastValue)

                overviewDataSeries.updateYAt(overviewDataSeries.count - 1, price.close)
            } else {
                ohlcDataSeries.append(price.date, price.open, price.high, price.low, price.close)

                smaLastValue = sma50.push(price.close).current
                xyDataSeries.append(price.date, smaLastValue)

                overviewDataSeries.append(price.date, price.close)

                // If the latest appending point is inside the viewport (i.e. not off the edge of the screen)
                // then scroll the viewport 1 bar, to keep the latest bar at the same place
                val visibleRange = binding.surface.xAxes[0].visibleRange
                if (visibleRange.maxAsDouble > ohlcDataSeries.count) {
                    visibleRange.setMinMaxDouble(visibleRange.minAsDouble + 1, visibleRange.maxAsDouble + 1)
                }
            }
            requireActivity().runOnUiThread {
                ohlcAxisMarker.background = (if (price.close >= price.open) STROKE_UP_COLOR else STROKE_DOWN_COLOR).drawable()
            }

            smaAxisMarker.y1 = smaLastValue
            ohlcAxisMarker.y1 = price.close
            lastPrice = price
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        marketDataService.stopGenerator()
    }

    companion object {
        private const val SECONDS_IN_FIVE_MINUTES = 5 * 60
        const val DEFAULT_POINT_COUNT = 150
        const val SMA_SERIES_COLOR = 0xFFFFA500
        const val STROKE_UP_COLOR = 0xFF00AA00
        const val STROKE_DOWN_COLOR = 0xFFFF0000
        const val STROKE_THICKNESS = 1.5f
    }

    private class OverviewPrototype(val parentSurface: SciChartSurface, fakeOverviewSurface: SciChartSurface) {
        private val leftBox = generateBoxAnnotation(R.drawable.example_grayed_out_box_annotation_background)
        private val rightBox = generateBoxAnnotation(R.drawable.example_grayed_out_box_annotation_background)
        private val boxAnnotation = generateBoxAnnotation(0)
        private val leftLineGrip = generateVerticalLine()
        private val rightLineGrip = generateVerticalLine()

        private val parentXAxisVisibleRange: IRange<*>
        private lateinit var overviewXAxisVisibleRange: IRange<*>
        val overviewDataSeries = XyDataSeries<Date, Double>().apply { acceptsUnsortedData = true }

        init {
            val parentXAxis = parentSurface.xAxes[0]
            parentXAxisVisibleRange = parentXAxis.visibleRange

            parentXAxis.setVisibleRangeChangeListener { _, _, newRange, _ ->
                val newMin = newRange.minAsDouble
                val newMax = newRange.maxAsDouble

                if (overviewXAxisVisibleRange != DoubleRange(0.0, 10.0)) {
                    parentXAxisVisibleRange.setMinMaxWithLimit(newMin, newMax, overviewXAxisVisibleRange)
                } else {
                    parentXAxisVisibleRange.setMinMax(newMin, newMax)
                }

                boxAnnotation.x1 = parentXAxisVisibleRange.min
                boxAnnotation.x2 = parentXAxisVisibleRange.max

                leftLineGrip.x1 = parentXAxisVisibleRange.min
                leftBox.x1 = overviewXAxisVisibleRange.min
                leftBox.x2 = parentXAxisVisibleRange.min

                rightLineGrip.x1 = parentXAxisVisibleRange.max
                rightBox.x1 = parentXAxisVisibleRange.max
                rightBox.x2 = overviewXAxisVisibleRange.max
            }

            initOverview(fakeOverviewSurface)

            overviewDataSeries.addObserver { _: IDataSeriesCore?, _: Int ->
                rightBox.x1 = parentXAxisVisibleRange.max
                rightBox.x2 = overviewXAxisVisibleRange.max
            }
        }

        private fun initOverview(surface :SciChartSurface) {
            surface.suspendUpdates {
                renderableSeriesAreaBorderStyle = null
                xAxes { categoryDateAxis {
                    barTimeFrame = SECONDS_IN_FIVE_MINUTES.toDouble()
                    autoRange = Always
                    drawMinorGridLines = false
                    visibility = GONE
                    growBy = DoubleRange(0.0, 0.1)

                    overviewXAxisVisibleRange = this.visibleRange
                    removeAxisGridLines(this)
                }}
                yAxes { numericAxis {
                    autoRange = Always
                    visibility = INVISIBLE
                    removeAxisGridLines(this)
                }}

                renderableSeries {
                    fastMountainRenderableSeries { dataSeries = overviewDataSeries }
                }

                annotations {
                    annotation(boxAnnotation)
                    annotation(leftBox)
                    annotation(rightBox)
                    annotation(leftLineGrip)
                    annotation(rightLineGrip)
                }
            }
        }

        private fun generateBoxAnnotation(@DrawableRes backgroundDrawable: Int): BoxAnnotation {
            return BoxAnnotation(parentSurface.context).apply {
                y1 = 0; y2 = 1
                setBackgroundResource(backgroundDrawable)
                coordinateMode = RelativeY
            }
        }

        private fun generateVerticalLine(): VerticalLineAnnotation {
            return VerticalLineAnnotation(parentSurface.context).apply {
                x1 = 0
                y1 = 0.3; y2 = 0.7
                coordinateMode = RelativeY
                verticalGravity = CENTER_VERTICAL
                stroke = SolidPenStyle(ColorUtil.Grey, 5f)
            }
        }

        private fun removeAxisGridLines(axis: IAxis) {
            axis.run {
                drawMajorGridLines = false
                drawMajorTicks = false
                drawMajorBands = false
                drawMinorGridLines = false
                drawMinorTicks = false
            }
        }
    }
}