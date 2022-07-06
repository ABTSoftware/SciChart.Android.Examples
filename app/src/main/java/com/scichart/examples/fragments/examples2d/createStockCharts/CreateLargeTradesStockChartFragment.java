//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2022. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// CreateLargeTradesStockChartFragment.java is part of SCICHART®, High Performance Scientific Charts
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

import android.graphics.Color;
import android.view.animation.DecelerateInterpolator;

import androidx.annotation.NonNull;

import com.scichart.charting.model.dataSeries.IOhlcDataSeries;
import com.scichart.charting.model.dataSeries.IXyzDataSeries;
import com.scichart.charting.numerics.indexDataProvider.DataSeriesIndexDataProvider;
import com.scichart.charting.numerics.indexDataProvider.IIndexDataProvider;
import com.scichart.charting.themes.ThemeManager;
import com.scichart.charting.visuals.SciChartSurface;
import com.scichart.charting.visuals.axes.AutoRange;
import com.scichart.charting.visuals.axes.IAxis;
import com.scichart.charting.visuals.renderableSeries.FastBubbleRenderableSeries;
import com.scichart.charting.visuals.renderableSeries.FastCandlestickRenderableSeries;
import com.scichart.core.framework.UpdateSuspender;
import com.scichart.drawing.common.SolidBrushStyle;
import com.scichart.examples.data.DataManager;
import com.scichart.examples.data.LargeTrade;
import com.scichart.examples.data.LargeTradeBar;
import com.scichart.examples.data.LargeTradeGenerator;
import com.scichart.examples.data.PriceSeries;
import com.scichart.examples.fragments.base.ExampleSingleChartBaseFragment;

import java.util.Collections;
import java.util.Date;
import java.util.List;

public class CreateLargeTradesStockChartFragment extends ExampleSingleChartBaseFragment {

    @Override
    protected void initExample(@NonNull SciChartSurface surface) {
        final IOhlcDataSeries<Date, Double> historicalData = sciChartBuilder.newOhlcDataSeries(Date.class, Double.class).build();
        final IXyzDataSeries<Date, Double, Double> largeSaleTradesData = sciChartBuilder.newXyzDataSeries(Date.class, Double.class, Double.class).withAcceptsUnsortedData().build();
        final IXyzDataSeries<Date, Double, Double> largeBuyTradesData = sciChartBuilder.newXyzDataSeries(Date.class, Double.class, Double.class).withAcceptsUnsortedData().build();

        final LargeTradeGenerator largeTradeGenerator = new LargeTradeGenerator();

        final PriceSeries priceSeries = DataManager.getInstance().getPriceDataIndu(getActivity());
        final List<LargeTradeBar> largeSaleTradesList = largeTradeGenerator.generatePricesSeriesWithLargeTrades(priceSeries);
        final List<LargeTradeBar> largeBuyTradesList = largeTradeGenerator.generatePricesSeriesWithLargeTrades(priceSeries);

        final int size = priceSeries.size();
        final List<Date> dateData = priceSeries.getDateData();

        historicalData.append(dateData, priceSeries.getOpenData(), priceSeries.getHighData(), priceSeries.getLowData(), priceSeries.getCloseData());

        appendLargeTrades(largeBuyTradesData, largeBuyTradesList);
        appendLargeTrades(largeSaleTradesData, largeSaleTradesList);

        final IIndexDataProvider indexDataProvider = new DataSeriesIndexDataProvider(historicalData);

        final IAxis xAxis = sciChartBuilder.newIndexDateAxis()
                .withIndexDataProvider(indexDataProvider)
                .withVisibleRange(dateData.get(size - 30), dateData.get(size - 1))
                .withGrowBy(0, 0.1)
                .build();

        final IAxis yAxis = sciChartBuilder.newNumericAxis().withGrowBy(0d, 0.1d).withAutoRangeMode(AutoRange.Always).build();

        final FastCandlestickRenderableSeries historicalPrices = sciChartBuilder.newCandlestickSeries()
                .withStrokeUp(0xFF00AA00)
                .withFillUpColor(0xAA00AA00)
                .withStrokeDown(0xFFFF0000)
                .withFillDownColor(0xAAFF0000)
                .withDataSeries(historicalData)
                .build();

        final FastBubbleRenderableSeries largeBuyTrades = sciChartBuilder.newBubbleSeries()
                .withDataSeries(largeBuyTradesData)
                .withBubbleBrushStyle(new SolidBrushStyle(0x774248F5))
                .withAutoZRange(false)
                .withStrokeStyle(Color.TRANSPARENT)
                .build();

        final FastBubbleRenderableSeries largeSellTrades = sciChartBuilder.newBubbleSeries()
                .withDataSeries(largeSaleTradesData)
                .withBubbleBrushStyle(new SolidBrushStyle(0x77F542AA))
                .withAutoZRange(false)
                .withStrokeStyle(Color.TRANSPARENT)
                .build();


        UpdateSuspender.using(surface, () -> {
            Collections.addAll(surface.getXAxes(), xAxis);
            Collections.addAll(surface.getYAxes(), yAxis);
            Collections.addAll(surface.getRenderableSeries(), historicalPrices, largeBuyTrades, largeSellTrades);
            Collections.addAll(surface.getChartModifiers(), sciChartBuilder.newModifierGroupWithDefaultModifiers().build());

            sciChartBuilder.newAnimator(historicalPrices).withWaveTransformation().withInterpolator(new DecelerateInterpolator()).withDuration(3000).withStartDelay(350).start();
            sciChartBuilder.newAnimator(largeBuyTrades).withWaveTransformation().withInterpolator(new DecelerateInterpolator()).withDuration(3000).withStartDelay(350).start();
            sciChartBuilder.newAnimator(largeSellTrades).withWaveTransformation().withInterpolator(new DecelerateInterpolator()).withDuration(3000).withStartDelay(350).start();
        });
    }

    private void appendLargeTrades(IXyzDataSeries<Date, Double, Double> largeTradesDS, List<LargeTradeBar> largeTradesList) {
        for (LargeTradeBar bar : largeTradesList) {
            final Date date = bar.getDate();

            for (LargeTrade largeTrade : bar.getLargeTrades()) {
                largeTradesDS.append(date, largeTrade.getPrice(), largeTrade.getVolume());
            }
        }
    }
}
