//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2021. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// SelectScatterPoint3DChartFragment.kt is part of SCICHART®, High Performance Scientific Charts
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

package com.scichart.examples.fragments.examples3d.tooltipsAndHitTest3DCharts.kt

import com.scichart.charting3d.visuals.SciChartSurface3D
import com.scichart.charting3d.visuals.renderableSeries.metadataProviders.DefaultSelectableMetadataProvider3D
import com.scichart.data.model.DoubleRange
import com.scichart.drawing.utility.ColorUtil
import com.scichart.examples.data.DataManager
import com.scichart.examples.fragments.base.ExampleSingleChart3DBaseFragment
import com.scichart.examples.utils.scichartExtensions.*

class SelectScatterPoint3DChartFragment : ExampleSingleChart3DBaseFragment() {
    override fun initExample(surface3d: SciChartSurface3D) {
        val dataManager = DataManager.getInstance()

        surface3d.suspendUpdates {
            xAxis = numericAxis3D { growBy = DoubleRange(0.1, 0.1) }
            yAxis = numericAxis3D { growBy = DoubleRange(0.1, 0.1) }
            zAxis = numericAxis3D { growBy = DoubleRange(0.1, 0.1) }

            renderableSeries {
                scatterRenderableSeries3D {
                    xyzDataSeries3D<Double, Double, Double> {
                        for (i in 0 until 250) {
                            val x = dataManager.getGaussianRandomNumber(5.0, 1.5)
                            val y = dataManager.getGaussianRandomNumber(5.0, 1.5)
                            val z = dataManager.getGaussianRandomNumber(5.0, 1.5)

                            append(x, y, z)
                        }
                        ellipsePointMarker3D { fill = ColorUtil.LimeGreen; size = 5f }
                        //set ISelectableMetadataProvider3D because we're going to use vertex selection for this series
                        metadataProvider = DefaultSelectableMetadataProvider3D()
                    }
                }
            }

            chartModifiers {
                pinchZoomModifier3D()
                orbitModifier3D { receiveHandledEvents = true }
                zoomExtentsModifier3D()
                vertexSelectionModifier3D { receiveHandledEvents = true }
            }
        }
    }
}