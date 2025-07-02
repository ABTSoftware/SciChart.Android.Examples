//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2021. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// PanAndZoomChartFragment.java is part of SCICHART®, High Performance Scientific Charts
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

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.animation.AccelerateDecelerateInterpolator;

import androidx.annotation.NonNull;

import com.scichart.charting.model.dataSeries.IXyDataSeries;
import com.scichart.charting.visuals.annotations.AnnotationCoordinateMode;
import com.scichart.charting.visuals.annotations.VerticalLineAnnotation;
import com.scichart.charting.visuals.axes.IAxis;
import com.scichart.charting.visuals.pointmarkers.EllipsePointMarker;
import com.scichart.charting.visuals.renderableSeries.FastLineRenderableSeries;
import com.scichart.charting.visuals.renderableSeries.SplineLineRenderableSeries;
import com.scichart.core.framework.UpdateSuspender;
import com.scichart.drawing.utility.ColorUtil;
import com.scichart.examples.databinding.ExampleZoomAndPanWithOverviewChartFragmentBinding;
import com.scichart.examples.fragments.base.ExampleBaseFragment;

import java.util.Collections;

public class ZoomAndPanWithOverviewChartFragment extends ExampleBaseFragment<ExampleZoomAndPanWithOverviewChartFragmentBinding> {

    //private static final int SECONDS_IN_FIVE_MINUTES = 5 * 60;

    @NonNull
    @Override
    protected ExampleZoomAndPanWithOverviewChartFragmentBinding inflateBinding(@NonNull LayoutInflater inflater) {
        return ExampleZoomAndPanWithOverviewChartFragmentBinding.inflate(inflater);
    }

    @Override
    protected void initExample(@NonNull ExampleZoomAndPanWithOverviewChartFragmentBinding binding) {
        final IAxis xAxis = sciChartBuilder.newNumericAxis().withGrowBy(0.1d, 0.1d).build();
        final IAxis yAxis = sciChartBuilder.newNumericAxis().withGrowBy(0.2d, 0.2d).build();

        final IXyDataSeries<Integer, Integer> dataSeries = sciChartBuilder.newXyDataSeries(Integer.class, Integer.class).build();
        final int[] yValues = {50, 35, 61, 58, 50, 50, 40, 53, 55, 23, 45, 12, 59, 60};
        for (int i = 0; i < yValues.length; i++) {
            dataSeries.append(i, yValues[i]);
        }

        final FastLineRenderableSeries lineSeries = sciChartBuilder.newLineSeries()
                .withDataSeries(dataSeries)
                .withPointMarker(sciChartBuilder.newPointMarker(new EllipsePointMarker()).withSize(7, 7).withStroke(0xFF50C7E0, 1).withFill(0xFFFFFFFF).build())
                .withStrokeStyle(0xFFF48420, 1f, true)
                .build();

        final SplineLineRenderableSeries rSeries = sciChartBuilder.newSplineLineSeries()
                .withDataSeries(dataSeries)
                .withStrokeStyle(0xFF50C7E0, 2f, true)
                .build();
        // Spline Line chart end

        UpdateSuspender.using(binding.surface, () -> {
            Collections.addAll(binding.surface.getXAxes(), xAxis);
            Collections.addAll(binding.surface.getYAxes(), yAxis);
            Collections.addAll(binding.surface.getRenderableSeries(), rSeries, lineSeries);
            //Collections.addAll(surface.getChartModifiers(), new RolloverModifier());
            binding.surface.getChartModifiers().add(sciChartBuilder.newModifierGroupWithDefaultModifiers().build());
            sciChartBuilder.newAnimator(rSeries).withSweepTransformation().withInterpolator(new AccelerateDecelerateInterpolator()).withDuration(1000L).withStartDelay(50L).start();
            sciChartBuilder.newAnimator(lineSeries).withSweepTransformation().withInterpolator(new AccelerateDecelerateInterpolator()).withDuration(1000L).withStartDelay(50L).start();
        });
        binding.overview.setParentSurface(binding.surface);
        binding.overview.setGrips(generateGrip(), generateGrip());
    }

    private VerticalLineAnnotation generateGrip() {
        return sciChartBuilder.newVerticalLineAnnotation()
                .withCoordinateMode(AnnotationCoordinateMode.RelativeY)
                .withVerticalGravity(Gravity.CENTER_VERTICAL)
                .withStroke(7, ColorUtil.Grey)
                .build();
    }

    private VerticalLineAnnotation generateGrip(float thickness, int color) {
        return sciChartBuilder.newVerticalLineAnnotation()
                .withCoordinateMode(AnnotationCoordinateMode.RelativeY)
                .withVerticalGravity(Gravity.CENTER_VERTICAL)
                .withStroke(thickness, color)
                .build();
    }

}