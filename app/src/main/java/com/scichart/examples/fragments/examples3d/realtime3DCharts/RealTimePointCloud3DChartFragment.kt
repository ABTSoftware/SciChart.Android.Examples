//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2021. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// RealTimePointCloud3DChartFragment.kt is part of SCICHART®, High Performance Scientific Charts
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

package com.scichart.examples.fragments.examples3d.realtime3DCharts.kt

import com.scichart.charting3d.visuals.SciChartSurface3D
import com.scichart.core.model.DoubleValues
import com.scichart.data.model.DoubleRange
import com.scichart.examples.data.DataManager
import com.scichart.examples.fragments.base.ExampleSingleChart3DBaseFragment
import com.scichart.examples.utils.scichartExtensions.*
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit

class RealTimePointCloud3DChartFragment : ExampleSingleChart3DBaseFragment() {
    private val dataSeries = XyzDataSeries3D<Double, Double, Double>()

    private val xData = DoubleValues()
    private val yData = DoubleValues()
    private val zData = DoubleValues()

    private val scheduledExecutorService = Executors.newSingleThreadScheduledExecutor()
    private lateinit var schedule: ScheduledFuture<*>

    override fun initExample(surface3d: SciChartSurface3D) {
        val dataManager = DataManager.getInstance()
        for (i in 0 until 1000) {
            xData.add(dataManager.getGaussianRandomNumber(5.0, 1.5))
            yData.add(dataManager.getGaussianRandomNumber(5.0, 1.5))
            zData.add(dataManager.getGaussianRandomNumber(5.0, 1.5))
        }
        dataSeries.append(xData, yData, zData)

        surface3d.suspendUpdates {
            xAxis = numericAxis3D { growBy = DoubleRange(0.1, 0.1) }
            yAxis = numericAxis3D { growBy = DoubleRange(0.1, 0.1) }
            zAxis = numericAxis3D { growBy = DoubleRange(0.1, 0.1) }

            renderableSeries {
                scatterRenderableSeries3D {
                    dataSeries = this@RealTimePointCloud3DChartFragment.dataSeries
                    ellipsePointMarker3D { fill = 0x7747bde6; size = 3f }
                }
            }

            chartModifiers { defaultModifiers3D() }
        }

        schedule = scheduledExecutorService.scheduleWithFixedDelay(scheduledRunnable, 0, 10, TimeUnit.MILLISECONDS)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        schedule.cancel(true)
    }

    private val scheduledRunnable: Runnable = object : Runnable {
        private val random = Random()

        override fun run() {
            binding.surface3d.suspendUpdates {
                val xItems: DoubleArray = xData.itemsArray
                val yItems: DoubleArray = yData.itemsArray
                val zItems: DoubleArray = zData.itemsArray

                for (i in 0 until dataSeries.count) {
                    xItems[i] += random.nextDouble() - 0.5
                    yItems[i] += random.nextDouble() - 0.5
                    zItems[i] += random.nextDouble() - 0.5
                }

                dataSeries.updateRangeXyzAt(0, xData, yData, zData)
            }
        }
    }
}