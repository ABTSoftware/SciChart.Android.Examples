//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2017. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// CreateMultiPaneStockChartFragment.java is part of the SCICHART® Examples. Permission is hereby granted
// to modify, create derivative works, distribute and publish any part of this source
// code whether for commercial, private or personal use.
//
// The SCICHART® examples are distributed in the hope that they will be useful, but
// without any warranty. It is provided "AS IS" without warranty of any kind, either
// expressed or implied.
//******************************************************************************

package com.scichart.examples.fragments;

import com.scichart.charting.ClipMode;
import com.scichart.charting.Direction2D;
import com.scichart.charting.layoutManagers.ChartLayoutState;
import com.scichart.charting.layoutManagers.DefaultLayoutManager;
import com.scichart.charting.layoutManagers.VerticalAxisLayoutStrategy;
import com.scichart.charting.model.AnnotationCollection;
import com.scichart.charting.model.RenderableSeriesCollection;
import com.scichart.charting.model.dataSeries.OhlcDataSeries;
import com.scichart.charting.model.dataSeries.XyDataSeries;
import com.scichart.charting.model.dataSeries.XyyDataSeries;
import com.scichart.charting.modifiers.AxisDragModifierBase;
import com.scichart.charting.visuals.SciChartSurface;
import com.scichart.charting.visuals.axes.AutoRange;
import com.scichart.charting.visuals.axes.AxisLayoutState;
import com.scichart.charting.visuals.axes.CategoryDateAxis;
import com.scichart.charting.visuals.axes.IAxis;
import com.scichart.charting.visuals.axes.NumericAxis;
import com.scichart.charting.visuals.renderableSeries.BaseRenderableSeries;
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
import java.util.HashMap;

import butterknife.BindView;

public class CreateMultiPaneStockChartFragment extends ExampleBaseFragment {
    private static final String VOLUME = "Volume";
    private static final String PRICES = "Prices";
    private static final String RSI = "RSI";
    private static final String MACD = "MACD";

    @BindView(R.id.chart)
    SciChartSurface surface;

    @Override
    protected int getLayoutId() {
        return R.layout.example_single_chart_fragment;
    }

    @Override
    protected void initExample() {

        final DefaultLayoutManager layoutManager = new DefaultLayoutManager.Builder().setRightOuterAxesLayoutStrategy(new RightAlignedOuterVerticallyStackedYAxisLayoutStrategy()).build();
        surface.setLayoutManager(layoutManager);

        final CategoryDateAxis xAxis = sciChartBuilder.newCategoryDateAxis().withGrowBy(0, 0.05).build();
        surface.getXAxes().add(xAxis);

        final PriceSeries priceData = DataManager.getInstance().getPriceDataEurUsd(getActivity());

        final PricePaneModel pricePaneModel = new PricePaneModel(sciChartBuilder, priceData);
        final MacdPaneModel macdPaneModel = new MacdPaneModel(sciChartBuilder, priceData);
        final RsiPaneModel rsiPaneModel = new RsiPaneModel(sciChartBuilder, priceData);
        final VolumePaneModel volumePaneModel = new VolumePaneModel(sciChartBuilder, priceData);

        addModel(pricePaneModel);
        addModel(macdPaneModel);
        addModel(rsiPaneModel);
        addModel(volumePaneModel);

        surface.getChartModifiers().add(sciChartBuilder
                .newModifierGroup()
                    .withXAxisDragModifier().withReceiveHandledEvents(true).withDragMode(AxisDragModifierBase.AxisDragMode.Pan).withClipModeX(ClipMode.StretchAtExtents).build()
                    .withPinchZoomModifier().withReceiveHandledEvents(true).withXyDirection(Direction2D.XDirection).build()
                    .withZoomPanModifier().withReceiveHandledEvents(true).build()
                    .withZoomExtentsModifier().withReceiveHandledEvents(true).build()
                    .withLegendModifier().withShowCheckBoxes(false).build()
                .build());
    }

