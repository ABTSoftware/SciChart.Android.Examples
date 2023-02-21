//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2021. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// QuadLeftRightYAxesFragment.java is part of SCICHART®, High Performance Scientific Charts
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
import com.scichart.charting.visuals.SciChartSurface;
import com.scichart.charting.visuals.axes.AxisAlignment;
import com.scichart.charting.visuals.axes.IAxis;
import com.scichart.charting.visuals.renderableSeries.FastLineRenderableSeries;
import com.scichart.charting.visuals.renderableSeries.IRenderableSeries;
import com.scichart.core.framework.UpdateSuspender;
import com.scichart.core.model.DoubleValues;
import com.scichart.data.model.DoubleRange;
import com.scichart.examples.R;
import com.scichart.examples.fragments.base.ExampleSingleChartBaseFragment;
import com.scichart.examples.utils.Constant;
import com.scichart.examples.utils.interpolator.DefaultInterpolator;

import java.util.Collections;
import java.util.Random;

public class QuadLeftRightYAxesFragment extends ExampleSingleChartBaseFragment {
    private final static String Y_LEFT_AXIS_1 = "yLeftAxis1";
    private final static String Y_LEFT_AXIS_2 = "yLeftAxis2";
    private final static String Y_LEFT_AXIS_3 = "yLeftAxis3";
    private final static String Y_LEFT_AXIS_4 = "yLeftAxis4";
    private final static String Y_RIGHT_AXIS_1 = "yRightAxis1";
    private final static String Y_RIGHT_AXIS_2 = "yRightAxis2";
    private final static String Y_RIGHT_AXIS_3 = "yRightAxis3";
    private final static String Y_RIGHT_AXIS_4 = "yRightAxis4";

    private final static int COUNT = 50;
    private final Random random = new Random(251916);

    private final IXyDataSeries<Double, Double> ds1 = sciChartBuilder.newXyDataSeries(Double.class, Double.class).withSeriesName("Red line").build();
    private final IXyDataSeries<Double, Double> ds2 = sciChartBuilder.newXyDataSeries(Double.class, Double.class).withSeriesName("Black line").build();
    private final IXyDataSeries<Double, Double> ds3 = sciChartBuilder.newXyDataSeries(Double.class, Double.class).withSeriesName("Orange line").build();
    private final IXyDataSeries<Double, Double> ds4 = sciChartBuilder.newXyDataSeries(Double.class, Double.class).withSeriesName("Blue line").build();
    private final IXyDataSeries<Double, Double> ds5 = sciChartBuilder.newXyDataSeries(Double.class, Double.class).withSeriesName("Another Blue").build();
    private final IXyDataSeries<Double, Double> ds6 = sciChartBuilder.newXyDataSeries(Double.class, Double.class).withSeriesName("Green line").build();
    private final IXyDataSeries<Double, Double> ds7 = sciChartBuilder.newXyDataSeries(Double.class, Double.class).withSeriesName("Another Red").build();
    private final IXyDataSeries<Double, Double> ds8 = sciChartBuilder.newXyDataSeries(Double.class, Double.class).withSeriesName("Another Black").build();

    @Override
    public boolean showDefaultModifiersInToolbar() {
        return false;
    }

