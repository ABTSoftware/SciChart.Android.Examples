//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2021. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// InteractionWithAnnotationsFragment.java is part of SCICHART®, High Performance Scientific Charts
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

import android.graphics.Typeface;
import android.view.Gravity;

import androidx.annotation.NonNull;

import com.scichart.charting.model.dataSeries.OhlcDataSeries;
import com.scichart.charting.modifiers.ZoomPanModifier;
import com.scichart.charting.visuals.SciChartSurface;
import com.scichart.charting.visuals.annotations.AnnotationCoordinateMode;
import com.scichart.charting.visuals.annotations.AnnotationSurfaceEnum;
import com.scichart.charting.visuals.annotations.HorizontalAnchorPoint;
import com.scichart.charting.visuals.annotations.LabelPlacement;
import com.scichart.drawing.utility.ColorUtil;
import com.scichart.examples.R;
import com.scichart.examples.data.MarketDataService;
import com.scichart.examples.data.PriceSeries;
import com.scichart.examples.fragments.base.ExampleSingleChartBaseFragment;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

public class InteractionWithAnnotationsFragment extends ExampleSingleChartBaseFragment {

    @Override
    public boolean showDefaultModifiersInToolbar() {
        return false;
    }

    @Override
    protected void initExample(@NonNull SciChartSurface surface) {
        surface.setTheme(R.style.SciChart_NavyBlue);

        final OhlcDataSeries<Date, Double> dataSeries = sciChartBuilder.newOhlcDataSeries(Date.class, Double.class).build();

        final MarketDataService marketDataService = new MarketDataService(Calendar.getInstance().getTime(), 5, 5);
        final PriceSeries data = marketDataService.getHistoricalData(200);

        dataSeries.append(data.getDateData(), data.getOpenData(), data.getHighData(), data.getLowData(), data.getCloseData());

        Collections.addAll(surface.getRenderableSeries(), sciChartBuilder.newCandlestickSeries().withDataSeries(dataSeries).withOpacity(0.4f).build());
        Collections.addAll(surface.getXAxes(), sciChartBuilder.newCategoryDateAxis().build());
        Collections.addAll(surface.getYAxes(), sciChartBuilder.newNumericAxis().withVisibleRange(30d, 37d).build());
        Collections.addAll(surface.getChartModifiers(), new ZoomPanModifier());

        Collections.addAll(surface.getAnnotations(),
                sciChartBuilder.newTextAnnotation()
                        .withPosition(10, 30.5d)
                        .withIsEditable(true)
                        .withText("Buy!")
                        .withFontStyle(20, ColorUtil.White)
                        .withZIndex(1) // draw this annotation above other annotations
                        .build(),
                sciChartBuilder.newTextAnnotation()
                        .withPosition(50, 34d)
                        .withBackgroundDrawableId(R.drawable.example_text_annotation_background)
                        .withIsEditable(true)
                        .withText("Sell!")
                        .withFontStyle(20, ColorUtil.White)
                        .withPadding(8)
                        .withZIndex(1) // draw this annotation above other annotations
                        .build(),
                sciChartBuilder.newTextAnnotation()
                        .withX1(80d).withY1(37d)
                        .withIsEditable(true)
                        .withText("Rotated text")
                        .withFontStyle(20, ColorUtil.White)
                        .withRotationAngle(30)
                        .withZIndex(1) // draw this annotation above other annotations
                        .build(),
                sciChartBuilder.newBoxAnnotation()
                        .withPosition(50, 35.5, 120, 32)
                        .withIsEditable(true)
                        .withBackgroundDrawableId(R.drawable.example_box_annotation_background_4)
                        .build(),
                sciChartBuilder.newLineAnnotation()
                        .withPosition(40, 30.5d, 60, 33.5d)
                        .withIsEditable(true)
                        .withStroke(2f, 0xAAFF6600)
                        .build(),
                sciChartBuilder.newLineAnnotation()
                        .withPosition(120, 30.5, 175, 36)
                        .withIsEditable(true)
                        .withStroke(2f, 0xAAFF6600)
                        .build(),
                sciChartBuilder.newLineArrowAnnotation()
                        .withPosition(50, 35d, 80, 31.4d)
                        .withArrowHeadLength(8f)
                        .withArrowHeadWidth(16f)
                        .withIsEditable(true)
                        .build(),
                sciChartBuilder.newAxisMarkerAnnotation()
                        .withIsEditable(true)
                        .withY1(32.7d)
                        .build(),
                sciChartBuilder.newAxisMarkerAnnotation()
                        .withAnnotationSurface(AnnotationSurfaceEnum.XAxis)
                        .withFormattedValue("Horizontal")
                        .withIsEditable(true)
                        .withX1(100)
                        .build(),
                sciChartBuilder.newHorizontalLineAnnotation()
                        .withPosition(150d, 32.2d)
                        .withStroke(2, ColorUtil.Red)
                        .withHorizontalGravity(Gravity.END)
                        .withIsEditable(true)
                        .withAnnotationLabel(LabelPlacement.Axis)
                        .build(),
                sciChartBuilder.newHorizontalLineAnnotation()
                        .withX1(130).withY1(33.9d).withX2(160)
                        .withStroke(2, ColorUtil.Blue)
                        .withHorizontalGravity(Gravity.CENTER_HORIZONTAL)
                        .withIsEditable(true)
                        .withAnnotationLabel(LabelPlacement.Left, "Left")
                        .withAnnotationLabel(LabelPlacement.Top, "Top")
                        .withAnnotationLabel(LabelPlacement.Right, "Right")
                        .build(),
                sciChartBuilder.newVerticalLineAnnotation()
                        .withX1(20).withY1(35d).withY2(33d)
                        .withStroke(2, ColorUtil.DarkGreen)
                        .withVerticalGravity(Gravity.CENTER_VERTICAL)
                        .withIsEditable(true)
                        .build(),
                sciChartBuilder.newVerticalLineAnnotation()
                        .withPosition(40, 34)
                        .withStroke(2, ColorUtil.Green)
                        .withVerticalGravity(Gravity.TOP)
                        .withIsEditable(true)
                        .withAnnotationLabel(LabelPlacement.Top, null, 90)
                        .build(),
                sciChartBuilder.newTextAnnotation()
                        .withPosition(0.5, 0.5)
                        .withCoordinateMode(AnnotationCoordinateMode.Relative)
                        .withHorizontalAnchorPoint(HorizontalAnchorPoint.Center)
                        .withText("EUR.USD")
                        .withFontStyle(Typeface.DEFAULT_BOLD, 72, 0x77FFFFFF)
                        .withZIndex(-1) // draw this annotation below other annotations
                        .build()
        );
    }
}
