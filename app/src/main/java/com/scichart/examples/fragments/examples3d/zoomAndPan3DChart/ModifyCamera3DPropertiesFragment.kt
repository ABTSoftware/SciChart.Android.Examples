//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2021. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// ModifyCamera3DPropertiesFragment.kt is part of SCICHART®, High Performance Scientific Charts
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
import android.view.View
import android.widget.SeekBar
import com.scichart.charting3d.visuals.SciChartSurface3D
import com.scichart.charting3d.visuals.camera.Camera3D
import com.scichart.charting3d.visuals.camera.ICameraController
import com.scichart.charting3d.visuals.camera.ICameraUpdateListener
import com.scichart.core.utility.Dispatcher
import com.scichart.examples.databinding.ExampleChart3dCameraPropertiesFragmentBinding
import com.scichart.examples.fragments.base.ExampleBaseFragment
import com.scichart.examples.utils.SeekBarChangeListenerBase
import com.scichart.examples.utils.scichartExtensions.chartModifiers
import com.scichart.examples.utils.scichartExtensions.defaultModifiers3D
import com.scichart.examples.utils.scichartExtensions.numericAxis3D
import com.scichart.examples.utils.scichartExtensions.suspendUpdates
import java.text.DecimalFormat
import kotlin.math.roundToInt

class ModifyCamera3DPropertiesFragment : ExampleBaseFragment<ExampleChart3dCameraPropertiesFragmentBinding>(), ICameraUpdateListener {
    override fun inflateBinding(inflater: LayoutInflater): ExampleChart3dCameraPropertiesFragmentBinding {
        return ExampleChart3dCameraPropertiesFragmentBinding.inflate(inflater)
    }

    override fun initExample(binding: ExampleChart3dCameraPropertiesFragmentBinding) {
        val camera = Camera3D().apply { setCameraUpdateListener(this@ModifyCamera3DPropertiesFragment) }
        binding.surface3d.suspendUpdates {
            this.camera = camera

            xAxis = numericAxis3D()
            yAxis = numericAxis3D()
            zAxis = numericAxis3D()

            chartModifiers { defaultModifiers3D() }

            setUpSeekBarListeners(binding, this)
            binding.lhsRadioButton.setOnClickListener { this.isLeftHandedCoordinateSystem = true }
            binding.rhsRadioButton.setOnClickListener { this.isLeftHandedCoordinateSystem = false }
        }

        binding.perspectiveRadioButton.setOnClickListener {
            camera.toPerspective()

            binding.perspectiveProperties.visibility = View.VISIBLE
            binding.orthogonalProperties.visibility = View.GONE
        }

        binding.orthogonalRadioButton.setOnClickListener {
            camera.toOrthogonal()

            binding.perspectiveProperties.visibility = View.GONE
            binding.orthogonalProperties.visibility = View.VISIBLE
        }
    }

    private fun setUpSeekBarListeners(binding: ExampleChart3dCameraPropertiesFragmentBinding, surface3d: SciChartSurface3D) {
        val camera = surface3d.camera
        updateUIWithValuesFrom(camera)

        binding.pitchSeekBar.setOnSeekBarChangeListener(object : SeekBarChangeListenerBase() {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                camera.orbitalPitch = progress.toFloat()
            }
        })
        binding.yawSeekBar.setOnSeekBarChangeListener(object : SeekBarChangeListenerBase() {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                camera.orbitalYaw = progress.toFloat()
            }
        })
        binding.radiusSeekBar.setOnSeekBarChangeListener(object : SeekBarChangeListenerBase() {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                camera.radius = progress.toFloat()
            }
        })
        binding.fovSeekBar.setOnSeekBarChangeListener(object : SeekBarChangeListenerBase() {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                camera.fieldOfView = progress.toFloat()
            }
        })
        binding.orthoWidthSeekBar.setOnSeekBarChangeListener(object : SeekBarChangeListenerBase() {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                camera.orthoWidth = progress.toFloat()
            }
        })
        binding.orthoHeightSeekBar.setOnSeekBarChangeListener(object : SeekBarChangeListenerBase() {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                camera.orthoHeight = progress.toFloat()
            }
        })

        binding.orthogonalProperties.visibility = View.GONE
    }

    override fun onCameraUpdated(camera: ICameraController) {
        camera.orbitalPitch = constrainAngle(camera.orbitalPitch)
        camera.orbitalYaw = constrainAngle(camera.orbitalYaw)

        Dispatcher.postOnUiThread { updateUIWithValuesFrom(camera) }
    }

    private fun constrainAngle(angle: Float): Float {
        return if (angle < 0) angle + 360 else if (angle > 360) angle - 360 else angle
    }

    private fun updateUIWithValuesFrom(camera: ICameraController) {
        val format = DecimalFormat("0.0")
        val position = camera.position

        val positionTextBuilder = StringBuilder()
        positionTextBuilder.append("Position: ")
            .append("X=").append(format.format(position.x.toDouble()))
            .append(", Y=").append(format.format(position.y.toDouble()))
            .append(", Z=").append(format.format(position.z.toDouble()))

        binding.positionText.text = positionTextBuilder

        trySetSeekBarProgress(binding.pitchSeekBar, camera.orbitalPitch)
        trySetSeekBarProgress(binding.yawSeekBar, camera.orbitalYaw)
        trySetSeekBarProgress(binding.radiusSeekBar, camera.radius)
        trySetSeekBarProgress(binding.fovSeekBar, camera.fieldOfView)
        trySetSeekBarProgress(binding.orthoWidthSeekBar, camera.orthoWidth)
        trySetSeekBarProgress(binding.orthoHeightSeekBar, camera.orthoHeight)
    }

    private fun trySetSeekBarProgress(seekBar: SeekBar, value: Float) {
        val newProgress = value.roundToInt()
        if (seekBar.progress != newProgress) {
            seekBar.progress = newProgress
        }
    }
}