    @Override
    protected void initExample(@NonNull SciChartSurface surface) {
        surface.setTheme(R.style.SciChart_NavyBlue);

        UpdateSuspender.using(surface, () -> {
            fillDataSeries(ds1);
            fillDataSeries(ds2);
            fillDataSeries(ds3);
            fillDataSeries(ds4);
            fillDataSeries(ds5);
            fillDataSeries(ds6);
            fillDataSeries(ds7);
            fillDataSeries(ds8);

            final IAxis xAxis = sciChartBuilder.newNumericAxis()
                    .withGrowBy(new DoubleRange(0.2d, 0.2d))
                    .withAxisAlignment(AxisAlignment.Bottom)
                    .build();

            final IAxis yLeftAxis1 = sciChartBuilder.newNumericAxis()
                    .withGrowBy(new DoubleRange(0.1d, 0.1d))
                    .withAxisAlignment(AxisAlignment.Left)
                    .withAxisId(Y_LEFT_AXIS_1)
                    .withTextColor(0xFFFF1919)
                    .withDrawMajorTicks(false)
                    .withDrawMinorTicks(false)
                    .build();

            final IAxis yLeftAxis2 = sciChartBuilder.newNumericAxis()
                    .withGrowBy(new DoubleRange(0.1d, 0.1d))
                    .withAxisAlignment(AxisAlignment.Left)
                    .withAxisId(Y_LEFT_AXIS_2)
                    .withTextColor(0xFFCCCCCC)
                    .withDrawMajorTicks(false)
                    .withDrawMinorTicks(false)
                    .build();

            final IAxis yLeftAxis3 = sciChartBuilder.newNumericAxis()
                    .withGrowBy(new DoubleRange(0.1d, 0.1d))
                    .withAxisAlignment(AxisAlignment.Left)
                    .withAxisId(Y_LEFT_AXIS_3)
                    .withTextColor(0xFFFC9C29)
                    .withDrawMajorTicks(false)
                    .withDrawMinorTicks(false)
                    .withIsCenterAxis(true)
                    .build();

            final IAxis yLeftAxis4 = sciChartBuilder.newNumericAxis()
                    .withGrowBy(new DoubleRange(0.1d, 0.1d))
                    .withAxisAlignment(AxisAlignment.Left)
                    .withAxisId(Y_LEFT_AXIS_4)
                    .withTextColor(0xFF4083B7)
                    .withDrawMajorTicks(false)
                    .withDrawMinorTicks(false)
                    .withIsCenterAxis(true)
                    .build();

            final IAxis yRightAxis1 = sciChartBuilder.newNumericAxis()
                    .withGrowBy(new DoubleRange(0.1d, 0.1d))
                    .withAxisAlignment(AxisAlignment.Right)
                    .withAxisId(Y_RIGHT_AXIS_1)
                    .withTextColor(0xFF4083B7)
                    .withDrawMajorTicks(false)
                    .withDrawMinorTicks(false)
                    .build();

            final IAxis yRightAxis2 = sciChartBuilder.newNumericAxis()
                    .withGrowBy(new DoubleRange(0.1d, 0.1d))
                    .withAxisAlignment(AxisAlignment.Right)
                    .withAxisId(Y_RIGHT_AXIS_2)
                    .withTextColor(0xFF279B27)
                    .withDrawMajorTicks(false)
                    .withDrawMinorTicks(false)
                    .build();

            final IAxis yRightAxis3 = sciChartBuilder.newNumericAxis()
                    .withGrowBy(new DoubleRange(0.1d, 0.1d))
                    .withAxisAlignment(AxisAlignment.Right)
                    .withAxisId(Y_RIGHT_AXIS_3)
                    .withTextColor(0xFFFF1919)
                    .withDrawMajorTicks(false)
                    .withDrawMinorTicks(false)
                    .withIsCenterAxis(true)
                    .build();

            final IAxis yRightAxis4 = sciChartBuilder.newNumericAxis()
                    .withGrowBy(new DoubleRange(0.1d, 0.1d))
                    .withAxisAlignment(AxisAlignment.Right)
                    .withAxisId(Y_RIGHT_AXIS_4)
                    .withTextColor(0xFFCCCCCC)
                    .withDrawMajorTicks(false)
                    .withDrawMinorTicks(false)
                    .withIsCenterAxis(true)
                    .build();

            final FastLineRenderableSeries rs1 = sciChartBuilder.newLineSeries()
                    .withDataSeries(ds1)
                    .withYAxisId(yLeftAxis1.getAxisId())
                    .withStrokeStyle(0xFFFF1919, 1f, true)
                    .build();

            final FastLineRenderableSeries rs2 = sciChartBuilder.newLineSeries()
                    .withDataSeries(ds2)
                    .withYAxisId(yLeftAxis2.getAxisId())
                    .withStrokeStyle(0xFFCCCCCC, 1f, true)
                    .build();

            final FastLineRenderableSeries rs3 = sciChartBuilder.newLineSeries()
                    .withDataSeries(ds3)
                    .withYAxisId(yLeftAxis3.getAxisId())
                    .withStrokeStyle(0xFFFC9C29, 1f, true)
                    .build();

            final FastLineRenderableSeries rs4 = sciChartBuilder.newLineSeries()
                    .withDataSeries(ds4)
                    .withYAxisId(yLeftAxis4.getAxisId())
                    .withStrokeStyle(0xFF4083B7, 1f, true)
                    .build();

            final FastLineRenderableSeries rs5 = sciChartBuilder.newLineSeries()
                    .withDataSeries(ds5)
                    .withYAxisId(yRightAxis1.getAxisId())
                    .withStrokeStyle(0xFF4083B7, 1f, true)
                    .build();

            final FastLineRenderableSeries rs6 = sciChartBuilder.newLineSeries()
                    .withDataSeries(ds6)
                    .withYAxisId(yRightAxis2.getAxisId())
                    .withStrokeStyle(0xFF279B27, 1f, true)
                    .build();

            final FastLineRenderableSeries rs7 = sciChartBuilder.newLineSeries()
                    .withDataSeries(ds7)
                    .withYAxisId(yRightAxis3.getAxisId())
                    .withStrokeStyle(0xFFFF1919, 1f, true)
                    .build();

            final FastLineRenderableSeries rs8 = sciChartBuilder.newLineSeries()
                    .withDataSeries(ds8)
                    .withYAxisId(Y_RIGHT_AXIS_4)
                    .withStrokeStyle(0xFFCCCCCC, 1f, true)
                    .build();

            Collections.addAll(surface.getXAxes(), xAxis);
            Collections.addAll(surface.getYAxes(), yLeftAxis1, yLeftAxis2, yLeftAxis3, yLeftAxis4, yRightAxis1, yRightAxis2, yRightAxis3, yRightAxis4);
            Collections.addAll(surface.getRenderableSeries(), rs1, rs2, rs3, rs4, rs5, rs6, rs7, rs8);
            Collections.addAll(surface.getChartModifiers(), sciChartBuilder.newModifierGroupWithDefaultModifiers().build());

            sciChartBuilder.newAnimator(rs1).withSweepTransformation().withInterpolator(DefaultInterpolator.getInterpolator()).withDuration(Constant.ANIMATION_DURATION).withStartDelay(Constant.ANIMATION_START_DELAY).start();
            sciChartBuilder.newAnimator(rs2).withSweepTransformation().withInterpolator(DefaultInterpolator.getInterpolator()).withDuration(Constant.ANIMATION_DURATION).withStartDelay(Constant.ANIMATION_START_DELAY).start();
            sciChartBuilder.newAnimator(rs3).withSweepTransformation().withInterpolator(DefaultInterpolator.getInterpolator()).withDuration(Constant.ANIMATION_DURATION).withStartDelay(Constant.ANIMATION_START_DELAY).start();
            sciChartBuilder.newAnimator(rs4).withSweepTransformation().withInterpolator(DefaultInterpolator.getInterpolator()).withDuration(Constant.ANIMATION_DURATION).withStartDelay(Constant.ANIMATION_START_DELAY).start();
            sciChartBuilder.newAnimator(rs5).withSweepTransformation().withInterpolator(DefaultInterpolator.getInterpolator()).withDuration(Constant.ANIMATION_DURATION).withStartDelay(Constant.ANIMATION_START_DELAY).start();
            sciChartBuilder.newAnimator(rs6).withSweepTransformation().withInterpolator(DefaultInterpolator.getInterpolator()).withDuration(Constant.ANIMATION_DURATION).withStartDelay(Constant.ANIMATION_START_DELAY).start();
            sciChartBuilder.newAnimator(rs7).withSweepTransformation().withInterpolator(DefaultInterpolator.getInterpolator()).withDuration(Constant.ANIMATION_DURATION).withStartDelay(Constant.ANIMATION_START_DELAY).start();
            sciChartBuilder.newAnimator(rs8).withSweepTransformation().withInterpolator(DefaultInterpolator.getInterpolator()).withDuration(Constant.ANIMATION_DURATION).withStartDelay(Constant.ANIMATION_START_DELAY).start();


        });
    }

    private void fillDataSeries(IXyDataSeries<Double, Double> dataSeries) {
        final DoubleValues xValues = new DoubleValues();
        final DoubleValues yValues = new DoubleValues();

        double randomWalk = 5;
        for (int i = 0; i < COUNT; i++) {
            randomWalk += random.nextDouble() - 0.498;
            xValues.add(i);
            yValues.add(randomWalk);
        }
        dataSeries.append(xValues, yValues);
    }
}
