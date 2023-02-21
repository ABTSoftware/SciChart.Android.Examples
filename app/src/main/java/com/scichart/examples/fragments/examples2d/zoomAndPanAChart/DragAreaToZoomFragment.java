//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2021. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// DragAreaToZoomFragment.java is part of SCICHART®, High Performance Scientific Charts
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

import android.app.Dialog;
import android.view.animation.DecelerateInterpolator;

import androidx.annotation.NonNull;

import com.scichart.charting.model.dataSeries.IXyDataSeries;
import com.scichart.charting.modifiers.RubberBandXyZoomModifier;
import com.scichart.charting.visuals.SciChartSurface;
import com.scichart.charting.visuals.axes.IAxis;
import com.scichart.charting.visuals.renderableSeries.FastLineRenderableSeries;
import com.scichart.core.framework.UpdateSuspender;
import com.scichart.data.model.DoubleRange;
import com.scichart.examples.R;
import com.scichart.examples.data.DoubleSeries;
import com.scichart.examples.data.RandomWalkGenerator;
import com.scichart.examples.fragments.base.ExampleSingleChartBaseFragment;
import com.scichart.examples.utils.Constant;
import com.scichart.examples.utils.ViewSettingsUtil;
import com.scichart.examples.utils.interpolator.DefaultInterpolator;
import com.scichart.examples.utils.widgetgeneration.ImageViewWidget;
import com.scichart.examples.utils.widgetgeneration.Widget;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DragAreaToZoomFragment extends ExampleSingleChartBaseFragment {
    private RubberBandXyZoomModifier rubberBandXyZoomModifier;

    @Override
    public boolean showDefaultModifiersInToolbar() {
        return false;
    }

    @NonNull
    @Override
    public List<Widget> getToolbarItems() {
        return new ArrayList<Widget>() {{
                add(new ImageViewWidget.Builder().setId(R.drawable.example_toolbar_settings).setListener(v -> openSettingsDialog()).build());
        }};
    }

    @Override
    protected void initExample(@NonNull SciChartSurface surface) {
        surface.setTheme(R.style.SciChart_NavyBlue);

        final IAxis xAxis = sciChartBuilder.newNumericAxis().build();
        final IAxis yAxis = sciChartBuilder.newNumericAxis().withGrowBy(new DoubleRange(0.1d, 0.1d)).build();

        DoubleSeries data = new RandomWalkGenerator(0).setBias(0.0001).getRandomWalkSeries(10000);
        final IXyDataSeries<Double, Double> dataSeries = sciChartBuilder.newXyDataSeries(Double.class, Double.class).build();
        dataSeries.append(data.xValues, data.yValues);

        final FastLineRenderableSeries rSeries = sciChartBuilder.newLineSeries().withDataSeries(dataSeries).withStrokeStyle(0xFF47bde6, 1, true).build();

        rubberBandXyZoomModifier = new RubberBandXyZoomModifier();
        rubberBandXyZoomModifier.setIsXAxisOnly(true);
        rubberBandXyZoomModifier.setReceiveHandledEvents(true);

        UpdateSuspender.using(surface, () -> {
            Collections.addAll(surface.getXAxes(), xAxis);
            Collections.addAll(surface.getYAxes(), yAxis);
            Collections.addAll(surface.getRenderableSeries(), rSeries);
            Collections.addAll(surface.getChartModifiers(), sciChartBuilder.newModifierGroup()
                    .withZoomExtentsModifier().build()
                    .withModifier(rubberBandXyZoomModifier)
                    .build());

            sciChartBuilder.newAnimator(rSeries).withSweepTransformation().withInterpolator(DefaultInterpolator.getInterpolator()).withDuration(Constant.ANIMATION_DURATION).withStartDelay(Constant.ANIMATION_START_DELAY).start();
        });
    }

    private void openSettingsDialog() {
        final Dialog dialog = ViewSettingsUtil.createSettingsPopup(getActivity(), R.layout.example_drag_area_to_zoom_popop_layout);

        ViewSettingsUtil.setUpSwitchCompat(dialog, R.id.zoom_x_axis_only_checkbox, rubberBandXyZoomModifier.getIsXAxisOnly(), (buttonView, isChecked) -> rubberBandXyZoomModifier.setIsXAxisOnly(isChecked));
        ViewSettingsUtil.setUpSwitchCompat(dialog, R.id.zoom_extents_y_axis_checkbox, rubberBandXyZoomModifier.getZoomExtentsY(), (buttonView, isChecked) -> rubberBandXyZoomModifier.setZoomExtentsY(isChecked));
        ViewSettingsUtil.setUpSwitchCompat(dialog, R.id.use_animation_checkbox, rubberBandXyZoomModifier.getIsAnimated(), (buttonView, isChecked) -> rubberBandXyZoomModifier.setIsAnimated(isChecked));

        dialog.show();
    }
}