//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2021. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// ScatterChartFillFragment.java is part of SCICHART®, High Performance Scientific Charts
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

package com.scichart.examples.fragments.examples2d.aTest;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.annotation.NonNull;

import com.scichart.charting.model.dataSeries.IXyDataSeries;
import com.scichart.charting.model.dataSeries.XyDataSeries;
import com.scichart.charting.visuals.SciChartSurface;
import com.scichart.charting.visuals.pointmarkers.DrawablePointMarker;
import com.scichart.charting.visuals.pointmarkers.IPointMarker;
import com.scichart.charting.visuals.renderableSeries.IRenderableSeries;
import com.scichart.data.model.DoubleRange;
import com.scichart.drawing.common.IBrush2D;
import com.scichart.drawing.common.IPen2D;
import com.scichart.drawing.common.IRenderContext2D;
import com.scichart.drawing.common.RadialGradientBrushStyle;
import com.scichart.drawing.common.TextureBrushStyle;
import com.scichart.drawing.utility.ColorUtil;
import com.scichart.examples.R;
import com.scichart.examples.fragments.base.ExampleSingleChartBaseFragment;

import java.util.Collections;
import java.util.Random;

public class ScatterChartFillFragment extends ExampleSingleChartBaseFragment {

    @Override
    protected void initExample(@NonNull SciChartSurface surface) {
        final Bitmap texture = BitmapFactory.decodeResource(getResources(), R.drawable.example_scichartlogo);

        final IXyDataSeries<Double, Double> ds1 = new XyDataSeries<>(Double.class, Double.class);
        final IXyDataSeries<Double, Double> ds2 = new XyDataSeries<>(Double.class, Double.class);
        final IXyDataSeries<Double, Double> ds3 = new XyDataSeries<>(Double.class, Double.class);
        final IXyDataSeries<Double, Double> ds4 = new XyDataSeries<>(Double.class, Double.class);
        final IXyDataSeries<Double, Double> ds5 = new XyDataSeries<>(Double.class, Double.class);

        final int dataSize = 10;
        Random rnd = new Random();

        for (int i = 0; i < dataSize; i++) {
            ds1.append((double) i, rnd.nextDouble());
            ds2.append((double) i, 1 + rnd.nextDouble());
            ds3.append((double) i, 1.8 + rnd.nextDouble());
            ds4.append((double) i, 2.5 + rnd.nextDouble());
            ds5.append((double) i, 3.5 + rnd.nextDouble());
        }

        final IPointMarker pointMarker1 = sciChartBuilder.newPointMarker(new EllipsePointMarker()).withSize(50).withFill(0x990077ff).withStroke(ColorUtil.Blue, 2).build();
        final IPointMarker pointMarker2 = sciChartBuilder.newPointMarker(new EllipsePointMarker()).withSize(60).withFill(0x99ff0000).withStroke(ColorUtil.Red, 2).build();
        final IPointMarker pointMarker3 = sciChartBuilder.newPointMarker(new EllipsePointMarker()).withSize(60).withFill(new RadialGradientBrushStyle(0.5f, 0.5f, 0.4f, 0.4f, ColorUtil.Red, ColorUtil.Green)).withStroke(0xffff6600, 2).build();
        final IPointMarker pointMarker4 = sciChartBuilder.newPointMarker(new EllipsePointMarker()).withSize(65).withStroke(ColorUtil.Magenta, 4).build();
        final IPointMarker pointMarker5 = sciChartBuilder.newPointMarker(new EllipsePointMarker()).withSize(75).withFill(new TextureBrushStyle(texture)).withStroke(sciChartBuilder.newPen().withColor(ColorUtil.Red).withThickness(4f).withStrokeDashArray(new float[]{2, 3, 4, 5}).build()).build();

        IRenderableSeries rs1 = sciChartBuilder.newLineSeries().withDataSeries(ds1).withPointMarker(pointMarker1).withStrokeStyle(ColorUtil.Blue).build();
        IRenderableSeries rs2 = sciChartBuilder.newLineSeries().withDataSeries(ds2).withPointMarker(pointMarker2).withStrokeStyle(ColorUtil.Red).build();
        IRenderableSeries rs3 = sciChartBuilder.newLineSeries().withDataSeries(ds3).withPointMarker(pointMarker3).withStrokeStyle(ColorUtil.Yellow).build();
        IRenderableSeries rs4 = sciChartBuilder.newLineSeries().withDataSeries(ds4).withPointMarker(pointMarker4).withStrokeStyle(ColorUtil.Magenta).build();
        IRenderableSeries rs5 = sciChartBuilder.newLineSeries().withDataSeries(ds5).withPointMarker(pointMarker5).withStrokeStyle(ColorUtil.Olive).build();

        Collections.addAll(surface.getXAxes(), sciChartBuilder.newNumericAxis().withGrowBy(new DoubleRange(0.1, 0.1)).build());
        Collections.addAll(surface.getYAxes(), sciChartBuilder.newNumericAxis().withGrowBy(new DoubleRange(0.1, 0.1)).build());
        Collections.addAll(surface.getRenderableSeries(), rs1, rs2, rs3, rs4, rs5);
        Collections.addAll(surface.getChartModifiers(), sciChartBuilder.newModifierGroupWithDefaultModifiers().build());
    }

    private static final class EllipsePointMarker extends DrawablePointMarker {
        @Override
        protected void internalDraw(IRenderContext2D renderContext, float x, float y, IPen2D strokePen, IBrush2D fillBrush) {
            renderContext.drawEllipse(x, y, getWidth(), getHeight(), strokePen, fillBrush);
        }
    }
}
