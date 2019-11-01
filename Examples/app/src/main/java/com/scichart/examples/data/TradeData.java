//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2017. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// TradeData.java is part of the SCICHART® Examples. Permission is hereby granted
// to modify, create derivative works, distribute and publish any part of this source
// code whether for commercial, private or personal use.
//
// The SCICHART® examples are distributed in the hope that they will be useful, but
// without any warranty. It is provided "AS IS" without warranty of any kind, either
// expressed or implied.
//******************************************************************************

package com.scichart.examples.data;

import java.util.Date;

public class TradeData {
    private Date tradeDate;
    private double tradePrice;
    private double tradeSize;

    public TradeData() {
    }

    public TradeData(Date tradeDate, double tradePrice, double tradeSize) {
        this.tradeDate = tradeDate;
        this.tradePrice = tradePrice;
        this.tradeSize = tradeSize;
    }

    public Date getTradeDate() {
        return tradeDate;
    }

    public void setTradeDate(Date tradeDate) {
        this.tradeDate = tradeDate;
    }

    public double getTradePrice() {
        return tradePrice;
    }

    public void setTradePrice(double tradePrice) {
        this.tradePrice = tradePrice;
    }

    public double getTradeSize() {
        return tradeSize;
    }

    public void setTradeSize(double tradeSize) {
        this.tradeSize = tradeSize;
    }
}
