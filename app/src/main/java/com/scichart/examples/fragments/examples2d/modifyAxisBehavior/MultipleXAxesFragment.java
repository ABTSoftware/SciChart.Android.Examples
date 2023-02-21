//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2021. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// MultipleXAxesFragment.java is part of SCICHART®, High Performance Scientific Charts
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

import android.view.animation.DecelerateInterpolator;

import androidx.annotation.NonNull;

import com.scichart.charting.model.dataSeries.IXyDataSeries;
import com.scichart.charting.modifiers.SourceMode;
import com.scichart.charting.visuals.SciChartSurface;
import com.scichart.charting.visuals.axes.AxisAlignment;
import com.scichart.charting.visuals.axes.IAxis;
import com.scichart.charting.visuals.renderableSeries.FastLineRenderableSeries;
import com.scichart.core.framework.UpdateSuspender;
import com.scichart.core.model.DoubleValues;
import com.scichart.data.model.DoubleRange;
import com.scichart.examples.fragments.base.ExampleSingleChartBaseFragment;
import com.scichart.examples.utils.Constant;
import com.scichart.examples.utils.interpolator.DefaultInterpolator;

import java.util.Collections;
import java.util.Random;

public class MultipleXAxesFragment extends ExampleSingleChartBaseFragment {

    private final static String X_TOP_AXIS = "xTopAxis";
    private final static String X_BOTTOM_AXIS = "xBottomAxis";
    private final static String Y_LEFT_AXIS = "yLeftAxis";
    private final static String Y_RIGHT_AXIS = "yRightAxis";

    private final static int COUNT = 150;
    private final Random random = new Random(251916);

    @Override
    protected void initExample(@NonNull SciChartSurface surface) {
        final IAxis xTopAxis = sciChartBuilder.newNumericAxis()
                .withAxisAlignment(AxisAlignment.Top)
                .withAxisId(X_TOP_AXIS)
                .withTextColor(0xFFAE418D)
                .build();

        final IAxis xBottomAxis = sciChartBuilder.newNumericAxis()
                .withAxisAlignment(AxisAlignment.Bottom)
                .withAxisId(X_BOTTOM_AXIS)
                .withTextColor(0xFF47BDE6)
                .build();

        final IAxis yLeftAxis = sciChartBuilder.newNumericAxis()
                .withGrowBy(new DoubleRange(0.1d, 0.1d))
                .withAxisAlignment(AxisAlignment.Left)
                .withAxisId(Y_LEFT_AXIS)
                .withTextFormatting("#.0")
                .withTextColor(0xFF68BCAE)
                .build();

        final IAxis yRightAxis = sciChartBuilder.newNumericAxis()
                .withGrowBy(new DoubleRange(0.1d, 0.1d))
                .withAxisAlignment(AxisAlignment.Right)
                .withAxisId(Y_RIGHT_AXIS)
                .withTextFormatting("#.0")
                .withTextColor(0xFFE97064)
                .build();

        final IXyDataSeries<Double, Double> ds1 = sciChartBuilder.newXyDataSeries(Double.class, Double.class).withSeriesName("Line 1").build();
        final IXyDataSeries<Double, Double> ds2 = sciChartBuilder.newXyDataSeries(Double.class, Double.class).withSeriesName("Line 2").build();
        final IXyDataSeries<Double, Double> ds3 = sciChartBuilder.newXyDataSeries(Double.class, Double.class).withSeriesName("Line 3").build();
        final IXyDataSeries<Double, Double> ds4 = sciChartBuilder.newXyDataSeries(Double.class, Double.class).withSeriesName("Line 4").build();

        fillDataSeries(ds1);
        fillDataSeries(ds2);
        fillDataSeries(ds3);
        fillDataSeries(ds4);

        final FastLineRenderableSeries rs1 = sciChartBuilder.newLineSeries()
                .withDataSeries(ds1)
                .withXAxisId(X_BOTTOM_AXIS)
                .withYAxisId(Y_LEFT_AXIS)
                .withStrokeStyle(0xFF47BDE6, 1f, true)
                .build();

        final FastLineRenderableSeries rs2 = sciChartBuilder.newLineSeries()
                .withDataSeries(ds2)
                .withXAxisId(X_BOTTOM_AXIS)
                .withYAxisId(Y_LEFT_AXIS)
                .withStrokeStyle(0xFFAE418D, 1f, true)
                .build();

        final FastLineRenderableSeries rs3 = sciChartBuilder.newLineSeries()
                .withDataSeries(ds3)
                .withXAxisId(X_TOP_AXIS)
                .withYAxisId(Y_RIGHT_AXIS)
                .withStrokeStyle(0xFF68BCAE, 1f, true)
                .build();

        final FastLineRenderableSeries rs4 = sciChartBuilder.newLineSeries()
                .withDataSeries(ds4)
                .withXAxisId(X_TOP_AXIS)
                .withYAxisId(Y_RIGHT_AXIS)
                .withStrokeStyle(0xFFE97064, 1f, true)
                .build();

        UpdateSuspender.using(surface, () -> {
            Collections.addAll(surface.getXAxes(), xTopAxis, xBottomAxis);
            Collections.addAll(surface.getYAxes(), yLeftAxis, yRightAxis);
            Collections.addAll(surface.getRenderableSeries(), rs1, rs2, rs3, rs4);
            Collections.addAll(surface.getChartModifiers(), sciChartBuilder.newModifierGroupWithDefaultModifiers()
                    .withLegendModifier().withSourceMode(SourceMode.AllSeries).build()
                    .withXAxisDragModifier().withReceiveHandledEvents(true).build()
                    .withYAxisDragModifier().withReceiveHandledEvents(true).build()
                    .build());

            sciChartBuilder.newAnimator(rs1).withSweepTransformation().withInterpolator(DefaultInterpolator.getInterpolator()).withDuration(Constant.ANIMATION_DURATION).withStartDelay(Constant.ANIMATION_START_DELAY).start();
            sciChartBuilder.newAnimator(rs2).withSweepTransformation().withInterpolator(DefaultInterpolator.getInterpolator()).withDuration(Constant.ANIMATION_DURATION).withStartDelay(Constant.ANIMATION_START_DELAY).start();
            sciChartBuilder.newAnimator(rs3).withSweepTransformation().withInterpolator(DefaultInterpolator.getInterpolator()).withDuration(Constant.ANIMATION_DURATION).withStartDelay(Constant.ANIMATION_START_DELAY).start();
            sciChartBuilder.newAnimator(rs4).withSweepTransformation().withInterpolator(DefaultInterpolator.getInterpolator()).withDuration(Constant.ANIMATION_DURATION).withStartDelay(Constant.ANIMATION_START_DELAY).start();
        });
    }

    private void fillDataSeries(IXyDataSeries<Double, Double> dataSeries) {
        final DoubleValues xValues = new DoubleValues();
        final DoubleValues yValues = new DoubleValues();

        double randomWalk = 10;
        for (int i = 0; i < COUNT; i++) {
            randomWalk += random.nextDouble() - 0.498;
            xValues.add(i);
            yValues.add(randomWalk);
        }
        dataSeries.append(xValues, yValues);
    }
}