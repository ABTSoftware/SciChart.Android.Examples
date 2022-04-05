//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2021. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// SurfaceMeshFloorAndCeiling3DChartFragment.kt is part of SCICHART®, High Performance Scientific Charts
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
import com.scichart.charting3d.visuals.renderableSeries.data.DrawMeshAs.SolidWireframe
import com.scichart.charting3d.visuals.renderableSeries.data.GradientColorPalette
import com.scichart.data.model.DoubleRange
import com.scichart.drawing.utility.ColorUtil.*
import com.scichart.examples.fragments.base.ExampleSingleChart3DBaseFragment
import com.scichart.examples.utils.scichartExtensions.*

class SurfaceMeshFloorAndCeiling3DChartFragment : ExampleSingleChart3DBaseFragment() {
    override fun initExample(surface3d: SciChartSurface3D) {
        val data = arrayOf(
            doubleArrayOf(-1.43, -2.95, -2.97, -1.81, -1.33, -1.53, -2.04, 2.08, 1.94, 1.42, 1.58),
            doubleArrayOf(1.77, 1.76, -1.1, -0.26, 0.72, 0.64, 3.26, 3.2, 3.1, 1.94, 1.54),
            doubleArrayOf(0.0, 0.0, 0.0, 0.0, 0.0, 3.7, 3.7, 3.7, 3.7, -0.48, -0.48),
            doubleArrayOf(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0)
        )

        val palette = GradientColorPalette(
            intArrayOf(-0xe2d395, Blue, Cyan, GreenYellow, Yellow, Red, DarkRed),
            floatArrayOf(0f, .1f, .2f, .4f, .6f, .8f, 1f)
        )
        val uniformGridDataSeries = UniformGridDataSeries3D<Double, Double, Double>(xSize, zSize).apply {
            startX = 0.0
            stepX = 0.09
            startZ = 0.0
            stepZ = 0.75
            for (z in 0 until zSize) {
                for (x in 0 until xSize) {
                    updateYAt(x, z, data[z][x])
                }
            }
        }

        surface3d.suspendUpdates {
            xAxis = numericAxis3D { maxAutoTicks = 7 }
            yAxis = numericAxis3D { visibleRange = DoubleRange(-4.0, 4.0) }
            zAxis = numericAxis3D()

            renderableSeries {
                surfaceMeshRenderableSeries3D {
                    dataSeries = uniformGridDataSeries
                    heightScaleFactor = 0f
                    drawMeshAs = SolidWireframe
                    stroke = 0xFF228B22.toInt()
                    strokeThickness = 1f
                    maximum = 4.0
                    meshColorPalette = palette
                    opacity = 0.7f
                }
                surfaceMeshRenderableSeries3D {
                    dataSeries = uniformGridDataSeries
                    drawMeshAs = SolidWireframe
                    stroke = 0xFF228B22.toInt()
                    strokeThickness = 1f
                    maximum = 4.0
                    drawSkirt = false
                    meshColorPalette = palette
                    opacity = 0.9f
                }
                surfaceMeshRenderableSeries3D {
                    dataSeries = uniformGridDataSeries
                    heightScaleFactor = 0f
                    drawMeshAs = SolidWireframe
                    stroke = 0xFF228B22.toInt()
                    strokeThickness = 1f
                    maximum = 4.0
                    yOffset = 400f
                    meshColorPalette = palette
                    opacity = 0.7f
                }
            }

            chartModifiers {
                pinchZoomModifier3D()
                orbitModifier3D { receiveHandledEvents = true }
                zoomExtentsModifier3D { resetPosition.assign(-1300f, 1300f, -1300f) }
            }

            camera.position.assign(-1300f, 1300f, -1300f)
            worldDimensions.assign(1100f, 400f, 400f)
        }
    }

    companion object {
        const val xSize = 11
        const val zSize = 4
    }
}