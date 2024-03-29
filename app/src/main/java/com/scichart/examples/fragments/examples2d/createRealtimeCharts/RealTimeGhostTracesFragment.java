//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2021. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// RealTimeGhostTracesFragment.java is part of SCICHART®, High Performance Scientific Charts
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

package com.scichart.examples.fragments.examples2d.createRealtimeCharts;


import android.view.LayoutInflater;
import android.widget.SeekBar;

import androidx.annotation.NonNull;

import com.scichart.charting.model.RenderableSeriesCollection;
import com.scichart.charting.model.dataSeries.XyDataSeries;
import com.scichart.charting.visuals.SciChartSurface;
import com.scichart.charting.visuals.axes.AutoRange;
import com.scichart.charting.visuals.axes.NumericAxis;
import com.scichart.core.framework.UpdateSuspender;
import com.scichart.core.utility.NumberUtil;
import com.scichart.data.model.DoubleRange;
import com.scichart.drawing.utility.ColorUtil;
import com.scichart.examples.R;
import com.scichart.examples.data.DataManager;
import com.scichart.examples.data.DoubleSeries;
import com.scichart.examples.databinding.ExampleRealTimeGhostTracesFragmentBinding;
import com.scichart.examples.fragments.base.ExampleBaseFragment;

import java.util.Collections;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class RealTimeGhostTracesFragment extends ExampleBaseFragment<ExampleRealTimeGhostTracesFragmentBinding> implements SeekBar.OnSeekBarChangeListener {
    private final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
    private ScheduledFuture<?> schedule;

    @Override
    public boolean showDefaultModifiersInToolbar() {
        return false;
    }

    @NonNull
    @Override
    protected ExampleRealTimeGhostTracesFragmentBinding inflateBinding(@NonNull LayoutInflater inflater) {
        return ExampleRealTimeGhostTracesFragmentBinding.inflate(inflater);
    }

    @Override
    protected void initExample(ExampleRealTimeGhostTracesFragmentBinding binding) {
        binding.surface.setTheme(R.style.SciChart_NavyBlue);

        final NumericAxis xAxis = sciChartBuilder.newNumericAxis().withAutoRangeMode(AutoRange.Always) .build();
        final NumericAxis yAxis = sciChartBuilder.newNumericAxis()
                .withGrowBy(new DoubleRange(0.1d, 0.1d))
                .withAutoRangeMode(AutoRange.Never)
                .withVisibleRange(new DoubleRange(-2d, 2d))
                .build();

        final SciChartSurface surface = binding.surface;
        Collections.addAll(surface.getXAxes(), xAxis);
        Collections.addAll(surface.getYAxes(), yAxis);

        final int seriesColor = 0xFF68bcae;
        Collections.addAll(surface.getRenderableSeries(),
                sciChartBuilder.newLineSeries().withStrokeStyle(seriesColor).build(),
                sciChartBuilder.newLineSeries().withStrokeStyle(ColorUtil.argb(seriesColor, 0.9f)).build(),
                sciChartBuilder.newLineSeries().withStrokeStyle(ColorUtil.argb(seriesColor, 0.8f)).build(),
                sciChartBuilder.newLineSeries().withStrokeStyle(ColorUtil.argb(seriesColor, 0.7f)).build(),
                sciChartBuilder.newLineSeries().withStrokeStyle(ColorUtil.argb(seriesColor, 0.62f)).build(),
                sciChartBuilder.newLineSeries().withStrokeStyle(ColorUtil.argb(seriesColor, 0.55f)).build(),
                sciChartBuilder.newLineSeries().withStrokeStyle(ColorUtil.argb(seriesColor, 0.45f)).build(),
                sciChartBuilder.newLineSeries().withStrokeStyle(ColorUtil.argb(seriesColor, 0.35f)).build(),
                sciChartBuilder.newLineSeries().withStrokeStyle(ColorUtil.argb(seriesColor, 0.25f)).build(),
                sciChartBuilder.newLineSeries().withStrokeStyle(ColorUtil.argb(seriesColor, 0.15f)).build()
        );

        final SeekBar seekBar = binding.seekBar;
        seekBar.setOnSeekBarChangeListener(this);
        onProgressChanged(seekBar, seekBar.getProgress(), false);
    }


    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (progress > 0) {
            binding.speedValue.setText(String.format("%d ms", progress));

            if (schedule != null) {
                schedule.cancel(true);
            }

            schedule = scheduledExecutorService.scheduleWithFixedDelay(scheduledRunnable, 0, progress, TimeUnit.MILLISECONDS);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) { }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) { }

    private final Runnable scheduledRunnable = new Runnable() {
        private double lastAmplitude = 1.0;
        private double phase = 0;
        private final Random random = new Random();

        @Override
        public void run() {
            final SciChartSurface surface = binding.surface;
            UpdateSuspender.using(surface, () -> {
                final XyDataSeries<Double, Double> dataSeries = new XyDataSeries<>(Double.class, Double.class);

                final double randomAmplitude = NumberUtil.constrain(lastAmplitude + (random.nextDouble() - 0.5), -2d, 2);
                final DoubleSeries noisySinewave = DataManager.getInstance().getNoisySinewave(randomAmplitude, phase, 1000, 0.25);
                lastAmplitude = randomAmplitude;

                dataSeries.append(noisySinewave.xValues, noisySinewave.yValues);

                reassignRenderableSeries(surface, dataSeries);
            });
        }
    };

    private void reassignRenderableSeries(final SciChartSurface surface, final XyDataSeries<Double, Double> dataSeries) {
        UpdateSuspender.using(surface, () -> {
            final RenderableSeriesCollection rs = surface.getRenderableSeries();

            // shift old data series
            rs.get(9).setDataSeries(rs.get(8).getDataSeries());
            rs.get(8).setDataSeries(rs.get(7).getDataSeries());
            rs.get(7).setDataSeries(rs.get(6).getDataSeries());
            rs.get(6).setDataSeries(rs.get(5).getDataSeries());
            rs.get(5).setDataSeries(rs.get(4).getDataSeries());
            rs.get(4).setDataSeries(rs.get(3).getDataSeries());
            rs.get(3).setDataSeries(rs.get(2).getDataSeries());
            rs.get(2).setDataSeries(rs.get(1).getDataSeries());
            rs.get(1).setDataSeries(rs.get(0).getDataSeries());

            // use new data series to draw first renderable series
            rs.get(0).setDataSeries(dataSeries);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if (schedule != null) {
            schedule.cancel(true);
        }
    }
}
