//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2017. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// RoundedColumnsExampleFragment.java is part of the SCICHART® Examples. Permission is hereby granted
// to modify, create derivative works, distribute and publish any part of this source
// code whether for commercial, private or personal use.
//
// The SCICHART® examples are distributed in the hope that they will be useful, but
// without any warranty. It is provided "AS IS" without warranty of any kind, either
// expressed or implied.
//******************************************************************************

package com.scichart.examples.fragments.roundedColumnsSeries;

import android.view.animation.OvershootInterpolator;

import com.scichart.charting.model.dataSeries.IXyDataSeries;
import com.scichart.charting.visuals.SciChartSurface;
import com.scichart.charting.visuals.axes.IAxis;
import com.scichart.core.framework.UpdateSuspender;
import com.scichart.examples.R;
import com.scichart.examples.fragments.base.ExampleBaseFragment;
import com.scichart.examples.fragments.roundedColumnsSeries.RoundedColumnsRenderableSeries.RoundedColumnsRenderableSeriesBuilder;

import java.util.Collections;

import butterknife.BindView;

public class RoundedColumnsExampleFragment extends ExampleBaseFragment {

    @BindView(R.id.chart)
    SciChartSurface surface;

    @Override
    protected int getLayoutId() {
        return R.layout.example_single_chart_fragment;
    }

    @Override
    protected void initExample() {
        final IAxis xAxis = sciChartBuilder.newNumericAxis().withGrowBy(0.1d, 0.1d).build();
        final IAxis yAxis = sciChartBuilder.newNumericAxis().withGrowBy(0.2d, 0.2d).build();

        final IXyDataSeries<Integer, Integer> dataSeries = sciChartBuilder.newXyDataSeries(Integer.class, Integer.class).build();
        final int[] yValues = {50, 35, 61, 58, 50, 50, 40, 53, 55, 23, 45, 12, 59, 60};
        for (int i = 0; i < yValues.length; i++) {
            dataSeries.append(i, yValues[i]);
        }

        final RoundedColumnsRenderableSeries rSeries = new RoundedColumnsRenderableSeriesBuilder(getActivity())
                .withDataSeries(dataSeries)
                .withFillColor(0xFF3CF3A6)
                .build();

        UpdateSuspender.using(surface, () -> {
            Collections.addAll(surface.getXAxes(), xAxis);
            Collections.addAll(surface.getYAxes(), yAxis);
            Collections.addAll(surface.getRenderableSeries(), rSeries);
            Collections.addAll(surface.getChartModifiers(), sciChartBuilder.newModifierGroupWithDefaultModifiers().build());

            sciChartBuilder.newAnimator(rSeries).withScaleTransformation().withInterpolator(new OvershootInterpolator()).withDuration(1500).withStartDelay(350).start();
        });
    }
}