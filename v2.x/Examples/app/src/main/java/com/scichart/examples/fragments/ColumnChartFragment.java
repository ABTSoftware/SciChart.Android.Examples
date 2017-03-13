//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2017. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// ColumnChartFragment.java is part of the SCICHART® Examples. Permission is hereby granted
// to modify, create derivative works, distribute and publish any part of this source
// code whether for commercial, private or personal use.
//
// The SCICHART® examples are distributed in the hope that they will be useful, but
// without any warranty. It is provided "AS IS" without warranty of any kind, either
// expressed or implied.
//******************************************************************************

package com.scichart.examples.fragments;

import com.scichart.charting.model.dataSeries.IXyDataSeries;
import com.scichart.charting.visuals.SciChartSurface;
import com.scichart.charting.visuals.axes.IAxis;
import com.scichart.charting.visuals.renderableSeries.FastColumnRenderableSeries;
import com.scichart.charting.visuals.renderableSeries.paletteProviders.IFillPaletteProvider;
import com.scichart.charting.visuals.renderableSeries.paletteProviders.PaletteProviderBase;
import com.scichart.core.framework.UpdateSuspender;
import com.scichart.core.model.IntegerValues;
import com.scichart.drawing.utility.ColorUtil;
import com.scichart.examples.R;
import com.scichart.examples.fragments.base.ExampleBaseFragment;

import java.util.Collections;

import butterknife.Bind;

public class ColumnChartFragment extends ExampleBaseFragment {

    @Bind(R.id.chart)
    SciChartSurface surface;

    @Override
    protected int getLayoutId() {
        return R.layout.example_single_chart_fragment;
    }

    @Override
    protected void initExample() {
        IXyDataSeries<Integer, Integer> dataSeries = sciChartBuilder.newXyDataSeries(Integer.class, Integer.class).build();
        final int[] yValues = {50, 35, 61, 58, 50, 50, 40, 53, 55, 23, 45, 12, 59, 60};

        for (int i = 0; i < yValues.length; i++) {
            dataSeries.append(i, yValues[i]);
        }

        final IAxis xAxis = sciChartBuilder.newNumericAxis().withGrowBy(0.1, 0.1).build();
        final IAxis yAxis = sciChartBuilder.newNumericAxis().withGrowBy(0, 0.1).build();

        final FastColumnRenderableSeries columnSeries = sciChartBuilder.newColumnSeries()
                .withStrokeStyle(0xFF232323, 0.4f)
                .withDataPointWidth(0.7)
                .withLinearGradientColors(ColorUtil.LightSteelBlue, ColorUtil.SteelBlue)
                .withDataSeries(dataSeries)
                .withPaletteProvider(new ColumnsPaletteProvider())
                .build();

        surface.getChartModifiers().add(sciChartBuilder.newModifierGroupWithDefaultModifiers().build());

        UpdateSuspender.using(surface, new Runnable() {
            @Override
            public void run() {
                Collections.addAll(surface.getXAxes(), xAxis);
                Collections.addAll(surface.getYAxes(), yAxis);
                Collections.addAll(surface.getRenderableSeries(), columnSeries);
            }
        });
    }

    private class ColumnsPaletteProvider extends PaletteProviderBase<FastColumnRenderableSeries> implements IFillPaletteProvider {
        /*
            Gradients as in iOS, we don't support gradient palette provider yet
            #1 start: 0xFFa9d34f; finish: 0xFF93b944; PEN 0xFF232323
            #2 start: 0xFFfc9930; finish: 0xFFd17f28; PEN 0xFF232323
            #3 start: 0xFFd63b3f; finish: 0xFFbc3337; PEN 0xFF232323
         */
        private final IntegerValues colors = new IntegerValues();
        private final int[] desiredColors = new int[]{0xFFa9d34f, 0xFFfc9930, 0xFFd63b3f};

        protected ColumnsPaletteProvider() {
            super(FastColumnRenderableSeries.class);
        }

        @Override
        public void update() {
            final int size = this.renderableSeries.getCurrentRenderPassData().pointsCount();
            colors.setSize(size);

            final int[] colorsArray = colors.getItemsArray();
            for (int i = 0; i < size; i++) {
                colorsArray[i] = desiredColors[i % 3];
            }
        }

        @Override
        public IntegerValues getFillColors() {
            return colors;
        }
    }
}