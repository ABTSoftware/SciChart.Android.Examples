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

import java.util.Collections;

public class ColumnChartFragment extends ExampleSingleChartBaseFragment {

    @Override
    protected void initExample(@NonNull SciChartSurface surface) {
        final IAxis xAxis = sciChartBuilder.newNumericAxis().withGrowBy(0.1, 0.1).build();
        final IAxis yAxis = sciChartBuilder.newNumericAxis().withGrowBy(0, 0.1).build();

        IXyDataSeries<Integer, Integer> dataSeries = sciChartBuilder.newXyDataSeries(Integer.class, Integer.class).build();
        final int[] yValues = {50, 35, 61, 58, 50, 50, 40, 53, 55, 23, 45, 12, 59, 60};

        for (int i = 0; i < yValues.length; i++) {
            dataSeries.append(i, yValues[i]);
        }

        final FastColumnRenderableSeries rSeries = sciChartBuilder.newColumnSeries()
                .withStrokeStyle(0xFF232323, 0.4f)
                .withDataPointWidth(0.7)
                .withLinearGradientColors(ColorUtil.LightSteelBlue, ColorUtil.SteelBlue)
                .withDataSeries(dataSeries)
                .withPaletteProvider(new ColumnsPaletteProvider())
                .build();

        UpdateSuspender.using(surface, () -> {
            Collections.addAll(surface.getXAxes(), xAxis);
            Collections.addAll(surface.getYAxes(), yAxis);
            Collections.addAll(surface.getRenderableSeries(), rSeries);
            Collections.addAll(surface.getChartModifiers(), sciChartBuilder.newModifierGroupWithDefaultModifiers().build());

            sciChartBuilder.newAnimator(rSeries).withWaveTransformation().withInterpolator(new DecelerateInterpolator()).withDuration(3000).withStartDelay(350).start();
        });
    }

    private class ColumnsPaletteProvider extends PaletteProviderBase<FastColumnRenderableSeries> implements IFillPaletteProvider {
        private final IntegerValues colors = new IntegerValues();
        private final int[] desiredColors = new int[]{0xFFa9d34f, 0xFFfc9930, 0xFFd63b3f};

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
