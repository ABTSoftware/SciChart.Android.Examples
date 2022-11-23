//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2021. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// LogarithmicAxisFragment.java is part of SCICHART®, High Performance Scientific Charts
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

package com.scichart.examples.fragments.examples2d.modifyAxisBehavior;

import androidx.annotation.NonNull;

import com.scichart.charting.model.dataSeries.IXyDataSeries;
import com.scichart.charting.visuals.SciChartSurface;
import com.scichart.charting.visuals.axes.AutoRange;
import com.scichart.charting.visuals.axes.NumericAxis;
import com.scichart.charting.visuals.pointmarkers.EllipsePointMarker;
import com.scichart.charting.visuals.pointmarkers.IPointMarker;
import com.scichart.charting.visuals.renderableSeries.FastLineRenderableSeries;
import com.scichart.core.framework.UpdateSuspender;
import com.scichart.drawing.utility.ColorUtil;
import com.scichart.examples.fragments.base.ExampleSingleChartBaseFragment;

import java.util.Collections;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class FixedWidthAxisFragment extends ExampleSingleChartBaseFragment {

    private final static int FIFO_CAPACITY = 50;
    private final static long TIME_INTERVAL = 30;

    private final IXyDataSeries<Double, Double> ds1 = sciChartBuilder.newXyDataSeries(Double.class, Double.class).withFifoCapacity(FIFO_CAPACITY).build();
    private final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
    private ScheduledFuture<?> schedule;

    private Double index = 0.0;
    private Double value = 0.0;
    private Boolean isIncreasing = true;

    @Override
    protected void initExample(@NonNull SciChartSurface surface) {

        final int line1Color = ColorUtil.argb(0xFF, 0x47, 0xBD, 0xE6);

        final IPointMarker pointMarker1 = sciChartBuilder.newPointMarker(new EllipsePointMarker()).withSize(5).withFill(line1Color).build();

        final FastLineRenderableSeries line1 = sciChartBuilder.newLineSeries()
                .withStrokeStyle(line1Color)
                .withPointMarker(pointMarker1)
                .withDataSeries(ds1)
                .build();

        NumericAxis xAxis = sciChartBuilder.newNumericAxis().withAutoRangeMode(AutoRange.Always).build();
        NumericAxis yAxis = sciChartBuilder.newNumericAxis().withAutoRangeMode(AutoRange.Always).build();
        yAxis.setFixedSize(200);

        UpdateSuspender.using(surface, () -> {
            Collections.addAll(surface.getXAxes(), xAxis);
            Collections.addAll(surface.getYAxes(), yAxis);
            Collections.addAll(surface.getRenderableSeries(), line1);
            Collections.addAll(surface.getChartModifiers(), sciChartBuilder.newModifierGroupWithDefaultModifiers().build());

        });

        schedule = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            UpdateSuspender.using(surface, insertRunnable);
        }, 0, TIME_INTERVAL, TimeUnit.MILLISECONDS);
    }

    private final Runnable insertRunnable = () -> {
        ds1.append(index, value * value);
        index++;
        if (value == 200.0) {
            isIncreasing = false;
        }
        if (value == 0.0) {
            isIncreasing = true;
        }
        if (isIncreasing) {
            value += 1.0;
        } else {
            value -= 1.0;
        }
    };

}