//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2021. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// AnimatingLineChartFragment.java is part of SCICHART®, High Performance Scientific Charts
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

package com.scichart.examples.fragments.examples2d.createCustomAnimations;

import android.animation.Animator;
import android.animation.FloatEvaluator;
import android.os.Bundle;
import android.view.animation.DecelerateInterpolator;

import androidx.annotation.NonNull;

import com.scichart.charting.model.dataSeries.IXyDataSeries;
import com.scichart.charting.visuals.SciChartSurface;
import com.scichart.charting.visuals.animations.AnimationsHelper;
import com.scichart.charting.visuals.animations.BaseRenderPassDataTransformation;
import com.scichart.charting.visuals.animations.TransformationHelpers;
import com.scichart.charting.visuals.axes.AutoRange;
import com.scichart.charting.visuals.axes.IAxis;
import com.scichart.charting.visuals.axes.NumericAxis;
import com.scichart.charting.visuals.renderableSeries.FastLineRenderableSeries;
import com.scichart.charting.visuals.renderableSeries.data.LineRenderPassData;
import com.scichart.core.framework.UpdateSuspender;
import com.scichart.core.model.FloatValues;
import com.scichart.data.model.DoubleRange;
import com.scichart.data.model.IRange;
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

public class AnimatingLineChartFragment extends ExampleSingleChartBaseFragment {
    private final static int FIFO_CAPACITY = 50;
    private final static long TIME_INTERVAL = 1000;
    private final static long ANIMATION_DURATION = 500;
    private final static double X_RANGE_STEP = 1.0;
    private final static double VISIBLE_RANGE_MAX = 10.0;
    private final static double MAX_Y_VALUE = 100.0;

    private final Random random = new Random();

    private final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
    private ScheduledFuture<?> schedule;
    private volatile boolean isRunning = true;

    private final IXyDataSeries<Double, Double> dataSeries = sciChartBuilder.newXyDataSeries(Double.class, Double.class)
            .withFifoCapacity(FIFO_CAPACITY).build();

    private final FastLineRenderableSeries rSeries = sciChartBuilder.newLineSeries()
            .withDataSeries(dataSeries)
            .withStrokeStyle(sciChartBuilder.newPen().withColor(0xFF47bde6).withThickness(3f).build())
            .build();

    private final DoubleRange xVisibleRange = new DoubleRange(-1.0, VISIBLE_RANGE_MAX);
    private double currentXValue = 0;
    private double yValue = 0;

    private Animator animator = AnimationsHelper.createAnimator(
            rSeries,
            new AppendedPointTransformation(),
            ANIMATION_DURATION,
            0,
            new DecelerateInterpolator(),
            new FloatEvaluator(),
            0f, 1f
    );

    @Override
    public boolean showDefaultModifiersInToolbar() {
        return false;
    }

    @NonNull
    @Override
    public List<Widget> getToolbarItems() {
        return new ArrayList<Widget>() {{
            add(new ImageViewWidget.Builder().setId(R.drawable.example_toolbar_play).setListener(v -> isRunning = true).build());
            add(new ImageViewWidget.Builder().setId(R.drawable.example_toolbar_pause).setListener(v -> isRunning = false).build());
            add(new ImageViewWidget.Builder().setId(R.drawable.example_toolbar_stop).setListener(v -> {
                isRunning = false;
                resetChart();
            }).build());
        }};
    }

