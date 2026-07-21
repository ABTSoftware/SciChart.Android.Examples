//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2026. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// Chart3DTestFragment.kt is part of SCICHART®, High Performance Scientific Charts
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

package com.scichart.examples.fragments.examples3d.aTest.kt

import android.content.Intent
import android.view.LayoutInflater
import com.scichart.charting3d.visuals.camera.Camera3D
import com.scichart.data.model.DoubleRange
import com.scichart.drawing.utility.ColorUtil
import com.scichart.examples.databinding.ExampleChart3dTestFragmentBinding
import com.scichart.examples.fragments.base.ExampleBaseFragment
import com.scichart.examples.utils.scichartExtensions.*
import kotlin.math.cos
import kotlin.math.sin

class Chart3DTestFragment : ExampleBaseFragment<ExampleChart3dTestFragmentBinding>() {

    override fun inflateBinding(inflater: LayoutInflater): ExampleChart3dTestFragmentBinding {
        return ExampleChart3dTestFragmentBinding.inflate(inflater)
    }

    override fun initExample(binding: ExampleChart3dTestFragmentBinding) {
        val surface3d = binding.surface3d

        val camera3d = Camera3D().apply {
            zoomToFitOnAttach = false
            position.assign(-350f, 100f, -350f)
            target.assign(0f, 50f, 0f)
        }

        surface3d.suspendUpdates {
            camera = camera3d
            xAxis = numericAxis3D { growBy = DoubleRange(0.1, 0.1) }
            yAxis = numericAxis3D { growBy = DoubleRange(0.1, 0.1) }
            zAxis = numericAxis3D { growBy = DoubleRange(0.1, 0.1) }

            renderableSeries {
                pointLineRenderableSeries3D {
                    pixelPointMarker3D { fill = ColorUtil.Blue }
                    xyzDataSeries3D<Double, Double, Double> {
                        for (i in 0..499) {
                            val x = 10 * sin(i * 0.1)
                            val y = i * 0.2
                            val z = 10 * cos(i * 0.1)
                            append(x, y, z)
                        }
                    }
                    stroke = ColorUtil.Cyan
                    strokeThickness = 3f
                    isAntialiased = true
                    isLineStrips = true
                }
            }

            chartModifiers { defaultModifiers3D() }
        }

        binding.btnGoToSecond.setOnClickListener {
            val intent = Intent(activity, Chart3DTest2Activity::class.java)
            startActivity(intent)
        }
    }
}
