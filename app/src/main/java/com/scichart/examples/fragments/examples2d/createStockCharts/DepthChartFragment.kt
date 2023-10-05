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

import android.content.Context
import android.content.res.Configuration
import android.graphics.Color
import android.view.LayoutInflater
import android.view.MotionEvent
import com.scichart.charting.model.dataSeries.OhlcDataSeries
import com.scichart.charting.model.dataSeries.XyDataSeries
import com.scichart.charting.modifiers.ChartModifierBase
import com.scichart.charting.modifiers.PinchZoomModifier
import com.scichart.charting.modifiers.ZoomExtentsModifier
import com.scichart.charting.modifiers.ZoomPanModifier
import com.scichart.charting.visuals.annotations.*
import com.scichart.charting.visuals.axes.AxisAlignment
import com.scichart.charting.visuals.axes.DateAxis
import com.scichart.charting.visuals.axes.NumericAxis
import com.scichart.charting.visuals.renderableSeries.FastCandlestickRenderableSeries
import com.scichart.charting.visuals.renderableSeries.FastLineRenderableSeries
import com.scichart.charting.visuals.renderableSeries.IRenderableSeries
import com.scichart.charting.visuals.renderableSeries.hitTest.HitTestInfo
import com.scichart.core.IServiceContainer
import com.scichart.core.framework.UpdateSuspender
import com.scichart.core.utility.Dispatcher.runOnUiThread
import com.scichart.core.utility.touch.ModifierTouchEventArgs
import com.scichart.data.model.DateRange
import com.scichart.drawing.common.FontStyle
import com.scichart.drawing.common.SolidPenStyle
import com.scichart.examples.R
import com.scichart.examples.databinding.ExampleDepthChartsFragmentBinding
import com.scichart.examples.fragments.base.ExampleBaseFragment
import com.scichart.examples.fragments.examples2d.createStockCharts.DepthChartFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader
import java.text.SimpleDateFormat
import java.util.*

class DepthChartFragment : ExampleBaseFragment<ExampleDepthChartsFragmentBinding>() {

    private var askLineSeries: FastLineRenderableSeries? = null
    private var askDataSeries: XyDataSeries<Double, Double>? = null

    private var bidLineSeries: FastLineRenderableSeries? = null
    private var bidDataSeries: XyDataSeries<Double, Double>? = null

    private var askMapList = mutableMapOf<Int, Double>()
    private var askMapSumList = mutableMapOf<Int, Double>()

    private var bidsMapList = mutableMapOf<Int, Double>()
    private var bidsMapSumList = mutableMapOf<Int, Double>()

    private var ohlcDataSeries: OhlcDataSeries<Date, Double>? = null

    override fun inflateBinding(inflater: LayoutInflater): ExampleDepthChartsFragmentBinding {
        return ExampleDepthChartsFragmentBinding.inflate(inflater)
    }

    override fun initExample(binding: ExampleDepthChartsFragmentBinding) {
        binding.depthChartSurface.theme = R.style.SciChart_NavyBlue
        binding.ohlcChartSurface.theme = R.style.SciChart_NavyBlue

        askMapList = readCsv("data/depth_chart/asks_initial_data.csv")
        bidsMapList = readCsv("data/depth_chart/bids_initial_data.csv")

        configData()
    }

    private fun readCsv(fileName: String): MutableMap<Int, Double> {

        val inputStream = InputStreamReader(context?.assets?.open(fileName))
        val reader = BufferedReader(inputStream)

        val total: StringBuilder = StringBuilder()
        var line: String?

        val map = mutableMapOf<Int, Double>()

        while (reader.readLine().also { line = it } != null) {
            total.append(line).append('\n')
            line?.let {
                val data = it.split(Regex(","))
                map[data.getOrNull(0)?.toIntOrNull() ?: 0] =
                    data.getOrNull(1)?.toDoubleOrNull() ?: -1.0
            }
        }

        return map
    }

