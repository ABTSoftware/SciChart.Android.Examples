//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2017. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// DigitalLineChartFragment.java is part of the SCICHART® Examples. Permission is hereby granted
// to modify, create derivative works, distribute and publish any part of this source
// code whether for commercial, private or personal use.
//
// The SCICHART® examples are distributed in the hope that they will be useful, but
// without any warranty. It is provided "AS IS" without warranty of any kind, either
// expressed or implied.
//******************************************************************************

package com.scichart.examples.fragments;

import com.scichart.charting.model.dataSeries.XyDataSeries;
import com.scichart.charting.visuals.SciChartSurface;
import com.scichart.charting.visuals.axes.NumericAxis;
import com.scichart.charting.visuals.renderableSeries.FastLineRenderableSeries;
import com.scichart.core.framework.UpdateSuspender;
import com.scichart.examples.R;
import com.scichart.examples.data.DataManager;
import com.scichart.examples.data.DoubleSeries;
import com.scichart.examples.fragments.base.ExampleBaseFragment;

import java.util.Collections;

import butterknife.Bind;

public class DigitalLineChartFragment extends ExampleBaseFragment {
    @Bind(R.id.chart)
    SciChartSurface surface;

    @Override
    protected int getLayoutId() {
        return R.layout.example_single_chart_fragment;
    }

    @Override
    protected void initExample() {
        final NumericAxis xAxis = sciChartBuilder.newNumericAxis().withGrowBy(0.1, 0.1).withDrawMajorBands(true).withVisibleRange(1, 1.25).build();
        final NumericAxis yAxis = sciChartBuilder.newNumericAxis().withGrowBy(0.5, 0.5).withDrawMajorBands(true).withVisibleRange(2.3, 3.3).build();

        final XyDataSeries<Double, Double> dataSeries = sciChartBuilder.newXyDataSeries(Double.class, Double.class).build();
        final FastLineRenderableSeries renderableSeries = sciChartBuilder.newLineSeries()
                .withDataSeries(dataSeries)
                .withIsDigitalLine(true)
                .withStrokeStyle(0xFF99EE99)
                .build();

        final DoubleSeries data = DataManager.getInstance().getFourierSeries(1.0, 0.1, 5000);
        dataSeries.append(data.xValues, data.yValues);

        UpdateSuspender.using(surface, new Runnable() {
            @Override
            public void run() {
                Collections.addAll(surface.getXAxes(), xAxis);
                Collections.addAll(surface.getYAxes(), yAxis);
                Collections.addAll(surface.getRenderableSeries(), renderableSeries);

                surface.getChartModifiers().add(sciChartBuilder.newModifierGroupWithDefaultModifiers().build());
            }
        });
    }
}