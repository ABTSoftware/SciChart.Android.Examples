//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2017. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// DefaultAudioAnalyzerDataProvider.kt is part of the SCICHART® Showcases. Permission is hereby granted
// to modify, create derivative works, distribute and publish any part of this source
// code whether for commercial, private or personal use.
//
// The SCICHART® examples are distributed in the hope that they will be useful, but
// without any warranty. It is provided "AS IS" without warranty of any kind, either
// expressed or implied.
//******************************************************************************

package com.scichart.scishowcase.model.audioAnalyzer

import android.media.AudioFormat
import android.media.AudioRecord
import com.scichart.scishowcase.model.DataProviderBase
import java.util.concurrent.TimeUnit

class DefaultAudioAnalyzerDataProvider(sampleRate: Int = 44100,
                                       private val minBufferSize: Int = 2048, // should be with power of 2 for correct work of FFT
                                       interval: Long = (sampleRate / minBufferSize).toLong()) : DataProviderBase<AudioData>(interval, TimeUnit.MILLISECONDS), IAudioAnalyzerDataProvider {

    private val audioRecord = AudioRecord(1, sampleRate, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, minBufferSize * 2)

    private val audioData = AudioData(minBufferSize)

    private var time = 0L

    init {
        if (audioRecord.state != AudioRecord.STATE_INITIALIZED)
            throw UnsupportedOperationException("This device doesn't support AudioRecord")
    }

    override fun onStart() {
        super.onStart()

        audioRecord.startRecording()
    }

    override fun onStop() {
        super.onStop()

        audioRecord.stop()
    }

    override fun onNext(): AudioData {
        audioRecord.read(audioData.yData.itemsArray, 0, minBufferSize)

        val itemsArray = audioData.xData.itemsArray
        for (index in 0 until minBufferSize)
            itemsArray[index] = time++

        return audioData
    }

    override fun getBufferSize(): Int = minBufferSize
}