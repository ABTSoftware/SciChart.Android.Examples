//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2021. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// MultipleChartsFragment.java is part of SCICHART®, High Performance Scientific Charts
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

package com.scichart.examples.fragments.examples2d.multiChart;

import android.content.res.Configuration;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.animation.DecelerateInterpolator;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;

import com.scichart.charting.model.dataSeries.IXyDataSeries;
import com.scichart.charting.model.dataSeries.XyDataSeries;
import com.scichart.charting.visuals.SciChartSurface;
import com.scichart.charting.visuals.annotations.HorizontalAnchorPoint;
import com.scichart.charting.visuals.annotations.TextAnnotation;
import com.scichart.charting.visuals.annotations.VerticalAnchorPoint;
import com.scichart.charting.visuals.axes.IAxis;
import com.scichart.charting.visuals.pointmarkers.EllipsePointMarker;
import com.scichart.charting.visuals.renderableSeries.FastLineRenderableSeries;
import com.scichart.charting.visuals.renderableSeries.hitTest.DefaultXySeriesInfoProvider;
import com.scichart.core.framework.UpdateSuspender;
import com.scichart.data.model.DoubleRange;
import com.scichart.data.model.IRange;
import com.scichart.drawing.common.SolidBrushStyle;
import com.scichart.examples.R;
import com.scichart.examples.databinding.ExampleMultipleChartsFragmentBinding;
import com.scichart.examples.fragments.base.ExampleBaseFragment;
import com.scichart.examples.utils.Constant;
import com.scichart.examples.utils.interpolator.DefaultInterpolator;

import java.util.Collections;

public class MultipleChartsFragment extends ExampleBaseFragment<ExampleMultipleChartsFragmentBinding> {
    private final static int POINTS_COUNT = 100;

    private final IRange<?> sharedXRange = new DoubleRange();
    private final IRange<?> sharedYRange = new DoubleRange();

    @Override
    public boolean showDefaultModifiersInToolbar() {
        return false;
    }

    @NonNull
    @Override
    protected ExampleMultipleChartsFragmentBinding inflateBinding(@NonNull LayoutInflater inflater) {
        return ExampleMultipleChartsFragmentBinding.inflate(inflater);
    }

    @Override
    protected void initExample(ExampleMultipleChartsFragmentBinding binding) {
        int orientation = getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            binding.multiChartLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
        } else {
            binding.multiChartLinearLayout.setOrientation(LinearLayout.VERTICAL);
        }

