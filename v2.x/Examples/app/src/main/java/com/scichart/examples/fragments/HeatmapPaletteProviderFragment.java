//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2017. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// HeatmapPaletteProviderFragment.java is part of the SCICHART® Examples. Permission is hereby granted
// to modify, create derivative works, distribute and publish any part of this source
// code whether for commercial, private or personal use.
//
// The SCICHART® examples are distributed in the hope that they will be useful, but
// without any warranty. It is provided "AS IS" without warranty of any kind, either
// expressed or implied.
//******************************************************************************

package com.scichart.examples.fragments;

import android.graphics.Color;
import android.widget.SeekBar;
import android.widget.TextView;

import com.scichart.charting.model.dataSeries.UniformHeatmapDataSeries;
import com.scichart.charting.visuals.SciChartSurface;
import com.scichart.charting.visuals.axes.AxisAlignment;
import com.scichart.charting.visuals.axes.NumericAxis;
import com.scichart.charting.visuals.renderableSeries.paletteProviders.IUniformHeatmapPaletteProvider;
import com.scichart.charting.visuals.renderableSeries.paletteProviders.PaletteProviderBase;
import com.scichart.charting.visuals.renderableSeries.FastUniformHeatmapRenderableSeries;
import com.scichart.charting.visuals.renderableSeries.data.UniformHeatmapRenderPassData;
import com.scichart.core.model.DoubleValues;
import com.scichart.core.model.IValues;
import com.scichart.core.model.IntegerValues;
import com.scichart.examples.R;
import com.scichart.examples.fragments.base.ExampleBaseFragment;

import java.util.Collections;
import java.util.Random;

import butterknife.Bind;

public class HeatmapPaletteProviderFragment extends ExampleBaseFragment implements SeekBar.OnSeekBarChangeListener {
    private static final int WIDTH = 300, HEIGHT = 200;

    @Bind(R.id.chart)
    SciChartSurface chart;

    @Bind(R.id.seekBar)
    SeekBar seekBar;

    @Bind(R.id.thresholdValue)
    TextView thresholdValue;

    private final CustomUniformHeatMapProvider paletteProvider = new CustomUniformHeatMapProvider();

    @Override
    protected int getLayoutId() { return R.layout.example_heatmap_palette_fragment; }

    @Override
    protected void initExample() {
        seekBar.setOnSeekBarChangeListener(this);

        final NumericAxis xAxis = sciChartBuilder.newNumericAxis().withAxisAlignment(AxisAlignment.Bottom).build();
        final NumericAxis yAxis = sciChartBuilder.newNumericAxis().withAxisAlignment(AxisAlignment.Right).build();

        final UniformHeatmapDataSeries<Integer, Integer, Double> dataSeries = new UniformHeatmapDataSeries<>(Integer.class, Integer.class, Double.class, WIDTH, HEIGHT);

        paletteProvider.setThresholdValue(seekBar.getProgress());

        dataSeries.updateZValues(createValues());
        final FastUniformHeatmapRenderableSeries heatmapRenderableSeries = sciChartBuilder.newUniformHeatmap()
                .withDataSeries(dataSeries)
                .withMinimum(0)
                .withMaximum(200)
                .withPaletteProvider(paletteProvider)
                .build();

        Collections.addAll(chart.getXAxes(), xAxis);
        Collections.addAll(chart.getYAxes(), yAxis);
        Collections.addAll(chart.getRenderableSeries(), heatmapRenderableSeries);
        Collections.addAll(chart.getChartModifiers(), sciChartBuilder.newModifierGroupWithDefaultModifiers().build());
    }

    private static IValues<Double> createValues() {
        final DoubleValues values = new DoubleValues(WIDTH * HEIGHT);

        final Random random = new Random();
        final double angle = Math.PI * 2;
        final double cx =150, cy = 100;
        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {
                final double v = (1 + Math.sin(x * 0.04 + angle)) * 50 + (1 + Math.sin(y * 0.1 + angle)) * 50 * (1 + Math.sin(angle * 2));
                final double r = Math.sqrt((x - cx) * (x - cx) + (y - cy) * (y - cy));
                final double exp = Math.max(0, 1 - r * 0.008);

                values.add(v * exp + random.nextDouble() * 50);
            }
        }

        return values;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        paletteProvider.setThresholdValue(progress);
        thresholdValue.setText(String.format("%d", progress));
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) { }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {  }

    private static class CustomUniformHeatMapProvider extends PaletteProviderBase<FastUniformHeatmapRenderableSeries> implements IUniformHeatmapPaletteProvider {
        private double thresholdValue;

        public CustomUniformHeatMapProvider() {
            super(FastUniformHeatmapRenderableSeries.class);
        }

        public void setThresholdValue(double thresholdValue) {
            this.thresholdValue = thresholdValue;

            if(renderableSeries != null)
                renderableSeries.invalidateElement();
        }

        @Override
        public boolean shouldSetColors() {
            return false;
        }

        @Override
        public void update() {
            final FastUniformHeatmapRenderableSeries renderableSeries = this.renderableSeries;
            final UniformHeatmapRenderPassData currentRenderPassData = (UniformHeatmapRenderPassData) renderableSeries.getCurrentRenderPassData();

            final DoubleValues zValues = currentRenderPassData.zValues;
            final IntegerValues zColors = currentRenderPassData.zColors;

            final int size = zValues.size();

            zColors.setSize(size);

            // working with array is much faster than calling set() many times
            final double[] zValuesArray = zValues.getItemsArray();
            final int[] zColorsArray = zColors.getItemsArray();

            for (int zIndex = 0; zIndex < size; zIndex++) {
                final double value = zValuesArray[zIndex];

                zColorsArray[zIndex] = value < thresholdValue ? Color.BLACK : Color.WHITE;
            }
        }
    }
}
