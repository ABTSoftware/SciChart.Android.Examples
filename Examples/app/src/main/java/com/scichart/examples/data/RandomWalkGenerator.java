//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2017. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// RandomWalkGenerator.java is part of the SCICHART® Examples. Permission is hereby granted
// to modify, create derivative works, distribute and publish any part of this source
// code whether for commercial, private or personal use.
//
// The SCICHART® examples are distributed in the hope that they will be useful, but
// without any warranty. It is provided "AS IS" without warranty of any kind, either
// expressed or implied.
//******************************************************************************

package com.scichart.examples.data;

import java.util.Random;

public class RandomWalkGenerator {
    private final Random random;
    private double last;
    private int index;
    private double bias = 0.01;

    public RandomWalkGenerator(){
        random = new Random();
    }

    public RandomWalkGenerator(int seed) {
        random = new Random(seed);
    }

    public void reset() {
        index = 0;
        last = 0;
    }

    public RandomWalkGenerator setBias(double bias){
        this.bias = bias;
        return this;
    }

    public DoubleSeries getRandomWalkSeries(int count) {
        final DoubleSeries result = new DoubleSeries(count);

        // Generate a slightly positive biased random walk
        // y[i] = y[i-1] + random,
        // where random is in the range -0.5, +0.5
        for(int i = 0; i < count; i++) {
            double next = last + (random.nextDouble() - 0.5 + bias);
            last = next;
            result.add((double)index++, next);
        }

        return result;
    }
}
