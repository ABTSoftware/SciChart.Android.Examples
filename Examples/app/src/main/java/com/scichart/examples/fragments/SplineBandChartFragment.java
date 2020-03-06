//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2020. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// SplineBandChartFragment.java is part of the SCICHART® Examples. Permission is hereby granted
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
import com.scichart.charting.visuals.pointmarkers.EllipsePointMarker;
import com.scichart.charting.visuals.renderableSeries.SplineBandRenderableSeries;
import com.scichart.core.framework.UpdateSuspender;
import com.scichart.examples.R;
import com.scichart.examples.data.DataManager;
import com.scichart.examples.data.DoubleSeries;
import com.scichart.examples.fragments.base.ExampleBaseFragment;

import java.util.Collections;

import butterknife.BindView;

public class SplineBandChartFragment extends ExampleBaseFragment {
    @BindView(R.id.chart)
    SciChartSurface surface;

    @Override
    protected int getLayoutId() {
        return R.layout.example_single_chart_fragment;
    }

    @Override
    protected void initExample() {
        final IAxis xAxis = sciChartBuilder.newNumericAxis().withGrowBy(0.1, 0.1).build();
        final IAxis yAxis = sciChartBuilder.newNumericAxis().withGrowBy(0.2, 0.2).build();

        final DoubleSeries data = DataManager.getInstance().getDampedSinewave(1.0, 0.005, 1000, 13);
        final DoubleSeries moreData = DataManager.getInstance().getDampedSinewave(1.0, 0.005, 1000, 12);

        final XyyDataSeries<Double, Double> dataSeries = sciChartBuilder.newXyyDataSeries(Double.class, Double.class).build();
        for (int i = 0; i < 10; i++) {
            final int index = i * 100;
            dataSeries.append(data.xValues.get(index), data.yValues.get(index), moreData.yValues.get(index));
        }

        final SplineBandRenderableSeries rSeries = sciChartBuilder.newSplineBandSeries()
                .withDataSeries(dataSeries)
                .withFillColor(0x33279B27).withFillY1Color(0x33FF1919)
                .withStrokeStyle(0xFFFF1919, 1f, true).withStrokeY1Style(0xFF279B27, 1f, true)
                .withPointMarker(sciChartBuilder.newPointMarker(new EllipsePointMarker()).withSize(7, 7).withStroke(0xFF006400, 1).withFill(0xFFFFFFFF).build())
                .build();

        UpdateSuspender.using(surface, () -> {
            Collections.addAll(surface.getXAxes(), xAxis);
            Collections.addAll(surface.getYAxes(), yAxis);
            Collections.addAll(surface.getRenderableSeries(), rSeries);
            Collections.addAll(surface.getChartModifiers(), sciChartBuilder.newModifierGroupWithDefaultModifiers().build());

            sciChartBuilder.newAnimator(rSeries).withScaleTransformation().withInterpolator(new EasingInterpolator(Ease.ELASTIC_OUT)).withDuration(3000).withStartDelay(350).start();
        });
    }
}