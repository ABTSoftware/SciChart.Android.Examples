//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2021. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// UsePointMarkersFragment.java is part of SCICHART®, High Performance Scientific Charts
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

package com.scichart.examples.fragments.examples2d.stylingAndTheming;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;

import com.scichart.charting.model.dataSeries.IXyDataSeries;
import com.scichart.charting.model.dataSeries.XyDataSeries;
import com.scichart.charting.visuals.SciChartSurface;
import com.scichart.charting.visuals.pointmarkers.CrossPointMarker;
import com.scichart.charting.visuals.pointmarkers.EllipsePointMarker;
import com.scichart.charting.visuals.pointmarkers.IPointMarker;
import com.scichart.charting.visuals.pointmarkers.SpritePointMarker;
import com.scichart.charting.visuals.pointmarkers.SquarePointMarker;
import com.scichart.charting.visuals.pointmarkers.TrianglePointMarker;
import com.scichart.charting.visuals.renderableSeries.IRenderableSeries;
import com.scichart.data.model.DoubleRange;
import com.scichart.drawing.utility.ColorUtil;
import com.scichart.examples.R;
import com.scichart.examples.fragments.base.ExampleSingleChartBaseFragment;
import com.scichart.examples.utils.Constant;

import java.util.Collections;
import java.util.Random;

public class UsePointMarkersFragment extends ExampleSingleChartBaseFragment {
    private static final int dataSize = 15;
    private final Random rnd = new Random();

    @Override
    protected void initExample(@NonNull SciChartSurface surface) {
        final IXyDataSeries<Double, Double> ds1 = new XyDataSeries<>(Double.class, Double.class);
        final IXyDataSeries<Double, Double> ds2 = new XyDataSeries<>(Double.class, Double.class);
        final IXyDataSeries<Double, Double> ds3 = new XyDataSeries<>(Double.class, Double.class);
        final IXyDataSeries<Double, Double> ds4 = new XyDataSeries<>(Double.class, Double.class);
        final IXyDataSeries<Double, Double> ds5 = new XyDataSeries<>(Double.class, Double.class);

        fillDataSeries(ds1, 0);
        fillDataSeries(ds2, 1);
        fillDataSeries(ds3, 2.5);
        fillDataSeries(ds4, 4);
        fillDataSeries(ds5, 5.5);

        final IPointMarker pointMarker1 = sciChartBuilder.newPointMarker(new EllipsePointMarker()).withSize(15,15).withFill(0x990077ff).withStroke(ColorUtil.LightBlue, 2).build();
        final IPointMarker pointMarker2 = sciChartBuilder.newPointMarker(new SquarePointMarker()).withSize(20, 20).withFill(0x99ff0000).withStroke(ColorUtil.Red, 2).build();
        final IPointMarker pointMarker3 = sciChartBuilder.newPointMarker(new TrianglePointMarker()).withSize(20, 20).withFill(0xffffdd00).withStroke(0xffff6600, 2).build();
        final IPointMarker pointMarker4 = sciChartBuilder.newPointMarker(new CrossPointMarker()).withSize(25, 25).withStroke(ColorUtil.Magenta, 15).build();
        final IPointMarker pointMarker5 = sciChartBuilder.newPointMarker(new SpritePointMarker(new CustomPointMarkerDrawer(requireContext(), R.drawable.example_weather_storm))).withSize(40, 40).build();

        IRenderableSeries rs1 = sciChartBuilder.newLineSeries().withDataSeries(ds1).withPointMarker(pointMarker1).withStrokeStyle(ColorUtil.LightBlue, 2f).build();
        IRenderableSeries rs2 = sciChartBuilder.newLineSeries().withDataSeries(ds2).withPointMarker(pointMarker2).withStrokeStyle(ColorUtil.Red, 2f).build();
        IRenderableSeries rs3 = sciChartBuilder.newLineSeries().withDataSeries(ds3).withPointMarker(pointMarker3).withStrokeStyle(ColorUtil.Yellow, 2f).build();
        IRenderableSeries rs4 = sciChartBuilder.newLineSeries().withDataSeries(ds4).withPointMarker(pointMarker4).withStrokeStyle(ColorUtil.Magenta, 2f).build();
        IRenderableSeries rs5 = sciChartBuilder.newLineSeries().withDataSeries(ds5).withPointMarker(pointMarker5).withStrokeStyle(ColorUtil.Wheat, 2f).build();

        Collections.addAll(surface.getXAxes(), sciChartBuilder.newNumericAxis().withGrowBy(new DoubleRange(0.1, 0.1)).build());
        Collections.addAll(surface.getYAxes(), sciChartBuilder.newNumericAxis().withGrowBy(new DoubleRange(0.1, 0.1)).build());
        Collections.addAll(surface.getRenderableSeries(), rs1, rs2, rs3, rs4, rs5);
        Collections.addAll(surface.getChartModifiers(), sciChartBuilder.newModifierGroupWithDefaultModifiers().build());

        sciChartBuilder.newOpacityAnimator(rs1).withDuration(Constant.ANIMATION_DURATION).withStartDelay(Constant.ANIMATION_START_DELAY).start();
        sciChartBuilder.newOpacityAnimator(rs2).withDuration(Constant.ANIMATION_DURATION).withStartDelay(Constant.ANIMATION_START_DELAY).start();
        sciChartBuilder.newOpacityAnimator(rs3).withDuration(Constant.ANIMATION_DURATION).withStartDelay(Constant.ANIMATION_START_DELAY).start();
        sciChartBuilder.newOpacityAnimator(rs4).withDuration(Constant.ANIMATION_DURATION).withStartDelay(Constant.ANIMATION_START_DELAY).start();
        sciChartBuilder.newOpacityAnimator(rs5).withDuration(Constant.ANIMATION_DURATION).withStartDelay(Constant.ANIMATION_START_DELAY).start();
    }

    private void fillDataSeries(IXyDataSeries<Double, Double> dataSeries, double offset) {
        for (int i = 0; i < dataSize; i++) {
            dataSeries.append((double)i, offset + rnd.nextDouble());
        }
        dataSeries.updateYAt(7, Double.NaN);
    }

    private static class CustomPointMarkerDrawer implements SpritePointMarker.ISpritePointMarkerDrawer {
        private final Drawable drawable;

        CustomPointMarkerDrawer(Context context, @DrawableRes int drawableId) {
            this.drawable = ResourcesCompat.getDrawable(context.getResources(), drawableId, null);
        }

        @Override
        public void onDraw(Canvas canvas, Paint stroke, Paint fill) {
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
        }
    }
}