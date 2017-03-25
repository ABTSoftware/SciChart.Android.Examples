//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2017. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// SeriesSelectionFragment.java is part of the SCICHART® Examples. Permission is hereby granted
// to modify, create derivative works, distribute and publish any part of this source
// code whether for commercial, private or personal use.
//
// The SCICHART® examples are distributed in the hope that they will be useful, but
// without any warranty. It is provided "AS IS" without warranty of any kind, either
// expressed or implied.
//******************************************************************************

package com.scichart.examples.fragments;

import com.scichart.charting.model.dataSeries.IDataSeries;
import com.scichart.charting.model.dataSeries.XyDataSeries;
import com.scichart.charting.modifiers.SeriesSelectionModifier;
import com.scichart.charting.visuals.SciChartSurface;
import com.scichart.charting.visuals.axes.AutoRange;
import com.scichart.charting.visuals.axes.AxisAlignment;
import com.scichart.charting.visuals.axes.NumericAxis;
import com.scichart.charting.visuals.pointmarkers.EllipsePointMarker;
import com.scichart.charting.visuals.pointmarkers.IPointMarker;
import com.scichart.charting.visuals.renderableSeries.FastLineRenderableSeries;
import com.scichart.charting.visuals.renderableSeries.IRenderableSeries;
import com.scichart.charting.visuals.renderableSeries.SeriesStyleBase;
import com.scichart.core.framework.UpdateSuspender;
import com.scichart.drawing.common.PenStyle;
import com.scichart.drawing.utility.ColorUtil;
import com.scichart.examples.R;
import com.scichart.examples.data.DataManager;
import com.scichart.examples.data.DoubleSeries;
import com.scichart.examples.fragments.base.ExampleBaseFragment;

import java.util.Collections;

import butterknife.Bind;

public class SeriesSelectionFragment extends ExampleBaseFragment {
    private static final int SERIES_POINT_COUNT = 50;
    private static final int SERIES_COUNT = 80;

    @Bind(R.id.chart)
    SciChartSurface surface;

    @Override
    public boolean showDefaultModifiersInToolbar() {
        return false;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.example_single_chart_fragment;
    }

    @Override
    protected void initExample() {
        final NumericAxis xAxis = sciChartBuilder.newNumericAxis().withAxisAlignment(AxisAlignment.Bottom).withAutoRangeMode(AutoRange.Always).build();
        final NumericAxis leftAxis = sciChartBuilder.newNumericAxis().withAxisAlignment(AxisAlignment.Left).withAxisId(AxisAlignment.Left.name()).build();
        final NumericAxis rightAxis = sciChartBuilder.newNumericAxis().withAxisAlignment(AxisAlignment.Right).withAxisId(AxisAlignment.Right.name()).build();

        final SeriesSelectionModifier seriesSelectionModifier = new SeriesSelectionModifier();

        // set selected style
        seriesSelectionModifier.setSelectedSeriesStyle(new SeriesStyleBase<IRenderableSeries>(IRenderableSeries.class) {
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

        UpdateSuspender.using(surface, new Runnable() {
            private int initialColor = ColorUtil.Blue;

            @Override
            public void run() {
                Collections.addAll(surface.getXAxes(), xAxis);
                Collections.addAll(surface.getYAxes(), leftAxis, rightAxis);

                surface.getChartModifiers().add(seriesSelectionModifier);

                for (int i = 0; i < SERIES_COUNT; i++) {
                    final AxisAlignment alignment = i % 2 == 0 ? AxisAlignment.Left : AxisAlignment.Right;

                    final IDataSeries ds = generateDataSeries(alignment, i);
                    final FastLineRenderableSeries rs = sciChartBuilder.newLineSeries()
                            .withStrokeStyle(initialColor)
                            .withDataSeries(ds)
                            .withYAxisId(alignment.name())
                            .build();

                    surface.getRenderableSeries().add(rs);

                    final int red = ColorUtil.red(initialColor);
                    final int green = ColorUtil.green(initialColor);
                    final int blue = ColorUtil.blue(initialColor);

                    int newR = red == 255 ? 255 : red + 5;
                    int newB = blue == 0 ? 0 : blue - 2;

                    initialColor = ColorUtil.rgb(newR, green, newB);
                }
            }
        });
    }

    private static IDataSeries generateDataSeries(AxisAlignment axisAlignment, int index) {
        final XyDataSeries<Double, Double> ds = new XyDataSeries<>(Double.class, Double.class);
        ds.setSeriesName(String.format("Series %d", index));

        final double gradient = axisAlignment == AxisAlignment.Right ? index : -index;
        final double start = axisAlignment == AxisAlignment.Right ? 0d : 14000d;

        final DoubleSeries straightLine = DataManager.getInstance().getStraightLine(gradient, start, SERIES_POINT_COUNT);

        ds.append(straightLine.xValues, straightLine.yValues);

        return ds;
    }
}