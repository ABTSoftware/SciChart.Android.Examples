//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2021. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// ColumnChartFragment.java is part of SCICHART®, High Performance Scientific Charts
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

package com.scichart.examples.fragments.examples2d.basicChartTypes;

import android.view.animation.DecelerateInterpolator;

import androidx.annotation.NonNull;

import com.scichart.charting.model.dataSeries.IXyDataSeries;
import com.scichart.charting.visuals.SciChartSurface;
import com.scichart.charting.visuals.axes.IAxis;
import com.scichart.charting.visuals.renderableSeries.FastColumnRenderableSeries;
import com.scichart.charting.visuals.renderableSeries.data.XSeriesRenderPassData;
import com.scichart.charting.visuals.renderableSeries.paletteProviders.IFillPaletteProvider;
import com.scichart.charting.visuals.renderableSeries.paletteProviders.PaletteProviderBase;
import com.scichart.core.framework.UpdateSuspender;
import com.scichart.core.model.IntegerValues;
import com.scichart.drawing.utility.ColorUtil;
import com.scichart.examples.fragments.base.ExampleSingleChartBaseFragment;
import com.scichart.examples.utils.Constant;
import com.scichart.examples.utils.interpolator.DefaultInterpolator;

import java.util.Collections;

public class ColumnChartFragment extends ExampleSingleChartBaseFragment {

    @Override
    protected void initExample(@NonNull SciChartSurface surface) {
        final IAxis xAxis = sciChartBuilder.newNumericAxis().withGrowBy(0.1, 0.1).build();
        final IAxis yAxis = sciChartBuilder.newNumericAxis().withGrowBy(0, 0.1).build();

        final int[] xValues = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19};
        final int[] yValues = {1, 2, 4, 8, 11, 15, 24, 46, 81, 117, 144, 160, 137, 101, 64, 35, 25, 14, 4, 1};

        IXyDataSeries<Integer, Integer> dataSeries = sciChartBuilder
                .newXyDataSeries(Integer.class, Integer.class)
                .build();

        for (int i = 0; i < xValues.length; i++) {
            dataSeries.append(xValues[i], yValues[i]);
        }

        final FastColumnRenderableSeries rSeries = sciChartBuilder.newColumnSeries()
                .withStrokeStyle(0xFFE4F5FC, 0.4f)
                .withDataPointWidth(0.7)
                .withLinearGradientColors(ColorUtil.LightSteelBlue, ColorUtil.SteelBlue)
                .withDataSeries(dataSeries)
//                .withPaletteProvider(new ColumnsPaletteProvider())
                .build();

        UpdateSuspender.using(surface, () -> {
            Collections.addAll(surface.getXAxes(), xAxis);
            Collections.addAll(surface.getYAxes(), yAxis);
            Collections.addAll(surface.getRenderableSeries(), rSeries);
            Collections.addAll(surface.getChartModifiers(), sciChartBuilder.newModifierGroupWithDefaultModifiers().build());

            sciChartBuilder.newAnimator(rSeries).withWaveTransformation().withInterpolator(DefaultInterpolator.getInterpolator()).withDuration(Constant.ANIMATION_DURATION).withStartDelay(Constant.ANIMATION_START_DELAY).start();
        });
    }

    private class ColumnsPaletteProvider extends PaletteProviderBase<FastColumnRenderableSeries> implements IFillPaletteProvider {
        private final IntegerValues colors = new IntegerValues();
        private final int[] desiredColors = new int[]{0xFF21a0d8, 0xFFc43360, 0xFF34c19c};

        protected ColumnsPaletteProvider() {
            super(FastColumnRenderableSeries.class);
        }

        @Override
        public void update() {
            final XSeriesRenderPassData currentRenderPassData = (XSeriesRenderPassData) renderableSeries.getCurrentRenderPassData();

            final int size = currentRenderPassData.pointsCount();
            colors.setSize(size);

            final int[] colorsArray = colors.getItemsArray();
            final int[] indices = currentRenderPassData.indices.getItemsArray();
            for (int i = 0; i < size; i++) {
                final int index = indices[i];
                colorsArray[i] = desiredColors[index % 3];
            }
        }

        @Override
        public IntegerValues getFillColors() {
            return colors;
        }
    }
}
