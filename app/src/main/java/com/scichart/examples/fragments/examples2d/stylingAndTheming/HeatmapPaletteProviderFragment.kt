//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2021. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// HeatmapPaletteProviderFragment.kt is part of SCICHART®, High Performance Scientific Charts
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

package com.scichart.examples.fragments.examples2d.stylingAndTheming.kt

import android.graphics.Color
import android.view.LayoutInflater
import android.widget.SeekBar
import com.scichart.charting.visuals.axes.AxisAlignment.Bottom
import com.scichart.charting.visuals.axes.AxisAlignment.Right
import com.scichart.charting.visuals.renderableSeries.FastUniformHeatmapRenderableSeries
import com.scichart.charting.visuals.renderableSeries.data.UniformHeatmapRenderPassData
import com.scichart.charting.visuals.renderableSeries.paletteProviders.IUniformHeatmapPaletteProvider
import com.scichart.charting.visuals.renderableSeries.paletteProviders.PaletteProviderBase
import com.scichart.core.model.DoubleValues
import com.scichart.core.model.IValues
import com.scichart.examples.R
import com.scichart.examples.databinding.ExampleHeatmapPaletteFragmentBinding
import com.scichart.examples.fragments.base.ExampleBaseFragment
import com.scichart.examples.utils.scichartExtensions.*
import java.util.*
import kotlin.math.sin
import kotlin.math.sqrt

class HeatmapPaletteProviderFragment : ExampleBaseFragment<ExampleHeatmapPaletteFragmentBinding>(), SeekBar.OnSeekBarChangeListener {

    private val heatmapPaletteProvider = CustomUniformHeatMapProvider()

    override fun inflateBinding(inflater: LayoutInflater): ExampleHeatmapPaletteFragmentBinding {
        return ExampleHeatmapPaletteFragmentBinding.inflate(inflater)
    }

    override fun initExample(binding: ExampleHeatmapPaletteFragmentBinding) {
        binding.surface.theme = R.style.SciChart_NavyBlue

        binding.seekBar.run {
            setOnSeekBarChangeListener(this@HeatmapPaletteProviderFragment)
            heatmapPaletteProvider.setThresholdValue(progress.toDouble())
        }

        binding.surface.suspendUpdates {
            xAxes { numericAxis { axisAlignment = Bottom } }
            yAxes { numericAxis { axisAlignment = Right } }
            renderableSeries {
                fastUniformHeatmapRenderableSeries {
                    uniformHeatmapDataSeries<Int, Int, Double>(WIDTH, HEIGHT) {
                        updateZValues(createValues())
                    }
                    minimum = 0.0
                    maximum = 200.0
                    this.paletteProvider = heatmapPaletteProvider
                }
            }
            chartModifiers { defaultModifiers() }
        }
    }

    private fun createValues(): IValues<Double> {
        val values = DoubleValues(WIDTH * HEIGHT)

        val random = Random()
        val angle = Math.PI * 2
        val cx = 150.0
        val cy = 100.0
        for (x in 0 until WIDTH) {
            for (y in 0 until HEIGHT) {
                val v = (1 + sin(x * 0.04 + angle)) * 50 + (1 + sin(y * 0.1 + angle)) * 50 * (1 + sin(angle * 2))
                val r = sqrt((x - cx) * (x - cx) + (y - cy) * (y - cy))
                val exp = 0.0.coerceAtLeast(1 - r * 0.008)

                values.add(v * exp + random.nextDouble() * 50)
            }
        }

        return values
    }

    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
        heatmapPaletteProvider.setThresholdValue(progress.toDouble())
        binding.thresholdValue.text = String.format("%d", progress)
    }

    override fun onStartTrackingTouch(seekBar: SeekBar?) {}

    override fun onStopTrackingTouch(seekBar: SeekBar?) {}

    private class CustomUniformHeatMapProvider : PaletteProviderBase<FastUniformHeatmapRenderableSeries>(FastUniformHeatmapRenderableSeries::class.java), IUniformHeatmapPaletteProvider {
        private var thresholdValue = 0.0

        fun setThresholdValue(thresholdValue: Double) {
            this.thresholdValue = thresholdValue
            renderableSeries?.invalidateElement()
        }

        override fun shouldSetColors(): Boolean = false

        override fun update() {
            val renderableSeries = renderableSeries
            val currentRenderPassData = renderableSeries!!.currentRenderPassData as UniformHeatmapRenderPassData

            val zValues = currentRenderPassData.zValues
            val zColors = currentRenderPassData.zColors

            val size = zValues.size()
            zColors.setSize(size)

            // working with array is much faster than calling set() many times
            val zValuesArray = zValues.itemsArray
            val zColorsArray = zColors.itemsArray
            for (zIndex in 0 until size) {
                val value = zValuesArray[zIndex]
                zColorsArray[zIndex] = if (value < thresholdValue) Color.BLACK else Color.WHITE
            }
        }
    }

    companion object {
        private const val WIDTH = 300
        private const val HEIGHT = 200
    }
}