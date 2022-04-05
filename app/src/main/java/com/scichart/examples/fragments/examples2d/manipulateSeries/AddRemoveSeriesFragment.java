//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2021. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// AddRemoveSeriesFragment.java is part of SCICHART®, High Performance Scientific Charts
// For full terms and conditions of the license, see http://www.scichart.com/scichart-eula/
//
// This source code is protected by international copyright law. Unauthorized
// reproduction, reverse-engineering, or distribution of all or any portion of
// this source code is strictly prohibited.
//
// This source code contains confidential and proprietary trade secrets of
// SciChart Ltd., and should at no time be copied, transferred, sold,
// distributed or made available without express written permission.
//******************************************************************************

package com.scichart.examples.fragments.examples2d.manipulateSeries;

import android.os.Bundle;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;

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
import com.scichart.examples.data.DataManager;
import com.scichart.examples.data.DoubleSeries;
import com.scichart.examples.databinding.ExampleAddRemoveSeriesFragmentBinding;
import com.scichart.examples.fragments.base.ExampleBaseFragment;

import java.util.Random;

public class AddRemoveSeriesFragment extends ExampleBaseFragment<ExampleAddRemoveSeriesFragmentBinding> {
    @NonNull
    @Override
    protected ExampleAddRemoveSeriesFragmentBinding inflateBinding(@NonNull LayoutInflater inflater) {
        return ExampleAddRemoveSeriesFragmentBinding.inflate(inflater);
    }

    @Override
    protected void initExample(ExampleAddRemoveSeriesFragmentBinding binding) {
        final NumericAxis xAxis = sciChartBuilder.newNumericAxis().withAutoRangeMode(AutoRange.Always).withAxisTitle("X Axis").build();
        final NumericAxis yAxis = sciChartBuilder.newNumericAxis().withAutoRangeMode(AutoRange.Always).withAxisTitle("Y Axis").build();

        final SciChartSurface surface = binding.surface;
        UpdateSuspender.using(surface, () -> {
            surface.getXAxes().add(xAxis);
            surface.getYAxes().add(yAxis);
            surface.getChartModifiers().add(sciChartBuilder.newModifierGroupWithDefaultModifiers().build());
        });

        binding.addSeries.setOnClickListener(v -> add());
        binding.removeSeries.setOnClickListener(v -> remove());
        binding.reset.setOnClickListener(v -> binding.surface.getRenderableSeries().clear());
    }

    private void add() {
        final SciChartSurface surface = binding.surface;
        UpdateSuspender.using(surface, () -> {
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
        });
    }

    private void remove() {
        final SciChartSurface surface = binding.surface;
        UpdateSuspender.using(surface, () -> {
            final RenderableSeriesCollection renderableSeries = surface.getRenderableSeries();
            if (!renderableSeries.isEmpty()) {
                renderableSeries.remove(0);
            }
        });
    }

    @Override
    public void onSaveInstanceState(@NonNull final Bundle outState) {
        super.onSaveInstanceState(outState);

        final SciChartSurface surface = binding.surface;
        UpdateSuspender.using(surface, () -> {
            final int size = surface.getRenderableSeries().size();
            outState.putInt("seriesCount", size);
            for (int i = 0; i < size; i++) {
                final IRenderableSeries renderableSeries = surface.getRenderableSeries().get(i);
                IXyDataSeries<?, ?> series = (IXyDataSeries<?, ?>) renderableSeries.getDataSeries();
                if (renderableSeries instanceof BaseMountainRenderableSeries) {
                    outState.putInt("areaColor" + i, ((BaseMountainRenderableSeries) renderableSeries).getAreaStyle().hashCode());
                }
                outState.putInt("seriesColor" + i, renderableSeries.getStrokeStyle().getColor());
                outState.putParcelable("xValues" + i, series.getXValues());
                outState.putParcelable("yValues" + i, series.getYValues());
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

                binding.surface.getRenderableSeries().add(renderableSeries);
            }
        }
    }
}