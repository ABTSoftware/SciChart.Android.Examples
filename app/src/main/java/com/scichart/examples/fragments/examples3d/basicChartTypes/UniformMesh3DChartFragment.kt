//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2021. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// UniformMesh3DChartFragment.kt is part of SCICHART®, High Performance Scientific Charts
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

package com.scichart.examples.fragments.examples3d.basicChartTypes.kt

import com.scichart.charting3d.visuals.SciChartSurface3D
import com.scichart.charting3d.visuals.renderableSeries.data.DrawMeshAs.SolidWireframe
import com.scichart.charting3d.visuals.renderableSeries.data.GradientColorPalette
import com.scichart.data.model.DoubleRange
import com.scichart.drawing.utility.ColorUtil.*
import com.scichart.examples.fragments.base.ExampleSingleChart3DBaseFragment
import com.scichart.examples.utils.scichartExtensions.*
import kotlin.math.sin

class UniformMesh3DChartFragment : ExampleSingleChart3DBaseFragment() {
    override fun initExample(surface3d: SciChartSurface3D) {
        surface3d.suspendUpdates {
            xAxis = numericAxis3D { growBy = DoubleRange(0.1, 0.1) }
            yAxis = numericAxis3D { growBy = DoubleRange(0.1, 0.1); visibleRange = DoubleRange(0.0, 0.3) }
            zAxis = numericAxis3D { growBy = DoubleRange(0.1, 0.1) }

            renderableSeries {
                surfaceMeshRenderableSeries3D {
                    uniformGridDataSeries3D<Double, Double, Double>(xSize, zSize) {
                        for (x in 0 until xSize) {
                            for (z in 0 until zSize) {
                                val xVal = 25.0 * x / xSize
                                val zVal = 25.0 * z / zSize

                                val y = sin(xVal * .2) / ((zVal + 1) * 2)
                                updateYAt(x, z, y)
                            }
                        }
                    }
                    minimum = 0.0
                    maximum = 0.5
                    opacity = 0.9f
                    shininess = 0f
                    lightingFactor = 0f
                    highlight = 1f
                    drawMeshAs = SolidWireframe
                    stroke = 0x77364BA0
                    strokeThickness = 1f
                    contourStroke = 0x77364BA0
                    contourInterval = 2f
                    contourOffset = 0f
                    contourStrokeThickness = 2f
                    drawSkirt = false
                    meshColorPalette = GradientColorPalette(
                        intArrayOf(0xFF14233C.toInt(),
                            0xFF264B93.toInt(),
                            0xFF50C7E0.toInt(),
                            0xFF67BDAF.toInt(), 0xFFDC7969.toInt(), 0xFFF48420.toInt(), 0xFFEC0F6C.toInt()
                        ),
                        floatArrayOf(0f, .1f, .3f, .5f, .7f, .9f, 1f)
                    )
                }
            }

            chartModifiers { defaultModifiers3D()}
        }
    }

    companion object {
        const val xSize = 25
        const val zSize = 25
    }
}