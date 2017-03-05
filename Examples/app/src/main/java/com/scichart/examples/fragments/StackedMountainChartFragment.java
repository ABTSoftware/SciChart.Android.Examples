//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2017. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// StackedMountainChartFragment.java is part of the SCICHART® Examples. Permission is hereby granted
// to modify, create derivative works, distribute and publish any part of this source
// code whether for commercial, private or personal use.
//
// The SCICHART® examples are distributed in the hope that they will be useful, but
// without any warranty. It is provided "AS IS" without warranty of any kind, either
// expressed or implied.
//******************************************************************************

package com.scichart.examples.fragments;

import com.scichart.charting.model.dataSeries.IXyDataSeries;
import com.scichart.charting.modifiers.TooltipModifier;
import com.scichart.charting.visuals.SciChartSurface;
import com.scichart.charting.visuals.axes.IAxis;
import com.scichart.charting.visuals.renderableSeries.StackedMountainRenderableSeries;
import com.scichart.charting.visuals.renderableSeries.VerticallyStackedMountainsCollection;
import com.scichart.core.framework.UpdateSuspender;
import com.scichart.examples.R;
import com.scichart.examples.fragments.base.ExampleBaseFragment;

import java.util.Collections;

import butterknife.Bind;

public class StackedMountainChartFragment extends ExampleBaseFragment {
    @Bind(R.id.chart)
    SciChartSurface surface;

    @Override
    protected int getLayoutId() {
        return R.layout.example_single_chart_fragment;
    }

    @Override
    protected void initExample() {
        double[] yValues1 = new double[] { 4.0,  7,    5.2,  9.4,  3.8,  5.1, 7.5,  12.4, 14.6, 8.1, 11.7, 14.4, 16.0, 3.7, 5.1, 6.4, 3.5, 2.5, 12.4, 16.4, 7.1, 8.0, 9.0 };
        double[] yValues2 = new double[] { 15.0, 10.1, 10.2, 10.4, 10.8, 1.1, 11.5, 3.4,  4.6,  0.1, 1.7, 14.4, 6.0, 13.7, 10.1, 8.4, 8.5, 12.5, 1.4, 0.4, 10.1, 5.0, 1.0 };

        final IAxis xAxis = sciChartBuilder.newNumericAxis().build();
        final IAxis yAxis = sciChartBuilder.newNumericAxis().build();

        final IXyDataSeries<Double, Double> ds1 = sciChartBuilder.newXyDataSeries(Double.class, Double.class).build();
        final IXyDataSeries<Double, Double> ds2 = sciChartBuilder.newXyDataSeries(Double.class, Double.class).build();

        for (int i = 0; i < yValues1.length; i++) ds1.append((double) i, yValues1[i]);
        for (int i = 0; i < yValues2.length; i++) ds2.append((double) i, yValues2[i]);

        final StackedMountainRenderableSeries s1 = sciChartBuilder.newStackedMountain().withDataSeries(ds1).withLinearGradientColors(0xDDDBE0E1, 0x88B6C1C3).build();
        final StackedMountainRenderableSeries s2 = sciChartBuilder.newStackedMountain().withDataSeries(ds2).withLinearGradientColors(0xDDACBCCA, 0x88439AAF).build();

        final VerticallyStackedMountainsCollection seriesCollection = new VerticallyStackedMountainsCollection();
        seriesCollection.add(s1);
        seriesCollection.add(s2);

        UpdateSuspender.using(surface, new Runnable() {
            @Override
            public void run() {
                Collections.addAll(surface.getRenderableSeries(), seriesCollection);
                Collections.addAll(surface.getXAxes(), xAxis);
                Collections.addAll(surface.getYAxes(), yAxis);
                Collections.addAll(surface.getChartModifiers(), new TooltipModifier());
            }
        });
    }
}