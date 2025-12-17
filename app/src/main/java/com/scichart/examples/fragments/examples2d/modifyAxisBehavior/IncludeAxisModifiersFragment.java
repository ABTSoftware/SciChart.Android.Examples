//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2025. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// IncludeAxisModifiersFragment.java is part of SCICHART®, High Performance Scientific Charts
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

import android.os.Handler;

import androidx.annotation.NonNull;

import com.scichart.charting.model.dataSeries.IXyDataSeries;
import com.scichart.charting.modifiers.CursorModifier;
import com.scichart.charting.modifiers.PinchZoomModifier;
import com.scichart.charting.modifiers.RolloverModifier;
import com.scichart.charting.modifiers.XAxisDragModifier;
import com.scichart.charting.modifiers.YAxisDragModifier;
import com.scichart.charting.modifiers.ZoomExtentsModifier;
import com.scichart.charting.modifiers.ZoomPanModifier;
import com.scichart.charting.visuals.SciChartSurface;
import com.scichart.charting.visuals.axes.AxisAlignment;
import com.scichart.charting.visuals.axes.IAxis;
import com.scichart.charting.visuals.renderableSeries.FastLineRenderableSeries;
import com.scichart.core.framework.UpdateSuspender;
import com.scichart.drawing.utility.ColorUtil;
import com.scichart.examples.R;
import com.scichart.examples.data.DataManager;
import com.scichart.examples.data.DoubleSeries;
import com.scichart.examples.fragments.base.ExampleSingleChartBaseFragment;
import com.scichart.examples.utils.Constant;
import com.scichart.examples.utils.interpolator.DefaultInterpolator;

import java.util.Collections;

/**
 * Example demonstrating the includeAxis API for modifiers.
 * This example shows how to selectively apply modifiers to specific axes in a multi-axis chart.
 */
public class IncludeAxisModifiersFragment extends ExampleSingleChartBaseFragment {

    private static final String X_AXIS_ID = "xAxis";
    private static final String Y_LEFT_AXIS_ID = "yLeftAxis";
    private static final String Y_RIGHT_AXIS_ID = "yRightAxis";

    @Override
    public boolean showDefaultModifiersInToolbar() {
        return false;
    }

