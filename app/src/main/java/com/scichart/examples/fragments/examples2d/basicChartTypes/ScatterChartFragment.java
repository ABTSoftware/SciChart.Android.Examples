//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2021. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// ScatterChartFragment.java is part of SCICHART®, High Performance Scientific Charts
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

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;

import com.scichart.charting.model.dataSeries.IXyDataSeries;
import com.scichart.charting.modifiers.AxisDragModifierBase.AxisDragMode;
import com.scichart.charting.visuals.SciChartSurface;
import com.scichart.charting.visuals.axes.IAxis;
import com.scichart.charting.visuals.pointmarkers.EllipsePointMarker;
import com.scichart.charting.visuals.pointmarkers.IPointMarker;
import com.scichart.charting.visuals.pointmarkers.TrianglePointMarker;
import com.scichart.charting.visuals.renderableSeries.XyScatterRenderableSeries;
import com.scichart.core.framework.UpdateSuspender;
import com.scichart.examples.fragments.base.ExampleSingleChartBaseFragment;

import java.util.Collections;
import java.util.Random;

public class ScatterChartFragment extends ExampleSingleChartBaseFragment {

    private final Random random = new Random();

    @Override
    protected void initExample(@NonNull SciChartSurface surface) {
        final IAxis xAxis = sciChartBuilder.newNumericAxis().withGrowBy(0.1d, 0.1d).build();
        final IAxis yAxis = sciChartBuilder.newNumericAxis().withGrowBy(0.1d, 0.1d).build();

        final XyScatterRenderableSeries rSeries1 = getScatterRenderableSeries(new TrianglePointMarker(), 0xFFFFEB01, false);
        final XyScatterRenderableSeries rSeries2 = getScatterRenderableSeries(new EllipsePointMarker(), 0xFFffA300, false);
        final XyScatterRenderableSeries rSeries3 = getScatterRenderableSeries(new TrianglePointMarker(), 0xFFff6501, true);
        final XyScatterRenderableSeries rSeries4 = getScatterRenderableSeries(new EllipsePointMarker(), 0xFFffa300, true);

        UpdateSuspender.using(surface, () -> {
            Collections.addAll(surface.getXAxes(), xAxis);
            Collections.addAll(surface.getYAxes(), yAxis);
            Collections.addAll(surface.getRenderableSeries(), rSeries1, rSeries2, rSeries3, rSeries4);
            Collections.addAll(surface.getChartModifiers(), sciChartBuilder.newModifierGroup()
                    .withZoomExtentsModifier().build()
                    .withPinchZoomModifier().build()
                    .withCursorModifier().withReceiveHandledEvents(true).build()
                    .withXAxisDragModifier().withReceiveHandledEvents(true).build()
                    .withYAxisDragModifier().withDragMode(AxisDragMode.Pan).build()
                    .build());

            sciChartBuilder.newAnimator(rSeries1).withWaveTransformation().withInterpolator(new DecelerateInterpolator()).withDuration(3000).withStartDelay(350).start();
            sciChartBuilder.newAnimator(rSeries2).withWaveTransformation().withInterpolator(new DecelerateInterpolator()).withDuration(3000).withStartDelay(350).start();
            sciChartBuilder.newAnimator(rSeries3).withWaveTransformation().withInterpolator(new DecelerateInterpolator()).withDuration(3000).withStartDelay(350).start();
            sciChartBuilder.newAnimator(rSeries4).withWaveTransformation().withInterpolator(new DecelerateInterpolator()).withDuration(3000).withStartDelay(350).start();
        });
    }

    private XyScatterRenderableSeries getScatterRenderableSeries(IPointMarker pointMarker, @ColorInt int color, boolean negative) {
        final String seriesName = pointMarker instanceof EllipsePointMarker ?
                negative ? "Negative Ellipse" : "Positive Ellipse" :
                negative ? "Negative" : "Positive";

        final IXyDataSeries<Integer, Double> dataSeries = sciChartBuilder.newXyDataSeries(Integer.class, Double.class).withSeriesName(seriesName).build();

        for (int i = 0; i < 200; i++) {
            final double time = (i < 100) ? getRandom(0, i + 10) / 100 : getRandom(0, 200 - i + 10) / 100;
            final double y = negative ? -time * time * time : time * time * time;

            dataSeries.append(i, y);
        }

        return sciChartBuilder.newScatterSeries()
                .withDataSeries(dataSeries)
                .withStrokeStyle(color)
                .withPointMarker(sciChartBuilder.newPointMarker(pointMarker)
                        .withSize(6, 6)
                        .withStroke(0xFFFFFFFF, 0.1f)
                        .withFill(color)
                        .build())
                .build();
    }

    private double getRandom(double min, double max) {
        return min + (max - min) * random.nextDouble();
    }
}