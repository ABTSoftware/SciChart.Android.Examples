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
import com.scichart.examples.utils.scichartExtensions.*

class DonutChartFragment : ExampleBaseFragment<ExampleSinglePieChartWithLegendFragmentBinding>() {

    override fun inflateBinding(inflater: LayoutInflater): ExampleSinglePieChartWithLegendFragmentBinding {
        return ExampleSinglePieChartWithLegendFragmentBinding.inflate(inflater)
    }

    override fun initExample(binding: ExampleSinglePieChartWithLegendFragmentBinding) {
        val donutSeries = DonutRenderableSeries().apply {
            segmentsCollection {
                pieSegment { value = 40.0; title = "Green"; fillStyle = RadialGradientBrushStyle(0xff84BC3D, 0xff5B8829) }
                pieSegment { value = 10.0; title = "Red"; fillStyle = RadialGradientBrushStyle(0xffe04a2f, 0xffB7161B) }
                pieSegment { value = 20.0; title = "Blue"; fillStyle = RadialGradientBrushStyle(0xff4AB6C1, 0xff2182AD) }
                pieSegment { value = 15.0; title = "Yellow"; fillStyle = RadialGradientBrushStyle(0xffFFFF00, 0xfffed325) }
            }
        }

        binding.pieChart.run {
            renderableSeries { rSeries(donutSeries) }
            chartModifiers {
                pieChartLegendModifier(binding.pieChartLegend) { setSourceSeries(donutSeries) }
                pieSegmentSelectionModifier()
            }
        }

        donutSeries.animate(800)
    }
}