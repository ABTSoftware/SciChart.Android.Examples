//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2021. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// CreateMultiPaneStockChartsFragment.java is part of SCICHART®, High Performance Scientific Charts
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

package com.scichart.examples.fragments.examples2d.createStockCharts;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;

import com.scichart.charting.ClipMode;
import com.scichart.charting.Direction2D;
import com.scichart.charting.model.AnnotationCollection;
import com.scichart.charting.model.RenderableSeriesCollection;
import com.scichart.charting.model.dataSeries.OhlcDataSeries;
import com.scichart.charting.model.dataSeries.XyDataSeries;
import com.scichart.charting.model.dataSeries.XyyDataSeries;
import com.scichart.charting.modifiers.AxisDragModifierBase;
import com.scichart.charting.visuals.SciChartSurface;
import com.scichart.charting.visuals.axes.AutoRange;
import com.scichart.charting.visuals.axes.CategoryDateAxis;
import com.scichart.charting.visuals.axes.NumericAxis;
import com.scichart.charting.visuals.renderableSeries.BaseRenderableSeries;
import com.scichart.charting.visuals.renderableSeries.FastColumnRenderableSeries;
import com.scichart.charting.visuals.renderableSeries.data.XSeriesRenderPassData;
import com.scichart.charting.visuals.renderableSeries.paletteProviders.IFillPaletteProvider;
import com.scichart.charting.visuals.renderableSeries.paletteProviders.IStrokePaletteProvider;
import com.scichart.charting.visuals.renderableSeries.paletteProviders.PaletteProviderBase;
import com.scichart.charting.visuals.synchronization.SciChartVerticalGroup;
import com.scichart.core.model.IntegerValues;
import com.scichart.core.utility.ListUtil;
import com.scichart.data.model.DoubleRange;
import com.scichart.examples.R;
import com.scichart.examples.data.DataManager;
import com.scichart.examples.data.MovingAverage;
import com.scichart.examples.data.PriceSeries;
import com.scichart.examples.databinding.ExampleMultipaneStockChartsFragmentBinding;
import com.scichart.examples.fragments.base.ExampleBaseFragment;
import com.scichart.extensions.builders.SciChartBuilder;

import java.util.Collections;
import java.util.Date;

public class CreateMultiPaneStockChartsFragment extends ExampleBaseFragment<ExampleMultipaneStockChartsFragmentBinding> {
    private static final String VOLUME = "Volume";
    private static final String PRICES = "Prices";
    private static final String RSI = "RSI";
    private static final String MACD = "MACD";

    private final SciChartVerticalGroup verticalGroup = new SciChartVerticalGroup();
    private final DoubleRange sharedXRange = new DoubleRange();

    @Override
    public boolean showDefaultModifiersInToolbar() {
        return false;
    }

    @NonNull
    @Override
    protected ExampleMultipaneStockChartsFragmentBinding inflateBinding(@NonNull LayoutInflater inflater) {
        return ExampleMultipaneStockChartsFragmentBinding.inflate(inflater);
    }

    @Override
    protected void initExample(ExampleMultipaneStockChartsFragmentBinding binding) {
        binding.macdChart.setTheme(R.style.SciChart_NavyBlue);
        binding.priceChart.setTheme(R.style.SciChart_NavyBlue);
        binding.rsiChart.setTheme(R.style.SciChart_NavyBlue);
        binding.volumeChart.setTheme(R.style.SciChart_NavyBlue);

        final PriceSeries priceData = DataManager.getInstance().getPriceDataEurUsd(getActivity());

        final PricePaneModel pricePaneModel = new PricePaneModel(sciChartBuilder, priceData);
        final MacdPaneModel macdPaneModel = new MacdPaneModel(sciChartBuilder, priceData);
        final RsiPaneModel rsiPaneModel = new RsiPaneModel(sciChartBuilder, priceData);
        final VolumePaneModel volumePaneModel = new VolumePaneModel(sciChartBuilder, priceData);

        initChart(binding.priceChart, pricePaneModel, true);
        initChart(binding.macdChart, macdPaneModel, false);
        initChart(binding.rsiChart, rsiPaneModel, false);
        initChart(binding.volumeChart, volumePaneModel, false);
    }

    private void initChart(SciChartSurface surface, BasePaneModel model, boolean isMainPane) {
        final CategoryDateAxis xAxis = sciChartBuilder.newCategoryDateAxis()
                .withVisibility(isMainPane ? View.VISIBLE : View.GONE)
                .withVisibleRange(sharedXRange)
                .withGrowBy(0, 0.05)
                .build();

        surface.getXAxes().add(xAxis);
        surface.getYAxes().add(model.yAxis);

        surface.getRenderableSeries().addAll(model.renderableSeries);

        surface.getChartModifiers().add(sciChartBuilder
                .newModifierGroup()
                    .withXAxisDragModifier().withReceiveHandledEvents(true).withDragMode(AxisDragModifierBase.AxisDragMode.Pan).withClipModeX(ClipMode.StretchAtExtents).build()
                    .withPinchZoomModifier().withReceiveHandledEvents(true).withXyDirection(Direction2D.XDirection).build()
                    .withZoomPanModifier().withReceiveHandledEvents(true).build()
                    .withZoomExtentsModifier().withReceiveHandledEvents(true).build()
                    .withLegendModifier().withShowCheckBoxes(false).build()
                .build());

        surface.setAnnotations(model.annotations);

        verticalGroup.addSurfaceToGroup(surface);
    }

