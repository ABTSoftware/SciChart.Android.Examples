//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2017. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// ScatterChartFragment.java is part of the SCICHART® Examples. Permission is hereby granted
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
import com.scichart.charting.visuals.pointmarkers.EllipsePointMarker;
import com.scichart.charting.visuals.renderableSeries.IRenderableSeries;
import com.scichart.core.framework.UpdateSuspender;
import com.scichart.data.model.DoubleRange;
import com.scichart.drawing.utility.ColorUtil;
import com.scichart.examples.R;
import com.scichart.examples.data.DataManager;
import com.scichart.examples.data.DoubleSeries;
import com.scichart.examples.fragments.base.ExampleBaseFragment;

import java.util.Collections;

import butterknife.Bind;

public class ScatterChartFragment extends ExampleBaseFragment {

    @Bind(R.id.chart)
    SciChartSurface surface;

    private final IXyDataSeries<Double, Double> dataSeries = sciChartBuilder.newXyDataSeries(Double.class, Double.class).build();

    @Override
    protected int getLayoutId() {
        return R.layout.example_single_chart_fragment;
    }

    @Override
    protected void initExample() {
        UpdateSuspender.using(surface, new Runnable() {
            @Override
            public void run() {

                final DoubleSeries ds1Points = DataManager.getInstance().getDampedSinewave(1.0, 0.02, 150, 5);

                dataSeries.append(ds1Points.xValues, ds1Points.yValues);

                final IAxis xBottomAxis = sciChartBuilder.newNumericAxis()
                        .withGrowBy(new DoubleRange(0.1d, 0.1d))
                        .build();

                final IAxis yRightAxis = sciChartBuilder.newNumericAxis()
                        .withGrowBy(new DoubleRange(0.1d, 0.1d))
                        .build();

                final EllipsePointMarker pointMarker = sciChartBuilder.newPointMarker(new EllipsePointMarker())
                        .withSize(15, 15)
                        .withStroke(ColorUtil.argb(255, 176, 196, 222), 2)
                        .withFill(ColorUtil.argb(255, 70, 130, 180))
                        .build();

                final IRenderableSeries rs1 = sciChartBuilder.newScatterSeries()
                        .withDataSeries(dataSeries)
                        .withPointMarker(pointMarker)
                        .withXAxisId(xBottomAxis.getAxisId())
                        .withYAxisId(yRightAxis.getAxisId())
                        .withStrokeStyle(ColorUtil.argb(0xFF, 0x27, 0x9B, 0x27))
                        .build();

                Collections.addAll(surface.getXAxes(), xBottomAxis);
                Collections.addAll(surface.getYAxes(), yRightAxis);
                Collections.addAll(surface.getRenderableSeries(), rs1);
                Collections.addAll(surface.getChartModifiers(), sciChartBuilder.newModifierGroupWithDefaultModifiers().build());
            }
        });
    }
}