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
import com.scichart.examples.data.RandomWalkGenerator
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
import com.scichart.examples.utils.scichartExtensions.fastColumnRenderableSeries
import com.scichart.examples.utils.scichartExtensions.fastLineRenderableSeries
import com.scichart.examples.utils.scichartExtensions.fastMountainRenderableSeries
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

    override fun inflateBinding(inflater: LayoutInflater): ExampleZoomAndPanWithOverviewChartFragmentBinding {
        return ExampleZoomAndPanWithOverviewChartFragmentBinding.inflate(inflater)
    }

    override fun initExample(binding: ExampleZoomAndPanWithOverviewChartFragmentBinding) {


        val randomWalkGenerator1 = RandomWalkGenerator()
        val data1 = randomWalkGenerator1.getRandomWalkSeries(POINTS_COUNT)

        val randomWalkGenerator2 = RandomWalkGenerator()
        val data2 = randomWalkGenerator2.getRandomWalkSeries(POINTS_COUNT)

        val randomWalkGenerator3 = RandomWalkGenerator()
        val data3 = randomWalkGenerator3.getRandomWalkSeries(POINTS_COUNT)

        val ds1 = XyDataSeries<Double, Double>()
        ds1.seriesName = "Line Series"
        ds1.append(data1.xValues, data1.yValues)

        val ds2 = XyDataSeries<Double, Double>()
        ds2.seriesName = "Mountain Series"
        ds2.append(data2.xValues, data2.yValues)

        val ds3 = XyDataSeries<Double, Double>()
        ds3.seriesName = "Column Series"
        ds3.append(data3.xValues, data3.yValues)

        binding.surface.suspendUpdates {
            xAxes {
                numericAxis {
                }
            }
            yAxes {
                numericAxis {
                }
            }
            renderableSeries {
                fastLineRenderableSeries {
                    dataSeries = ds1
                    strokeStyle = SolidPenStyle(0xFFFF70FF)
                }

                fastMountainRenderableSeries {
                    dataSeries = ds2
                    strokeStyle = SolidPenStyle(0xFFe9Fe64)
                }

                fastColumnRenderableSeries {
                    dataSeries = ds3
                    strokeStyle = SolidPenStyle(0xFFe97064)
                    fillBrushStyle = SolidBrushStyle(0xFFe97064)
                }

            }
            chartModifiers { defaultModifiers() }
        }
        binding.overview.setOverviewTransformation { renderableSeries ->
            return@setOverviewTransformation renderableSeries
        }
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

    companion object {
        private const val POINTS_COUNT = 1500
    }
}