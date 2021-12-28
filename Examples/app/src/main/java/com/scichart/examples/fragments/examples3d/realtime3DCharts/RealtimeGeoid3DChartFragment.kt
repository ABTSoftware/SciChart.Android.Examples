//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2021. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// CreateRealtimeGeoid3DChartFragment.kt is part of SCICHART®, High Performance Scientific Charts
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

import android.content.Context
import android.graphics.BitmapFactory
import com.scichart.charting.visuals.axes.AutoRange.Never
import com.scichart.charting3d.common.math.Vector3
import com.scichart.charting3d.model.dataSeries.freeSurface.EllipsoidDataSeries3D
import com.scichart.charting3d.visuals.SciChartSurface3D
import com.scichart.charting3d.visuals.renderableSeries.data.DrawMeshAs.SolidMesh
import com.scichart.charting3d.visuals.renderableSeries.data.GradientColorPalette
import com.scichart.core.framework.UpdateSuspender
import com.scichart.core.model.DoubleValues
import com.scichart.data.model.DoubleRange
import com.scichart.drawing.utility.ColorUtil.*
import com.scichart.examples.R
import com.scichart.examples.fragments.base.ExampleSingleChart3DBaseFragment
import com.scichart.examples.utils.scichartExtensions.*
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit
import kotlin.math.pow
import kotlin.math.sin

class RealtimeGeoid3DChartFragment : ExampleSingleChart3DBaseFragment() {
    private val scheduledExecutorService = Executors.newSingleThreadScheduledExecutor()
    private lateinit var schedule: ScheduledFuture<*>

    private lateinit var globeHeightMap: DoubleValues
    private lateinit var dataSeries: EllipsoidDataSeries3D<Double>

    private var frames = 0
    private val buffer = DoubleValues()

    override fun initExample(surface3d: SciChartSurface3D) {
        globeHeightMap = getGlobeHeightMap(requireContext())

        surface3d.suspendUpdates {
            xAxis = numericAxis3D { visibleRange = DoubleRange(-8.0, 8.0); autoRange = Never }
            yAxis = numericAxis3D { visibleRange = DoubleRange(-8.0, 8.0); autoRange = Never }
            zAxis = numericAxis3D { visibleRange = DoubleRange(-8.0, 8.0); autoRange = Never }

            renderableSeries {
                freeSurfaceRenderableSeries3D {
                    ellipsoidDataSeries3D<Double>(SIZE, SIZE) {
                        a = 6.0
                        b = 6.0
                        c = 6.0
                        copyFrom(globeHeightMap)

                        this@RealtimeGeoid3DChartFragment.dataSeries = this
                    }
                    drawMeshAs = SolidMesh
                    stroke = 0x77228b22
                    contourStroke = 0x77228b22
                    strokeThickness = 1f
                    meshColorPalette = GradientColorPalette(
                        intArrayOf(0xFF1D2C6B.toInt(), Blue, Cyan, GreenYellow, Yellow, Red, DarkRed),
                        floatArrayOf(0f, 0.005f, 0.0075f, 0.01f, 0.5f, 0.7f, 1f)
                    )
                    paletteMinimum = Vector3(0f, 6f, 0f)
                    paletteMaximum = Vector3(0f, 7f, 0f)
                }
            }

            chartModifiers { defaultModifiers3D() }

            worldDimensions.assign(200f, 200f, 200f)
        }

        schedule = scheduledExecutorService.scheduleWithFixedDelay({ UpdateSuspender.using(surface3d) {
            val freq = (sin(frames++ * 0.1) + 1.0) / 2.0
            val exp = freq * 10

            val offset = frames % SIZE
            val size = globeHeightMap.size()

            buffer.setSize(size)

            val heightMapItems = globeHeightMap.itemsArray
            val bufferItems = buffer.itemsArray
            for (i in 0 until size) {
                var currentValueIndex = i + offset
                if (currentValueIndex >= size) {
                    currentValueIndex -= SIZE
                }

                val currentValue = heightMapItems[currentValueIndex]
                bufferItems[i] = currentValue + currentValue.pow(exp) * HEIGHT_OFFSET_SCALE
            }

            dataSeries.copyFrom(buffer)
        }}, 0, 33, TimeUnit.MILLISECONDS)
    }

    private fun getGlobeHeightMap(context: Context): DoubleValues {
        val bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.example_globe_heightmap)
        val stepU = bitmap.width / SIZE
        val stepV = bitmap.height / SIZE

        val globeHeightMap = DoubleValues()
        globeHeightMap.setSize(SIZE * SIZE)

        val heightMapItems = globeHeightMap.itemsArray
        for (v in 0 until SIZE) {
            for (u in 0 until SIZE) {
                val index = v * SIZE + u
                val x = u * stepU
                val y = v * stepV

                heightMapItems[index] = red(bitmap.getPixel(x, y)) / 255.0
            }
        }

        bitmap.recycle()

        return globeHeightMap
    }

    override fun onDestroyView() {
        super.onDestroyView()

        schedule.cancel(true)
    }

    companion object {
        private const val SIZE = 100
        private const val HEIGHT_OFFSET_SCALE = 0.5
    }
}