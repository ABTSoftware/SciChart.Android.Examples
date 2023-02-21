//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2021. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// ImpulseChartFragment.java is part of SCICHART®, High Performance Scientific Charts
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

import android.util.Log;
import android.view.animation.DecelerateInterpolator;

import androidx.annotation.NonNull;

import com.scichart.charting.model.dataSeries.IXyDataSeries;
import com.scichart.charting.visuals.SciChartSurface;
import com.scichart.charting.visuals.axes.IAxis;
import com.scichart.charting.visuals.pointmarkers.EllipsePointMarker;
import com.scichart.charting.visuals.renderableSeries.FastImpulseRenderableSeries;
import com.scichart.core.framework.UpdateSuspender;
import com.scichart.core.model.DoubleValues;
import com.scichart.drawing.utility.ColorUtil;
import com.scichart.examples.data.DataManager;
import com.scichart.examples.data.DoubleSeries;
import com.scichart.examples.fragments.base.ExampleSingleChartBaseFragment;
import com.scichart.examples.utils.Constant;
import com.scichart.examples.utils.interpolator.DefaultInterpolator;

import java.util.Collections;

public class ImpulseChartFragment extends ExampleSingleChartBaseFragment {

    @Override
    protected void initExample(@NonNull SciChartSurface surface) {
        final IAxis xBottomAxis = sciChartBuilder.newNumericAxis().withGrowBy(0.1d, 0.1d).build();
        final IAxis yRightAxis = sciChartBuilder.newNumericAxis().withGrowBy(0.1d, 0.1d).build();

        final DoubleSeries ds1Points = DataManager.getInstance().getDampedSinewave(1.0, 0.05, 50, 5);
        final IXyDataSeries<Double, Double> dataSeries = sciChartBuilder.newXyDataSeries(Double.class, Double.class).build();

        for (int i = 0; i <= 70; i++) {
            dataSeries.append((double) i, Math.sin(i * 0.2) * -Math.log((float) i / 100));
        }

        final EllipsePointMarker pointMarker = sciChartBuilder.newPointMarker(new EllipsePointMarker())
                .withSize(10, 10)
                .withStroke(ColorUtil.argb(0xFF, 0xEC, 0x0F, 0x6C), 1)
                .withFill(ColorUtil.argb(0xFF, 0xEC, 0x0F, 0x6C))
                .build();

        final FastImpulseRenderableSeries rSeries = sciChartBuilder.newImpulseSeries()
                .withDataSeries(dataSeries)
                .withStrokeStyle(0xFFEC0F6C)
                .withPointMarker(pointMarker)
                .build();

        UpdateSuspender.using(surface, () -> {
            Collections.addAll(surface.getXAxes(), xBottomAxis);
            Collections.addAll(surface.getYAxes(), yRightAxis);
            Collections.addAll(surface.getRenderableSeries(), rSeries);
            Collections.addAll(surface.getChartModifiers(), sciChartBuilder.newModifierGroupWithDefaultModifiers().build());

            sciChartBuilder.newAnimator(rSeries).withWaveTransformation().withInterpolator(DefaultInterpolator.getInterpolator()).withDuration(Constant.ANIMATION_DURATION).withStartDelay(Constant.ANIMATION_START_DELAY).start();
        });
    }
}