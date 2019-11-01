package com.example.tutorials.scichart.scichartandroid_tutorial1;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.widget.LinearLayout;

import com.scichart.charting.ClipMode;
import com.scichart.charting.model.dataSeries.XyDataSeries;
import com.scichart.charting.modifiers.AxisDragModifierBase;
import com.scichart.charting.modifiers.ModifierGroup;
import com.scichart.charting.visuals.SciChartSurface;
import com.scichart.charting.visuals.annotations.HorizontalAnchorPoint;
import com.scichart.charting.visuals.annotations.TextAnnotation;
import com.scichart.charting.visuals.annotations.VerticalAnchorPoint;
import com.scichart.charting.visuals.axes.AxisAlignment;
import com.scichart.charting.visuals.axes.IAxis;
import com.scichart.charting.visuals.pointmarkers.EllipsePointMarker;
import com.scichart.charting.visuals.renderableSeries.IRenderableSeries;
import com.scichart.core.annotations.Orientation;
import com.scichart.core.framework.UpdateSuspender;
import com.scichart.core.model.DoubleValues;
import com.scichart.drawing.utility.ColorUtil;
import com.scichart.extensions.builders.SciChartBuilder;

import java.util.Collections;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Added in Tutorial #1
        // Create a SciChartSurface
        final SciChartSurface surface = new SciChartSurface(this);

        // Get a layout declared in "activity_main.xml" by id
        LinearLayout chartLayout = (LinearLayout) findViewById(R.id.chart_layout);

        // Add the SciChartSurface to the layout
        chartLayout.addView(surface);

        // Initialize the SciChartBuilder
        SciChartBuilder.init(this);

        // Obtain the SciChartBuilder instance
        final SciChartBuilder sciChartBuilder = SciChartBuilder.instance();

        // Create a numeric X axis
        final IAxis xAxis = sciChartBuilder.newNumericAxis()
                .withAxisTitle("X Axis Title")
                .withVisibleRange(-5, 15)
                .build();

        // Added in Tutorial #8 - two Y axes
        // Create a numeric axis, right-aligned
        final IAxis yAxisRight = sciChartBuilder.newNumericAxis()
                .withAxisTitle("Primary")
                // Assign a unique ID to the axis
                .withAxisId("primaryYAxis")
                .withAxisAlignment(AxisAlignment.Right)
                .build();

        // Create another numeric axis, left-aligned
        final IAxis yAxisLeft = sciChartBuilder.newNumericAxis()
                .withAxisTitle("Secondary")
                // Assign a unique ID to the axis
                .withAxisId("secondaryYAxis")
                .withAxisAlignment(AxisAlignment.Left)
                .withGrowBy(0.2,0.2)
                .build();

        // Add both Y axes to the YAxes collection of the surface
        Collections.addAll(surface.getYAxes(), yAxisLeft, yAxisRight);

        // Added in Tutorial #3
        // Add a bunch of interaction modifiers to a ModifierGroup
        ModifierGroup chartModifiers = sciChartBuilder.newModifierGroup()
                // Setting MotionEventsGroup
                .withMotionEventsGroup("SharedMotionEvents").withReceiveHandledEvents(true)
                .withPinchZoomModifier().build()
                .withZoomPanModifier().withReceiveHandledEvents(true).build()
                .withZoomExtentsModifier().withReceiveHandledEvents(true).build()
                .withXAxisDragModifier().withReceiveHandledEvents(true).withDragMode(AxisDragModifierBase.AxisDragMode.Scale).withClipModex(ClipMode.None).build()
                .withYAxisDragModifier().withReceiveHandledEvents(true).withDragMode(AxisDragModifierBase.AxisDragMode.Pan).build()
                .build();

        // Add the X axis to the XAxes collection of the surface
        Collections.addAll(surface.getXAxes(), xAxis);

        // Add the interactions to the ChartModifiers collection of the surface
        Collections.addAll(surface.getChartModifiers(), chartModifiers);

        // Added in Tutorial #6 - FIFO (scrolling) series
        // Create a couple of DataSeries for numeric (Int, Double) data
        // Set FIFO capacity to 500 on DataSeries
        final int fifoCapacity = 500;

        final XyDataSeries lineData = sciChartBuilder.newXyDataSeries(Integer.class, Double.class)
                .withFifoCapacity(fifoCapacity)
                .build();
        final XyDataSeries scatterData = sciChartBuilder.newXyDataSeries(Integer.class, Double.class)
                .withFifoCapacity(fifoCapacity)
                .build();

        // Create and configure a line series
        final IRenderableSeries lineSeries = sciChartBuilder.newLineSeries()
                .withDataSeries(lineData)
                // Register on a particular Y axis using its ID
                .withYAxisId("primaryYAxis")
                .withStrokeStyle(ColorUtil.LightBlue, 2f, true)
                .build();

        // Create an Ellipse PointMarker for the Scatter Series
        EllipsePointMarker pointMarker = sciChartBuilder
                .newPointMarker(new EllipsePointMarker())
                .withFill(ColorUtil.LightBlue)
                .withStroke(ColorUtil.Green, 2f)
                .withSize(10)
                .build();

        // Create and configure a scatter series
        final IRenderableSeries scatterSeries = sciChartBuilder.newScatterSeries()
                .withDataSeries(scatterData)
                // Register on a particular Y axis using its ID
                .withYAxisId("primaryYAxis")
                .withPointMarker(pointMarker)
                .build();

        // Add a RenderableSeries onto the SciChartSurface
        surface.getRenderableSeries().add(scatterSeries);
        surface.getRenderableSeries().add(lineSeries);
        surface.zoomExtents();

        // Added in Tutorial #5
        // Create a LegendModifier and configure a chart legend
        ModifierGroup legendModifier = sciChartBuilder.newModifierGroup()
                .withLegendModifier()
                .withOrientation(Orientation.HORIZONTAL)
                .withPosition(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM, 10)
                .build()
                .build();

        // Add the LegendModifier to the SciChartSurface
        surface.getChartModifiers().add(legendModifier);

        // Create and configure a CursorModifier
        ModifierGroup cursorModifier = sciChartBuilder.newModifierGroup()
                // Setting MotionEventsGroup
                .withMotionEventsGroup("SharedMotionEvents").withReceiveHandledEvents(true)
                .withCursorModifier().withShowTooltip(true).build()
                .build();

        // Add the CursorModifier to the SciChartSurface
        surface.getChartModifiers().add(cursorModifier);

        // Added in Tutorial #9 - one more SciChartSurface
        final SciChartSurface surface2 = new SciChartSurface(this);

        // Add the SciChartSurface to the layout
        chartLayout.addView(surface2);

        // Set layout parameters for both surfaces
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, 1.0f);
        surface.setLayoutParams(layoutParams);
        surface2.setLayoutParams(layoutParams);

        // Create a numeric X axis
        final IAxis xAxis2 = sciChartBuilder.newNumericAxis()
                .withAxisTitle("X Axis Title")
                .withVisibleRange(-5, 15)
                .build();

        // Create a numeric axis
        final IAxis yAxisRight2 = sciChartBuilder.newNumericAxis()
                .withAxisTitle("Primary")
                .withAxisId("primaryYAxis")
                .withAxisAlignment(AxisAlignment.Right)
                .build();

        // Create another numeric axis
        final IAxis yAxisLeft2 = sciChartBuilder.newNumericAxis()
                .withAxisTitle("Secondary")
                .withAxisId("secondaryYAxis")
                .withAxisAlignment(AxisAlignment.Left)
                .withGrowBy(0.2, 0.2)
                .build();

        // Add the Y axis to the YAxes collection of the surface
        Collections.addAll(surface2.getYAxes(), yAxisLeft2, yAxisRight2);

        // Add the X axis to the XAxes collection of the surface
        Collections.addAll(surface2.getXAxes(), xAxis2);

        // Create and configure an area series
        final IRenderableSeries areaSeries = sciChartBuilder.newMountainSeries()
                .withDataSeries(scatterData)
                .withYAxisId("primaryYAxis")
                .withStrokeStyle(ColorUtil.LightBlue, 2f, true)
                .withAreaFillColor(ColorUtil.argb(ColorUtil.LightSteelBlue, 0.6f))
                .build();

        // Add the area series to the RenderableSeries collection of the surface
        Collections.addAll(surface2.getRenderableSeries(), areaSeries);

        // Create the second collection of chart modifiers
        ModifierGroup chartModifiers2 = sciChartBuilder.newModifierGroup()
                // Setting MotionEventsGroup
                .withMotionEventsGroup("SharedMotionEvents").withReceiveHandledEvents(true)
                .withZoomExtentsModifier().withReceiveHandledEvents(true).build()
                .withZoomPanModifier().withReceiveHandledEvents(true).build()
                .withPinchZoomModifier().withReceiveHandledEvents(true).build()
                .withCursorModifier().withReceiveHandledEvents(true).build()
                .build();

        // Add the interactions to the ChartModifiers collection of the surface
        Collections.addAll(surface2.getChartModifiers(), chartModifiers2);

        TimerTask updateDataTask = new TimerTask() {
            private int x = 0;

            @Override
            public void run() {
                UpdateSuspender.using(surface, new Runnable() {
                    @Override
                    public void run() {
                        lineData.append(x, Math.sin(x * 0.1));
                        scatterData.append(x, Math.cos(x * 0.1));

                        // Added in Tutorial #7
                        // Add an annotation every 100 data points
                        if(x%100 == 0) {
                            TextAnnotation marker = sciChartBuilder.newTextAnnotation()
                                    // Register on a particular Y axis using its ID
                                    .withYAxisId("primaryYAxis")
                                    .withIsEditable(false)
                                    .withText("N")
                                    .withBackgroundColor(ColorUtil.Green)
                                    .withX1(x)
                                    .withY1(0.0)
                                    .withVerticalAnchorPoint(VerticalAnchorPoint.Center)
                                    .withHorizontalAnchorPoint(HorizontalAnchorPoint.Center)
                                    .withFontStyle(20, ColorUtil.White)
                                    .withZIndex(1)
                                    .build();

                            surface.getAnnotations().add(marker);

                            // Remove one annotation from the beginning
                            // in the FIFO way
                            if(x > fifoCapacity){
                                surface.getAnnotations().remove(0);
                            }
                        }

                        // Zoom series to fit the viewport
                        surface.zoomExtents();
                        surface2.zoomExtents();
                        ++x;
                    }
                });
            }
        };

        Timer timer = new Timer();

        long delay = 0;
        long interval = 10;
        timer.schedule(updateDataTask, delay, interval);
    }
}
