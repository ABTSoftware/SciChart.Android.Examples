//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2017. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// VerticallyStackedYAxesFragment.java is part of the SCICHART® Examples. Permission is hereby granted
// to modify, create derivative works, distribute and publish any part of this source
// code whether for commercial, private or personal use.
//
// The SCICHART® examples are distributed in the hope that they will be useful, but
// without any warranty. It is provided "AS IS" without warranty of any kind, either
// expressed or implied.
//******************************************************************************

package com.scichart.examples.fragments;

import android.view.animation.DecelerateInterpolator;

import com.scichart.charting.layoutManagers.ChartLayoutState;
import com.scichart.charting.layoutManagers.DefaultLayoutManager;
import com.scichart.charting.layoutManagers.VerticalAxisLayoutStrategy;
import com.scichart.charting.model.dataSeries.IXyDataSeries;
import com.scichart.charting.model.dataSeries.XyDataSeries;
import com.scichart.charting.visuals.SciChartSurface;
import com.scichart.charting.visuals.axes.AutoRange;
import com.scichart.charting.visuals.axes.AxisAlignment;
import com.scichart.charting.visuals.axes.AxisLayoutState;
import com.scichart.charting.visuals.axes.IAxis;
import com.scichart.charting.visuals.renderableSeries.FastLineRenderableSeries;
import com.scichart.core.framework.UpdateSuspender;
import com.scichart.examples.R;
import com.scichart.examples.data.DataManager;
import com.scichart.examples.data.DoubleSeries;
import com.scichart.examples.fragments.base.ExampleBaseFragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;

public class VerticallyStackedYAxesFragment extends ExampleBaseFragment {

    @BindView(R.id.chart)
    SciChartSurface surface;

    @Override
    public boolean showDefaultModifiersInToolbar() {
        return false;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.example_single_chart_fragment;
    }

    @Override
    protected void initExample() {
        final List<IXyDataSeries<Double, Double>> dataSeries = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            final XyDataSeries<Double, Double> ds = new XyDataSeries<>(Double.class, Double.class);
            dataSeries.add(ds);

            final DoubleSeries sineWave = DataManager.getInstance().getSinewave(3, i, 1000);
            ds.append(sineWave.xValues, sineWave.yValues);
        }

        final FastLineRenderableSeries ch0 = sciChartBuilder.newLineSeries().withDataSeries(dataSeries.get(0)).withStrokeStyle(0xFFFF1919, 1f, true).withYAxisId("Ch0").build();
        final FastLineRenderableSeries ch1 = sciChartBuilder.newLineSeries().withDataSeries(dataSeries.get(1)).withStrokeStyle(0xFFFC9C29, 1f, true).withYAxisId("Ch1").build();
        final FastLineRenderableSeries ch2 = sciChartBuilder.newLineSeries().withDataSeries(dataSeries.get(2)).withStrokeStyle(0xFFFF1919, 1f, true).withYAxisId("Ch2").build();
        final FastLineRenderableSeries ch3 = sciChartBuilder.newLineSeries().withDataSeries(dataSeries.get(3)).withStrokeStyle(0xFFFC9C29, 1f, true).withYAxisId("Ch3").build();
        final FastLineRenderableSeries ch4 = sciChartBuilder.newLineSeries().withDataSeries(dataSeries.get(4)).withStrokeStyle(0xFF4083B7, 1f, true).withYAxisId("Ch4").build();

