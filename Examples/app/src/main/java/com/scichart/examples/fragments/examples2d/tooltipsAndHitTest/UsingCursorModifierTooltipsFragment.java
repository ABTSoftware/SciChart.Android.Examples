//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2021. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// UsingCursorModifierTooltipsFragment.java is part of SCICHART®, High Performance Scientific Charts
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

package com.scichart.examples.fragments.examples2d.tooltipsAndHitTest;

import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableList;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.Spinner;

import androidx.annotation.NonNull;

import com.scichart.charting.model.dataSeries.XyDataSeries;
import com.scichart.charting.modifiers.CursorModifier;
import com.scichart.charting.modifiers.ModifierGroup;
import com.scichart.charting.modifiers.SourceMode;
import com.scichart.charting.visuals.SciChartSurface;
import com.scichart.charting.visuals.axes.AutoRange;
import com.scichart.charting.visuals.axes.IAxis;
import com.scichart.charting.visuals.renderableSeries.FastLineRenderableSeries;
import com.scichart.core.framework.UpdateSuspender;
import com.scichart.data.model.DoubleRange;
import com.scichart.drawing.utility.ColorUtil;
import com.scichart.examples.R;
import com.scichart.examples.components.SpinnerStringAdapter;
import com.scichart.examples.data.DataManager;
import com.scichart.examples.data.DoubleSeries;
import com.scichart.examples.fragments.base.ExampleSingleChartBaseFragment;
import com.scichart.examples.utils.EnumUtils;
import com.scichart.examples.utils.ItemSelectedListenerBase;
import com.scichart.examples.utils.ViewSettingsUtil;
import com.scichart.examples.utils.widgetgeneration.ImageViewWidget;
import com.scichart.examples.utils.widgetgeneration.Widget;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UsingCursorModifierTooltipsFragment extends ExampleSingleChartBaseFragment {
    private static final int POINT_COUNT = 500;

    private static final List<SourceMode> sourceModeValues = unmodifiableList(asList(SourceMode.values()));
    private CursorModifier cursorModifier;

    private int selectedSourceMode = 1;
    private boolean showTooltip = true;
    private boolean showAxisLabels = true;

    @Override
    protected void initExample(@NonNull SciChartSurface surface) {
        final IAxis xAxis = sciChartBuilder.newNumericAxis().withVisibleRange(new DoubleRange(3d, 6d)).build();
        final IAxis yAxis = sciChartBuilder.newNumericAxis().withAutoRangeMode(AutoRange.Always).withGrowBy(0.05d, 0.05d).build();

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

        UpdateSuspender.using(surface, () -> {
            Collections.addAll(surface.getXAxes(), xAxis);
            Collections.addAll(surface.getYAxes(), yAxis);
            Collections.addAll(surface.getRenderableSeries(), rs1, rs2, rs3, rs4);
            Collections.addAll(surface.getChartModifiers(), sciChartBuilder.newModifierGroup()
                    .withCursorModifier().withShowTooltip(showTooltip).withShowAxisLabels(showAxisLabels).build()
                    .build());
            cursorModifier = (CursorModifier) ((ModifierGroup) surface.getChartModifiers().get(0)).getChildModifiers().get(0);

            sciChartBuilder.newAnimator(rs1).withSweepTransformation().withInterpolator(new DecelerateInterpolator()).withDuration(2000).withStartDelay(350).start();
            sciChartBuilder.newAnimator(rs2).withSweepTransformation().withInterpolator(new DecelerateInterpolator()).withDuration(2000).withStartDelay(350).start();
            sciChartBuilder.newAnimator(rs3).withSweepTransformation().withInterpolator(new DecelerateInterpolator()).withDuration(2000).withStartDelay(350).start();
            sciChartBuilder.newAnimator(rs4).withSweepTransformation().withInterpolator(new DecelerateInterpolator()).withDuration(2000).withStartDelay(350).start();
        });
    }

    @NonNull
    @Override
    public List<Widget> getToolbarItems() {
        return new ArrayList<Widget>() {{
            add(new ImageViewWidget.Builder().setId(R.drawable.example_toolbar_settings).setListener(v -> openSettingsDialog()).build());
        }};
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

        ViewSettingsUtil.setUpSwitchCompat(dialog, R.id.show_tooltips_checkbox, showTooltip, (buttonView, isChecked) -> {
            showTooltip = isChecked;
            cursorModifier.setShowTooltip(showTooltip);
        });

        ViewSettingsUtil.setUpSwitchCompat(dialog, R.id.show_axis_labels_checkbox, showAxisLabels, (buttonView, isChecked) -> {
            showAxisLabels = isChecked;
            cursorModifier.setShowAxisLabels(showAxisLabels);
        });

        dialog.show();
    }
}