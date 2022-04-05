//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2021. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// AddRemoveSeries3DFragment.kt is part of SCICHART®, High Performance Scientific Charts
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

package com.scichart.examples.fragments.examples3d.manipulateSeries.kt

import android.view.LayoutInflater
import com.scichart.charting.visuals.axes.AutoRange.Always
import com.scichart.charting3d.visuals.renderableSeries.metadataProviders.PointMetadataProvider3D
import com.scichart.charting3d.visuals.renderableSeries.metadataProviders.PointMetadataProvider3D.PointMetadata3D
import com.scichart.data.model.DoubleRange
import com.scichart.examples.data.DataManager
import com.scichart.examples.databinding.ExampleAddRemoveSeries3dFragmentBinding
import com.scichart.examples.fragments.base.ExampleBaseFragment
import com.scichart.examples.utils.scichartExtensions.*
import java.util.*

class AddRemoveSeries3DFragment : ExampleBaseFragment<ExampleAddRemoveSeries3dFragmentBinding>() {
    override fun inflateBinding(inflater: LayoutInflater): ExampleAddRemoveSeries3dFragmentBinding {
        return ExampleAddRemoveSeries3dFragmentBinding.inflate(inflater)
    }

    override fun initExample(binding: ExampleAddRemoveSeries3dFragmentBinding) {
        binding.surface3d.suspendUpdates {
            xAxis = numericAxis3D { autoRange = Always; growBy = DoubleRange(0.1, 0.1) }
            yAxis = numericAxis3D { autoRange = Always; growBy = DoubleRange(0.1, 0.1) }
            zAxis = numericAxis3D { autoRange = Always; growBy = DoubleRange(0.1, 0.1) }

            chartModifiers {
                defaultModifiers3D()
                legendModifier3D { setShowSeriesMarkers(false) }
            }
        }

        binding.addSeries.setOnClickListener { add() }
        binding.removeSeries.setOnClickListener { remove() }
        binding.reset.setOnClickListener { binding.surface3d.renderableSeries.clear() }
    }

    private fun add() {
        binding.surface3d.suspendUpdates {
            val count = renderableSeries.size
            if (count >= MAX_SERIES_AMOUNT) return@suspendUpdates

            val dataManager = DataManager.getInstance()
            val pointMetadataProvider3D = PointMetadataProvider3D()

            renderableSeries {
                scatterRenderableSeries3D {
                    xyzDataSeries3D<Double, Double, Double>("Series #$count") {
                        val metadata = pointMetadataProvider3D.metadata
                        for (i in 0 until DATA_POINTS_COUNT) {
                            val x = dataManager.getGaussianRandomNumber(5.0, 1.5)
                            val y = dataManager.getGaussianRandomNumber(5.0, 1.5)
                            val z = dataManager.getGaussianRandomNumber(5.0, 1.5)
                            append(x, y, z)

                            metadata.add(PointMetadata3D(dataManager.randomColor, dataManager.randomScale))
                        }
                    }
                    metadataProvider = pointMetadataProvider3D

                    when (random.nextInt(6)) {
                        0 -> cubePointMarker3D()
                        1 -> ellipsePointMarker3D()
                        2 -> pyramidPointMarker3D()
                        3 -> quadPointMarker3D()
                        4 -> spherePointMarker3D()
                        5 -> trianglePointerMarker3D()
                    }
                }
            }
        }
    }

    private fun remove() {
        binding.surface3d.suspendUpdates {
            if (!renderableSeries.isEmpty()) {
                renderableSeries.removeAt(0)
            }
        }
    }

    companion object {
        private const val MAX_SERIES_AMOUNT = 15
        private const val DATA_POINTS_COUNT = 15

        private val random = Random()
    }
}