        final IAxis xAxis = sciChartBuilder.newNumericAxis().withAxisAlignment(AxisAlignment.Bottom).build();
        final IAxis yAxis0 = sciChartBuilder.newNumericAxis().withAxisAlignment(AxisAlignment.Left).withAxisId("Ch0").withAxisTitle("Ch0").withVisibleRange(-2, 2).withAutoRangeMode(AutoRange.Never).withDrawMajorGridLines(false).withDrawMinorGridLines(false).withDrawMajorBands(false).build();
        final IAxis yAxis1 = sciChartBuilder.newNumericAxis().withAxisAlignment(AxisAlignment.Left).withAxisId("Ch1").withAxisTitle("Ch1").withVisibleRange(-2, 2).withAutoRangeMode(AutoRange.Never).withDrawMajorGridLines(false).withDrawMinorGridLines(false).withDrawMajorBands(false).build();
        final IAxis yAxis2 = sciChartBuilder.newNumericAxis().withAxisAlignment(AxisAlignment.Left).withAxisId("Ch2").withAxisTitle("Ch2").withVisibleRange(-2, 2).withAutoRangeMode(AutoRange.Never).withDrawMajorGridLines(false).withDrawMinorGridLines(false).withDrawMajorBands(false).build();
        final IAxis yAxis3 = sciChartBuilder.newNumericAxis().withAxisAlignment(AxisAlignment.Left).withAxisId("Ch3").withAxisTitle("Ch3").withVisibleRange(-2, 2).withAutoRangeMode(AutoRange.Never).withDrawMajorGridLines(false).withDrawMinorGridLines(false).withDrawMajorBands(false).build();
        final IAxis yAxis4 = sciChartBuilder.newNumericAxis().withAxisAlignment(AxisAlignment.Left).withAxisId("Ch4").withAxisTitle("Ch4").withVisibleRange(-2, 2).withAutoRangeMode(AutoRange.Never).withDrawMajorGridLines(false).withDrawMinorGridLines(false).withDrawMajorBands(false).build();

        final DefaultLayoutManager layoutManager = new DefaultLayoutManager.Builder().setLeftOuterAxesLayoutStrategy(new LeftAlignedOuterVerticallyStackedYAxisLayoutStrategy()).build();

        UpdateSuspender.using(surface, new Runnable() {
            @Override
            public void run() {
                surface.setLayoutManager(layoutManager);

                Collections.addAll(surface.getXAxes(), xAxis);
                Collections.addAll(surface.getYAxes(), yAxis0, yAxis1, yAxis2, yAxis3, yAxis4);
                Collections.addAll(surface.getRenderableSeries(), ch0, ch1, ch2, ch3, ch4);
                Collections.addAll(surface.getChartModifiers(), sciChartBuilder.newModifierGroupWithDefaultModifiers().build());

                sciChartBuilder.newAnimator(ch0).withSweepTransformation().withInterpolator(new DecelerateInterpolator()).withDuration(3000).withStartDelay(350).start();
                sciChartBuilder.newAnimator(ch1).withSweepTransformation().withInterpolator(new DecelerateInterpolator()).withDuration(3000).withStartDelay(350).start();
                sciChartBuilder.newAnimator(ch2).withSweepTransformation().withInterpolator(new DecelerateInterpolator()).withDuration(3000).withStartDelay(350).start();
                sciChartBuilder.newAnimator(ch3).withSweepTransformation().withInterpolator(new DecelerateInterpolator()).withDuration(3000).withStartDelay(350).start();
                sciChartBuilder.newAnimator(ch4).withSweepTransformation().withInterpolator(new DecelerateInterpolator()).withDuration(3000).withStartDelay(350).start();
            }
        });
    }

    private static class LeftAlignedOuterVerticallyStackedYAxisLayoutStrategy extends VerticalAxisLayoutStrategy {
        @Override
        public void measureAxes(int availableWidth, int availableHeight, ChartLayoutState chartLayoutState) {
            for (int i = 0, size = axes.size(); i < size; i++) {
                final IAxis axis = axes.get(i);

                axis.updateAxisMeasurements();

                final AxisLayoutState axisLayoutState = axis.getAxisLayoutState();

                chartLayoutState.leftOuterAreaSize = Math.max(getRequiredAxisSize(axisLayoutState), chartLayoutState.leftOuterAreaSize);
            }
        }

        @Override
        public void layoutAxes(int left, int top, int right, int bottom) {
            final int size = axes.size();

            final int height = bottom - top;
            final int axisSize = height / size;

            int topPlacement = top;

            for (int i = 0; i < size; i++) {
                final IAxis axis = axes.get(i);

                final AxisLayoutState axisLayoutState = axis.getAxisLayoutState();

                final int bottomPlacement = topPlacement + axisSize;

                axis.layoutArea(right - getRequiredAxisSize(axisLayoutState) + axisLayoutState.additionalLeftSize, topPlacement, right - axisLayoutState.additionalRightSize, bottomPlacement);

                topPlacement = bottomPlacement;
            }
        }
    }
}