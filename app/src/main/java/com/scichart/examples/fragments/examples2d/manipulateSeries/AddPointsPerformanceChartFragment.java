//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2021. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// AddPointsPerformanceChartFragment.java is part of SCICHART®, High Performance Scientific Charts
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

import com.scichart.charting.model.dataSeries.IXyDataSeries;
import com.scichart.charting.model.dataSeries.XyDataSeries;
import com.scichart.charting.visuals.SciChartSurface;
import com.scichart.charting.visuals.axes.NumericAxis;
import com.scichart.charting.visuals.renderableSeries.IRenderableSeries;
import com.scichart.core.framework.UpdateSuspender;
import com.scichart.data.model.ISciList;
import com.scichart.drawing.utility.ColorUtil;
import com.scichart.examples.data.DoubleSeries;
import com.scichart.examples.data.RandomWalkGenerator;
import com.scichart.examples.databinding.ExampleAddPointsPerformanceFragmentBinding;
import com.scichart.examples.fragments.base.ExampleBaseFragment;

import java.util.Random;

public class AddPointsPerformanceChartFragment extends ExampleBaseFragment<ExampleAddPointsPerformanceFragmentBinding>{

    private final Random random = new Random();

    @NonNull
    @Override
    protected ExampleAddPointsPerformanceFragmentBinding inflateBinding(@NonNull LayoutInflater inflater) {
        return ExampleAddPointsPerformanceFragmentBinding.inflate(inflater);
    }

    @Override
    protected void initExample(ExampleAddPointsPerformanceFragmentBinding binding) {
        binding.append10k.setOnClickListener(v -> onAppendPoints(10_000));
        binding.append100k.setOnClickListener(v -> onAppendPoints(100_00));
        binding.appendMLN.setOnClickListener(v -> onAppendPoints(1_000_000));
        binding.reset.setOnClickListener(v -> onReset());

        final SciChartSurface surface = binding.surface;

        final NumericAxis xAxis = sciChartBuilder.newNumericAxis().withVisibleRange(0, 10).withAxisTitle("X Axis").build();
        final NumericAxis yAxis = sciChartBuilder.newNumericAxis().withVisibleRange(0, 10).withAxisTitle("Y Axis").build();

        surface.getXAxes().add(xAxis);
        surface.getYAxes().add(yAxis);

        surface.getChartModifiers().add(sciChartBuilder.newModifierGroupWithDefaultModifiers().build());
    }

    private void onAppendPoints(int count) {
        final SciChartSurface surface = binding.surface;
        final Random biasRandom = new Random();

        final DoubleSeries randomWalkSeries = new RandomWalkGenerator(100).setBias(biasRandom.nextDouble() / 100).getRandomWalkSeries(count);
        final IXyDataSeries<Double, Double> dataSeries = new XyDataSeries<>(Double.class, Double.class);
        dataSeries.append(randomWalkSeries.xValues, randomWalkSeries.yValues);

        surface.getRenderableSeries().add(sciChartBuilder.newLineSeries()
                .withStrokeStyle(ColorUtil.rgb(random.nextInt(255), random.nextInt(255), random.nextInt(255)))
                .withDataSeries(dataSeries)
                .build()
        );

        surface.animateZoomExtents(250);
    }

    private void onReset() {
        final SciChartSurface surface = binding.surface;
        UpdateSuspender.using(surface, () -> {
            for (int i = 0; i < surface.getRenderableSeries().size(); i++) {
                surface.getRenderableSeries().get(i).getDataSeries().clear();
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
                final IXyDataSeries<?, ?> series = (IXyDataSeries<?, ?>) renderableSeries.getDataSeries();
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
                final ISciList<Double> xValues = savedInstanceState.getParcelable("xValues" + i);
                final ISciList<Double> yValues = savedInstanceState.getParcelable("yValues" + i);
                final IXyDataSeries<Double, Double> dataSeries = new XyDataSeries<>(Double.class, Double.class);
                dataSeries.append(xValues, yValues);

                binding.surface.getRenderableSeries().add(sciChartBuilder.newLineSeries().withStrokeStyle(seriesColor).withDataSeries(dataSeries).build());
            }
        }
    }
}
