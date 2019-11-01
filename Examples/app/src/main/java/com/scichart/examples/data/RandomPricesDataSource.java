//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2017. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// RandomPricesDataSource.java is part of the SCICHART® Examples. Permission is hereby granted
// to modify, create derivative works, distribute and publish any part of this source
// code whether for commercial, private or personal use.
//
// The SCICHART® examples are distributed in the hope that they will be useful, but
// without any warranty. It is provided "AS IS" without warranty of any kind, either
// expressed or implied.
//******************************************************************************

package com.scichart.examples.data;

import com.scichart.core.utility.DateIntervalUtil;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Random;
import java.util.TimeZone;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class RandomPricesDataSource {

    private static final int MARKET_CLOSE_HOUR = 16;
    private static final int MARKET_CLOSE_MINUTES = 30;

    private static final int MARKET_OPEN_HOUR = 8;
    private static final int MARKET_OPEN_MINUTE = 0;

    private final class PriceBarInfo {
        public Date date;
        public double close;

        public PriceBarInfo(Date date, double close) {
            this.date = date;
            this.close = close;
        }
    }

    private final Random random;
    private final int candleIntervalMinutes;
    private final boolean simulateDateGap;
    private PriceBar lastPriceBar;
    private final PriceBarInfo initialPriceBar;
    private double currentTime;
    private final int updatesPerPrice;
    private int currentUpdateCount;

    private ScheduledFuture<?> schedule;
    private final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
    public MarketDataService.INewDataObserver newDataObserver;
    public MarketDataService.IUpdateDataObserver updateDataObserver;

    private volatile boolean isRunning = false;

    private final Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"), Locale.getDefault());

    public RandomPricesDataSource(int candleIntervalMinutes, boolean simulateDateGap, long timerInterval, final int updatesPerPrice, int randomSeed, double startingPrice, Date startDate) {
        this.candleIntervalMinutes = candleIntervalMinutes;
        this.simulateDateGap = simulateDateGap;
        this.updatesPerPrice = updatesPerPrice;

        schedule = scheduledExecutorService.scheduleWithFixedDelay(new Runnable() {
            @Override
            public synchronized void run() {
                if (isRunning) {
                    if (currentUpdateCount < RandomPricesDataSource.this.updatesPerPrice) {
                        currentUpdateCount++;
                        PriceBar updatedData = GetUpdatedData();
                        if (updateDataObserver != null) {
                            updateDataObserver.onUpdateData(updatedData);
                        }
                    } else {
                        currentUpdateCount = 0;
                        PriceBar nextData = getNextData();
                        if (newDataObserver != null) {
                            newDataObserver.onNewData(nextData);
                        }
                    }
                }
            }
        }, 0, timerInterval, TimeUnit.MILLISECONDS);

        this.initialPriceBar = new PriceBarInfo(startDate, startingPrice);
        this.lastPriceBar = new PriceBar(
                this.initialPriceBar.date,
                this.initialPriceBar.close,
                this.initialPriceBar.close,
                this.initialPriceBar.close,
                this.initialPriceBar.close,
                0L);

        this.random = new Random(randomSeed);
    }

    public boolean isRunning() {
        return isRunning;
    }

    public void startGeneratePriceBars() {
        isRunning = true;
    }

    public void stopGeneratePriceBars() {
        isRunning = false;
    }

    public PriceBar getNextData() {
        return getNextRandomPriceBar();
    }

    private PriceBar getNextRandomPriceBar() {
        double close = lastPriceBar.getClose();
        double num = (random.nextDouble() - 0.9) * initialPriceBar.close / 30.0;
        double num2 = random.nextDouble();
        double num3 = initialPriceBar.close + initialPriceBar.close / 2.0 * Math.sin(7.27220521664304E-06 * currentTime) + initialPriceBar.close / 16.0 * Math.cos(7.27220521664304E-05 * currentTime) + initialPriceBar.close / 32.0 * Math.sin(7.27220521664304E-05 * (10.0 + num2) * currentTime) + initialPriceBar.close / 64.0 * Math.cos(7.27220521664304E-05 * (20.0 + num2) * currentTime) + num;
        double num4 = Math.max(close, num3);
        double num5 = random.nextDouble() * initialPriceBar.close / 100.0;
        double high = num4 + num5;
        double num6 = Math.min(close, num3);
        double num7 = random.nextDouble() * initialPriceBar.close / 100.0;
        double low = num6 - num7;
        long volume = (long) (random.nextDouble() * 30000 + 20000);
        Date openTime = simulateDateGap ? emulateDateGap(lastPriceBar.getDate()) : lastPriceBar.getDate();
        Date closeTime = new Date(openTime.getTime() + DateIntervalUtil.fromMinutes(candleIntervalMinutes));

        lastPriceBar = new PriceBar(closeTime, close, high, low, num3, volume);

        currentTime += candleIntervalMinutes * 60;

        return lastPriceBar;
    }

    private Date emulateDateGap(Date candleOpenTime) {
        calendar.clear();
        calendar.setTime(candleOpenTime);

        if (calendar.get(Calendar.HOUR_OF_DAY) > MARKET_CLOSE_HOUR || (calendar.get(Calendar.HOUR_OF_DAY) == MARKET_CLOSE_HOUR && calendar.get(Calendar.MINUTE) >= MARKET_CLOSE_MINUTES)) {
            calendar.set(Calendar.HOUR_OF_DAY, MARKET_OPEN_HOUR);
            calendar.set(Calendar.MINUTE, MARKET_OPEN_MINUTE);

            calendar.add(Calendar.DAY_OF_YEAR, 1);
            while (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY || calendar.get(Calendar.DAY_OF_YEAR) == Calendar.SUNDAY) {
                calendar.add(Calendar.DAY_OF_YEAR, 1);
            }
        }

        return calendar.getTime();
    }

    private PriceBar GetUpdatedData() {
        double num = lastPriceBar.getClose() + ((random.nextDouble() - 0.48) * (lastPriceBar.getClose() / 100.0));
        double high = (num > lastPriceBar.getHigh()) ? num : lastPriceBar.getHigh();
        double low = (num < lastPriceBar.getLow()) ? num : lastPriceBar.getLow();
        long volumeInc = (long) ((random.nextDouble() * 30000 + 20000) * 0.05);
        lastPriceBar = new PriceBar(lastPriceBar.getDate(), lastPriceBar.getOpen(), high, low, num, lastPriceBar.getVolume() + volumeInc);

        return lastPriceBar;
    }

    public void clearObservers() {
        newDataObserver = null;
        updateDataObserver = null;
    }

    public void cancelScheduler() {
        if (schedule != null) {
            schedule.cancel(true);
        }
    }
}
