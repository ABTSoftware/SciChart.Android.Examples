//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2021. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// RealTimeGhostTracesFragment.kt is part of SCICHART®, High Performance Scientific Charts
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

package com.scichart.examples.fragments.examples2d.createRealtimeCharts.kt

import android.view.LayoutInflater
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import com.scichart.charting.model.dataSeries.XyDataSeries
import com.scichart.charting.visuals.SciChartSurface
import com.scichart.charting.visuals.axes.AutoRange
import com.scichart.core.utility.NumberUtil
import com.scichart.data.model.DoubleRange
import com.scichart.drawing.utility.ColorUtil
import com.scichart.examples.data.DataManager
import com.scichart.examples.databinding.ExampleRealTimeGhostTracesFragmentBinding
import com.scichart.examples.fragments.base.ExampleBaseFragment
import com.scichart.examples.utils.scichartExtensions.*
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit

class RealTimeGhostTracesFragment : ExampleBaseFragment<ExampleRealTimeGhostTracesFragmentBinding>(), OnSeekBarChangeListener {
    private val scheduledExecutorService = Executors.newSingleThreadScheduledExecutor()
    private var schedule: ScheduledFuture<*>? = null

    override fun showDefaultModifiersInToolbar(): Boolean = false

    override fun inflateBinding(inflater: LayoutInflater): ExampleRealTimeGhostTracesFragmentBinding {
        return ExampleRealTimeGhostTracesFragmentBinding.inflate(inflater)
    }

    override fun initExample(binding: ExampleRealTimeGhostTracesFragmentBinding) {
        binding.surface.run {
            xAxes { numericAxis  { autoRange = AutoRange.Always } }
            yAxes { numericAxis  {
                growBy = DoubleRange(0.1, 0.1)
                autoRange = AutoRange.Never
                visibleRange = DoubleRange(-2.0, 2.0)
            }}
            renderableSeries {
                val seriesColor = (0xff68bcae).toInt();
                fastLineRenderableSeries { strokeStyle = SolidPenStyle(seriesColor) }
                fastLineRenderableSeries { strokeStyle = SolidPenStyle(ColorUtil.argb(seriesColor, 0.9f)) }
                fastLineRenderableSeries { strokeStyle = SolidPenStyle(ColorUtil.argb(seriesColor, 0.8f)) }
                fastLineRenderableSeries { strokeStyle = SolidPenStyle(ColorUtil.argb(seriesColor, 0.7f)) }
                fastLineRenderableSeries { strokeStyle = SolidPenStyle(ColorUtil.argb(seriesColor, 0.62f)) }
                fastLineRenderableSeries { strokeStyle = SolidPenStyle(ColorUtil.argb(seriesColor, 0.55f)) }
                fastLineRenderableSeries { strokeStyle = SolidPenStyle(ColorUtil.argb(seriesColor, 0.45f)) }
                fastLineRenderableSeries { strokeStyle = SolidPenStyle(ColorUtil.argb(seriesColor, 0.35f)) }
                fastLineRenderableSeries { strokeStyle = SolidPenStyle(ColorUtil.argb(seriesColor, 0.25f)) }
                fastLineRenderableSeries { strokeStyle = SolidPenStyle(ColorUtil.argb(seriesColor, 0.15f)) }
            }
        }

        binding.seekBar.run {
            setOnSeekBarChangeListener(this@RealTimeGhostTracesFragment)
            onProgressChanged(this, this.progress, false)
        }
    }

    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
        if (progress > 0) {
            binding.speedValue.text = String.format("%d ms", progress)

            schedule?.cancel(true)
            schedule = scheduledExecutorService.scheduleWithFixedDelay(scheduledRunnable, 0, progress.toLong(), TimeUnit.MILLISECONDS)
        }
    }

    override fun onStartTrackingTouch(seekBar: SeekBar?) { }
    override fun onStopTrackingTouch(seekBar: SeekBar?) { }

    private val scheduledRunnable: Runnable = object : Runnable {
        private var lastAmplitude = 1.0
        private val phase = 0.0
        private val random = Random()
        override fun run() {
            val surface = binding.surface
            surface.suspendUpdates {
                val dataSeries = XyDataSeries<Double, Double>()
                val randomAmplitude = NumberUtil.constrain(lastAmplitude + (random.nextDouble() - 0.5), -2.0, 2.0)
                val noisySinewave = DataManager.getInstance().getNoisySinewave(randomAmplitude, phase, 1000, 0.25)
                lastAmplitude = randomAmplitude

                dataSeries.append(noisySinewave.xValues, noisySinewave.yValues)

                reassignRenderableSeries(surface, dataSeries)
            }
        }
    }

    private fun reassignRenderableSeries(surface: SciChartSurface, dataSeries: XyDataSeries<Double, Double>) {
        surface.suspendUpdates {
            val rs = surface.renderableSeries

            // shift old data series
            rs[9].dataSeries = rs[8].dataSeries
            rs[8].dataSeries = rs[7].dataSeries
            rs[7].dataSeries = rs[6].dataSeries
            rs[6].dataSeries = rs[5].dataSeries
            rs[5].dataSeries = rs[4].dataSeries
            rs[4].dataSeries = rs[3].dataSeries
            rs[3].dataSeries = rs[2].dataSeries
            rs[2].dataSeries = rs[1].dataSeries
            rs[1].dataSeries = rs[0].dataSeries

            // use new data series to draw first renderable series
            rs[0].dataSeries = dataSeries
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        schedule?.cancel(true)
    }
}
