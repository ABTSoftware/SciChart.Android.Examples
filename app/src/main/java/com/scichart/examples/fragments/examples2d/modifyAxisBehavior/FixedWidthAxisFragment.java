//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2021. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// LogarithmicAxisFragment.java is part of SCICHART®, High Performance Scientific Charts
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

package com.scichart.examples.fragments.examples2d.modifyAxisBehavior;

import android.app.Dialog;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.SeekBar;

import androidx.annotation.NonNull;

import com.scichart.charting.model.dataSeries.IXyDataSeries;
import com.scichart.charting.visuals.SciChartSurface;
import com.scichart.charting.visuals.axes.AutoRange;
import com.scichart.charting.visuals.axes.AxisAlignment;
import com.scichart.charting.visuals.axes.AxisTickLabelStyle;
import com.scichart.charting.visuals.axes.NumericAxis;
import com.scichart.charting.visuals.pointmarkers.EllipsePointMarker;
import com.scichart.charting.visuals.pointmarkers.IPointMarker;
import com.scichart.charting.visuals.renderableSeries.FastLineRenderableSeries;
import com.scichart.core.framework.UpdateSuspender;
import com.scichart.drawing.utility.ColorUtil;
import com.scichart.examples.R;
import com.scichart.examples.fragments.base.ExampleSingleChartBaseFragment;
import com.scichart.examples.utils.ItemSelectedListenerBase;
import com.scichart.examples.utils.SeekBarChangeListenerBase;
import com.scichart.examples.utils.ViewSettingsUtil;
import com.scichart.examples.utils.widgetgeneration.ImageViewWidget;
import com.scichart.examples.utils.widgetgeneration.Widget;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class FixedWidthAxisFragment extends ExampleSingleChartBaseFragment {

    private final static int FIFO_CAPACITY = 50;
    private final static long TIME_INTERVAL = 500;

    private final IXyDataSeries<Double, Double> ds1 = sciChartBuilder.newXyDataSeries(Double.class, Double.class).withFifoCapacity(FIFO_CAPACITY).build();
    private final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
    private ScheduledFuture<?> schedule;

    private Double index = 0.0;
    private Double value = 0.0;
    private Boolean isIncreasing = true;

    private int yAxisAlignment = 0;
    private int xAxisAlignment = 0;

    private int xAxisSize = 200;
    private int yAxisSize = 200;
    private NumericAxis xAxis;
    private NumericAxis yAxis;

    @NonNull
    @Override
    public List<Widget> getToolbarItems() {
        return new ArrayList<Widget>() {{
            add(new ImageViewWidget.Builder().setId(R.drawable.example_toolbar_settings).setListener(v -> openSettingsDialog()).build());
        }};
    }

    @Override
    protected void initExample(@NonNull SciChartSurface surface) {

        final int line1Color = ColorUtil.argb(0xFF, 0x47, 0xBD, 0xE6);

        final IPointMarker pointMarker1 = sciChartBuilder.newPointMarker(new EllipsePointMarker()).withSize(5).withFill(line1Color).build();

        final FastLineRenderableSeries line1 = sciChartBuilder.newLineSeries()
                .withStrokeStyle(line1Color)
                .withPointMarker(pointMarker1)
                .withDataSeries(ds1)
                .build();

        xAxis = sciChartBuilder
                .newNumericAxis()
                .withAutoRangeMode(AutoRange.Always)
                .withAxisTickLabelStyle(Gravity.TOP, 0,0,0,0)
                .withFixedSize(xAxisSize)
                .build();

        yAxis = sciChartBuilder
                .newNumericAxis()
                .withAutoRangeMode(AutoRange.Always)
                .withAxisTickLabelStyle(Gravity.LEFT, 0,0,0,0)
                .withFixedSize(yAxisSize)
                .build();

        UpdateSuspender.using(surface, () -> {
            Collections.addAll(surface.getXAxes(), xAxis);
            Collections.addAll(surface.getYAxes(), yAxis);
            Collections.addAll(surface.getRenderableSeries(), line1);
            Collections.addAll(surface.getChartModifiers(), sciChartBuilder.newModifierGroupWithDefaultModifiers().build());

        });

        schedule = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            UpdateSuspender.using(surface, insertRunnable);
        }, 0, TIME_INTERVAL, TimeUnit.MILLISECONDS);
    }

    private final Runnable insertRunnable = () -> {
        ds1.append(index, value * value);
        index++;
        if (value == 200.0) {
            isIncreasing = false;
        }
        if (value == 0.0) {
            isIncreasing = true;
        }
        if (isIncreasing) {
            value += 1.0;
        } else {
            value -= 1.0;
        }
    };

    private void openSettingsDialog() {
        final Dialog dialog = ViewSettingsUtil.createSettingsPopup(getActivity(), R.layout.example_fixed_width_popup_layout);

        // For X Axis
        ViewSettingsUtil.setUpSpinner(dialog, R.id.xAxisAlignmentSelector, R.array.x_axis_alignment_list, xAxisAlignment, new ItemSelectedListenerBase() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                UpdateSuspender.using(binding.surface, ()->{
                    switch (position) {
                        case 0:
                            xAxis.setAxisTickLabelStyle(new AxisTickLabelStyle(
                                    Gravity.TOP,
                                    0,0,0,0
                            ));
                            xAxisAlignment = 0;
                            break;
                        case 1:
                            xAxis.setAxisTickLabelStyle(new AxisTickLabelStyle(
                                    Gravity.CENTER,
                                    0,0,0,0
                            ));
                            xAxisAlignment = 1;
                            break;
                        case 2:
                            xAxis.setAxisTickLabelStyle(new AxisTickLabelStyle(
                                    Gravity.BOTTOM,
                                    0,0,0,0
                            ));
                            xAxisAlignment = 2;
                            break;
                        default:
                            break;
                    }
                });
            }
        });
        ViewSettingsUtil.setUpSeekBar(dialog, R.id.x_axis_width_seek_bar, xAxisSize, new SeekBarChangeListenerBase() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                UpdateSuspender.using(binding.surface, ()->{
                    xAxis.setFixedSize(progress);
                    xAxisSize = progress;
                });
            }
        });

        // For Y Axis
        ViewSettingsUtil.setUpSpinner(dialog, R.id.yAxisAlignmentSelector, R.array.y_axis_alignment_list, yAxisAlignment, new ItemSelectedListenerBase() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                UpdateSuspender.using(binding.surface, ()->{
                    switch (position) {
                        case 0:
                            yAxis.setAxisTickLabelStyle(new AxisTickLabelStyle(
                                    Gravity.LEFT,
                                    0,0,0,0
                            ));
                            yAxisAlignment = 0;
                            break;
                        case 1:
                            yAxis.setAxisTickLabelStyle(new AxisTickLabelStyle(
                                    Gravity.CENTER,
                                    0,0,0,0
                            ));
                            yAxisAlignment = 1;
                            break;
                        case 2:
                            yAxis.setAxisTickLabelStyle(new AxisTickLabelStyle(
                                    Gravity.RIGHT,
                                    0,0,0,0
                            ));
                            yAxisAlignment = 2;
                            break;
                        default:
                            break;
                    }
                });
            }
        });
        ViewSettingsUtil.setUpSeekBar(dialog, R.id.y_axis_width_seek_bar, yAxisSize, new SeekBarChangeListenerBase() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                UpdateSuspender.using(binding.surface, ()->{
                    yAxis.setFixedSize(progress);
                    yAxisSize = progress;
                });
            }
        });


        dialog.show();
    }

}