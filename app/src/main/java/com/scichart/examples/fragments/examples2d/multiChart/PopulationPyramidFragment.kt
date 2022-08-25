//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2022. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// PopulationPyramidFragment.kt is part of SCICHART®, High Performance Scientific Charts
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

package com.scichart.examples.fragments.examples2d.multiChart.kt

import androidx.lifecycle.lifecycleScope
import com.scichart.charting.model.dataSeries.IXyDataSeries
import com.scichart.charting.numerics.labelProviders.NumericLabelProvider
import com.scichart.charting.visuals.SciChartSurface
import com.scichart.charting.visuals.annotations.TextAnnotation
import com.scichart.charting.visuals.axes.AxisAlignment
import com.scichart.drawing.common.FontStyle
import com.scichart.examples.fragments.base.ExampleSingleChartBaseFragment
import com.scichart.examples.utils.PopData
import com.scichart.examples.utils.PopulationPyramidUtil
import com.scichart.examples.utils.populationpyramid.PyramidLabelProvider
import com.scichart.examples.utils.populationpyramid.SharedPaletteProvider
import com.scichart.examples.utils.scichartExtensions.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader

class PopulationPyramidFragment : ExampleSingleChartBaseFragment() {

    private var maleDataList = mutableListOf<PopData>()
    private var femaleDataList = mutableListOf<PopData>()

    private lateinit var maleDataSeriesXy: IXyDataSeries<Double, Double>
    private lateinit var femaleDataSeriesXy: IXyDataSeries<Double, Double>

    private val sharedPaletteProvider = SharedPaletteProvider(7.0, 16.0)

    private lateinit var textAnnotation: TextAnnotation
    private var mainIndex = 0

    override fun initExample(surface: SciChartSurface) {
        maleDataList = PopulationPyramidUtil.readCsv("data/male_population_data_formated.csv", true, requireActivity()).toMutableList()
        femaleDataList = PopulationPyramidUtil.readCsv("data/female_population_data_formated.csv", false, requireActivity()).toMutableList()

        val maleData = maleDataList.getOrNull(mainIndex)?.population?.reversed()
        val femaleData = femaleDataList.getOrNull(mainIndex)?.population?.reversed()

        textAnnotation = TextAnnotation(requireContext())
        textAnnotation.text = maleDataList.getOrNull(mainIndex)?.year
        textAnnotation.fontStyle = FontStyle(150f, -0x440363d7)
        textAnnotation.x1 = 0
        textAnnotation.y1 = -500

        val data = 0.0
        maleDataSeriesXy = XyDataSeries<Double, Double>().apply {
            maleData?.forEachIndexed { i, value ->
                append(data + i, value)
            }
        }
        femaleDataSeriesXy = XyDataSeries<Double, Double>().apply {
            femaleData?.forEachIndexed { i, value -> append(data + i, value) }
        }
        surface.suspendUpdates {
            annotations.add(textAnnotation)
            xAxes {
                numericAxis {
                    axisAlignment = AxisAlignment.Left
                    labelProvider = NumericLabelProvider(PyramidLabelProvider())
                }
            }

            yAxes {
                numericAxis {
                    axisAlignment = AxisAlignment.Bottom
                }
            }

            renderableSeries {
                verticallyStackedColumnsCollection {
                    stackedColumnRenderableSeries {
                        dataSeries = maleDataSeriesXy
                        paletteProvider = sharedPaletteProvider
                    }
                    stackedColumnRenderableSeries {
                        dataSeries = femaleDataSeriesXy
                        paletteProvider = sharedPaletteProvider
                    }
                }
            }

            chartModifiers { defaultModifiers() }
        }

        lifecycleScope.launch {
            while (true) {
                delay(1500)
                refreshData()
            }
        }
    }

    private fun refreshData() {
        if (maleDataList.size > mainIndex) {
            mainIndex++
        } else {
            mainIndex = 0
        }

        activity?.runOnUiThread {
            binding.surface.suspendUpdates {

                textAnnotation.text = maleDataList.getOrNull(mainIndex)?.year
                val maleData = maleDataList.getOrNull(mainIndex)
                maleData?.population?.reversed()?.forEachIndexed { index, d ->
                    maleDataSeriesXy.updateYAt(index, d)
                }
                val femaleData = femaleDataList.getOrNull(mainIndex)
                femaleData?.population?.reversed()?.forEachIndexed { index, d ->
                    femaleDataSeriesXy.updateYAt(index, d)
                }
            }
        }
    }
}