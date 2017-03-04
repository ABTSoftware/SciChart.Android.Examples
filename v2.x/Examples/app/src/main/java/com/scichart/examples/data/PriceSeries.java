//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2017. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// PriceSeries.java is part of the SCICHART® Examples. Permission is hereby granted
// to modify, create derivative works, distribute and publish any part of this source
// code whether for commercial, private or personal use.
//
// The SCICHART® examples are distributed in the hope that they will be useful, but
// without any warranty. It is provided "AS IS" without warranty of any kind, either
// expressed or implied.
//******************************************************************************

package com.scichart.examples.data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PriceSeries extends ArrayList<PriceBar> {

    public PriceSeries() {
    }

    public PriceSeries(int capacity) {
        super(capacity);
    }

    public List<Date> getDateData() {
        final List<Date> result = new ArrayList<>();
        for (PriceBar priceBar : this) {
            result.add(priceBar.getDate());
        }
        return result;
    }

    public List<Double> getLowData() {
        final List<Double> result = new ArrayList<>();
        for (PriceBar priceBar : this) {
            result.add(priceBar.getLow());
        }
        return result;
    }

    public List<Double> getCloseData() {
        final List<Double> result = new ArrayList<>();
        for (PriceBar priceBar : this) {
            result.add(priceBar.getClose());
        }
        return result;
    }

    public List<Double> getOpenData() {
        final List<Double> result = new ArrayList<>();
        for (PriceBar priceBar : this) {
            result.add(priceBar.getOpen());
        }
        return result;
    }

    public List<Double> getHighData() {
        final List<Double> result = new ArrayList<>();
        for (PriceBar priceBar : this) {
            result.add(priceBar.getHigh());
        }
        return result;
    }

    public List<Long> getVolumeData() {
        final List<Long> result = new ArrayList<>();
        for (PriceBar priceBar : this) {
            result.add(priceBar.getVolume());
        }
        return result;
    }

    public List<Double> getIndexesAsDouble() {
        final List<Double> result = new ArrayList<>();
        for (int i = 0; i < size(); i++) {
            result.add((double) i);
        }
        return result;
    }

    public List<Integer> getIndexes() {
        final List<Integer> result = new ArrayList<>();
        for (int i = 0; i < size(); i++) {
            result.add(i);
        }
        return result;
    }
}