//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2021. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// LegendFragment.java is part of SCICHART®, High Performance Scientific Charts
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

package com.scichart.examples.fragments.examples2d.legends;

import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableList;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.Spinner;

import androidx.annotation.NonNull;

import com.scichart.charting.model.dataSeries.XyDataSeries;
import com.scichart.charting.modifiers.LegendModifier;
import com.scichart.charting.modifiers.ModifierGroup;
import com.scichart.charting.modifiers.SeriesSelectionModifier;
import com.scichart.charting.modifiers.SourceMode;
import com.scichart.charting.visuals.SciChartSurface;
import com.scichart.charting.visuals.renderableSeries.FastLineRenderableSeries;
import com.scichart.charting.visuals.renderableSeries.IRenderableSeries;
import com.scichart.charting.visuals.renderableSeries.StyleBase;
import com.scichart.core.annotations.Orientation;
import com.scichart.core.framework.UpdateSuspender;
import com.scichart.drawing.common.PenStyle;
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

public class LegendFragment extends ExampleSingleChartBaseFragment {
    private static final List<SourceMode> sourceModeValues = unmodifiableList(asList(SourceMode.values()));
    private LegendModifier legendModifier;

    private int selectedOrientation = Orientation.VERTICAL;
    private int selectedSourceMode = 0;
    private boolean showLegend = true;
    private boolean showCheckBoxes = true;
    private boolean showSeriesMarkers = true;

    @NonNull
    @Override
    public List<Widget> getToolbarItems() {
        return new ArrayList<Widget>() {
            {
                add(new ImageViewWidget.Builder().setId(R.drawable.example_toolbar_settings).setListener(v -> openSettingsDialog()).build());
            }
        };
    }

    @Override
    protected void initExample(@NonNull SciChartSurface surface) {
        final XyDataSeries<Double, Double> dataSeries1 = sciChartBuilder.newXyDataSeries(Double.class, Double.class).withSeriesName("Curve A").build();
        final XyDataSeries<Double, Double> dataSeries2 = sciChartBuilder.newXyDataSeries(Double.class, Double.class).withSeriesName("Curve B").build();
        final XyDataSeries<Double, Double> dataSeries3 = sciChartBuilder.newXyDataSeries(Double.class, Double.class).withSeriesName("Curve C").build();
        final XyDataSeries<Double, Double> dataSeries4 = sciChartBuilder.newXyDataSeries(Double.class, Double.class).withSeriesName("Curve D").build();

        final DoubleSeries ds1Points = DataManager.getInstance().getStraightLine(4000, 1.0, 10);
        final DoubleSeries ds2Points = DataManager.getInstance().getStraightLine(3000, 1.0, 10);
        final DoubleSeries ds3Points = DataManager.getInstance().getStraightLine(2000, 1.0, 10);
        final DoubleSeries ds4Points = DataManager.getInstance().getStraightLine(1000, 1.0, 10);

        dataSeries1.append(ds1Points.xValues, ds1Points.yValues);
        dataSeries2.append(ds2Points.xValues, ds2Points.yValues);
        dataSeries3.append(ds3Points.xValues, ds3Points.yValues);
        dataSeries4.append(ds4Points.xValues, ds4Points.yValues);

        final int line1Color = ColorUtil.argb(0xFF, 0x47, 0xBD, 0xE6);
        final int line2Color = ColorUtil.argb(0xFF, 0xAE, 0x41, 0x8D);
        final int line3Color = ColorUtil.argb(0xFF, 0x68, 0xBC, 0xA8);
        final int line4Color = ColorUtil.argb(0xFF, 0xE9, 0x70, 0x64);

        final FastLineRenderableSeries line1 = sciChartBuilder.newLineSeries().withStrokeStyle(line1Color, 1f, true).withDataSeries(dataSeries1).build();
        final FastLineRenderableSeries line2 = sciChartBuilder.newLineSeries().withStrokeStyle(line2Color, 1f, true).withDataSeries(dataSeries2).build();
        final FastLineRenderableSeries line3 = sciChartBuilder.newLineSeries().withStrokeStyle(line3Color, 1f, true).withDataSeries(dataSeries3).build();
        final FastLineRenderableSeries line4 = sciChartBuilder.newLineSeries().withStrokeStyle(line4Color, 1f, true).withDataSeries(dataSeries4).withIsVisible(false).build();

        UpdateSuspender.using(surface, () -> {
            final SeriesSelectionModifier seriesSelectionModifier = new SeriesSelectionModifier();
            seriesSelectionModifier.setSelectedSeriesStyle(new StyleBase<IRenderableSeries>(IRenderableSeries.class) {
                @Override
                protected void applyStyleInternal(IRenderableSeries renderableSeriesToStyle) {
                    final PenStyle currentStrokeStyle = renderableSeriesToStyle.getStrokeStyle();
                    putPropertyValue(renderableSeriesToStyle, "Stroke", currentStrokeStyle);

                    final PenStyle newStrokeStyle = sciChartBuilder.newPen().withColor(currentStrokeStyle.getColor()).withThickness(3).build();
                    renderableSeriesToStyle.setStrokeStyle(newStrokeStyle);
                }

                @Override
                protected void discardStyleInternal(IRenderableSeries renderableSeriesToStyle) {
                    final PenStyle stroke = getPropertyValue(renderableSeriesToStyle, "Stroke", PenStyle.class);
                    renderableSeriesToStyle.setStrokeStyle(stroke);
                }
            });

            ModifierGroup modifierGroup = sciChartBuilder.newModifierGroup()
                    .withLegendModifier().withPosition(Gravity.TOP | Gravity.START, 16).withSourceMode(sourceModeValues.get(selectedSourceMode)).withOrientation(selectedOrientation).build()
                    .withModifier(seriesSelectionModifier)
                    .build();
            legendModifier = (LegendModifier) modifierGroup.getChildModifiers().get(0);

            Collections.addAll(surface.getXAxes(), sciChartBuilder.newNumericAxis().build());
            Collections.addAll(surface.getYAxes(), sciChartBuilder.newNumericAxis().build());
            Collections.addAll(surface.getRenderableSeries(), line1, line2, line3, line4);
            Collections.addAll(surface.getChartModifiers(), modifierGroup);

            sciChartBuilder.newAnimator(line1).withSweepTransformation().withInterpolator(new DecelerateInterpolator()).withDuration(3000).withStartDelay(350).start();
            sciChartBuilder.newAnimator(line2).withSweepTransformation().withInterpolator(new DecelerateInterpolator()).withDuration(3000).withStartDelay(350).start();
            sciChartBuilder.newAnimator(line3).withSweepTransformation().withInterpolator(new DecelerateInterpolator()).withDuration(3000).withStartDelay(350).start();
            sciChartBuilder.newAnimator(line4).withSweepTransformation().withInterpolator(new DecelerateInterpolator()).withDuration(3000).withStartDelay(350).start();
        });
    }

