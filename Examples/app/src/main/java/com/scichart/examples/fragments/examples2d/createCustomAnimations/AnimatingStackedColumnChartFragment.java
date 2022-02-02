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
import android.view.LayoutInflater;
import android.view.animation.DecelerateInterpolator;

import androidx.annotation.NonNull;

import com.scichart.charting.model.dataSeries.IXyDataSeries;
import com.scichart.charting.visuals.animations.AnimationsHelper;
import com.scichart.charting.visuals.animations.BaseRenderPassDataTransformation;
import com.scichart.charting.visuals.animations.TransformationHelpers;
import com.scichart.charting.visuals.axes.NumericAxis;
import com.scichart.charting.visuals.renderableSeries.StackedColumnRenderableSeries;
import com.scichart.charting.visuals.renderableSeries.VerticallyStackedColumnsCollection;
import com.scichart.charting.visuals.renderableSeries.data.StackedColumnRenderPassData;
import com.scichart.core.framework.UpdateSuspender;
import com.scichart.core.model.FloatValues;
import com.scichart.data.model.DoubleRange;
import com.scichart.data.model.ISciList;
import com.scichart.drawing.common.SolidBrushStyle;
import com.scichart.drawing.common.SolidPenStyle;
import com.scichart.examples.R;
import com.scichart.examples.databinding.ExampleAnimatingStackedColumnChartFragmentBinding;
import com.scichart.examples.fragments.base.ExampleBaseFragment;
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

public class AnimatingStackedColumnChartFragment extends ExampleBaseFragment<ExampleAnimatingStackedColumnChartFragmentBinding> {
    private final static long TIME_INTERVAL = 1000;
    private final static long ANIMATION_DURATION = 500;
    private final static int X_VALUES_COUNT = 12;
    private final static double MAX_Y_VALUE = 100.0;

    private final Random random = new Random();

    private final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
    private ScheduledFuture<?> schedule;
    private volatile boolean isRunning = true;

    private final IXyDataSeries<Double, Double> dataSeries1 = sciChartBuilder.newXyDataSeries(Double.class, Double.class).build();
    private final IXyDataSeries<Double, Double> dataSeries2 = sciChartBuilder.newXyDataSeries(Double.class, Double.class).build();

    private final StackedColumnRenderableSeries rSeries1 = sciChartBuilder.newStackedColumn().withDataSeries(dataSeries1).withStrokeStyle(sciChartBuilder.newPen().withColor(0xff226fb7).withThickness(1f).build()).build();
    private final StackedColumnRenderableSeries rSeries2 = sciChartBuilder.newStackedColumn().withDataSeries(dataSeries2).withStrokeStyle(sciChartBuilder.newPen().withColor(0xffff9a2e).withThickness(1f).build()).build();

    private final Animator animator1 = createAnimator(rSeries1);
    private final Animator animator2 = createAnimator(rSeries2);

