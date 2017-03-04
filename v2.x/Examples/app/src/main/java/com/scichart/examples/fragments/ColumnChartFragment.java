//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2017. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// ColumnChartFragment.java is part of the SCICHART® Examples. Permission is hereby granted
// to modify, create derivative works, distribute and publish any part of this source
// code whether for commercial, private or personal use.
//
// The SCICHART® examples are distributed in the hope that they will be useful, but
// without any warranty. It is provided "AS IS" without warranty of any kind, either
// expressed or implied.
//******************************************************************************

package com.scichart.examples.fragments;


import com.scichart.charting.model.dataSeries.IXyDataSeries;
import com.scichart.charting.visuals.SciChartSurface;
import com.scichart.charting.visuals.axes.IAxis;
import com.scichart.charting.visuals.renderableSeries.FastColumnRenderableSeries;
import com.scichart.core.framework.UpdateSuspender;
import com.scichart.data.model.DoubleRange;
import com.scichart.drawing.utility.ColorUtil;
import com.scichart.examples.R;
import com.scichart.examples.fragments.base.ExampleBaseFragment;

import java.util.Collections;

import butterknife.Bind;

public class ColumnChartFragment extends ExampleBaseFragment {

    @Bind(R.id.chart)
    SciChartSurface surface;

    @Override
    protected int getLayoutId() {
        return R.layout.example_single_chart_fragment;
    }

    @Override
    protected void initExample() {
        IXyDataSeries<Integer, Double> dataSeries = sciChartBuilder.newXyDataSeries(Integer.class, Double.class).build();
        final double[] yValues = {0.1, 0.2, 0.4, 0.8, 1.1, 1.5, 2.4, 4.6, 8.1, 11.7, 14.4, 16.0, 13.7, 10.1, 6.4, 3.5, 2.5, 1.4, 0.4, 0.1};

        for (int i = 0; i < yValues.length; i++) {
            dataSeries.append(i, yValues[i]);
        }

        final IAxis xAxis = sciChartBuilder.newNumericAxis().build();
        final IAxis yAxis = sciChartBuilder.newNumericAxis().withGrowBy(new DoubleRange(0d, 0.1d)).build();

        final FastColumnRenderableSeries columnSeries = sciChartBuilder.newColumnSeries()
                .withStrokeStyle(0xA99A8A)
                .withDataPointWidth(1)
                .withLinearGradientColors(ColorUtil.LightSteelBlue, ColorUtil.SteelBlue)
                .withDataSeries(dataSeries)
                .build();

        surface.getChartModifiers().add(sciChartBuilder.newModifierGroupWithDefaultModifiers().build());

        UpdateSuspender.using(surface, new Runnable() {
            @Override
            public void run() {
                Collections.addAll(surface.getXAxes(), xAxis);
                Collections.addAll(surface.getYAxes(), yAxis);
                Collections.addAll(surface.getRenderableSeries(), columnSeries);
            }
        });
    }
}
