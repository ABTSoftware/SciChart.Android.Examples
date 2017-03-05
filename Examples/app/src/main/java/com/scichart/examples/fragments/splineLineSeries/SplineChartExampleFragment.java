//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2017. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// SplineChartExampleFragment.java is part of the SCICHART® Examples. Permission is hereby granted
// to modify, create derivative works, distribute and publish any part of this source
// code whether for commercial, private or personal use.
//
// The SCICHART® examples are distributed in the hope that they will be useful, but
// without any warranty. It is provided "AS IS" without warranty of any kind, either
// expressed or implied.
//******************************************************************************

package com.scichart.examples.fragments.splineLineSeries;

import com.scichart.charting.model.dataSeries.IXyDataSeries;
import com.scichart.charting.modifiers.RubberBandXyZoomModifier;
import com.scichart.charting.modifiers.ZoomExtentsModifier;
import com.scichart.charting.visuals.SciChartSurface;
import com.scichart.charting.visuals.axes.IAxis;
import com.scichart.charting.visuals.pointmarkers.EllipsePointMarker;
import com.scichart.charting.visuals.pointmarkers.IPointMarker;
import com.scichart.charting.visuals.renderableSeries.FastLineRenderableSeries;
import com.scichart.core.framework.UpdateSuspender;
import com.scichart.data.model.DoubleRange;
import com.scichart.examples.R;
import com.scichart.examples.data.DataManager;
import com.scichart.examples.data.DoubleSeries;
import com.scichart.examples.fragments.base.ExampleBaseFragment;
import com.scichart.examples.fragments.splineLineSeries.SplineLineRenderableSeries.SplineLineRenderableSeriesBuilder;

import java.util.Collections;

import butterknife.Bind;

public class SplineChartExampleFragment extends ExampleBaseFragment {

    @Bind(R.id.chart)
    SciChartSurface surface;

    @Override
    protected int getLayoutId() {
        return R.layout.example_single_chart_fragment;
    }

    @Override
    protected void initExample() {
        final IXyDataSeries<Double, Double> originalData = sciChartBuilder.newXyDataSeries(Double.class, Double.class).withSeriesName("Original").build();
        final IXyDataSeries<Double, Double> splineData = sciChartBuilder.newXyDataSeries(Double.class, Double.class).withSeriesName("Spline").build();

        final DoubleSeries sineWave = DataManager.getInstance().getSinewave(1.0, 0.0, 100, 25);

        originalData.append(sineWave.xValues, sineWave.yValues);
        splineData.append(sineWave.xValues, sineWave.yValues);

        final IAxis xAxis = sciChartBuilder.newNumericAxis().withGrowBy(new DoubleRange(0.1d, 0.1d)).build();
        final IAxis yAxis = sciChartBuilder.newNumericAxis().withGrowBy(new DoubleRange(0.1d, 0.1d)).build();

        final FastLineRenderableSeries lineSeries = sciChartBuilder.newLineSeries()
                .withDataSeries(originalData)
                .withStrokeStyle(0xFF4282B4, 1)
                .build();

        IPointMarker pointMarker = sciChartBuilder.newPointMarker(new EllipsePointMarker()).withSize(5, 5).withStroke(0xFF006400, 1).withFill(0xFF006400).build();

        final SplineLineRenderableSeries splineSeries = new SplineLineRenderableSeriesBuilder(getActivity())
                .withDataSeries(splineData)
                .withStrokeStyle(0xFF006400, 1, false)
                .withPointMarker(pointMarker)
                .withIsSplineEnabled(true)
                .withUpSampleFactor(20)
                .build();

        UpdateSuspender.using(surface, new Runnable() {
            @Override
            public void run() {
                Collections.addAll(surface.getXAxes(), xAxis);
                Collections.addAll(surface.getYAxes(), yAxis);
                Collections.addAll(surface.getRenderableSeries(), lineSeries, splineSeries);
                Collections.addAll(surface.getChartModifiers(), new RubberBandXyZoomModifier(), new ZoomExtentsModifier());
            }
        });
    }
}
