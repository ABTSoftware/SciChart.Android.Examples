//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2020. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// DefaultAudioAnalyzerDataProvider.java is part of the SCICHART® Examples. Permission is hereby granted
// to modify, create derivative works, distribute and publish any part of this source
// code whether for commercial, private or personal use.
//
// The SCICHART® examples are distributed in the hope that they will be useful, but
// without any warranty. It is provided "AS IS" without warranty of any kind, either
// expressed or implied.
//******************************************************************************

package com.scichart.examples.fragments.showcase.audioAnalyzer;

import android.media.AudioFormat;
import android.media.AudioRecord;

import com.scichart.examples.fragments.base.DataProviderBase;

import java.util.concurrent.TimeUnit;

public class DefaultAudioAnalyzerDataProvider extends DataProviderBase<AudioData> implements IAudioAnalyzerDataProvider {
    private final int sampleRate;
    private final int minBufferSize; // should be with power of 2 for correct work of FFT

    private final AudioRecord audioRecord;
    private final AudioData audioData;

    private long time = 0L;

    public DefaultAudioAnalyzerDataProvider(int sampleRate, int minBufferSize) {
        super(sampleRate / minBufferSize, TimeUnit.MILLISECONDS);

        this.sampleRate = sampleRate;
        this.minBufferSize = minBufferSize;
        this.audioRecord = new AudioRecord(1, sampleRate, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, minBufferSize);
        this.audioData = new AudioData(minBufferSize);

        if(this.audioRecord.getState() != AudioRecord.STATE_INITIALIZED) {
            throw new UnsupportedOperationException("This devices doesn't support AudioRecord");
        }
    }

    public DefaultAudioAnalyzerDataProvider() {
        this(44100, 2048);
    }

    @Override
    protected void onStart() {
        super.onStart();

        audioRecord.startRecording();
    }

    @Override
    protected void onStop() {
        audioRecord.stop();

        super.onStop();
    }

    @Override
    protected AudioData onNext() {
        audioRecord.read(audioData.yData.getItemsArray(), 0, minBufferSize);

        final long[] itemsArray = audioData.xData.getItemsArray();
        for (int i = 0; i < minBufferSize; i++) {
            itemsArray[i] = time++;
        }

        return audioData;
    }

    @Override
    public int getBufferSize() {
        return minBufferSize;
    }

    @Override
    public int getSampleRate() {
        return sampleRate;
    }
}
