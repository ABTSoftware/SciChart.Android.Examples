//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2021. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// ThemeManager3DChartFragment.kt is part of SCICHART®, High Performance Scientific Charts
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

package com.scichart.examples.fragments.examples3d.style3DChart.kt

import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import com.scichart.charting.themes.ThemeManager
import com.scichart.charting3d.visuals.renderableSeries.metadataProviders.PointMetadataProvider3D
import com.scichart.charting3d.visuals.renderableSeries.metadataProviders.PointMetadataProvider3D.PointMetadata3D
import com.scichart.data.model.DoubleRange
import com.scichart.drawing.utility.ColorUtil.LimeGreen
import com.scichart.examples.R
import com.scichart.examples.components.SpinnerStringAdapter
import com.scichart.examples.data.DataManager
import com.scichart.examples.databinding.ExampleThemeManager3dChartFragmentBinding
import com.scichart.examples.fragments.base.ExampleBaseFragment
import com.scichart.examples.utils.ItemSelectedListenerBase
import com.scichart.examples.utils.scichartExtensions.*

class ThemeManager3DChartFragment : ExampleBaseFragment<ExampleThemeManager3dChartFragmentBinding>() {

    override fun inflateBinding(inflater: LayoutInflater): ExampleThemeManager3dChartFragmentBinding {
        return ExampleThemeManager3dChartFragmentBinding.inflate(inflater)
    }

    override fun initExample(binding: ExampleThemeManager3dChartFragmentBinding) {
        val dataManager = DataManager.getInstance()
        val pointMetaDataProvider = PointMetadataProvider3D()

        binding.surface3d.suspendUpdates {
            xAxis = numericAxis3D { growBy = DoubleRange(0.1, 0.1) }
            yAxis = numericAxis3D { growBy = DoubleRange(0.1, 0.1) }
            zAxis = numericAxis3D { growBy = DoubleRange(0.1, 0.1) }

            renderableSeries {
                scatterRenderableSeries3D {
                    xyzDataSeries3D<Double, Double, Double> {
                        val metadata = pointMetaDataProvider.metadata
                        for (i in 0 until 1250) {
                            val x = dataManager.getGaussianRandomNumber(5.0, 1.5)
                            val y = dataManager.getGaussianRandomNumber(5.0, 1.5)
                            val z = dataManager.getGaussianRandomNumber(5.0, 1.5)
                            append(x, y, z)

                            metadata.add(PointMetadata3D(dataManager.randomColor, dataManager.randomScale))
                        }
                    }
                    spherePointMarker3D { fill = LimeGreen; size = 2f }
                    metadataProvider = pointMetaDataProvider
                }
            }

            chartModifiers { defaultModifiers3D() }
        }

        binding.themeSelector.run {
            adapter = SpinnerStringAdapter(activity, R.array.style_list)
            setSelection(7)
            onItemSelectedListener = object : ItemSelectedListenerBase() {
                override fun onItemSelected(parent: AdapterView<*>?, view: View, position: Int, id: Long) {
                    setTheme(position)
                }
            }
        }
    }

    private fun setTheme(position: Int) {
        binding.surface3d.theme = when (position) {
            BLACK_STEEL -> R.style.SciChart_BlackSteel
            BRIGHT_SPARK -> R.style.SciChart_Bright_Spark
            CHROME -> R.style.SciChart_ChromeStyle
            ELECTRIC -> R.style.SciChart_ElectricStyle
            EXPRESSION_DARK -> R.style.SciChart_ExpressionDarkStyle
            EXPRESSION_LIGHT -> R.style.SciChart_ExpressionLightStyle
            OSCILLOSCOPE -> R.style.SciChart_OscilloscopeStyle
            SCI_CHART_V4_DARK -> R.style.SciChart_SciChartv4DarkStyle
            BERRY_BLUE -> R.style.SciChart_BerryBlue
            else -> ThemeManager.DEFAULT_THEME
        }
    }

    companion object {
        private const val BLACK_STEEL = 0
        private const val BRIGHT_SPARK = 1
        private const val CHROME = 2
        private const val ELECTRIC = 3
        private const val EXPRESSION_DARK = 4
        private const val EXPRESSION_LIGHT = 5
        private const val OSCILLOSCOPE = 6
        private const val SCI_CHART_V4_DARK = 7
        private const val BERRY_BLUE = 8
    }
}
