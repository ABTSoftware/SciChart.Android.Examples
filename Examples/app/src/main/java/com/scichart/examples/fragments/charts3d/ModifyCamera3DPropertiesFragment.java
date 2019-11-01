//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2019. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// ModifyCamera3DPropertiesFragment.java is part of the SCICHART® Examples. Permission is hereby granted
// to modify, create derivative works, distribute and publish any part of this source
// code whether for commercial, private or personal use.
//
// The SCICHART® examples are distributed in the hope that they will be useful, but
// without any warranty. It is provided "AS IS" without warranty of any kind, either
// expressed or implied.
//******************************************************************************

package com.scichart.examples.fragments.charts3d;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.scichart.charting3d.common.math.Vector3;
import com.scichart.charting3d.visuals.SciChartSurface3D;
import com.scichart.charting3d.visuals.axes.NumericAxis3D;
import com.scichart.charting3d.visuals.camera.Camera3D;
import com.scichart.charting3d.visuals.camera.ICameraController;
import com.scichart.charting3d.visuals.camera.ICameraUpdateListener;
import com.scichart.core.framework.UpdateSuspender;
import com.scichart.core.utility.Dispatcher;
import com.scichart.examples.R;
import com.scichart.examples.fragments.base.ExampleBaseFragment;
import com.scichart.examples.utils.SeekBarChangeListenerBase;

import java.text.DecimalFormat;

import butterknife.BindView;
import butterknife.OnClick;

public class ModifyCamera3DPropertiesFragment extends ExampleBaseFragment implements ICameraUpdateListener {
    @BindView(R.id.chart3d)
    SciChartSurface3D surface3d;

    @BindView(R.id.positionText)
    TextView positionText;

    @BindView(R.id.pitchSeekBar)
    SeekBar pitchSeekBar;

    @BindView(R.id.yawSeekBar)
    SeekBar yawSeekBar;

    @BindView(R.id.radiusSeekBar)
    SeekBar radiusSeekBar;

    @BindView(R.id.fovSeekBar)
    SeekBar fovSeekBar;

    @BindView(R.id.orthoWidthSeekBar)
    SeekBar orthoWidthSeekBar;

    @BindView(R.id.orthoHeightSeekBar)
    SeekBar orthoHeightSeekBar;

    @BindView(R.id.perspectiveProperties)
    LinearLayout perspectiveProperties;

    @BindView(R.id.orthogonalProperties)
    LinearLayout orthogonalProperties;

    @Override
    protected int getLayoutId() {
        return R.layout.example_chart3d_camera_properties_fragment;
    }

    @Override
    protected void initExample() {
        final Camera3D camera = sciChart3DBuilder.newCamera3D().build();

        camera.setCameraUpdateListener(this);

        final NumericAxis3D xAxis = sciChart3DBuilder.newNumericAxis3D().build();
        final NumericAxis3D yAxis = sciChart3DBuilder.newNumericAxis3D().build();
        final NumericAxis3D zAxis = sciChart3DBuilder.newNumericAxis3D().build();

        UpdateSuspender.using(surface3d, new Runnable() {
            @Override
            public void run() {
                surface3d.setCamera(camera);

                surface3d.setXAxis(xAxis);
                surface3d.setYAxis(yAxis);
                surface3d.setZAxis(zAxis);

                surface3d.getChartModifiers().add(sciChart3DBuilder.newModifierGroupWithDefaultModifiers().build());
            }
        });

        setUpSeekBarListeners();
    }

    private void setUpSeekBarListeners() {
        final ICameraController camera = surface3d.getCamera();

        updateUIWithValuesFrom(camera);

        pitchSeekBar.setOnSeekBarChangeListener(new SeekBarChangeListenerBase() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                surface3d.getCamera().setOrbitalPitch(progress);
            }
        });

        yawSeekBar.setOnSeekBarChangeListener(new SeekBarChangeListenerBase() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                surface3d.getCamera().setOrbitalYaw(progress);
            }
        });

        radiusSeekBar.setOnSeekBarChangeListener(new SeekBarChangeListenerBase() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                surface3d.getCamera().setRadius(progress);
            }
        });

        fovSeekBar.setOnSeekBarChangeListener(new SeekBarChangeListenerBase() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                surface3d.getCamera().setFieldOfView(progress);
            }
        });

        orthoWidthSeekBar.setOnSeekBarChangeListener(new SeekBarChangeListenerBase() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                surface3d.getCamera().setOrthoWidth(progress);
            }
        });

        orthoHeightSeekBar.setOnSeekBarChangeListener(new SeekBarChangeListenerBase() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                surface3d.getCamera().setOrthoHeight(progress);
            }
        });

        orthogonalProperties.setVisibility(View.GONE);
    }

    @Override
    public void onCameraUpdated(final ICameraController camera) {
        camera.setOrbitalPitch(constrainAngle(camera.getOrbitalPitch()));
        camera.setOrbitalYaw(constrainAngle(camera.getOrbitalYaw()));

        Dispatcher.postOnUiThread(new Runnable() {
            @Override
            public void run() {
                updateUIWithValuesFrom(camera);
            }
        });
    }

    private float constrainAngle(float angle) {
        if(angle < 0) {
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

        positionText.setText(positionTextBuilder);

        trySetSeekBarProgress(pitchSeekBar, camera.getOrbitalPitch());
        trySetSeekBarProgress(yawSeekBar, camera.getOrbitalYaw());
        trySetSeekBarProgress(radiusSeekBar, camera.getRadius());
        trySetSeekBarProgress(fovSeekBar, camera.getFieldOfView());
        trySetSeekBarProgress(orthoWidthSeekBar, camera.getOrthoWidth());
        trySetSeekBarProgress(orthoHeightSeekBar, camera.getOrthoHeight());
    }

    private void trySetSeekBarProgress(SeekBar seekBar, float value) {
        final int newProgress = Math.round(value);
        if(seekBar.getProgress() != newProgress) {
            seekBar.setProgress(newProgress);
        }
    }

    @OnClick(R.id.lhsRadioButton)
    public void onLeftHandCoorinateSystemSelected() {
        surface3d.setIsLeftHandedCoordinateSystem(true);
    }

    @OnClick(R.id.rhsRadioButton)
    public void onRightHandCoorinateSystemSelected() {
        surface3d.setIsLeftHandedCoordinateSystem(false);
    }

    @OnClick(R.id.perspectiveRadioButton)
    public void onPerspectiveCameraEnabled() {
        surface3d.getCamera().toPerspective();

        perspectiveProperties.setVisibility(View.VISIBLE);
        orthogonalProperties.setVisibility(View.GONE);
    }

    @OnClick(R.id.orthogonalRadioButton)
    public void onOrthogonalCameraEnabled() {
        surface3d.getCamera().toOrthogonal();

        perspectiveProperties.setVisibility(View.GONE);
        orthogonalProperties.setVisibility(View.VISIBLE);
    }
}
