//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2026. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// MultipleCharts3DFragment.kt is part of SCICHART®, High Performance Scientific Charts
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

package com.scichart.examples.fragments.examples3d.axis3D.kt

import android.view.View
import com.scichart.charting3d.visuals.SciChartSurface3D
import com.scichart.charting3d.visuals.renderableSeries.data.DrawMeshAs.SolidWithContours
import com.scichart.charting3d.visuals.renderableSeries.data.GradientColorPalette
import com.scichart.charting3d.visuals.renderableSeries.metadataProviders.PointMetadataProvider3D
import com.scichart.charting3d.visuals.renderableSeries.metadataProviders.PointMetadataProvider3D.PointMetadata3D
import com.scichart.data.model.DoubleRange
import com.scichart.drawing.utility.ColorUtil
import com.scichart.drawing.utility.ColorUtil.Aqua
import com.scichart.drawing.utility.ColorUtil.Brown
import com.scichart.drawing.utility.ColorUtil.BurlyWood
import com.scichart.drawing.utility.ColorUtil.DarkKhaki
import com.scichart.drawing.utility.ColorUtil.DarkOrange
import com.scichart.drawing.utility.ColorUtil.DarkSalmon
import com.scichart.drawing.utility.ColorUtil.ForestGreen
import com.scichart.drawing.utility.ColorUtil.Green
import com.scichart.drawing.utility.ColorUtil.GreenYellow
import com.scichart.drawing.utility.ColorUtil.SaddleBrown
import com.scichart.examples.R
import com.scichart.examples.data.DataManager
import com.scichart.examples.fragments.base.ExampleSingleChart3DBaseFragment
import com.scichart.examples.fragments.examples3d.createSurfaceMeshChart.kt.SurfaceMeshContours3DChartFragment.Companion.h
import com.scichart.examples.fragments.examples3d.createSurfaceMeshChart.kt.SurfaceMeshContours3DChartFragment.Companion.ratio
import com.scichart.examples.fragments.examples3d.createSurfaceMeshChart.kt.SurfaceMeshContours3DChartFragment.Companion.w
import com.scichart.examples.utils.scichartExtensions.*
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.sin
import kotlin.math.sqrt

class MultipleCharts3DFragment : ExampleSingleChart3DBaseFragment() {

    override fun initExample(surface3d: SciChartSurface3D) {
        val dataManager = DataManager.getInstance()
        val pointMetadataProvider3D = PointMetadataProvider3D()

        surface3d.suspendUpdates {
            xAxis = numericAxis3D { growBy = DoubleRange(0.1, 0.1) }
            yAxis = numericAxis3D { growBy = DoubleRange(0.1, 0.1) }
            zAxis = numericAxis3D { growBy = DoubleRange(0.1, 0.1) }

            renderableSeries {
                pointLineRenderableSeries3D {
                    spherePointMarker3D { fill = ColorUtil.Red; size = 5f }
                    xyzDataSeries3D<Double, Double, Double> {
                        val metadata = pointMetadataProvider3D.metadata
                        for (i in 0..99) {
                            val x = 5 * sin(i.toDouble())
                            val y = i.toDouble()
                            val z = 5 * cos(i.toDouble())
                            append(x, y, z)

                            metadata.add(PointMetadata3D(dataManager.randomColor, dataManager.randomScale))
                        }
                    }
                    stroke = Green
                    strokeThickness = 2f
                    isAntialiased = false
                    isLineStrips = true
                    metadataProvider = pointMetadataProvider3D
                }
            }

            chartModifiers { defaultModifiers3D() }
        }
        // Second surface
        val surface3d2 = binding.surface3d2
        surface3d2.setTheme(R.style.SciChart_NavyBlue)
        surface3d2.visibility = View.VISIBLE
        surface3d2.suspendUpdates {
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
}