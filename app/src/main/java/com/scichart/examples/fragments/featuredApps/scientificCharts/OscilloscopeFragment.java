//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2021. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// OscilloscopeFragment.java is part of SCICHART®, High Performance Scientific Charts
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

package com.scichart.examples.fragments.featuredApps.scientificCharts;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;

import com.scichart.charting.model.dataSeries.IXyDataSeries;
import com.scichart.charting.visuals.SciChartSurface;
import com.scichart.charting.visuals.axes.AutoRange;
import com.scichart.charting.visuals.axes.IAxis;
import com.scichart.charting.visuals.renderableSeries.FastLineRenderableSeries;
import com.scichart.core.framework.UpdateSuspender;
import com.scichart.core.model.DoubleValues;
import com.scichart.data.model.DoubleRange;
import com.scichart.examples.R;
import com.scichart.examples.components.SpinnerStringAdapter;
import com.scichart.examples.data.DataManager;
import com.scichart.examples.fragments.base.ExampleSingleChartBaseFragment;
import com.scichart.examples.utils.EnumUtils;
import com.scichart.examples.utils.ItemSelectedListenerBase;
import com.scichart.examples.utils.ViewSettingsUtil;
import com.scichart.examples.utils.widgetgeneration.ImageViewWidget;
import com.scichart.examples.utils.widgetgeneration.Widget;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

import androidx.annotation.NonNull;

public class OscilloscopeFragment extends ExampleSingleChartBaseFragment {
    private final static long TIME_INTERVAL = 20;
    enum DataSourceEnum {
        FourierSeries,
        Lissajous
    }

    private final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
    private ScheduledFuture<?> schedule;

    private FastLineRenderableSeries rSeries;
    private final IXyDataSeries<Double, Double> dataSeries1 = sciChartBuilder.newXyDataSeries(Double.class, Double.class).withAcceptsUnsortedData().build();
    private final IXyDataSeries<Double, Double> dataSeries2 = sciChartBuilder.newXyDataSeries(Double.class, Double.class).withAcceptsUnsortedData().build();

    private double phase0 = 0.0;
    private double phase1 = 0.0;
    private double phaseIncrement = Math.PI * 0.1;
    private DataSourceEnum selectedSource = DataSourceEnum.FourierSeries;
    private boolean isDigitalLine = false;

    @NonNull
    @Override
    public List<Widget> getToolbarItems() {
        return new ArrayList<Widget>() {{
            add(new ImageViewWidget.Builder().setId(R.drawable.example_toolbar_settings).setListener(v -> openSettingsDialog()).build());
        }};
    }

    @Override
    protected void initExample(@NonNull SciChartSurface surface) {
        surface.setTheme(R.style.SciChart_NavyBlue);

        UpdateSuspender.using(surface, () -> {
            final IAxis xBottomAxis = sciChartBuilder.newNumericAxis()
                    .withAutoRangeMode(AutoRange.Never)
                    .withAxisTitle("Time (ms)")
                    .withVisibleRange(new DoubleRange(2.5, 4.5))
                    .build();

            final IAxis yRightAxis = sciChartBuilder.newNumericAxis()
                    .withAutoRangeMode(AutoRange.Never)
                    .withAxisTitle("Voltage (mV)")
                    .withVisibleRange(new DoubleRange(-12.5, 12.5))
                    .build();

            rSeries = sciChartBuilder.newLineSeries().withDataSeries(dataSeries1).build();

            Collections.addAll(surface.getXAxes(), xBottomAxis);
            Collections.addAll(surface.getYAxes(), yRightAxis);
            Collections.addAll(surface.getRenderableSeries(), rSeries);
            Collections.addAll(surface.getChartModifiers(), sciChartBuilder.newModifierGroupWithDefaultModifiers().build());
        });

        schedule = scheduledExecutorService.scheduleWithFixedDelay(appendDataRunnable, 0, TIME_INTERVAL, MILLISECONDS);
    }

    private final Runnable appendDataRunnable = new Runnable() {
        private boolean isSecondDataSeries = false;
        private final DoubleValues xValues = new DoubleValues(), yValues = new DoubleValues();

        @Override
        public void run() {
            UpdateSuspender.using(binding.surface, this::appendData);
        }

        private void appendData() {
            if (selectedSource == DataSourceEnum.Lissajous) {
                DataManager.getInstance().setLissajousCurve(xValues, yValues, 0.12, phase1, phase0, 2500);
            } else {
                DataManager.getInstance().setFourierSeries(xValues, yValues, 2.0, phase0, 1000);
            }
            phase0 += phaseIncrement;
            phase1 += phaseIncrement * 0.005;

            // TODO - this code prevents blinking of series
            if (isSecondDataSeries) {
                dataSeries1.clear();
                dataSeries1.append(xValues, yValues);
                rSeries.setDataSeries(dataSeries1);
            } else {
                dataSeries2.clear();
                dataSeries2.append(xValues, yValues);
                rSeries.setDataSeries(dataSeries2);
            }

            isSecondDataSeries = !isSecondDataSeries;
        }
    };

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        schedule.cancel(true);
    }

    private void openSettingsDialog() {
        final Dialog dialog = ViewSettingsUtil.createSettingsPopup(getActivity(), R.layout.example_oscilloscope_demo_popup_layout);
        Context context = dialog.getContext();

        final SpinnerStringAdapter dataSourceAdapter = new SpinnerStringAdapter(context, EnumUtils.getEnumValuesArray(DataSourceEnum.class));
        final Spinner dataSourceSpinner = (Spinner) dialog.findViewById(R.id.data_source_spinner);
        dataSourceSpinner.setAdapter(dataSourceAdapter);
        dataSourceSpinner.setSelection(dataSourceAdapter.getPosition(String.valueOf(selectedSource)));
        dataSourceSpinner.setOnItemSelectedListener(new ItemSelectedListenerBase() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                final SciChartSurface surface = binding.surface;

                selectedSource = DataSourceEnum.valueOf(dataSourceAdapter.getItem(position));
                if (selectedSource == DataSourceEnum.FourierSeries) {
                    surface.getXAxes().get(0).setVisibleRange(new DoubleRange(2.5, 4.5));
                    surface.getYAxes().get(0).setVisibleRange(new DoubleRange(-12.5, 12.5));
                    phaseIncrement = Math.PI * 0.1;
                } else {
                    surface.getXAxes().get(0).setVisibleRange(new DoubleRange(-1.2, 1.2));
                    surface.getYAxes().get(0).setVisibleRange(new DoubleRange(-1.2, 1.2));
                    phaseIncrement = Math.PI * 0.02;
                }
            }
        });

        ViewSettingsUtil.setUpSwitchCompat(dialog, R.id.is_step_line_checkbox, isDigitalLine, (buttonView, isChecked) -> {
            isDigitalLine = isChecked;
            rSeries.setIsDigitalLine(isDigitalLine);
        });

        dialog.show();
    }
}
