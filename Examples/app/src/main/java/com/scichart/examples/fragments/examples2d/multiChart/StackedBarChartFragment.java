//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2021. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// StackedBarChartFragment.java is part of SCICHART®, High Performance Scientific Charts
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

import android.view.animation.DecelerateInterpolator;

import androidx.annotation.NonNull;

import com.scichart.charting.model.dataSeries.IXyDataSeries;
import com.scichart.charting.modifiers.CursorModifier;
import com.scichart.charting.modifiers.ZoomExtentsModifier;
import com.scichart.charting.visuals.SciChartSurface;
import com.scichart.charting.visuals.axes.AxisAlignment;
import com.scichart.charting.visuals.axes.IAxis;
import com.scichart.charting.visuals.renderableSeries.StackedColumnRenderableSeries;
import com.scichart.charting.visuals.renderableSeries.VerticallyStackedColumnsCollection;
import com.scichart.core.framework.UpdateSuspender;
import com.scichart.examples.fragments.base.ExampleSingleChartBaseFragment;

import java.util.Collections;

public class StackedBarChartFragment extends ExampleSingleChartBaseFragment {

    @Override
    protected void initExample(@NonNull SciChartSurface surface) {
        final IAxis xAxis = sciChartBuilder.newNumericAxis().withAxisAlignment(AxisAlignment.Left).build();
        final IAxis yAxis = sciChartBuilder.newNumericAxis().withAxisAlignment(AxisAlignment.Bottom).withFlipCoordinates(true).build();

        double[] yValues1 = new double[]{0.0, 0.1, 0.2, 0.4, 0.8, 1.1, 1.5, 2.4, 4.6, 8.1, 11.7, 14.4, 16.0, 13.7, 10.1, 6.4, 3.5, 2.5, 5.4, 6.4, 7.1, 8.0, 9.0};
        double[] yValues2 = new double[]{2.0, 10.1, 10.2, 10.4, 10.8, 1.1, 11.5, 3.4, 4.6, 0.1, 1.7, 14.4, 16.0, 13.7, 10.1, 6.4, 3.5, 2.5, 1.4, 0.4, 10.1, 0.0, 0.0};
        double[] yValues3 = new double[]{20.0, 4.1, 4.2, 10.4, 10.8, 1.1, 11.5, 3.4, 4.6, 5.1, 5.7, 14.4, 16.0, 13.7, 10.1, 6.4, 3.5, 2.5, 1.4, 10.4, 8.1, 10.0, 15.0};

        final IXyDataSeries<Double, Double> ds1 = sciChartBuilder.newXyDataSeries(Double.class, Double.class).withSeriesName("data 1").build();
        final IXyDataSeries<Double, Double> ds2 = sciChartBuilder.newXyDataSeries(Double.class, Double.class).withSeriesName("data 2").build();
        final IXyDataSeries<Double, Double> ds3 = sciChartBuilder.newXyDataSeries(Double.class, Double.class).withSeriesName("data 3").build();

        for (int i = 0; i < yValues1.length; i++) {
            double xValue = (double) i;
            ds1.append(xValue, yValues1[i]);
            ds2.append(xValue, yValues2[i]);
            ds3.append(xValue, yValues3[i]);
        }

        final StackedColumnRenderableSeries series1 = sciChartBuilder.newStackedColumn().withDataPointWidth(0.8).withDataSeries(ds1).withLinearGradientColors(0xff567893, 0xff3D5568).withStrokeStyle(0xff567893, 0f).build();
        final StackedColumnRenderableSeries series2 = sciChartBuilder.newStackedColumn().withDataPointWidth(0.8).withDataSeries(ds2).withLinearGradientColors(0xffACBCCA, 0xff439AAF).withStrokeStyle(0xffACBCCA, 0f).build();
        final StackedColumnRenderableSeries series3 = sciChartBuilder.newStackedColumn().withDataPointWidth(0.8).withDataSeries(ds3).withLinearGradientColors(0xffDBE0E1, 0xffB6C1C3).withStrokeStyle(0xffDBE0E1, 0f).build();

        final VerticallyStackedColumnsCollection verticalCollection = new VerticallyStackedColumnsCollection();
        verticalCollection.add(series1);
        verticalCollection.add(series2);
        verticalCollection.add(series3);

        UpdateSuspender.using(surface, () -> {
            Collections.addAll(surface.getRenderableSeries(), verticalCollection);
            Collections.addAll(surface.getXAxes(), xAxis);
            Collections.addAll(surface.getYAxes(), yAxis);
            Collections.addAll(surface.getChartModifiers(), new CursorModifier());
            Collections.addAll(surface.getChartModifiers(), new ZoomExtentsModifier());

            sciChartBuilder.newAnimator(series1).withWaveTransformation().withInterpolator(new DecelerateInterpolator()).withDuration(3000).withStartDelay(350).start();
            sciChartBuilder.newAnimator(series2).withWaveTransformation().withInterpolator(new DecelerateInterpolator()).withDuration(3000).withStartDelay(350).start();
            sciChartBuilder.newAnimator(series3).withWaveTransformation().withInterpolator(new DecelerateInterpolator()).withDuration(3000).withStartDelay(350).start();
        });
    }
}