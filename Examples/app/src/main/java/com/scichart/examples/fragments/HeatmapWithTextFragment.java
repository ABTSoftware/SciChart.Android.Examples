//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2017. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// HeatmapWithTextFragment.java is part of the SCICHART® Examples. Permission is hereby granted
// to modify, create derivative works, distribute and publish any part of this source
// code whether for commercial, private or personal use.
//
// The SCICHART® examples are distributed in the hope that they will be useful, but
// without any warranty. It is provided "AS IS" without warranty of any kind, either
// expressed or implied.
//******************************************************************************

package com.scichart.examples.fragments;

import com.scichart.charting.model.dataSeries.IDataSeries;
import com.scichart.charting.model.dataSeries.UniformHeatmapDataSeries;
import com.scichart.charting.visuals.SciChartHeatmapColourMap;
import com.scichart.charting.visuals.SciChartSurface;
import com.scichart.charting.visuals.axes.NumericAxis;
import com.scichart.charting.visuals.renderableSeries.FastUniformHeatmapRenderableSeries;
import com.scichart.drawing.utility.ColorUtil;
import com.scichart.examples.R;
import com.scichart.examples.fragments.base.ExampleBaseFragment;

import java.text.DecimalFormat;
import java.util.Collections;
import java.util.Random;

import butterknife.Bind;

public class HeatmapWithTextFragment extends ExampleBaseFragment {
    @Bind(R.id.chart)
    SciChartSurface chart;

    @Bind(R.id.heatmapColourMap)
    SciChartHeatmapColourMap colourMap;

    @Override
    protected int getLayoutId() {
        return R.layout.example_heatmap_with_text_fragment;
    }

    @Override
    protected void initExample() {
        final NumericAxis xAxis = sciChartBuilder.newNumericAxis()
                .withGrowBy(0.1, 0.1)
                .withFlipCoordinates(true)
                .build();

        final NumericAxis yAxis = sciChartBuilder.newNumericAxis()
                .withGrowBy(0.1, 0.1)
                .withFlipCoordinates(true)
                .build();


        final FastUniformHeatmapRenderableSeries heatmapRenderableSeries = sciChartBuilder.newUniformHeatmap()
                .withMinimum(0)
                .withMaximum(100)
                .withCellTextStyle(sciChartBuilder.newFont().withTextSize(8).withTextColor(ColorUtil.White).build())
                .withDrawTextInCell(true)
                .withDataSeries(createDataSeries())
                .build();

        colourMap.setMinimum(heatmapRenderableSeries.getMinimum());
        colourMap.setMaximum(heatmapRenderableSeries.getMaximum());
        colourMap.setColorMap(heatmapRenderableSeries.getColorMap());
        colourMap.setTextFormat(new DecimalFormat("0.##"));

        Collections.addAll(chart.getXAxes(), xAxis);
        Collections.addAll(chart.getYAxes(), yAxis);
        Collections.addAll(chart.getRenderableSeries(), heatmapRenderableSeries);
        Collections.addAll(chart.getChartModifiers(), sciChartBuilder.newModifierGroupWithDefaultModifiers().build());
    }

    private IDataSeries createDataSeries() {
        final int w = 12;
        final int h = 7;

        final UniformHeatmapDataSeries<Integer, Integer, Double> dataSeries = new UniformHeatmapDataSeries<>(Integer.class, Integer.class, Double.class, w, h);

        final Random random = new Random();
        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                dataSeries.updateZAt(x, y, Math.pow(random.nextDouble(), 0.15) * x / (w - 1) * y / (h - 1) * 100);
            }
        }

        return dataSeries;
    }
}
