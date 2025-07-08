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

import androidx.annotation.NonNull;

import com.scichart.charting.model.dataSeries.XyDataSeries;
import com.scichart.charting.visuals.annotations.AnnotationCoordinateMode;
import com.scichart.charting.visuals.annotations.VerticalLineAnnotation;
import com.scichart.charting.visuals.axes.NumericAxis;
import com.scichart.charting.visuals.renderableSeries.FastColumnRenderableSeries;
import com.scichart.charting.visuals.renderableSeries.FastLineRenderableSeries;
import com.scichart.charting.visuals.renderableSeries.FastMountainRenderableSeries;
import com.scichart.core.framework.UpdateSuspender;
import com.scichart.drawing.common.SolidBrushStyle;
import com.scichart.drawing.common.SolidPenStyle;
import com.scichart.drawing.utility.ColorUtil;
import com.scichart.examples.data.DoubleSeries;
import com.scichart.examples.data.RandomWalkGenerator;
import com.scichart.examples.databinding.ExampleZoomAndPanWithOverviewChartFragmentBinding;
import com.scichart.examples.fragments.base.ExampleBaseFragment;

import java.util.Collections;

public class ZoomAndPanWithOverviewChartFragment extends ExampleBaseFragment<ExampleZoomAndPanWithOverviewChartFragmentBinding> {

    private static final int POINTS_COUNT = 1500;

    @NonNull
    @Override
    protected ExampleZoomAndPanWithOverviewChartFragmentBinding inflateBinding(@NonNull LayoutInflater inflater) {
        return ExampleZoomAndPanWithOverviewChartFragmentBinding.inflate(inflater);
    }

    @Override
    protected void initExample(@NonNull ExampleZoomAndPanWithOverviewChartFragmentBinding binding) {
        final RandomWalkGenerator randomWalkGenerator1 = new RandomWalkGenerator();
        final DoubleSeries data1 = randomWalkGenerator1.getRandomWalkSeries(POINTS_COUNT);

        final RandomWalkGenerator randomWalkGenerator2 = new RandomWalkGenerator();
        final DoubleSeries data2 = randomWalkGenerator2.getRandomWalkSeries(POINTS_COUNT);

        final RandomWalkGenerator randomWalkGenerator3 = new RandomWalkGenerator();
        final DoubleSeries data3 = randomWalkGenerator3.getRandomWalkSeries(POINTS_COUNT);

        XyDataSeries<Double, Double> ds1 = sciChartBuilder.newXyDataSeries(Double.class, Double.class).build();
        ds1.append(data1.xValues, data1.yValues);

        XyDataSeries<Double, Double> ds2 = sciChartBuilder.newXyDataSeries(Double.class, Double.class).build();
        ds2.append(data2.xValues, data2.yValues);

        XyDataSeries<Double, Double> ds3 = sciChartBuilder.newXyDataSeries(Double.class, Double.class).build();
        ds3.append(data3.xValues, data3.yValues);

        FastLineRenderableSeries fastLineRenderableSeries = sciChartBuilder.newLineSeries()
                .withDataSeries(ds1)
                .withStrokeStyle(new SolidPenStyle(0xFFFF70FF, true, 1f, null))
                .build();

        FastMountainRenderableSeries fastMountainRenderableSeries = sciChartBuilder.newMountainSeries()
                .withDataSeries(ds2)
                .withStrokeStyle(new SolidPenStyle(0xFFe9Fe64, true, 1f, null))
                .build();

        FastColumnRenderableSeries fastColumnRenderableSeries = sciChartBuilder.newColumnSeries()
                .withDataSeries(ds3)
                .withStrokeStyle(new SolidPenStyle(0xFFe97064, true, 1f, null))
                .withFillColor(0xFFe97064)
                .build();

        NumericAxis xAxis = new NumericAxis(getContext());
        NumericAxis yAxis = new NumericAxis(getContext());

        UpdateSuspender.using(binding.surface, () -> {
            Collections.addAll(binding.surface.getXAxes(), xAxis);
            Collections.addAll(binding.surface.getYAxes(), yAxis);
            Collections.addAll(binding.surface.getRenderableSeries(), fastLineRenderableSeries, fastColumnRenderableSeries, fastMountainRenderableSeries);
            //Collections.addAll(surface.getChartModifiers(), new RolloverModifier());
            binding.surface.getChartModifiers().add(sciChartBuilder.newModifierGroupWithDefaultModifiers().build());
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