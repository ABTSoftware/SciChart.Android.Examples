//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2021. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// LogarithmicAxisFragment.java is part of SCICHART®, High Performance Scientific Charts
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

package com.scichart.examples.fragments.examples2d.modifyAxisBehavior;

import android.text.Layout;
import android.view.Gravity;
import android.view.animation.DecelerateInterpolator;

import androidx.annotation.NonNull;

import com.scichart.charting.model.dataSeries.IXyDataSeries;
import com.scichart.charting.model.dataSeries.XyDataSeries;
import com.scichart.charting.modifiers.SourceMode;
import com.scichart.charting.visuals.SciChartSurface;
import com.scichart.charting.visuals.axes.AutoRange;
import com.scichart.charting.visuals.axes.AxisAlignment;
import com.scichart.charting.visuals.axes.AxisTickLabelStyle;
import com.scichart.charting.visuals.axes.DateAxis;
import com.scichart.charting.visuals.axes.IAxis;
import com.scichart.charting.visuals.axes.NumericAxis;
import com.scichart.charting.visuals.pointmarkers.EllipsePointMarker;
import com.scichart.charting.visuals.pointmarkers.IPointMarker;
import com.scichart.charting.visuals.renderableSeries.FastLineRenderableSeries;
import com.scichart.core.framework.UpdateSuspender;
import com.scichart.core.model.DateValues;
import com.scichart.core.model.DoubleValues;
import com.scichart.data.model.DoubleRange;
import com.scichart.drawing.common.FontStyle;
import com.scichart.drawing.utility.ColorUtil;
import com.scichart.examples.fragments.base.ExampleSingleChartBaseFragment;
import com.scichart.examples.utils.Constant;
import com.scichart.examples.utils.interpolator.DefaultInterpolator;

