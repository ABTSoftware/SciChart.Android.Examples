//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2021. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// AddPointsPerformanceChartFragment.kt is part of SCICHART®, High Performance Scientific Charts
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

package com.scichart.examples.fragments.examples2d.manipulateSeries.kt

import android.os.Bundle
import android.view.LayoutInflater
import com.scichart.charting.model.dataSeries.IXyDataSeries
import com.scichart.data.model.DoubleRange
import com.scichart.data.model.ISciList
import com.scichart.drawing.utility.ColorUtil
import com.scichart.examples.data.RandomWalkGenerator
import com.scichart.examples.databinding.ExampleAddPointsPerformanceFragmentBinding
import com.scichart.examples.fragments.base.ExampleBaseFragment
import com.scichart.examples.utils.scichartExtensions.*
import java.util.*

class AddPointsPerformanceChartFragment: ExampleBaseFragment<ExampleAddPointsPerformanceFragmentBinding>() {
    override fun inflateBinding(inflater: LayoutInflater): ExampleAddPointsPerformanceFragmentBinding {
        return ExampleAddPointsPerformanceFragmentBinding.inflate(inflater)
    }

    override fun initExample(binding: ExampleAddPointsPerformanceFragmentBinding) {
        binding.append10k.setOnClickListener { onAppendPoints(10000) }
        binding.append100k.setOnClickListener { onAppendPoints(10000) }
        binding.appendMLN.setOnClickListener { onAppendPoints(1000000) }
        binding.reset.setOnClickListener { onReset() }

        binding.surface.suspendUpdates {
            xAxes { numericAxis { visibleRange = DoubleRange(0.0, 10.0); axisTitle = "X Axis" }}
            yAxes { numericAxis { visibleRange = DoubleRange(0.0, 10.0); axisTitle = "Y Axis" }}

            chartModifiers { defaultModifiers() }
        }
    }

    private fun onAppendPoints(count: Int) {
        val biasRandom = Random()
        val randomWalkSeries = RandomWalkGenerator(100).setBias(biasRandom.nextDouble() / 100).getRandomWalkSeries(count)

        binding.surface.suspendUpdates {
            renderableSeries {
                fastLineRenderableSeries {
                    xyDataSeries<Double, Double> { append(randomWalkSeries.xValues, randomWalkSeries.yValues) }
                    strokeStyle = SolidPenStyle(ColorUtil.rgb(random.nextInt(255), random.nextInt(255), random.nextInt(255)))
                }
            }

            animateZoomExtents(500)
        }
    }

    private fun onReset() {
        binding.surface.suspendUpdates {
            renderableSeries.forEach {
                it.dataSeries.clear()
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        binding.surface.suspendUpdates {
            val size = renderableSeries.size
            outState.putInt("seriesCount", size)
            for (i in 0 until size) {
                val rSeries = renderableSeries[i]
                val dataSeries = rSeries.dataSeries as IXyDataSeries<*, *>
                outState.putInt("seriesColor$i", rSeries.strokeStyle.color)
                outState.putParcelable("xValues$i", dataSeries.xValues)
                outState.putParcelable("yValues$i", dataSeries.yValues)
            }
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        savedInstanceState?.run {
            val seriesCount = savedInstanceState.getInt("seriesCount")
            for (i in 0 until seriesCount) {
                val seriesColor = getInt("seriesColor$i")
                val xValues: ISciList<Double> = getParcelable("xValues$i")!!
                val yValues: ISciList<Double> = getParcelable("yValues$i")!!

                binding.surface.renderableSeries.run {
                    fastLineRenderableSeries {
                        xyDataSeries<Double, Double> { append(xValues, yValues) }
                        strokeStyle = SolidPenStyle(seriesColor)
                    }
                }
            }
        }
    }

    companion object {
        private val random = Random()
    }
}