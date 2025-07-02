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

import android.annotation.SuppressLint;
import android.graphics.PointF;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;

import com.scichart.charting.model.dataSeries.XyDataSeries;
import com.scichart.charting.modifiers.SeriesSelectionModifier;
import com.scichart.charting.visuals.SciChartSurface;
import com.scichart.charting.visuals.axes.NumericAxis;
import com.scichart.charting.visuals.renderableSeries.FastLineRenderableSeries;
import com.scichart.charting.visuals.renderableSeries.IRenderableSeries;
import com.scichart.charting.visuals.renderableSeries.StyleBase;
import com.scichart.charting.visuals.renderableSeries.hitTest.HitTestInfo;
import com.scichart.core.framework.UpdateSuspender;
import com.scichart.data.model.DoubleRange;
import com.scichart.drawing.common.SolidPenStyle;
import com.scichart.drawing.utility.ColorUtil;
import com.scichart.examples.R;
import com.scichart.examples.data.DataManager;
import com.scichart.examples.data.DoubleSeries;
import com.scichart.examples.fragments.base.ExampleSingleChartBaseFragment;

import java.util.Collections;
import java.util.Objects;

public class StyleSeriesOnSelectionFragment extends ExampleSingleChartBaseFragment {
    private static final int POINTS_COUNT = 500;
    private static final float THICKNESS = 4f;

    private static final int COLOR_WAVE_A = ColorUtil.argb(0xFF, 0xFF, 0x41, 0x8D);
    private static final int COLOR_WAVE_B = ColorUtil.argb(0xFF, 0xFB, 0xBE, 0x17);
    private static final int COLOR_WAVE_C = ColorUtil.argb(0xFF, 0x68, 0xBC, 0xA8);
    private static final int COLOR_WAVE_D = ColorUtil.argb(0xFF, 0xE9, 0x70, 0x64);

    private static final int COLOR_DIM = ColorUtil.Grey;

    private static final String SERIES_NAME_WAVE_A = "Wave A";
    private static final String SERIES_NAME_WAVE_B = "Wave B";
    private static final String SERIES_NAME_WAVE_C = "Wave C";
    private static final String SERIES_NAME_WAVE_D = "Wave D";

    IRenderableSeries previousSelectedSeries = null;