    @Override
    protected void initExample(@NonNull SciChartSurface surface) {
        surface.setTheme(R.style.SciChart_NavyBlue);

        UpdateSuspender.using(surface, () -> {
            final NumericAxis xAxis = sciChartBuilder.newNumericAxis()
                    .withAutoRangeMode(AutoRange.Never)
                    .withVisibleRange(xVisibleRange)
                    .build();

            final NumericAxis yAxis = sciChartBuilder.newNumericAxis()
                    .withVisibleRange(new DoubleRange(0.0, MAX_Y_VALUE))
                    .withGrowBy(new DoubleRange(0.1d, 0.1d))
                    .build();

            Collections.addAll(surface.getXAxes(), xAxis);
            Collections.addAll(surface.getYAxes(), yAxis);
            Collections.addAll(surface.getRenderableSeries(), rSeries);
        });

        addPointAnimated();
        schedule = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            if (!isRunning) {
                return;
            }
            UpdateSuspender.using(surface, insertRunnable);
        }, 0, TIME_INTERVAL, TimeUnit.MILLISECONDS);
    }

    private final Runnable insertRunnable = this::addPointAnimated;

    private void addPointAnimated() {
        requireActivity().runOnUiThread(() -> {
            animator.cancel();

            UpdateSuspender.using(binding.surface, () -> {
                yValue = random.nextDouble() * MAX_Y_VALUE;
                dataSeries.append(currentXValue, yValue);
            });

            animator.start();

            currentXValue += X_RANGE_STEP;
            animateVisibleRangeIfNeeded();
        });
    }

    private void animateVisibleRangeIfNeeded() {
        if (currentXValue > VISIBLE_RANGE_MAX) {
            IAxis xAxis = binding.surface.getXAxes().get(0);
            IRange newRange = new DoubleRange(
                    xAxis.getVisibleRange().getMinAsDouble() + X_RANGE_STEP,
                    xAxis.getVisibleRange().getMaxAsDouble() + X_RANGE_STEP
            );
            xAxis.animateVisibleRangeTo(newRange, ANIMATION_DURATION);
        }
    }

    private void resetChart() {
        UpdateSuspender.using(binding.surface, dataSeries::clear);
        currentXValue = 0.0;
        binding.surface.getXAxes().get(0).animateVisibleRangeTo(new DoubleRange(-1.0, VISIBLE_RANGE_MAX), ANIMATION_DURATION);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        isRunning = false;

        outState.putDouble("currentXValue", currentXValue);
        outState.putDouble("yValue", yValue);
        outState.putDouble("xVisibleRangeMin", xVisibleRange.getMinAsDouble());
        outState.putDouble("xVisibleRangeMax", xVisibleRange.getMaxAsDouble());
        outState.putParcelable("xValues1", dataSeries.getXValues());
        outState.putParcelable("yValues1", dataSeries.getYValues());
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            currentXValue = savedInstanceState.getDouble("currentXValue");
            yValue = savedInstanceState.getDouble("yValue");

            final double xVisibleRangeMin = savedInstanceState.getDouble("xVisibleRangeMin");
            final double xVisibleRangeMax = savedInstanceState.getDouble("xVisibleRangeMax");
            xVisibleRange.setMinMaxDouble(xVisibleRangeMin, xVisibleRangeMax);

            final ISciList<Double> xValues1 = savedInstanceState.getParcelable("xValues1");
            final ISciList<Double> yValues1 = savedInstanceState.getParcelable("yValues1");
            dataSeries.append(xValues1, yValues1);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if (schedule != null) {
            schedule.cancel(true);
        }
    }

    private static class AppendedPointTransformation extends BaseRenderPassDataTransformation<LineRenderPassData> {
        private final FloatValues originalXCoordinates = new FloatValues();
        private final FloatValues originalYCoordinates = new FloatValues();

        protected AppendedPointTransformation() {
            super(LineRenderPassData.class);
        }

        @Override
        protected void saveOriginalData() {
            if (!renderPassData.isValid()) return;

            TransformationHelpers.copyData(renderPassData.xCoords, originalXCoordinates);
            TransformationHelpers.copyData(renderPassData.yCoords, originalYCoordinates);
        }

        @Override
        protected void applyTransformation() {
            if (!renderPassData.isValid()) return;

            int count = renderPassData.pointsCount();
            float currentTransformationValue = getCurrentTransformationValue();

            float xStart;
            if (count <= 1) {
                xStart = renderPassData.getXCoordinateCalculator().getCoordinate(0.0);
            } else {
                xStart = originalXCoordinates.get(count - 2);
            }
            float xFinish = originalXCoordinates.get(count - 1);
            float additionalX = xStart + (xFinish - xStart) * currentTransformationValue;
            renderPassData.xCoords.set(count - 1, additionalX);

            float yStart;
            if (count <= 1) {
                yStart = renderPassData.getYCoordinateCalculator().getCoordinate(0.0);
            } else {
                yStart = originalYCoordinates.get(count - 2);
            }
            float yFinish = originalYCoordinates.get(count - 1);
            float additionalY = yStart + (yFinish - yStart) * currentTransformationValue;
            renderPassData.yCoords.set(count - 1, additionalY);
        }

        @Override
        protected void discardTransformation() {
            TransformationHelpers.copyData(originalXCoordinates, renderPassData.xCoords);
            TransformationHelpers.copyData(originalYCoordinates, renderPassData.yCoords);
        }

        @Override
        protected void onInternalRenderPassDataChanged() {
            applyTransformation();
        }
    }
}
