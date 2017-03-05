//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2017. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// DragAxisToScaleChartFragment.java is part of the SCICHART® Examples. Permission is hereby granted
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
import android.widget.Spinner;

import com.scichart.charting.ClipMode;
import com.scichart.charting.Direction2D;
import com.scichart.charting.model.dataSeries.XyDataSeries;
import com.scichart.charting.modifiers.ModifierGroup;
import com.scichart.charting.modifiers.XAxisDragModifier;
import com.scichart.charting.modifiers.YAxisDragModifier;
import com.scichart.charting.visuals.SciChartSurface;
import com.scichart.charting.visuals.axes.AxisAlignment;
import com.scichart.charting.visuals.axes.NumericAxis;
import com.scichart.charting.visuals.renderableSeries.FastLineRenderableSeries;
import com.scichart.charting.visuals.renderableSeries.FastMountainRenderableSeries;
import com.scichart.core.framework.UpdateSuspender;
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

import static com.scichart.charting.modifiers.AxisDragModifierBase.AxisDragMode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.Bind;

public class DragAxisToScaleChartFragment extends ExampleBaseFragment {

    private XAxisDragModifier xAxisDragModifier;
    private YAxisDragModifier yAxisDragModifier;

    private AxisDragMode selectedDragMode = AxisDragMode.Scale;
    private Direction2D selectedDirection = Direction2D.XyDirection;

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
        final NumericAxis xAxis = sciChartBuilder.newNumericAxis().withTextFormatting("0.0").withGrowBy(0.1, 0.1).withVisibleRange(3, 6).build();
        final NumericAxis rightYAxis = sciChartBuilder.newNumericAxis().withAxisId("RightAxisId").withAxisAlignment(AxisAlignment.Right).withTextColor(0xFF279B27).withGrowBy(0.1, 0.1).build();
        final NumericAxis leftYAxis = sciChartBuilder.newNumericAxis().withAxisId("LeftAxisId").withAxisAlignment(AxisAlignment.Left).withTextColor(0xFF4083B7).withGrowBy(0.1, 0.1).build();

        final XyDataSeries<Double, Double> mountainDS = sciChartBuilder.newXyDataSeries(Double.class, Double.class).build();
        final XyDataSeries<Double, Double> lineDS = sciChartBuilder.newXyDataSeries(Double.class, Double.class).build();

        final FastMountainRenderableSeries mountainRenderableSeries = sciChartBuilder.newMountainSeries().withDataSeries(mountainDS).withAreaFillColor(0x771964FF).withStrokeStyle(0xFF0944CF).withYAxisId("LeftAxisId").build();
        final FastLineRenderableSeries lineRenderableSeries = sciChartBuilder.newLineSeries().withDataSeries(lineDS).withStrokeStyle(0xFF279B27, 2f).withYAxisId("RightAxisId").build();

        final DoubleSeries fourierSeries = DataManager.getInstance().getFourierSeries(1.0, 0.1, 5000);
        final DoubleSeries dampedSinewave = DataManager.getInstance().getDampedSinewave(1500, 3.0, 0.0, 0.005, 5000, 10);

        mountainDS.append(fourierSeries.xValues, fourierSeries.yValues);
        lineDS.append(dampedSinewave.xValues, dampedSinewave.yValues);

        UpdateSuspender.using(surface, new Runnable() {
            @Override
            public void run() {
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
            }
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
