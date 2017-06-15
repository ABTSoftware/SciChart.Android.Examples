//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2017. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// OscilloscopeFragment.java is part of the SCICHART® Examples. Permission is hereby granted
// to modify, create derivative works, distribute and publish any part of this source
// code whether for commercial, private or personal use.
//
// The SCICHART® examples are distributed in the hope that they will be useful, but
// without any warranty. It is provided "AS IS" without warranty of any kind, either
// expressed or implied.
//******************************************************************************

package com.scichart.examples.fragments;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
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
import com.scichart.examples.fragments.base.ExampleBaseFragment;
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

import butterknife.Bind;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

public class OscilloscopeFragment extends ExampleBaseFragment {

    enum DataSourceEnum {
        FourierSeries,
        Lissajous
    }

    private DataSourceEnum selectedSource = DataSourceEnum.FourierSeries;
    private boolean isDigitalLine = false;

    private final static long TIME_INTERVAL = 20;

    private final IXyDataSeries<Double, Double> dataSeries1 = sciChartBuilder.newXyDataSeries(Double.class, Double.class).withAcceptsUnsortedData().build();
    private final IXyDataSeries<Double, Double> dataSeries2 = sciChartBuilder.newXyDataSeries(Double.class, Double.class).withAcceptsUnsortedData().build();

    private final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
    private ScheduledFuture<?> schedule;

    private double phase0 = 0.0;
    private double phase1 = 0.0;
    private double phaseIncrement = Math.PI * 0.1;

    @Bind(R.id.chart)
    SciChartSurface surface;

    private FastLineRenderableSeries rSeries;

    @Override
    public List<Widget> getToolbarItems() {
        return new ArrayList<Widget>() {
            {
                add(new ImageViewWidget.Builder().setId(R.drawable.example_toolbar_settings).setListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openSettingsDialog();
                    }
                }).build());
            }
        };
    }

    @Override
    protected int getLayoutId() {
        return R.layout.example_single_chart_fragment;
    }

    @Override
    protected void initExample() {
        UpdateSuspender.using(surface, new Runnable() {
            @Override
            public void run() {
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

                rSeries = sciChartBuilder.newLineSeries()
                        .withDataSeries(dataSeries1)
                        .withXAxisId(xBottomAxis.getAxisId())
                        .withYAxisId(yRightAxis.getAxisId())
                        .build();

                Collections.addAll(surface.getXAxes(), xBottomAxis);
                Collections.addAll(surface.getYAxes(), yRightAxis);
                Collections.addAll(surface.getRenderableSeries(), rSeries);
                Collections.addAll(surface.getChartModifiers(), sciChartBuilder.newModifierGroupWithDefaultModifiers().build());
            }
        });

        schedule = scheduledExecutorService.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                UpdateSuspender.using(surface, appendDataRunnable);
            }
        }, 0, TIME_INTERVAL, MILLISECONDS);
    }

    private Runnable appendDataRunnable = new Runnable() {
        private boolean isSecondDataSeries = false;
        private final DoubleValues xValues = new DoubleValues(), yValues = new DoubleValues();

        @Override
        public void run() {

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

        ViewSettingsUtil.setUpSwitchCompat(dialog, R.id.is_step_line_checkbox, isDigitalLine, new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isDigitalLine = isChecked;
                ((FastLineRenderableSeries) surface.getRenderableSeries().get(0)).setIsDigitalLine(isDigitalLine);
            }
        });

        dialog.show();
    }
}
