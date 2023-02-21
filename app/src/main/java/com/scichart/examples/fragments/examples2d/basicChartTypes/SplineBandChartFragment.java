//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2021. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// SplineBandChartFragment.java is part of SCICHART®, High Performance Scientific Charts
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

import android.view.animation.AccelerateDecelerateInterpolator;

import androidx.annotation.NonNull;

import com.scichart.charting.model.dataSeries.XyyDataSeries;
import com.scichart.charting.visuals.SciChartSurface;
import com.scichart.charting.visuals.axes.IAxis;
import com.scichart.charting.visuals.pointmarkers.EllipsePointMarker;
import com.scichart.charting.visuals.renderableSeries.SplineBandRenderableSeries;
import com.scichart.core.framework.UpdateSuspender;
import com.scichart.examples.data.DataManager;
import com.scichart.examples.data.DoubleSeries;
import com.scichart.examples.fragments.base.ExampleSingleChartBaseFragment;
import com.scichart.examples.utils.Constant;
import com.scichart.examples.utils.interpolator.DefaultInterpolator;
import com.scichart.examples.utils.interpolator.ElasticOutInterpolator;

import java.util.Collections;

public class SplineBandChartFragment extends ExampleSingleChartBaseFragment {

    @Override
    protected void initExample(@NonNull SciChartSurface surface) {
        final IAxis xAxis = sciChartBuilder.newNumericAxis().withGrowBy(0.1, 0.1).build();
        final IAxis yAxis = sciChartBuilder.newNumericAxis().withGrowBy(0.2, 0.2).build();

        final DoubleSeries data = DataManager.getInstance().getDampedSinewave(1.0, 0.005, 1000, 13);
        final DoubleSeries moreData = DataManager.getInstance().getDampedSinewave(1.0, 0.005, 1000, 12);

        final XyyDataSeries<Double, Double> dataSeries = sciChartBuilder.newXyyDataSeries(Double.class, Double.class).build();
        for (int i = 0; i < 10; i++) {
            final int index = i * 100;
            dataSeries.append(data.xValues.get(index), data.yValues.get(index), moreData.yValues.get(index));
        }

        final SplineBandRenderableSeries rSeries = sciChartBuilder.newSplineBandSeries()
                .withDataSeries(dataSeries)
                .withFillColor(0x33F48420).withFillY1Color(0x3350C7E0)
                .withStrokeStyle(0xFFF48420, 1f, true).withStrokeY1Style(0xFF50C7E0, 1f, true)
                .withPointMarker(sciChartBuilder.newPointMarker(new EllipsePointMarker()).withSize(7, 7).withStroke(0xFF50C7E0, 1).withFill(0xFFE4F5FC).build())
                .build();

        UpdateSuspender.using(surface, () -> {
            Collections.addAll(surface.getXAxes(), xAxis);
            Collections.addAll(surface.getYAxes(), yAxis);
            Collections.addAll(surface.getRenderableSeries(), rSeries);
            Collections.addAll(surface.getChartModifiers(), sciChartBuilder.newModifierGroupWithDefaultModifiers().build());

            sciChartBuilder.newAnimator(rSeries).withScaleTransformation().withInterpolator(DefaultInterpolator.getInterpolator()).withDuration(Constant.ANIMATION_DURATION).withStartDelay(Constant.ANIMATION_START_DELAY).start();
        });
    }
}