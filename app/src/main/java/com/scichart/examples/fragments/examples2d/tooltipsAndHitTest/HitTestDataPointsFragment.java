//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2021. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// HitTestDatapointsFragment.java is part of SCICHART®, High Performance Scientific Charts
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


import android.annotation.SuppressLint;
import android.graphics.PointF;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.scichart.charting.model.dataSeries.IOhlcDataSeries;
import com.scichart.charting.model.dataSeries.IXyDataSeries;
import com.scichart.charting.visuals.SciChartSurface;
import com.scichart.charting.visuals.axes.AxisAlignment;
import com.scichart.charting.visuals.axes.IAxis;
import com.scichart.charting.visuals.pointmarkers.EllipsePointMarker;
import com.scichart.charting.visuals.renderableSeries.FastCandlestickRenderableSeries;
import com.scichart.charting.visuals.renderableSeries.FastColumnRenderableSeries;
import com.scichart.charting.visuals.renderableSeries.FastLineRenderableSeries;
import com.scichart.charting.visuals.renderableSeries.FastMountainRenderableSeries;
import com.scichart.charting.visuals.renderableSeries.IRenderableSeries;
import com.scichart.charting.visuals.renderableSeries.hitTest.HitTestInfo;
import com.scichart.core.framework.UpdateSuspender;
import com.scichart.core.utility.ListUtil;
import com.scichart.data.model.DoubleRange;
import com.scichart.drawing.utility.ColorUtil;
import com.scichart.examples.R;
import com.scichart.examples.fragments.base.ExampleSingleChartBaseFragment;
import com.scichart.examples.utils.Constant;
import com.scichart.examples.utils.interpolator.DefaultInterpolator;

import java.util.Collections;

@SuppressLint("ClickableViewAccessibility")
public class HitTestDataPointsFragment extends ExampleSingleChartBaseFragment implements View.OnTouchListener {
    private Toast toast;

    private final PointF touchPoint = new PointF();
    private final HitTestInfo hitTestInfo = new HitTestInfo();

    @Override
    public boolean showDefaultModifiersInToolbar() {
        return false;
    }

