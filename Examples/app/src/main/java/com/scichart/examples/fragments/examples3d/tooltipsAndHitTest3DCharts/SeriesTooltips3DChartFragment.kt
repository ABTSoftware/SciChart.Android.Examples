//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2021. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// SeriesTooltips3DChartFragment.kt is part of SCICHART®, High Performance Scientific Charts
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

import android.view.LayoutInflater
import com.scichart.charting3d.model.dataSeries.xyz.XyzDataSeries3D
import com.scichart.charting3d.modifiers.CrosshairMode.Lines
import com.scichart.charting3d.visuals.renderableSeries.metadataProviders.PointMetadataProvider3D
import com.scichart.charting3d.visuals.renderableSeries.metadataProviders.PointMetadataProvider3D.PointMetadata3D
import com.scichart.data.model.DoubleRange
import com.scichart.drawing.utility.ColorUtil
import com.scichart.examples.databinding.ExampleSingleChart3dWithModifierTipFragmentBinding
import com.scichart.examples.fragments.base.ExampleBaseFragment
import com.scichart.examples.utils.scichartExtensions.*
import kotlin.math.cos
import kotlin.math.sin

class SeriesTooltips3DChartFragment : ExampleBaseFragment<ExampleSingleChart3dWithModifierTipFragmentBinding>() {
    override fun inflateBinding(inflater: LayoutInflater): ExampleSingleChart3dWithModifierTipFragmentBinding {
        return ExampleSingleChart3dWithModifierTipFragmentBinding.inflate(inflater)
    }

    override fun initExample(binding: ExampleSingleChart3dWithModifierTipFragmentBinding) {
        val pointMetadataProvider3D = PointMetadataProvider3D()


        binding.surface3d.suspendUpdates {
            xAxis = numericAxis3D { growBy = DoubleRange(0.2, 0.2); maxAutoTicks = 5 }
            yAxis = numericAxis3D { growBy = DoubleRange(0.1, 0.1) }
            zAxis = numericAxis3D { growBy = DoubleRange(0.2, 0.2) }

            renderableSeries {
                pointLineRenderableSeries3D {
                    xyzDataSeries3D<Double, Double, Double> {
                        var currentAngle = 0.0
                        val metadata = pointMetadataProvider3D.metadata
                        for (i in -SEGMENTS_COUNT until SEGMENTS_COUNT + 1) {
                            appendPoint(this, -4.0, i.toDouble(), currentAngle)
                            appendPoint(this, 4.0, i.toDouble(), currentAngle)

                            metadata.add(PointMetadata3D(blueColor))
                            metadata.add(PointMetadata3D(redColor))

                            currentAngle = (currentAngle + rotationAngle) % 360
                        }
                    }
                    spherePointMarker3D { size = 8f }
                    isLineStrips = false
                    strokeThickness = 4f
                    metadataProvider = pointMetadataProvider3D
                }
            }

            chartModifiers {
                pinchZoomModifier3D()
                orbitModifier3D { receiveHandledEvents = true; executeOnPointerCount = 2 }
                zoomExtentsModifier3D()
                tooltipModifier3D {
                    receiveHandledEvents = true
                    crosshairMode = Lines
                    crosshairPlanesFill = 0x33FF6600
                    executeOnPointerCount = 1
                }
            }

            camera {
                position(-160f, 190f, -520f)
                target(-45f, 150f, 0f)
            }
            worldDimensions.assign(600f, 300f, 180f)
        }
    }

    private fun appendPoint(ds: XyzDataSeries3D<Double, Double, Double>, x: Double, y: Double, currentAngle: Double) {
        val radAngle = Math.toRadians(currentAngle)
        val temp = x * cos(radAngle)
        val xValue = temp * COS_Y_ANGLE - y * SIN_Y_ANGLE
        val yValue = temp * SIN_Y_ANGLE + y * COS_Y_ANGLE
        val zValue = x * sin(radAngle)
        ds.append(xValue, yValue, zValue)
    }

    companion object {
        private const val SEGMENTS_COUNT = 25
        private const val rotationAngle = 360 / 45.0

        private val Y_ANGLE = Math.toRadians(-65.0)
        private val COS_Y_ANGLE = cos(Y_ANGLE)
        private val SIN_Y_ANGLE = sin(Y_ANGLE)

        private val blueColor = ColorUtil.argb(0xFF, 0x00, 0x84, 0xCF)
        private val redColor = ColorUtil.argb(0xFF, 0xEE, 0x11, 0x10)
    }
}