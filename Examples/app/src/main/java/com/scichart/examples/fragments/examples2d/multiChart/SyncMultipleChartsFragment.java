//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2021. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// SyncMultipleChartsFragment.java is part of SCICHART®, High Performance Scientific Charts
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

package com.scichart.examples.fragments.examples2d.multiChart;

import android.view.LayoutInflater;
import android.view.animation.DecelerateInterpolator;

import androidx.annotation.NonNull;

import com.scichart.charting.model.dataSeries.IXyDataSeries;
import com.scichart.charting.model.dataSeries.XyDataSeries;
import com.scichart.charting.visuals.SciChartSurface;
import com.scichart.charting.visuals.axes.IAxis;
import com.scichart.charting.visuals.renderableSeries.FastLineRenderableSeries;
import com.scichart.core.framework.UpdateSuspender;
import com.scichart.data.model.DoubleRange;
import com.scichart.data.model.IRange;
import com.scichart.drawing.utility.ColorUtil;
import com.scichart.examples.databinding.ExampleSyncMultipleChartsFragmentBinding;
import com.scichart.examples.fragments.base.ExampleBaseFragment;

import java.util.Collections;

public class SyncMultipleChartsFragment extends ExampleBaseFragment<ExampleSyncMultipleChartsFragmentBinding> {
    private final static int POINTS_COUNT = 500;

    private final IRange<?> sharedXRange = new DoubleRange();
    private final IRange<?> sharedYRange = new DoubleRange();

    @Override
    public boolean showDefaultModifiersInToolbar() {
        return false;
    }

    @NonNull
    @Override
    protected ExampleSyncMultipleChartsFragmentBinding inflateBinding(@NonNull LayoutInflater inflater) {
        return ExampleSyncMultipleChartsFragmentBinding.inflate(inflater);
    }

    @Override
    protected void initExample(ExampleSyncMultipleChartsFragmentBinding binding) {
        initChart(binding.chart0);
        initChart(binding.chart1);
    }

    private void initChart(final SciChartSurface surface) {
        final IAxis xAxis = sciChartBuilder.newNumericAxis().withGrowBy(0.1d, 0.1d).withVisibleRange(sharedXRange).build();
        final IAxis yAxis = sciChartBuilder.newNumericAxis().withGrowBy(0.1d, 0.1d).withVisibleRange(sharedYRange).build();

        IXyDataSeries<Double, Double> dataSeries = new XyDataSeries<>(Double.class, Double.class);
        for (int i = 1; i < POINTS_COUNT; i++) {
            dataSeries.append((double) i, POINTS_COUNT * Math.sin(i * Math.PI * 0.1) / i);
        }

        final FastLineRenderableSeries line = sciChartBuilder.newLineSeries().withDataSeries(dataSeries).withStrokeStyle(ColorUtil.Green, 1f, true).build();

        UpdateSuspender.using(surface, () -> {
            Collections.addAll(surface.getXAxes(), xAxis);
            Collections.addAll(surface.getYAxes(), yAxis);
            Collections.addAll(surface.getRenderableSeries(), line);
            Collections.addAll(surface.getChartModifiers(), sciChartBuilder.newModifierGroup()
                    .withMotionEventsGroup("ModifiersSharedEventsGroup").withReceiveHandledEvents(true)
                    .withZoomExtentsModifier().build()
                    .withPinchZoomModifier().build()
                    .withRolloverModifier().withReceiveHandledEvents(true).build()
                    .withXAxisDragModifier().withReceiveHandledEvents(true).build()
                    .withYAxisDragModifier().withReceiveHandledEvents(true).build()
                    .build());

            surface.zoomExtents();

            sciChartBuilder.newAnimator(line).withSweepTransformation().withInterpolator(new DecelerateInterpolator()).withDuration(3000).withStartDelay(350).start();
        });
    }
}