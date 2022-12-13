//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2021. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// HeatmapChartFragment.java is part of SCICHART®, High Performance Scientific Charts
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

package com.scichart.examples.fragments.examples2d.basicChartTypes;

import android.view.LayoutInflater;

import androidx.annotation.NonNull;

import com.scichart.charting.model.dataSeries.UniformHeatmapDataSeries;
import com.scichart.charting.modifiers.ModifierGroup;
import com.scichart.charting.visuals.SciChartHeatmapColourMap;
import com.scichart.charting.visuals.SciChartSurface;
import com.scichart.charting.visuals.axes.NumericAxis;
import com.scichart.charting.visuals.renderableSeries.ColorMap;
import com.scichart.charting.visuals.renderableSeries.FastUniformHeatmapRenderableSeries;
import com.scichart.core.framework.UpdateSuspender;
import com.scichart.core.model.DoubleValues;
import com.scichart.core.model.IValues;
import com.scichart.examples.databinding.ExampleHeatmapChartFragmentBinding;
import com.scichart.examples.fragments.base.ExampleBaseFragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import static com.scichart.drawing.utility.ColorUtil.*;

public class HeatmapChartFragment extends ExampleBaseFragment<ExampleHeatmapChartFragmentBinding> {
    private static final int WIDTH = 300, HEIGHT = 200;
    private static final int SERIES_PER_PERIOD = 30;
    private static final long TIME_INTERVAL = 40;

    private final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
    private ScheduledFuture<?> schedule;

    private final UniformHeatmapDataSeries<Integer, Integer, Double> dataSeries = new UniformHeatmapDataSeries<>(Integer.class, Integer.class, Double.class, WIDTH, HEIGHT);

    private int timerIndex = 0;
    private final List<IValues<Double>> valuesList = new ArrayList<>(SERIES_PER_PERIOD);

    @NonNull
    @Override
    protected ExampleHeatmapChartFragmentBinding inflateBinding(@NonNull LayoutInflater inflater) {
        return ExampleHeatmapChartFragmentBinding.inflate(inflater);
    }

    @Override
    protected void initExample(@NonNull ExampleHeatmapChartFragmentBinding binding) {
        final NumericAxis xAxis = sciChartBuilder.newNumericAxis().build();
        final NumericAxis yAxis = sciChartBuilder.newNumericAxis().build();

        for (int i = 0; i < SERIES_PER_PERIOD; i++) {
            valuesList.add(createValues(i));
        }

        final FastUniformHeatmapRenderableSeries heatmapRenderableSeries = sciChartBuilder.newUniformHeatmap()
                .withColorMap(new ColorMap(
                        new int[]{0xFF14233C, 0xFF264B93, 0xFF50C7E0, 0xFF67BDAF, 0xFFDC7969, 0xFFF48420, 0xFFEC0F6C},
                        new float[]{0f, 0.2f, 0.3f, 0.5f, 0.7f, 0.9f, 1})
                )
                .withMinimum(0)
                .withMaximum(200)
                .withDataSeries(dataSeries)
                .build();

        final SciChartHeatmapColourMap colourMap = binding.heatmapColourMap;
        colourMap.setMinimum(heatmapRenderableSeries.getMinimum());
        colourMap.setMaximum(heatmapRenderableSeries.getMaximum());
        colourMap.setColorMap(heatmapRenderableSeries.getColorMap());

        final SciChartSurface surface = binding.surface;
        Collections.addAll(surface.getXAxes(), xAxis);
        Collections.addAll(surface.getYAxes(), yAxis);
        Collections.addAll(surface.getRenderableSeries(), heatmapRenderableSeries);

        ModifierGroup modifiers = sciChartBuilder.newModifierGroupWithDefaultModifiers()
                .withCursorModifier().withShowTooltip(true).withReceiveHandledEvents(true).build()
                .build();

        Collections.addAll(surface.getChartModifiers(), modifiers);

        schedule = scheduledExecutorService.scheduleWithFixedDelay(() -> UpdateSuspender.using(surface, () -> {
            final IValues<Double> values = valuesList.get(timerIndex % SERIES_PER_PERIOD);
            dataSeries.updateZValues(values);

            timerIndex++;
        }), 0, TIME_INTERVAL, TimeUnit.MILLISECONDS);
    }

    private static IValues<Double> createValues(int index) {
        final DoubleValues values = new DoubleValues(WIDTH * HEIGHT);

        final Random random = new Random();
        final double angle = Math.PI * 2 * index / SERIES_PER_PERIOD;
        final double cx = 150, cy = 100;
        final double cpMax = 200;
        // When appending data to IValues<Double> for the heatmap, always go Y then X
        for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {
                final double v = (1 + Math.sin(x * 0.04 + angle)) * 50 + (1 + Math.sin(y * 0.1 + angle)) * 50 * (1 + Math.sin(angle * 2));
                final double r = Math.sqrt((x - cx) * (x - cx) + (y - cy) * (y - cy));
                final double exp = Math.max(0, 1 - r * 0.008);
                double zValue = v * exp + Math.random() * 10;
                values.add(zValue > cpMax ? cpMax : zValue);
            }
        }

        return values;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        schedule.cancel(true);
    }
}
