//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2017. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// DigitalMountainChartFragment.java is part of the SCICHART® Examples. Permission is hereby granted
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
import com.scichart.charting.visuals.renderableSeries.IRenderableSeries;
import com.scichart.core.framework.UpdateSuspender;
import com.scichart.data.model.DoubleRange;
import com.scichart.examples.R;
import com.scichart.examples.data.DataManager;
import com.scichart.examples.data.PriceSeries;
import com.scichart.examples.fragments.base.ExampleBaseFragment;

import java.util.Collections;
import java.util.Date;

import butterknife.Bind;

public class DigitalMountainChartFragment extends ExampleBaseFragment {
    @Bind(R.id.chart)
    SciChartSurface surface;

    @Override
    protected int getLayoutId() {
        return R.layout.example_single_chart_fragment;
    }

    @Override
    protected void initExample() {
        final IAxis xAxis = sciChartBuilder.newDateAxis()
                .withGrowBy(new DoubleRange(0.1d, 0.1d))
                .build();

        final IAxis yAxis = sciChartBuilder.newNumericAxis()
                .withGrowBy(new DoubleRange(0.1d, 0.1d))
                .build();

        final IXyDataSeries<Date, Double> dataSeries = sciChartBuilder.newXyDataSeries(Date.class, Double.class).build();
        final IRenderableSeries renderableSeries = sciChartBuilder.newMountainSeries()
                .withDataSeries(dataSeries)
                .withIsDigitalLine(true)
                .withStrokeStyle(0xAAFFC9A8)
                .withAreaFillLinearGradientColors(0xAAFF8D42,0x88090E11)
                .build();

        final PriceSeries priceData = DataManager.getInstance().getPriceDataIndu(getActivity());
        dataSeries.append(priceData.getDateData(), priceData.getCloseData());

        UpdateSuspender.using(surface, new Runnable() {
            @Override
            public void run() {
                Collections.addAll(surface.getXAxes(), xAxis);
                Collections.addAll(surface.getYAxes(), yAxis);
                Collections.addAll(surface.getRenderableSeries(), renderableSeries);
                Collections.addAll(surface.getChartModifiers(), sciChartBuilder.newModifierGroupWithDefaultModifiers().build());
            }
        });
    }
}