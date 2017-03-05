//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2017. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// ErrorBarsChartFragment.java is part of the SCICHART® Examples. Permission is hereby granted
// to modify, create derivative works, distribute and publish any part of this source
// code whether for commercial, private or personal use.
//
// The SCICHART® examples are distributed in the hope that they will be useful, but
// without any warranty. It is provided "AS IS" without warranty of any kind, either
// expressed or implied.
//******************************************************************************

package com.scichart.examples.fragments;

import com.scichart.charting.model.dataSeries.IXyDataSeries;
import com.scichart.charting.model.dataSeries.XyDataSeries;
import com.scichart.charting.visuals.SciChartSurface;
import com.scichart.charting.visuals.axes.IAxis;
import com.scichart.charting.visuals.pointmarkers.EllipsePointMarker;
import com.scichart.charting.visuals.pointmarkers.IPointMarker;
import com.scichart.charting.visuals.renderableSeries.ErrorDirection;
import com.scichart.charting.visuals.renderableSeries.ErrorMode;
import com.scichart.charting.visuals.renderableSeries.ErrorType;
import com.scichart.charting.visuals.renderableSeries.FastFixedErrorBarsRenderableSeries;
import com.scichart.charting.visuals.renderableSeries.FastLineRenderableSeries;
import com.scichart.core.framework.UpdateSuspender;
import com.scichart.drawing.utility.ColorUtil;
import com.scichart.examples.R;
import com.scichart.examples.fragments.base.ExampleBaseFragment;

import java.util.Collections;

import butterknife.Bind;

public class ErrorBarsChartFragment extends ExampleBaseFragment {
    @Bind(R.id.chart)
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
        final IXyDataSeries<Double, Double> dataSeries = new XyDataSeries<>(Double.class, Double.class);

        dataSeries.append(new Double[]{0d, 1d, 2d, 3d, 4d, 5d, 6d, 7d, 8d, 9d, 10d},
                new Double[]{0.8d, 1d, 0.1d, -0.75d, -1d, -1.2d, -0.4d, 0.6d, 1.5d, 0.5d, -0.5d});

        final IAxis xAxis = sciChartBuilder.newNumericAxis().withGrowBy(0.1, 0.1).build();
        final IAxis yAxis = sciChartBuilder.newNumericAxis().withGrowBy(0.1, 0.1).build();

        final FastFixedErrorBarsRenderableSeries verticalErrorBars = sciChartBuilder.newFixedErrorBarsSeries()
                .withDataSeries(dataSeries)
                .withStrokeStyle(ColorUtil.argb(255, 70, 130, 180))
                .build();

        verticalErrorBars.setErrorHigh(0.3);
        verticalErrorBars.setErrorLow(0.1);
        verticalErrorBars.setErrorType(ErrorType.Relative);

        verticalErrorBars.setErrorMode(ErrorMode.Both);

        final FastFixedErrorBarsRenderableSeries horizontalErrorBars = sciChartBuilder.newFixedErrorBarsSeries()
                .withDataSeries(dataSeries)
                .withStrokeStyle(0xFFFF0000)
                .build();

        horizontalErrorBars.setErrorDirection(ErrorDirection.Horizontal);

        horizontalErrorBars.setErrorHigh(0.3);
        horizontalErrorBars.setErrorLow(0.3);
        horizontalErrorBars.setErrorType(ErrorType.Absolute);

        horizontalErrorBars.setErrorMode(ErrorMode.Both);

        IPointMarker pointMarker = sciChartBuilder.newPointMarker(new EllipsePointMarker())
                .withSize(15, 15)
                .withStroke(ColorUtil.argb(255, 176, 196, 222), 2)
                .withFill(ColorUtil.argb(255, 70, 130, 180))
                .build();

        final FastLineRenderableSeries lineRenderableSeries = sciChartBuilder.newLineSeries()
                .withDataSeries(dataSeries)
                .withPointMarker(pointMarker)
                .withStrokeStyle(ColorUtil.argb(255, 176, 196, 222))
                .build();

        UpdateSuspender.using(surface, new Runnable() {
            @Override
            public void run() {
                Collections.addAll(surface.getXAxes(), xAxis);
                Collections.addAll(surface.getYAxes(), yAxis);
                Collections.addAll(surface.getRenderableSeries(), horizontalErrorBars, verticalErrorBars, lineRenderableSeries);
                Collections.addAll(surface.getChartModifiers(), sciChartBuilder.newModifierGroupWithDefaultModifiers().build());
            }
        });
    }
}
