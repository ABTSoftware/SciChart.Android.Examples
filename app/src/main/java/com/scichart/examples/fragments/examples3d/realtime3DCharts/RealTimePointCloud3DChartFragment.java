//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2021. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// CreateRealTimePointCloud3DChartFragment.java is part of SCICHART®, High Performance Scientific Charts
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

import androidx.annotation.NonNull;

import com.scichart.charting3d.model.dataSeries.xyz.XyzDataSeries3D;
import com.scichart.charting3d.visuals.SciChartSurface3D;
import com.scichart.charting3d.visuals.axes.NumericAxis3D;
import com.scichart.charting3d.visuals.pointMarkers.EllipsePointMarker3D;
import com.scichart.charting3d.visuals.renderableSeries.scatter.ScatterRenderableSeries3D;
import com.scichart.core.framework.UpdateSuspender;
import com.scichart.core.model.DoubleValues;
import com.scichart.examples.data.DataManager;
import com.scichart.examples.fragments.base.ExampleSingleChart3DBaseFragment;

import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class RealTimePointCloud3DChartFragment extends ExampleSingleChart3DBaseFragment {
    private final XyzDataSeries3D<Double, Double, Double> dataSeries = new XyzDataSeries3D<>(Double.class, Double.class, Double.class);

    private final DoubleValues xData = new DoubleValues();
    private final DoubleValues yData = new DoubleValues();
    private final DoubleValues zData = new DoubleValues();

    private final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
    private ScheduledFuture<?> schedule;

    @Override
    protected void initExample(@NonNull SciChartSurface3D surface3d) {
        final NumericAxis3D xAxis = sciChart3DBuilder.newNumericAxis3D().withGrowBy(.1, .1).build();
        final NumericAxis3D yAxis = sciChart3DBuilder.newNumericAxis3D().withGrowBy(.1, .1).build();
        final NumericAxis3D zAxis = sciChart3DBuilder.newNumericAxis3D().withGrowBy(.1, .1).build();

        final EllipsePointMarker3D pointMarker3D = sciChart3DBuilder.newEllipsePointMarker3D()
                .withFill(0x7747bde6)
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

        UpdateSuspender.using(surface3d, () -> {
            surface3d.setXAxis(xAxis);
            surface3d.setYAxis(yAxis);
            surface3d.setZAxis(zAxis);
            surface3d.getRenderableSeries().add(scatterRenderableSeries3D);
            surface3d.getChartModifiers().add(sciChart3DBuilder.newModifierGroupWithDefaultModifiers().build());
        });

        schedule = scheduledExecutorService.scheduleWithFixedDelay(() -> UpdateSuspender.using(surface3d, scheduledRunnable), 0, 10, TimeUnit.MILLISECONDS);
    }

    private final Runnable scheduledRunnable = new Runnable() {
        private final Random random = new Random();

        @Override
        public void run() {
            final double[] xItems = xData.getItemsArray();
            final double[] yItems = yData.getItemsArray();
            final double[] zItems = zData.getItemsArray();

            for (int i = 0, size = dataSeries.getCount(); i < size; i++) {
                xItems[i] += random.nextDouble() - 0.5;
                yItems[i] += random.nextDouble() - 0.5;
                zItems[i] += random.nextDouble() - 0.5;
            }

            dataSeries.updateRangeXyzAt(0, xData, yData, zData);
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
