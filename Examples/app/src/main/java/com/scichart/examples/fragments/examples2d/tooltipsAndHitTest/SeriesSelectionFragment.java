//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2021. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// SeriesSelectionFragment.java is part of SCICHART®, High Performance Scientific Charts
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

import android.view.animation.DecelerateInterpolator;

import androidx.annotation.NonNull;

import com.scichart.charting.model.dataSeries.IDataSeries;
import com.scichart.charting.model.dataSeries.XyDataSeries;
import com.scichart.charting.modifiers.SeriesSelectionModifier;
import com.scichart.charting.visuals.SciChartSurface;
import com.scichart.charting.visuals.axes.AutoRange;
import com.scichart.charting.visuals.axes.AxisAlignment;
import com.scichart.charting.visuals.axes.IAxis;
import com.scichart.charting.visuals.pointmarkers.EllipsePointMarker;
import com.scichart.charting.visuals.pointmarkers.IPointMarker;
import com.scichart.charting.visuals.renderableSeries.FastLineRenderableSeries;
import com.scichart.charting.visuals.renderableSeries.IRenderableSeries;
import com.scichart.charting.visuals.renderableSeries.StyleBase;
import com.scichart.core.framework.UpdateSuspender;
import com.scichart.drawing.common.PenStyle;
import com.scichart.drawing.utility.ColorUtil;
import com.scichart.examples.data.DataManager;
import com.scichart.examples.data.DoubleSeries;
import com.scichart.examples.fragments.base.ExampleSingleChartBaseFragment;

import java.util.Collections;

public class SeriesSelectionFragment extends ExampleSingleChartBaseFragment {
    private static final int SERIES_POINT_COUNT = 50;
    private static final int SERIES_COUNT = 80;

    @Override
    public boolean showDefaultModifiersInToolbar() {
        return false;
    }

    @Override
    protected void initExample(@NonNull SciChartSurface surface) {
        final IAxis xAxis = sciChartBuilder.newNumericAxis().withAxisAlignment(AxisAlignment.Bottom).withAutoRangeMode(AutoRange.Always).build();
        final IAxis leftAxis = sciChartBuilder.newNumericAxis().withAxisAlignment(AxisAlignment.Left).withAxisId(AxisAlignment.Left.name()).build();
        final IAxis rightAxis = sciChartBuilder.newNumericAxis().withAxisAlignment(AxisAlignment.Right).withAxisId(AxisAlignment.Right.name()).build();

        final SeriesSelectionModifier seriesSelectionModifier = new SeriesSelectionModifier();

        // set selected style
        seriesSelectionModifier.setSelectedSeriesStyle(new StyleBase<IRenderableSeries>(IRenderableSeries.class) {
            private final PenStyle selectedStrokeStyle = sciChartBuilder.newPen().withColor(ColorUtil.White).withThickness(4f).build();
            private final IPointMarker selectedPointMarker = sciChartBuilder.newPointMarker(new EllipsePointMarker()).withSize(10, 10).withFill(0xFFFF00DC).withStroke(ColorUtil.White, 1f).build();

            private static final String STROKE = "Stroke";
            private static final String POINT_MARKER = "PointMarker";

            @Override
            protected void applyStyleInternal(IRenderableSeries renderableSeriesToStyle) {
                putPropertyValue(renderableSeriesToStyle, STROKE, renderableSeriesToStyle.getStrokeStyle());
                putPropertyValue(renderableSeriesToStyle, POINT_MARKER, renderableSeriesToStyle.getPointMarker());

                renderableSeriesToStyle.setStrokeStyle(selectedStrokeStyle);
                renderableSeriesToStyle.setPointMarker(selectedPointMarker);
            }

            @Override
            protected void discardStyleInternal(IRenderableSeries renderableSeriesToStyle) {
                renderableSeriesToStyle.setStrokeStyle(getPropertyValue(renderableSeriesToStyle, STROKE, PenStyle.class));
                renderableSeriesToStyle.setPointMarker(getPropertyValue(renderableSeriesToStyle, POINT_MARKER, IPointMarker.class));
            }
        });

        UpdateSuspender.using(surface, () -> {
            Collections.addAll(surface.getXAxes(), xAxis);
            Collections.addAll(surface.getYAxes(), leftAxis, rightAxis);
            Collections.addAll(surface.getChartModifiers(), seriesSelectionModifier);

            int initialColor = ColorUtil.Blue;
            for (int i = 0; i < SERIES_COUNT; i++) {
                final AxisAlignment alignment = i % 2 == 0 ? AxisAlignment.Left : AxisAlignment.Right;

                final IDataSeries<?, ?> ds = generateDataSeries(alignment, i);
                final FastLineRenderableSeries rSeries = sciChartBuilder.newLineSeries()
                        .withYAxisId(alignment.name())
                        .withStrokeStyle(initialColor, 1f, true)
                        .withDataSeries(ds)
                        .build();

                surface.getRenderableSeries().add(rSeries);

                final int red = ColorUtil.red(initialColor);
                final int green = ColorUtil.green(initialColor);
                final int blue = ColorUtil.blue(initialColor);

                int newR = red == 255 ? 255 : red + 5;
                int newB = blue == 0 ? 0 : blue - 2;

                initialColor = ColorUtil.rgb(newR, green, newB);

                sciChartBuilder.newAnimator(rSeries).withSweepTransformation().withInterpolator(new DecelerateInterpolator()).withDuration(2000).withStartDelay(350).start();
            }
        });
    }

    private static IDataSeries<?, ?> generateDataSeries(AxisAlignment axisAlignment, int index) {
        final XyDataSeries<Double, Double> ds = new XyDataSeries<>(Double.class, Double.class);
        ds.setSeriesName(String.format("Series %d", index));

        final double gradient = axisAlignment == AxisAlignment.Right ? index : -index;
        final double start = axisAlignment == AxisAlignment.Right ? 0d : 14000d;

        final DoubleSeries straightLine = DataManager.getInstance().getStraightLine(gradient, start, SERIES_POINT_COUNT);
        ds.append(straightLine.xValues, straightLine.yValues);

        return ds;
    }
}