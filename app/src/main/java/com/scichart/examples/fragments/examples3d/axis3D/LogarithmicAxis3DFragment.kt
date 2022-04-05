//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2021. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// LogarithmicAxis3DFragment.kt is part of SCICHART®, High Performance Scientific Charts
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

import com.scichart.charting.visuals.axes.ScientificNotation.LogarithmicBase
import com.scichart.charting.visuals.axes.ScientificNotation.None
import com.scichart.charting3d.visuals.SciChartSurface3D
import com.scichart.charting3d.visuals.renderableSeries.metadataProviders.PointMetadataProvider3D
import com.scichart.charting3d.visuals.renderableSeries.metadataProviders.PointMetadataProvider3D.PointMetadata3D
import com.scichart.data.model.DoubleRange
import com.scichart.examples.data.DataManager
import com.scichart.examples.fragments.base.ExampleSingleChart3DBaseFragment
import com.scichart.examples.utils.scichartExtensions.*

class LogarithmicAxis3DFragment : ExampleSingleChart3DBaseFragment() {
    override fun initExample(surface3d: SciChartSurface3D) {
        val count = 100

        val dataManager = DataManager.getInstance()
        val data = dataManager.getExponentialCurve(1.8, count)
        val pointMetaDataProvider = PointMetadataProvider3D()

        surface3d.suspendUpdates {
            xAxis = logarithmicNumericAxis3D {
                growBy = DoubleRange(0.1, 0.1)
                drawMajorBands = false
                textFormatting = "#.#e+0"
                cursorTextFormatting = "0.0"
                scientificNotation = LogarithmicBase
            }
            yAxis = logarithmicNumericAxis3D {
                growBy = DoubleRange(0.1, 0.1)
                drawMajorBands = false
                textFormatting = "#.000"
                cursorTextFormatting = "0.0"
                scientificNotation = None
            }
            zAxis = numericAxis3D { growBy = DoubleRange(0.5, 0.5) }

            renderableSeries {
                pointLineRenderableSeries3D {
                    xyzDataSeries3D<Double, Double, Double> {
                        for (i in 0 until count) {
                            val x = data.xValues[i]
                            val y = data.yValues[i]
                            val z = dataManager.getGaussianRandomNumber(15.0, 1.5)
                            append(x, y, z)

                            pointMetaDataProvider.metadata.add(PointMetadata3D(dataManager.randomColor, dataManager.randomScale))
                        }
                    }
                    strokeThickness = 2f
                    spherePointMarker3D { size = 5f }
                    metadataProvider = pointMetaDataProvider
                }
            }

            chartModifiers { defaultModifiers3D() }
        }
    }
}