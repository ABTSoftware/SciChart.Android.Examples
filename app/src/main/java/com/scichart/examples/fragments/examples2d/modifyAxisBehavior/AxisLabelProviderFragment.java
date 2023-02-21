//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2021. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// AxisLabelProviderFragment.java is part of SCICHART®, High Performance Scientific Charts
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

import android.graphics.Color;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;

import androidx.annotation.NonNull;

import com.scichart.charting.model.dataSeries.IXyDataSeries;
import com.scichart.charting.numerics.labelProviders.NumericLabelProvider;
import com.scichart.charting.visuals.SciChartSurface;
import com.scichart.charting.visuals.axes.NumericAxis;
import com.scichart.charting.visuals.renderableSeries.FastColumnRenderableSeries;
import com.scichart.core.framework.UpdateSuspender;
import com.scichart.data.model.DoubleRange;
import com.scichart.drawing.common.SolidBrushStyle;
import com.scichart.examples.fragments.base.ExampleSingleChartBaseFragment;
import com.scichart.examples.utils.Constant;
import com.scichart.examples.utils.TextLabelFormatter;
import com.scichart.examples.utils.interpolator.DefaultInterpolator;

import java.util.ArrayList;
import java.util.Collections;

public class AxisLabelProviderFragment extends ExampleSingleChartBaseFragment {


    @Override
    protected void initExample(@NonNull SciChartSurface surface) {
        ArrayList<String> labelList = new ArrayList();
        labelList.add("Sun");
        labelList.add("Mon");
        labelList.add("Tue");
        labelList.add("Wed");
        labelList.add("Thu");
        labelList.add("Fri");
        labelList.add("Sat");

        TextLabelFormatter textLabelFormatter = new TextLabelFormatter();
        textLabelFormatter.setList(labelList);

        NumericAxis xAxis = sciChartBuilder.newNumericAxis()
                .withVisibleRange(new DoubleRange(-0.5, 6.5))
                .withMaxAutoTicks(7)
                .withLabelProvider(new NumericLabelProvider(textLabelFormatter)).build();
        NumericAxis yAxis = sciChartBuilder.newNumericAxis().build();

        IXyDataSeries<Integer, Integer> ds1 = sciChartBuilder.newXyDataSeries(Integer.class, Integer.class).build();
        ds1.append(0, 5645);
        ds1.append(1, 1390);
        ds1.append(2, 2626);
        ds1.append(3, 9427);
        ds1.append(4, 513);
        ds1.append(5, 8737);
        ds1.append(6, 5987);

        FastColumnRenderableSeries fastColumnRenderableSeries = sciChartBuilder.newColumnSeries()
                .withDataSeries(ds1)
                .withDataPointWidth(0.75)
                .build();
        fastColumnRenderableSeries.setFillBrushStyle(new SolidBrushStyle(Color.CYAN));

        UpdateSuspender.using(surface, () -> {
            Collections.addAll(surface.getXAxes(), xAxis);
            Collections.addAll(surface.getYAxes(), yAxis);
            Collections.addAll(surface.getRenderableSeries(), fastColumnRenderableSeries);

            sciChartBuilder.newAnimator(fastColumnRenderableSeries).withWaveTransformation().withInterpolator(DefaultInterpolator.getInterpolator()).withDuration(Constant.ANIMATION_DURATION).withStartDelay(Constant.ANIMATION_START_DELAY).start();

        });
    }


}