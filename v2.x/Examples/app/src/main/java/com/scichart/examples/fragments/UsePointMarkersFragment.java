//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2017. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// UsePointMarkersFragment.java is part of the SCICHART® Examples. Permission is hereby granted
// to modify, create derivative works, distribute and publish any part of this source
// code whether for commercial, private or personal use.
//
// The SCICHART® examples are distributed in the hope that they will be useful, but
// without any warranty. It is provided "AS IS" without warranty of any kind, either
// expressed or implied.
//******************************************************************************

package com.scichart.examples.fragments;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;

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
import com.scichart.examples.fragments.base.ExampleBaseFragment;

import java.util.Collections;
import java.util.Random;

import butterknife.Bind;

public class UsePointMarkersFragment extends ExampleBaseFragment {

    @Bind(R.id.chart)
    SciChartSurface surface;

    @Override
    protected int getLayoutId() {
        return R.layout.example_single_chart_fragment;
    }

    @Override
    protected void initExample() {
        final IXyDataSeries<Double, Double> ds1 = new XyDataSeries<>(Double.class, Double.class);
        final IXyDataSeries<Double, Double> ds2 = new XyDataSeries<>(Double.class, Double.class);
        final IXyDataSeries<Double, Double> ds3 = new XyDataSeries<>(Double.class, Double.class);
        final IXyDataSeries<Double, Double> ds4 = new XyDataSeries<>(Double.class, Double.class);
        final IXyDataSeries<Double, Double> ds5 = new XyDataSeries<>(Double.class, Double.class);

        final int dataSize = 15;
        Random rnd = new Random();

        for (int i = 0; i < dataSize; i++) {
            ds1.append((double)i, rnd.nextDouble());
            ds2.append((double)i, 1+rnd.nextDouble());
            ds3.append((double)i, 2.5+rnd.nextDouble());
            ds4.append((double)i, 4+rnd.nextDouble());
            ds5.append((double)i, 5.5+rnd.nextDouble());
        }

        ds1.updateYAt(7, Double.NaN);
        ds2.updateYAt(7, Double.NaN);
        ds3.updateYAt(7, Double.NaN);
        ds4.updateYAt(7, Double.NaN);
        ds5.updateYAt(7, Double.NaN);

        final IPointMarker pointMarker1 = sciChartBuilder.newPointMarker(new EllipsePointMarker()).withSize(15,15).withFill(0x990077ff).withStroke(ColorUtil.LightBlue, 2).build();
        final IPointMarker pointMarker2 = sciChartBuilder.newPointMarker(new SquarePointMarker()).withSize(20, 20).withFill(0x99ff0000).withStroke(ColorUtil.Red, 2).build();
        final IPointMarker pointMarker3 = sciChartBuilder.newPointMarker(new TrianglePointMarker()).withSize(20, 20).withFill(0xffffdd00).withStroke(0xffff6600, 2).build();
        final IPointMarker pointMarker4 = sciChartBuilder.newPointMarker(new CrossPointMarker()).withSize(25, 25).withStroke(ColorUtil.Magenta, 4).build();
        final IPointMarker pointMarker5 = sciChartBuilder.newPointMarker(new SpritePointMarker(new CustomPointMarkerDrawer(getActivity(), R.drawable.example_weather_storm))).withSize(40, 40).build();

        IRenderableSeries rs1 = sciChartBuilder.newLineSeries().withDataSeries(ds1).withPointMarker(pointMarker1).withStrokeStyle(ColorUtil.LightBlue, 2f).build();
        IRenderableSeries rs2 = sciChartBuilder.newLineSeries().withDataSeries(ds2).withPointMarker(pointMarker2).withStrokeStyle(ColorUtil.Red, 2f).build();
        IRenderableSeries rs3 = sciChartBuilder.newLineSeries().withDataSeries(ds3).withPointMarker(pointMarker3).withStrokeStyle(ColorUtil.Yellow, 2f).build();
        IRenderableSeries rs4 = sciChartBuilder.newLineSeries().withDataSeries(ds4).withPointMarker(pointMarker4).withStrokeStyle(ColorUtil.Magenta, 2f).build();
        IRenderableSeries rs5 = sciChartBuilder.newLineSeries().withDataSeries(ds5).withPointMarker(pointMarker5).withStrokeStyle(ColorUtil.Wheat, 2f).build();

        Collections.addAll(surface.getXAxes(), sciChartBuilder.newNumericAxis().withGrowBy(new DoubleRange(0.1, 0.1)).build());
        Collections.addAll(surface.getYAxes(), sciChartBuilder.newNumericAxis().withGrowBy(new DoubleRange(0.1, 0.1)).build());
        Collections.addAll(surface.getRenderableSeries(), rs1, rs2, rs3, rs4, rs5);
        Collections.addAll(surface.getChartModifiers(), sciChartBuilder.newModifierGroupWithDefaultModifiers().build());
    }

    private class CustomPointMarkerDrawer implements SpritePointMarker.ISpritePointMarkerDrawer {
        private final Drawable drawable;

        private CustomPointMarkerDrawer(Context context, @DrawableRes int drawableId) {
            this.drawable = context.getResources().getDrawable(drawableId);
        }

        @Override
        public void onDraw(Canvas canvas, Paint stroke, Paint fill) {
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
        }
    }

}
