//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2017. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// CreateMultiPaneStockChartsFragment.java is part of the SCICHART® Examples. Permission is hereby granted
// to modify, create derivative works, distribute and publish any part of this source
// code whether for commercial, private or personal use.
//
// The SCICHART® examples are distributed in the hope that they will be useful, but
// without any warranty. It is provided "AS IS" without warranty of any kind, either
// expressed or implied.
//******************************************************************************

package com.scichart.examples.fragments;

import android.view.View;

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
import com.scichart.charting.visuals.synchronization.SciChartVerticalGroup;
import com.scichart.core.common.Func1;
import com.scichart.core.utility.ListUtil;
import com.scichart.data.model.DoubleRange;
import com.scichart.examples.R;
import com.scichart.examples.data.DataManager;
import com.scichart.examples.data.MovingAverage;
import com.scichart.examples.data.PriceSeries;
import com.scichart.examples.fragments.base.ExampleBaseFragment;
import com.scichart.extensions.builders.SciChartBuilder;

import java.util.Collections;
import java.util.Date;

import butterknife.Bind;

public class CreateMultiPaneStockChartsFragment extends ExampleBaseFragment {
    private static final String VOLUME = "Volume";
    private static final String PRICES = "Prices";
    private static final String RSI = "RSI";
    private static final String MACD = "MACD";

    @Bind(R.id.priceChart)
    SciChartSurface priceChart;

    @Bind(R.id.macdChart)
    SciChartSurface macdChart;

    @Bind(R.id.rsiChart)
    SciChartSurface rsiChart;

    @Bind(R.id.volumeChart)
    SciChartSurface volumeChart;

    private final SciChartVerticalGroup verticalGroup = new SciChartVerticalGroup();

    private final DoubleRange sharedXRange = new DoubleRange();

    @Override
    protected int getLayoutId() {
        return R.layout.example_multipane_stock_charts_fragment;
    }

    @Override
    public boolean showDefaultModifiersInToolbar() {
        return false;
    }

    @Override
    protected void initExample() {
        final PriceSeries priceData = DataManager.getInstance().getPriceDataEurUsd(getActivity());

        final PricePaneModel pricePaneModel = new PricePaneModel(sciChartBuilder, priceData);
        final MacdPaneModel macdPaneModel = new MacdPaneModel(sciChartBuilder, priceData);
        final RsiPaneModel rsiPaneModel = new RsiPaneModel(sciChartBuilder, priceData);
        final VolumePaneModel volumePaneModel = new VolumePaneModel(sciChartBuilder, priceData);

        initChart(priceChart, pricePaneModel, true);
        initChart(macdChart, macdPaneModel, false);
        initChart(rsiChart, rsiPaneModel, false);
        initChart(volumeChart, volumePaneModel, false);
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
                    .withXAxisDragModifier().withReceiveHandledEvents(true).withDragMode(AxisDragModifierBase.AxisDragMode.Pan).withClipModex(ClipMode.StretchAtExtents).build()
                    .withPinchZoomModifier().withReceiveHandledEvents(true).withXyDirection(Direction2D.XDirection).build()
                    .withZoomPanModifier().withReceiveHandledEvents(true).build()
                    .withZoomExtentsModifier().withReceiveHandledEvents(true).build()
                    .withLegendModifier().withShowCheckBoxes(false).build()
                .build());

        surface.setAnnotations(model.annotations);

