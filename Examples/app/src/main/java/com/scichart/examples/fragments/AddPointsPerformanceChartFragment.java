//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2017. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// AddPointsPerformanceChartFragment.java is part of the SCICHART® Examples. Permission is hereby granted
// to modify, create derivative works, distribute and publish any part of this source
// code whether for commercial, private or personal use.
//
// The SCICHART® examples are distributed in the hope that they will be useful, but
// without any warranty. It is provided "AS IS" without warranty of any kind, either
// expressed or implied.
//******************************************************************************

package com.scichart.examples.fragments;

import android.os.Bundle;

import com.scichart.charting.model.dataSeries.IXyDataSeries;
import com.scichart.charting.model.dataSeries.XyDataSeries;
import com.scichart.charting.visuals.SciChartSurface;
import com.scichart.charting.visuals.axes.NumericAxis;
import com.scichart.charting.visuals.renderableSeries.FastLineRenderableSeries;
import com.scichart.charting.visuals.renderableSeries.IRenderableSeries;
import com.scichart.core.framework.UpdateSuspender;
import com.scichart.data.model.ISciList;
import com.scichart.drawing.utility.ColorUtil;
import com.scichart.examples.R;
import com.scichart.examples.data.DoubleSeries;
import com.scichart.examples.data.RandomWalkGenerator;
import com.scichart.examples.fragments.base.ExampleBaseFragment;

import java.util.Random;

import butterknife.Bind;
import butterknife.OnClick;

public class AddPointsPerformanceChartFragment extends ExampleBaseFragment {

    @Bind(R.id.chart)
    SciChartSurface surface;

    private Random random = new Random();

    @Override
    protected int getLayoutId() {
        return R.layout.example_add_points_performance_fragment;
    }

    @Override
    protected void initExample() {
        final NumericAxis xAxis = sciChartBuilder.newNumericAxis().withVisibleRange(0, 10).withAxisTitle("X Axis").build();
        final NumericAxis yAxis = sciChartBuilder.newNumericAxis().withVisibleRange(0, 10).withAxisTitle("Y Axis").build();

        surface.getXAxes().add(xAxis);
        surface.getYAxes().add(yAxis);

        surface.getChartModifiers().add(sciChartBuilder.newModifierGroupWithDefaultModifiers().build());
    }

    @Override
    public void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);
        UpdateSuspender.using(surface, new Runnable() {
            @Override
            public void run() {
                final int size = surface.getRenderableSeries().size();
                outState.putInt("seriesCount", size);
                for (int i = 0; i < size; i++) {
                    final IRenderableSeries renderableSeries = surface.getRenderableSeries().get(i);
                    IXyDataSeries<Double, Double> series = (IXyDataSeries<Double, Double>) renderableSeries.getDataSeries();
                    outState.putInt("seriesColor" + i, renderableSeries.getStrokeStyle().getColor());
                    outState.putParcelable("xValues" + i, series.getXValues());
                    outState.putParcelable("yValues" + i, series.getYValues());
                }
            }
        });
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            final int seriesCount = savedInstanceState.getInt("seriesCount");
            for (int i = 0; i < seriesCount; i++) {
                final int seriesColor = savedInstanceState.getInt("seriesColor" + i);
                final ISciList<Double> xValues = savedInstanceState.getParcelable("xValues" + i);
                final ISciList<Double> yValues = savedInstanceState.getParcelable("yValues" + i);
                final IXyDataSeries<Double, Double> dataSeries = new XyDataSeries<>(Double.class, Double.class);
                final FastLineRenderableSeries renderableSeries = sciChartBuilder.newLineSeries()
                        .withStrokeStyle(seriesColor)
                        .withDataSeries(dataSeries).build();
                dataSeries.append(xValues, yValues);
                surface.getRenderableSeries().add(renderableSeries);
            }
        }
    }

    private void onAppendPoints(int count) {
        IXyDataSeries<Double, Double> dataSeries = new XyDataSeries<>(Double.class, Double.class);

        final FastLineRenderableSeries renderableSeries = sciChartBuilder.newLineSeries()
                .withStrokeStyle(ColorUtil.rgb(random.nextInt(255), random.nextInt(255), random.nextInt(255)))
                .withDataSeries(dataSeries).build();

        Random biasRandom = new Random();
        DoubleSeries randomWalkSeries = new RandomWalkGenerator(100)
                .setBias(biasRandom.nextDouble() / 100).getRandomWalkSeries(count);
        dataSeries.append(randomWalkSeries.xValues, randomWalkSeries.yValues);

        surface.getRenderableSeries().add(renderableSeries);

        surface.animateZoomExtents(500);
    }

    @OnClick(R.id.append10k)
    void onAppend10KPoints() {
        onAppendPoints(10_000);
    }

    @OnClick(R.id.append100k)
    void onAppend100KPoints() {
        onAppendPoints(100_000);
    }

    @OnClick(R.id.appendMLN)
    void onAppendMLNPoints() {
        onAppendPoints(1_000_000);
    }

    @OnClick(R.id.reset)
    void onReset() {
        UpdateSuspender.using(surface, new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < surface.getRenderableSeries().size(); i++) {
                    surface.getRenderableSeries().get(i).getDataSeries().clear();
                }
            }
        });
    }
}
