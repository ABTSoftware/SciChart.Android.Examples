//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2020. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// StubAudioAnalyzerDataProvider.java is part of the SCICHART® Examples. Permission is hereby granted
// to modify, create derivative works, distribute and publish any part of this source
// code whether for commercial, private or personal use.
//
// The SCICHART® examples are distributed in the hope that they will be useful, but
// without any warranty. It is provided "AS IS" without warranty of any kind, either
// expressed or implied.
//******************************************************************************

package com.scichart.examples.fragments.showcase.audioAnalyzer;

import com.scichart.examples.fragments.base.DataProviderBase;

import java.util.Random;
import java.util.concurrent.TimeUnit;

public class StubAudioAnalyzerDataProvider extends DataProviderBase<AudioData> implements IAudioAnalyzerDataProvider {
    private final int bufferSizeInShorts = 2048;
    private final AudioData audioData = new AudioData(bufferSizeInShorts);

    private final IYValuesProvider provider = new AggregateYValueProvider(
      new FrequencySinewaveYValueProvider(8000, 0, 0, 1, 0.0000005),
      new NoisySinewaveYValueProvider(8000, 0, 0.000032, 200),
      new NoisySinewaveYValueProvider(6000, 0, 0.000016, 100),
      new NoisySinewaveYValueProvider(4000, 0, 0.000064, 100)
    );

    private long time = 0L;

    public StubAudioAnalyzerDataProvider() {
        super(20L, TimeUnit.MILLISECONDS);
    }

    @Override
    protected AudioData onNext() {
        final long[] xItems = audioData.xData.getItemsArray();
        final short[] yItems = audioData.yData.getItemsArray();

        for (int i = 0; i < bufferSizeInShorts; i++) {
            xItems[i] = time++;
            yItems[i] = provider.getYValueForIndex(time);
        }

        return audioData;
    }

    @Override
    public int getBufferSize() {
        return bufferSizeInShorts;
    }

    @Override
    public int getSampleRate() {
        return 44100;
    }

    private interface IYValuesProvider {
        short getYValueForIndex(long index);
    }

    private static class NoisySinewaveYValueProvider implements IYValuesProvider {
       private final Random random = new Random();

       private final double amplitude;
       private final double phase;
       private final double noiseAmplitude;
       private final double wn;

        public NoisySinewaveYValueProvider(double amplitude, double phase, double frequency, double noiseAmplitude) {
            this.amplitude = amplitude;
            this.phase = phase;
            this.noiseAmplitude = noiseAmplitude;
            this.wn = 2 * Math.PI * frequency;
        }

        @Override
        public short getYValueForIndex(long index) {
            return (short)(amplitude * Math.sin(index * wn + phase) + (random.nextDouble() - .5) * noiseAmplitude);
        }
    }

    private static class FrequencySinewaveYValueProvider implements IYValuesProvider {
        private final double amplitude;
        private final double phase;

        private final double minFrequency;
        private final double maxFrequency;
        private final double step;

        private double frequency;

        public FrequencySinewaveYValueProvider(double amplitude, double phase, double minFrequency, double maxFrequency, double step) {
            this.amplitude = amplitude;
            this.phase = phase;

            this.minFrequency = minFrequency;
            this.maxFrequency = maxFrequency;
            this.step = step;

            this.frequency = minFrequency;
        }

        @Override
        public short getYValueForIndex(long index) {
            this.frequency = frequency <= maxFrequency ? frequency + step : minFrequency;

            final double wn = 2 * Math.PI * frequency;
            return (short) (amplitude * Math.sin(index * wn + phase));
        }
    }

    private static class AggregateYValueProvider implements IYValuesProvider {
        private final IYValuesProvider[] providers;

        private AggregateYValueProvider(IYValuesProvider... providers) {
            this.providers = providers;
        }

        @Override
        public short getYValueForIndex(long index) {
            double sum = 0;
            for (int i = 0; i < providers.length; i++) {
                sum += providers[i].getYValueForIndex(index);
            }
            return (short) sum;
        }
    }
}