    private abstract static class BasePaneModel {
        public final RenderableSeriesCollection renderableSeries = new RenderableSeriesCollection();
        public final AnnotationCollection annotations = new AnnotationCollection();
        public final NumericAxis yAxis;
        public final String title;

        protected BasePaneModel(SciChartBuilder builder, String title, String yAxisTextFormatting, boolean isMainPane) {
            this.title = title;

            this.yAxis = builder.newNumericAxis()
                    .withAxisId(title)
                    .withTextFormatting(yAxisTextFormatting)
                    .withAutoRangeMode(AutoRange.Always)
                    .withMinorsPerMajor(isMainPane ? 4 : 2)
                    .withMaxAutoTicks(isMainPane ? 8 : 4)
                    .withGrowBy(isMainPane ? new DoubleRange(0.05d, 0.05d) : new DoubleRange(0d, 0d))
                    .build();
        }

        final void addRenderableSeries(BaseRenderableSeries renderableSeries) {
            renderableSeries.setClipToBounds(true);
            this.renderableSeries.add(renderableSeries);
        }
    }

    private static class PricePaneModel extends BasePaneModel {

        public PricePaneModel(SciChartBuilder builder, PriceSeries prices) {
            super(builder, PRICES, "$0.0000", true);

            // Add the main OHLC chart
            final OhlcDataSeries<Date, Double> stockPrices = builder.newOhlcDataSeries(Date.class, Double.class).withSeriesName("EUR/USD").build();
            stockPrices.append(prices.getDateData(), prices.getOpenData(), prices.getHighData(), prices.getLowData(), prices.getCloseData());
            addRenderableSeries(builder
                    .newCandlestickSeries()
                    .withDataSeries(stockPrices)
                    .withYAxisId(PRICES)
                    .withStrokeUp(0xFF67BDAF).withFillUpColor(0xFF447487)
                    .withStrokeDown(0xFFDC7969).withFillDownColor(0x77DC7969)
                    .build()
            );

            final XyDataSeries<Date, Double> maLow = builder.newXyDataSeries(Date.class, Double.class).withSeriesName("Low Line").build();
            maLow.append(prices.getDateData(), MovingAverage.movingAverage(prices.getCloseData(), 50));
            addRenderableSeries(builder.newLineSeries().withDataSeries(maLow).withStrokeStyle(0xFFEC0F6C, 1f).withYAxisId(PRICES).build());

            final XyDataSeries<Date, Double> maHigh = builder.newXyDataSeries(Date.class, Double.class).withSeriesName("High Line").build();
            maHigh.append(prices.getDateData(), MovingAverage.movingAverage(prices.getCloseData(), 200));
            addRenderableSeries(builder.newLineSeries().withDataSeries(maHigh).withStrokeStyle(0xFF50C7E0, 1f).withYAxisId(PRICES).build());

            Collections.addAll(annotations,
                    builder.newAxisMarkerAnnotation().withY1(stockPrices.getYValues().get(stockPrices.getCount() - 1)).withBackgroundColor(0xFF67BDAF).withYAxisId(PRICES).build(),
                    builder.newAxisMarkerAnnotation().withY1(maLow.getYValues().get(maLow.getCount() - 1)).withBackgroundColor(0xFFEC0F6C).withYAxisId(PRICES).build(),
                    builder.newAxisMarkerAnnotation().withY1(maHigh.getYValues().get(maHigh.getCount() - 1)).withBackgroundColor(0xFF50C7E0).withYAxisId(PRICES).build());
        }
    }

    private static class VolumePaneModel extends BasePaneModel {
        public VolumePaneModel(SciChartBuilder builder, PriceSeries prices) {
            super(builder, VOLUME, "###E+0", false);

            final XyDataSeries<Date, Double> volumePrices = builder.newXyDataSeries(Date.class, Double.class).withSeriesName(VOLUME).build();
            volumePrices.append(prices.getDateData(), ListUtil.select(prices.getVolumeData(), Long::doubleValue));
            addRenderableSeries(builder
                    .newColumnSeries()
                    .withDataSeries(volumePrices)
                    .withYAxisId(VOLUME)
                    .withPaletteProvider(new VolumePaletteProvider(prices))
                    .build()
            );

            Collections.addAll(annotations,
                    builder.newAxisMarkerAnnotation().withY1(volumePrices.getYValues().get(volumePrices.getCount() - 1)).withYAxisId(VOLUME).build());
        }

        class VolumePaletteProvider extends PaletteProviderBase<FastColumnRenderableSeries> implements IFillPaletteProvider, IStrokePaletteProvider {
            private final IntegerValues colors = new IntegerValues();
            private final int[] desiredColors = new int[]{0xFF67BDAF, 0xFFDC7969};

