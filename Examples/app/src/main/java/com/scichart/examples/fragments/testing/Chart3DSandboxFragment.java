//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2018. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// Chart3DSandboxFragment.java is part of the SCICHART® Examples. Permission is hereby granted
// to modify, create derivative works, distribute and publish any part of this source
// code whether for commercial, private or personal use.
//
// The SCICHART® examples are distributed in the hope that they will be useful, but
// without any warranty. It is provided "AS IS" without warranty of any kind, either
// expressed or implied.
//******************************************************************************

package com.scichart.examples.fragments.testing;

import com.scichart.charting3d.model.dataSeries.xyz.XyzDataSeries3D;
import com.scichart.charting3d.visuals.SciChartSurface3D;
import com.scichart.charting3d.visuals.axes.NumericAxis3D;
import com.scichart.charting3d.visuals.camera.Camera3D;
import com.scichart.charting3d.visuals.camera.ICameraController;
import com.scichart.charting3d.visuals.pointMarkers.PixelPointMarker3D;
import com.scichart.charting3d.visuals.renderableSeries.pointLine.PointLineRenderableSeries3D;
import com.scichart.core.framework.UpdateSuspender;
import com.scichart.drawing.utility.ColorUtil;
import com.scichart.examples.R;
import com.scichart.examples.fragments.base.ExampleBaseFragment;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;

public class Chart3DSandboxFragment extends ExampleBaseFragment {
    @BindView(R.id.chart3d)
    SciChartSurface3D surface3d;

    @Override
    protected int getLayoutId() {
        return R.layout.example_single_chart3d_fragment;
    }

    private final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
    private ScheduledFuture<?> schedule;

    @Override
    protected void initExample() {
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

        UpdateSuspender.using(surface3d, new Runnable() {
            @Override
            public void run() {
                surface3d.setCamera(camera);

                surface3d.setXAxis(xAxis);
                surface3d.setYAxis(yAxis);
                surface3d.setZAxis(zAxis);

                surface3d.getRenderableSeries().add(rs);
            }
        });

        if (schedule != null)
            schedule.cancel(true);

        schedule = scheduledExecutorService.scheduleWithFixedDelay(scheduledRunnable, 0, 100, TimeUnit.MILLISECONDS);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if (schedule != null)
            schedule.cancel(true);
    }

    private final Runnable scheduledRunnable = new Runnable() {
        @Override
        public void run() {
            final ICameraController camera = surface3d.getCamera();
            UpdateSuspender.using(camera, new Runnable() {
                @Override
                public void run() {
                    camera.setOrbitalYaw(camera.getOrbitalYaw() + 5);
                    camera.setOrbitalPitch(camera.getOrbitalPitch() - 5);
                }
            });
        }
    };
}
