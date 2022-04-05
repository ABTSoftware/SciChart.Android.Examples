//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2021. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// ModifyCamera3DPropertiesFragment.java is part of SCICHART®, High Performance Scientific Charts
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

package com.scichart.examples.fragments.examples3d.zoomAndPan3DChart;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.SeekBar;

import androidx.annotation.NonNull;

import com.scichart.charting3d.common.math.Vector3;
import com.scichart.charting3d.visuals.SciChartSurface3D;
import com.scichart.charting3d.visuals.axes.NumericAxis3D;
import com.scichart.charting3d.visuals.camera.Camera3D;
import com.scichart.charting3d.visuals.camera.ICameraController;
import com.scichart.charting3d.visuals.camera.ICameraUpdateListener;
import com.scichart.core.framework.UpdateSuspender;
import com.scichart.core.utility.Dispatcher;
import com.scichart.examples.databinding.ExampleChart3dCameraPropertiesFragmentBinding;
import com.scichart.examples.fragments.base.ExampleBaseFragment;
import com.scichart.examples.utils.SeekBarChangeListenerBase;

import java.text.DecimalFormat;

public class ModifyCamera3DPropertiesFragment extends ExampleBaseFragment<ExampleChart3dCameraPropertiesFragmentBinding> implements ICameraUpdateListener {

    @NonNull
    @Override
    protected ExampleChart3dCameraPropertiesFragmentBinding inflateBinding(@NonNull LayoutInflater inflater) {
        return ExampleChart3dCameraPropertiesFragmentBinding.inflate(inflater);
    }

    @Override
    protected void initExample(ExampleChart3dCameraPropertiesFragmentBinding binding) {
        final Camera3D camera = sciChart3DBuilder.newCamera3D().build();
        camera.setCameraUpdateListener(this);

        final NumericAxis3D xAxis = sciChart3DBuilder.newNumericAxis3D().build();
        final NumericAxis3D yAxis = sciChart3DBuilder.newNumericAxis3D().build();
        final NumericAxis3D zAxis = sciChart3DBuilder.newNumericAxis3D().build();

        final SciChartSurface3D surface3d = binding.surface3d;
        UpdateSuspender.using(surface3d, () -> {
            surface3d.setCamera(camera);

            surface3d.setXAxis(xAxis);
            surface3d.setYAxis(yAxis);
            surface3d.setZAxis(zAxis);

            surface3d.getChartModifiers().add(sciChart3DBuilder.newModifierGroupWithDefaultModifiers().build());
        });

        setUpSeekBarListeners(binding, surface3d);

        binding.lhsRadioButton.setOnClickListener(v -> {
            surface3d.setIsLeftHandedCoordinateSystem(true);
        });

        binding.rhsRadioButton.setOnClickListener(v -> {
            surface3d.setIsLeftHandedCoordinateSystem(false);
        });

        binding.perspectiveRadioButton.setOnClickListener(v -> {
            surface3d.getCamera().toPerspective();

            binding.perspectiveProperties.setVisibility(View.VISIBLE);
            binding.orthogonalProperties.setVisibility(View.GONE);
        });

        binding.orthogonalRadioButton.setOnClickListener(v -> {
            surface3d.getCamera().toOrthogonal();

            binding.perspectiveProperties.setVisibility(View.GONE);
            binding.orthogonalProperties.setVisibility(View.VISIBLE);
        });
    }

    private void setUpSeekBarListeners(ExampleChart3dCameraPropertiesFragmentBinding binding, SciChartSurface3D surface3d) {
        final ICameraController camera = surface3d.getCamera();
        updateUIWithValuesFrom(camera);

        binding.pitchSeekBar.setOnSeekBarChangeListener(new SeekBarChangeListenerBase() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                camera.setOrbitalPitch(progress);
            }
        });
        binding.yawSeekBar.setOnSeekBarChangeListener(new SeekBarChangeListenerBase() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                camera.setOrbitalYaw(progress);
            }
        });
        binding.radiusSeekBar.setOnSeekBarChangeListener(new SeekBarChangeListenerBase() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                camera.setRadius(progress);
            }
        });
        binding.fovSeekBar.setOnSeekBarChangeListener(new SeekBarChangeListenerBase() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                camera.setFieldOfView(progress);
            }
        });

        binding.orthoWidthSeekBar.setOnSeekBarChangeListener(new SeekBarChangeListenerBase() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                camera.setOrthoWidth(progress);
            }
        });
        binding.orthoHeightSeekBar.setOnSeekBarChangeListener(new SeekBarChangeListenerBase() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                camera.setOrthoHeight(progress);
            }
        });

        binding.orthogonalProperties.setVisibility(View.GONE);
    }

    @Override
    public void onCameraUpdated(final ICameraController camera) {
        camera.setOrbitalPitch(constrainAngle(camera.getOrbitalPitch()));
        camera.setOrbitalYaw(constrainAngle(camera.getOrbitalYaw()));

        Dispatcher.postOnUiThread(() -> updateUIWithValuesFrom(camera));
    }

    private float constrainAngle(float angle) {
        if (angle < 0) {
            angle += 360;
        } else if (angle > 360) {
            angle -= 360;
        }

        return angle;
    }

    private void updateUIWithValuesFrom(ICameraController camera) {
        final DecimalFormat format = new DecimalFormat("0.0");
        final Vector3 position = camera.getPosition();

        final StringBuilder positionTextBuilder = new StringBuilder();
        positionTextBuilder.append("Position: ")
                .append("X=").append(format.format(position.getX()))
                .append(", Y=").append(format.format(position.getY()))
                .append(", Z=").append(format.format(position.getZ()));

        binding.positionText.setText(positionTextBuilder);

        trySetSeekBarProgress(binding.pitchSeekBar, camera.getOrbitalPitch());
        trySetSeekBarProgress(binding.yawSeekBar, camera.getOrbitalYaw());
        trySetSeekBarProgress(binding.radiusSeekBar, camera.getRadius());
        trySetSeekBarProgress(binding.fovSeekBar, camera.getFieldOfView());
        trySetSeekBarProgress(binding.orthoWidthSeekBar, camera.getOrthoWidth());
        trySetSeekBarProgress(binding.orthoHeightSeekBar, camera.getOrthoHeight());
    }

    private void trySetSeekBarProgress(SeekBar seekBar, float value) {
        final int newProgress = Math.round(value);
        if (seekBar.getProgress() != newProgress) {
            seekBar.setProgress(newProgress);
        }
    }
}
