//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2023. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// RandomStockPriceGenerator.java is part of SCICHART®, High Performance Scientific Charts
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

package com.scichart.examples.data;

import java.util.Date;
import java.util.Random;

public class RandomStockPriceGenerator {

    private final Random random;
    private double last;
    private Date lastDate = new Date(1673511400000L);

    public RandomStockPriceGenerator(){
        random = new Random();
        last = random.nextInt(100);
    }

    public RandomStockPriceGenerator(int seed) {
        random = new Random(seed);
        last = random.nextInt(100);
    }

    public void reset() {
        last = 0;
        lastDate = new Date(1673511400000L);
    }

    public PriceSeries getRandomPriceSeries(int count) {
        final PriceSeries result = new PriceSeries(count);
        // Generate a slightly positive biased random walk
        // y[i] = y[i-1] + random,
        // where random is in the range -0.5, +0.5
        for(int i = 0; i < count; i++) {
            double next = Math.abs(last + (random.nextInt(100) - 50));
            last = next;
            Date nextDate = new Date(lastDate.getTime() + (random.nextInt(100) * 10000L));
            lastDate = nextDate;
            result.add(
                    new PriceBar(
                            nextDate,
                            0,0,0,next,0
                    )
            );

        }

        return result;
    }
}
