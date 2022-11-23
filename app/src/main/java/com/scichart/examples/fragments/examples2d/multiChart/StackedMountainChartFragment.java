//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2021. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// StackedMountainChartFragment.java is part of SCICHART®, High Performance Scientific Charts
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
import com.scichart.charting.modifiers.TooltipModifier;
import com.scichart.charting.visuals.SciChartSurface;
import com.scichart.charting.visuals.renderableSeries.StackedMountainRenderableSeries;
import com.scichart.charting.visuals.renderableSeries.VerticallyStackedMountainsCollection;
import com.scichart.core.framework.UpdateSuspender;
import com.scichart.examples.fragments.base.ExampleSingleChartBaseFragment;

import java.util.Collections;

public class StackedMountainChartFragment extends ExampleSingleChartBaseFragment {

    @Override
    protected void initExample(@NonNull SciChartSurface surface) {
        double[] yValues1 = new double[] { 4.0,  7,    5.2,  9.4,  3.8,  5.1, 7.5,  12.4, 14.6, 8.1, 11.7, 14.4, 16.0, 3.7, 5.1, 6.4, 3.5, 2.5, 12.4, 16.4, 7.1, 8.0, 9.0 };
        double[] yValues2 = new double[] { 15.0, 10.1, 10.2, 10.4, 10.8, 1.1, 11.5, 3.4,  4.6,  0.1, 1.7, 14.4, 6.0, 13.7, 10.1, 8.4, 8.5, 12.5, 1.4, 0.4, 10.1, 5.0, 1.0 };

        final IXyDataSeries<Double, Double> ds1 = sciChartBuilder.newXyDataSeries(Double.class, Double.class).build();
        final IXyDataSeries<Double, Double> ds2 = sciChartBuilder.newXyDataSeries(Double.class, Double.class).build();

        for (int i = 0; i < yValues1.length; i++) ds1.append((double) i, yValues1[i]);
        for (int i = 0; i < yValues2.length; i++) ds2.append((double) i, yValues2[i]);

        final StackedMountainRenderableSeries s1 = sciChartBuilder.newStackedMountain().withDataSeries(ds1).withLinearGradientColors(0xDD47bde6, 0x88e2f4fd).build();
        final StackedMountainRenderableSeries s2 = sciChartBuilder.newStackedMountain().withDataSeries(ds2).withLinearGradientColors(0xDDae418d, 0x88efb4d3).build();

        final VerticallyStackedMountainsCollection seriesCollection = new VerticallyStackedMountainsCollection();
        seriesCollection.add(s1);
        seriesCollection.add(s2);

        UpdateSuspender.using(surface, () -> {
            Collections.addAll(surface.getRenderableSeries(), seriesCollection);
            Collections.addAll(surface.getXAxes(), sciChartBuilder.newNumericAxis().build());
            Collections.addAll(surface.getYAxes(), sciChartBuilder.newNumericAxis().build());
            Collections.addAll(surface.getChartModifiers(), new TooltipModifier());

            sciChartBuilder.newAnimator(s1).withWaveTransformation().withInterpolator(new DecelerateInterpolator()).withDuration(3000).withStartDelay(350).start();
            sciChartBuilder.newAnimator(s2).withWaveTransformation().withInterpolator(new DecelerateInterpolator()).withDuration(3000).withStartDelay(350).start();
        });
    }
}