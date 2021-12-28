//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2021. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// CreateRealTimeUniformMeshChart3DFragment.java is part of SCICHART®, High Performance Scientific Charts
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

package com.scichart.examples.fragments.examples3d.realtime3DCharts;

import com.scichart.charting3d.model.dataSeries.IndexCalculator;
import com.scichart.charting3d.model.dataSeries.grid.UniformGridDataSeries3D;
import com.scichart.charting3d.visuals.SciChartSurface3D;
import com.scichart.charting3d.visuals.axes.NumericAxis3D;
import com.scichart.charting3d.visuals.renderableSeries.data.GradientColorPalette;
import com.scichart.charting3d.visuals.renderableSeries.surfaceMesh.SurfaceMeshRenderableSeries3D;
import com.scichart.core.framework.UpdateSuspender;
import com.scichart.core.model.DoubleValues;
import com.scichart.examples.fragments.base.ExampleSingleChart3DBaseFragment;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import static com.scichart.charting.visuals.axes.AutoRange.Always;
import static com.scichart.drawing.utility.ColorUtil.Blue;
import static com.scichart.drawing.utility.ColorUtil.Cyan;
import static com.scichart.drawing.utility.ColorUtil.DarkRed;
import static com.scichart.drawing.utility.ColorUtil.GreenYellow;
import static com.scichart.drawing.utility.ColorUtil.Red;
import static com.scichart.drawing.utility.ColorUtil.Yellow;

import androidx.annotation.NonNull;

public class RealTimeUniformMeshChart3DFragment extends ExampleSingleChart3DBaseFragment {
    private final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
    private ScheduledFuture<?> schedule;

    private static final int WIDTH = 50;
    private static final int HEIGHT = 50;

    final UniformGridDataSeries3D<Double, Double, Double> dataSeries = new UniformGridDataSeries3D<>(Double.class, Double.class, Double.class, WIDTH, HEIGHT);

    @Override
    protected void initExample(@NonNull SciChartSurface3D surface3d) {
        final NumericAxis3D xAxis = sciChart3DBuilder.newNumericAxis3D().withAutoRangeMode(Always).build();
        final NumericAxis3D yAxis = sciChart3DBuilder.newNumericAxis3D().withVisibleRange(0, 1).build();
        final NumericAxis3D zAxis = sciChart3DBuilder.newNumericAxis3D().withAutoRangeMode(Always).build();

        final SurfaceMeshRenderableSeries3D rs = sciChart3DBuilder.newSurfaceMeshSeries3D()
                .withDataSeries(dataSeries)
                .withStroke(0x7FFFFFFF)
                .withStrokeThicknes(1f)
                .withDrawSkirt(false)
                .withMinimum(0)
                .withMaximum(0.5)
                .withShininess(64f)
                .withMeshColorPalette(new GradientColorPalette(
                        new int[]{0xFF1D2C6B, Blue, Cyan, GreenYellow, Yellow, Red, DarkRed},
                        new float[]{0, .1f, .3f, .5f, .7f, .9f, 1}
                ))
                .build();

        UpdateSuspender.using(surface3d, () -> {
            surface3d.setXAxis(xAxis);
            surface3d.setYAxis(yAxis);
            surface3d.setZAxis(zAxis);
            surface3d.getRenderableSeries().add(rs);
            surface3d.getChartModifiers().add(sciChart3DBuilder.newModifierGroupWithDefaultModifiers().build());
        });

        schedule = scheduledExecutorService.scheduleWithFixedDelay(scheduledRunnable, 0, 33, TimeUnit.MILLISECONDS);
    }

    private final Runnable scheduledRunnable = new Runnable() {
        private final DoubleValues buffer = new DoubleValues();
        private int frames = 0;

        @Override
        public void run() {
            UpdateSuspender.using(binding.surface3d, () -> {
                double wc = WIDTH * 0.5, hc = HEIGHT * 0.5;
                double freq = Math.sin(frames++ * 0.1) * 0.1 + 0.1;

                final IndexCalculator indexCalculator = dataSeries.getIndexCalculator();
                buffer.setSize(indexCalculator.size);

                final double[] items = buffer.getItemsArray();
                for (int i = 0; i < HEIGHT; i++) {
                    for (int j = 0; j < WIDTH; j++) {
                        final double radius = Math.sqrt((wc - i) * (wc - i) + (hc - j) * (hc - j));
                        final double d = Math.PI * radius * freq;
                        final double value = Math.sin(d) / d;

                        final int index = indexCalculator.getIndex(i, j);
                        items[index] = Double.isNaN(value) ? 1 : value;
                    }
                }

                dataSeries.copyFrom(buffer);
            });
        }
    };

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if (schedule != null) {
            schedule.cancel(true);
        }
    }
}