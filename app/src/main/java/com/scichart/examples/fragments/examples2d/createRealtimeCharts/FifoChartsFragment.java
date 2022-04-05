//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2021. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// FifoChartsFragment.java is part of SCICHART®, High Performance Scientific Charts
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

import androidx.annotation.NonNull;

import com.scichart.charting.model.dataSeries.IXyDataSeries;
import com.scichart.charting.visuals.SciChartSurface;
import com.scichart.charting.visuals.axes.AutoRange;
import com.scichart.charting.visuals.axes.NumericAxis;
import com.scichart.charting.visuals.renderableSeries.IRenderableSeries;
import com.scichart.core.framework.UpdateSuspender;
import com.scichart.data.model.DoubleRange;
import com.scichart.data.model.ISciList;
import com.scichart.examples.R;
import com.scichart.examples.fragments.base.ExampleSingleChartBaseFragment;
import com.scichart.examples.utils.widgetgeneration.ImageViewWidget;
import com.scichart.examples.utils.widgetgeneration.Widget;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class FifoChartsFragment extends ExampleSingleChartBaseFragment {

    private final static int FIFO_CAPACITY = 50;
    private final static long TIME_INTERVAL = 30;
    private final static double ONE_OVER_TIME_INTERVAL = 1.0 / TIME_INTERVAL;
    private final static double VISIBLE_RANGE_MAX = FIFO_CAPACITY * ONE_OVER_TIME_INTERVAL;
    private final static double GROW_BY = VISIBLE_RANGE_MAX * 0.1;

    private final Random random = new Random();

    private final IXyDataSeries<Double, Double> ds1 = sciChartBuilder.newXyDataSeries(Double.class, Double.class).withFifoCapacity(FIFO_CAPACITY).build();
    private final IXyDataSeries<Double, Double> ds2 = sciChartBuilder.newXyDataSeries(Double.class, Double.class).withFifoCapacity(FIFO_CAPACITY).build();
    private final IXyDataSeries<Double, Double> ds3 = sciChartBuilder.newXyDataSeries(Double.class, Double.class).withFifoCapacity(FIFO_CAPACITY).build();

    private final DoubleRange xVisibleRange = new DoubleRange(-GROW_BY, VISIBLE_RANGE_MAX + GROW_BY);

    private final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
    private ScheduledFuture<?> schedule;
    private volatile boolean isRunning = true;

    @NonNull
    @Override
    public List<Widget> getToolbarItems() {
        return new ArrayList<Widget>() {
            {
                add(new ImageViewWidget.Builder().setId(R.drawable.example_toolbar_play).setListener(v -> isRunning = true).build());
                add(new ImageViewWidget.Builder().setId(R.drawable.example_toolbar_pause).setListener(v -> isRunning = false).build());
                add(new ImageViewWidget.Builder().setId(R.drawable.example_toolbar_stop).setListener(v -> {
                    isRunning = false;
                    resetChart();
                }).build());
            }
        };
    }

    @Override
    protected void initExample(@NonNull SciChartSurface surface) {
        final NumericAxis xAxis = sciChartBuilder.newNumericAxis().withVisibleRange(xVisibleRange).withAutoRangeMode(AutoRange.Never).build();
        final NumericAxis yAxis = sciChartBuilder.newNumericAxis().withGrowBy(0.1d, 0.1d).withAutoRangeMode(AutoRange.Always).build();

        final IRenderableSeries rs1 = sciChartBuilder.newLineSeries().withDataSeries(ds1).withStrokeStyle(0xFF4083B7, 2f, true).build();
        final IRenderableSeries rs2 = sciChartBuilder.newLineSeries().withDataSeries(ds2).withStrokeStyle(0xFFFFA500, 2f, true).build();
        final IRenderableSeries rs3 = sciChartBuilder.newLineSeries().withDataSeries(ds3).withStrokeStyle(0xFFE13219, 2f, true).build();

        UpdateSuspender.using(surface, () -> {
            Collections.addAll(surface.getXAxes(), xAxis);
            Collections.addAll(surface.getYAxes(), yAxis);
            Collections.addAll(surface.getRenderableSeries(), rs1, rs2, rs3);
        });

        schedule = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            if (!isRunning) {
                return;
            }
            UpdateSuspender.using(surface, insertRunnable);
        }, 0, TIME_INTERVAL, TimeUnit.MILLISECONDS);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        isRunning = false;

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

    double t = 0;
    private final Runnable insertRunnable = () -> {
        double y1 = 3.0 * Math.sin(((2 * Math.PI) * 1.4) * t) + random.nextDouble() * 0.5;
        double y2 = 2.0 * Math.cos(((2 * Math.PI) * 0.8) * t) + random.nextDouble() * 0.5;
        double y3 = Math.sin(((2 * Math.PI) * 2.2) * t) + random.nextDouble() * 0.5;

        ds1.append(t, y1);
        ds2.append(t, y2);
        ds3.append(t, y3);

        t += ONE_OVER_TIME_INTERVAL;

        if (t > VISIBLE_RANGE_MAX) {
            xVisibleRange.setMinMax(xVisibleRange.getMin() + ONE_OVER_TIME_INTERVAL, xVisibleRange.getMax() + ONE_OVER_TIME_INTERVAL);
        }
    };

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if (schedule != null) {
            schedule.cancel(true);
        }
    }

    private void resetChart() {
        UpdateSuspender.using(binding.surface, () -> {
            ds1.clear();
            ds2.clear();
            ds3.clear();
        });
    }
}