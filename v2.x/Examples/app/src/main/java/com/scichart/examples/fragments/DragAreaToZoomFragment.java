//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2017. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// DragAreaToZoomFragment.java is part of the SCICHART® Examples. Permission is hereby granted
// to modify, create derivative works, distribute and publish any part of this source
// code whether for commercial, private or personal use.
//
// The SCICHART® examples are distributed in the hope that they will be useful, but
// without any warranty. It is provided "AS IS" without warranty of any kind, either
// expressed or implied.
//******************************************************************************

package com.scichart.examples.fragments;

import android.app.Dialog;
import android.view.View;
import android.widget.CompoundButton;

import com.scichart.charting.model.dataSeries.IXyDataSeries;
import com.scichart.charting.modifiers.RubberBandXyZoomModifier;
import com.scichart.charting.visuals.SciChartSurface;
import com.scichart.charting.visuals.axes.IAxis;
import com.scichart.charting.visuals.renderableSeries.FastLineRenderableSeries;
import com.scichart.core.framework.UpdateSuspender;
import com.scichart.data.model.DoubleRange;
import com.scichart.drawing.utility.ColorUtil;
import com.scichart.examples.R;
import com.scichart.examples.data.DoubleSeries;
import com.scichart.examples.data.RandomWalkGenerator;
import com.scichart.examples.fragments.base.ExampleBaseFragment;
import com.scichart.examples.utils.ViewSettingsUtil;
import com.scichart.examples.utils.widgetgeneration.ImageViewWidget;
import com.scichart.examples.utils.widgetgeneration.Widget;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.Bind;

public class DragAreaToZoomFragment extends ExampleBaseFragment {

    private RubberBandXyZoomModifier rubberBandXyZoomModifier;

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
    public boolean showDefaultModifiersInToolbar() {
        return false;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.example_single_chart_fragment;
    }

    @Override
    protected void initExample() {
        DoubleSeries data = new RandomWalkGenerator(0).setBias(0.0001).getRandomWalkSeries(10000);

        final IXyDataSeries<Double, Double> dataSeries = sciChartBuilder.newXyDataSeries(Double.class, Double.class).build();
        dataSeries.append(data.xValues, data.yValues);

        final IAxis xAxis = sciChartBuilder.newNumericAxis().build();
        final IAxis yAxis = sciChartBuilder.newNumericAxis().withGrowBy(new DoubleRange(0.1d, 0.1d)).build();

        final FastLineRenderableSeries lineSeries = sciChartBuilder.newLineSeries().withDataSeries(dataSeries).withStrokeStyle(ColorUtil.argb(255, 9, 68, 27)).build();

        rubberBandXyZoomModifier = new RubberBandXyZoomModifier();
        rubberBandXyZoomModifier.setIsXAxisOnly(true);
        rubberBandXyZoomModifier.setReceiveHandledEvents(true);

        UpdateSuspender.using(surface, new Runnable() {
            @Override
            public void run() {
                Collections.addAll(surface.getXAxes(), xAxis);
                Collections.addAll(surface.getYAxes(), yAxis);
                Collections.addAll(surface.getRenderableSeries(), lineSeries);
                Collections.addAll(surface.getChartModifiers(), sciChartBuilder.newModifierGroup()
                        .withZoomExtentsModifier().build()
                        .withModifier(rubberBandXyZoomModifier)
                        .build());
            }
        });
    }

    private void openSettingsDialog() {
        final Dialog dialog = ViewSettingsUtil.createSettingsPopup(getActivity(), R.layout.example_drag_area_to_zoom_popop_layout);

        ViewSettingsUtil.setUpSwitchCompat(dialog, R.id.zoom_x_axis_only_checkbox, rubberBandXyZoomModifier.getIsXAxisOnly(), new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                rubberBandXyZoomModifier.setIsXAxisOnly(isChecked);
            }
        });

        ViewSettingsUtil.setUpSwitchCompat(dialog, R.id.zoom_extents_y_axis_checkbox, rubberBandXyZoomModifier.getZoomExtentsY(), new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                rubberBandXyZoomModifier.setZoomExtentsY(isChecked);
            }
        });

        ViewSettingsUtil.setUpSwitchCompat(dialog, R.id.use_animation_checkbox, rubberBandXyZoomModifier.getIsAnimated(), new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                rubberBandXyZoomModifier.setIsAnimated(isChecked);
            }
        });

        dialog.show();
    }
}