//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2017. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// LogarithmicAxisFragment.java is part of the SCICHART® Examples. Permission is hereby granted
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

import com.scichart.charting.model.AxisCollection;
import com.scichart.charting.model.dataSeries.XyDataSeries;
import com.scichart.charting.modifiers.ModifierGroup;
import com.scichart.charting.visuals.SciChartSurface;
import com.scichart.charting.visuals.axes.IAxis;
import com.scichart.charting.visuals.axes.ILogarithmicNumericAxis;
import com.scichart.charting.visuals.axes.ScientificNotation;
import com.scichart.charting.visuals.pointmarkers.EllipsePointMarker;
import com.scichart.charting.visuals.pointmarkers.IPointMarker;
import com.scichart.charting.visuals.renderableSeries.FastLineRenderableSeries;
import com.scichart.core.framework.UpdateSuspender;
import com.scichart.drawing.utility.ColorUtil;
import com.scichart.examples.R;
import com.scichart.examples.components.SpinnerStringAdapter;
import com.scichart.examples.data.DataManager;
import com.scichart.examples.data.DoubleSeries;
import com.scichart.examples.fragments.base.ExampleBaseFragment;
import com.scichart.examples.utils.ItemSelectedListenerBase;
import com.scichart.examples.utils.ViewSettingsUtil;
import com.scichart.examples.utils.widgetgeneration.ImageViewWidget;
import com.scichart.examples.utils.widgetgeneration.Widget;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.Bind;

public class LogarithmicAxisFragment extends ExampleBaseFragment {

    private double selectedLogBase = 10d;

    private boolean isXLogAxis, isYLogAxis;
    private IAxis xAxis, yAxis;

    @Bind(R.id.chart)
    SciChartSurface surface;

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
        xAxis = generateLogarithmicAxis();
        yAxis = generateLogarithmicAxis();
        isXLogAxis = isYLogAxis = true;

        final XyDataSeries<Double, Double> dataSeries1 = sciChartBuilder.newXyDataSeries(Double.class, Double.class).withSeriesName("Curve A").build();
        final XyDataSeries<Double, Double> dataSeries2 = sciChartBuilder.newXyDataSeries(Double.class, Double.class).withSeriesName("Curve B").build();
        final XyDataSeries<Double, Double> dataSeries3 = sciChartBuilder.newXyDataSeries(Double.class, Double.class).withSeriesName("Curve C").build();

        final DoubleSeries ds1Points = DataManager.getInstance().getExponentialCurve(1.8, 100);
        final DoubleSeries ds2Points = DataManager.getInstance().getExponentialCurve(2.25, 100);
        final DoubleSeries ds3Points = DataManager.getInstance().getExponentialCurve(3.59, 100);

        dataSeries1.append(ds1Points.xValues, ds1Points.yValues);
        dataSeries2.append(ds2Points.xValues, ds2Points.yValues);
        dataSeries3.append(ds3Points.xValues, ds3Points.yValues);

        final int line1Color = ColorUtil.argb(0xFF, 0xFF, 0xFF, 0x00);
        final int line2Color = ColorUtil.argb(0xFF, 0x27, 0x9B, 0x27);
        final int line3Color = ColorUtil.argb(0xFF, 0xFF, 0x19, 0x19);

        final IPointMarker pointMarker1 = sciChartBuilder.newPointMarker(new EllipsePointMarker())
                .withSize(5)
                .withFill(line1Color)
                .build();
        final IPointMarker pointMarker2 = sciChartBuilder.newPointMarker(new EllipsePointMarker())
                .withSize(5)
                .withFill(line2Color)
                .build();
        final IPointMarker pointMarker3 = sciChartBuilder.newPointMarker(new EllipsePointMarker())
                .withSize(5)
                .withFill(line3Color)
                .build();

        final FastLineRenderableSeries line1 = sciChartBuilder.newLineSeries()
                .withStrokeStyle(line1Color)
                .withPointMarker(pointMarker1)
                .withDataSeries(dataSeries1)
                .build();
        final FastLineRenderableSeries line2 = sciChartBuilder.newLineSeries()
                .withStrokeStyle(line2Color)
                .withPointMarker(pointMarker2)
                .withDataSeries(dataSeries2)
                .build();

