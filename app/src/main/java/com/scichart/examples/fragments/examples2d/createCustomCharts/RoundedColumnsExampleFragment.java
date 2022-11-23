//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2021. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// RoundedColumnsExampleFragment.java is part of SCICHART®, High Performance Scientific Charts
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

package com.scichart.examples.fragments.examples2d.createCustomCharts;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.animation.OvershootInterpolator;

import androidx.annotation.NonNull;

import com.scichart.charting.model.dataSeries.IDataSeries;
import com.scichart.charting.model.dataSeries.IXyDataSeries;
import com.scichart.charting.visuals.SciChartSurface;
import com.scichart.charting.visuals.axes.IAxis;
import com.scichart.charting.visuals.renderableSeries.FastColumnRenderableSeries;
import com.scichart.charting.visuals.renderableSeries.FastLineRenderableSeries;
import com.scichart.charting.visuals.renderableSeries.data.ColumnRenderPassData;
import com.scichart.charting.visuals.renderableSeries.data.ISeriesRenderPassData;
import com.scichart.charting.visuals.renderableSeries.hitTest.ColumnHitProvider;
import com.scichart.charting.visuals.renderableSeries.hitTest.NearestColumnPointProvider;
import com.scichart.core.framework.UpdateSuspender;
import com.scichart.core.model.FloatValues;
import com.scichart.drawing.common.BrushStyle;
import com.scichart.drawing.common.IAssetManager2D;
import com.scichart.drawing.common.IBrush2D;
import com.scichart.drawing.common.IRenderContext2D;
import com.scichart.drawing.common.SolidBrushStyle;
import com.scichart.examples.fragments.base.ExampleSingleChartBaseFragment;

import java.util.Collections;

public class RoundedColumnsExampleFragment extends ExampleSingleChartBaseFragment {

    @Override
    protected void initExample(@NonNull SciChartSurface surface) {
        final IAxis xAxis = sciChartBuilder.newNumericAxis().withGrowBy(0.1d, 0.1d).build();
        final IAxis yAxis = sciChartBuilder.newNumericAxis().withGrowBy(0.2d, 0.2d).build();

        final IXyDataSeries<Integer, Integer> dataSeries = sciChartBuilder.newXyDataSeries(Integer.class, Integer.class).build();
        final int[] yValues = {50, 35, 61, 58, 50, 50, 40, 53, 55, 23, 45, 12, 59, 60};
        for (int i = 0; i < yValues.length; i++) {
            dataSeries.append(i, yValues[i]);
        }

        final RoundedColumnsRenderableSeries rSeries = new RoundedColumnsRenderableSeries.Builder(requireContext())
                .withDataSeries(dataSeries)
                .withFillColor(0xFF634e96)
                .build();

        UpdateSuspender.using(surface, () -> {
            Collections.addAll(surface.getXAxes(), xAxis);
            Collections.addAll(surface.getYAxes(), yAxis);
            Collections.addAll(surface.getRenderableSeries(), rSeries);
            Collections.addAll(surface.getChartModifiers(), sciChartBuilder.newModifierGroupWithDefaultModifiers().build());

            sciChartBuilder.newAnimator(rSeries).withScaleTransformation().withInterpolator(new OvershootInterpolator()).withDuration(1500).withStartDelay(350).start();
        });
    }

    /**
     * A CustomRenderableSeries example which draws Rounded Columns
     */
     static class RoundedColumnsRenderableSeries extends FastColumnRenderableSeries {

        private final FloatValues topEllipseBuffer = new FloatValues();
        private final FloatValues rectsBuffer = new FloatValues();
        private final FloatValues bottomEllipseBuffer = new FloatValues();

        /**
         * Creates a new instance of {@link FastLineRenderableSeries} class
         */
        public RoundedColumnsRenderableSeries() {
            super(new ColumnRenderPassData(), new ColumnHitProvider(), new NearestColumnPointProvider());
        }

        @Override
        protected void disposeCachedData() {
            super.disposeCachedData();

            topEllipseBuffer.disposeItems();
            rectsBuffer.disposeItems();
            bottomEllipseBuffer.disposeItems();
        }

        @Override
        protected void internalDraw(IRenderContext2D renderContext, IAssetManager2D assetManager, ISeriesRenderPassData renderPassData) {
            // Don't draw transparent series
            final float opacity = getOpacity();
            if (opacity == 0) return;

            final BrushStyle fillBrush = getFillBrushStyle();
            if (fillBrush == null || !fillBrush.isVisible()) return;

            final ColumnRenderPassData rpd = (ColumnRenderPassData) renderPassData;
            final float diameter = rpd.columnPixelWidth;
            updateDrawingBuffers(rpd, diameter, rpd.zeroLineCoord);

            final IBrush2D brush = assetManager.createBrush(fillBrush);
            renderContext.fillRects(rectsBuffer.getItemsArray(), 0, rectsBuffer.size(), brush);
            renderContext.drawEllipses(topEllipseBuffer.getItemsArray(), 0, topEllipseBuffer.size(), diameter, diameter, brush);
            renderContext.drawEllipses(bottomEllipseBuffer.getItemsArray(), 0, bottomEllipseBuffer.size(), diameter, diameter, brush);
        }

        private void updateDrawingBuffers(ColumnRenderPassData renderPassData, float columnPixelWidth, float zeroLine) {
            final float halfWidth = columnPixelWidth / 2;

            topEllipseBuffer.setSize(renderPassData.pointsCount() * 2);
            rectsBuffer.setSize(renderPassData.pointsCount() * 4);
            bottomEllipseBuffer.setSize(renderPassData.pointsCount() * 2);

            final float[] topArray = topEllipseBuffer.getItemsArray();
            final float[] rectsArray = rectsBuffer.getItemsArray();
            final float[] bottomArray = bottomEllipseBuffer.getItemsArray();

            final float[] xCoordsArray = renderPassData.xCoords.getItemsArray();
            final float[] yCoordsArray = renderPassData.yCoords.getItemsArray();
            for (int i = 0, count = renderPassData.pointsCount(); i < count; i++) {
                final float x = xCoordsArray[i];
                final float y = yCoordsArray[i];

                topArray[i * 2] = x;
                topArray[i * 2 + 1] = y - halfWidth;

                rectsArray[i * 4] = x - halfWidth;
                rectsArray[i * 4 + 1] = y - halfWidth;
                rectsArray[i * 4 + 2] = x + halfWidth;
                rectsArray[i * 4 + 3] = zeroLine + halfWidth;

                bottomArray[i * 2] = x;
                bottomArray[i * 2 + 1] = zeroLine + halfWidth;
            }
        }

        public static class Builder {
            private final DisplayMetrics displayMetrics;
            private final RoundedColumnsRenderableSeries renderableSeries;

            public Builder(Context context) {
                this.displayMetrics = context.getResources().getDisplayMetrics();
                this.renderableSeries = new RoundedColumnsRenderableSeries();
            }

            public Builder withDataSeries(IDataSeries<?,?> dataSeries) {
                renderableSeries.setDataSeries(dataSeries);
                return this;
            }

            public Builder withFillColor(int fillColor) {
                renderableSeries.setFillBrushStyle(new SolidBrushStyle(fillColor));
                return this;
            }

            public RoundedColumnsRenderableSeries build() {
                return renderableSeries;
            }
        }
    }
}