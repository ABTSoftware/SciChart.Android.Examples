//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2017. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// MovingAverage.java is part of the SCICHART® Examples. Permission is hereby granted
// to modify, create derivative works, distribute and publish any part of this source
// code whether for commercial, private or personal use.
//
// The SCICHART® examples are distributed in the hope that they will be useful, but
// without any warranty. It is provided "AS IS" without warranty of any kind, either
// expressed or implied.
//******************************************************************************

package com.scichart.examples.data;

import java.util.ArrayList;
import java.util.List;

public class MovingAverage {

    private final int length;
    private int circIndex = -1;
    private boolean filled;
    private double current = Double.NaN;
    private final double oneOverLength;
    private final double[] circularBuffer;
    private double total;

    public MovingAverage(int length) {
        this.length = length;
        this.oneOverLength = 1.0 / length;
        this.circularBuffer = new double[length];
    }

    public MovingAverage update(double value) {
        double lostValue = circularBuffer[circIndex];
        circularBuffer[circIndex] = value;

        // Maintain totals for Push function
        total += value;
        total -= lostValue;

        // If not yet filled, just return. Current value should be double.NaN
        if (!filled) {
            current = Double.NaN;
            return this;
        }

        // Compute the average
        double average = 0.0;
        for (double aCircularBuffer : circularBuffer) {
            average += aCircularBuffer;
        }

        current = average * oneOverLength;

        return this;
    }

    public MovingAverage push(double value) {
        // Apply the circular buffer
        if (++circIndex == length) {
            circIndex = 0;
        }

        double lostValue = circularBuffer[circIndex];
        circularBuffer[circIndex] = value;

        // Compute the average
        total += value;
        total -= lostValue;

        // If not yet filled, just return. Current value should be double.NaN
        if (!filled && circIndex != length - 1) {
            current = Double.NaN;
            return this;
        } else {
            // Set a flag to indicate this is the first time the buffer has been filled
            filled = true;
        }

        current = total * oneOverLength;

        return this;
    }

    public int getLength() {
        return length;
    }

    public double getCurrent() {
        return current;
    }

    public static List<Double> movingAverage(List<Double> input, int period) {
        final MovingAverage ma = new MovingAverage(period);

        final ArrayList<Double> output = new ArrayList<>(input.size());

        for (Double item : input) {
            ma.push(item);
            output.add(ma.getCurrent());
        }

        return output;
    }

    public static List<Double> rsi(List<PriceBar> input, int period) {
        final MovingAverage averageGain = new MovingAverage(period);
        final MovingAverage averageLoss = new MovingAverage(period);

        final ArrayList<Double> output = new ArrayList<>(input.size());

        // skip first point
        PriceBar previousBar = input.get(0);
        output.add(Double.NaN);

        for (int i = 1, size = input.size(); i < size; i++) {
            final PriceBar priceBar = input.get(i);

            final double gain = priceBar.getClose() > previousBar.getClose() ? priceBar.getClose() - previousBar.getClose() : 0.0;
            final double loss = previousBar.getClose() > priceBar.getClose() ? previousBar.getClose() - priceBar.getClose() : 0.0;

            averageGain.push(gain);
            averageLoss.push(loss);

            final double relativeStrength = Double.isNaN(averageGain.getCurrent()) || Double.isNaN(averageLoss.getCurrent()) ? Double.NaN : averageGain.getCurrent() / averageLoss.getCurrent();

            output.add(Double.isNaN(relativeStrength) ? Double.NaN : 100.0 - (100.0 / (1.0 + relativeStrength)));

            previousBar = priceBar;
        }

        return output;
    }

    public static MacdPoints macd(List<Double> input, int slow, int fast, int signal) {
        final MovingAverage maSlow = new MovingAverage(slow);
        final MovingAverage maFast = new MovingAverage(fast);
        final MovingAverage maSignal = new MovingAverage(signal);

        final MacdPoints output = new MacdPoints();

        for (Double item : input) {
            final double macd = maSlow.push(item).getCurrent() - maFast.push(item).getCurrent();
            final double signalLine = Double.isNaN(macd) ? Double.NaN : maSignal.push(macd).getCurrent();
            final double divergence = Double.isNaN(macd) || Double.isNaN(signalLine) ? Double.NaN : macd - signalLine;

            output.addPoint(macd, signalLine, divergence);
        }

        return output;
    }

    public static final class MacdPoints {
        public final List<Double> macdValues = new ArrayList<>();
        public final List<Double> signalValues = new ArrayList<>();
        public final List<Double> divergenceValues = new ArrayList<>();

        public void addPoint(double macd, double signal, double divergence) {
            macdValues.add(macd);
            signalValues.add(signal);
            divergenceValues.add(divergence);
        }
    }
}
