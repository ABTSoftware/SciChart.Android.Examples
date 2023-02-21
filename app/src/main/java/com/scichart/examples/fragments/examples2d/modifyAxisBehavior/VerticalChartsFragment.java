//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2021. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// VerticalChartsFragment.java is part of SCICHART®, High Performance Scientific Charts
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
import com.scichart.data.model.DoubleRange;
import com.scichart.examples.R;
import com.scichart.examples.data.DataManager;
import com.scichart.examples.data.DoubleSeries;
import com.scichart.examples.fragments.base.ExampleSingleChartBaseFragment;
import com.scichart.examples.utils.Constant;
import com.scichart.examples.utils.interpolator.DefaultInterpolator;

import java.util.Collections;

public class VerticalChartsFragment extends ExampleSingleChartBaseFragment {

    @Override
    public boolean showDefaultModifiersInToolbar() {
        return false;
    }

    @Override
    protected void initExample(@NonNull SciChartSurface surface) {
        surface.setTheme(R.style.SciChart_NavyBlue);

        final IAxis xAxis = sciChartBuilder.newNumericAxis().withAxisAlignment(AxisAlignment.Left).withAxisTitle("X-Axis").build();
        final IAxis yAxis = sciChartBuilder.newNumericAxis().withAxisAlignment(AxisAlignment.Top).withAxisTitle("Y-Axis").withGrowBy(new DoubleRange(0d, 0.1d)).build();

        final DoubleSeries ds1Points = DataManager.getInstance().getRandomDoubleSeries(20);
        final DoubleSeries ds2Points = DataManager.getInstance().getRandomDoubleSeries(20);

        IXyDataSeries<Double, Double> dataSeries0 = sciChartBuilder.newXyDataSeries(Double.class, Double.class).build();
        IXyDataSeries<Double, Double> dataSeries1 = sciChartBuilder.newXyDataSeries(Double.class, Double.class).build();

        dataSeries0.append(ds1Points.xValues, ds1Points.yValues);
        dataSeries1.append(ds2Points.xValues, ds2Points.yValues);

        final FastLineRenderableSeries lineSeries0 = sciChartBuilder.newLineSeries()
                .withDataSeries(dataSeries0)
                .withStrokeStyle(0xFFe97064, 2, true)
                .build();

        final FastLineRenderableSeries lineSeries1 = sciChartBuilder.newLineSeries()
                .withDataSeries(dataSeries1)
                .withStrokeStyle(0xFF274b92, 2, true)
                .build();

        UpdateSuspender.using(surface, () -> {
            Collections.addAll(surface.getXAxes(), xAxis);
            Collections.addAll(surface.getYAxes(), yAxis);
            Collections.addAll(surface.getRenderableSeries(), lineSeries0, lineSeries1);
            Collections.addAll(surface.getChartModifiers(), sciChartBuilder.newModifierGroupWithDefaultModifiers().build());

            sciChartBuilder.newAnimator(lineSeries0).withSweepTransformation().withInterpolator(DefaultInterpolator.getInterpolator()).withDuration(Constant.ANIMATION_DURATION).withStartDelay(Constant.ANIMATION_START_DELAY).start();
            sciChartBuilder.newAnimator(lineSeries1).withSweepTransformation().withInterpolator(DefaultInterpolator.getInterpolator()).withDuration(Constant.ANIMATION_DURATION).withStartDelay(Constant.ANIMATION_START_DELAY).start();
        });
    }
}