        verticalGroup.addSurfaceToGroup(surface);
    }

    private abstract static class BasePaneModel {
        public final RenderableSeriesCollection renderableSeries;
        public final AnnotationCollection annotations;
        public final NumericAxis yAxis;
        public final String title;

        protected BasePaneModel(SciChartBuilder builder, String title, String yAxisTextFormatting, boolean isFirstPane) {
            this.title = title;
            this.renderableSeries = new RenderableSeriesCollection();
            this.annotations = new AnnotationCollection();

            this.yAxis = builder.newNumericAxis()
                    .withAxisId(title)
                    .withTextFormatting(yAxisTextFormatting)
                    .withAutoRangeMode(AutoRange.Always)
                    .withDrawMinorGridLines(true)
                    .withDrawMajorGridLines(true)
                    .withMinorsPerMajor(isFirstPane ? 4 : 2)
                    .withMaxAutoTicks(isFirstPane ? 8 : 4)
                    .withGrowBy(isFirstPane ? new DoubleRange(0.05d, 0.05d) : new DoubleRange(0d, 0d))
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
            addRenderableSeries(builder.newCandlestickSeries().withDataSeries(stockPrices).withYAxisId(PRICES).build());

            final XyDataSeries<Date, Double> maLow = builder.newXyDataSeries(Date.class, Double.class).withSeriesName("Low Line").build();
            maLow.append(prices.getDateData(), MovingAverage.movingAverage(prices.getCloseData(), 50));
            addRenderableSeries(builder.newLineSeries().withDataSeries(maLow).withStrokeStyle(0xFFFF3333, 1f).withYAxisId(PRICES).build());

            final XyDataSeries<Date, Double> maHigh = builder.newXyDataSeries(Date.class, Double.class).withSeriesName("High Line").build();
            maHigh.append(prices.getDateData(), MovingAverage.movingAverage(prices.getCloseData(), 200));
            addRenderableSeries(builder.newLineSeries().withDataSeries(maHigh).withStrokeStyle(0xFF33DD33, 1f).withYAxisId(PRICES).build());

            Collections.addAll(annotations,
                    builder.newAxisMarkerAnnotation().withY1(stockPrices.getYValues().get(stockPrices.getCount() - 1)).withBackgroundColor(0xFFFF3333).withYAxisId(PRICES).build(),
                    builder.newAxisMarkerAnnotation().withY1(maLow.getYValues().get(maLow.getCount() - 1)).withBackgroundColor(0xFFFF3333).withYAxisId(PRICES).build(),
                    builder.newAxisMarkerAnnotation().withY1(maHigh.getYValues().get(maHigh.getCount() - 1)).withBackgroundColor(0xFF33DD33).withYAxisId(PRICES).build());
        }
    }

    private static class VolumePaneModel extends BasePaneModel {
        public VolumePaneModel(SciChartBuilder builder, PriceSeries prices) {
            super(builder, VOLUME, "###E+0", false);

            final XyDataSeries<Date, Double> volumePrices = builder.newXyDataSeries(Date.class, Double.class).withSeriesName("Volume").build();
            volumePrices.append(prices.getDateData(), ListUtil.select(prices.getVolumeData(), new Func1<Long, Double>() {
                @Override
                public Double func(Long arg) {
                    return arg.doubleValue();
                }
            }));
            addRenderableSeries(builder.newColumnSeries().withDataSeries(volumePrices).withYAxisId(VOLUME).build());

            Collections.addAll(annotations,
                    builder.newAxisMarkerAnnotation().withY1(volumePrices.getYValues().get(volumePrices.getCount() - 1)).withYAxisId(VOLUME).build());
        }
    }

    private static class RsiPaneModel extends BasePaneModel {
        public RsiPaneModel(SciChartBuilder builder, PriceSeries prices) {
            super(builder, RSI, "0.0", false);

            final XyDataSeries<Date, Double> rsiSeries = builder.newXyDataSeries(Date.class, Double.class).withSeriesName("RSI").build();
            rsiSeries.append(prices.getDateData(), MovingAverage.rsi(prices, 14));
            addRenderableSeries(builder.newLineSeries().withDataSeries(rsiSeries).withStrokeStyle(0xFFC6E6FF, 1f).withYAxisId(RSI).build());

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
            addRenderableSeries(builder.newColumnSeries().withDataSeries(histogramDataSeries).withYAxisId(MACD).build());

            final XyyDataSeries<Date, Double> macdDataSeries = builder.newXyyDataSeries(Date.class, Double.class).withSeriesName("MACD").build();
            macdDataSeries.append(prices.getDateData(), macdPoints.macdValues, macdPoints.signalValues);
            addRenderableSeries(builder.newBandSeries().withDataSeries(macdDataSeries).withYAxisId(MACD).build());

            Collections.addAll(annotations,
                    builder.newAxisMarkerAnnotation().withY1(histogramDataSeries.getYValues().get(histogramDataSeries.getCount() - 1)).withYAxisId(MACD).build(),
                    builder.newAxisMarkerAnnotation().withY1(macdDataSeries.getYValues().get(macdDataSeries.getCount() - 1)).withYAxisId(MACD).build());
        }
    }
}