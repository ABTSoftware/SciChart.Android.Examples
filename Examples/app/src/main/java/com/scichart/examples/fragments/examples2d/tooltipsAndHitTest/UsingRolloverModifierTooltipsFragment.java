//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2021. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// UsingRolloverModifierTooltipsFragment.java is part of SCICHART®, High Performance Scientific Charts
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

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.Spinner;

import com.scichart.charting.model.dataSeries.XyDataSeries;
import com.scichart.charting.modifiers.RolloverModifier;
import com.scichart.charting.modifiers.SourceMode;
import com.scichart.charting.visuals.SciChartSurface;
import com.scichart.charting.visuals.axes.IAxis;
import com.scichart.charting.visuals.pointmarkers.EllipsePointMarker;
import com.scichart.charting.visuals.renderableSeries.FastLineRenderableSeries;
import com.scichart.core.framework.UpdateSuspender;
import com.scichart.data.model.DoubleRange;
import com.scichart.drawing.utility.ColorUtil;
import com.scichart.examples.R;
import com.scichart.examples.components.SpinnerStringAdapter;
import com.scichart.examples.fragments.base.ExampleSingleChartBaseFragment;
import com.scichart.examples.utils.EnumUtils;
import com.scichart.examples.utils.ItemSelectedListenerBase;
import com.scichart.examples.utils.ViewSettingsUtil;
import com.scichart.examples.utils.widgetgeneration.ImageViewWidget;
import com.scichart.examples.utils.widgetgeneration.Widget;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableList;

import androidx.annotation.NonNull;

public class UsingRolloverModifierTooltipsFragment extends ExampleSingleChartBaseFragment {
    private static final List<SourceMode> sourceModeValues = unmodifiableList(asList(SourceMode.values()));
    private RolloverModifier rolloverModifier;

    private int selectedSourceMode = 1;
    private boolean showTooltip = true;
    private boolean showAxisLabels = true;
    private boolean drawVerticalLine = true;

    @Override
    protected void initExample(@NonNull SciChartSurface surface) {
        final IAxis xAxis = sciChartBuilder.newNumericAxis().build();
        final IAxis yAxis = sciChartBuilder.newNumericAxis().withGrowBy(new DoubleRange(0.2d, 0.2d)).build();

        rolloverModifier = new RolloverModifier();
        rolloverModifier.setShowTooltip(showTooltip);
        rolloverModifier.setShowAxisLabels(showAxisLabels);
        rolloverModifier.setDrawVerticalLine(drawVerticalLine);

        final XyDataSeries<Integer, Double> ds1 = sciChartBuilder.newXyDataSeries(Integer.class, Double.class).withSeriesName("SineWave A").build();
        final XyDataSeries<Integer, Double> ds2 = sciChartBuilder.newXyDataSeries(Integer.class, Double.class).withSeriesName("SineWave B").build();
        final XyDataSeries<Integer, Double> ds3 = sciChartBuilder.newXyDataSeries(Integer.class, Double.class).withSeriesName("SineWave C").build();

        double count = 100.0;
        double k = 2 * Math.PI / 30.0;
        for (int i = 0; i < (int) count; i++) {
            double phi = k * i;
            ds1.append(i, (1.0 + i / count) * Math.sin(phi));
            ds2.append(i, (0.5 + i / count) * Math.sin(phi));
            ds3.append(i, (i / count) * Math.sin(phi));
        }

        final FastLineRenderableSeries rs1 = sciChartBuilder.newLineSeries()
                .withDataSeries(ds1)
                .withStrokeStyle(ColorUtil.SteelBlue, 2)
                .withPointMarker(sciChartBuilder.newPointMarker(new EllipsePointMarker()).withSize(7, 7).withFill(ColorUtil.Lavender).build())
                .build();
        final FastLineRenderableSeries rs2 = sciChartBuilder.newLineSeries()
                .withDataSeries(ds2)
                .withStrokeStyle(ColorUtil.DarkGreen, 2)
                .withPointMarker(sciChartBuilder.newPointMarker(new EllipsePointMarker()).withSize(7, 7).withFill(ColorUtil.Lavender).build())
                .build();
        final FastLineRenderableSeries rs3 = sciChartBuilder.newLineSeries()
                .withDataSeries(ds3)
                .withStrokeStyle(ColorUtil.LightSteelBlue, 2, true)
                .build();

        UpdateSuspender.using(surface, () -> {
            Collections.addAll(surface.getXAxes(), xAxis);
            Collections.addAll(surface.getYAxes(), yAxis);
            Collections.addAll(surface.getRenderableSeries(), rs1, rs2, rs3);
            Collections.addAll(surface.getChartModifiers(), rolloverModifier);

            sciChartBuilder.newAnimator(rs1).withSweepTransformation().withInterpolator(new DecelerateInterpolator()).withDuration(2000).withStartDelay(350).start();
            sciChartBuilder.newAnimator(rs2).withSweepTransformation().withInterpolator(new DecelerateInterpolator()).withDuration(2000).withStartDelay(350).start();
            sciChartBuilder.newAnimator(rs3).withSweepTransformation().withInterpolator(new DecelerateInterpolator()).withDuration(2000).withStartDelay(350).start();
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
        final Dialog dialog = ViewSettingsUtil.createSettingsPopup(getActivity(), R.layout.example_using_rollover_modofier_tooltips_popup_layout);
        Context context = dialog.getContext();

        final SpinnerStringAdapter legendSourceAdapter = new SpinnerStringAdapter(context, EnumUtils.getEnumValuesArray(SourceMode.class));
        final Spinner legendSourceSpinner = (Spinner) dialog.findViewById(R.id.legend_source_spinner_rollover);
        legendSourceSpinner.setAdapter(legendSourceAdapter);
        legendSourceSpinner.setSelection(selectedSourceMode);
        legendSourceSpinner.setOnItemSelectedListener(new ItemSelectedListenerBase() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedSourceMode = position;
                rolloverModifier.setSourceMode(sourceModeValues.get(selectedSourceMode));
            }
        });

        ViewSettingsUtil.setUpSwitchCompat(dialog, R.id.show_tooltips_checkbox, showTooltip, (buttonView, isChecked) -> {
            showTooltip = isChecked;
            rolloverModifier.setShowTooltip(showTooltip);
        });

        ViewSettingsUtil.setUpSwitchCompat(dialog, R.id.show_axis_labels_checkbox, showAxisLabels, (buttonView, isChecked) -> {
            showAxisLabels = isChecked;
            rolloverModifier.setShowAxisLabels(showAxisLabels);
        });

        ViewSettingsUtil.setUpSwitchCompat(dialog, R.id.draw_vertical_line_checkbox, drawVerticalLine, (buttonView, isChecked) -> {
            drawVerticalLine = isChecked;
            rolloverModifier.setDrawVerticalLine(drawVerticalLine);
        });

        dialog.show();
    }
}