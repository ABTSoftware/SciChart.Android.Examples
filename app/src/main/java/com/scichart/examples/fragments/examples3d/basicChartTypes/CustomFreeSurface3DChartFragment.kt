//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2021. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// CustomFreeSurface3DChartFragment.kt is part of SCICHART®, High Performance Scientific Charts
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

import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import com.scichart.charting3d.common.math.Vector3
import com.scichart.charting3d.model.dataSeries.freeSurface.CustomSurfaceDataSeries3D.UVFunc
import com.scichart.charting3d.model.dataSeries.freeSurface.CustomSurfaceDataSeries3D.ValueFunc
import com.scichart.charting3d.visuals.axes.AxisSideClipping.None
import com.scichart.charting3d.visuals.renderableSeries.data.DrawMeshAs.SolidWireframe
import com.scichart.charting3d.visuals.renderableSeries.data.GradientColorPalette
import com.scichart.charting3d.visuals.renderableSeries.freeSurface.FreeSurfacePaletteMinMaxMode
import com.scichart.charting3d.visuals.renderableSeries.freeSurface.FreeSurfaceRenderableSeries3D
import com.scichart.drawing.utility.ColorUtil
import com.scichart.drawing.utility.ColorUtil.*
import com.scichart.examples.R
import com.scichart.examples.components.SpinnerStringAdapter
import com.scichart.examples.databinding.ExampleCreateFreeSurface3dFragmentBinding
import com.scichart.examples.fragments.base.ExampleBaseFragment
import com.scichart.examples.utils.ItemSelectedListenerBase
import com.scichart.examples.utils.scichartExtensions.*
import kotlin.math.cos
import kotlin.math.sin

class CustomFreeSurface3DChartFragment : ExampleBaseFragment<ExampleCreateFreeSurface3dFragmentBinding>() {
    override fun inflateBinding(inflater: LayoutInflater): ExampleCreateFreeSurface3dFragmentBinding {
        return ExampleCreateFreeSurface3dFragmentBinding.inflate(inflater)
    }

    override fun initExample(binding: ExampleCreateFreeSurface3dFragmentBinding) {
        val radialDistanceFunc = UVFunc { u: Double, v: Double -> 5.0 + sin(5 * (u + v)) }
        val azimuthalAngleFunc = UVFunc { u: Double, _: Double -> u }
        val polarAngleFunc = UVFunc { _: Double, v: Double -> v }

        val xFunc = ValueFunc { r: Double, theta: Double, phi: Double -> r * sin(theta) * cos(phi) }
        val yFunc = ValueFunc { r: Double, theta: Double, _: Double -> r * cos(theta) }
        val zFunc = ValueFunc { r: Double, theta: Double, phi: Double -> r * sin(theta) * sin(phi) }

        binding.surface3d.suspendUpdates {
            xAxis = numericAxis3D { negativeSideClipping = None; positiveSideClipping = None }
            yAxis = numericAxis3D()
            zAxis = numericAxis3D { negativeSideClipping = None; positiveSideClipping = None }

            renderableSeries {
                freeSurfaceRenderableSeries3D {
                    customSurfaceDataSeries3D<Double, Double, Double>(
                        30, 30,
                        radialDistanceFunc, azimuthalAngleFunc, polarAngleFunc,
                        xFunc, yFunc, zFunc,
                        0.0, Math.PI * 2, 0.0, Math.PI
                    )
                    drawMeshAs = SolidWireframe
                    stroke = 0x77228B22
                    contourInterval = 0.1f
                    contourStroke = 0x77228B22
                    strokeThickness = 1f
                    lightingFactor = 0.8f
                    meshColorPalette = GradientColorPalette(
                        intArrayOf(0xFF1D2C6B.toInt(), Blue, Cyan, GreenYellow, Yellow, Red, DarkRed),
                        floatArrayOf(0f, .1f, .3f, .5f, .7f, .9f, 1f)
                    )

                    binding.paletteModeSelector.run {
                        adapter = SpinnerStringAdapter(activity, R.array.palette_mode_list)
                        setSelection(0)
                        onItemSelectedListener = object : ItemSelectedListenerBase() {
                            override fun onItemSelected(parent: AdapterView<*>?, view: View, position: Int, id: Long) {
                                switchPaletteMode(this@freeSurfaceRenderableSeries3D, position)
                            }
                        }
                    }
                }
            }

            chartModifiers { defaultModifiers3D() }
        }
    }

    private fun switchPaletteMode(rs: FreeSurfaceRenderableSeries3D, paletteMode: Int) {
        when (paletteMode) {
            0 -> {
                rs.paletteMinMaxMode = FreeSurfacePaletteMinMaxMode.Relative
                rs.paletteMinimum = Vector3(0f, 5f, 0f)
                rs.paletteMaximum = Vector3(0f, 7f, 0f)
                rs.paletteRadialFactor = 1f
                rs.paletteAxialFactor = Vector3(0f, 0f, 0f)
                rs.paletteAzimuthalFactor = 0f
                rs.palettePolarFactor = 0f
            }
            1 -> {
                rs.paletteMinMaxMode = FreeSurfacePaletteMinMaxMode.Absolute
                rs.paletteMinimum = Vector3(0f, -2f, 0f)
                rs.paletteMaximum = Vector3(0f, 2f, 0f)
                rs.paletteRadialFactor = 0f
                rs.paletteAxialFactor = Vector3(0f, 1f, 0f)
                rs.paletteAzimuthalFactor = 0f
                rs.palettePolarFactor = 0f
            }
            2 -> {
                rs.paletteRadialFactor = 0f
                rs.paletteAxialFactor = Vector3(0f, 0f, 0f)
                rs.paletteAzimuthalFactor = 1f
                rs.palettePolarFactor = 0f
            }
            3 -> {
                rs.paletteRadialFactor = 0f
                rs.paletteAxialFactor = Vector3(0f, 0f, 0f)
                rs.paletteAzimuthalFactor = 0f
                rs.palettePolarFactor = 1f
            }
            else -> throw UnsupportedOperationException()
        }
    }
}