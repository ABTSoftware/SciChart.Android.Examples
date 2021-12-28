//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2021. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// SurfaceMeshContours3DChartFragment.kt is part of SCICHART®, High Performance Scientific Charts
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
import com.scichart.charting3d.visuals.renderableSeries.data.DrawMeshAs.SolidWithContours
import com.scichart.charting3d.visuals.renderableSeries.data.GradientColorPalette
import com.scichart.drawing.utility.ColorUtil.*
import com.scichart.examples.fragments.base.ExampleSingleChart3DBaseFragment
import com.scichart.examples.utils.scichartExtensions.*
import kotlin.math.max
import kotlin.math.sin
import kotlin.math.sqrt

class SurfaceMeshContours3DChartFragment : ExampleSingleChart3DBaseFragment() {
    override fun initExample(surface3d: SciChartSurface3D) {
        surface3d.suspendUpdates {
            xAxis = numericAxis3D()
            yAxis = numericAxis3D()
            zAxis = numericAxis3D()

            renderableSeries {
                surfaceMeshRenderableSeries3D {
                    uniformGridDataSeries3D<Double, Double, Double>(w, h) {
                        stepX = 0.01
                        stepZ = 0.01
                        for (x in 0 until w) {
                            for (z in 0 until h) {
                                val v = (1 + sin(x * 0.04 * ratio)) * 50 + (1 + sin(z * 0.1 * ratio)) * 50
                                val cx = w / 2.0
                                val cy = h / 2.0
                                val r = sqrt((x - cx) * (x - cx) + (z - cy) * (z - cy)) * ratio
                                val exp = max(0.0, 1 - r * 0.008)
                                val zValue = v * exp

                                updateYAt(x, z, zValue)
                            }
                        }
                    }
                    drawMeshAs = SolidWithContours
                    contourStrokeThickness = 2f
                    stroke = 0x77228B22
                    maximum = 150.0
                    strokeThickness = 1f
                    drawSkirt = true
                    meshColorPalette = GradientColorPalette(
                        intArrayOf(Aqua, Green, ForestGreen, DarkKhaki, BurlyWood, DarkSalmon, GreenYellow, DarkOrange, SaddleBrown, Brown, Brown),
                        floatArrayOf(0f, .1f, .2f, .3f, .4f, .5f, .6f, .7f, .8f, .9f, 1f)
                    )
                    opacity = 0.8f
                }
            }

            chartModifiers { defaultModifiers3D() }

            camera.position.assign(-1300f, 1300f, -1300f)
            worldDimensions.assign(600f, 300f, 300f)
        }
    }

    companion object {
        const val w = 64
        const val h = 64
        const val ratio = 200.0 / 64.0
    }
}