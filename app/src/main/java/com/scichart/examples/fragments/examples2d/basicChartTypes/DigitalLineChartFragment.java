//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2021. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// DigitalLineChartFragment.java is part of SCICHART®, High Performance Scientific Charts
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

package com.scichart.examples.fragments.examples2d.basicChartTypes;

import android.view.animation.DecelerateInterpolator;

import androidx.annotation.NonNull;

import com.scichart.charting.model.dataSeries.XyDataSeries;
import com.scichart.charting.visuals.SciChartSurface;
import com.scichart.charting.visuals.axes.IAxis;
import com.scichart.charting.visuals.renderableSeries.FastLineRenderableSeries;
import com.scichart.core.framework.UpdateSuspender;
import com.scichart.examples.data.DataManager;
import com.scichart.examples.data.DoubleSeries;
import com.scichart.examples.fragments.base.ExampleSingleChartBaseFragment;

import java.util.Collections;

public class DigitalLineChartFragment extends ExampleSingleChartBaseFragment {
    @Override
    protected void initExample(@NonNull SciChartSurface surface) {
        final IAxis xAxis = sciChartBuilder.newNumericAxis().withGrowBy(0.1, 0.1).withVisibleRange(1, 1.25).build();
        final IAxis yAxis = sciChartBuilder.newNumericAxis().withGrowBy(0.5, 0.5).withVisibleRange(2.3, 3.3).build();

        final DoubleSeries fourierSeries = DataManager.getInstance().getFourierSeries(1.0, 0.1, 5000);
        final XyDataSeries<Double, Double> dataSeries = sciChartBuilder.newXyDataSeries(Double.class, Double.class).build();
        dataSeries.append(fourierSeries.xValues, fourierSeries.yValues);

        final FastLineRenderableSeries rSeries = sciChartBuilder.newLineSeries()
                .withDataSeries(dataSeries)
                .withStrokeStyle(0xFF99EE99, 1f, true)
                .withIsDigitalLine(true)
                .build();

        UpdateSuspender.using(surface, () -> {
            Collections.addAll(surface.getXAxes(), xAxis);
            Collections.addAll(surface.getYAxes(), yAxis);
            Collections.addAll(surface.getRenderableSeries(), rSeries);
            Collections.addAll(surface.getChartModifiers(), sciChartBuilder.newModifierGroupWithDefaultModifiers().build());

            sciChartBuilder.newAnimator(rSeries).withWaveTransformation().withInterpolator(new DecelerateInterpolator()).withDuration(3000).withStartDelay(350).start();
        });
    }
}