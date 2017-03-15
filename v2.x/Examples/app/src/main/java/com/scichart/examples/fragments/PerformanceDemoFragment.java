//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2017. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// PerformanceDemoFragment.java is part of the SCICHART® Examples. Permission is hereby granted
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
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;

import com.scichart.charting.model.ChartModifierCollection;
import com.scichart.charting.model.RenderableSeriesCollection;
import com.scichart.charting.model.dataSeries.IDataSeries;
import com.scichart.charting.model.dataSeries.IXyDataSeries;
import com.scichart.charting.model.dataSeries.XyDataSeries;
import com.scichart.charting.modifiers.IChartModifier;
import com.scichart.charting.visuals.SciChartSurface;
import com.scichart.charting.visuals.annotations.AnnotationCoordinateMode;
import com.scichart.charting.visuals.annotations.CustomAnnotation;
import com.scichart.charting.visuals.axes.AutoRange;
import com.scichart.charting.visuals.axes.IAxis;
import com.scichart.charting.visuals.pointmarkers.CrossPointMarker;
import com.scichart.charting.visuals.renderableSeries.IRenderableSeries;
import com.scichart.core.framework.UpdateSuspender;
import com.scichart.core.model.FloatValues;
import com.scichart.core.model.IntegerValues;
import com.scichart.data.model.ISciList;
import com.scichart.data.numerics.ResamplingMode;
import com.scichart.drawing.common.PenStyle;
import com.scichart.examples.R;
import com.scichart.examples.components.SpinnerStringAdapter;
import com.scichart.examples.data.MovingAverage;
import com.scichart.examples.fragments.base.ExampleBaseFragment;
import com.scichart.examples.utils.ItemSelectedListenerBase;
import com.scichart.examples.utils.ViewSettingsUtil;
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

import butterknife.Bind;

public class PerformanceDemoFragment extends ExampleBaseFragment {
    private List<Integer> pointCounts = new ArrayList<Integer>() {{
        add(10);
        add(100);
        add(1000);
    }};

    private final static int MA_LOW = 200;
    private final static int MA_HIGH = 1000;
    private final static long TIME_INTERVAL = 10;

    private boolean isRunning = true;
    private String selectedSeriesType;
    private int selectedStrokeThickness = 1;
    private int pointsCount = 100;
    private ResamplingMode selectedResamplingMode = ResamplingMode.Auto;

    private Random random = new Random();
    private MovingAverage maLow = new MovingAverage(MA_LOW);
    private MovingAverage maHigh = new MovingAverage(MA_HIGH);

    @Bind(R.id.chart)
    SciChartSurface surface;
    private ScheduledFuture<?> schedule;
    private final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();

    private TextView textView;