    @NonNull
    @Override
    protected ExampleAnimatingStackedColumnChartFragmentBinding inflateBinding(@NonNull LayoutInflater inflater) {
        return ExampleAnimatingStackedColumnChartFragmentBinding.inflate(inflater);
    }

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
        }};
    }

    @Override
    protected void initExample(@NonNull ExampleAnimatingStackedColumnChartFragmentBinding binding) {
        binding.refreshData.setOnClickListener(v -> {
            if (isRunning) {
                if (schedule != null) {
                    schedule.cancel(true);
                }
                schedule = createSchedule();
            } else {
                refreshData();
            }
        });

        configureRenderableSeries(rSeries1, dataSeries1, 0xff226fb7);
        configureRenderableSeries(rSeries2, dataSeries2, 0xffff9a2e);

        fillWithInitialData();

        UpdateSuspender.using(binding.surface, () -> {
            final NumericAxis xAxis = sciChartBuilder.newNumericAxis()
                    .withGrowBy(new DoubleRange(0.1d, 0.1d))
                    .build();

            final NumericAxis yAxis = sciChartBuilder.newNumericAxis()
                    .withGrowBy(new DoubleRange(0.1d, 0.1d))
                    .withVisibleRange(new DoubleRange(0.0, MAX_Y_VALUE * 2))
                    .build();

            VerticallyStackedColumnsCollection collection = new VerticallyStackedColumnsCollection();
            collection.add(rSeries1);
            collection.add(rSeries2);

            Collections.addAll(binding.surface.getXAxes(), xAxis);
            Collections.addAll(binding.surface.getYAxes(), yAxis);
            Collections.addAll(binding.surface.getRenderableSeries(), collection);
        });

        schedule = createSchedule();
    }

    private void configureRenderableSeries(StackedColumnRenderableSeries rSeries, IXyDataSeries<Double, Double> dataSeries, int fillColor) {
        rSeries.setDataSeries(dataSeries);
        rSeries.setFillBrushStyle(new SolidBrushStyle(fillColor));
        rSeries.setStrokeStyle(new SolidPenStyle(fillColor, true, 1f, null));
    }

    private void fillWithInitialData() {
        UpdateSuspender.using(binding.surface, () -> {
            for (int i = 0; i < X_VALUES_COUNT; i++) {
                dataSeries1.append((double)i, getRandomYValue());
                dataSeries2.append((double)i, getRandomYValue());
            }
        });
    }

    private ScheduledFuture<?> createSchedule() {
        return scheduledExecutorService.scheduleWithFixedDelay(() -> {
            if (!isRunning) {
                return;
            }
            UpdateSuspender.using(binding.surface, insertRunnable);
        }, 0, TIME_INTERVAL, TimeUnit.MILLISECONDS);
    }

    private final Runnable insertRunnable = this::refreshData;

    private void refreshData() {
        requireActivity().runOnUiThread(() -> {
            animator1.cancel();
            animator2.cancel();

            UpdateSuspender.using(binding.surface, () -> {
                for (int i = 0; i < X_VALUES_COUNT; i++) {
                    dataSeries1.updateYAt(i, getRandomYValue());
                    dataSeries2.updateYAt(i, getRandomYValue());
                }
            });

            animator1.start();
            animator2.start();
        });
    }

    private double getRandomYValue() {
        return random.nextDouble() * MAX_Y_VALUE;
    }

    private Animator createAnimator(StackedColumnRenderableSeries rSeries) {
        return AnimationsHelper.createAnimator(
                rSeries,
                new UpdatedPointTransformation(),
                ANIMATION_DURATION,
                0,
                new DecelerateInterpolator(),
                new FloatEvaluator(),
                0f, 1f
        );
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        isRunning = false;
        outState.putParcelable("xValues1", dataSeries1.getXValues());
        outState.putParcelable("yValues1", dataSeries1.getYValues());
        outState.putParcelable("xValues2", dataSeries2.getXValues());
        outState.putParcelable("yValues2", dataSeries2.getYValues());
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            final ISciList<Double> xValues1 = savedInstanceState.getParcelable("xValues1");
            final ISciList<Double> yValues1 = savedInstanceState.getParcelable("yValues1");
            dataSeries1.append(xValues1, yValues1);

            final ISciList<Double> xValues2 = savedInstanceState.getParcelable("xValues2");
            final ISciList<Double> yValues2 = savedInstanceState.getParcelable("yValues2");
            dataSeries2.append(xValues2, yValues2);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if (schedule != null) {
            schedule.cancel(true);
        }
    }

    private static class UpdatedPointTransformation extends BaseRenderPassDataTransformation<StackedColumnRenderPassData> {
        private final FloatValues previousYCoordinates = new FloatValues();
        private final FloatValues previousPrevSeriesYCoordinates = new FloatValues();

        private final FloatValues originalYCoordinates = new FloatValues();
        private final FloatValues originalPrevSeriesYCoordinates = new FloatValues();

        protected UpdatedPointTransformation() {
            super(StackedColumnRenderPassData.class);
        }

        @Override
        protected void saveOriginalData() {
            if (!renderPassData.isValid()) return;

            TransformationHelpers.copyData(renderPassData.yCoords, originalYCoordinates);
            TransformationHelpers.copyData(renderPassData.prevSeriesYCoords, originalPrevSeriesYCoordinates);
        }

        @Override
        protected void applyTransformation() {
            if (!renderPassData.isValid()) return;

            int count = renderPassData.pointsCount();
            float currentTransformationValue = getCurrentTransformationValue();

            if (previousPrevSeriesYCoordinates.size() != count ||
                    previousYCoordinates.size() != count ||
                    originalYCoordinates.size() != count ||
                    originalPrevSeriesYCoordinates.size() != count) return;

            for (int i = 0; i < count; i++) {
                float startYCoord = previousYCoordinates.get(i);
                float originalYCoordinate = originalYCoordinates.get(i);
                float additionalY = startYCoord + (originalYCoordinate - startYCoord) * currentTransformationValue;

                float startPrevSeriesYCoords = previousPrevSeriesYCoordinates.get(i);
                float originalPrevSeriesYCoordinate = originalPrevSeriesYCoordinates.get(i);
                float additionalPrevSeriesY = startPrevSeriesYCoords + (originalPrevSeriesYCoordinate - startPrevSeriesYCoords) * currentTransformationValue;

                renderPassData.yCoords.set(i, additionalY);
                renderPassData.prevSeriesYCoords.set(i, additionalPrevSeriesY);
            }
        }

        @Override
        protected void discardTransformation() {
            TransformationHelpers.copyData(originalYCoordinates, renderPassData.yCoords);
            TransformationHelpers.copyData(originalPrevSeriesYCoordinates, renderPassData.prevSeriesYCoords);
        }

        @Override
        protected void onInternalRenderPassDataChanged() {
            applyTransformation();
        }

        @Override
        public void onAnimationEnd() {
            super.onAnimationEnd();

            TransformationHelpers.copyData(originalYCoordinates, previousYCoordinates);
            TransformationHelpers.copyData(originalPrevSeriesYCoordinates, previousPrevSeriesYCoordinates);
        }
    }
}
