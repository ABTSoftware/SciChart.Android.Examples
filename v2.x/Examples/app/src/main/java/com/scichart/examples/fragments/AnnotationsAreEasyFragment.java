//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2017. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// AnnotationsAreEasyFragment.java is part of the SCICHART® Examples. Permission is hereby granted
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
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;

import com.scichart.charting.visuals.SciChartSurface;
import com.scichart.charting.visuals.annotations.AnnotationCoordinateMode;
import com.scichart.charting.visuals.annotations.HorizontalAnchorPoint;
import com.scichart.charting.visuals.annotations.LabelPlacement;
import com.scichart.charting.visuals.annotations.VerticalAnchorPoint;
import com.scichart.charting.visuals.axes.IAxis;
import com.scichart.core.framework.UpdateSuspender;
import com.scichart.drawing.utility.ColorUtil;
import com.scichart.examples.R;
import com.scichart.examples.fragments.base.ExampleBaseFragment;

import java.util.Collections;

import butterknife.Bind;

public class AnnotationsAreEasyFragment extends ExampleBaseFragment {
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
        UpdateSuspender.using(surface, new Runnable() {
            @Override
            public void run() {
                final IAxis xAxis = sciChartBuilder.newNumericAxis()
                        .withVisibleRange(0d, 10d)
                        .withGrowBy(0.1d, 0.1d)
                        .withTextFormatting("0.0#")
                        .build();

                final IAxis yAxis = sciChartBuilder.newNumericAxis()
                        .withVisibleRange(0d, 10d)
                        .withGrowBy(0.1d, 0.1d)
                        .withTextFormatting("0.0#")
                        .build();

                Collections.addAll(surface.getXAxes(), xAxis);
                Collections.addAll(surface.getYAxes(), yAxis);

                Collections.addAll(surface.getAnnotations(),
                        // Watermark
                        sciChartBuilder.newTextAnnotation()
                                .withX1(0.5)
                                .withY1(0.5)
                                .withFontStyle(Typeface.DEFAULT_BOLD, 42, 0x22FFFFFF)
                                .withCoordinateMode(AnnotationCoordinateMode.Relative)
                                .withHorizontalAnchorPoint(HorizontalAnchorPoint.Center)
                                .withVerticalAnchorPoint(VerticalAnchorPoint.Center)
                                .withText("Create \n Watermarks")
                                .withTextGravity(Gravity.CENTER)
                                .build(),

                        // Text annotations
                        sciChartBuilder.newTextAnnotation()
                                .withX1(0.3)
                                .withY1(9.7)
                                .withFontStyle(24, ColorUtil.White)
                                .withText("Annotations are Easy!")
                                .build(),
                        sciChartBuilder.newTextAnnotation()
                                .withX1(1.0)
                                .withY1(9.0)
                                .withText("You can create text")
                                .withFontStyle(10, ColorUtil.White)
                                .build(),

                        // Text with Anchor Points
                        sciChartBuilder.newTextAnnotation()
                                .withX1(5d)
                                .withY1(8d)
                                .withHorizontalAnchorPoint(HorizontalAnchorPoint.Center)
                                .withVerticalAnchorPoint(VerticalAnchorPoint.Bottom)
                                .withText("Anchor Center (X1, Y1)")
                                .build(),
                        sciChartBuilder.newTextAnnotation()
                                .withX1(5d)
                                .withY1(8d)
                                .withHorizontalAnchorPoint(HorizontalAnchorPoint.Right)
                                .withVerticalAnchorPoint(VerticalAnchorPoint.Top)
                                .withText("Anchor Right")
                                .build(),
                        sciChartBuilder.newTextAnnotation()
                                .withX1(5d)
                                .withY1(8d)
                                .withHorizontalAnchorPoint(HorizontalAnchorPoint.Left)
                                .withVerticalAnchorPoint(VerticalAnchorPoint.Top)
                                .withText("or Anchor Left")
                                .build(),

                        // Line and LineArrow annotation
                        sciChartBuilder.newTextAnnotation()
                                .withText("Draw Lines with \nor without arrows")
                                .withX1(0.3).withY1(6.1)
                                .withFontStyle(12, ColorUtil.White)
                                .withVerticalAnchorPoint(VerticalAnchorPoint.Bottom)
                                .build(),

                        sciChartBuilder.newLineAnnotation()
                                .withPosition(1d, 4d, 2d, 6d)
                                .withStroke(2f, 0xFF555555)
                                .build(),
                        sciChartBuilder.newLineArrowAnnotation()
                                .withPosition(1.2d, 3.8d, 2.5d, 6d)
                                .withStroke(2f, 0xFF555555)
                                .withArrowHeadLength(4)
                                .withArrowHeadWidth(8)
                                .build(),

                        // Boxes
                        sciChartBuilder.newTextAnnotation()
                                .withText("Draw Boxes")
                                .withX1(3.5).withY1(6.1)
                                .withFontStyle(12, ColorUtil.White)
                                .withVerticalAnchorPoint(VerticalAnchorPoint.Bottom)
                                .build(),

                        sciChartBuilder.newBoxAnnotation()
                                .withPosition(3.5d, 4d, 5d, 5d)
                                .withBackgroundDrawableId(R.drawable.example_box_annotation_background_1)
                                .build(),
                        sciChartBuilder.newBoxAnnotation()
                                .withPosition(4d, 4.5d, 5.5d, 5.5d)
                                .withBackgroundDrawableId(R.drawable.example_box_annotation_background_2)
                                .build(),
                        sciChartBuilder.newBoxAnnotation()
                                .withPosition(4.5d, 5d, 6d, 6d)
                                .withBackgroundDrawableId(R.drawable.example_box_annotation_background_3)
                                .build(),

                        // Custom Shapes
                        sciChartBuilder.newTextAnnotation()
                                .withText("Or Custom Shapes")
                                .withX1(7).withY1(6.1)
                                .withFontStyle(12, ColorUtil.White)
                                .withVerticalAnchorPoint(VerticalAnchorPoint.Bottom)
                                .build(),
                        sciChartBuilder.newCustomAnnotation()
                                .withPosition(8d, 5.5d)
                                .withContent(R.layout.example_custom_annotation_view)
                                .build(),
                        sciChartBuilder.newCustomAnnotation()
                                .withPosition(7.5d, 5d)
                                .withContent(new CustomView2(getActivity()))
                                .build(),

                        // Horizontal Lines
                        sciChartBuilder.newHorizontalLineAnnotation()
                                .withPosition(5d, 3.2d)
                                .withStroke(2, ColorUtil.Orange)
                                .withHorizontalGravity(Gravity.RIGHT)
                                .withAnnotationLabel(LabelPlacement.TopLeft, "Right Aligned, with text on left")
                                .build(),
                        sciChartBuilder.newHorizontalLineAnnotation()
                                .withPosition(7d, 2.8d)
                                .withStroke(2, ColorUtil.Orange)
                                .withAnnotationLabel(LabelPlacement.Axis)
                                .build(),

                        // Vertical Lines
                        sciChartBuilder.newVerticalLineAnnotation()
                                .withPosition(9d, 4d)
                                .withVerticalGravity(Gravity.BOTTOM)
                                .withStroke(2, ColorUtil.Brown)
                                .withAnnotationLabel()
                                .build(),
                        sciChartBuilder.newVerticalLineAnnotation()
                                .withPosition(9.5d, 3d)
                                .withStroke(2, ColorUtil.Brown)
                                .withAnnotationLabel()
                                .withAnnotationLabel(LabelPlacement.TopRight, "Bottom-aligned", 90)
                                .build());

                surface.getChartModifiers().add(sciChartBuilder.newModifierGroupWithDefaultModifiers().build());
            }
        });
    }

    public static class CustomView1 extends View {

        private final int FILL_COLOR = Color.parseColor("#571CB61C");
        private final int STROKE_COLOR = Color.parseColor("#FF00B400");

        private final Path path = new Path();
        private final Paint paintFill = new Paint();
        private final Paint paintStroke = new Paint();

        public CustomView1(Context context) {
            super(context);
            init();
        }

        public CustomView1(Context context, AttributeSet attrs) {
            super(context, attrs);
            init();
        }

        private void init() {
            paintFill.setStyle(Paint.Style.FILL);
            paintFill.setColor(FILL_COLOR);
            paintStroke.setStyle(Paint.Style.STROKE);
            paintStroke.setColor(STROKE_COLOR);

            path.moveTo(0, 15);
            path.lineTo(15, 0);
            path.lineTo(30, 15);
            path.lineTo(20, 15);
            path.lineTo(20, 30);
            path.lineTo(10, 30);
            path.lineTo(10, 15);
            path.lineTo(0, 15);

            setMinimumHeight(50);
            setMinimumWidth(50);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            canvas.drawPath(path, paintFill);
            canvas.drawPath(path, paintStroke);
        }

    }

    public static class CustomView2 extends View {

        private final int FILL_COLOR = Color.parseColor("#57B22020");
        private final int STROKE_COLOR = Color.parseColor("#FF990000");

        private final Path path = new Path();
        private final Paint paintFill = new Paint();
        private final Paint paintStroke = new Paint();

        public CustomView2(Context context) {
            super(context);
            paintFill.setStyle(Paint.Style.FILL);
            paintFill.setColor(FILL_COLOR);
            paintStroke.setStyle(Paint.Style.STROKE);
            paintStroke.setColor(STROKE_COLOR);

            path.moveTo(0, 15);
            path.lineTo(10, 15);
            path.lineTo(10, 0);
            path.lineTo(20, 0);
            path.lineTo(20, 15);
            path.lineTo(30, 15);
            path.lineTo(15, 30);
            path.lineTo(0, 15);

            setMinimumHeight(50);
            setMinimumWidth(50);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            canvas.drawPath(path, paintFill);
            canvas.drawPath(path, paintStroke);
        }

    }
}
