//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2021. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// PointLine3DChartFragment.kt is part of SCICHART®, High Performance Scientific Charts
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
import com.scichart.charting3d.visuals.renderableSeries.metadataProviders.PointMetadataProvider3D
import com.scichart.charting3d.visuals.renderableSeries.metadataProviders.PointMetadataProvider3D.PointMetadata3D
import com.scichart.data.model.DoubleRange
import com.scichart.drawing.utility.ColorUtil
import com.scichart.examples.data.DataManager
import com.scichart.examples.fragments.base.ExampleSingleChart3DBaseFragment
import com.scichart.examples.utils.scichartExtensions.*
import kotlin.math.cos
import kotlin.math.sin

class PointLine3DChartFragment : ExampleSingleChart3DBaseFragment() {
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
                    stroke = ColorUtil.Green
                    strokeThickness = 2f
                    isAntialiased = false
                    isLineStrips = true
                    metadataProvider = pointMetadataProvider3D
                }
            }

            chartModifiers { defaultModifiers3D()}
        }
    }
}