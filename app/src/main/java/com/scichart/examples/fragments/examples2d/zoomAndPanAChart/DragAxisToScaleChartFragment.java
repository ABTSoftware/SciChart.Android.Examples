//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2021. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// DragAxisToScaleChartFragment.java is part of SCICHART®, High Performance Scientific Charts
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

package com.scichart.examples.fragments.examples2d.zoomAndPanAChart;

import static com.scichart.charting.modifiers.AxisDragModifierBase.AxisDragMode;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.Spinner;

import androidx.annotation.NonNull;

import com.scichart.charting.ClipMode;
import com.scichart.charting.Direction2D;
import com.scichart.charting.model.dataSeries.XyDataSeries;
import com.scichart.charting.modifiers.ModifierGroup;
import com.scichart.charting.modifiers.XAxisDragModifier;
import com.scichart.charting.modifiers.YAxisDragModifier;
import com.scichart.charting.visuals.SciChartSurface;
import com.scichart.charting.visuals.axes.AxisAlignment;
import com.scichart.charting.visuals.axes.IAxis;
import com.scichart.charting.visuals.renderableSeries.FastLineRenderableSeries;
import com.scichart.charting.visuals.renderableSeries.FastMountainRenderableSeries;
import com.scichart.core.framework.UpdateSuspender;
import com.scichart.examples.R;
import com.scichart.examples.components.SpinnerStringAdapter;
import com.scichart.examples.data.DataManager;
import com.scichart.examples.data.DoubleSeries;
import com.scichart.examples.fragments.base.ExampleSingleChartBaseFragment;
import com.scichart.examples.utils.Constant;
import com.scichart.examples.utils.EnumUtils;
import com.scichart.examples.utils.ItemSelectedListenerBase;
import com.scichart.examples.utils.ViewSettingsUtil;
import com.scichart.examples.utils.interpolator.CubicInOutInterpolator;
import com.scichart.examples.utils.interpolator.DefaultInterpolator;
import com.scichart.examples.utils.widgetgeneration.ImageViewWidget;
import com.scichart.examples.utils.widgetgeneration.Widget;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DragAxisToScaleChartFragment extends ExampleSingleChartBaseFragment {

    private XAxisDragModifier xAxisDragModifier;
    private YAxisDragModifier yAxisDragModifier;

    private AxisDragMode selectedDragMode = AxisDragMode.Scale;
    private Direction2D selectedDirection = Direction2D.XyDirection;

    @NonNull
    @Override
    public List<Widget> getToolbarItems() {
        return new ArrayList<Widget>() {{
                add(new ImageViewWidget.Builder().setId(R.drawable.example_toolbar_settings).setListener(v -> openSettingsDialog()).build());
        }};
    }

    @Override
    protected void initExample(@NonNull SciChartSurface surface) {
        final IAxis xAxis = sciChartBuilder.newNumericAxis().withTextFormatting("0.0").withAxisAlignment(AxisAlignment.Top).withGrowBy(0.1, 0.1).withVisibleRange(3, 6).build();
        final IAxis rightYAxis = sciChartBuilder.newNumericAxis().withAxisId("RightAxisId").withAxisAlignment(AxisAlignment.Right).withTextColor(0xFF47bde6).withGrowBy(0.1, 0.1).build();
        final IAxis leftYAxis = sciChartBuilder.newNumericAxis().withAxisId("LeftAxisId").withAxisAlignment(AxisAlignment.Left).withTextColor(0xFFae418d).withGrowBy(0.1, 0.1).build();

        final DoubleSeries fourierSeries = DataManager.getInstance().getFourierSeries(1.0, 0.1, 5000);
        final DoubleSeries dampedSinewave = DataManager.getInstance().getDampedSinewave(1500, 3.0, 0.0, 0.005, 5000, 10);

        final XyDataSeries<Double, Double> mountainDS = sciChartBuilder.newXyDataSeries(Double.class, Double.class).build();
        final XyDataSeries<Double, Double> lineDS = sciChartBuilder.newXyDataSeries(Double.class, Double.class).build();

        mountainDS.append(fourierSeries.xValues, fourierSeries.yValues);
        lineDS.append(dampedSinewave.xValues, dampedSinewave.yValues);

        final FastMountainRenderableSeries mountainRenderableSeries = sciChartBuilder.newMountainSeries().withDataSeries(mountainDS).withAreaFillColor(0x77ae418d).withStrokeStyle(0xFFc43360, 2f, true).withYAxisId("LeftAxisId").build();
        final FastLineRenderableSeries lineRenderableSeries = sciChartBuilder.newLineSeries().withDataSeries(lineDS).withStrokeStyle(0xFF47bde6, 2f, true).withYAxisId("RightAxisId").build();

        UpdateSuspender.using(surface, () -> {
            Collections.addAll(surface.getXAxes(), xAxis);
            Collections.addAll(surface.getYAxes(), leftYAxis, rightYAxis);
            Collections.addAll(surface.getRenderableSeries(), mountainRenderableSeries, lineRenderableSeries);

            xAxisDragModifier = new XAxisDragModifier();
            xAxisDragModifier.setClipModeX(ClipMode.None);

            yAxisDragModifier = new YAxisDragModifier();

            ModifierGroup modifiers = sciChartBuilder.newModifierGroup()
                    .withModifier(xAxisDragModifier)
                    .withModifier(yAxisDragModifier)
                    .withZoomPanModifier().withReceiveHandledEvents(true).build()
                    .withZoomExtentsModifier().build()
                    .build();

            Collections.addAll(surface.getChartModifiers(), modifiers);

            sciChartBuilder.newAnimator(lineRenderableSeries).withSweepTransformation().withInterpolator(DefaultInterpolator.getInterpolator()).withDuration(Constant.ANIMATION_DURATION).withStartDelay(Constant.ANIMATION_START_DELAY).start();
            sciChartBuilder.newAnimator(mountainRenderableSeries).withScaleTransformation().withInterpolator(DefaultInterpolator.getInterpolator()).withDuration(Constant.ANIMATION_DURATION).withStartDelay(Constant.ANIMATION_START_DELAY).start();
        });
    }

    private void openSettingsDialog() {
        final Dialog dialog = ViewSettingsUtil.createSettingsPopup(getActivity(), R.layout.example_drag_axis_to_scale_chart_layout_popup);
        Context context = dialog.getContext();

        final SpinnerStringAdapter axisDragModeAdapter = new SpinnerStringAdapter(context, EnumUtils.getEnumValuesArray(AxisDragMode.class));
        final Spinner axisDragModeSpinner = (Spinner) dialog.findViewById(R.id.axis_drag_mode_spinner);
        axisDragModeSpinner.setAdapter(axisDragModeAdapter);
        axisDragModeSpinner.setSelection(axisDragModeAdapter.getPosition(String.valueOf(selectedDragMode)));
        axisDragModeSpinner.setOnItemSelectedListener(new ItemSelectedListenerBase() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedDragMode = AxisDragMode.valueOf(axisDragModeAdapter.getItem(position));
                xAxisDragModifier.setDragMode(selectedDragMode);
                yAxisDragModifier.setDragMode(selectedDragMode);
            }
        });

        final SpinnerStringAdapter xyDirectionAdapter = new SpinnerStringAdapter(context, EnumUtils.getEnumValuesArray(Direction2D.class));
        final Spinner directionSpinner = (Spinner) dialog.findViewById(R.id.direction_spinner);
        directionSpinner.setAdapter(xyDirectionAdapter);
        directionSpinner.setSelection(xyDirectionAdapter.getPosition(String.valueOf(selectedDirection)));
        directionSpinner.setOnItemSelectedListener(new ItemSelectedListenerBase() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedDirection = Direction2D.valueOf(xyDirectionAdapter.getItem(position));
                updateAxesDragDirections(selectedDirection);
            }
        });

        dialog.show();
    }

    private void updateAxesDragDirections(Direction2D direction) {
        switch (direction) {
            case XDirection:
                xAxisDragModifier.setIsEnabled(true);
                yAxisDragModifier.setIsEnabled(false);
                break;
            case YDirection:
                xAxisDragModifier.setIsEnabled(false);
                yAxisDragModifier.setIsEnabled(true);
                break;
            case XyDirection:
                xAxisDragModifier.setIsEnabled(true);
                yAxisDragModifier.setIsEnabled(true);
                break;
        }
    }
}