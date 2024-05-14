//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2022. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// VolumeProfileStockChartFragment.java is part of SCICHART®, High Performance Scientific Charts
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
import android.view.View;

import androidx.annotation.NonNull;

import com.scichart.charting.ClipMode;
import com.scichart.charting.Direction2D;
import com.scichart.charting.model.AnnotationCollection;
import com.scichart.charting.model.RenderableSeriesCollection;
import com.scichart.charting.model.dataSeries.IOhlcDataSeries;
import com.scichart.charting.model.dataSeries.IXyDataSeries;
import com.scichart.charting.model.dataSeries.OhlcDataSeries;
import com.scichart.charting.model.dataSeries.XyDataSeries;
import com.scichart.charting.modifiers.AxisDragModifierBase;
import com.scichart.charting.modifiers.SeriesSelectionModifier;
import com.scichart.charting.modifiers.ZoomPanModifier;
import com.scichart.charting.numerics.indexDataProvider.DataSeriesIndexDataProvider;
import com.scichart.charting.numerics.indexDataProvider.IIndexDataProvider;
import com.scichart.charting.visuals.SciChartSurface;
import com.scichart.charting.visuals.axes.AutoRange;
import com.scichart.charting.visuals.axes.AxisAlignment;
import com.scichart.charting.visuals.axes.CategoryDateAxis;
import com.scichart.charting.visuals.axes.IAxis;
import com.scichart.charting.visuals.axes.IAxisCore;
import com.scichart.charting.visuals.axes.NumericAxis;
import com.scichart.charting.visuals.axes.VisibleRangeChangeListener;
import com.scichart.charting.visuals.renderableSeries.BaseRenderableSeries;
import com.scichart.charting.visuals.renderableSeries.FastCandlestickRenderableSeries;
import com.scichart.charting.visuals.renderableSeries.FastColumnRenderableSeries;
import com.scichart.charting.visuals.renderableSeries.FastLineRenderableSeries;
import com.scichart.charting.visuals.renderableSeries.data.XSeriesRenderPassData;
import com.scichart.charting.visuals.renderableSeries.paletteProviders.IFillPaletteProvider;
import com.scichart.charting.visuals.renderableSeries.paletteProviders.IStrokePaletteProvider;
import com.scichart.charting.visuals.renderableSeries.paletteProviders.PaletteProviderBase;
import com.scichart.core.framework.UpdateSuspender;
import com.scichart.core.model.DoubleValues;
import com.scichart.core.model.IntegerValues;
import com.scichart.core.utility.ListUtil;
import com.scichart.data.model.DoubleRange;
import com.scichart.data.model.IRange;
import com.scichart.examples.data.DataManager;
import com.scichart.examples.data.MovingAverage;
import com.scichart.examples.data.PriceSeries;
import com.scichart.examples.fragments.base.ExampleSingleChartBaseFragment;
import com.scichart.extensions.builders.SciChartBuilder;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

public class VolumeProfileStockChartFragment extends ExampleSingleChartBaseFragment {

    private static final String XAXIS_PRICES = "XAXIS_PRICES";
    private static final String YAXIS_PRICES = "YAXIS_PRICES";
    private static final String XAXIS_VOLUME = "XAXIS_Volume";
    private static final String YAXIS_VOLUME = "YAXIS_Volume";

    final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd", Locale.US);

    @Override
    protected void initExample(@NonNull SciChartSurface surface) {

        final PriceSeries priceData = DataManager.getInstance().getPriceDataEurUsd(getActivity());

        final PricePaneModel pricePaneModel = new PricePaneModel(sciChartBuilder, priceData);
        final VolumePaneModel volumePaneModel = new VolumePaneModel(sciChartBuilder, priceData);

        // Price Chart
        surface.getXAxes().add(pricePaneModel.xAxis);
        surface.getYAxes().add(pricePaneModel.yAxis);

        pricePaneModel.yAxis.setVisibleRangeChangeListener(new VisibleRangeChangeListener() {
            @Override
            public void onVisibleRangeChanged(IAxisCore axis, IRange oldRange, IRange newRange, boolean isAnimating) {
                volumePaneModel.visibleRangeChange(sciChartBuilder, priceData, newRange.getMinAsDouble(), newRange.getMaxAsDouble());
            }
        });

        surface.getRenderableSeries().addAll(pricePaneModel.renderableSeries);

        // Volume Chart
        volumePaneModel.xAxis.setAxisAlignment(AxisAlignment.Left);
//        volumePaneModel.xAxis.setVisibility(View.GONE);
        surface.getXAxes().add(volumePaneModel.xAxis);
        volumePaneModel.yAxis.setAxisAlignment(AxisAlignment.Top);
//        volumePaneModel.yAxis.setVisibility(View.GONE);
        surface.getYAxes().add(volumePaneModel.yAxis);

        volumePaneModel.visibleRangeChange(sciChartBuilder, priceData, 0.5, 1.5);
        surface.getRenderableSeries().add(volumePaneModel.renderableSeries);

        // Add modifiers
        surface.getChartModifiers().add(sciChartBuilder
                .newModifierGroup()
                .withXAxisDragModifier().withReceiveHandledEvents(true).withDragMode(AxisDragModifierBase.AxisDragMode.Pan).withClipModeX(ClipMode.StretchAtExtents).build()
                .withPinchZoomModifier().withReceiveHandledEvents(true).withXyDirection(Direction2D.XDirection).build()
                .withZoomPanModifier().withReceiveHandledEvents(true).build()
                .withZoomExtentsModifier().withReceiveHandledEvents(true).build()
                .withLegendModifier().withShowCheckBoxes(false).build()
                .build());

        surface.setAnnotations(pricePaneModel.annotations);
    }