import java.util.Collections;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class AxisRotationFragment extends ExampleSingleChartBaseFragment {
    private final static String X_TOP_AXIS = "xTopAxis";
    private final static String X_BOTTOM_AXIS = "xBottomAxis";
    private final static String Y_LEFT_AXIS = "yLeftAxis";
    private final static String Y_RIGHT_AXIS = "yRightAxis";

    private final static int COUNT = 150;
    private final Random random = new Random(251916);

    @Override
    protected void initExample(@NonNull SciChartSurface surface) {
        AxisTickLabelStyle rightStyle = new AxisTickLabelStyle(
                Gravity.RIGHT,
                0,0,0,0
        );

        final DateAxis xTopAxis = sciChartBuilder.newDateAxis()
                .withAxisAlignment(AxisAlignment.Top)
                .withAxisId(X_TOP_AXIS)
                .withTextColor(0xFF279B27)
                .build();
        xTopAxis.setAxisTickLabelStyle(rightStyle);
        xTopAxis.setAutoTicks(false);
        xTopAxis.setMajorDelta(new Date(20L * 24L * 60L * 60L * 1000L));
        xTopAxis.setMinorDelta(new Date(5L * 24L * 60L * 60L * 1000L));
        xTopAxis.setIsLabelCullingEnabled(false);
        xTopAxis.setAutoFitMarginalLabels(false);
        xTopAxis.setAxisLabelRotation(30);

        final DateAxis xBottomAxis = sciChartBuilder.newDateAxis()
                .withAxisAlignment(AxisAlignment.Bottom)
                .withAxisId(X_BOTTOM_AXIS)
                .withTextColor(0xFFFF1919)
                .build();
        xBottomAxis.setAutoTicks(false);
        xBottomAxis.setMajorDelta(new Date(10L * 24L * 60L * 60L * 1000L));
        xBottomAxis.setMinorDelta(new Date(5L * 24L * 60L * 60L * 1000L));
        xBottomAxis.setAutoFitMarginalLabels(false);
        xBottomAxis.setIsLabelCullingEnabled(false);
        xBottomAxis.setAxisLabelRotation(90);

        final NumericAxis yLeftAxis = sciChartBuilder.newNumericAxis()
                .withGrowBy(new DoubleRange(0.1d, 0.1d))
                .withAxisAlignment(AxisAlignment.Left)
                .withAxisId(Y_LEFT_AXIS)
                .withTextFormatting("#.0")
                .withTextColor(0xFFFC9C29)
                .build();
        yLeftAxis.setAxisLabelRotation(45);

        final NumericAxis yRightAxis = sciChartBuilder.newNumericAxis()
                .withGrowBy(new DoubleRange(0.1d, 0.1d))
                .withAxisAlignment(AxisAlignment.Right)
                .withAxisId(Y_RIGHT_AXIS)
                .withTextFormatting("#.0")
                .withTextColor(0xFF4083B7)
                .build();
        yRightAxis.setAxisLabelRotation(90);

        final IXyDataSeries<Date, Double> ds1 = sciChartBuilder.newXyDataSeries(Date.class, Double.class).withSeriesName("Red line").build();
        final IXyDataSeries<Date, Double> ds2 = sciChartBuilder.newXyDataSeries(Date.class, Double.class).withSeriesName("Green line").build();
        final IXyDataSeries<Date, Double> ds3 = sciChartBuilder.newXyDataSeries(Date.class, Double.class).withSeriesName("Orange line").build();
        final IXyDataSeries<Date, Double> ds4 = sciChartBuilder.newXyDataSeries(Date.class, Double.class).withSeriesName("Blue line").build();

        fillDataSeries(ds1);
        fillDataSeries(ds2);
        fillDataSeries(ds3);
        fillDataSeries(ds4);

        final FastLineRenderableSeries rs1 = sciChartBuilder.newLineSeries()
                .withDataSeries(ds1)
                .withXAxisId(X_BOTTOM_AXIS)
                .withYAxisId(Y_LEFT_AXIS)
                .withStrokeStyle(0xFFFF1919, 1f, true)
                .build();

        final FastLineRenderableSeries rs2 = sciChartBuilder.newLineSeries()
                .withDataSeries(ds2)
                .withXAxisId(X_BOTTOM_AXIS)
                .withYAxisId(Y_LEFT_AXIS)
                .withStrokeStyle(0xFF279B27, 1f, true)
                .build();

        final FastLineRenderableSeries rs3 = sciChartBuilder.newLineSeries()
                .withDataSeries(ds3)
                .withXAxisId(X_TOP_AXIS)
                .withYAxisId(Y_RIGHT_AXIS)
                .withStrokeStyle(0xFFFC9C29, 1f, true)
                .build();

        final FastLineRenderableSeries rs4 = sciChartBuilder.newLineSeries()
                .withDataSeries(ds4)
                .withXAxisId(X_TOP_AXIS)
                .withYAxisId(Y_RIGHT_AXIS)
                .withStrokeStyle(0xFF4083B7, 1f, true)
                .build();

        UpdateSuspender.using(surface, () -> {
            Collections.addAll(surface.getXAxes(), xTopAxis, xBottomAxis);
            Collections.addAll(surface.getYAxes(), yLeftAxis, yRightAxis);
            Collections.addAll(surface.getRenderableSeries(), rs1, rs2, rs3, rs4);
            Collections.addAll(surface.getChartModifiers(), sciChartBuilder.newModifierGroupWithDefaultModifiers()
                    .withXAxisDragModifier().withReceiveHandledEvents(true).build()
                    .withYAxisDragModifier().withReceiveHandledEvents(true).build()
                    .build());

            sciChartBuilder.newAnimator(rs1).withSweepTransformation().withInterpolator(DefaultInterpolator.getInterpolator()).withDuration(Constant.ANIMATION_DURATION).withStartDelay(Constant.ANIMATION_START_DELAY).start();
            sciChartBuilder.newAnimator(rs2).withSweepTransformation().withInterpolator(DefaultInterpolator.getInterpolator()).withDuration(Constant.ANIMATION_DURATION).withStartDelay(Constant.ANIMATION_START_DELAY).start();
            sciChartBuilder.newAnimator(rs3).withSweepTransformation().withInterpolator(DefaultInterpolator.getInterpolator()).withDuration(Constant.ANIMATION_DURATION).withStartDelay(Constant.ANIMATION_START_DELAY).start();
            sciChartBuilder.newAnimator(rs4).withSweepTransformation().withInterpolator(DefaultInterpolator.getInterpolator()).withDuration(Constant.ANIMATION_DURATION).withStartDelay(Constant.ANIMATION_START_DELAY).start();
        });
    }

    private void fillDataSeries(IXyDataSeries<Date, Double> dataSeries) {
        final DateValues xValues = new DateValues();
        final DoubleValues yValues = new DoubleValues();

        double randomWalk = 10;
        for (int i = 0; i < COUNT; i++) {
            randomWalk += random.nextDouble() - 0.498;
            xValues.add(new Date(1663047026485L + i*24L * 60L * 60L * 1000L));
            yValues.add(randomWalk);
        }
        dataSeries.append(xValues, yValues);
    }


}