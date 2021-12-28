//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2021. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// UseChartModifiers3DFragment.kt is part of SCICHART®, High Performance Scientific Charts
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

package com.scichart.examples.fragments.examples3d.zoomAndPan3DChart.kt

import android.view.LayoutInflater
import com.scichart.charting3d.common.math.Vector3
import com.scichart.charting3d.visuals.renderableSeries.metadataProviders.PointMetadataProvider3D
import com.scichart.charting3d.visuals.renderableSeries.metadataProviders.PointMetadataProvider3D.PointMetadata3D
import com.scichart.data.model.DoubleRange
import com.scichart.examples.data.DataManager
import com.scichart.examples.databinding.ExampleChart3dModifiersFragmentBinding
import com.scichart.examples.fragments.base.ExampleBaseFragment
import com.scichart.examples.utils.scichartExtensions.*
import kotlin.math.pow

class UseChartModifiers3DFragment: ExampleBaseFragment<ExampleChart3dModifiersFragmentBinding>() {
    override fun inflateBinding(inflater: LayoutInflater): ExampleChart3dModifiersFragmentBinding {
        return ExampleChart3dModifiersFragmentBinding.inflate(inflater)
    }

    override fun initExample(binding: ExampleChart3dModifiersFragmentBinding) {
        val dataManager = DataManager.getInstance()
        val pointMetadataProvider3D = PointMetadataProvider3D()

        binding.surface3d.suspendUpdates {
            xAxis = numericAxis3D { growBy = DoubleRange(0.1, 0.1) }
            yAxis = numericAxis3D { growBy = DoubleRange(0.1, 0.1) }
            zAxis = numericAxis3D { growBy = DoubleRange(0.1, 0.1) }

            renderableSeries {
                scatterRenderableSeries3D {
                    xyzDataSeries3D<Double, Double, Double> {
                        val count = 25
                        val metadata = pointMetadataProvider3D.metadata

                        for (x in 0 until count) {
                            val color = dataManager.randomColor
                            for (z in 1 until count) {
                                val y = z.toDouble().pow(0.3)
                                append(x.toDouble(), y, z.toDouble())

                                metadata.add(PointMetadata3D(color, 2f))
                            }
                        }
                    }
                    spherePointMarker3D { size = 2f }
                    metadataProvider = pointMetadataProvider3D
                }
            }

            chartModifiers {
                orbitModifier3D()
                pinchZoomModifier3D()
                zoomExtentsModifier3D {
                    animationDuration = 500
                    resetPosition = Vector3(200f, 200f, 200f)
                    resetTarget = Vector3(0f, 0f, 0f)
                }
            }
        }

        val camera = binding.surface3d.camera
        binding.rotateHorizontal.setOnClickListener {
            val orbitalYaw = camera.orbitalYaw
            if (orbitalYaw < 360) {
                camera.orbitalYaw = orbitalYaw + 90
            } else {
                camera.orbitalYaw = 360 - orbitalYaw
            }
        }

        binding.rotateVertical.setOnClickListener {
            val orbitalPitch = camera.orbitalPitch
            if (orbitalPitch < 89) {
                camera.orbitalPitch = orbitalPitch + 90
            } else {
                camera.orbitalPitch = -90f
            }
        }
    }
}