    private static class PricePaneModel {
        public final RenderableSeriesCollection renderableSeries = new RenderableSeriesCollection();
        public final AnnotationCollection annotations = new AnnotationCollection();
        public CategoryDateAxis xAxis;
        public NumericAxis yAxis;

        public PricePaneModel(SciChartBuilder builder, PriceSeries prices) {

            this.xAxis = builder.newCategoryDateAxis()
                    .withAxisId(XAXIS_PRICES)
                    .withGrowBy(0, 0.05)
                    .build();

            this.yAxis = builder.newNumericAxis()
                    .withAxisId(YAXIS_PRICES)
                    .withTextFormatting("$0.0000")
                    .withAutoRangeMode(AutoRange.Always)
                    .withMinorsPerMajor(4)
                    .withMaxAutoTicks(8)
                    .withGrowBy(new DoubleRange(0.05d, 0.05d))
                    .build();


            // Add the main OHLC chart
            final OhlcDataSeries<Date, Double> stockPrices = builder.newOhlcDataSeries(Date.class, Double.class).withSeriesName("EUR/USD").build();
            stockPrices.append(prices.getDateData(), prices.getOpenData(), prices.getHighData(), prices.getLowData(), prices.getCloseData());
            addRenderableSeries(builder
                    .newCandlestickSeries()
                    .withDataSeries(stockPrices)
                    .withXAxisId(XAXIS_PRICES)
                    .withYAxisId(YAXIS_PRICES)
                    .withStrokeUp(0xFF67BDAF).withFillUpColor(0xFF447487)
                    .withStrokeDown(0xFFDC7969).withFillDownColor(0x77DC7969)
                    .build()
            );

            Collections.addAll(annotations, builder.newAxisMarkerAnnotation().withY1(stockPrices.getYValues().get(stockPrices.getCount() - 1)).withBackgroundColor(0xFF67BDAF).withXAxisId(XAXIS_PRICES).withYAxisId(YAXIS_PRICES).build());
        }

        final void addRenderableSeries(BaseRenderableSeries renderableSeries) {
            renderableSeries.setClipToBounds(true);
            this.renderableSeries.add(renderableSeries);
        }
    }

    private static class VolumePaneModel  {

        public final FastColumnRenderableSeries renderableSeries;
        public final AnnotationCollection annotations = new AnnotationCollection();
        public NumericAxis xAxis;
        public NumericAxis yAxis;

        public final DoubleValues xValues = new DoubleValues();
        public final DoubleValues yValues = new DoubleValues();
        public VolumePaneModel(SciChartBuilder builder, PriceSeries prices) {

            this.xAxis = builder.newNumericAxis()
                    .withAxisId(XAXIS_VOLUME)
                    .withAutoRangeMode(AutoRange.Always)
                    .build();

            this.yAxis = builder.newNumericAxis()
                    .withAxisId(YAXIS_VOLUME)
                    .withAutoRangeMode(AutoRange.Always)
                    .withTextFormatting("###E+0")
                    .build();

            renderableSeries = builder
                    .newColumnSeries()
                    .withStrokeStyle(0x30FFFFFF)
                    .withFillColor(0x30FFFFFF)
                    .withXAxisId(XAXIS_VOLUME)
                    .withYAxisId(YAXIS_VOLUME)
//                    .withPaletteProvider(new VolumePaneModel.VolumePaletteProvider(prices))
                    .build();

        }

        final void visibleRangeChange(SciChartBuilder builder, PriceSeries prices, double min, double max) {
            final XyDataSeries<Double, Double> volumePriceSeries = builder.newXyDataSeries(Double.class, Double.class).withSeriesName("Volume").build();

            List<Double> closePrices = prices.getCloseData();
            List<Double> volumeData = ListUtil.select(prices.getVolumeData(), Long::doubleValue);

            calculateVolumeProfile(closePrices, volumeData, min, max);

            volumePriceSeries.setAcceptsUnsortedData(true);
            volumePriceSeries.append(xValues, yValues);

            renderableSeries.setDataSeries(volumePriceSeries);

            Collections.addAll(annotations,
                    builder.newAxisMarkerAnnotation().withY1(volumePriceSeries.getYValues().get(volumePriceSeries.getCount() - 1)).withXAxisId(XAXIS_VOLUME).withYAxisId(YAXIS_VOLUME).build());
        }

        public void calculateVolumeProfile(List<Double> prices, List<Double> volumes, double min, double max) {
            Map<Double, Double> volumeProfile = new HashMap<>();

            int numBins = 50;
            double stepSize = (max - min) / (numBins - 1);

            // Initialize bins
            double[] binRanges = new double[numBins];
            for (int i = 0; i < numBins; i++) {
                binRanges[i] = min + i * stepSize;;
                volumeProfile.put(binRanges[i], 0.0);
            }

            // Iterate over each price level
            for (int i = 0; i < prices.size(); i++) {
                double price = prices.get(i);
                double volume = volumes.get(i);

                // Find the appropriate bin and update volume
                for (int j = 0; j < numBins; j++) {
                    double binStart = binRanges[j];
                    double binEnd = 0;
                    if(j < numBins-1){
                        binEnd = binRanges[j+1];
                    } else {
                        binEnd = 100;
                    }
                    if (price >= binStart && price < binEnd) {
                        volumeProfile.put(binStart, volumeProfile.get(binStart) + volume);
                        break;
                    }
                }
            }

            xValues.clear();
            yValues.clear();
            for (Map.Entry<Double, Double> entry : volumeProfile.entrySet()) {
                xValues.add(entry.getKey());
                yValues.add(entry.getValue());
            }
        }
    }
}
