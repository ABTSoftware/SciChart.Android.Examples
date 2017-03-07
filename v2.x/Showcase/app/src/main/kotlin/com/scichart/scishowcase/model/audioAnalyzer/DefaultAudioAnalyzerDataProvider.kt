package com.scichart.scishowcase.model.audioAnalyzer

import android.media.AudioFormat
import android.media.AudioRecord
import android.util.Log
import com.scichart.scishowcase.model.DataProviderBase
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import java.util.concurrent.TimeUnit

class DefaultAudioAnalyzerDataProvider(sampleRate: Int = 44100,
                                       channelConfig: Int = AudioFormat.CHANNEL_IN_MONO,
                                       audioConfig: Int = AudioFormat.ENCODING_PCM_16BIT,
                                       private val minBufferSize: Int = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioConfig),
                                       private val minBufferSizeInShorts: Int = minBufferSize/ 2,
                                       interval: Long = (sampleRate / minBufferSizeInShorts).toLong()) : DataProviderBase<AudioData>(interval, TimeUnit.MILLISECONDS), IAudioAnalyzerDataProvider {

    private val audioRecord = AudioRecord(1, sampleRate, channelConfig, audioConfig, minBufferSize)

    private val audioData = AudioData(minBufferSizeInShorts)

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
        audioRecord.read(audioData.yData.itemsArray, 0, minBufferSizeInShorts)

        val itemsArray = audioData.xData.itemsArray
        for (index in 0 until minBufferSizeInShorts)
            itemsArray[index] = time++

        return audioData
    }

    override fun getBufferSize(): Int = minBufferSizeInShorts
}