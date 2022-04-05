//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2021. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// UniformImpulseSeries3DFragment.kt is part of SCICHART®, High Performance Scientific Charts
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
import com.scichart.data.model.DoubleRange
import com.scichart.drawing.utility.ColorUtil
import com.scichart.examples.fragments.base.ExampleSingleChart3DBaseFragment
import com.scichart.examples.utils.scichartExtensions.*
import kotlin.math.sin

class UniformImpulseSeries3DFragment : ExampleSingleChart3DBaseFragment() {
    override fun initExample(surface3d: SciChartSurface3D) {
        surface3d.suspendUpdates {
            xAxis = numericAxis3D { growBy = DoubleRange(0.1, 0.1) }
            yAxis = numericAxis3D { growBy = DoubleRange(0.1, 0.1); visibleRange = DoubleRange(0.0, 0.5) }
            zAxis = numericAxis3D { growBy = DoubleRange(0.1, 0.1) }

            renderableSeries {
                impulseRenderableSeries3D {
                    uniformGridDataSeries3D<Double, Double, Double>(COUNT, COUNT) {
                        for (x in 0 until COUNT) {
                            for (z in 0 until COUNT) {
                                val y = sin(x * .25) / ((z + 1) * 2)

                                updateYAt(x, z, y)
                            }
                        }
                    }
                    stroke = ColorUtil.DodgerBlue
                    strokeThickness = 1f
                    spherePointMarker3D { size = 5f; fill = ColorUtil.DodgerBlue }
                }
            }

            chartModifiers { defaultModifiers3D() }
        }
    }

    companion object {
        private const val COUNT = 15
    }
}