    @Override
    protected void initExample(@NonNull SciChartSurface surface) {
        surface.setTheme(R.style.SciChart_NavyBlue);

        final Double[] xValues = {0d, 1d, 2d, 3d, 4d, 5d, 6d, 7d, 8d, 9d};
        final Double[] yValues = {0d, 0.1d, 0.3d, 0.5d, 0.4d, 0.35d, 0.3d, 0.25d, 0.2d, 0.1d, 0.05d};

        IXyDataSeries<Double, Double> dataSeries0 = sciChartBuilder.newXyDataSeries(Double.class, Double.class).withSeriesName("Line Series").build();
        IXyDataSeries<Double, Double> dataSeries1 = sciChartBuilder.newXyDataSeries(Double.class, Double.class).withSeriesName("Mountain Series").build();
        IXyDataSeries<Double, Double> dataSeries2 = sciChartBuilder.newXyDataSeries(Double.class, Double.class).withSeriesName("Column Series").build();
        IOhlcDataSeries<Double, Double> dataSeries3 = sciChartBuilder.newOhlcDataSeries(Double.class, Double.class).withSeriesName("Candlestick Series").build();

        dataSeries0.append(xValues, yValues);
        dataSeries1.append(dataSeries0.getXValues(), ListUtil.select(dataSeries0.getYValues(), arg -> arg * 0.7d));
        dataSeries2.append(dataSeries0.getXValues(), ListUtil.select(dataSeries0.getYValues(), arg -> arg * 0.5d));
        dataSeries3.append(
                dataSeries0.getXValues(),
                ListUtil.select(dataSeries0.getYValues(), arg -> arg + 0.5d),
                ListUtil.select(dataSeries0.getYValues(), arg -> arg + 1d),
                ListUtil.select(dataSeries0.getYValues(), arg -> arg + 0.3d),
                ListUtil.select(dataSeries0.getYValues(), arg -> arg + 0.7d)
        );

        final IAxis xAxis = sciChartBuilder.newNumericAxis().withAxisAlignment(AxisAlignment.Bottom).build();
        final IAxis yAxis = sciChartBuilder.newNumericAxis().withAxisAlignment(AxisAlignment.Left).withGrowBy(new DoubleRange(0d, 0.1d)).build();

        final FastLineRenderableSeries lineSeries = sciChartBuilder.newLineSeries()
                .withDataSeries(dataSeries0)
                .withStrokeStyle(ColorUtil.SteelBlue, 3)
                .withPointMarker(sciChartBuilder.newPointMarker(new EllipsePointMarker())
                        .withSize(30, 30)
                        .withFill(ColorUtil.SteelBlue)
                        .withStroke(ColorUtil.Lavender, 2)
                        .build())
                .build();

        final FastMountainRenderableSeries mountainSeries = sciChartBuilder.newMountainSeries()
                .withStrokeStyle(ColorUtil.SteelBlue, 2f)
                .withAreaFillColor(ColorUtil.LightSteelBlue)
                .withDataSeries(dataSeries1)
                .build();

        final FastColumnRenderableSeries columnSeries = sciChartBuilder.newColumnSeries()
                .withDataSeries(dataSeries2)
                .build();

        final FastCandlestickRenderableSeries candlestickSeries = sciChartBuilder.newCandlestickSeries()
                .withDataSeries(dataSeries3)
                .build();

        UpdateSuspender.using(surface, () -> {
            Collections.addAll(surface.getXAxes(), xAxis);
            Collections.addAll(surface.getYAxes(), yAxis);
            Collections.addAll(surface.getRenderableSeries(), lineSeries, mountainSeries, columnSeries, candlestickSeries);

            sciChartBuilder.newAnimator(mountainSeries).withScaleTransformation().withInterpolator(DefaultInterpolator.getInterpolator()).withDuration(Constant.ANIMATION_DURATION).withStartDelay(Constant.ANIMATION_START_DELAY).start();
            sciChartBuilder.newAnimator(candlestickSeries).withScaleTransformation(0.3d).withInterpolator(DefaultInterpolator.getInterpolator()).withDuration(Constant.ANIMATION_DURATION).withStartDelay(Constant.ANIMATION_START_DELAY).start();
            sciChartBuilder.newAnimator(lineSeries).withScaleTransformation().withInterpolator(DefaultInterpolator.getInterpolator()).withDuration(Constant.ANIMATION_DURATION).withStartDelay(Constant.ANIMATION_START_DELAY).start();
            sciChartBuilder.newAnimator(columnSeries).withScaleTransformation().withInterpolator(DefaultInterpolator.getInterpolator()).withDuration(Constant.ANIMATION_DURATION).withStartDelay(Constant.ANIMATION_START_DELAY).start();
        });

        surface.setOnTouchListener(this);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        final SciChartSurface surface = (SciChartSurface) v;

        touchPoint.set(event.getX(), event.getY());
        surface.translatePoint(touchPoint, surface.getRenderableSeriesArea());

        final StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(String.format("Touch at: (%.1f, %.1f)", touchPoint.x, touchPoint.y));

        for (IRenderableSeries renderableSeries : surface.getRenderableSeries()) {
            renderableSeries.hitTest(hitTestInfo, touchPoint.x, touchPoint.y, 30);

            stringBuilder.append(String.format("\n%s - %s", renderableSeries.getClass().getSimpleName(), Boolean.toString(hitTestInfo.isHit)));
        }

        if(toast != null) toast.cancel();

        toast = Toast.makeText(getActivity(), null, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.TOP | Gravity.CENTER, 0, 0);
        toast.setText(stringBuilder.toString());
        toast.show();

        return true;
    }
}