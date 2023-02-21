//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2021. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// DonutChartFragment.kt is part of SCICHART®, High Performance Scientific Charts
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
import com.scichart.examples.databinding.ExampleSinglePieChartWithLegendFragmentBinding
import com.scichart.examples.fragments.base.ExampleBaseFragment
import com.scichart.examples.utils.Constant
import com.scichart.examples.utils.scichartExtensions.*

class DonutChartFragment : ExampleBaseFragment<ExampleSinglePieChartWithLegendFragmentBinding>() {

    override fun inflateBinding(inflater: LayoutInflater): ExampleSinglePieChartWithLegendFragmentBinding {
        return ExampleSinglePieChartWithLegendFragmentBinding.inflate(inflater)
    }

    override fun initExample(binding: ExampleSinglePieChartWithLegendFragmentBinding) {
        val donutSeries = DonutRenderableSeries().apply {
            segmentsCollection {
                pieSegment { value = 40.0; title = "Glazed Doughnut"; fillStyle = RadialGradientBrushStyle(0xff47bde6, 0xff47bde6) }
                pieSegment { value = 10.0; title = "Strawberry Frosted Doughnut"; fillStyle = RadialGradientBrushStyle(0xffae418d, 0xffae418d) }
                pieSegment { value = 20.0; title = "Apple-Crumb Doughnut"; fillStyle = RadialGradientBrushStyle(0xff68bcae, 0xff68bcae) }
                pieSegment { value = 15.0; title = "Cinnamon Twist Doughnut"; fillStyle = RadialGradientBrushStyle(0xffe97064, 0xffe97064) }
            }
        }

        binding.pieChart.run {
            renderableSeries { rSeries(donutSeries) }
            chartModifiers {
                pieChartLegendModifier(binding.pieChartLegend) { setSourceSeries(donutSeries) }
                pieSegmentSelectionModifier()
            }
        }

        donutSeries.animate(Constant.ANIMATION_DURATION)
    }
}