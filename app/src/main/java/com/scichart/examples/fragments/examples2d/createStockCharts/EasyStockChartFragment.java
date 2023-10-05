//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2022. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// MovingAverageTradesStockChartFragment.java is part of SCICHART®, High Performance Scientific Charts
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

import androidx.annotation.NonNull;

import com.scichart.charting.model.dataSeries.IOhlcDataSeries;
import com.scichart.charting.model.dataSeries.IXyDataSeries;
import com.scichart.charting.numerics.indexDataProvider.DataSeriesIndexDataProvider;
import com.scichart.charting.numerics.indexDataProvider.IIndexDataProvider;
import com.scichart.charting.visuals.SciChartSurface;
import com.scichart.charting.visuals.axes.AutoRange;
import com.scichart.charting.visuals.axes.IAxis;
import com.scichart.charting.visuals.renderableSeries.FastCandlestickRenderableSeries;
import com.scichart.charting.visuals.renderableSeries.FastLineRenderableSeries;
import com.scichart.core.framework.UpdateSuspender;
import com.scichart.examples.data.DataManager;
import com.scichart.examples.data.PriceSeries;
import com.scichart.examples.fragments.base.ExampleSingleChartBaseFragment;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class EasyStockChartFragment extends ExampleSingleChartBaseFragment {

    final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd", Locale.US);

    @Override
    protected void initExample(@NonNull SciChartSurface surface) {
        final IOhlcDataSeries<Date, Double> historicalData = sciChartBuilder.newOhlcDataSeries(Date.class, Double.class).build();
        final IXyDataSeries<Date, Double> movingAverageData = sciChartBuilder.newXyDataSeries(Date.class, Double.class).build();
        final IXyDataSeries<Date, Double> localMinMaxData = sciChartBuilder.newXyDataSeries(Date.class, Double.class).build();

        final PriceSeries priceSeries = DataManager.getInstance().getPriceAAPL(requireContext());
        final PriceSeries movingAverages = DataManager.getInstance().computeMovingAverageInPriceSeries(priceSeries, 14);

        final int size = priceSeries.size();
        final List<Date> dateData = priceSeries.getDateData();

        historicalData.append(dateData, priceSeries.getOpenData(), priceSeries.getHighData(), priceSeries.getLowData(), priceSeries.getCloseData());
        movingAverageData.append(movingAverages.getDateData(), movingAverages.getCloseData());

        // append local min and max values
        try {
            localMinMaxData.append(dateFormat.parse("2023.01.03"), 124.17);
            localMinMaxData.append(dateFormat.parse("2023.02.03"), 157.38);
            localMinMaxData.append(dateFormat.parse("2023.03.02"), 143.90);
            localMinMaxData.append(dateFormat.parse("2023.03.06"), 156.30);
            localMinMaxData.append(dateFormat.parse("2023.03.13"), 147.70);
            localMinMaxData.append(dateFormat.parse("2023.03.22"), 162.14);
        } catch (Exception e){
            e.printStackTrace();
        }

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

        final FastLineRenderableSeries movingAverageSeries = sciChartBuilder.newLineSeries()
                .withStrokeStyle(0xFFF48420, 1f, true)
                .withDataSeries(movingAverageData)
                .build();

        final FastLineRenderableSeries localMinMaxSeries = sciChartBuilder.newLineSeries()
                .withStrokeStyle(0xFF50C7E0, 1f, true)
                .withDataSeries(localMinMaxData)
                .build();

        UpdateSuspender.using(surface, () -> {
            Collections.addAll(surface.getXAxes(), xAxis);
            Collections.addAll(surface.getYAxes(), yAxis);
            Collections.addAll(surface.getRenderableSeries(), historicalPrices, movingAverageSeries, localMinMaxSeries);
            Collections.addAll(surface.getChartModifiers(), sciChartBuilder.newModifierGroupWithDefaultModifiers().build());

        });
    }
}