    private void openSettingsDialog() {
        final Dialog dialog = ViewSettingsUtil.createSettingsPopup(getActivity(), R.layout.example_chart_legend_popup_layout);
        Context context = dialog.getContext();

        final SpinnerStringAdapter legendOrientationAdapter = new SpinnerStringAdapter(context, R.array.legend_orientation);
        final Spinner legendOrientationSpinner = dialog.findViewById(R.id.legend_orientation_spinner);
        legendOrientationSpinner.setAdapter(legendOrientationAdapter);
        legendOrientationSpinner.setSelection(selectedOrientation);
        legendOrientationSpinner.setOnItemSelectedListener(new ItemSelectedListenerBase() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedOrientation = position == 0 ? Orientation.HORIZONTAL : Orientation.VERTICAL;
                legendModifier.setOrientation(selectedOrientation);
            }
        });

        ViewSettingsUtil.setUpSwitchCompat(dialog, R.id.show_legend_checkbox, showLegend, (buttonView, isChecked) -> {
            showLegend = isChecked;
            legendModifier.setShowLegend(showLegend);
        });

        ViewSettingsUtil.setUpSwitchCompat(dialog, R.id.show_checkboxes_checkbox, showCheckBoxes, (buttonView, isChecked) -> {
            showCheckBoxes = isChecked;
            legendModifier.setShowCheckboxes(showCheckBoxes);
        });

        ViewSettingsUtil.setUpSwitchCompat(dialog, R.id.show_series_markers_checkbox, showSeriesMarkers, (buttonView, isChecked) -> {
            showSeriesMarkers = isChecked;
            legendModifier.setShowSeriesMarkers(showSeriesMarkers);
        });

        final SpinnerStringAdapter legendSourceAdapter = new SpinnerStringAdapter(context, EnumUtils.getEnumValuesArray(SourceMode.class));
        final Spinner legendSourceSpinner = dialog.findViewById(R.id.legend_source_spinner);
        legendSourceSpinner.setAdapter(legendSourceAdapter);
        legendSourceSpinner.setSelection(selectedSourceMode);
        legendSourceSpinner.setOnItemSelectedListener(new ItemSelectedListenerBase() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedSourceMode = position;
                legendModifier.setSourceMode(sourceModeValues.get(selectedSourceMode));
            }
        });

        dialog.show();
    }
}