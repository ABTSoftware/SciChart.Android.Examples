//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2021. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// SecondaryYAxesFragment.java is part of SCICHART®, High Performance Scientific Charts
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
import com.scichart.core.framework.UpdateSuspender;
import com.scichart.examples.data.DataManager;
import com.scichart.examples.data.DoubleSeries;
import com.scichart.examples.fragments.base.ExampleSingleChartBaseFragment;

import java.util.Collections;

public class SecondaryYAxesFragment extends ExampleSingleChartBaseFragment {

    private final static String X_BOTTOM_AXIS = "xBottomAxis";
    private final static String Y_LEFT_AXIS = "yLeftAxis";
    private final static String Y_RIGHT_AXIS = "yRightAxis";

    @Override
    public boolean showDefaultModifiersInToolbar() {
        return false;
    }

    @Override
    protected void initExample(@NonNull SciChartSurface surface) {
        final IAxis xBottomAxis = sciChartBuilder.newNumericAxis()
                .withGrowBy(0.1d, 0.1d)
                .withAxisAlignment(AxisAlignment.Bottom)
                .withAxisId(X_BOTTOM_AXIS)
                .withAxisTitle("Bottom Axis")
                .build();
        final IAxis yLeftAxis = sciChartBuilder.newNumericAxis()
                .withGrowBy(0.1d, 0.1d)
                .withAxisAlignment(AxisAlignment.Left)
                .withAxisId(Y_LEFT_AXIS)
                .withAxisTitle("Left Axis")
                .withTextColor(0xFF47bde6)
                .build();
        final IAxis yRightAxis = sciChartBuilder.newNumericAxis()
                .withGrowBy(0.1d, 0.1d)
                .withAxisAlignment(AxisAlignment.Right)
                .withAxisId(Y_RIGHT_AXIS)
                .withAxisTitle("Right Axis")
                .withTextColor(0xFFae418d)
                .build();

        final DoubleSeries ds1Points = DataManager.getInstance().getFourierSeries(1.0, 0.1, 5000);
        final DoubleSeries ds2Points = DataManager.getInstance().getDampedSinewave(3.0, 0.005, 5000, 10);

        final IXyDataSeries<Double, Double> ds1 = sciChartBuilder.newXyDataSeries(Double.class, Double.class).build();
        final IXyDataSeries<Double, Double> ds2 = sciChartBuilder.newXyDataSeries(Double.class, Double.class).build();
        ds1.append(ds1Points.xValues, ds1Points.yValues);
        ds2.append(ds2Points.xValues, ds2Points.yValues);

        final FastLineRenderableSeries rs1 = sciChartBuilder.newLineSeries()
                .withDataSeries(ds1)
                .withXAxisId(X_BOTTOM_AXIS)
                .withYAxisId(Y_RIGHT_AXIS)
                .withStrokeStyle(0xFFae418d, 1f, true)
                .build();

        final FastLineRenderableSeries rs2 = sciChartBuilder.newLineSeries()
                .withDataSeries(ds2)
                .withXAxisId(X_BOTTOM_AXIS)
                .withYAxisId(Y_LEFT_AXIS)
                .withStrokeStyle(0xFF47bde6, 1f, true)
                .build();

        UpdateSuspender.using(surface, () -> {
            Collections.addAll(surface.getXAxes(), xBottomAxis);
            Collections.addAll(surface.getYAxes(), yLeftAxis, yRightAxis);
            Collections.addAll(surface.getRenderableSeries(), rs1, rs2);
            Collections.addAll(surface.getChartModifiers(), sciChartBuilder.newModifierGroupWithDefaultModifiers().build());

            sciChartBuilder.newAnimator(rs1).withSweepTransformation().withInterpolator(new DecelerateInterpolator()).withDuration(3000).withStartDelay(350).start();
            sciChartBuilder.newAnimator(rs2).withSweepTransformation().withInterpolator(new DecelerateInterpolator()).withDuration(3000).withStartDelay(350).start();
        });
    }
}