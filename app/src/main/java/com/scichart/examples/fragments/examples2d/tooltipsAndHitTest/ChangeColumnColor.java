//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2021. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// ChangeColumnColor.java is part of SCICHART®, High Performance Scientific Charts
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

import android.content.Context;
import android.graphics.PointF;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.scichart.charting.model.dataSeries.XyDataSeries;
import com.scichart.charting.modifiers.CursorModifier;
import com.scichart.charting.modifiers.RolloverModifier;
import com.scichart.charting.modifiers.TooltipModifier;
import com.scichart.charting.visuals.SciChartSurface;
import com.scichart.charting.visuals.axes.AxisAlignment;
import com.scichart.charting.visuals.axes.IAxis;
import com.scichart.charting.visuals.pointmarkers.EllipsePointMarker;
import com.scichart.charting.visuals.renderableSeries.FastColumnRenderableSeries;
import com.scichart.charting.visuals.renderableSeries.FastLineRenderableSeries;
import com.scichart.charting.visuals.renderableSeries.IRenderableSeries;
import com.scichart.charting.visuals.renderableSeries.data.XSeriesRenderPassData;
import com.scichart.charting.visuals.renderableSeries.hitTest.DefaultXySeriesInfoProvider;
import com.scichart.charting.visuals.renderableSeries.hitTest.HitTestInfo;
import com.scichart.charting.visuals.renderableSeries.hitTest.XySeriesInfo;
import com.scichart.charting.visuals.renderableSeries.paletteProviders.IFillPaletteProvider;
import com.scichart.charting.visuals.renderableSeries.paletteProviders.PaletteProviderBase;
import com.scichart.charting.visuals.renderableSeries.tooltips.ISeriesTooltip;
import com.scichart.charting.visuals.renderableSeries.tooltips.XySeriesTooltip;
import com.scichart.core.framework.UpdateSuspender;
import com.scichart.core.model.DoubleValues;
import com.scichart.core.model.IntegerValues;
import com.scichart.core.utility.StringUtil;
import com.scichart.drawing.utility.ColorUtil;
import com.scichart.examples.R;
import com.scichart.examples.data.DataManager;
import com.scichart.examples.data.DoubleSeries;
import com.scichart.examples.fragments.base.ExampleSingleChartBaseFragment;
import com.scichart.examples.utils.Constant;
import com.scichart.examples.utils.interpolator.DefaultInterpolator;

import java.util.Collections;

public class ChangeColumnColor extends ExampleSingleChartBaseFragment implements View.OnTouchListener {

    private final PointF touchPoint = new PointF();
    private final HitTestInfo hitTestInfo = new HitTestInfo();
    private XyDataSeries<Double, Double> dataSeries1;
    private int touchedIndex = -1;

    @Override
    public boolean showDefaultModifiersInToolbar() {
        return false;
    }

    @Override
    protected void initExample(@NonNull SciChartSurface surface) {
        surface.setTheme(R.style.SciChart_NavyBlue);

        final IAxis xAxis = sciChartBuilder.newNumericAxis().withGrowBy(0.1, 0.1).build();
        final IAxis yAxis = sciChartBuilder.newNumericAxis().withGrowBy(0.0, 0.1).withAxisAlignment(AxisAlignment.Left).build();

        dataSeries1 = sciChartBuilder.newXyDataSeries(Double.class, Double.class).withAcceptsUnsortedData().build();

        final int[] yValues = {50, 35, 61, 58, 50, 50, 40, 53, 55, 23, 45, 12, 59, 60};

        for (int i = 0; i < yValues.length; i++) {
            dataSeries1.append((double) i, (double) yValues[i]);
        }

        ColumnsPaletteProvider columnsPaletteProvider = new ColumnsPaletteProvider();

        final FastColumnRenderableSeries line = sciChartBuilder.newColumnSeries()
                .withDataPointWidth(0.7)
                .withDataSeries(dataSeries1)
                .withPaletteProvider(columnsPaletteProvider)
                .withSeriesInfoProvider(new FirstCustomSeriesInfoProvider())
                .build();

        UpdateSuspender.using(surface, () -> {
            Collections.addAll(surface.getXAxes(), xAxis);
            Collections.addAll(surface.getYAxes(), yAxis);
            Collections.addAll(surface.getRenderableSeries(), line);
            Collections.addAll(surface.getChartModifiers(), new TooltipModifier());

        });

        surface.setOnTouchListener(this);

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        SciChartSurface surface = (SciChartSurface) v;
        touchPoint.set(event.getX(), event.getY());
        surface.translatePoint(touchPoint, surface.getRenderableSeriesArea());

        for (IRenderableSeries renderableSeries : surface.getRenderableSeries()) {
            renderableSeries.hitTest(hitTestInfo, touchPoint.x, touchPoint.y, 30);
            if (hitTestInfo.isHit) {
                touchedIndex = hitTestInfo.dataSeriesIndex;
            } else {
                touchedIndex = -1;
            }
            surface.invalidateElement();
        }
        return false;
    }

    private class ColumnsPaletteProvider extends PaletteProviderBase<FastColumnRenderableSeries> implements IFillPaletteProvider {
        private final IntegerValues colors = new IntegerValues();
        private final int[] desiredColors = new int[]{0xFF21a0d8, 0xFFc43360};

        protected ColumnsPaletteProvider() {
            super(FastColumnRenderableSeries.class);
        }

        @Override
        public void update() {
            final XSeriesRenderPassData currentRenderPassData = (XSeriesRenderPassData) renderableSeries.getCurrentRenderPassData();

            final int size = currentRenderPassData.pointsCount();
            colors.setSize(size);

            final int[] colorsArray = colors.getItemsArray();
            for (int i = 0; i < size; i++) {
                if(touchedIndex == i){
                    colorsArray[i] = desiredColors[1];
                } else {
                    colorsArray[i] = desiredColors[0];
                }
            }
        }

        @Override
        public IntegerValues getFillColors() {
            return colors;
        }

    }

    private static class FirstCustomSeriesInfoProvider extends DefaultXySeriesInfoProvider {
        @Override
        protected ISeriesTooltip getSeriesTooltipInternal(Context context, XySeriesInfo<?> seriesInfo, Class<?> modifierType) {
            if (modifierType == TooltipModifier.class) {
                return new FirstCustomXySeriesTooltip(context, seriesInfo);
            } else if (modifierType == RolloverModifier.class) {
                return new FirstCustomXySeriesTooltip(context, seriesInfo);
            } else if (modifierType == CursorModifier.class) {
                return new FirstCustomXySeriesTooltip(context, seriesInfo);
            } else {
                return super.getSeriesTooltipInternal(context, seriesInfo, modifierType);
            }
        }

        private static class FirstCustomXySeriesTooltip extends XySeriesTooltip {

            public FirstCustomXySeriesTooltip(Context context, XySeriesInfo<?> seriesInfo) {
                super(context, seriesInfo);
            }

            @Override
            protected void internalUpdate(XySeriesInfo seriesInfo) {
                final SpannableStringBuilder sb = new SpannableStringBuilder();
                sb.append("X: ").append(seriesInfo.getFormattedXValue()).append(StringUtil.NEW_LINE);
                sb.append("Y: ").append(seriesInfo.getFormattedYValue());
                setText(sb);

                setSeriesColor(0xffffffff);
            }
        }
    }
}