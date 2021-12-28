//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2021. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// Chart3DSandboxFragment.java is part of SCICHART®, High Performance Scientific Charts
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

package com.scichart.examples.fragments.examples3d.aTest;

import androidx.annotation.NonNull;

import com.scichart.charting3d.model.dataSeries.xyz.XyzDataSeries3D;
import com.scichart.charting3d.visuals.SciChartSurface3D;
import com.scichart.charting3d.visuals.axes.NumericAxis3D;
import com.scichart.charting3d.visuals.camera.Camera3D;
import com.scichart.charting3d.visuals.camera.ICameraController;
import com.scichart.charting3d.visuals.pointMarkers.PixelPointMarker3D;
import com.scichart.charting3d.visuals.renderableSeries.pointLine.PointLineRenderableSeries3D;
import com.scichart.core.framework.UpdateSuspender;
import com.scichart.drawing.utility.ColorUtil;
import com.scichart.examples.fragments.base.ExampleSingleChart3DBaseFragment;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class Chart3DSandboxFragment extends ExampleSingleChart3DBaseFragment {
    private final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
    private ScheduledFuture<?> schedule;

    @Override
    protected void initExample(@NonNull SciChartSurface3D surface3d) {
        final Camera3D camera = sciChart3DBuilder.newCamera3D()
                .withZoomToFitOnAttach(false)
                .withPosition(-350, 100, -350)
                .withTarget(0, 50, 0)
                .build();

        final NumericAxis3D xAxis = sciChart3DBuilder.newNumericAxis3D().withGrowBy(.1, .1).build();
        final NumericAxis3D yAxis = sciChart3DBuilder.newNumericAxis3D().withGrowBy(.1, .1).build();
        final NumericAxis3D zAxis = sciChart3DBuilder.newNumericAxis3D().withGrowBy(.1, .1).build();

        final XyzDataSeries3D<Double, Double, Double> xyzDataSeries3D = new XyzDataSeries3D<>(Double.class, Double.class, Double.class);
        for (int i = 0; i < 10000; i++) {
            final double x = 5*Math.sin(i);
            final double y = i;
            final double z = 5*Math.cos(i);

            xyzDataSeries3D.append(x, y , z);
        }

        final PixelPointMarker3D pointMarker3D = sciChart3DBuilder.newPixelPointMarker3D()
                .withFill(ColorUtil.Red)
                .build();
        final PointLineRenderableSeries3D rs = sciChart3DBuilder.newPointLinesSeries3D()
                .withDataSeries(xyzDataSeries3D)
                .withStroke(ColorUtil.Green)
                .withStrokeThicknes(2f)
                .withIsAntialiased(false)
                .withIsLineStrips(true)
                .withPointMarker(pointMarker3D)
                .build();

        UpdateSuspender.using(surface3d, () -> {
            surface3d.setCamera(camera);

            surface3d.setXAxis(xAxis);
            surface3d.setYAxis(yAxis);
            surface3d.setZAxis(zAxis);

            surface3d.getRenderableSeries().add(rs);
        });

        if (schedule != null) {
            schedule.cancel(true);
        }

        schedule = scheduledExecutorService.scheduleWithFixedDelay(scheduledRunnable, 0, 100, TimeUnit.MILLISECONDS);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if (schedule != null)
            schedule.cancel(true);
    }

    private final Runnable scheduledRunnable = () -> {
        final ICameraController camera = binding.surface3d.getCamera();
        UpdateSuspender.using(camera, () -> {
            camera.setOrbitalYaw(camera.getOrbitalYaw() + 5);
            camera.setOrbitalPitch(camera.getOrbitalPitch() - 5);
        });
    };
}
