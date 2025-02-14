//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2021. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// RealTimeMinMaxAnnotationFragment.java is part of SCICHART®, High Performance Scientific Charts
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

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.scichart.charting.model.dataSeries.XyDataSeries;
import com.scichart.charting.visuals.SciChartSurface;
import com.scichart.charting.visuals.annotations.HorizontalAnchorPoint;
import com.scichart.charting.visuals.annotations.TextAnnotation;
import com.scichart.charting.visuals.annotations.VerticalAnchorPoint;
import com.scichart.charting.visuals.axes.AutoRange;
import com.scichart.charting.visuals.axes.NumericAxis;
import com.scichart.charting.visuals.renderableSeries.FastLineRenderableSeries;
import com.scichart.core.framework.UpdateSuspender;
import com.scichart.data.model.DoubleRange;
import com.scichart.data.model.ISciList;
import com.scichart.drawing.common.FontStyle;
import com.scichart.drawing.common.SolidPenStyle;
import com.scichart.examples.fragments.base.ExampleSingleChartBaseFragment;

import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class RealTimeMinMaxAnnotationFragment extends ExampleSingleChartBaseFragment {

    private static final int FIFO_CAPACITY = 50;
    private static final long TIME_INTERVAL = 30;
    private static final double ONE_OVER_TIME_INTERVAL = 1.0 / TIME_INTERVAL;
    private static final double VISIBLE_RANGE_MAX = FIFO_CAPACITY * ONE_OVER_TIME_INTERVAL;
    private static final double GROW_BY = VISIBLE_RANGE_MAX * 0.1;

    private final Random random = new Random();

    private final XyDataSeries<Double, Double> ds1 = new XyDataSeries<>(Double.class, Double.class);
    private final DoubleRange xVisibleRange = new DoubleRange(GROW_BY, VISIBLE_RANGE_MAX + GROW_BY);
    private final DoubleRange yVisibleRange = new DoubleRange(0.0, 1.0);
    private final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
    private ScheduledFuture<?> schedule;
    private TextAnnotation minAnnotation;
    private TextAnnotation maxAnnotation;
    private volatile boolean isRunning = true;
    private double t = 0.0;

    @Override
    protected void initExample(@NonNull SciChartSurface surface) {
        ds1.setFifoCapacity(FIFO_CAPACITY);

        UpdateSuspender.using(surface, () ->{
            NumericAxis xAxis = sciChartBuilder.newNumericAxis().withVisibleRange(xVisibleRange).withAutoRangeMode(AutoRange.Never).build();
            NumericAxis yAxis = sciChartBuilder.newNumericAxis().withVisibleRange(yVisibleRange).withAutoRangeMode(AutoRange.Never).build();

            FastLineRenderableSeries lineSeries = sciChartBuilder.newLineSeries().withDataSeries(ds1).withStrokeStyle(new SolidPenStyle(0xFF634e96, true, 2f, null)).build();

            surface.getXAxes().add(xAxis);
            surface.getYAxes().add(yAxis);
            surface.getRenderableSeries().add(lineSeries);

            minAnnotation = sciChartBuilder.newTextAnnotation()
                    .withText("Min: 0.0")
                    .withX1(5.0)
                    .withY1(5.0)
                    .withHorizontalAnchorPoint(HorizontalAnchorPoint.Center)
                    .withVerticalAnchorPoint(VerticalAnchorPoint.Bottom)
                    .withFontStyle(new FontStyle(40f, Color.RED))
                    .build();

            maxAnnotation = sciChartBuilder.newTextAnnotation()
                    .withText("Max: 0.0")
                    .withX1(0.0)
                    .withY1(0.0)
                    .withHorizontalAnchorPoint(HorizontalAnchorPoint.Center)
                    .withVerticalAnchorPoint(VerticalAnchorPoint.Top)
                    .withFontStyle(new FontStyle(40f, Color.GREEN))
                    .build();

            surface.getAnnotations().add(minAnnotation);
            surface.getAnnotations().add(maxAnnotation);
        });

        schedule = scheduledExecutorService.scheduleWithFixedDelay(insertRunnable, 0, TIME_INTERVAL, TimeUnit.MILLISECONDS);
    }

    private final Runnable insertRunnable = new Runnable() {
        @Override
        public void run() {
            if (!isRunning) return;


            double y1 = 3.0 * Math.sin(2 * Math.PI * 1.4 * t) + random.nextDouble() * 0.5;
            double y2 = 2.0 * Math.cos(2 * Math.PI * 0.8 * t) + random.nextDouble() * 0.5;
            double y3 = Math.sin(2 * Math.PI * 2.2 * t) + random.nextDouble() * 0.5;
            double y4 = Math.sin(2 * Math.PI * 2.2 * t) + random.nextDouble() * 0.5;

            ds1.append(t, y1 + y2 + y3 + y4);

            t += ONE_OVER_TIME_INTERVAL;
            if (t > VISIBLE_RANGE_MAX) {
                xVisibleRange.setMinMax(xVisibleRange.getMin() + ONE_OVER_TIME_INTERVAL, xVisibleRange.getMax() + ONE_OVER_TIME_INTERVAL);
            }

            // Update annotations with new min and max values
            double minY = ds1.getYRange().getMinAsDouble();
            double maxY = ds1.getYRange().getMaxAsDouble();
            int minIndex = ds1.getYValues().indexOf(minY);
            int maxIndex = ds1.getYValues().indexOf(maxY);
            double minX = ds1.getXValues().get(minIndex);
            double maxX = ds1.getXValues().get(maxIndex);
            double minAnnotationY = minY - (maxY - minY) * 0.02; // Move min annotation higher by 5% of the range
            double maxAnnotationY = maxY + (maxY - minY) * 0.02;

            minAnnotation.setText(String.format("Min: %.2f", minY));
            minAnnotation.setX1(minX);
            minAnnotation.setY1(minAnnotationY);

            maxAnnotation.setText(String.format("Max: %.2f", maxY));
            maxAnnotation.setX1(maxX);
            maxAnnotation.setY1(maxAnnotationY);


            // Calculate the adjusted range
            // range adjustment = data Range / (axis length - 2 * annotation height) * annotation height.
            double dr = (maxY - minY) / (binding.surface.getYAxes().get(0).getCurrentCoordinateCalculator().getViewportDimension() - 80) * 40;
            yVisibleRange.setMinMax(minY - dr, maxY + dr);

        }
    };

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        isRunning = false;

        outState.putDouble("time", t);
        outState.putParcelable("xValues1", ds1.getXValues());
        outState.putParcelable("yValues1", ds1.getYValues());
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null) {
            t = savedInstanceState.getDouble("time");
            ds1.append((ISciList<Double>) savedInstanceState.getParcelable("xValues1"), (ISciList<Double>) savedInstanceState.getParcelable("yValues1"));
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        schedule.cancel(true);
    }

    private void resetChart() {
        UpdateSuspender.using(binding.surface, () -> {
            ds1.clear();

        });
    }
}