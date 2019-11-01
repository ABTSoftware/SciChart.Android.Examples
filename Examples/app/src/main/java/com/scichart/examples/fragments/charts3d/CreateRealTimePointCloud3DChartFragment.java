//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2019. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// CreateRealTimePointCloud3DChartFragment.java is part of the SCICHART® Examples. Permission is hereby granted
// to modify, create derivative works, distribute and publish any part of this source
// code whether for commercial, private or personal use.
//
// The SCICHART® examples are distributed in the hope that they will be useful, but
// without any warranty. It is provided "AS IS" without warranty of any kind, either
// expressed or implied.
//******************************************************************************

package com.scichart.examples.fragments.charts3d;

import com.scichart.charting3d.model.dataSeries.xyz.XyzDataSeries3D;
import com.scichart.charting3d.visuals.SciChartSurface3D;
import com.scichart.charting3d.visuals.axes.NumericAxis3D;
import com.scichart.charting3d.visuals.camera.Camera3D;
import com.scichart.charting3d.visuals.pointMarkers.EllipsePointMarker3D;
import com.scichart.charting3d.visuals.renderableSeries.scatter.ScatterRenderableSeries3D;
import com.scichart.core.framework.UpdateSuspender;
import com.scichart.core.model.DoubleValues;
import com.scichart.examples.R;
import com.scichart.examples.data.DataManager;
import com.scichart.examples.fragments.base.ExampleBaseFragment;

import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;

public class CreateRealTimePointCloud3DChartFragment extends ExampleBaseFragment {
    @BindView(R.id.chart3d)
    SciChartSurface3D surface3d;

    private final XyzDataSeries3D<Double, Double, Double> dataSeries = new XyzDataSeries3D<>(Double.class, Double.class, Double.class);

    private final DoubleValues xData = new DoubleValues();
    private final DoubleValues yData = new DoubleValues();
    private final DoubleValues zData = new DoubleValues();

    private final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
    private ScheduledFuture<?> schedule;

    @Override
    protected int getLayoutId() {
        return R.layout.example_single_chart3d_fragment;
    }

    @Override
    protected void initExample() {
        final Camera3D camera = sciChart3DBuilder.newCamera3D().build();

        final NumericAxis3D xAxis = sciChart3DBuilder.newNumericAxis3D().withGrowBy(.1, .1).build();
        final NumericAxis3D yAxis = sciChart3DBuilder.newNumericAxis3D().withGrowBy(.1, .1).build();
        final NumericAxis3D zAxis = sciChart3DBuilder.newNumericAxis3D().withGrowBy(.1, .1).build();

        final EllipsePointMarker3D pointMarker3D = sciChart3DBuilder.newEllipsePointMarker3D()
                .withFill(0x77ADFF2F)
                .withSize(3f)
                .build();

        final ScatterRenderableSeries3D scatterRenderableSeries3D = sciChart3DBuilder.newScatterSeries3D()
                .withDataSeries(dataSeries)
                .withPointMarker(pointMarker3D)
                .build();

        final DataManager dataManager = DataManager.getInstance();
        for (int i = 0; i < 1000; i++) {
            xData.add(dataManager.getGaussianRandomNumber(5, 1.5));
            yData.add(dataManager.getGaussianRandomNumber(5, 1.5));
            zData.add(dataManager.getGaussianRandomNumber(5, 1.5));
        }

        dataSeries.append(xData, yData, zData);

        UpdateSuspender.using(surface3d, new Runnable() {
            @Override
            public void run() {
                surface3d.setCamera(camera);

                surface3d.setXAxis(xAxis);
                surface3d.setYAxis(yAxis);
                surface3d.setZAxis(zAxis);

                surface3d.getRenderableSeries().add(scatterRenderableSeries3D);

                surface3d.getChartModifiers().add(sciChart3DBuilder.newModifierGroupWithDefaultModifiers().build());
            }
        });

        schedule = scheduledExecutorService.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                UpdateSuspender.using(surface3d, scheduledRunnable);
            }
        }, 0, 10, TimeUnit.MILLISECONDS);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if (schedule != null)
            schedule.cancel(true);
    }

    private final Runnable scheduledRunnable = new Runnable() {
        private final Random random = new Random();

        @Override
        public void run() {
            final double[] xItems = xData.getItemsArray();
            final double[] yItems = yData.getItemsArray();
            final double[] zItems = zData.getItemsArray();

            final byte[] bytes = new byte[3];
            for (int i = 0, size = dataSeries.getCount(); i < size; i++) {
                xItems[i] += random.nextDouble() - 0.5;
                yItems[i] += random.nextDouble() - 0.5;
                zItems[i] += random.nextDouble() - 0.5;
            }

            dataSeries.updateRangeXyzAt(0, xData, yData, zData);
        }
    };
}