    private fun configData() {

        askMapList = askMapList.toSortedMap()

        var sum = 0.0
        askMapList.forEach { (t, u) ->
            sum += u
            askMapSumList[t] = sum
        }

        bidsMapList = bidsMapList.toSortedMap(compareByDescending { it })

        var sum2 = 0.0
        bidsMapList.forEach { (t, u) ->
            sum2 += u
            bidsMapSumList[t] = sum2
        }

        bidsMapSumList = bidsMapSumList.toSortedMap()

        configDepthChart()
        configOhlcChart()

        GlobalScope.launch {
            while (true) {
                delay(1000)
                updateChart()
            }
        }

    }

    private fun configOhlcChart() {
        val inputStream = InputStreamReader(context?.assets?.open("data/depth_chart/ohlc_data.csv"))
        val reader = BufferedReader(inputStream)

        val total: StringBuilder = StringBuilder()
        var line: String?

        val map = mutableMapOf<Date, List<Double>>()

        while (reader.readLine().also { line = it } != null) {
            total.append(line).append('\n')
            line?.let {
                val data = it.split(Regex(","))
                val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
                val date = sdf.parse(data.getOrNull(0) ?: "")
                map[date] =
                    listOf(
                        data.getOrNull(1)?.toDoubleOrNull() ?: -1.0,
                        data.getOrNull(2)?.toDoubleOrNull() ?: -1.0,
                        data.getOrNull(3)?.toDoubleOrNull() ?: -1.0,
                        data.getOrNull(4)?.toDoubleOrNull() ?: -1.0
                    )
            }
        }

        ohlcDataSeries =
            OhlcDataSeries(Date::class.javaObjectType, Double::class.javaObjectType).apply {
                map.forEach { (t, u) ->
                    acceptsUnsortedData = true
                    append(
                        t,
                        u[0],
                        u[1],
                        u[2],
                        u[3]
                    )
                }
            }
        val ohlcSeries = FastCandlestickRenderableSeries().apply {
            dataSeries = ohlcDataSeries
            fillDownBrushStyle = com.scichart.drawing.common.SolidBrushStyle(Color.RED)
            fillUpBrushStyle = com.scichart.drawing.common.SolidBrushStyle(Color.GREEN)

            strokeDownStyle = com.scichart.drawing.common.SolidPenStyle(Color.RED, true, 3f, null)
            strokeUpStyle = com.scichart.drawing.common.SolidPenStyle(Color.GREEN, true, 3f, null)
        }
        val dateAxis = DateAxis(context)
        dateAxis.visibleRange = DateRange(
            map.keys.drop(600)[0],
            map.keys.last()
        )
        UpdateSuspender.using(binding.ohlcChartSurface) {
            Collections.addAll(binding.ohlcChartSurface.xAxes, dateAxis)
            Collections.addAll(binding.ohlcChartSurface.yAxes, NumericAxis(context))

            Collections.addAll(binding.ohlcChartSurface.renderableSeries, ohlcSeries)
            Collections.addAll(
                binding.ohlcChartSurface.chartModifiers,
                PinchZoomModifier(),
                ZoomPanModifier(),
                ZoomExtentsModifier()
            )
        }
    }

    private fun configDepthChart() {
        askLineSeries = FastLineRenderableSeries().apply {
            askDataSeries =
                XyDataSeries(Double::class.javaObjectType, Double::class.javaObjectType).apply {
                    askMapSumList.forEach { (t, u) ->
                        append(t.toDouble(), u)
                    }
                }
            dataSeries = askDataSeries
            dataSeries.acceptsUnsortedData = true
            strokeStyle = com.scichart.drawing.common.SolidPenStyle(Color.RED, true, 3f, null)
        }
        bidLineSeries = FastLineRenderableSeries().apply {
            bidDataSeries =
                XyDataSeries(Double::class.javaObjectType, Double::class.javaObjectType).apply {
                    bidsMapSumList.forEach { (t, u) ->
                        append(t.toDouble(), u)
                    }
                }
            dataSeries = bidDataSeries
            dataSeries.acceptsUnsortedData = true
            strokeStyle = com.scichart.drawing.common.SolidPenStyle(Color.GREEN, true, 3f, null)
        }

        val xAxis = NumericAxis(context)
        val yAxis = NumericAxis(context)

        if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            yAxis.axisAlignment = AxisAlignment.Bottom
            xAxis.axisAlignment = AxisAlignment.Left
            xAxis.flipCoordinates = true
        }

