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
import com.scichart.charting.visuals.annotations.AnnotationCoordinateMode;
import com.scichart.charting.visuals.annotations.HorizontalAnchorPoint;
import com.scichart.charting.visuals.annotations.TextAnnotation;
import com.scichart.charting.visuals.annotations.VerticalAnchorPoint;
import com.scichart.charting.visuals.axes.IAxis;
import com.scichart.charting.visuals.pointmarkers.EllipsePointMarker;
import com.scichart.charting.visuals.renderableSeries.FastLineRenderableSeries;
import com.scichart.core.framework.UpdateSuspender;
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

        final DoubleSeries sineWave = DataManager.getInstance().getSinewave(1.0, 0.0, 28, 7);

        originalData.append(sineWave.xValues, sineWave.yValues);
        splineData.append(sineWave.xValues, sineWave.yValues);

        final IAxis xAxis = sciChartBuilder.newNumericAxis().withGrowBy(0.1d, 0.1d).build();
        final IAxis yAxis = sciChartBuilder.newNumericAxis().withGrowBy(0.2d, 0.2d).build();

        final FastLineRenderableSeries lineSeries = sciChartBuilder.newLineSeries()
                .withDataSeries(originalData)
                .withPointMarker(sciChartBuilder.newPointMarker(new EllipsePointMarker()).withSize(7, 7).withStroke(0xFF006400, 1).withFill(0xFFFFFFFF).build())
                .withStrokeStyle(0xFF4282B4, 1f, true)
                .build();

        final SplineLineRenderableSeries splineSeries = new SplineLineRenderableSeriesBuilder(getActivity())
                .withDataSeries(splineData)
                .withStrokeStyle(0xFF006400, 2f, true)
                .withIsSplineEnabled(true)
                .withUpSampleFactor(10)
                .build();

        final TextAnnotation textAnnotation = sciChartBuilder.newTextAnnotation()
                .withText("Custom Spline Chart")
                .withHorizontalAnchorPoint(HorizontalAnchorPoint.Center)
                .withVerticalAnchorPoint(VerticalAnchorPoint.Top)
                .withCoordinateMode(AnnotationCoordinateMode.Relative)
                .withX1(0.5)
                .withY1(0.01)
                .build();

        UpdateSuspender.using(surface, new Runnable() {
            @Override
            public void run() {
                Collections.addAll(surface.getXAxes(), xAxis);
                Collections.addAll(surface.getYAxes(), yAxis);
                Collections.addAll(surface.getRenderableSeries(), splineSeries, lineSeries);
                Collections.addAll(surface.getChartModifiers(), new RubberBandXyZoomModifier(), new ZoomExtentsModifier());
                Collections.addAll(surface.getAnnotations(), textAnnotation);
            }
        });
    }
}