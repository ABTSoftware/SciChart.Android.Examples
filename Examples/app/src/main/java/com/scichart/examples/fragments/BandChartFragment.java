//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2017. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// BandChartFragment.java is part of the SCICHART® Examples. Permission is hereby granted
// to modify, create derivative works, distribute and publish any part of this source
// code whether for commercial, private or personal use.
//
// The SCICHART® examples are distributed in the hope that they will be useful, but
// without any warranty. It is provided "AS IS" without warranty of any kind, either
// expressed or implied.
//******************************************************************************

package com.scichart.examples.fragments;

import com.daasuu.ei.Ease;
import com.daasuu.ei.EasingInterpolator;
import com.scichart.charting.model.dataSeries.XyyDataSeries;
import com.scichart.charting.visuals.SciChartSurface;
import com.scichart.charting.visuals.axes.IAxis;
import com.scichart.charting.visuals.renderableSeries.FastBandRenderableSeries;
import com.scichart.core.framework.UpdateSuspender;
import com.scichart.examples.R;
import com.scichart.examples.data.DataManager;
import com.scichart.examples.data.DoubleSeries;
import com.scichart.examples.fragments.base.ExampleBaseFragment;

import java.util.Collections;

import butterknife.BindView;

public class BandChartFragment extends ExampleBaseFragment {
    @BindView(R.id.chart)
    SciChartSurface surface;

    @Override
    protected int getLayoutId() {
        return R.layout.example_single_chart_fragment;
    }

    @Override
    protected void initExample() {
        final IAxis xAxis = sciChartBuilder.newNumericAxis().withVisibleRange(1.1, 2.7).build();
        final IAxis yAxis = sciChartBuilder.newNumericAxis().withGrowBy(0.1, 0.1).build();

        final DoubleSeries data = DataManager.getInstance().getDampedSinewave(1.0, 0.01, 1000, 10);
        final DoubleSeries moreData = DataManager.getInstance().getDampedSinewave(1.0, 0.005, 1000, 12);

        final XyyDataSeries<Double, Double> dataSeries = sciChartBuilder.newXyyDataSeries(Double.class, Double.class).build();
        dataSeries.append(data.xValues, data.yValues, moreData.yValues);

        final FastBandRenderableSeries rSeries = sciChartBuilder.newBandSeries()
                .withDataSeries(dataSeries)
                .withFillColor(0x33279B27).withFillY1Color(0x33FF1919)
                .withStrokeStyle(0xFFFF1919, 1f, true).withStrokeY1Style(0xFF279B27, 1f, true)
                .build();

        UpdateSuspender.using(surface, new Runnable() {
            @Override
            public void run() {
                Collections.addAll(surface.getXAxes(), xAxis);
                Collections.addAll(surface.getYAxes(), yAxis);
                Collections.addAll(surface.getRenderableSeries(), rSeries);
                Collections.addAll(surface.getChartModifiers(), sciChartBuilder.newModifierGroupWithDefaultModifiers().build());

                sciChartBuilder.newAnimator(rSeries).withScaleTransformation().withInterpolator(new EasingInterpolator(Ease.ELASTIC_OUT)).withDuration(3000).withStartDelay(350).start();
            }
        });
    }
}