//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2021. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// MultiplePieDonutChartFragment.kt is part of SCICHART®, High Performance Scientific Charts
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

package com.scichart.examples.fragments.examples2d.createGaugeCharts.kt

import android.view.LayoutInflater
import com.scichart.charting.visuals.renderableSeries.DonutRenderableSeries
import com.scichart.charting.visuals.renderableSeries.PieRenderableSeries
import com.scichart.examples.databinding.ExampleSinglePieChartWithLegendFragmentBinding
import com.scichart.examples.fragments.base.ExampleBaseFragment
import com.scichart.examples.utils.scichartExtensions.*

class MultiplePieDonutChartFragment : ExampleBaseFragment<ExampleSinglePieChartWithLegendFragmentBinding>() {

    override fun inflateBinding(inflater: LayoutInflater): ExampleSinglePieChartWithLegendFragmentBinding {
        return ExampleSinglePieChartWithLegendFragmentBinding.inflate(inflater)
    }

    override fun initExample(binding: ExampleSinglePieChartWithLegendFragmentBinding) {
        val pieSeries = PieRenderableSeries().apply {
            seriesName = "HowPeopleTravel"
            segmentsCollection {
                pieSegment { value = 34.0; title = "Ecologic"; fillStyle = RadialGradientBrushStyle(0xff84BC3D, 0xff5B8829) }
                pieSegment { value = 34.4; title = "Municipal"; fillStyle = RadialGradientBrushStyle(0xffe04a2f, 0xffB7161B) }
                pieSegment { value = 31.6; title = "Personal"; fillStyle = RadialGradientBrushStyle(0xff4AB6C1, 0xff2182AD) }
            }
        }

        val donutSeries = DonutRenderableSeries().apply {
            seriesName = "DetailedGroup"
            segmentsCollection {
                pieSegment { value = 28.8; title = "Walking"; fillStyle = RadialGradientBrushStyle(0xff84BC3D, 0xff5B8829) }
                pieSegment { value = 5.2; title = "Bicycle"; fillStyle = RadialGradientBrushStyle(0xff84BC3D, 0xff5B8829) }

                pieSegment { value = 12.3; title = "Metro"; fillStyle = RadialGradientBrushStyle(0xffe04a2f, 0xffB7161B) }
                pieSegment { value = 3.5; title = "Tram"; fillStyle = RadialGradientBrushStyle(0xffe04a2f, 0xffB7161B) }
                pieSegment { value = 5.9; title = "Rail"; fillStyle = RadialGradientBrushStyle(0xffe04a2f, 0xffB7161B) }
                pieSegment { value = 9.7; title = "Bus"; fillStyle = RadialGradientBrushStyle(0xffe04a2f, 0xffB7161B) }
                pieSegment { value = 3.0; title = "Taxi"; fillStyle = RadialGradientBrushStyle(0xffe04a2f, 0xffB7161B) }
                
                pieSegment { value = 23.2; title = "Car"; fillStyle = RadialGradientBrushStyle(0xff4AB6C1, 0xff2182AD) }
                pieSegment { value = 3.1; title = "Motorcycle"; fillStyle = RadialGradientBrushStyle(0xff4AB6C1, 0xff2182AD) }
                pieSegment { value = 5.3; title = "Other"; fillStyle = RadialGradientBrushStyle(0xff4AB6C1, 0xff2182AD) }
            }
        }

        binding.pieChart.run {
            renderableSeries {
                rSeries(pieSeries)
                rSeries(donutSeries)
            }
            chartModifiers {
                pieChartLegendModifier(binding.pieChartLegend) {
                    setSourceSeries(pieSeries)
                    setShowCheckboxes(false)
                }
                pieChartTooltipModifier()
            }
        }

        pieSeries.animate(800)
        donutSeries.animate(800)
    }
}