            private PriceSeries prices;

            protected VolumePaletteProvider(PriceSeries prices) {
                super(FastColumnRenderableSeries.class);
                this.prices = prices;
            }

            @Override
            public void update() {
                final XSeriesRenderPassData currentRenderPassData = (XSeriesRenderPassData) renderableSeries.getCurrentRenderPassData();

                final int size = currentRenderPassData.pointsCount();
                colors.setSize(size);

                final int[] colorsArray = colors.getItemsArray();
                final double[] indices = currentRenderPassData.xValues.getItemsArray();

                for (int i = 0; i < size; i++) {
                    final double index = indices[i];
                    double open = prices.get((int) index).getOpen();
                    double close = prices.get((int) index).getClose();
                    if(close - open > 0 ){
                        colorsArray[i] = desiredColors[0];
                    } else {
                        colorsArray[i] = desiredColors[1];
                    }
                }
            }

            @Override
            public IntegerValues getFillColors() {
                return colors;
            }

            @Override
            public IntegerValues getStrokeColors() {
                return colors;
            }
        }
    }

    private static class RsiPaneModel extends BasePaneModel {
        public RsiPaneModel(SciChartBuilder builder, PriceSeries prices) {
            super(builder, RSI, "0.0", false);

            final XyDataSeries<Date, Double> rsiSeries = builder.newXyDataSeries(Date.class, Double.class).withSeriesName(RSI).build();
            rsiSeries.append(prices.getDateData(), MovingAverage.rsi(prices, 14));
            addRenderableSeries(builder.newLineSeries().withDataSeries(rsiSeries).withStrokeStyle(0xFF537ABD, 1f).withYAxisId(RSI).build());

            Collections.addAll(annotations,
                    builder.newAxisMarkerAnnotation().withY1(rsiSeries.getYValues().get(rsiSeries.getCount() - 1)).withYAxisId(RSI).build());
        }
    }

    private static class MacdPaneModel extends BasePaneModel {
        public MacdPaneModel(SciChartBuilder builder, PriceSeries prices) {
            super(builder, MACD, "0.00", false);

            final MovingAverage.MacdPoints macdPoints = MovingAverage.macd(prices.getCloseData(), 12, 25, 9);

            final XyDataSeries<Date, Double> histogramDataSeries = builder.newXyDataSeries(Date.class, Double.class).withSeriesName("Histogram").build();
            histogramDataSeries.append(prices.getDateData(), macdPoints.divergenceValues);
            addRenderableSeries(builder
                    .newColumnSeries()
                    .withDataSeries(histogramDataSeries)
                    .withPaletteProvider(new MacdHistogramPaletteProvider())
                    .withYAxisId(MACD)
                    .build()
            );

            final XyyDataSeries<Date, Double> macdDataSeries = builder.newXyyDataSeries(Date.class, Double.class).withSeriesName(MACD).build();
            macdDataSeries.append(prices.getDateData(), macdPoints.macdValues, macdPoints.signalValues);
            addRenderableSeries(builder
                    .newBandSeries()
                    .withDataSeries(macdDataSeries)
                    .withYAxisId(MACD)
                    .withStrokeStyle(0xFF67BDAF).withStrokeY1Style(0xFFDC7969)
                    .withFillColor(0x77DC7969).withFillY1Color(0x7767BDAF)
                    .build()
            );

            Collections.addAll(annotations,
                    builder.newAxisMarkerAnnotation().withY1(histogramDataSeries.getYValues().get(histogramDataSeries.getCount() - 1)).withYAxisId(MACD).build(),
                    builder.newAxisMarkerAnnotation().withY1(macdDataSeries.getYValues().get(macdDataSeries.getCount() - 1)).withYAxisId(MACD).build());
        }

        class MacdHistogramPaletteProvider extends PaletteProviderBase<FastColumnRenderableSeries> implements IFillPaletteProvider, IStrokePaletteProvider {
            private final IntegerValues colors = new IntegerValues();
            private final int[] desiredColors = new int[]{0xFF67BDAF, 0xFFDC7969};

            protected MacdHistogramPaletteProvider() {
                super(FastColumnRenderableSeries.class);
            }

            @Override
            public void update() {
                final XSeriesRenderPassData currentRenderPassData = (XSeriesRenderPassData) renderableSeries.getCurrentRenderPassData();

                final int size = currentRenderPassData.pointsCount();
                colors.setSize(size);

                final int[] colorsArray = colors.getItemsArray();
                final double[] indices = currentRenderPassData.xValues.getItemsArray();

                XyDataSeries<Date, Double> dataSeries = (XyDataSeries<Date, Double>) renderableSeries.getDataSeries();

                for (int i = 0; i < size; i++) {
                    final double index = indices[i];
                    double value = dataSeries.getYValues().get((int) index);
                    if(value > 0 ){
                        colorsArray[i] = desiredColors[0];
                    } else {
                        colorsArray[i] = desiredColors[1];
                    }
                }
            }

            @Override
            public IntegerValues getFillColors() {
                return colors;
            }

            @Override
            public IntegerValues getStrokeColors() {
                return colors;
            }
        }
    }
}