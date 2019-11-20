//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2017. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// RealTimeGhostTracesFragment.java is part of the SCICHART® Examples. Permission is hereby granted
// to modify, create derivative works, distribute and publish any part of this source
// code whether for commercial, private or personal use.
//
// The SCICHART® examples are distributed in the hope that they will be useful, but
// without any warranty. It is provided "AS IS" without warranty of any kind, either
// expressed or implied.
//******************************************************************************

package com.scichart.examples.fragments;


import android.widget.SeekBar;
import android.widget.TextView;

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
import com.scichart.examples.fragments.base.ExampleBaseFragment;

import java.util.Collections;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;

public class RealTimeGhostTracesFragment extends ExampleBaseFragment implements SeekBar.OnSeekBarChangeListener{

    @BindView(R.id.seekBar)
    SeekBar seekBar;

    @BindView(R.id.speedValue)
    TextView speedValue;

    @BindView(R.id.chart)
    SciChartSurface surface;

    private final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
    private ScheduledFuture<?> schedule;

    @Override
    public boolean showDefaultModifiersInToolbar() {
        return false;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.example_real_time_ghost_traces_fragment;
    }

    @Override
    protected void initExample() {
        seekBar.setOnSeekBarChangeListener(this);

        final NumericAxis xAxis = sciChartBuilder.newNumericAxis()
                .withAutoRangeMode(AutoRange.Always)
                .build();

        final NumericAxis yAxis = sciChartBuilder.newNumericAxis()
                .withGrowBy(new DoubleRange(0.1d, 0.1d))
                .withAutoRangeMode(AutoRange.Never)
                .withVisibleRange(new DoubleRange(-2d, 2d))
                .build();

        Collections.addAll(surface.getXAxes(), xAxis);
        Collections.addAll(surface.getYAxes(), yAxis);

        final int seriesColor = ColorUtil.LimeGreen;

        Collections.addAll(surface.getRenderableSeries(),
                sciChartBuilder.newLineSeries()
                        .withStrokeStyle(seriesColor)
                        .build(),
                sciChartBuilder.newLineSeries()
                        .withStrokeStyle(ColorUtil.argb(seriesColor, 0.9f))
                        .build(),
                sciChartBuilder.newLineSeries()
                        .withStrokeStyle(ColorUtil.argb(seriesColor, 0.8f))
                        .build(),
                sciChartBuilder.newLineSeries()
                        .withStrokeStyle(ColorUtil.argb(seriesColor, 0.7f))
                        .build(),
                sciChartBuilder.newLineSeries()
                        .withStrokeStyle(ColorUtil.argb(seriesColor, 0.62f))
                        .build(),
                sciChartBuilder.newLineSeries()
                        .withStrokeStyle(ColorUtil.argb(seriesColor, 0.55f))
                        .build(),
                sciChartBuilder.newLineSeries()
                        .withStrokeStyle(ColorUtil.argb(seriesColor, 0.45f))
                        .build(),
                sciChartBuilder.newLineSeries()
                        .withStrokeStyle(ColorUtil.argb(seriesColor, 0.35f))
                        .build(),
                sciChartBuilder.newLineSeries()
                        .withStrokeStyle(ColorUtil.argb(seriesColor, 0.25f))
                        .build(),
                sciChartBuilder.newLineSeries()
                        .withStrokeStyle(ColorUtil.argb(seriesColor, 0.15f))
                        .build());

        onProgressChanged(seekBar, seekBar.getProgress(), false);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if (schedule != null)
            schedule.cancel(true);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (progress > 0) {
            speedValue.setText(String.format("%d ms", progress));

            if (schedule != null)
                schedule.cancel(true);

            schedule = scheduledExecutorService.scheduleWithFixedDelay(scheduledRunnable, 0, progress, TimeUnit.MILLISECONDS);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
    }

    private final Runnable scheduledRunnable = new Runnable() {
        private double lastAmplitude = 1.0;
        private double phase = 0;
        private Random random = new Random();

        @Override
        public void run() {
            UpdateSuspender.using(surface, new Runnable() {
                @Override
                public void run() {
                    final XyDataSeries<Double, Double> dataSeries = new XyDataSeries<>(Double.class, Double.class);

                    final double randomAmplitude = NumberUtil.constrain(lastAmplitude + (random.nextDouble() - 0.5), -2d, 2);
                    final DoubleSeries noisySinewave = DataManager.getInstance().getNoisySinewave(randomAmplitude, phase, 1000, 0.25);
                    lastAmplitude = randomAmplitude;

                    dataSeries.append(noisySinewave.xValues, noisySinewave.yValues);

                    reassignRenderableSeries(dataSeries);
                }
            });
        }
    };

    private void reassignRenderableSeries(final XyDataSeries<Double, Double> dataSeries) {
        UpdateSuspender.using(surface, new Runnable() {
            @Override
            public void run() {
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
            }
        });
    }
}
