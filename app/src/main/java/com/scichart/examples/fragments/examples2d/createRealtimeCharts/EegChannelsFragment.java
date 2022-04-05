//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2021. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// EegChannelsFragment.java is part of SCICHART®, High Performance Scientific Charts
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

import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.scichart.charting.model.dataSeries.IXyDataSeries;
import com.scichart.charting.visuals.SciChartSurface;
import com.scichart.charting.visuals.axes.AutoRange;
import com.scichart.charting.visuals.axes.NumericAxis;
import com.scichart.charting.visuals.renderableSeries.IRenderableSeries;
import com.scichart.core.model.DoubleValues;
import com.scichart.data.model.DoubleRange;
import com.scichart.drawing.common.PenStyle;
import com.scichart.drawing.utility.ColorUtil;
import com.scichart.examples.R;
import com.scichart.examples.databinding.ExampleEegChannelViewBinding;
import com.scichart.examples.databinding.ExampleEegChannelsFragmentBinding;
import com.scichart.examples.fragments.base.ExampleBaseFragment;
import com.scichart.examples.utils.widgetgeneration.ImageViewWidget;
import com.scichart.examples.utils.widgetgeneration.Widget;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class EegChannelsFragment extends ExampleBaseFragment<ExampleEegChannelsFragmentBinding> {
    private static final int CHANNELS_COUNT = 8;
    private static final int SIZE = 1000;
    private static final long TIME_INTERVAL = 10;
    private static final int BUFFER_SIZE = 10;

    private final EegChannelModel[] dataSet = new EegChannelModel[CHANNELS_COUNT];
    private final int[] colors = new int[]
            {
                    ColorUtil.White, ColorUtil.Yellow, ColorUtil.argb(255, 0, 128, 128), ColorUtil.argb(255, 176, 196, 222),
                    ColorUtil.argb(255, 255, 182, 193), ColorUtil.Purple, ColorUtil.argb(255, 245, 222, 179), ColorUtil.argb(255, 173, 216, 230),
                    ColorUtil.argb(255, 250, 128, 114), ColorUtil.argb(255, 144, 238, 144), ColorUtil.Orange, ColorUtil.argb(255, 192, 192, 192),
                    ColorUtil.argb(255, 255, 99, 71), ColorUtil.argb(255, 205, 133, 63), ColorUtil.argb(255, 64, 224, 208), ColorUtil.argb(255, 244, 164, 96)
            };

    private volatile boolean isRunning = true;

    private final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
    private ScheduledFuture<?> schedule;

    @NonNull
    @Override
    public List<Widget> getToolbarItems() {
        return new ArrayList<Widget>() {
            {
                add(new ImageViewWidget.Builder().setId(R.drawable.example_toolbar_play).setListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        isRunning = true;
                    }
                }).build());
                add(new ImageViewWidget.Builder().setId(R.drawable.example_toolbar_pause).setListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        isRunning = false;
                    }
                }).build());
                add(new ImageViewWidget.Builder().setId(R.drawable.example_toolbar_stop).setListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        isRunning = false;
                        resetChart();
                    }
                }).build());
            }
        };
    }

    @NonNull
    @Override
    protected ExampleEegChannelsFragmentBinding inflateBinding(@NonNull LayoutInflater inflater) {
        return ExampleEegChannelsFragmentBinding.inflate(inflater);
    }

    @Override
    protected void initExample(ExampleEegChannelsFragmentBinding binding) {
        final RecyclerView recyclerView = binding.recyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        for (int i = 0; i < CHANNELS_COUNT; i++) {
            final EegChannelModel eegChannelModel = new EegChannelModel(SIZE, sciChartBuilder
                    .newPen().withColor(colors[i]).withThickness(2f).build(), String.format("Ch %d", i));
            dataSet[i] = eegChannelModel;
        }

        EegChannelsAdapter adapter = new EegChannelsAdapter(dataSet);
        recyclerView.setAdapter(adapter);

        schedule = scheduledExecutorService.scheduleAtFixedRate(runnable, 0, TIME_INTERVAL, TimeUnit.MILLISECONDS);
    }

    private final Runnable runnable = new Runnable() {
        private Random random = new Random();
        private double currentSize = 0;

        private final DoubleValues xBuffer = new DoubleValues(BUFFER_SIZE), yBuffer = new DoubleValues(BUFFER_SIZE);

        @Override
        public synchronized void run() {
            if (!isRunning) return;

            for (int i = 0; i < CHANNELS_COUNT; i++) {
                final IXyDataSeries<Double, Double> dataSeries = dataSet[i].getDataSeries();

                double xValue = currentSize;
                double yValue;

                xBuffer.clear();
                yBuffer.clear();

                for (int j = 0; j < BUFFER_SIZE; j++) {
                    yValue = random.nextDouble();

                    xBuffer.add(xValue++);
                    yBuffer.add(yValue);
                }

                dataSeries.append(xBuffer, yBuffer);
            }

            currentSize += BUFFER_SIZE;
        }
    };

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if (schedule != null) {
            schedule.cancel(true);
        }
    }

    private void resetChart() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < CHANNELS_COUNT; i++) {
                    dataSet[i].reset();
                }
            }
        }, 100);
    }

    private class EegChannelModel {
        private final IXyDataSeries<Double, Double> dataSeries;
        private final PenStyle strokeStyle;
        private final String channelName;

        public EegChannelModel(int size, PenStyle strokeStyle, String channelName) {
            this.dataSeries = sciChartBuilder.newXyDataSeries(Double.class, Double.class)
                    .withFifoCapacity(size)
                    .build();

            this.strokeStyle = strokeStyle;
            this.channelName = channelName;
        }

        public IXyDataSeries<Double, Double> getDataSeries() {
            return dataSeries;
        }

        public PenStyle getStrokeStyle() {
            return strokeStyle;
        }

        public String getChannelName() {
            return channelName;
        }

        public void reset() {
            dataSeries.clear();
        }
    }

    class EegChannelsAdapter extends RecyclerView.Adapter<EegChannelsAdapter.EegChannelViewHolder> {
        private final EegChannelModel[] dataSet;

        public EegChannelsAdapter(EegChannelModel[] dataSet) {
            this.dataSet = dataSet;
        }

        @Override
        public EegChannelViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.example_eeg_channel_view, null);

            EegChannelViewHolder vh = new EegChannelViewHolder(v);

            return vh;
        }

        @Override
        public void onBindViewHolder(EegChannelViewHolder holder, int position) {
            holder.bindEegChannelModel(dataSet[position]);
        }

        @Override
        public int getItemCount() {
            return dataSet.length;
        }

        class EegChannelViewHolder extends RecyclerView.ViewHolder {
            private final ExampleEegChannelViewBinding binding;

            public EegChannelViewHolder(View itemView) {
                super(itemView);

                binding = ExampleEegChannelViewBinding.bind(itemView);

                final NumericAxis xAxis = sciChartBuilder.newNumericAxis()
                        .withGrowBy(new DoubleRange(0.05d, 0.05d))
                        .withAutoRangeMode(AutoRange.Always)
                        .withDrawMajorBands(false)
                        .withDrawLabels(false)
                        .withDrawMinorTicks(false)
                        .withDrawMajorTicks(false)
                        .withDrawMajorGridLines(false)
                        .withDrawMinorGridLines(false)
                        .build();

                final NumericAxis yAxis = sciChartBuilder.newNumericAxis()
                        .withGrowBy(new DoubleRange(0.1d, 0.1d))
                        .withAutoRangeMode(AutoRange.Always)
                        .withDrawMajorBands(false)
                        .withDrawLabels(false)
                        .withDrawMinorTicks(false)
                        .withDrawMajorTicks(false)
                        .withDrawMajorGridLines(false)
                        .withDrawMinorGridLines(false)
                        .build();

                final IRenderableSeries rs = sciChartBuilder.newLineSeries().build();

                final SciChartSurface chart = binding.eegChart;
                Collections.addAll(chart.getXAxes(), xAxis);
                Collections.addAll(chart.getYAxes(), yAxis);
                Collections.addAll(chart.getRenderableSeries(), rs);
            }

            public void bindEegChannelModel(EegChannelModel channelModel) {
                binding.channelName.setText(channelModel.getChannelName());

                final IRenderableSeries renderableSeries = binding.eegChart.getRenderableSeries().get(0);

                renderableSeries.setDataSeries(channelModel.getDataSeries());
                renderableSeries.setStrokeStyle(channelModel.getStrokeStyle());
            }
        }
    }

    @Override
    public boolean showDefaultModifiersInToolbar() {
        return false;
    }
}
