//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2021. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// LidarPointCloudFragment.kt is part of SCICHART®, High Performance Scientific Charts
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

package com.scichart.examples.fragments.featuredApps.scientificCharts.kt

import android.view.LayoutInflater
import com.scichart.charting.visuals.renderableSeries.ColorMap
import com.scichart.charting3d.visuals.renderableSeries.data.DrawMeshAs.SolidWithContours
import com.scichart.charting3d.visuals.renderableSeries.data.GradientColorPalette
import com.scichart.charting3d.visuals.renderableSeries.metadataProviders.PointMetadataProvider3D
import com.scichart.charting3d.visuals.renderableSeries.metadataProviders.PointMetadataProvider3D.PointMetadata3D
import com.scichart.core.model.IntegerValues
import com.scichart.data.model.DoubleRange
import com.scichart.drawing.utility.ColorUtil.*
import com.scichart.examples.data.AscData
import com.scichart.examples.data.DataManager
import com.scichart.examples.databinding.ExampleSingleChart3dFragmentBinding
import com.scichart.examples.fragments.base.ShowcaseExampleBaseFragment
import com.scichart.examples.utils.scichartExtensions.*

class LidarPointCloudFragment : ShowcaseExampleBaseFragment<ExampleSingleChart3dFragmentBinding>() {

    override fun inflateBinding(inflater: LayoutInflater): ExampleSingleChart3dFragmentBinding {
        return ExampleSingleChart3dFragmentBinding.inflate(inflater)
    }

    override fun initExample(binding: ExampleSingleChart3dFragmentBinding) {
        val lidarData = DataManager.getInstance().getLidarData(requireContext())
        val pointMetadataProvider = createPointMetadataProvider(MIN, MAX, lidarData)

        binding.surface3d.suspendUpdates {
            xAxis = numericAxis3D { axisTitle = "X Distance (metres)"; textFormatting = "0m" }
            yAxis = numericAxis3D { axisTitle = "Height (metres)"; textFormatting = "0m"; visibleRange = DoubleRange(MIN, MAX) }
            zAxis = numericAxis3D { axisTitle = "Y Distance (metres)"; textFormatting = "0m" }

            renderableSeries {
                scatterRenderableSeries3D {
                    xyzDataSeries3D<Int, Double, Int> {
                        append(lidarData.xValues, lidarData.yValues, lidarData.zValues)
                    }
                    pixelPointMarker3D()
                    metadataProvider = pointMetadataProvider
                }
                surfaceMeshRenderableSeries3D {
                    uniformGridDataSeries3D<Int, Double, Int>(lidarData.numberColumns, lidarData.numberRows) {
                        stepX = lidarData.cellSize
                        stepZ = lidarData.cellSize
                        var index = 0
                        for (z in 0 until lidarData.numberRows) {
                            for (x in 0 until lidarData.numberColumns) {
                                updateYAt(x, z, lidarData.yValues[index++])
                            }
                        }
                    }
                    drawMeshAs = SolidWithContours
                    meshColorPalette = GradientColorPalette(
                        intArrayOf(0xFF1e90FF.toInt(), 0xFF32CD32.toInt(), Orange, Red, Purple),
                        floatArrayOf(0.0f, 0.2f, 0.5f, 0.7f, 1.0f)
                    )
                    contourStroke = 0xFFF0FFFF.toInt()
                    contourStrokeThickness = 2f
                    minimum = MIN
                    maximum = MAX
                    opacity = 0.5f
                }
            }

            chartModifiers {
                orbitModifier3D()
                zoomExtentsModifier3D {
                    resetPosition(800f, 1000f, 800f)
                    resetTarget(0f, 25f, 0f)
                }
                pinchZoomModifier3D()
            }

            camera {
                position(800f, 1000f, 800f)
                farClip = 10000f
            }
            worldDimensions.assign(1000f, 200f, 1000f)
        }
    }

    private fun createPointMetadataProvider(min: Double, max: Double, lidarData: AscData): PointMetadataProvider3D {
        val metadataProvider3D = PointMetadataProvider3D()
        val colorMap = ColorMap(
            intArrayOf(0xFF1E90FF.toInt(), 0xFF32CD32.toInt(), Orange, Red, Purple),
            floatArrayOf(0.0f, 0.2f, 0.5f, 0.7f, 1f)
        )
        val colors = IntegerValues()
        colorMap.lerpColorsForValues(colors, lidarData.yValues, min, max)

        val colorItems = colors.itemsArray
        for (i in 0 until colors.size()) {
            metadataProvider3D.metadata.add(PointMetadata3D(colorItems[i]))
        }

        return metadataProvider3D
    }

    companion object {
        private const val MIN = 0.0
        private const val MAX = 50.0
    }
}