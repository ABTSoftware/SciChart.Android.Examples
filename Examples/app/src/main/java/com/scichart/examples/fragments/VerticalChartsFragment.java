//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2017. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// VerticalChartsFragment.java is part of the SCICHART® Examples. Permission is hereby granted
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
import com.scichart.charting.visuals.axes.AxisAlignment;
import com.scichart.charting.visuals.axes.IAxis;
import com.scichart.charting.visuals.renderableSeries.IRenderableSeries;
import com.scichart.core.framework.UpdateSuspender;
import com.scichart.core.model.DoubleValues;
import com.scichart.data.model.DoubleRange;
import com.scichart.drawing.utility.ColorUtil;
import com.scichart.examples.R;
import com.scichart.examples.data.DataManager;
import com.scichart.examples.fragments.base.ExampleBaseFragment;

import java.util.Collections;

import butterknife.Bind;

public class VerticalChartsFragment extends ExampleBaseFragment {

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
        final IAxis xAxis = sciChartBuilder.newNumericAxis().withAxisAlignment(AxisAlignment.Left).withAxisTitle("X-Axis").build();
        final IAxis yAxis = sciChartBuilder.newNumericAxis().withAxisAlignment(AxisAlignment.Top).withAxisTitle("Y-Axis").withGrowBy(new DoubleRange(0d, 0.1d)).build();

        IXyDataSeries<Double, Double> dataSeries0 = sciChartBuilder.newXyDataSeries(Double.class, Double.class).build();
        IXyDataSeries<Double, Double> dataSeries1 = sciChartBuilder.newXyDataSeries(Double.class, Double.class).build();

        final DoubleValues xValues = new DoubleValues();
        final DoubleValues yValues = new DoubleValues();

        DataManager.getInstance().setRandomDoubleSeries(xValues, yValues, 20);
        dataSeries0.append(xValues, yValues);

        xValues.clear();
        yValues.clear();

        DataManager.getInstance().setRandomDoubleSeries(xValues, yValues, 20);
        dataSeries1.append(xValues, yValues);

        final IRenderableSeries lineSeries0 = sciChartBuilder.newLineSeries()
                .withDataSeries(dataSeries0)
                .withStrokeStyle(ColorUtil.SteelBlue, 3)
                .build();

        final IRenderableSeries lineSeries1 = sciChartBuilder.newLineSeries()
                .withDataSeries(dataSeries1)
                .withStrokeStyle(ColorUtil.Lime, 3)
                .build();

        UpdateSuspender.using(surface, new Runnable() {
            @Override
            public void run() {
                Collections.addAll(surface.getXAxes(), xAxis);
                Collections.addAll(surface.getYAxes(), yAxis);
                Collections.addAll(surface.getRenderableSeries(), lineSeries0, lineSeries1);
                Collections.addAll(surface.getChartModifiers(), sciChartBuilder.newModifierGroupWithDefaultModifiers().build());
            }
        });
    }
}