    @Override
    protected void initExample() {
        selectedSeriesType = getResources().getStringArray(R.array.series_types)[0];

        initChart();

        // disable modifiers when updating in real-time
        updateModifiers(false);

        final int maxPointsCount = calcMaxPointCountToDisplay();

        schedule = scheduledExecutorService.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                if (!isRunning || getPointsCount() > maxPointsCount) {
                    return;
                }
                UpdateSuspender.using(surface, appendDataRunnable);
            }
        }, 0, TIME_INTERVAL, TimeUnit.MILLISECONDS);
    }

    private void initChart() {
        final IAxis xAxis = sciChartBuilder.newNumericAxis().withAutoRangeMode(AutoRange.Always).build();
        final IAxis yAxis = sciChartBuilder.newNumericAxis().withAutoRangeMode(AutoRange.Always).build();

        final IRenderableSeries rs1 = sciChartBuilder.newLineSeries().withStrokeStyle(0xFF4083B7).withDataSeries(new XyDataSeries<>(Integer.class, Float.class)).build();
        final IRenderableSeries rs2 = sciChartBuilder.newLineSeries().withStrokeStyle(0xFFFFA500).withDataSeries(new XyDataSeries<>(Integer.class, Float.class)).build();
        final IRenderableSeries rs3 = sciChartBuilder.newLineSeries().withStrokeStyle(0xFFE13219).withDataSeries(new XyDataSeries<>(Integer.class, Float.class)).build();

        textView = new TextView(getActivity());
        textView.setPadding(20, 20, 20, 20);
        final CustomAnnotation annotation = sciChartBuilder.newCustomAnnotation()
                .withCoordinateMode(AnnotationCoordinateMode.Relative)
                .withContent(textView)
                .withZIndex(-1)
                .withX1(0)
                .withY1(0)
                .build();

        UpdateSuspender.using(surface, new Runnable() {
            @Override
            public void run() {
                Collections.addAll(surface.getXAxes(), xAxis);
                Collections.addAll(surface.getYAxes(), yAxis);
                Collections.addAll(surface.getRenderableSeries(), rs1, rs2, rs3);
                Collections.addAll(surface.getChartModifiers(), sciChartBuilder.newModifierGroupWithDefaultModifiers().build());
                Collections.addAll(surface.getAnnotations(), annotation);
            }
        });
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null) {
            final int seriesCount = savedInstanceState.getInt("seriesCount");
            for (int i = 0; i < seriesCount; i++) {
                final ISciList<Integer> xValues = savedInstanceState.getParcelable("xValues" + i);
                final ISciList<Float> yValues = savedInstanceState.getParcelable("yValues" + i);
                final IXyDataSeries<Integer, Float> series = new XyDataSeries<>(Integer.class, Float.class);
                series.append(xValues, yValues);
                surface.getRenderableSeries().get(i).setDataSeries(series);
            }
        }
    }

    @Override
    public void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);

        isRunning = false;
        UpdateSuspender.using(surface, new Runnable() {
            @Override
            public void run() {
                final int size = surface.getRenderableSeries().size();
                outState.putInt("seriesCount", size);
                for (int i = 0; i < size; i++) {
                    final IRenderableSeries renderableSeries = surface.getRenderableSeries().get(i);
                    IXyDataSeries<Integer, Float> series = (IXyDataSeries<Integer, Float>) renderableSeries.getDataSeries();
                    outState.putParcelable("xValues" + i, series.getXValues());
                    outState.putParcelable("yValues" + i, series.getYValues());
                }
            }
        });
    }

    private static int calcMaxPointCountToDisplay() {
        // for resampling None need
        // 8 mb for data series (float, int)
        // 16 mb for resampling (double, double)
        // 4 mb for data indices (int)
        // 8 mb for coordinates (float, float)
        final float oneMlnPointsRequirement = 8 + 16 + 4 + 8;
        // need to reserve some memory for other needs ( 40 mb should be enough )
        final float memorySize = getMaxMemorySize() - 40;
        // max amount of point on screen
        final float maxPointCount = memorySize / oneMlnPointsRequirement * 1_000_000;
        // we have 3 series in example
        return Math.round(maxPointCount / 3);
    }

    private static int getMaxMemorySize() {
        // max memory size in megabytes
        return (int) (Runtime.getRuntime().maxMemory() / 1024L / 1024L);
    }

    private int getPointsCount() {
        int result = 0;
        RenderableSeriesCollection rsCollection = surface.getRenderableSeries();

        for (int i = 0; i < rsCollection.size(); i++) {
            IRenderableSeries renderableSeries = rsCollection.get(i);
            IDataSeries dataSeries = renderableSeries.getDataSeries();
            result += dataSeries.getCount();
        }

        return result;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        schedule.cancel(true);
    }

    private Runnable appendDataRunnable = new Runnable() {
        private final IntegerValues xValues = new IntegerValues(pointsCount);
        private final FloatValues firstYValues = new FloatValues(pointsCount);
        private final FloatValues secondYValues = new FloatValues(pointsCount);
        private final FloatValues thirdYValues = new FloatValues(pointsCount);

        @Override
        public void run() {
            xValues.clear();
            firstYValues.clear();
            secondYValues.clear();
            thirdYValues.clear();

            final IXyDataSeries<Integer, Float> mainSeries = (IXyDataSeries<Integer, Float>) surface.getRenderableSeries().get(0).getDataSeries();
            final IXyDataSeries<Integer, Float> maLowSeries = (IXyDataSeries<Integer, Float>) surface.getRenderableSeries().get(1).getDataSeries();
            final IXyDataSeries<Integer, Float> maHighSeries = (IXyDataSeries<Integer, Float>) surface.getRenderableSeries().get(2).getDataSeries();

            int xValue = mainSeries.getCount() > 0 ? mainSeries.getXValues().get(mainSeries.getCount() - 1) : 0;
            float yValue = mainSeries.getCount() > 0 ? (float) mainSeries.getYValues().get(mainSeries.getCount() - 1) : 10;
            for (int i = 0; i < pointsCount; i++) {
                xValue++;
                yValue = (yValue + (random.nextFloat() - 0.5f));
                xValues.add(xValue);
                firstYValues.add(yValue);
                secondYValues.add((float) maLow.push(yValue).getCurrent());
                thirdYValues.add((float) maHigh.push(yValue).getCurrent());
            }

            mainSeries.append(xValues, firstYValues);
            maLowSeries.append(xValues, secondYValues);
            maHighSeries.append(xValues, thirdYValues);

            final long count = mainSeries.getCount() + maLowSeries.getCount() + maHighSeries.getCount();
            final String text = "Amount of points: " + count;

            new Runnable() {
                @Override
                public void run() {
                    textView.setText(text);
                }
            }.run();
        }
    };

    private void resetChart() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                UpdateSuspender.using(surface, new Runnable() {
                    @Override
                    public void run() {
                        for (int i = 0; i < surface.getRenderableSeries().size(); i++) {
                            surface.getRenderableSeries().get(i).getDataSeries().clear();
                        }
                    }
                });
            }
        }, 100);
        maLow = new MovingAverage(MA_LOW);
        maHigh = new MovingAverage(MA_HIGH);
    }

    private void openSettingsDialog() {
        final Dialog dialog = ViewSettingsUtil.createSettingsPopup(getActivity(), R.layout.example_performance_demo_popup_layout);

        Context context = dialog.getContext();

        final SpinnerStringAdapter seriesTypesAdapter = new SpinnerStringAdapter(context, R.array.series_types);
        final Spinner seriesSpinner = (Spinner) dialog.findViewById(R.id.series_types_spinner);
        seriesSpinner.setAdapter(seriesTypesAdapter);
        seriesSpinner.setSelection(seriesTypesAdapter.getPosition(selectedSeriesType));
        seriesSpinner.setOnItemSelectedListener(new ItemSelectedListenerBase() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedSeriesType = seriesTypesAdapter.getItem(position);
                onChangeSeriesType();
            }
        });

        final SpinnerStringAdapter strokeAdapter = new SpinnerStringAdapter(context, R.array.stroke);
        final Spinner strokeSpinner = (Spinner) dialog.findViewById(R.id.stroke_spinner);
        strokeSpinner.setAdapter(strokeAdapter);
        strokeSpinner.setSelection(strokeAdapter.getPosition(String.valueOf(selectedStrokeThickness)));
        strokeSpinner.setOnItemSelectedListener(new ItemSelectedListenerBase() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedStrokeThickness = Integer.parseInt(strokeAdapter.getItem(position));
                onChangeStroke();
            }
        });

        final SpinnerStringAdapter pointsAdapter = new SpinnerStringAdapter(context, R.array.points);
        final Spinner pointsSpinner = (Spinner) dialog.findViewById(R.id.points_spinner);
        pointsSpinner.setAdapter(pointsAdapter);
        pointsSpinner.setSelection(pointsAdapter.getPosition(getResources().getStringArray(R.array.points)[pointCounts.indexOf(pointsCount)]));
        pointsSpinner.setOnItemSelectedListener(new ItemSelectedListenerBase() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                pointsCount = pointCounts.get(position);
                onChangeStroke();
            }
        });

        final SpinnerStringAdapter resamplingModeAdapter = new SpinnerStringAdapter(context, R.array.resampling_mode);
        final Spinner resamplingSpinner = (Spinner) dialog.findViewById(R.id.resampling_mode_spinner);
        resamplingSpinner.setAdapter(resamplingModeAdapter);
        resamplingSpinner.setSelection(resamplingModeAdapter.getPosition(selectedResamplingMode.toString()));
        resamplingSpinner.setOnItemSelectedListener(new ItemSelectedListenerBase() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedResamplingMode = ResamplingMode.valueOf(resamplingModeAdapter.getItem(position));
                onResamplingMode();
            }
        });

        dialog.show();
    }

    private void onChangeSeriesType() {
        UpdateSuspender.using(surface, new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < surface.getRenderableSeries().size(); i++) {
                    IRenderableSeries oldRenderableSeries = surface.getRenderableSeries().get(i);
                    IRenderableSeries newRenderableSeries = changeSeriesType(selectedSeriesType, oldRenderableSeries);
                    surface.getRenderableSeries().set(i, newRenderableSeries);
                }
            }
        });
    }

    private IRenderableSeries changeSeriesType(String param, IRenderableSeries initSeries) {
        if (getResources().getStringArray(R.array.series_types)[0].equals(param)) {
            return sciChartBuilder.newLineSeries()
                    .withDataSeries(initSeries.getDataSeries())
                    .withStrokeStyle(initSeries.getStrokeStyle())
                    .withResamplingMode(initSeries.getResamplingMode())
                    .build();
        } else if (getResources().getStringArray(R.array.series_types)[1].equals(param)) {
            return sciChartBuilder.newMountainSeries()
                    .withDataSeries(initSeries.getDataSeries())
                    .withStrokeStyle(initSeries.getStrokeStyle())
                    .withResamplingMode(initSeries.getResamplingMode())
                    .withAreaFillColor(initSeries.getStrokeStyle().getColor())
                    .build();
        } else if (getResources().getStringArray(R.array.series_types)[2].equals(param)) {
            CrossPointMarker pointMarker = new CrossPointMarker();
            pointMarker.setSize(20, 20);
            pointMarker.setStrokeStyle(initSeries.getStrokeStyle());

            return sciChartBuilder.newScatterSeries()
                    .withDataSeries(initSeries.getDataSeries())
                    .withStrokeStyle(initSeries.getStrokeStyle())
                    .withResamplingMode(initSeries.getResamplingMode())
                    .withPointMarker(pointMarker)
                    .build();
        }

        return null;
    }

    private void onChangeStroke() {
        UpdateSuspender.using(surface, new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < surface.getRenderableSeries().size(); i++) {
                    IRenderableSeries renderableSeries = surface.getRenderableSeries().get(i);
                    final PenStyle currentStyle = renderableSeries.getStrokeStyle();
                    renderableSeries.setStrokeStyle(sciChartBuilder.newPen()
                            .withColor(currentStyle.getColor())
                            .withAntiAliasing(currentStyle.antiAliasing)
                            .withThickness(selectedStrokeThickness)
                            .build());
                }
            }
        });
    }

    private void onResamplingMode() {
        UpdateSuspender.using(surface, new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < surface.getRenderableSeries().size(); i++) {
                    IRenderableSeries rSeries = surface.getRenderableSeries().get(i);
                    rSeries.setResamplingMode(selectedResamplingMode);
                }
            }
        });
    }

    @Override
    public List<Widget> getToolbarItems() {
        return new ArrayList<Widget>() {
            {
                add(new ImageViewWidget.Builder().setId(R.drawable.example_toolbar_play).setListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        isRunning = true;
                        updateAutoRangeBehavior(isRunning);
                        updateModifiers(!isRunning);
                    }
                }).build());
                add(new ImageViewWidget.Builder().setId(R.drawable.example_toolbar_pause).setListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        isRunning = false;
                        updateAutoRangeBehavior(isRunning);
                        updateModifiers(!isRunning);
                    }
                }).build());
                add(new ImageViewWidget.Builder().setId(R.drawable.example_toolbar_stop).setListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        isRunning = false;
                        resetChart();
                        updateAutoRangeBehavior(isRunning);
                        updateModifiers(!isRunning);
                    }
                }).build());
                add(new ImageViewWidget.Builder().setId(R.drawable.example_toolbar_settings).setListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openSettingsDialog();
                    }
                }).build());
            }
        };
    }

    private void updateAutoRangeBehavior(boolean isEnabled) {
        IAxis xAxis = surface.getXAxes().get(0);
        IAxis yAxis = surface.getYAxes().get(0);

        AutoRange autoRangeMode = isEnabled ? AutoRange.Always : AutoRange.Never;

        xAxis.setAutoRange(autoRangeMode);
        yAxis.setAutoRange(autoRangeMode);
    }

    private void updateModifiers(boolean isEnabled) {
        ChartModifierCollection modifiers = surface.getChartModifiers();
        for (IChartModifier modifier : modifiers) {
            modifier.setIsEnabled(isEnabled);
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.example_single_chart_fragment;
    }
}