//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2017. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// StackedColumnChartFragment.java is part of the SCICHART® Examples. Permission is hereby granted
// to modify, create derivative works, distribute and publish any part of this source
// code whether for commercial, private or personal use.
//
// The SCICHART® examples are distributed in the hope that they will be useful, but
// without any warranty. It is provided "AS IS" without warranty of any kind, either
// expressed or implied.
//******************************************************************************

package com.scichart.examples.fragments;

import com.scichart.charting.model.dataSeries.IXyDataSeries;
import com.scichart.charting.modifiers.RolloverModifier;
import com.scichart.charting.modifiers.ZoomExtentsModifier;
import com.scichart.charting.visuals.SciChartSurface;
import com.scichart.charting.visuals.axes.IAxis;
import com.scichart.charting.visuals.renderableSeries.HorizontallyStackedColumnsCollection;
import com.scichart.charting.visuals.renderableSeries.StackedColumnRenderableSeries;
import com.scichart.charting.visuals.renderableSeries.VerticallyStackedColumnsCollection;
import com.scichart.core.framework.UpdateSuspender;
import com.scichart.examples.R;
import com.scichart.examples.fragments.base.ExampleBaseFragment;

import java.util.Collections;

import butterknife.Bind;

public class StackedColumnChartFragment extends ExampleBaseFragment {
    @Bind(R.id.chart)
    SciChartSurface surface;

    @Override
    protected int getLayoutId() {
        return R.layout.example_single_chart_fragment;
    }

    @Override
    protected void initExample() {
        final IAxis xAxis = sciChartBuilder.newNumericAxis().build();
        final IAxis yAxis = sciChartBuilder.newNumericAxis().build();

        double[] porkData = new double[]{10, 13, 7, 16, 4, 6, 20, 14, 16, 10, 24, 11};
        double[] vealData = new double[]{12, 17, 21, 15, 19, 18, 13, 21, 22, 20, 5, 10};
        double[] tomatoesData = new double[]{7, 30, 27, 24, 21, 15, 17, 26, 22, 28, 21, 22};
        double[] cucumberData = new double[]{16, 10, 9, 8, 22, 14, 12, 27, 25, 23, 17, 17};
        double[] pepperData = new double[]{7, 24, 21, 11, 19, 17, 14, 27, 26, 22, 28, 16};

        final IXyDataSeries<Double, Double> ds1 = sciChartBuilder.newXyDataSeries(Double.class, Double.class).withSeriesName("Pork Series").build();
        final IXyDataSeries<Double, Double> ds2 = sciChartBuilder.newXyDataSeries(Double.class, Double.class).withSeriesName("Veal Series").build();
        final IXyDataSeries<Double, Double> ds3 = sciChartBuilder.newXyDataSeries(Double.class, Double.class).withSeriesName("Tomato Series").build();
        final IXyDataSeries<Double, Double> ds4 = sciChartBuilder.newXyDataSeries(Double.class, Double.class).withSeriesName("Cucumber Series").build();
        final IXyDataSeries<Double, Double> ds5 = sciChartBuilder.newXyDataSeries(Double.class, Double.class).withSeriesName("Pepper Series").build();

        final int data = 1992;
        for (int i = 0; i < porkData.length; i++) {
            double xValue = data + i;
            ds1.append(xValue, porkData[i]);
            ds2.append(xValue, vealData[i]);
            ds3.append(xValue, tomatoesData[i]);
            ds4.append(xValue, cucumberData[i]);
            ds5.append(xValue, pepperData[i]);
        }

        final StackedColumnRenderableSeries porkSeries = sciChartBuilder.newStackedColumn().withDataSeries(ds1).withFillColor(0xff226fb7).withStrokeStyle(0xff22579D, 0f).build();
        final StackedColumnRenderableSeries vealSeries = sciChartBuilder.newStackedColumn().withDataSeries(ds2).withFillColor(0xffff9a2e).withStrokeStyle(0xffBE642D, 0f).build();
        final StackedColumnRenderableSeries tomatoSeries = sciChartBuilder.newStackedColumn().withDataSeries(ds3).withFillColor(0xffdc443f).withStrokeStyle(0xffA33631, 0f).build();
        final StackedColumnRenderableSeries cucumberSeries = sciChartBuilder.newStackedColumn().withDataSeries(ds4).withFillColor(0xffaad34f).withStrokeStyle(0xff73953D, 0f).build();
        final StackedColumnRenderableSeries pepperSeries = sciChartBuilder.newStackedColumn().withDataSeries(ds5).withFillColor(0xff8562b4).withStrokeStyle(0xff64458A, 0f).build();

        final VerticallyStackedColumnsCollection verticalCollection1 = new VerticallyStackedColumnsCollection();
        verticalCollection1.add(porkSeries);
        verticalCollection1.add(vealSeries);

        final VerticallyStackedColumnsCollection verticalCollection2 = new VerticallyStackedColumnsCollection();
        verticalCollection2.add(tomatoSeries);
        verticalCollection2.add(cucumberSeries);
        verticalCollection2.add(pepperSeries);

        final HorizontallyStackedColumnsCollection columnsCollection = new HorizontallyStackedColumnsCollection();
        columnsCollection.setSpacing(0f);

        columnsCollection.add(verticalCollection1);
        columnsCollection.add(verticalCollection2);

        UpdateSuspender.using(surface, new Runnable() {
            @Override
            public void run() {
                Collections.addAll(surface.getRenderableSeries(), columnsCollection);
                Collections.addAll(surface.getXAxes(), xAxis);
                Collections.addAll(surface.getYAxes(), yAxis);
                Collections.addAll(surface.getChartModifiers(), new RolloverModifier());
                Collections.addAll(surface.getChartModifiers(), new ZoomExtentsModifier());
            }
        });
    }
}