//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2021. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// FanChartFragment.java is part of SCICHART®, High Performance Scientific Charts
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

import com.scichart.charting.model.dataSeries.XyDataSeries;
import com.scichart.charting.model.dataSeries.XyyDataSeries;
import com.scichart.charting.visuals.SciChartSurface;
import com.scichart.charting.visuals.axes.IAxis;
import com.scichart.charting.visuals.renderableSeries.FastBandRenderableSeries;
import com.scichart.charting.visuals.renderableSeries.FastLineRenderableSeries;
import com.scichart.core.framework.UpdateSuspender;
import com.scichart.core.utility.IterableUtil;
import com.scichart.drawing.utility.ColorUtil;
import com.scichart.examples.data.RandomWalkGenerator;
import com.scichart.examples.fragments.base.ExampleSingleChartBaseFragment;
import com.scichart.examples.utils.Constant;
import com.scichart.examples.utils.interpolator.DefaultInterpolator;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class FanChartFragment extends ExampleSingleChartBaseFragment {

    @Override
    protected void initExample(@NonNull SciChartSurface surface) {
        final IAxis xAxis = sciChartBuilder.newDateAxis().withGrowBy(0.1, 0.1).build();
        final IAxis yAxis = sciChartBuilder.newNumericAxis().withGrowBy(0.1, 0.1).build();

        final XyDataSeries<Date, Double> actualDataSeries = sciChartBuilder.newXyDataSeries(Date.class, Double.class).build();
        final XyyDataSeries<Date, Double> var3DataSeries = sciChartBuilder.newXyyDataSeries(Date.class, Double.class).build();
        final XyyDataSeries<Date, Double> var2DataSeries = sciChartBuilder.newXyyDataSeries(Date.class, Double.class).build();
        final XyyDataSeries<Date, Double> var1DataSeries = sciChartBuilder.newXyyDataSeries(Date.class, Double.class).build();

        final List<VarPoint> varianceData = getVarianceData();
        for (int i = 0; i < varianceData.size(); i++) {
            final VarPoint dataPoint = varianceData.get(i);

            actualDataSeries.append(dataPoint.date, dataPoint.actual);
            var3DataSeries.append(dataPoint.date, dataPoint.varMin, dataPoint.varMax);
            var2DataSeries.append(dataPoint.date, dataPoint.var1, dataPoint.var4);
            var1DataSeries.append(dataPoint.date, dataPoint.var2, dataPoint.var3);
        }

        final FastBandRenderableSeries projectedVar3 = sciChartBuilder.newBandSeries().withDataSeries(var3DataSeries).withStrokeY1Style(ColorUtil.Transparent).withStrokeStyle(ColorUtil.Transparent).build();
        final FastBandRenderableSeries projectedVar2 = sciChartBuilder.newBandSeries().withDataSeries(var2DataSeries).withStrokeY1Style(ColorUtil.Transparent).withStrokeStyle(ColorUtil.Transparent).build();
        final FastBandRenderableSeries projectedVar = sciChartBuilder.newBandSeries().withDataSeries(var1DataSeries).withStrokeY1Style(ColorUtil.Transparent).withStrokeStyle(ColorUtil.Transparent).build();
        final FastLineRenderableSeries lineSeries = sciChartBuilder.newLineSeries().withDataSeries(actualDataSeries).withStrokeStyle(0xFFe97064, 1f).build();

        UpdateSuspender.using(surface, () -> {
            Collections.addAll(surface.getXAxes(), xAxis);
            Collections.addAll(surface.getYAxes(), yAxis);
            Collections.addAll(surface.getRenderableSeries(), projectedVar3, projectedVar2, projectedVar, lineSeries);
            Collections.addAll(surface.getChartModifiers(), sciChartBuilder.newModifierGroupWithDefaultModifiers().build());

            sciChartBuilder.newAnimator(lineSeries).withSweepTransformation(true).withInterpolator(DefaultInterpolator.getInterpolator()).withDuration(Constant.ANIMATION_DURATION).withStartDelay(Constant.ANIMATION_START_DELAY).start();
            sciChartBuilder.newAnimator(projectedVar).withSweepTransformation(true).withInterpolator(DefaultInterpolator.getInterpolator()).withDuration(Constant.ANIMATION_DURATION).withStartDelay(Constant.ANIMATION_START_DELAY).start();
            sciChartBuilder.newAnimator(projectedVar2).withSweepTransformation(true).withInterpolator(DefaultInterpolator.getInterpolator()).withDuration(Constant.ANIMATION_DURATION).withStartDelay(Constant.ANIMATION_START_DELAY).start();
            sciChartBuilder.newAnimator(projectedVar3).withSweepTransformation(true).withInterpolator(DefaultInterpolator.getInterpolator()).withDuration(Constant.ANIMATION_DURATION).withStartDelay(Constant.ANIMATION_START_DELAY).start();
        });
    }

    // Create a table of Variance data. Each row in the table consists of
    //
    //  DateTime, Actual (Y-Value), Projected Min, Variance 1, 2, 3, 4 and Projected Maximum
    //
    //        DateTime    Actual 	Min     Var1	Var2	Var3	Var4	Max
    //        Jan-11	  y0	    -	    -	    -	    -	    -	    -
    //        Feb-11	  y1	    -	    -	    -	    -	    -	    -
    //        Mar-11	  y2	    -	    -	    -	    -	    -	    -
    //        Apr-11	  y3	    -	    -	    -	    -	    -	    -
    //        May-11	  y4	    -	    -	    -	    -	    -	    -
    //        Jun-11	  y5        min0  var1_0  var2_0  var3_0  var4_0  max_0
    //        Jul-11	  y6        min1  var1_1  var2_1  var3_1  var4_1  max_1
    //        Aug-11	  y7        min2  var1_2  var2_2  var3_2  var4_2  max_2
    //        Dec-11	  y8        min3  var1_3  var2_3  var3_3  var4_3  max_3
    //        Jan-12      y9        min4  var1_4  var2_4  var3_4  var4_4  max_4

    private static List<VarPoint> getVarianceData() {
        final int count = 10;

        final Date[] dates = IterableUtil.toArray(IterableUtil.range(0, count, arg -> {
            final Calendar instance = Calendar.getInstance();

            instance.clear();
            instance.set(2011, 1, 1);
            instance.add(Calendar.MONTH, arg);

            return instance.getTime();
        }), Date.class);

        final double[] yValues = new RandomWalkGenerator().getRandomWalkSeries(count).yValues.getItemsArray();

        final List<VarPoint> result = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            double varMax = Double.NaN;
            double var4 = Double.NaN;
            double var3 = Double.NaN;
            double var2 = Double.NaN;
            double var1 = Double.NaN;
            double varMin = Double.NaN;

            if (i > 4) {
                varMax = yValues[i] + (i - 5) * 0.3;
                var4 = yValues[i] + (i - 5) * 0.2;
                var3 = yValues[i] + (i - 5) * 0.1;
                var2 = yValues[i] - (i - 5) * 0.1;
                var1 = yValues[i] - (i - 5) * 0.2;
                varMin = yValues[i] - (i - 5) * 0.3;
            }

            result.add(new VarPoint(dates[i], yValues[i], var4, var3, var2, var1, varMin, varMax));
        }

        return result;
    }

    private static class VarPoint {
        public final Date date;
        public final double actual;
        public final double varMax;
        public final double var4;
        public final double var3;
        public final double var2;
        public final double var1;
        public final double varMin;

        public VarPoint(Date date, double actual, double var4, double var3, double var2, double var1, double varMin, double varMax) {
            this.date = date;
            this.actual = actual;
            this.var4 = var4;
            this.var3 = var3;
            this.var2 = var2;
            this.var1 = var1;
            this.varMin = varMin;
            this.varMax = varMax;
        }
    }
}