//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2021. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// AxisBordersExampleFragment.java is part of SCICHART®, High Performance Scientific Charts
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

import androidx.annotation.NonNull;

import com.scichart.charting.model.dataSeries.IXyDataSeries;
import com.scichart.charting.visuals.SciChartSurface;
import com.scichart.charting.visuals.axes.AxisAlignment;
import com.scichart.charting.visuals.axes.IAxis;
import com.scichart.charting.visuals.renderableSeries.FastLineRenderableSeries;
import com.scichart.core.framework.UpdateSuspender;
import com.scichart.drawing.common.AxisBorderStyle;
import com.scichart.drawing.common.FontStyle;
import com.scichart.drawing.common.PenStyle;
import com.scichart.drawing.common.SolidPenStyle;
import com.scichart.examples.fragments.base.ExampleSingleChartBaseFragment;

import java.util.Collections;

public class AxisBordersExampleFragment extends ExampleSingleChartBaseFragment {

    private static final int[] PRIMARY_COLORS = new int[]{
            0xFF4FBEE6,
            0xFFAD3D8D,
            0xFF6BBDAE,
            0xFFE76E63,
            0xFF2C4B92
    };

    @Override
    protected void initExample(@NonNull SciChartSurface surface) {
        final float[] axisBorderThickness = {3f};
        final float tickSize = 16f;
        final float tickThickness = 3f;
        final int axisTitleSize = 48;
        final int labelSize = 32;

        final IAxis xAxis1 = sciChartBuilder.newNumericAxis()
                .withAxisId("xAxis1")
                .withAxisTitle("X Axis")
                .withAxisAlignment(AxisAlignment.Bottom)
                .withDrawMajorBands(false)
                .withDrawMajorGridLines(true)
                .withDrawMinorGridLines(false)
                .withDrawMajorTicks(true)
                .withVisibleRange(-10, 110)
                .build();
        xAxis1.setIsPrimaryAxis(true);

        final IAxis xAxis2 = sciChartBuilder.newNumericAxis()
                .withAxisId("xAxis2")
                .withAxisTitle("Flipped X Axis")
                .withAxisAlignment(AxisAlignment.Bottom)
                .withFlipCoordinates(true)
                .withDrawMajorBands(false)
                .withDrawMajorGridLines(false)
                .withDrawMinorGridLines(false)
                .withDrawMajorTicks(true)
                .withVisibleRange(-10, 110)
                .build();

        final IAxis xAxis3 = sciChartBuilder.newNumericAxis()
                .withAxisId("xAxis3")
                .withAxisTitle("Stacked X Axis")
                .withAxisAlignment(AxisAlignment.Right)
                .withDrawMajorBands(false)
                .withDrawMajorGridLines(false)
                .withDrawMinorGridLines(false)
                .withDrawMajorTicks(true)
                .withVisibleRange(-10, 110)
                .build();

        final IAxis yAxis1 = sciChartBuilder.newNumericAxis()
                .withAxisId("yAxis1")
                .withAxisTitle("Flipped Y Axis - Left Aligned")
                .withAxisAlignment(AxisAlignment.Left)
                .withFlipCoordinates(true)
                .withDrawMajorBands(false)
                .withDrawMajorGridLines(true)
                .withDrawMinorGridLines(false)
                .withDrawMajorTicks(true)
                .withVisibleRange(-10, 140)
                .build();
        yAxis1.setIsPrimaryAxis(true);

        final IAxis yAxis2 = sciChartBuilder.newNumericAxis()
                .withAxisId("yAxis2")
                .withAxisTitle("Stacked Y Axis")
                .withAxisAlignment(AxisAlignment.Left)
                .withDrawMajorBands(false)
                .withDrawMajorGridLines(false)
                .withDrawMinorGridLines(false)
                .withDrawMajorTicks(true)
                .withVisibleRange(-10, 140)
                .build();

        final IAxis yAxis3 = sciChartBuilder.newNumericAxis()
                .withAxisId("yAxis3")
                .withAxisTitle("Y Axis - Top Aligned")
                .withAxisAlignment(AxisAlignment.Top)
                .withDrawMajorBands(false)
                .withDrawMajorGridLines(false)
                .withDrawMinorGridLines(false)
                .withDrawMajorTicks(true)
                .withVisibleRange(-10, 140)
                .build();

        final IAxis[] xAxes = new IAxis[]{xAxis1, xAxis2, xAxis3};
        final IAxis[] yAxes = new IAxis[]{yAxis1, yAxis2, yAxis3};

        UpdateSuspender.using(surface, () -> {
            Collections.addAll(surface.getXAxes(), xAxes);
            Collections.addAll(surface.getYAxes(), yAxes);

            for (int i = 0; i < xAxes.length; i++) {
                final int color = PRIMARY_COLORS[i];
                final IAxis xAxis = xAxes[i];
                final IAxis yAxis = yAxes[i];

                xAxis.setAxisBorderStyle(new AxisBorderStyle(color, true, axisBorderThickness[0]++));
                yAxis.setAxisBorderStyle(new AxisBorderStyle(color, true, axisBorderThickness[0]++));

                final PenStyle tickPenStyle = new SolidPenStyle(color, true, tickThickness, null);
                xAxis.setMajorTickLineStyle(tickPenStyle);
                yAxis.setMajorTickLineStyle(tickPenStyle);
                xAxis.setMajorTickLineLength(tickSize);
                yAxis.setMajorTickLineLength(tickSize);

                final FontStyle titleStyle = new FontStyle((float) axisTitleSize, color);
                xAxis.setTitleStyle(titleStyle);
                yAxis.setTitleStyle(titleStyle);

                final FontStyle labelStyle = new FontStyle((float) labelSize, color);
                xAxis.setTickLabelStyle(labelStyle);
                yAxis.setTickLabelStyle(labelStyle);

                final IXyDataSeries<Double, Double> dataSeries = sciChartBuilder
                        .newXyDataSeries(Double.class, Double.class)
                        .build();

                for (int j = 0; j < 100; j++) {
                    final double x = j;
                    final double y = Math.sin(j * 0.1) * j + 50;
                    dataSeries.append(x, y);
                }

                final FastLineRenderableSeries lineSeries = sciChartBuilder.newLineSeries()
                        .withDataSeries(dataSeries)
                        .withStrokeStyle(color, 3f)
                        .withXAxisId(xAxis.getAxisId())
                        .withYAxisId(yAxis.getAxisId())
                        .build();

                surface.getRenderableSeries().add(lineSeries);
            }

            Collections.addAll(surface.getChartModifiers(),
                    sciChartBuilder.newModifierGroupWithDefaultModifiers().build());
        });
    }
}
