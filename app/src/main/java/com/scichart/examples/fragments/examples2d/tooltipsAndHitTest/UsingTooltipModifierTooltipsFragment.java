//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2021. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// UsingTooltipModifierTooltipsFragment.java is part of SCICHART®, High Performance Scientific Charts
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

package com.scichart.examples.fragments.examples2d.tooltipsAndHitTest;

import androidx.annotation.NonNull;

import com.scichart.charting.model.dataSeries.XyDataSeries;
import com.scichart.charting.modifiers.TooltipModifier;
import com.scichart.charting.visuals.SciChartSurface;
import com.scichart.charting.visuals.axes.AxisAlignment;
import com.scichart.charting.visuals.axes.IAxis;
import com.scichart.charting.visuals.pointmarkers.EllipsePointMarker;
import com.scichart.charting.visuals.renderableSeries.FastLineRenderableSeries;
import com.scichart.core.framework.UpdateSuspender;
import com.scichart.core.model.DoubleValues;
import com.scichart.examples.R;
import com.scichart.examples.data.DataManager;
import com.scichart.examples.data.DoubleSeries;
import com.scichart.examples.fragments.base.ExampleSingleChartBaseFragment;

import java.util.Collections;

public class UsingTooltipModifierTooltipsFragment extends ExampleSingleChartBaseFragment {

    @Override
    public boolean showDefaultModifiersInToolbar() {
        return false;
    }

    @Override
    protected void initExample(@NonNull SciChartSurface surface) {
        surface.setTheme(R.style.SciChart_NavyBlue);

        final IAxis xAxis = sciChartBuilder.newNumericAxis().withGrowBy(0.1, 0.1).build();
        final IAxis yAxis = sciChartBuilder.newNumericAxis().withGrowBy(0.1, 0.1).withAxisAlignment(AxisAlignment.Left).build();

        final XyDataSeries<Double, Double> dataSeries1 = sciChartBuilder.newXyDataSeries(Double.class, Double.class).withSeriesName("Lissajous Curve").withAcceptsUnsortedData().build();
        final XyDataSeries<Double, Double> dataSeries2 = sciChartBuilder.newXyDataSeries(Double.class, Double.class).withSeriesName("SineWave").withAcceptsUnsortedData().build();

        final DoubleSeries ds1Points = DataManager.getInstance().getLissajousCurve(0.8, 0.2, 0.43, 500);
        final DoubleSeries ds2Points = DataManager.getInstance().getSinewave(1.5, 1.0, 500);

        final DoubleValues scaledXValues = getScaledValues(ds1Points.xValues);

        dataSeries1.append(scaledXValues, ds1Points.yValues);
        dataSeries2.append(ds2Points.xValues, ds2Points.yValues);

        final FastLineRenderableSeries line1 = sciChartBuilder.newLineSeries()
                .withStrokeStyle(0xFF47bde6, 1f, true)
                .withPointMarker(sciChartBuilder.newPointMarker(new EllipsePointMarker()).withSize(5).withStroke(0xFF47bde6, 2f).withFill(0xFF47bde6).build())
                .withDataSeries(dataSeries1)
                .build();
        final FastLineRenderableSeries line2 = sciChartBuilder.newLineSeries()
                .withStrokeStyle(0xFFae418d, 1f, true)
                .withPointMarker(sciChartBuilder.newPointMarker(new EllipsePointMarker()).withSize(5).withStroke(0xFFae418d, 2f).withFill(0xFFae418d).build())
                .withDataSeries(dataSeries2)
                .build();

        UpdateSuspender.using(surface, () -> {
            Collections.addAll(surface.getXAxes(), xAxis);
            Collections.addAll(surface.getYAxes(), yAxis);
            Collections.addAll(surface.getRenderableSeries(), line1, line2);
            Collections.addAll(surface.getChartModifiers(), new TooltipModifier());

            sciChartBuilder.newOpacityAnimator(line1).withDuration(1000).withStartDelay(350).start();
            sciChartBuilder.newOpacityAnimator(line2).withDuration(1000).withStartDelay(350).start();
        });
    }

    private DoubleValues getScaledValues(DoubleValues values) {
        final DoubleValues result = new DoubleValues();
        for (double value : values.getItemsArray()) {
            result.add((value + 1) * 5);
        }
        return result;
    }
}