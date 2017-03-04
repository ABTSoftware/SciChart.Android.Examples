//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2017. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// MarketDataService.java is part of the SCICHART® Examples. Permission is hereby granted
// to modify, create derivative works, distribute and publish any part of this source
// code whether for commercial, private or personal use.
//
// The SCICHART® examples are distributed in the hope that they will be useful, but
// without any warranty. It is provided "AS IS" without warranty of any kind, either
// expressed or implied.
//******************************************************************************

package com.scichart.examples.data;

import com.scichart.core.common.Action1;

import java.util.Date;

public class MarketDataService implements IMarketDataService {

    public interface INewDataObserver {
        void onNewData(PriceBar data);
    }

    public interface IUpdateDataObserver {
        void onUpdateData(PriceBar data);
    }

    private final RandomPricesDataSource generator;

    public MarketDataService(Date startDate, int timeFrameMinutes, int tickTimerIntervals) {
        this.generator = new RandomPricesDataSource(timeFrameMinutes, true, tickTimerIntervals, 25, 367367, 30, startDate);
    }

    @Override
    public void subscribePriceUpdate(final Action1<PriceBar> callback) {
        if (!generator.isRunning()) {
            generator.newDataObserver = new INewDataObserver() {
                @Override
                public void onNewData(PriceBar data) {
                    callback.execute(data);
                }
            };
            generator.updateDataObserver = new IUpdateDataObserver() {
                @Override
                public void onUpdateData(PriceBar data) {
                    callback.execute(data);
                }
            };

            generator.startGeneratePriceBars();
        }
    }

    @Override
    public void clearSubscriptions() {
        if (generator.isRunning()) {
            generator.stopGeneratePriceBars();
            generator.clearObservers();
        }
    }

    @Override
    public PriceSeries getHistoricalData(int numberBars) {
        PriceSeries prices = new PriceSeries(numberBars);
        for (int i = 0; i < numberBars; i++) {
            prices.add(generator.getNextData());
        }

        return prices;
    }

    public void stopGenerator() {
        clearSubscriptions();
        generator.cancelScheduler();
    }
}
