//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2017. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// DataManager.java is part of the SCICHART® Examples. Permission is hereby granted
// to modify, create derivative works, distribute and publish any part of this source
// code whether for commercial, private or personal use.
//
// The SCICHART® examples are distributed in the hope that they will be useful, but
// without any warranty. It is provided "AS IS" without warranty of any kind, either
// expressed or implied.
//******************************************************************************

package com.scichart.examples.data;

import android.content.Context;
import android.util.Log;

import androidx.annotation.ColorInt;
import androidx.annotation.FloatRange;
import androidx.annotation.NonNull;

import com.scichart.core.model.DoubleValues;
import com.scichart.data.model.SciListUtil;
import com.scichart.data.numerics.SearchMode;
import com.scichart.drawing.utility.ColorUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class DataManager {
    private final Random random = new Random();
    private static final DataManager INSTANCE = new DataManager();

    private static final String TRADETICKS_DATA_PATH = "data/TradeTicks.csv";
    private static final String WAVEFORM_DATA_PATH = "data/waveform.txt";
    private static final String FFT_DATA_PATH = "data/FourierTransform.txt";
    private static final String PRICE_INDU_DAILY_DATA_PATH = "data/INDU_Daily.csv";
    private static final String PRICE_EURUSD_DAILY_DATA_PATH = "data/EURUSD_Daily.csv";
    private static final String LIDAR_DATA_PATH = "data/LIDAR-DSM-2M-TQ38sw/tq3080_DSM_2M.asc";

    private DataManager() {
    }

    public static DataManager getInstance() {
        return INSTANCE;
    }

    @ColorInt
    public int getRandomColor() {
        final int red = random.nextInt(205) + 50;
        final int green = random.nextInt(205) + 50;
        final int blue = random.nextInt(205) + 50;

        return ColorUtil.argb(0xFF, red, green, blue);
    }

    @FloatRange(from = 0f, to = 1f)
    public float getRandomFloat() {
        return random.nextFloat();
    }

    @FloatRange(from = 0d, to = 1d)
    public double getRandomDouble() {
        return random.nextDouble();
    }

    public boolean getRandomBoolean() {
        return random.nextInt(2) == 0;
    }

    public float getRandomScale() {
        return (getRandomFloat() + 0.5f) * 3f;
    }

    public double getGaussianRandomNumber(double mean, double stdDev) {
        //these are uniform(0,1) random doubles
        final double u1 = random.nextDouble();
        final double u2 = random.nextDouble();

        //random normal(0,1)
        final double randStdNormal = Math.sqrt(-2.0 * Math.log(u1)) * Math.sin(2.0 * Math.PI * u2);

        //random normal(mean,stdDev^2)
        return mean * stdDev * randStdNormal;
    }

    public void setRandomDoubleSeries(DoubleValues xValues, DoubleValues yValues, int pointCount) {
        final double[] xValuesArray = getValuesArray(xValues, pointCount);
        final double[] yValuesArray = getValuesArray(yValues, pointCount);

        final double amplitude = random.nextDouble() + 0.5;
        final double freq = Math.PI * (random.nextDouble() + 0.5) * 10;
        final double offset = random.nextDouble() - 0.5;
        for (int i = 0; i < pointCount; i++) {
            xValuesArray[i] = i;
            yValuesArray[i] = offset + amplitude * Math.sin(freq * i);
        }
    }

    public DoubleSeries getRandomDoubleSeries(int pointCount) {
        final DoubleSeries doubleSeries = new DoubleSeries(pointCount);

        setRandomDoubleSeries(doubleSeries.xValues, doubleSeries.yValues, pointCount);

        return doubleSeries;
    }

    public void setStraightLines(DoubleValues xValues, DoubleValues yValues, double gradient, double yIntercept, int pointCount) {
        final double[] xValuesArray = getValuesArray(xValues, pointCount);
        final double[] yValuesArray = getValuesArray(yValues, pointCount);

        for (int i = 0; i < pointCount; i++) {
            final double x = i + 1;
            xValuesArray[i] = x;
            yValuesArray[i] = gradient * x + yIntercept;
        }
    }

    public DoubleSeries getStraightLine(double gradient, double yIntercept, int pointCount) {
        final DoubleSeries doubleSeries = new DoubleSeries(pointCount);

        setStraightLines(doubleSeries.xValues, doubleSeries.yValues, gradient, yIntercept, pointCount);

        return doubleSeries;
    }

    public DoubleSeries getExponentialCurve(double exponent, int pointCount) {
        final DoubleSeries doubleSeries = new DoubleSeries(pointCount);

        double x = 0.00001;
        double y;

        final double fudgeFactor = 1.4;
        for (int i = 0; i < pointCount; i++) {
            x *= fudgeFactor;
            y = Math.pow((double) i + 1, exponent);

            doubleSeries.add(x, y);
        }

        return doubleSeries;
    }

    public void setFourierSeries(DoubleValues xValues, DoubleValues yValues, double amplitude, double phaseShift, int count) {
        final double[] xValuesArray = getValuesArray(xValues, count);
        final double[] yValuesArray = getValuesArray(yValues, count);

        for (int i = 0; i < count; i++) {
            double time = 10 * i / (double) count;
            double wn = 2 * Math.PI / (count / 10);
            double y = Math.PI * amplitude *
                    (Math.sin(i * wn + phaseShift) +
                            0.33 * Math.sin(i * 3 * wn + phaseShift) +
                            0.20 * Math.sin(i * 5 * wn + phaseShift) +
                            0.14 * Math.sin(i * 7 * wn + phaseShift) +
                            0.11 * Math.sin(i * 9 * wn + phaseShift) +
                            0.09 * Math.sin(i * 11 * wn + phaseShift));

            xValuesArray[i] = time;
            yValuesArray[i] = y;
        }
    }

    public DoubleSeries getFourierSeries(double amplitude, double phaseShift, int count) {
        final DoubleSeries doubleSeries = new DoubleSeries(count);

        setFourierSeries(doubleSeries.xValues, doubleSeries.yValues, amplitude, phaseShift, count);

        return doubleSeries;
    }

    public void setFourierSeriesZoomed(DoubleValues xValues, DoubleValues yValues, double amplitude, double phaseShift, double xStart, double xEnd, int count) {
        setFourierSeries(xValues, yValues, amplitude, phaseShift, count);

        int startIndex = SciListUtil.instance().findIndex(xValues.getItemsArray(), 0, count, true, xStart, SearchMode.RoundDown);
        int endIndex = SciListUtil.instance().findIndex(xValues.getItemsArray(), startIndex, count - startIndex, true, xEnd, SearchMode.RoundUp);

        int size = endIndex - startIndex;
        System.arraycopy(xValues.getItemsArray(), startIndex, xValues.getItemsArray(), 0, size);
        System.arraycopy(yValues.getItemsArray(), startIndex, yValues.getItemsArray(), 0, size);

        xValues.setSize(size);
        yValues.setSize(size);
    }

    public DoubleSeries getFourierSeries(double amplitude, double phaseShift, double xStart, double xEnd, int count) {
        final DoubleSeries doubleSeries = new DoubleSeries(count);

        setFourierSeriesZoomed(doubleSeries.xValues, doubleSeries.yValues, amplitude, phaseShift, xStart, xEnd, count);

        return doubleSeries;
    }

    public void setLissajousCurve(DoubleValues xValues, DoubleValues yValues, double alpha, double beta, double delta, int count) {
        final double[] xValuesArray = getValuesArray(xValues, count);
        final double[] yValuesArray = getValuesArray(yValues, count);

        // From http://en.wikipedia.org/wiki/Lissajous_curve
        // x = Asin(at + d), y = Bsin(bt)
        for (int i = 0; i < count; i++) {
            xValuesArray[i] = Math.sin(alpha * i * 0.1 + delta);
            yValuesArray[i] = Math.sin(beta * i * 0.1);
        }
    }

    public DoubleSeries getLissajousCurve(double alpha, double beta, double delta, int count) {
        final DoubleSeries doubleSeries = new DoubleSeries(count);

        setLissajousCurve(doubleSeries.xValues, doubleSeries.yValues, alpha, beta, delta, count);

        return doubleSeries;
    }

    public DoubleSeries getDampedSinewave(double amplitude, double dampingFactor, int pointCount, int freq) {
        return getDampedSinewave(0, amplitude, 0.0, dampingFactor, pointCount, freq);
    }

    public DoubleSeries getDampedSinewave(int pad, double amplitude, double phase, double dampingFactor, int pointCount, int freq) {
        final DoubleSeries doubleSeries = new DoubleSeries(pointCount);

        for (int i = 0; i < pad; i++) {
            double time = 10 * i / (double) pointCount;
            doubleSeries.add(time, 0d);
        }

        for (int i = pad, j = 0; i < pointCount; i++, j++) {
            double time = 10 * i / (double) pointCount;
            double wn = 2 * Math.PI / (pointCount / (double) freq);

            final double d = amplitude * Math.sin(j * wn + phase);
            doubleSeries.add(time, d);

            amplitude *= (1.0 - dampingFactor);
        }

        return doubleSeries;
    }

    public DoubleSeries getSinewave(double amplitude, double phase, int pointCount, int freq) {
        return getDampedSinewave(0, amplitude, phase, 0, pointCount, freq);
    }

    public DoubleSeries getSinewave(double amplitude, double phase, int pointCount) {
        return getSinewave(amplitude, phase, pointCount, 10);
    }

    public DoubleSeries getNoisySinewave(double amplitude, double phase, int pointCount, double noiseAmplitude) {
        final DoubleSeries sinewave = getSinewave(amplitude, phase, pointCount);
        final DoubleValues yValues = sinewave.yValues;

        for (int i = 0; i < pointCount; i++) {
            final double y = yValues.get(i);
            yValues.set(i, y + random.nextDouble() * noiseAmplitude - noiseAmplitude * 0.5);
        }

        return sinewave;
    }

    public List<Double> offset(final Iterable<Double> inputIterable, final double offset) {
        final List<Double> result = new ArrayList<>();
        for (Double value : inputIterable) {
            result.add(value + offset);
        }
        return result;
    }

    public List<Double> computeMovingAverage(List<Double> prices, int length) {
        final List<Double> result = new ArrayList<>();
        for (int i = 0; i < prices.size(); i++) {
            if (i < length) {
                result.add(Double.NaN);
                continue;
            }
            result.add(averageOf(prices, i - length, i));
        }
        return result;
    }

    private double averageOf(List<Double> prices, int from, int to) {
        double result = 0;
        for (int i = from; i < to; i++) {
            result += prices.get(i);
        }
        return result / (to - from);
    }

    public PriceSeries getPriceDataIndu(Context context) {
        return getPriceBarsFromPath(context, PRICE_INDU_DAILY_DATA_PATH, "MM/dd/yyyy");
    }

    public PriceSeries getPriceDataEurUsd(Context context) {
        return getPriceBarsFromPath(context, PRICE_EURUSD_DAILY_DATA_PATH, "yyyy.MM.dd");
    }

    @NonNull
    private static PriceSeries getPriceBarsFromPath(Context context, String path, String dateFormatString) {
        BufferedReader reader = null;
        final PriceSeries result = new PriceSeries();
        try {
            reader = new BufferedReader(new InputStreamReader(context.getAssets().open(path)));

            String line;
            final SimpleDateFormat dateFormat = new SimpleDateFormat(dateFormatString, Locale.US);
            while ((line = reader.readLine()) != null) {
                final String[] split = line.split(",");
                Date parse = dateFormat.parse(split[0]);
                final PriceBar priceBar = new PriceBar(
                        parse,
                        Double.parseDouble(split[1]),
                        Double.parseDouble(split[2]),
                        Double.parseDouble(split[3]),
                        Double.parseDouble(split[4]),
                        Long.parseLong(split[5]));
                result.add(priceBar);
            }
        } catch (IOException | ParseException ex) {
            Log.e("DataManager", "get price data from " + path, ex);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
        return result;
    }

    private static double[] getValuesArray(DoubleValues values, int count) {
        values.clear();

        values.setSize(count);

        return values.getItemsArray();
    }

    public DoubleSeries getButterflyCurve(int count) {
        // From http://en.wikipedia.org/wiki/Butterfly_curve_%28transcendental%29
        // x = sin(t) * (e^cos(t) - 2cos(4t) - sin^5(t/12))
        // y = cos(t) * (e^cos(t) - 2cos(4t) - sin^5(t/12))
        double temp = 0.01;
        final DoubleSeries doubleSeries = new DoubleSeries(count);
        for (int i = 0; i < count; i++) {
            final double t = i * temp;

            final double multiplier = Math.pow(Math.E, Math.cos(t)) - 2 * Math.cos(4 * t) - Math.pow(Math.sin(t / 12), 5);

            final double x = Math.sin(t) * multiplier;
            final double y = Math.cos(t) * multiplier;
            doubleSeries.add(x, y);
        }
        return doubleSeries;
    }

    public double[] loadWaveformData(Context context) {
        BufferedReader reader = null;
        final List<Double> result = new ArrayList<>();
        try {
            reader = new BufferedReader(new InputStreamReader(context.getAssets().open(WAVEFORM_DATA_PATH)));

            String line = reader.readLine();
            while (line != null) {
                result.add(Double.parseDouble(line));
                line = reader.readLine();
            }
        } catch (IOException ex) {
            Log.e("DataManager", "loadWaveformData", ex);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
        final double[] resultList = new double[result.size()];
        for (int i = 0; i < result.size(); i++) {
            resultList[i] = result.get(i);
        }
        return resultList;
    }

    public List<TradeData> getTradeTicks(Context context) {
        BufferedReader reader = null;
        final List<TradeData> result = new ArrayList<>();
        try {
            reader = new BufferedReader(new InputStreamReader(context.getAssets().open(TRADETICKS_DATA_PATH)));

            final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss", Locale.US);
            String line = reader.readLine();
            while (line != null) {

                String[] tokens = line.split(",");
                TradeData data = new TradeData(dateFormat.parse(tokens[0]), Double.parseDouble(tokens[1]), Double.parseDouble(tokens[2]));

                result.add(data);
                line = reader.readLine();
            }
        } catch (IOException | ParseException ex) {
            Log.e("DataManager", "getTradeTicks", ex);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }

        return result;
    }

    public List<DoubleValues> loadFFT(Context context) {
        BufferedReader reader = null;
        final List<DoubleValues> result = new ArrayList<>();
        try {
            reader = new BufferedReader(new InputStreamReader(context.getAssets().open(FFT_DATA_PATH)));

            String line = reader.readLine();
            while (line != null) {
                final DoubleValues fft = new DoubleValues();
                final String[] tokens = line.split(",");

                for (int i = 0; i < tokens.length; i++) {
                    fft.add(Double.parseDouble(tokens[i]));
                }

                result.add(fft);
                line = reader.readLine();
            }
        } catch (IOException ex) {
            Log.e("DataManager", "loadFFT", ex);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }

        return result;
    }

    public AscData getLidarData(Context context) {
        BufferedReader reader = null;
        final List<DoubleValues> result = new ArrayList<>();
        try {
            reader = new BufferedReader(new InputStreamReader(context.getAssets().open(LIDAR_DATA_PATH)));

            return new AscData(reader);
        } catch (IOException ex) {
            Log.e("DataManager", "loadFFT", ex);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }

        return null;
    }
}
