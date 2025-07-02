//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2021. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// PanAndZoomChartFragment.kt is part of SCICHART®, High Performance Scientific Charts
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

package com.scichart.examples.fragments.examples2d.zoomAndPanAChart.kt

import android.graphics.Color
import android.view.Gravity
import android.view.LayoutInflater
import com.scichart.charting.model.dataSeries.IXyDataSeries
import com.scichart.charting.model.dataSeries.OhlcDataSeries
import com.scichart.charting.numerics.indexDataProvider.DataSeriesIndexDataProvider
import com.scichart.charting.numerics.indexDataProvider.IIndexDataProvider
import com.scichart.charting.visuals.annotations.AnnotationCoordinateMode
import com.scichart.charting.visuals.annotations.VerticalLineAnnotation
import com.scichart.charting.visuals.overview.ISciChartOverviewTransformation
import com.scichart.charting.visuals.pointmarkers.EllipsePointMarker
import com.scichart.charting.visuals.renderableSeries.FastCandlestickRenderableSeries
import com.scichart.charting.visuals.renderableSeries.FastLineRenderableSeries
import com.scichart.charting.visuals.renderableSeries.IRenderableSeries
import com.scichart.data.model.DateRange
import com.scichart.data.model.DoubleRange
import com.scichart.data.model.ISciList
import com.scichart.drawing.utility.ColorUtil
import com.scichart.examples.data.DataManager
import com.scichart.examples.databinding.ExampleZoomAndPanWithOverviewChartFragmentBinding
import com.scichart.examples.fragments.base.ExampleBaseFragment
import com.scichart.examples.utils.scichartExtensions.OhlcDataSeries
import com.scichart.examples.utils.scichartExtensions.SolidBrushStyle
import com.scichart.examples.utils.scichartExtensions.SolidPenStyle
import com.scichart.examples.utils.scichartExtensions.XyDataSeries
import com.scichart.examples.utils.scichartExtensions.chartModifiers
import com.scichart.examples.utils.scichartExtensions.dateAxis
import com.scichart.examples.utils.scichartExtensions.defaultModifiers
import com.scichart.examples.utils.scichartExtensions.fastCandlestickRenderableSeries
import com.scichart.examples.utils.scichartExtensions.fastLineRenderableSeries
import com.scichart.examples.utils.scichartExtensions.indexDateAxis
import com.scichart.examples.utils.scichartExtensions.numericAxis
import com.scichart.examples.utils.scichartExtensions.pinchZoomModifier
import com.scichart.examples.utils.scichartExtensions.renderableSeries
import com.scichart.examples.utils.scichartExtensions.splineLineRenderableSeries
import com.scichart.examples.utils.scichartExtensions.suspendUpdates
import com.scichart.examples.utils.scichartExtensions.xAxes
import com.scichart.examples.utils.scichartExtensions.yAxes
import com.scichart.examples.utils.scichartExtensions.zoomExtentsModifier
import com.scichart.examples.utils.scichartExtensions.zoomPanModifier
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ZoomAndPanWithOverviewChartFragment : ExampleBaseFragment<ExampleZoomAndPanWithOverviewChartFragmentBinding>() {

    val dateFormat = SimpleDateFormat("yyyy.MM.dd", Locale.US)

    override fun inflateBinding(inflater: LayoutInflater): ExampleZoomAndPanWithOverviewChartFragmentBinding {
        return ExampleZoomAndPanWithOverviewChartFragmentBinding.inflate(inflater)
    }

    override fun initExample(binding: ExampleZoomAndPanWithOverviewChartFragmentBinding) {

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

        binding.surface.suspendUpdates {
            xAxes {
                indexDateAxis {
                    visibleRange = DateRange(dateData[size - 30], dateData[size - 1])
                    setIndexDataProvider(indexDataProvider)
                }
            }
            yAxes {
                numericAxis {
                }
            }
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
        binding.overview.setOverviewTransformation(object : ISciChartOverviewTransformation{
            override fun transformRenderableSeries(renderableSeries: IRenderableSeries): IRenderableSeries? {
                if(renderableSeries is FastCandlestickRenderableSeries){
                    val ohlcData = renderableSeries.dataSeries as? OhlcDataSeries<*, *>

                    // Build new XyDataSeries from Date and Close
                    val xValues = ohlcData?.xValues
                    val closeValues = ohlcData?.closeValues

                    val lineData = XyDataSeries<Date, Double>().apply {
                        for (i in 0 until (ohlcData?.count ?: 0)) {
                            append(xValues?.get(i) as Date, closeValues?.get(i) as Double)

                        }
                    }

                    val lineSeries = FastLineRenderableSeries().apply {
                        dataSeries = lineData
//                        dataSeries = renderableSeries
                        strokeStyle = SolidPenStyle(0xFF00AA00)
                    }
                    return lineSeries
                } else {
                    return null
                }
            }
        })
        binding.overview.parentSurface = binding.surface
        binding.overview.setGrips(generateGrip(), generateGrip());

    }

    private fun generateGrip(): VerticalLineAnnotation {
        return sciChartBuilder.newVerticalLineAnnotation()
            .withCoordinateMode(AnnotationCoordinateMode.RelativeY)
            .withVerticalGravity(Gravity.CENTER_VERTICAL)
            .withStroke(7f, ColorUtil.Grey)
            .build()
    }
}