    @Override
    protected void initExample(@NonNull SciChartSurface surface) {
        surface.setTheme(R.style.SciChart_NavyBlue);

        // Create X Axis
        final IAxis xAxis = sciChartBuilder.newNumericAxis()
                .withAxisId(X_AXIS_ID)
                .withAxisAlignment(AxisAlignment.Bottom)
                .withAxisTitle("X Axis (Shared)")
                .withGrowBy(0.1d, 0.1d)
                .build();

        // Create Left Y Axis
        final IAxis yLeftAxis = sciChartBuilder.newNumericAxis()
                .withAxisId(Y_LEFT_AXIS_ID)
                .withAxisAlignment(AxisAlignment.Left)
                .withAxisTitle("Left Y Axis (Not Fixed)")
                .withGrowBy(0.1d, 0.1d)
                .withTextColor(ColorUtil.argb(0xFF, 0x47, 0xBD, 0xE6))
                .withTickLabelStyle(sciChartBuilder.newFont().withTextSize(14f).withTextColor(ColorUtil.argb(0xFF, 0x47, 0xBD, 0xE6)).build())
                .build();

        // Create Right Y Axis
        final IAxis yRightAxis = sciChartBuilder.newNumericAxis()
                .withAxisId(Y_RIGHT_AXIS_ID)
                .withAxisAlignment(AxisAlignment.Right)
                .withAxisTitle("Right Y Axis (Fixed)")
                .withGrowBy(0.1d, 0.1d)
                .withTextColor(ColorUtil.argb(0xFF, 0xAE, 0x41, 0x8D))
                .withTickLabelStyle(sciChartBuilder.newFont().withTextSize(14f).withTextColor(ColorUtil.argb(0xFF, 0xAE, 0x41, 0x8D)).build())
                .build();

        // Create data series for left axis
        final DoubleSeries leftData = DataManager.getInstance().getFourierSeries(1.0, 0.1, 5000);
        final IXyDataSeries<Double, Double> leftDataSeries = sciChartBuilder.newXyDataSeries(Double.class, Double.class).build();
        leftDataSeries.append(leftData.xValues, leftData.yValues);
        leftDataSeries.setSeriesName("leftData");

        // Create data series for right axis
        final DoubleSeries rightData = DataManager.getInstance().getDampedSinewave(3.0, 0.005, 5000, 10);
        final IXyDataSeries<Double, Double> rightDataSeries = sciChartBuilder.newXyDataSeries(Double.class, Double.class).build();
        rightDataSeries.append(rightData.xValues, rightData.yValues);
        rightDataSeries.setSeriesName("rightData");

        // Create renderable series for left axis
        final FastLineRenderableSeries leftSeries = sciChartBuilder.newLineSeries()
                .withDataSeries(leftDataSeries)
                .withXAxisId(X_AXIS_ID)
                .withYAxisId(Y_LEFT_AXIS_ID)
                .withStrokeStyle(ColorUtil.argb(0xFF, 0x47, 0xBD, 0xE6), 2f, true)
                .build();

        // Create renderable series for right axis
        final FastLineRenderableSeries rightSeries = sciChartBuilder.newLineSeries()
                .withDataSeries(rightDataSeries)
                .withXAxisId(X_AXIS_ID)
                .withYAxisId(Y_RIGHT_AXIS_ID)
                .withStrokeStyle(ColorUtil.argb(0xFF, 0xAE, 0x41, 0x8D), 2f, true)
                .build();

        // Create ZoomPanModifier
        final ZoomPanModifier zoomPanModifier = new ZoomPanModifier();
        zoomPanModifier.setReceiveHandledEvents(true);
        zoomPanModifier.includeYAxis(yRightAxis, false);

        // Create PinchZoomModifier that ONLY works on LEFT Y AXIS
        // This demonstrates selective axis inclusion
        final PinchZoomModifier pinchZoomModifier = new PinchZoomModifier();
        //pinchZoomModifier.includeYAxis(yLeftAxis, true);  // Include only left Y axis
        pinchZoomModifier.includeYAxis(yRightAxis, false); // Exclude right Y axis
        pinchZoomModifier.setReceiveHandledEvents(true);

        // Create Y Axis Drag Modifier that ONLY works on LEFT Y AXIS
        final YAxisDragModifier yAxisDragModifier = new YAxisDragModifier();
        //yAxisDragModifier.includeYAxis(yLeftAxis, true);  // Include only left Y axis
        yAxisDragModifier.includeYAxis(yRightAxis, false);  // Include only left Y axis

        // Create X Axis Drag Modifier (works on X axis by default)
        final XAxisDragModifier xAxisDragModifier = new XAxisDragModifier();

        // Create Zoom Extents Modifier that EXCLUDES the RIGHT Y AXIS
        // This demonstrates selective axis exclusion
        final ZoomExtentsModifier zoomExtentsModifier = new ZoomExtentsModifier();

        final CursorModifier cursorModifier = new CursorModifier();
        cursorModifier.includeRenderableSeries(
                rightSeries,
                false
        );



        UpdateSuspender.using(surface, () -> {
            Collections.addAll(surface.getXAxes(), xAxis);
            Collections.addAll(surface.getYAxes(), yLeftAxis, yRightAxis);
            Collections.addAll(surface.getRenderableSeries(), leftSeries, rightSeries);
            Collections.addAll(surface.getChartModifiers(),
                    zoomPanModifier,
                    pinchZoomModifier,
                    yAxisDragModifier,
                    xAxisDragModifier,
                    zoomExtentsModifier,
                    cursorModifier
            );

            // Animate the series
            sciChartBuilder.newAnimator(leftSeries)
                    .withSweepTransformation()
                    .withInterpolator(DefaultInterpolator.getInterpolator())
                    .withDuration(Constant.ANIMATION_DURATION)
                    .withStartDelay(Constant.ANIMATION_START_DELAY)
                    .start();

            sciChartBuilder.newAnimator(rightSeries)
                    .withSweepTransformation()
                    .withInterpolator(DefaultInterpolator.getInterpolator())
                    .withDuration(Constant.ANIMATION_DURATION)
                    .withStartDelay(Constant.ANIMATION_START_DELAY)
                    .start();
        });

        // Add text annotation explaining the behavior
        surface.getAnnotations().add(
                sciChartBuilder.newTextAnnotation()
                        .withText("Try pinch-zoom and Y-axis drag:\n" +
                                "• Left Y Axis (blue) is zoomable\n" +
                                "• Right Y Axis (purple) is fixed\n" +
                                "• Double-tap zooms only left axis")
                        .withX1(0.5)
                        .withY1(9.5)
                        .withFontStyle(14, ColorUtil.White)
                        .build()
        );
    }
}

