//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2021. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// ImageAnnotationsFragment.java is part of SCICHART®, High Performance Scientific Charts
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

package com.scichart.examples.fragments.examples2d.chartAnnotations;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.scichart.charting.model.AnnotationCollection;
import com.scichart.charting.model.dataSeries.XyDataSeries;
import com.scichart.charting.visuals.SciChartSurface;
import com.scichart.charting.visuals.annotations.AnnotationCoordinateMode;
import com.scichart.charting.visuals.annotations.AnnotationSurfaceEnum;
import com.scichart.charting.visuals.annotations.ContentModeEnum;
import com.scichart.charting.visuals.annotations.HorizontalAnchorPoint;
import com.scichart.charting.visuals.annotations.ImageAnnotation;
import com.scichart.charting.visuals.annotations.LabelPlacement;
import com.scichart.charting.visuals.annotations.TextAnnotation;
import com.scichart.charting.visuals.annotations.VerticalAnchorPoint;
import com.scichart.charting.visuals.axes.AxisAlignment;
import com.scichart.charting.visuals.axes.IAxis;
import com.scichart.charting.visuals.renderableSeries.FastColumnRenderableSeries;
import com.scichart.core.framework.UpdateSuspender;
import com.scichart.drawing.common.SolidPenStyle;
import com.scichart.drawing.utility.ColorUtil;
import com.scichart.examples.R;
import com.scichart.examples.fragments.base.ExampleSingleChartBaseFragment;
import com.scichart.examples.utils.imageannotation.CarSales;
import com.scichart.examples.utils.imageannotation.CarSalesModel;

import java.util.Collections;
import java.util.List;

public class ImageAnnotationsFragment extends ExampleSingleChartBaseFragment {

    private final List<CarSalesModel> data = CarSales.INSTANCE.getData();

    @Override
    public boolean showDefaultModifiersInToolbar() {
        return false;
    }

    @Override
    protected void initExample(@NonNull SciChartSurface surface) {
        surface.setTheme(R.style.SciChart_Image_Annotation);

        UpdateSuspender.using(surface, () -> {
            final IAxis xAxis = sciChartBuilder.newNumericAxis()
                    .withGrowBy(0.08d, 0.08d)
                    .withVisibility(View.GONE)
                    .withDrawMajorGridLines(false)
                    .withDrawMinorGridLines(false)
                    .withAxisAlignment(AxisAlignment.Left)
                    .build();

            final IAxis yAxis = sciChartBuilder.newNumericAxis()
                    .withGrowBy(0.0, 0.15)
                    .withAxisAlignment(AxisAlignment.Bottom)
                    .withDrawMajorGridLines(false)
                    .withDrawMinorGridLines(false)
                    .withFlipCoordinates(true)
                    .build();

            XyDataSeries<Double, Double> dataSeries = new XyDataSeries<Double, Double>(Double.class, Double.class);
            AnnotationCollection annotationCollection = new AnnotationCollection();

            for (int i = 0; i < data.size(); i++) {
                dataSeries.append((double) i, (double) data.get(i).getCarsSold());
                annotationCollection.add(
                        sciChartBuilder.newImageAnnotation()
                                .withX1(i - 0.15)
                                .withY1(data.get(i).getCarsSold() + 1000000.0)
                                .withDesiredSize(30, 24)
                                .withImage(data.get(i).getIcon())
                                .withContentMode(ImageView.ScaleType.FIT_START)
                                .build()
                );
            }

            TextAnnotation titleAnnotation = sciChartBuilder.newTextAnnotation()
                    .withX1(-1)
                    .withY1(8000000)
                    .withText("Top 10 car markets")
                    .withFontStyle(Typeface.DEFAULT_BOLD, 22, Color.WHITE)
                    .build();
            annotationCollection.add(titleAnnotation);

            TextAnnotation descriptionAnnotation = sciChartBuilder.newTextAnnotation()
                    .withX1(5.5)
                    .withY1(9000000)
                    .withText("The image annotation can be \nplaced in front or behind the \ngrid lines and can either \nbe fixed or moved along\nwith the chart.")
                    .withFontStyle(Typeface.DEFAULT_BOLD, 22, Color.WHITE)
                    .build();
            annotationCollection.add(descriptionAnnotation);


            final FastColumnRenderableSeries series = sciChartBuilder.newColumnSeries()
                    .withDataSeries(dataSeries)
                    .withStrokeStyle(new SolidPenStyle(0xff1f6f6f, true, 0.4f, null))
                    .withFillColor(0xff1f6f6f)
                    .withDataPointWidth(0.7)
                    .build();

            Collections.addAll(surface.getXAxes(), xAxis);
            Collections.addAll(surface.getYAxes(), yAxis);
            Collections.addAll(surface.getRenderableSeries(), series);

            surface.setAnnotations(annotationCollection);

            surface.getChartModifiers().add(sciChartBuilder.newModifierGroupWithDefaultModifiers().build());
        });

    }

    public static class CustomView1 extends View {

        private final int FILL_COLOR = Color.parseColor("#5768bcae");
        private final int STROKE_COLOR = Color.parseColor("#FF68bcae");

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

        private final int FILL_COLOR = Color.parseColor("#57ae418d");
        private final int STROKE_COLOR = Color.parseColor("#FFae418d");

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
