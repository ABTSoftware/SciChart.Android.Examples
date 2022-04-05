//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2021. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// UsingSeriesValueModifierFragment.java is part of SCICHART®, High Performance Scientific Charts
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

package com.scichart.examples.fragments.examples2d.createRealtimeCharts;

import android.os.Bundle;
import android.view.Gravity;

import androidx.annotation.NonNull;

import com.scichart.charting.model.dataSeries.IXyDataSeries;
import com.scichart.charting.visuals.SciChartSurface;
import com.scichart.charting.visuals.axes.AutoRange;
import com.scichart.charting.visuals.axes.NumericAxis;
import com.scichart.charting.visuals.renderableSeries.IRenderableSeries;
import com.scichart.core.framework.UpdateSuspender;
import com.scichart.data.model.ISciList;
import com.scichart.examples.fragments.base.ExampleSingleChartBaseFragment;

import java.util.Collections;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class RealtimeSeriesValuesOnAxisFragment extends ExampleSingleChartBaseFragment {

    private final static int FIFO_CAPACITY = 100;
    private final static long TIME_INTERVAL = 50;

    private final IXyDataSeries<Double, Double> ds1 = sciChartBuilder.newXyDataSeries(Double.class, Double.class).withSeriesName("Orange Series").withFifoCapacity(FIFO_CAPACITY).build();
    private final IXyDataSeries<Double, Double> ds2 = sciChartBuilder.newXyDataSeries(Double.class, Double.class).withSeriesName("Blue Series").withFifoCapacity(FIFO_CAPACITY).build();
    private final IXyDataSeries<Double, Double> ds3 = sciChartBuilder.newXyDataSeries(Double.class, Double.class).withSeriesName("Green Series").withFifoCapacity(FIFO_CAPACITY).build();

    private final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
    private ScheduledFuture<?> schedule;

    @Override
    protected void initExample(@NonNull SciChartSurface surface) {
        final NumericAxis xAxis = sciChartBuilder.newNumericAxis()
                .withAutoRangeMode(AutoRange.Always)
                .withAxisTitle("Time (Seconds)")
                .withTextFormatting("0.0")
                .build();
        final NumericAxis yAxis = sciChartBuilder.newNumericAxis()
                .withAutoRangeMode(AutoRange.Always)
                .withAxisTitle("Amplitude (Volts)")
                .withTextFormatting("0.0")
                .withCursorTextFormating("0.00")
                .withGrowBy(0.1d, 0.1d)
                .build();

        final IRenderableSeries rs1 = sciChartBuilder.newLineSeries().withDataSeries(ds1).withStrokeStyle(0xFFFF8C00, 2f, true).build();
        final IRenderableSeries rs2 = sciChartBuilder.newLineSeries().withDataSeries(ds2).withStrokeStyle(0xFF4682B4, 2f, true).build();
        final IRenderableSeries rs3 = sciChartBuilder.newLineSeries().withDataSeries(ds3).withStrokeStyle(0xFF556B2F, 2f, true).build();

        UpdateSuspender.using(surface, () -> {
            Collections.addAll(surface.getXAxes(), xAxis);
            Collections.addAll(surface.getYAxes(), yAxis);
            Collections.addAll(surface.getRenderableSeries(), rs1, rs2, rs3);
            Collections.addAll(surface.getChartModifiers(), sciChartBuilder.newModifierGroup()
                    .withSeriesValueModifier().build()
                    .withLegendModifier().withPosition(Gravity.TOP | Gravity.START, 16).build()
                    .build());
        });

        schedule = scheduledExecutorService.scheduleWithFixedDelay(() -> UpdateSuspender.using(surface, insertRunnable), 0, TIME_INTERVAL, TimeUnit.MILLISECONDS);
    }

    double t = 0;
    private final Runnable insertRunnable = () -> {
        double y1 = 3.0 * Math.sin(((2 * Math.PI) * 1.4) * t * 0.02);
        double y2 = 2.0 * Math.cos(((2 * Math.PI) * 0.8) * t * 0.02);
        double y3 = Math.sin(((2 * Math.PI) * 2.2) * t * 0.02);

        ds1.append(t, y1);
        ds2.append(t, y2);
        ds3.append(t, y3);

        t += TIME_INTERVAL / 1000.0;
    };

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putDouble("time", t);
        outState.putParcelable("xValues1", ds1.getXValues());
        outState.putParcelable("yValues1", ds1.getYValues());
        outState.putParcelable("xValues2", ds2.getXValues());
        outState.putParcelable("yValues2", ds2.getYValues());
        outState.putParcelable("xValues3", ds3.getXValues());
        outState.putParcelable("yValues3", ds3.getYValues());
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            t = savedInstanceState.getDouble("time");
            final ISciList<Double> xValues1 = savedInstanceState.getParcelable("xValues1");
            final ISciList<Double> yValues1 = savedInstanceState.getParcelable("yValues1");
            final ISciList<Double> xValues2 = savedInstanceState.getParcelable("xValues2");
            final ISciList<Double> yValues2 = savedInstanceState.getParcelable("yValues2");
            final ISciList<Double> xValues3 = savedInstanceState.getParcelable("xValues3");
            final ISciList<Double> yValues3 = savedInstanceState.getParcelable("yValues3");
            ds1.append(xValues1, yValues1);
            ds2.append(xValues2, yValues2);
            ds3.append(xValues3, yValues3);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if (schedule != null) {
            schedule.cancel(true);
        }
    }
}