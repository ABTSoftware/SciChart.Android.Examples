//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2021. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// AddRemoveSeriesFragment.kt is part of SCICHART®, High Performance Scientific Charts
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
import com.scichart.charting.visuals.axes.AutoRange.Always
import com.scichart.charting.visuals.renderableSeries.BaseMountainRenderableSeries
import com.scichart.data.model.ISciList
import com.scichart.drawing.utility.ColorUtil
import com.scichart.examples.data.DataManager
import com.scichart.examples.databinding.ExampleAddRemoveSeriesFragmentBinding
import com.scichart.examples.fragments.base.ExampleBaseFragment
import com.scichart.examples.utils.scichartExtensions.*
import java.util.*

class AddRemoveSeriesFragment: ExampleBaseFragment<ExampleAddRemoveSeriesFragmentBinding>() {
    override fun inflateBinding(inflater: LayoutInflater): ExampleAddRemoveSeriesFragmentBinding {
        return ExampleAddRemoveSeriesFragmentBinding.inflate(inflater)
    }

    override fun initExample(binding: ExampleAddRemoveSeriesFragmentBinding) {
        binding.surface.suspendUpdates {
            xAxes { numericAxis { autoRange = Always; axisTitle = "X Axis" }}
            yAxes { numericAxis { autoRange = Always; axisTitle = "Y Axis" }}

            chartModifiers { defaultModifiers() }
        }

        binding.addSeries.setOnClickListener { add() }
        binding.removeSeries.setOnClickListener { remove() }
        binding.reset.setOnClickListener { binding.surface.renderableSeries.clear() }
    }

    private fun add() {
        binding.surface.suspendUpdates {
            val random = Random()
            val randomDoubleSeries = DataManager.getInstance().getRandomDoubleSeries(150)

            renderableSeries {
                fastMountainRenderableSeries {
                    xyDataSeries<Double, Double> { append(randomDoubleSeries.xValues, randomDoubleSeries.yValues) }
                    strokeStyle = SolidPenStyle(ColorUtil.argb(random.nextInt(255), random.nextInt(255), random.nextInt(255), 150))
                    areaStyle = SolidBrushStyle(ColorUtil.rgb(random.nextInt(255), random.nextInt(255), random.nextInt(255)))
                }
            }
        }
    }

    private fun remove() {
        binding.surface.suspendUpdates {
            if (!renderableSeries.isEmpty()) {
                renderableSeries.removeAt(0)
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        binding.surface.suspendUpdates {
            val size = renderableSeries.size
            outState.putInt("seriesCount", size)
            for (i in 0 until size) {
                val renderableSeries = renderableSeries[i]
                val series = renderableSeries.dataSeries as IXyDataSeries<*, *>
                if (renderableSeries is BaseMountainRenderableSeries) {
                    outState.putInt("areaColor$i", renderableSeries.areaStyle.hashCode())
                }
                outState.putInt("seriesColor$i", renderableSeries.strokeStyle.color)
                outState.putParcelable("xValues$i", series.xValues)
                outState.putParcelable("yValues$i", series.yValues)
            }
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        savedInstanceState?.run {
            val seriesCount = getInt("seriesCount")
            for (i in 0 until seriesCount) {
                val seriesColor = getInt("seriesColor$i")
                val areaColor = getInt("areaColor$i")
                val xValues: ISciList<Double> = getParcelable("xValues$i")!!
                val yValues: ISciList<Double> = getParcelable("yValues$i")!!

                binding.surface.renderableSeries.run {
                    fastMountainRenderableSeries {
                        xyDataSeries<Double, Double> { append(xValues, yValues) }
                        strokeStyle = SolidPenStyle(seriesColor)
                        areaStyle = SolidBrushStyle(areaColor)
                    }
                }
            }
        }
    }
}