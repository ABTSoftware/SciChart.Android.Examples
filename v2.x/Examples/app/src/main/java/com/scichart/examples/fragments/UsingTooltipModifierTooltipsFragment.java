//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2017. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// UsingTooltipModifierTooltipsFragment.java is part of the SCICHART® Examples. Permission is hereby granted
// to modify, create derivative works, distribute and publish any part of this source
// code whether for commercial, private or personal use.
//
// The SCICHART® examples are distributed in the hope that they will be useful, but
// without any warranty. It is provided "AS IS" without warranty of any kind, either
// expressed or implied.
//******************************************************************************

package com.scichart.examples.fragments;

import com.scichart.charting.model.dataSeries.XyDataSeries;
import com.scichart.charting.modifiers.TooltipModifier;
import com.scichart.charting.visuals.SciChartSurface;
import com.scichart.charting.visuals.axes.AxisAlignment;
import com.scichart.charting.visuals.axes.IAxis;
import com.scichart.charting.visuals.pointmarkers.EllipsePointMarker;
import com.scichart.charting.visuals.renderableSeries.FastLineRenderableSeries;
import com.scichart.core.framework.UpdateSuspender;
import com.scichart.core.model.DoubleValues;
import com.scichart.drawing.utility.ColorUtil;
import com.scichart.examples.R;
import com.scichart.examples.data.DataManager;
import com.scichart.examples.data.DoubleSeries;
import com.scichart.examples.fragments.base.ExampleBaseFragment;

import java.util.Collections;

import butterknife.Bind;

public class UsingTooltipModifierTooltipsFragment extends ExampleBaseFragment {

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
        final IAxis xAxis = sciChartBuilder.newNumericAxis().withGrowBy(0.1, 0.1).build();
        final IAxis yAxis = sciChartBuilder.newNumericAxis().withGrowBy(0.1, 0.1).withAxisAlignment(AxisAlignment.Left).build();

        final XyDataSeries<Double, Double> dataSeries1 = sciChartBuilder.newXyDataSeries(Double.class, Double.class).withSeriesName("Lissajous Curve").withAcceptsUnsortedData().build();
        final XyDataSeries<Double, Double> dataSeries2 = sciChartBuilder.newXyDataSeries(Double.class, Double.class).withSeriesName("Sinewave").withAcceptsUnsortedData().build();

        final DoubleSeries ds1Points = DataManager.getInstance().getLissajousCurve(0.8, 0.2, 0.43, 500);
        final DoubleSeries ds2Points = DataManager.getInstance().getSinewave(1.5, 1.0, 500);

        final DoubleValues scaledXValues = getScaledValues(ds1Points.xValues);

        dataSeries1.append(scaledXValues, ds1Points.yValues);
        dataSeries2.append(ds2Points.xValues, ds2Points.yValues);

        final FastLineRenderableSeries line1 = sciChartBuilder.newLineSeries()
                .withStrokeStyle(ColorUtil.SteelBlue, 2f, true)
                .withPointMarker(sciChartBuilder.newPointMarker(new EllipsePointMarker()).withSize(5).withStroke(ColorUtil.SteelBlue, 2f).withFill(ColorUtil.SteelBlue).build())
                .withDataSeries(dataSeries1)
                .build();
        final FastLineRenderableSeries line2 = sciChartBuilder.newLineSeries()
                .withStrokeStyle(0xFFFF3333, 2f, true)
                .withPointMarker(sciChartBuilder.newPointMarker(new EllipsePointMarker()).withSize(5).withStroke(0xFFFF3333, 2f).withFill(0xFFFF3333).build())
                .withDataSeries(dataSeries2)
                .build();

        UpdateSuspender.using(surface, new Runnable() {
            @Override
            public void run() {
                Collections.addAll(surface.getXAxes(), xAxis);
                Collections.addAll(surface.getYAxes(), yAxis);
                Collections.addAll(surface.getRenderableSeries(), line1, line2);
                Collections.addAll(surface.getChartModifiers(), new TooltipModifier());
            }
        });
    }

    private DoubleValues getScaledValues(DoubleValues values) {
        final DoubleValues result = new DoubleValues();
        for (double value : values.getItemsArray()) {
            result.add((value + 1) * 5);
        }
        return result;
    }
}