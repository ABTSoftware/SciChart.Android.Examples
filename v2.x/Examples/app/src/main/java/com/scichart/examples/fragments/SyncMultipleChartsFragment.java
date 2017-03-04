//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2017. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// SyncMultipleChartsFragment.java is part of the SCICHART® Examples. Permission is hereby granted
// to modify, create derivative works, distribute and publish any part of this source
// code whether for commercial, private or personal use.
//
// The SCICHART® examples are distributed in the hope that they will be useful, but
// without any warranty. It is provided "AS IS" without warranty of any kind, either
// expressed or implied.
//******************************************************************************

package com.scichart.examples.fragments;

import com.scichart.charting.model.dataSeries.IDataSeries;
import com.scichart.charting.model.dataSeries.IXyDataSeries;
import com.scichart.charting.model.dataSeries.XyDataSeries;
import com.scichart.charting.visuals.SciChartSurface;
import com.scichart.charting.visuals.axes.NumericAxis;
import com.scichart.charting.visuals.renderableSeries.FastLineRenderableSeries;
import com.scichart.data.model.DoubleRange;
import com.scichart.data.model.IRange;
import com.scichart.drawing.utility.ColorUtil;
import com.scichart.examples.R;
import com.scichart.examples.fragments.base.ExampleBaseFragment;

import java.util.Collections;

import butterknife.Bind;

public class SyncMultipleChartsFragment extends ExampleBaseFragment {

    private final static int POINTS_COUNT = 1000;

    private IRange sharedXRange = new DoubleRange(0d, 1d);
    private IRange sharedYRange = new DoubleRange(0d, 1d);

    @Bind(R.id.chart0)
    SciChartSurface chart0;

    @Bind(R.id.chart1)
    SciChartSurface chart1;

    @Override
    public boolean showDefaultModifiersInToolbar() {
        return false;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.example_sync_multiple_charts_fragment;
    }

    @Override
    protected void initExample() {
        final NumericAxis xAxis0 = sciChartBuilder.newNumericAxis()
                .withGrowBy(new DoubleRange(0.1d, 0.1d))
                .withVisibleRange(sharedXRange)
                .withDrawMajorBands(true)
                .build();

        final NumericAxis yAxis0 = sciChartBuilder.newNumericAxis()
                .withGrowBy(new DoubleRange(0.1d, 0.1d))
                .withVisibleRange(sharedYRange)
                .build();

        final FastLineRenderableSeries line0 = sciChartBuilder.newLineSeries()
                .withDataSeries(createDataSeries())
                .withStrokeStyle(ColorUtil.Green)
                .build();

        Collections.addAll(chart0.getXAxes(), xAxis0);
        Collections.addAll(chart0.getYAxes(), yAxis0);
        Collections.addAll(chart0.getRenderableSeries(), line0);
        Collections.addAll(chart0.getChartModifiers(), sciChartBuilder.newModifierGroupWithDefaultModifiers().build());

        chart0.zoomExtents();

        final NumericAxis xAxis1 = sciChartBuilder.newNumericAxis()
                .withGrowBy(new DoubleRange(0.1d, 0.1d))
                .withVisibleRange(sharedXRange)
                .withDrawMajorBands(true)
                .build();

        final NumericAxis yAxis1 = sciChartBuilder.newNumericAxis()
                .withGrowBy(new DoubleRange(0.1d, 0.1d))
                .withVisibleRange(sharedYRange)
                .build();

        final FastLineRenderableSeries line1 = sciChartBuilder.newLineSeries()
                .withDataSeries(createDataSeries())
                .withStrokeStyle(ColorUtil.Green)
                .build();


        Collections.addAll(chart1.getXAxes(), xAxis1);
        Collections.addAll(chart1.getYAxes(), yAxis1);
        Collections.addAll(chart1.getRenderableSeries(), line1);
        Collections.addAll(chart1.getChartModifiers(), sciChartBuilder.newModifierGroupWithDefaultModifiers().build());

        chart1.zoomExtents();
    }

    private IDataSeries createDataSeries(){
        IXyDataSeries<Double, Double> dataSeries = new XyDataSeries<>(Double.class, Double.class);

        for (int i = 0; i < POINTS_COUNT; i++) {
            dataSeries.append((double)i, POINTS_COUNT * Math.sin(i * Math.PI * 0.1)/ i);
        }

        return dataSeries;
    }
}
