//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2017. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// UsingCursorModifierTooltipsFragment.java is part of the SCICHART® Examples. Permission is hereby granted
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

import com.scichart.charting.model.dataSeries.XyDataSeries;
import com.scichart.charting.modifiers.CursorModifier;
import com.scichart.charting.modifiers.ModifierGroup;
import com.scichart.charting.modifiers.SourceMode;
import com.scichart.charting.visuals.SciChartSurface;
import com.scichart.charting.visuals.axes.AutoRange;
import com.scichart.charting.visuals.axes.NumericAxis;
import com.scichart.charting.visuals.renderableSeries.FastLineRenderableSeries;
import com.scichart.core.framework.UpdateSuspender;
import com.scichart.data.model.DoubleRange;
import com.scichart.drawing.utility.ColorUtil;
import com.scichart.examples.R;
import com.scichart.examples.components.SpinnerStringAdapter;
import com.scichart.examples.data.DataManager;
import com.scichart.examples.data.DoubleSeries;
import com.scichart.examples.fragments.base.ExampleBaseFragment;
import com.scichart.examples.utils.EnumUtils;
import com.scichart.examples.utils.ItemSelectedListenerBase;
import com.scichart.examples.utils.ViewSettingsUtil;
import com.scichart.examples.utils.widgetgeneration.ImageViewWidget;
import com.scichart.examples.utils.widgetgeneration.Widget;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.Bind;

import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableList;

public class UsingCursorModifierTooltipsFragment extends ExampleBaseFragment {
    private static final int POINT_COUNT = 500;

    private static final List<SourceMode> sourceModeValues = unmodifiableList(asList(SourceMode.values()));
    private CursorModifier cursorModifier;

    private int selectedSourceMode = 1;    //1 = SourceMode.AllVisibleSeries
    private boolean showTooltip = true;
    private boolean showAxisLabels = true;

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
    protected void initExample() {
        final NumericAxis xAxis = sciChartBuilder.newNumericAxis().withVisibleRange(new DoubleRange(3d, 6d)).build();
        final NumericAxis yAxis = sciChartBuilder.newNumericAxis().withAutoRangeMode(AutoRange.Always).withGrowBy(0.05d, 0.05d).build();

        final XyDataSeries<Double, Double> ds1 = sciChartBuilder.newXyDataSeries(Double.class, Double.class).withSeriesName("Green Series").build();
        final XyDataSeries<Double, Double> ds2 = sciChartBuilder.newXyDataSeries(Double.class, Double.class).withSeriesName("Red Series").build();
        final XyDataSeries<Double, Double> ds3 = sciChartBuilder.newXyDataSeries(Double.class, Double.class).withSeriesName("Grey Series").build();
        final XyDataSeries<Double, Double> ds4 = sciChartBuilder.newXyDataSeries(Double.class, Double.class).withSeriesName("Gold Series").build();

        final DoubleSeries data1 = DataManager.getInstance().getNoisySinewave(300, 1.0d, POINT_COUNT, 0.25);
        final DoubleSeries data2 = DataManager.getInstance().getSinewave(100, 2, POINT_COUNT);
        final DoubleSeries data3 = DataManager.getInstance().getSinewave(200, 1.5d, POINT_COUNT);
        final DoubleSeries data4 = DataManager.getInstance().getSinewave(50, 0.1d, POINT_COUNT);

        ds1.append(data1.xValues, data1.yValues);
        ds2.append(data2.xValues, data2.yValues);
        ds3.append(data3.xValues, data3.yValues);
        ds4.append(data4.xValues, data4.yValues);

        final FastLineRenderableSeries rs1 = sciChartBuilder.newLineSeries().withDataSeries(ds1).withStrokeStyle(0xFF177B17, 2, true).build();
        final FastLineRenderableSeries rs2 = sciChartBuilder.newLineSeries().withDataSeries(ds2).withStrokeStyle(0xFFDD0909, 2, true).build();
        final FastLineRenderableSeries rs3 = sciChartBuilder.newLineSeries().withDataSeries(ds3).withStrokeStyle(ColorUtil.Grey, 2, true).build();
        final FastLineRenderableSeries rs4 = sciChartBuilder.newLineSeries().withDataSeries(ds4).withStrokeStyle(ColorUtil.Gold, 2, true).withIsVisible(false).build();

        UpdateSuspender.using(surface, new Runnable() {
            @Override
            public void run() {
                Collections.addAll(surface.getXAxes(), xAxis);
                Collections.addAll(surface.getYAxes(), yAxis);
                Collections.addAll(surface.getRenderableSeries(), rs1, rs2, rs3, rs4);
                Collections.addAll(surface.getChartModifiers(), sciChartBuilder.newModifierGroup()
                        .withCursorModifier().withShowTooltip(showTooltip).withShowAxisLabels(showAxisLabels).build()
                        .build());
                cursorModifier = (CursorModifier) ((ModifierGroup) surface.getChartModifiers().get(0)).getChildModifiers().get(0);
            }
        });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.example_single_chart_fragment;
    }

    private void openSettingsDialog() {
        final Dialog dialog = ViewSettingsUtil.createSettingsPopup(getActivity(), R.layout.example_using_cursor_modofier_tooltips_popup_layout);

        Context context = dialog.getContext();

        final SpinnerStringAdapter legendSourceAdapter = new SpinnerStringAdapter(context, EnumUtils.getEnumValuesArray(SourceMode.class));
        final Spinner legendSourceSpinner = (Spinner) dialog.findViewById(R.id.legend_source_spinner_cursor);
        legendSourceSpinner.setAdapter(legendSourceAdapter);
        legendSourceSpinner.setSelection(selectedSourceMode);
        legendSourceSpinner.setOnItemSelectedListener(new ItemSelectedListenerBase() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedSourceMode = position;
                cursorModifier.setSourceMode(sourceModeValues.get(selectedSourceMode));
            }
        });

        ViewSettingsUtil.setUpSwitchCompat(dialog, R.id.show_tooltips_checkbox, showTooltip, new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                showTooltip = isChecked;
                cursorModifier.setShowTooltip(showTooltip);
            }
        });

        ViewSettingsUtil.setUpSwitchCompat(dialog, R.id.show_axis_labels_checkbox, showAxisLabels, new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                showAxisLabels = isChecked;
                cursorModifier.setShowAxisLabels(showAxisLabels);
            }
        });

        dialog.show();
    }
}