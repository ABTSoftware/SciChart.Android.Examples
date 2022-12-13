//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2021. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// SyncMultipleChartsFragment.kt is part of SCICHART®, High Performance Scientific Charts
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

import android.view.LayoutInflater
import android.view.animation.DecelerateInterpolator
import com.scichart.charting.visuals.SciChartSurface
import com.scichart.data.model.DoubleRange
import com.scichart.examples.R
import com.scichart.examples.databinding.ExampleSyncMultipleChartsFragmentBinding
import com.scichart.examples.fragments.base.ExampleBaseFragment
import com.scichart.examples.utils.scichartExtensions.*
import kotlin.math.sin

class SyncMultipleChartsFragment: ExampleBaseFragment<ExampleSyncMultipleChartsFragmentBinding>() {
    private val sharedXRange = DoubleRange()
    private val sharedYRange = DoubleRange()

    override fun showDefaultModifiersInToolbar(): Boolean = false

    override fun inflateBinding(inflater: LayoutInflater): ExampleSyncMultipleChartsFragmentBinding {
        return ExampleSyncMultipleChartsFragmentBinding.inflate(inflater)
    }

    override fun initExample(binding: ExampleSyncMultipleChartsFragmentBinding) {
        initChart(binding.chart0)
        initChart(binding.chart1)
    }

    private fun initChart(surface: SciChartSurface) {
        surface.theme = R.style.SciChart_NavyBlue

        surface.suspendUpdates {
            xAxes { numericAxis { growBy = DoubleRange(0.1, 0.1); visibleRange = sharedXRange} }
            yAxes { numericAxis { growBy = DoubleRange(0.1, 0.1); visibleRange = sharedYRange} }
            renderableSeries {
                fastLineRenderableSeries {
                    xyDataSeries<Double, Double> {
                        for (i in 1 until POINTS_COUNT) {
                            append(i.toDouble(), POINTS_COUNT * sin(i * Math.PI * 0.1) / i)
                        }
                    }
                    strokeStyle = SolidPenStyle(0xFF47bde6)

                    sweepAnimation { interpolator = DecelerateInterpolator() }
                }
            }

            chartModifiers {
                modifierGroup(context) {
                    zoomExtentsModifier()
                    pinchZoomModifier()
                    rolloverModifier { receiveHandledEvents = true }
                    xAxisDragModifier { receiveHandledEvents = true }
                    yAxisDragModifier { receiveHandledEvents = true }
                }.run {
                    motionEventGroup = "ModifiersSharedEventsGroup"
                    receiveHandledEvents = true
                }
            }
        }

        surface.zoomExtents()
    }

    companion object {
        private const val POINTS_COUNT = 500
    }
}