        final FastLineRenderableSeries line3 = sciChartBuilder.newLineSeries()
                .withStrokeStyle(line3Color)
                .withPointMarker(pointMarker3)
                .withDataSeries(dataSeries3)
                .build();

        UpdateSuspender.using(surface, new Runnable() {
            @Override
            public void run() {
                Collections.addAll(surface.getXAxes(), xAxis);
                Collections.addAll(surface.getYAxes(), yAxis);
                Collections.addAll(surface.getRenderableSeries(), line1, line2, line3);

                ModifierGroup modifierGroup = sciChartBuilder
                        .newModifierGroupWithDefaultModifiers()
                        .build();

                surface.getChartModifiers().add(modifierGroup);
            }
        });

        surface.zoomExtents();
    }

    private IAxis generateLinearAxis() {
        return sciChartBuilder.newNumericAxis()
                .withTextFormatting("#.#E+0")
                .withScientificNotation(ScientificNotation.Normalized)
                .withGrowBy(0.1, 0.1)
                .withDrawMajorBands(false)
                .build();
    }

    private IAxis generateLogarithmicAxis() {
        return sciChartBuilder.newLogarithmicNumericAxis()
                .withTextFormatting("#.#E+0")
                .withScientificNotation(ScientificNotation.LogarithmicBase)
                .withLogarithmicBase(selectedLogBase)
                .withGrowBy(0.1, 0.1)
                .withDrawMajorBands(false)
                .build();
    }

    private void openSettingsDialog() {
        final Dialog dialog = ViewSettingsUtil.createSettingsPopup(getActivity(), R.layout.example_log_axis_popup_layout);

        Context context = dialog.getContext();

        final SpinnerStringAdapter logBaseModesAdapter = new SpinnerStringAdapter(context, R.array.log_base_modes);
        final Spinner logBaseModesSpinner = (Spinner) dialog.findViewById(R.id.log_base_spinner);
        logBaseModesSpinner.setAdapter(logBaseModesAdapter);
        logBaseModesSpinner.setSelection(2);
        logBaseModesSpinner.setOnItemSelectedListener(new ItemSelectedListenerBase() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0: selectedLogBase = 2d; break;
                    case 1: selectedLogBase = 5d; break;
                    case 2: selectedLogBase = 10d; break;
                    case 3: selectedLogBase = Math.E; break;
                }

                trySetLogBaseForAxis(xAxis, selectedLogBase);
                trySetLogBaseForAxis(yAxis, selectedLogBase);
            }
        });

        ViewSettingsUtil.setUpSwitchCompat(dialog, R.id.use_log_on_x_axis_checkbox, isXLogAxis, new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isXLogAxis = isChecked;
                xAxis = isXLogAxis ? generateLogarithmicAxis() : generateLinearAxis();

                UpdateSuspender.using(surface, new Runnable() {
                    @Override
                    public void run() {
                        final AxisCollection xAxes = surface.getXAxes();

                        xAxes.clear();
                        xAxes.add(xAxis);
                    }
                });
            }
        });

        ViewSettingsUtil.setUpSwitchCompat(dialog, R.id.use_log_on_y_axis_checkbox, isYLogAxis, new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isYLogAxis = isChecked;
                yAxis = isYLogAxis ? generateLogarithmicAxis() : generateLinearAxis();

                UpdateSuspender.using(surface, new Runnable() {
                    @Override
                    public void run() {
                        final AxisCollection yAxes = surface.getYAxes();

                        yAxes.clear();
                        yAxes.add(yAxis);
                    }
                });
            }
        });

        dialog.show();
    }

    private static void trySetLogBaseForAxis(IAxis axis, double logBase) {
        if(axis instanceof ILogarithmicNumericAxis)
            ((ILogarithmicNumericAxis)axis).setLogarithmicBase(logBase);
    }
}