        val depthChartRollover = DepthChartRollover(
                requireContext(),
                bidLineSeries!!,
                askLineSeries!!
            )

        UpdateSuspender.using(binding.depthChartSurface) {
            Collections.addAll(binding.depthChartSurface.xAxes, xAxis)
            Collections.addAll(binding.depthChartSurface.yAxes, yAxis)

            Collections.addAll(binding.depthChartSurface.renderableSeries, askLineSeries)
            Collections.addAll(binding.depthChartSurface.renderableSeries, bidLineSeries)

            Collections.addAll(binding.depthChartSurface.chartModifiers, depthChartRollover);
        }
    }

    private fun updateChart() {

        GlobalScope.launch(Dispatchers.Main) {

            askMapList.forEach { (t, u) ->
                val random = Math.random() / 200
                if ((Math.random() * 100).mod(2.0).toInt() == 0) {
                    askMapList[t] = if (u - random < 0) u else u - random
                } else {
                    askMapList[t] = u + random
                }
            }

            var sum = 0.0
            askMapList.forEach { (t, u) ->
                sum += u
                askMapSumList[t] = sum
            }

            bidsMapList.forEach { (t, u) ->
                val random = Math.random() / 200
                if ((Math.random() * 100).mod(2.0).toInt() == 0) {
                    bidsMapList[t] = if (u - random < 0) u else u - random
                } else {
                    bidsMapList[t] = u + random
                }
            }

            var sum2 = 0.0
            bidsMapList.forEach { (t, u) ->
                sum2 += u
                bidsMapSumList[t] = sum2
            }

            runOnUiThread {
                UpdateSuspender.using(binding.depthChartSurface) {
                    askMapSumList.entries.forEachIndexed { index, mutableEntry ->
                        askDataSeries?.updateXyAt(
                            index,
                            mutableEntry.key.toDouble(),
                            mutableEntry.value
                        )
                    }
                    bidsMapSumList.entries.forEachIndexed { index, mutableEntry ->
                        bidDataSeries?.updateXyAt(
                            index,
                            mutableEntry.key.toDouble(),
                            mutableEntry.value
                        )
                    }
                }
                UpdateSuspender.using(binding.ohlcChartSurface) {
                    ohlcDataSeries?.update(
                        (ohlcDataSeries?.count ?: 1) - 1,
                        23590.04,
                        23695.04,
                        23480.04,
                        if ((Math.random() * 100).mod(2.0).toInt() == 0) {
                            23692.04 + Math.random() * 100
                        } else {
                            23692.04 - Math.random() * 100
                        }
                    )
                }
            }
        }
    }

    class DepthChartRollover(
        context: Context,
        var buySeries: IRenderableSeries,
        var sellSeries: IRenderableSeries,
    ) : ChartModifierBase() {
        var crosshairStrokeThickness = 2
        var midPoint = 0.0
        var xBuyLineAnnotation: VerticalLineAnnotation? = null
        var yBuyLineAnnotation: LineAnnotation? = null
        var xSellLineAnnotation: VerticalLineAnnotation? = null
        var ySellLineAnnotation: LineAnnotation? = null
        var midLine: VerticalLineAnnotation? = null
        var buyLabel: TextAnnotation? = null
        var sellLabel: TextAnnotation? = null
        override fun attachTo(services: IServiceContainer) {
            super.attachTo(services)
            xBuyLineAnnotation = createVerticalLineAnnotation(Color.GREEN)
            xSellLineAnnotation = createVerticalLineAnnotation(Color.RED)
            yBuyLineAnnotation = createHorizontalLineAnnotation(Color.GREEN)
            ySellLineAnnotation = createHorizontalLineAnnotation(Color.RED)
            buyLabel = createTextAnnotation()
            buyLabel?.horizontalAnchorPoint = HorizontalAnchorPoint.Right
            sellLabel = createTextAnnotation()
            sellLabel?.horizontalAnchorPoint = HorizontalAnchorPoint.Left
            parentSurface.annotations.add(xBuyLineAnnotation)
            parentSurface.annotations.add(xSellLineAnnotation)
            parentSurface.annotations.add(yBuyLineAnnotation)
            parentSurface.annotations.add(ySellLineAnnotation)
            parentSurface.annotations.add(buyLabel)
            parentSurface.annotations.add(sellLabel)
            createMidLine()
        }

        override fun detach() {
            super.detach()
        }

        override fun onTouch(args: ModifierTouchEventArgs) {
            val buyHitTestInfo = HitTestInfo()
            val sellHitTestInfo = HitTestInfo()
            super.onTouch(args)
            when (args.e.action) {
                MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
                    xBuyLineAnnotation?.setIsHidden(false)
                    xSellLineAnnotation?.setIsHidden(false)
                    yBuyLineAnnotation?.setIsHidden(false)
                    ySellLineAnnotation?.setIsHidden(false)
                    buyLabel?.setIsHidden(false)
                    sellLabel?.setIsHidden(false)
                    midLine?.setIsHidden(false)
                    buySeries.verticalSliceHitTest(buyHitTestInfo, args.e.x, args.e.y)
                    sellSeries.verticalSliceHitTest(sellHitTestInfo, args.e.x, args.e.y)
                    if (buyHitTestInfo.isHit) {
                        val buyValue =
                            (buySeries.dataSeries as XyDataSeries<*, *>).xValues[buyHitTestInfo.dataSeriesIndex] as Double
                        xBuyLineAnnotation?.x1 = buyValue
                        val sellValue = midPoint + (midPoint - buyValue)
                        xSellLineAnnotation?.x1 = sellValue

                        // Horizontal buy line
                        val buyYValue =
                            (buySeries.dataSeries as XyDataSeries<*, *>).yValues[buyHitTestInfo.dataSeriesIndex] as Double
                        yBuyLineAnnotation?.x1 = buyValue
                        yBuyLineAnnotation?.x2 = midPoint
                        yBuyLineAnnotation?.y1 = buyYValue
                        yBuyLineAnnotation?.y2 = buyYValue

                        // Horizontal sell line
                        val index =
                            (sellSeries.dataSeries as XyDataSeries<*, *>).xValues.indexOf(sellValue)
                        val sellYValue =
                            (sellSeries.dataSeries as XyDataSeries<*, *>).yValues[index] as Double
                        ySellLineAnnotation?.x1 = midPoint
                        ySellLineAnnotation?.x2 = sellValue
                        ySellLineAnnotation?.y1 = sellYValue
                        ySellLineAnnotation?.y2 = sellYValue

                        // Text Annotation
                        buyLabel?.x1 = midPoint
                        buyLabel?.y1 = buyYValue
                        buyLabel?.text = String.format("%1.2f", buyYValue)
                        sellLabel?.x1 = midPoint
                        sellLabel?.y1 = sellYValue
                        sellLabel?.text = String.format("%1.2f", sellYValue)
                    }
                    if (sellHitTestInfo.isHit) {
                        val sellValue =
                            (sellSeries.dataSeries as XyDataSeries<*, *>).xValues[sellHitTestInfo.dataSeriesIndex] as Double
                        xSellLineAnnotation?.x1 = sellValue
                        val buyValue = midPoint - (sellValue - midPoint)
                        xBuyLineAnnotation?.x1 = buyValue

                        // Horizontal line
                        val sellYValue =
                            (sellSeries.dataSeries as XyDataSeries<*, *>).yValues[sellHitTestInfo.dataSeriesIndex] as Double
                        ySellLineAnnotation?.x1 = midPoint
                        ySellLineAnnotation?.x2 = sellValue
                        ySellLineAnnotation?.y1 = sellYValue
                        ySellLineAnnotation?.y2 = sellYValue

                        // Horizontal sell line
                        val index =
                            (buySeries.dataSeries as XyDataSeries<*, *>).xValues.indexOf(buyValue)
                        val buyYValue =
                            (buySeries.dataSeries as XyDataSeries<*, *>).yValues[index] as Double
                        yBuyLineAnnotation?.x1 = buyValue
                        yBuyLineAnnotation?.x2 = midPoint
                        yBuyLineAnnotation?.y1 = buyYValue
                        yBuyLineAnnotation?.y2 = buyYValue

                        // Text Annotation
                        buyLabel?.x1 = midPoint
                        buyLabel?.y1 = buyYValue
                        buyLabel?.text = String.format("%1.2f", buyYValue)
                        sellLabel?.x1 = midPoint
                        sellLabel?.y1 = sellYValue
                        sellLabel?.text = String.format("%1.2f", sellYValue)
                    }
                    args.isHandled = true
                }
                MotionEvent.ACTION_UP -> {
                    xBuyLineAnnotation?.setIsHidden(true)
                    xSellLineAnnotation?.setIsHidden(true)
                    yBuyLineAnnotation?.setIsHidden(true)
                    ySellLineAnnotation?.setIsHidden(true)
                    midLine?.setIsHidden(true)
                    buyLabel?.setIsHidden(true)
                    sellLabel?.setIsHidden(true)
                    args.isHandled = true
                }
                else -> {}
            }
        }

        private fun createVerticalLineAnnotation(
            lineColor: Int,
        ): VerticalLineAnnotation {
            val lineAnnotation = VerticalLineAnnotation(context)
            lineAnnotation.stroke =
                SolidPenStyle(lineColor, true, crosshairStrokeThickness.toFloat(), null)
            lineAnnotation.coordinateMode = AnnotationCoordinateMode.RelativeY
            lineAnnotation.setIsHidden(true)
            val annotationLabel = AnnotationLabel(context)
            annotationLabel.labelPlacement = LabelPlacement.Axis
            lineAnnotation.annotationLabels.add(annotationLabel)
            return lineAnnotation
        }

        private fun createHorizontalLineAnnotation(
            lineColor: Int,
        ): LineAnnotation {
            val lineAnnotation = LineAnnotation(context)
            lineAnnotation.stroke = SolidPenStyle(lineColor, true, 5f, null)
            lineAnnotation.setIsHidden(true)
            return lineAnnotation
        }

        private fun createTextAnnotation(): TextAnnotation {
            val textAnnotation = TextAnnotation(context)
            textAnnotation.fontStyle = FontStyle(20f, Color.WHITE)
            textAnnotation.setPadding(10, 0, 10, 0)
            return textAnnotation
        }

        private fun createMidLine() {
            val buyValues = (buySeries.dataSeries as XyDataSeries<*, *>).xValues
            val buyPoint = buyValues[buyValues.size - 1] as Double
            val sellValues = (sellSeries.dataSeries as XyDataSeries<*, *>).xValues
            val sellPoint = sellValues[0] as Double
            midPoint = (buyPoint + sellPoint) / 2
            midLine = VerticalLineAnnotation(context)
            midLine?.stroke = SolidPenStyle(Color.WHITE, true, 3f, null)
            midLine?.coordinateMode = AnnotationCoordinateMode.RelativeY
            midLine?.setIsHidden(true)
            midLine?.x1 = midPoint.toInt()
            parentSurface.annotations.add(midLine)
        }
    }
}
