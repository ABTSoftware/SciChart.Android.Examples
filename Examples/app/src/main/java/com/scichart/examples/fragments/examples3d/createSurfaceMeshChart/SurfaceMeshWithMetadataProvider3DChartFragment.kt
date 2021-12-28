//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2021. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// SurfaceMeshWithMetadataProvider3DChartFragment.kt is part of SCICHART®, High Performance Scientific Charts
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

package com.scichart.examples.fragments.examples3d.createSurfaceMeshChart.kt

import com.scichart.charting3d.visuals.SciChartSurface3D
import com.scichart.charting3d.visuals.axes.NumericAxis3D
import com.scichart.charting3d.visuals.renderableSeries.data.DrawMeshAs.SolidMesh
import com.scichart.charting3d.visuals.renderableSeries.data.GradientColorPalette
import com.scichart.charting3d.visuals.renderableSeries.metadataProviders.ISurfaceMeshMetadataProvider3D
import com.scichart.charting3d.visuals.renderableSeries.metadataProviders.MetadataProvider3DBase
import com.scichart.charting3d.visuals.renderableSeries.surfaceMesh.SurfaceMeshRenderPassData3D
import com.scichart.charting3d.visuals.renderableSeries.surfaceMesh.SurfaceMeshRenderableSeries3D
import com.scichart.core.framework.UpdateSuspender
import com.scichart.core.model.IntegerValues
import com.scichart.drawing.utility.ColorUtil.*
import com.scichart.examples.data.DataManager
import com.scichart.examples.fragments.base.ExampleSingleChart3DBaseFragment
import com.scichart.examples.utils.scichartExtensions.*
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit
import kotlin.math.pow

class SurfaceMeshWithMetadataProvider3DChartFragment : ExampleSingleChart3DBaseFragment() {
    private val scheduledExecutorService = Executors.newSingleThreadScheduledExecutor()
    private lateinit var schedule: ScheduledFuture<*>

    override fun initExample(surface3d: SciChartSurface3D) {
        val meshDataSeries0 = UniformGridDataSeries3D<Double, Double, Double>(X_SIZE, Z_SIZE)
        val meshDataSeries1 = UniformGridDataSeries3D<Double, Double, Double>(X_SIZE, Z_SIZE)
        for (x in 48 downTo 24) {
            val y = (x - 23.7).pow(0.3)
            val y2 = (49.5 - x).pow(0.3)

            meshDataSeries0.updateYAt(x, 24, y)
            meshDataSeries1.updateYAt(x, 24, y2 + 1.505)
        }

        for (x in 24 downTo 0) {
            for (z in 49 downTo 26) {
                val y = (z - 23.7).pow(0.3)
                val y2 = (50.5 - z).pow(0.3) + 1.505

                meshDataSeries0.updateYAt(x + 24, 49 - z, y)
                meshDataSeries0.updateYAt(z - 1, 24 - x, y)

                meshDataSeries1.updateYAt(x + 24, 49 - z, y2)
                meshDataSeries1.updateYAt(z - 1, 24 - x, y2)

                meshDataSeries0.updateYAt(24 - x, 49 - z, y)
                meshDataSeries0.updateYAt(49 - z, 24 - x, y)

                meshDataSeries1.updateYAt(24 - x, 49 - z, y2)
                meshDataSeries1.updateYAt(49 - z, 24 - x, y2)

                meshDataSeries0.updateYAt(x + 24, z - 1, y)
                meshDataSeries0.updateYAt(z - 1, x + 24, y)

                meshDataSeries1.updateYAt(x + 24, z - 1, y2)
                meshDataSeries1.updateYAt(z - 1, x + 24, y2)

                meshDataSeries0.updateYAt(24 - x, z - 1, y)
                meshDataSeries0.updateYAt(49 - z, x + 24, y)

                meshDataSeries1.updateYAt(24 - x, z - 1, y2)
                meshDataSeries1.updateYAt(49 - z, x + 24, y2)
            }
        }
        val palette = GradientColorPalette(
            intArrayOf(DarkBlue, Blue, CadetBlue, Cyan, LimeGreen, GreenYellow, Yellow, Tomato, IndianRed, Red, DarkRed),
            floatArrayOf(0f, .1f, .2f, .3f, .4f, .5f, .6f, .7f, .8f, .9f, 1f)
        )

        val rSeries0 = SurfaceMeshRenderableSeries3D().apply {
            dataSeries = meshDataSeries0
            drawMeshAs = SolidMesh
            drawSkirt = false
            meshColorPalette = palette
            metadataProvider = SurfaceMeshMetadataProvider3D()
        }
        val rSeries1 = SurfaceMeshRenderableSeries3D().apply {
            dataSeries = meshDataSeries1
            drawMeshAs = SolidMesh
            drawSkirt = false
            meshColorPalette = palette
            metadataProvider = SurfaceMeshMetadataProvider3D()
        }

        surface3d.suspendUpdates {
            xAxis = createNumericAxis3D()
            yAxis = createNumericAxis3D()
            zAxis = createNumericAxis3D()

            renderableSeries {
                rSeries(rSeries0)
                rSeries(rSeries1)
            }
            chartModifiers { defaultModifiers3D() }
        }

        schedule = scheduledExecutorService.scheduleWithFixedDelay({
            UpdateSuspender.using(surface3d) {
                rSeries0.invalidateMetadata()
                rSeries1.invalidateMetadata()
            }
        }, 0, 10, TimeUnit.MILLISECONDS)
    }

    private fun createNumericAxis3D(): NumericAxis3D {
        return numericAxis3D {
            drawMajorBands = false
            drawLabels = false
            drawMajorGridLines = false
            drawMajorTicks = false
            drawMinorGridLines = false
            drawMinorTicks = false
            planeBorderThickness = 0f
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        schedule.cancel(true)
    }

    private class SurfaceMeshMetadataProvider3D : MetadataProvider3DBase<SurfaceMeshRenderableSeries3D>(SurfaceMeshRenderableSeries3D::class.java), ISurfaceMeshMetadataProvider3D {
        override fun updateMeshColors(cellColors: IntegerValues) {
            val currentRenderPassData = renderableSeries!!.currentRenderPassData as SurfaceMeshRenderPassData3D
            val dataManager = DataManager.getInstance()

            val countX = currentRenderPassData.countX - 1
            val countZ = currentRenderPassData.countZ - 1
            cellColors.setSize(currentRenderPassData.pointsCount)

            val items = cellColors.itemsArray
            for (x in 0 until countX) {
                for (z in 0 until countZ) {
                    val index = x * countZ + z

                    val color: Int = if (x in 20..26 && z > 0 && z < 47 || z in 20..26 && x > 0 && x < 47) {
                        // need to use special transparent color definition for MetadataProvider3D for it to work
                        TRANSPARENT
                    } else {
                        dataManager.randomColor
                    }
                    items[index] = color
                }
            }
        }
    }

    companion object {
        const val X_SIZE = 49
        const val Z_SIZE = 49
    }
}
