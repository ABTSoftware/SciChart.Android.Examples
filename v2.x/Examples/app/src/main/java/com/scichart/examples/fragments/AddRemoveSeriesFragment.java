//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2017. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// AddRemoveSeriesFragment.java is part of the SCICHART® Examples. Permission is hereby granted
// to modify, create derivative works, distribute and publish any part of this source
// code whether for commercial, private or personal use.
//
// The SCICHART® examples are distributed in the hope that they will be useful, but
// without any warranty. It is provided "AS IS" without warranty of any kind, either
// expressed or implied.
//******************************************************************************

package com.scichart.examples.fragments;

import android.os.Bundle;

import com.scichart.charting.model.RenderableSeriesCollection;
import com.scichart.charting.model.dataSeries.IXyDataSeries;
import com.scichart.charting.model.dataSeries.XyDataSeries;
import com.scichart.charting.visuals.SciChartSurface;
import com.scichart.charting.visuals.axes.AutoRange;
import com.scichart.charting.visuals.axes.NumericAxis;
import com.scichart.charting.visuals.renderableSeries.BaseMountainRenderableSeries;
import com.scichart.charting.visuals.renderableSeries.FastMountainRenderableSeries;
import com.scichart.charting.visuals.renderableSeries.IRenderableSeries;
import com.scichart.core.framework.UpdateSuspender;
import com.scichart.data.model.ISciList;
import com.scichart.drawing.utility.ColorUtil;
import com.scichart.examples.R;
import com.scichart.examples.data.DataManager;
import com.scichart.examples.data.DoubleSeries;
import com.scichart.examples.fragments.base.ExampleBaseFragment;

import java.util.Random;

import butterknife.Bind;
import butterknife.OnClick;

public class AddRemoveSeriesFragment extends ExampleBaseFragment {

    @Bind(R.id.chart)
    SciChartSurface surface;

    @Override
    protected int getLayoutId() {
        return R.layout.example_add_remove_series_fragment;
    }

    @Override
    protected void initExample() {
        final NumericAxis xAxis = sciChartBuilder.newNumericAxis().withAutoRangeMode(AutoRange.Always).withDrawMajorBands(true).withVisibleRange(0, 150).withAxisTitle("X Axis").build();
        final NumericAxis yAxis = sciChartBuilder.newNumericAxis().withAutoRangeMode(AutoRange.Always).withDrawMajorBands(true).withVisibleRange(-1.5, -1.5).withAxisTitle("Y Axis").build();

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
                    if (renderableSeries instanceof BaseMountainRenderableSeries) {
                        outState.putInt("areaColor" + i, ((BaseMountainRenderableSeries) renderableSeries).getAreaStyle().hashCode());
                    }
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
                final int areaColor = savedInstanceState.getInt("areaColor" + i);
                final ISciList<Double> xValues = savedInstanceState.getParcelable("xValues" + i);
                final ISciList<Double> yValues = savedInstanceState.getParcelable("yValues" + i);

                final IXyDataSeries<Double, Double> series = new XyDataSeries<>(Double.class, Double.class);
                series.append(xValues, yValues);

                final FastMountainRenderableSeries renderableSeries = sciChartBuilder.newMountainSeries()
                        .withDataSeries(series)
                        .withAreaFillColor(areaColor)
                        .withStrokeStyle(seriesColor)
                        .build();

                surface.getRenderableSeries().add(renderableSeries);
            }
        }
    }

    @OnClick(R.id.addSeries)
    void onAddSeries() {
        UpdateSuspender.using(surface, new Runnable() {
            @Override
            public void run() {
                final Random random = new Random();
                final DoubleSeries randomDoubleSeries = DataManager.getInstance().getRandomDoubleSeries(150);

                final IXyDataSeries<Double, Double> series = new XyDataSeries<>(Double.class, Double.class);
                series.append(randomDoubleSeries.xValues, randomDoubleSeries.yValues);

                final FastMountainRenderableSeries renderableSeries = sciChartBuilder.newMountainSeries()
                        .withDataSeries(series)
                        .withAreaFillColor(ColorUtil.argb(random.nextInt(255), random.nextInt(255), random.nextInt(255), 150))
                        .withStrokeStyle(ColorUtil.rgb(random.nextInt(255), random.nextInt(255), random.nextInt(255)))
                        .build();

                surface.getRenderableSeries().add(renderableSeries);
            }
        });
    }

    @OnClick(R.id.removeSeries)
    void onRemoveSeries() {
        UpdateSuspender.using(surface, new Runnable() {
            @Override
            public void run() {
                final RenderableSeriesCollection renderableSeries = surface.getRenderableSeries();
                if (!renderableSeries.isEmpty()) {
                    renderableSeries.remove(0);
                }
            }
        });
    }

    @OnClick(R.id.reset)
    void onReset() {
        UpdateSuspender.using(surface, new Runnable() {
            @Override
            public void run() {
                surface.getRenderableSeries().clear();
            }
        });
    }
}