        initChart0(binding.chart0);
        initChart1(binding.chart1);
        initChart2(binding.chart2);
    }

    private void initChart0(final SciChartSurface surface) {
        surface.setTheme(R.style.SciChart_NavyBlue);

        final IAxis xAxis = sciChartBuilder.newNumericAxis().withGrowBy(0.1d, 0.1d).build();
        final IAxis yAxis = sciChartBuilder.newNumericAxis().withGrowBy(0.1d, 0.1d).build();

        IXyDataSeries<Double, Double> dataSeries = new XyDataSeries<>(Double.class, Double.class);
        for (int i = 1; i < POINTS_COUNT; i++) {
            dataSeries.append((double) i, Math.asin(i * 0.01) + Math.sin(i * Math.PI * 0.1));
        }

        final FastLineRenderableSeries line = sciChartBuilder.newLineSeries().withDataSeries(dataSeries).withStrokeStyle(0xFF47bde6, 1f, true).build();

        TextAnnotation titleAnnotation = sciChartBuilder.newTextAnnotation()
                .withText("Simple Line Chart")
                .withPosition(50d,2d)
                .withFontStyle(12, Color.WHITE)
                .withVerticalAnchorPoint(VerticalAnchorPoint.Center)
                .withHorizontalAnchorPoint(HorizontalAnchorPoint.Center)
                .build();

        UpdateSuspender.using(surface, () -> {
            Collections.addAll(surface.getXAxes(), xAxis);
            Collections.addAll(surface.getYAxes(), yAxis);
            Collections.addAll(surface.getRenderableSeries(), line);
            Collections.addAll(surface.getAnnotations(), titleAnnotation);

            surface.zoomExtents();

            sciChartBuilder.newAnimator(line).withSweepTransformation().withInterpolator(DefaultInterpolator.getInterpolator()).withDuration(Constant.ANIMATION_DURATION).withStartDelay(Constant.ANIMATION_START_DELAY).start();
        });
    }

    private void initChart1(final SciChartSurface surface) {
        surface.setTheme(R.style.SciChart_NavyBlue);

        final IAxis xAxis = sciChartBuilder.newNumericAxis().withGrowBy(0.1d, 0.1d).build();
        final IAxis yAxis = sciChartBuilder.newNumericAxis().withGrowBy(0.1d, 0.1d).build();

        int[] xValues = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24};
        Double[] yValues = {0.3933834,
                -0.0493884,
                0.4083136,
                -0.0458077,
                -0.5242618,
                -0.9631066,
                -0.6873195,
                0.0,
                -0.1682597,
                0.1255406,
                -0.0313127,
                -0.3261995,
                -0.5490017,
                -0.2462973,
                0.2475873,
                0.15,
                -0.2443795,
                -0.7002707,
                0.0,
                -1.24664,
                -0.8722853,
                -1.1531512,
                -0.7264951,
                -0.9779677,
                -0.5377044};

        IXyDataSeries<Double, Double> dataSeries = new XyDataSeries<>(Double.class, Double.class);
        for (int i = 1; i < xValues.length; i++) {
            dataSeries.append((double) xValues[i], (Double) yValues[i]);
        }
        final FastLineRenderableSeries line = sciChartBuilder.newLineSeries().withDataSeries(dataSeries).withSeriesInfoProvider(new DefaultXySeriesInfoProvider()).build();

        TextAnnotation titleAnnotation = sciChartBuilder.newTextAnnotation()
                .withText("Tooltips on Line Chart")
                .withPosition(12d,0.4d)
                .withFontStyle(12, Color.WHITE)
                .withVerticalAnchorPoint(VerticalAnchorPoint.Center)
                .withHorizontalAnchorPoint(HorizontalAnchorPoint.Center)
                .build();

        UpdateSuspender.using(surface, () -> {
            Collections.addAll(surface.getXAxes(), xAxis);
            Collections.addAll(surface.getYAxes(), yAxis);
            Collections.addAll(surface.getRenderableSeries(), line);
            Collections.addAll(surface.getAnnotations(), titleAnnotation);
            Collections.addAll(surface.getChartModifiers(), sciChartBuilder.newModifierGroup()
                    .withRolloverModifier().build()
                    .build());

            surface.zoomExtents();

            sciChartBuilder.newAnimator(line).withSweepTransformation().withInterpolator(DefaultInterpolator.getInterpolator()).withDuration(Constant.ANIMATION_DURATION).withStartDelay(Constant.ANIMATION_START_DELAY).start();
        });
    }

    private void initChart2(final SciChartSurface surface) {
        surface.setTheme(R.style.SciChart_NavyBlue);

        final IAxis xAxis = sciChartBuilder.newNumericAxis().withGrowBy(0.1d, 0.1d).build();
        final IAxis yAxis = sciChartBuilder.newNumericAxis().withGrowBy(0.1d, 0.1d).build();

        int[] xValues = {0, 1, 2, 3, 4, 5, 6, 7, 8};
        double[] yValues = {1, 2, 3, 2, 0.5, 1, 2.5, 1, 1};

        IXyDataSeries<Double, Double> dataSeries = new XyDataSeries<>(Double.class, Double.class);
        for (int i = 1; i < xValues.length; i++) {
            dataSeries.append((double) xValues[i], yValues[i]);
        }
        EllipsePointMarker pointMarker = new EllipsePointMarker();
        pointMarker.setWidth(20);
        pointMarker.setHeight(20);
        pointMarker.setFillStyle(new SolidBrushStyle(0xFFf4840b));

        final FastLineRenderableSeries line = sciChartBuilder.newLineSeries().withDataSeries(dataSeries).withStrokeStyle(0xFFae418d, 1f, true).withIsDigitalLine(true).build();
        line.setPointMarker(pointMarker);

        TextAnnotation titleAnnotation = sciChartBuilder.newTextAnnotation()
                .withText("Digital Line Chart")
                .withPosition(5d,3d)
                .withFontStyle(12, Color.WHITE)
                .withVerticalAnchorPoint(VerticalAnchorPoint.Center)
                .withHorizontalAnchorPoint(HorizontalAnchorPoint.Center)
                .build();

        UpdateSuspender.using(surface, () -> {
            Collections.addAll(surface.getXAxes(), xAxis);
            Collections.addAll(surface.getYAxes(), yAxis);
            Collections.addAll(surface.getAnnotations(), titleAnnotation);
            Collections.addAll(surface.getRenderableSeries(), line);

            surface.zoomExtents();

            sciChartBuilder.newAnimator(line).withSweepTransformation().withInterpolator(DefaultInterpolator.getInterpolator()).withDuration(Constant.ANIMATION_DURATION).withStartDelay(Constant.ANIMATION_START_DELAY).start();
        });
    }
}