//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2019. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// CreateRealTimeUniformMeshChart3DFragment.java is part of the SCICHART® Examples. Permission is hereby granted
// to modify, create derivative works, distribute and publish any part of this source
// code whether for commercial, private or personal use.
//
// The SCICHART® examples are distributed in the hope that they will be useful, but
// without any warranty. It is provided "AS IS" without warranty of any kind, either
// expressed or implied.
//******************************************************************************

package com.scichart.examples.fragments.charts3d;

import com.scichart.charting.visuals.axes.AutoRange;
import com.scichart.charting3d.model.dataSeries.IndexCalculator;
import com.scichart.charting3d.model.dataSeries.grid.UniformGridDataSeries3D;
import com.scichart.charting3d.visuals.SciChartSurface3D;
import com.scichart.charting3d.visuals.axes.NumericAxis3D;
import com.scichart.charting3d.visuals.camera.Camera3D;
import com.scichart.charting3d.visuals.renderableSeries.data.GradientColorPalette;
import com.scichart.charting3d.visuals.renderableSeries.surfaceMesh.SurfaceMeshRenderableSeries3D;
import com.scichart.core.framework.UpdateSuspender;
import com.scichart.core.model.DoubleValues;
import com.scichart.examples.R;
import com.scichart.examples.fragments.base.ExampleBaseFragment;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;

import static com.scichart.drawing.utility.ColorUtil.Blue;
import static com.scichart.drawing.utility.ColorUtil.Cyan;
import static com.scichart.drawing.utility.ColorUtil.DarkRed;
import static com.scichart.drawing.utility.ColorUtil.GreenYellow;
import static com.scichart.drawing.utility.ColorUtil.Red;
import static com.scichart.drawing.utility.ColorUtil.Yellow;

public class CreateRealTimeUniformMeshChart3DFragment  extends ExampleBaseFragment {
    @BindView(R.id.chart3d)
    SciChartSurface3D surface3d;

    private final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
    private ScheduledFuture<?> schedule;

    private int frames = 0;

    @Override
    protected int getLayoutId() {
        return R.layout.example_single_chart3d_fragment;
    }

    @Override
    protected void initExample() {
        final Camera3D camera = sciChart3DBuilder.newCamera3D().build();

        final NumericAxis3D xAxis = sciChart3DBuilder.newNumericAxis3D().withAutoRangeMode(AutoRange.Always).build();
        final NumericAxis3D yAxis = sciChart3DBuilder.newNumericAxis3D().withVisibleRange(0, 1).build();
        final NumericAxis3D zAxis = sciChart3DBuilder.newNumericAxis3D().withAutoRangeMode(AutoRange.Always).build();

        final int w = 50;
        final int h = 50;

        final UniformGridDataSeries3D<Double, Double, Double> ds = new UniformGridDataSeries3D<>(Double.class, Double.class, Double.class, w, h);

        final int[] colors = new int[]{0xFF1D2C6B, Blue, Cyan, GreenYellow, Yellow, Red, DarkRed};
        final float[] stops = new float[]{0, .1f, .3f, .5f, .7f, .9f, 1};

        final SurfaceMeshRenderableSeries3D rs = sciChart3DBuilder.newSurfaceMeshSeries3D()
                .withDataSeries(ds)
                .withStroke(0x7FFFFFFF)
                .withStrokeThicknes(1f)
                .withDrawSkirt(false)
                .withMinimum(0)
                .withMaximum(0.5)
                .withShininess(64f)
                .withMeshColorPalette(new GradientColorPalette(colors, stops))
                .build();

        UpdateSuspender.using(surface3d, new Runnable() {
            @Override
            public void run() {
                surface3d.setCamera(camera);

                surface3d.setXAxis(xAxis);
                surface3d.setYAxis(yAxis);
                surface3d.setZAxis(zAxis);

                surface3d.getRenderableSeries().add(rs);

                surface3d.getChartModifiers().add(sciChart3DBuilder.newModifierGroupWithDefaultModifiers().build());
            }
        });

        schedule = scheduledExecutorService.scheduleWithFixedDelay(new Runnable() {
            private final DoubleValues buffer = new DoubleValues();
            @Override
            public void run() {
                UpdateSuspender.using(surface3d, new Runnable() {
                    @Override
                    public void run() {
                        double wc = w*0.5, hc = h*0.5;
                        double freq = Math.sin(frames++*0.1)*0.1 + 0.1;

                        final IndexCalculator indexCalculator = ds.getIndexCalculator();

                        buffer.setSize(indexCalculator.size);

                        final double[] items = buffer.getItemsArray();
                        for (int i = 0; i < h; i++) {
                            for (int j = 0; j < w; j++) {
                                final double radius = Math.sqrt((wc - i)*(wc - i) + (hc - j)*(hc - j));
                                final double d = Math.PI*radius*freq;
                                final double value = Math.sin(d)/d;

                                final int index = indexCalculator.getIndex(i, j);
                                items[index] = Double.isNaN(value) ? 1 : value;
                            }
                        }

                        ds.copyFrom(buffer);
                    }
                });
            }
        }, 0, 33, TimeUnit.MILLISECONDS);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if (schedule != null)
            schedule.cancel(true);
    }
}