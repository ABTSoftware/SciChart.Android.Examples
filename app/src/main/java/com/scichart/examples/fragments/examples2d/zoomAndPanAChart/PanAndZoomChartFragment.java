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

import android.view.animation.DecelerateInterpolator;

import androidx.annotation.NonNull;

import com.scichart.charting.model.dataSeries.XyDataSeries;
import com.scichart.charting.visuals.SciChartSurface;
import com.scichart.charting.visuals.axes.IAxis;
import com.scichart.charting.visuals.renderableSeries.FastMountainRenderableSeries;
import com.scichart.core.framework.UpdateSuspender;
import com.scichart.examples.data.DataManager;
import com.scichart.examples.data.DoubleSeries;
import com.scichart.examples.fragments.base.ExampleSingleChartBaseFragment;

import java.util.Collections;

public class PanAndZoomChartFragment extends ExampleSingleChartBaseFragment {

    @Override
    protected void initExample(@NonNull SciChartSurface surface) {
        final IAxis xAxis = sciChartBuilder.newNumericAxis().withGrowBy(0.1, 0.1).withVisibleRange(3, 6).build();
        final IAxis yAxis = sciChartBuilder.newNumericAxis().withGrowBy(0.1, 0.1).build();

        final XyDataSeries<Double, Double> ds1 = sciChartBuilder.newXyDataSeries(Double.class, Double.class).build();
        final XyDataSeries<Double, Double> ds2 = sciChartBuilder.newXyDataSeries(Double.class, Double.class).build();
        final XyDataSeries<Double, Double> ds3 = sciChartBuilder.newXyDataSeries(Double.class, Double.class).build();

        final FastMountainRenderableSeries rs1 = sciChartBuilder.newMountainSeries().withDataSeries(ds1).withAreaFillColor(0xFF177B17).withStrokeStyle(0xFF177B17, 1f, true).build();
        final FastMountainRenderableSeries rs2 = sciChartBuilder.newMountainSeries().withDataSeries(ds2).withAreaFillColor(0x77FF1919).withStrokeStyle(0xFFDD0909, 1f, true).build();
        final FastMountainRenderableSeries rs3 = sciChartBuilder.newMountainSeries().withDataSeries(ds3).withAreaFillColor(0x771964FF).withStrokeStyle(0xFF0944CF, 1f, true).build();

        final DoubleSeries data1 = DataManager.getInstance().getDampedSinewave(300, 1.0, 0.0, 0.01, 1000, 10);
        final DoubleSeries data2 = DataManager.getInstance().getDampedSinewave(300, 1.0, 0.0, 0.024, 1000, 10);
        final DoubleSeries data3 = DataManager.getInstance().getDampedSinewave(300, 1.0, 0.0, 0.049, 1000, 10);

        ds1.append(data1.xValues, data1.yValues);
        ds2.append(data2.xValues, data2.yValues);
        ds3.append(data3.xValues, data3.yValues);

        UpdateSuspender.using(surface, () -> {
            Collections.addAll(surface.getXAxes(), xAxis);
            Collections.addAll(surface.getYAxes(), yAxis);
            Collections.addAll(surface.getRenderableSeries(), rs1, rs2, rs3);
            Collections.addAll(surface.getChartModifiers(), sciChartBuilder.newModifierGroup()
                    .withPinchZoomModifier().withReceiveHandledEvents(true).build()
                    .withZoomPanModifier().withReceiveHandledEvents(true).build()
                    .withZoomExtentsModifier().build()
                    .build());

            sciChartBuilder.newAnimator(rs1).withWaveTransformation().withInterpolator(new DecelerateInterpolator()).withDuration(2000).withStartDelay(350).start();
            sciChartBuilder.newAnimator(rs2).withWaveTransformation().withInterpolator(new DecelerateInterpolator()).withDuration(2000).withStartDelay(350).start();
            sciChartBuilder.newAnimator(rs3).withWaveTransformation().withInterpolator(new DecelerateInterpolator()).withDuration(2000).withStartDelay(350).start();
        });
    }
}