    @Override
    public boolean showDefaultModifiersInToolbar() {
        return false;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void initExample(@NonNull SciChartSurface surface) {
        surface.setTheme(R.style.SciChart_NavyBlue);

        DoubleSeries sineA = DataManager.getInstance().getSinewave(100.0,1.0,POINTS_COUNT,8);
        DoubleSeries sineB = DataManager.getInstance().getSinewave(200.0,1.5, POINTS_COUNT,10);
        DoubleSeries sineC = DataManager.getInstance().getSinewave(150.0,2.0, POINTS_COUNT,12);
        DoubleSeries sineD = DataManager.getInstance().getSinewave(50.0,2.5, POINTS_COUNT,14);

        NumericAxis xAxis = sciChartBuilder.newNumericAxis().withVisibleRange(new DoubleRange(0.0, 2.0)).build();
        NumericAxis yAxis = sciChartBuilder.newNumericAxis().withGrowBy(new DoubleRange(0.1,0.1)).build();

        XyDataSeries<Double, Double> dataSeriesA = sciChartBuilder.newXyDataSeries(Double.class, Double.class).withSeriesName(SERIES_NAME_WAVE_A).build();
        dataSeriesA.append(sineA.xValues, sineA.yValues);
        FastLineRenderableSeries lineA = sciChartBuilder.newLineSeries()
                .withStrokeStyle(
                new SolidPenStyle(COLOR_WAVE_A, false, THICKNESS, null)
                ).withDataSeries(dataSeriesA).build();

        XyDataSeries<Double, Double> dataSeriesB = sciChartBuilder.newXyDataSeries(Double.class, Double.class).withSeriesName(SERIES_NAME_WAVE_B).build();
        dataSeriesB.append(sineB.xValues, sineB.yValues);
        FastLineRenderableSeries lineB = sciChartBuilder.newLineSeries()
                .withStrokeStyle(
                new SolidPenStyle(COLOR_WAVE_B, false, THICKNESS, null)
                ).withDataSeries(dataSeriesB).build();

        XyDataSeries<Double, Double> dataSeriesC = sciChartBuilder.newXyDataSeries(Double.class, Double.class).withSeriesName(SERIES_NAME_WAVE_C).build();
        dataSeriesC.append(sineC.xValues, sineC.yValues);
        FastLineRenderableSeries lineC = sciChartBuilder.newLineSeries()
                .withStrokeStyle(
                new SolidPenStyle(COLOR_WAVE_C, false, THICKNESS, null)
                ).withDataSeries(dataSeriesC).build();

        XyDataSeries<Double, Double> dataSeriesD = sciChartBuilder.newXyDataSeries(Double.class, Double.class).withSeriesName(SERIES_NAME_WAVE_D).build();
        dataSeriesD.append(sineD.xValues, sineD.yValues);
        FastLineRenderableSeries lineD = sciChartBuilder.newLineSeries()
                .withStrokeStyle(
                new SolidPenStyle(COLOR_WAVE_D, false, THICKNESS, null)
                ).withDataSeries(dataSeriesD).build();

        SeriesSelectionModifier seriesSelectionModifier = new SeriesSelectionModifier();
        // Set the selected style to know which series was hit.
        seriesSelectionModifier.setSelectedSeriesStyle(new StyleBase<IRenderableSeries>(IRenderableSeries.class) {
            @Override
            protected void applyStyleInternal(IRenderableSeries renderableSeriesToStyle) {
                // if previously there was any series selected then discard it style and set it as unselected
                if(previousSelectedSeries != null){
                    discardStyleInternal(previousSelectedSeries);
                }
                adjustSeriesOpacity(renderableSeriesToStyle, true);
                // set current series selected to previous selected series so next time this can be discarded
                previousSelectedSeries = renderableSeriesToStyle;

            }

            @Override
            protected void discardStyleInternal(IRenderableSeries renderableSeriesToStyle) {
                // Mark this series to unselected and adjusts its color
                adjustSeriesOpacity(renderableSeriesToStyle, false);
                renderableSeriesToStyle.setIsSelected(false);
            }
        });

        UpdateSuspender.using(surface, () -> {
            Collections.addAll(surface.getXAxes(), xAxis);
            Collections.addAll(surface.getYAxes(), yAxis);

            Collections.addAll(surface.getRenderableSeries(), lineA, lineB, lineC, lineD);
            Collections.addAll(surface.getChartModifiers(), seriesSelectionModifier);
        });

        PointF touchPoint = new PointF();
        HitTestInfo hitTestInfo = new HitTestInfo();
        // Set the touch listener to find if a series is hit or not
        // If series if not hit then user tapped on the empty chart so reset the chart to default colors
        binding.surface.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                touchPoint.set(event.getX(), event.getY());
                surface.translatePoint(touchPoint, surface.getRenderSurface());

                boolean isAnySeriesHit = false;

                // Check if any of the series was hit
                // If no hit then user tapped on the empty screen
                for (int i = 0; i < surface.getRenderableSeries().size(); i++) {
                    IRenderableSeries series = surface.getRenderableSeries().get(i);
                    series.hitTest(hitTestInfo, touchPoint.x, touchPoint.y);
                    if(hitTestInfo.isHit){
                        isAnySeriesHit = true;
                    }
                }

                // If tap on empty screen reset opacity
                if(!isAnySeriesHit){
                    resetSeriesOpacity();
                }

                return false;
            }
        });
    }

    private void resetSeriesOpacity() {
        // Reset every series to its original color and opacity
        for (int i = 0; i < binding.surface.getRenderableSeries().size(); i++) {
            IRenderableSeries series = binding.surface.getRenderableSeries().get(i);
            series.setIsSelected(false);
            series.setOpacity(1.0f);
            setDefaultColor(series);
        }
    }

    private void adjustSeriesOpacity(IRenderableSeries selectedSeries, Boolean isSelected){
        // Loop over every series to find the selected series and adjust color and opacity of the lines
        for (int i = 0; i < binding.surface.getRenderableSeries().size(); i++) {
            IRenderableSeries series = binding.surface.getRenderableSeries().get(i);
            if(series == selectedSeries && isSelected){
                series.setOpacity(1.0f);
                setDefaultColor(series);
            } else {
                series.setOpacity(0.3f);
                series.setStrokeStyle(new SolidPenStyle(COLOR_DIM, false, THICKNESS, null));
            }
        }
    }

    private void setDefaultColor(IRenderableSeries renderableSeries){
        // Set to default color by series name
        if(Objects.equals(renderableSeries.getDataSeries().getSeriesName(), SERIES_NAME_WAVE_A)){
            renderableSeries.setStrokeStyle(new SolidPenStyle(COLOR_WAVE_A, false, THICKNESS, null));
        } else if(Objects.equals(renderableSeries.getDataSeries().getSeriesName(), SERIES_NAME_WAVE_B)){
            renderableSeries.setStrokeStyle(new SolidPenStyle(COLOR_WAVE_B, false, THICKNESS, null));
        } else if(Objects.equals(renderableSeries.getDataSeries().getSeriesName(), SERIES_NAME_WAVE_C)){
            renderableSeries.setStrokeStyle(new SolidPenStyle(COLOR_WAVE_C, false, THICKNESS, null));
        } else if(Objects.equals(renderableSeries.getDataSeries().getSeriesName(), SERIES_NAME_WAVE_D)){
            renderableSeries.setStrokeStyle(new SolidPenStyle(COLOR_WAVE_D, false, THICKNESS, null));
        }
    }
}