    private void addModel(BasePaneModel pricePaneModel) {
        surface.getYAxes().add(pricePaneModel.yAxis);
        surface.getRenderableSeries().addAll(pricePaneModel.renderableSeries);
        surface.getAnnotations().addAll(pricePaneModel.annotations);
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

        protected final void addRenderableSeries(BaseRenderableSeries renderableSeries) {
            renderableSeries.setClipToBounds(true);

            this.renderableSeries.add(renderableSeries);
        }
    }

    private static class PricePaneModel extends BasePaneModel {

        public PricePaneModel(SciChartBuilder builder, PriceSeries prices) {
            super(builder, PRICES, "$0.0000", true);

            // Add the main OHLC chart
            final OhlcDataSeries<Date, Double> stockPrices = builder.newOhlcDataSeries(Date.class, Double.class).withSeriesName("OHLC").build();
            stockPrices.append(prices.getDateData(), prices.getOpenData(), prices.getHighData(), prices.getLowData(), prices.getCloseData());
            addRenderableSeries(builder.newCandlestickSeries().withDataSeries(stockPrices).withYAxisId(PRICES).build());

            final XyDataSeries<Date, Double> maLow = builder.newXyDataSeries(Date.class, Double.class).withSeriesName("Low Line").build();
            maLow.append(prices.getDateData(), MovingAverage.movingAverage(prices.getCloseData(), 50));
            addRenderableSeries(builder.newLineSeries().withDataSeries(maLow).withStrokeStyle(0xFFFF3333, 2f).withYAxisId(PRICES).build());

            final XyDataSeries<Date, Double> maHigh = builder.newXyDataSeries(Date.class, Double.class).withSeriesName("High Line").build();
            maHigh.append(prices.getDateData(), MovingAverage.movingAverage(prices.getCloseData(), 200));
            addRenderableSeries(builder.newLineSeries().withDataSeries(maHigh).withStrokeStyle(0xFF33DD33, 2f).withYAxisId(PRICES).build());

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
            addRenderableSeries(builder.newLineSeries().withDataSeries(rsiSeries).withYAxisId(RSI).build());

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

    private static class RightAlignedOuterVerticallyStackedYAxisLayoutStrategy extends VerticalAxisLayoutStrategy {
        private final HashMap<String, Float> axisSizes = new HashMap<>();

        public RightAlignedOuterVerticallyStackedYAxisLayoutStrategy() {
            axisSizes.put(PRICES, 0.61f);
            axisSizes.put(VOLUME, 0.13f);
            axisSizes.put(MACD, 0.13f);
            axisSizes.put(RSI, 0.13f);
        }

        @Override
        public void measureAxes(int availableWidth, int availableHeight, ChartLayoutState chartLayoutState) {
            for (int i = 0, size = axes.size(); i < size; i++) {
                final IAxis axis = axes.get(i);

                axis.updateAxisMeasurements();

                final AxisLayoutState axisLayoutState = axis.getAxisLayoutState();

                chartLayoutState.rightOuterAreaSize = Math.max(getRequiredAxisSize(axisLayoutState), chartLayoutState.rightOuterAreaSize);
            }
        }

        @Override
        public void layoutAxes(int left, int top, int right, int bottom) {
            final int size = axes.size();

            final int height = bottom - top;

            int topPlacement = top;

            for (int i = 0; i < size; i++) {
                final IAxis axis = axes.get(i);
                final float axisHeightRelative = axisSizes.get(axis.getAxisId());

                final AxisLayoutState axisLayoutState = axis.getAxisLayoutState();

                final int bottomPlacement = Math.round(topPlacement + axisHeightRelative * height);

                axis.layoutArea(left, topPlacement, left + getRequiredAxisSize(axisLayoutState), bottomPlacement);

                topPlacement = bottomPlacement;
            }
        }
    }
}
