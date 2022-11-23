//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2021. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// LogarithmicAxisFragment.kt is part of SCICHART®, High Performance Scientific Charts
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

package com.scichart.examples.fragments.examples2d.modifyAxisBehavior.kt

import com.scichart.charting.visuals.SciChartSurface
import com.scichart.charting.visuals.axes.AutoRange
import com.scichart.data.model.DoubleRange
import com.scichart.drawing.utility.ColorUtil
import com.scichart.examples.fragments.base.ExampleSingleChartBaseFragment
import com.scichart.examples.utils.scichartExtensions.*
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit

class FixedWidthAxisFragment : ExampleSingleChartBaseFragment() {

    private val ds1 = XyDataSeries<Double, Double>().apply { fifoCapacity = FIFO_CAPACITY }

    private val scheduledExecutorService = Executors.newSingleThreadScheduledExecutor()
    private lateinit var schedule: ScheduledFuture<*>

    private var index = 0.0
    private var value = 0.0
    private var isIncreasing = true

    override fun initExample(surface: SciChartSurface) {
        val line1Color = ColorUtil.argb(0xFF, 0x47, 0xBD, 0xE6)

        binding.surface.suspendUpdates {
            xAxes {
                numericAxis {
//                    visibleRange = xVisibleRange
                    autoRange = AutoRange.Always
                }
            }
            yAxes {
                numericAxis {
                    fixedSize = 200
                    autoRange = AutoRange.Always
                }
            }
            renderableSeries {
                fastLineRenderableSeries {
                    dataSeries = ds1
                    strokeStyle = SolidPenStyle(line1Color)
                    ellipsePointMarker { setSize(5); fillStyle = SolidBrushStyle(line1Color) }
                }
            }

            schedule = scheduledExecutorService.scheduleWithFixedDelay(
                insertRunnable,
                0,
                TIME_INTERVAL,
                TimeUnit.MILLISECONDS
            )
        }
    }

    private val insertRunnable = Runnable {
        binding.surface.suspendUpdates {
            ds1.append(index, value * value)
            index++
            if (value == 200.0) {
                isIncreasing = false
            }
            if (value == 0.0) {
                isIncreasing = true
            }
            if (isIncreasing) {
                value += 1.0
            } else {
                value -= 1.0
            }
        }
    }

    companion object {
        private const val FIFO_CAPACITY = 50
        private const val TIME_INTERVAL: Long = 30
    }
}