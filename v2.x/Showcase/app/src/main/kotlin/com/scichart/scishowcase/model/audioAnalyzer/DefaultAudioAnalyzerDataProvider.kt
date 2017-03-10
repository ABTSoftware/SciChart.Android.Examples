package com.scichart.scishowcase.model.audioAnalyzer

import android.media.AudioFormat
import android.media.AudioRecord
import android.util.Log
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import java.util.concurrent.TimeUnit

class DefaultAudioAnalyzerDataProvider(sampleRate: Int = 44100, channelConfig: Int = AudioFormat.CHANNEL_IN_MONO, audioConfig: Int = AudioFormat.ENCODING_PCM_16BIT) : IAudioAnalyzerDataProvider {
    private val minBufferSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioConfig)

    private val minBufferSizeInShorts = minBufferSize / 2

    private val interval: Long = (sampleRate / minBufferSizeInShorts).toLong()

    private val audioRecord = AudioRecord(1, sampleRate, channelConfig, audioConfig, minBufferSize)

    private val audioDataPublisher: PublishSubject<AudioData> = PublishSubject.create<AudioData>()
    private val audioData = AudioData(minBufferSizeInShorts)

    private var subscription: Disposable? = null
    private var time = 0L

    init {
        if (audioRecord.state != AudioRecord.STATE_INITIALIZED)
            throw UnsupportedOperationException("This device doesn't support AudioRecord")
    }

    override fun start() {
        audioRecord.startRecording()

        subscription = Observable
                .interval(interval, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.computation())
                .doOnNext { sample() }
                .doOnError { Log.e("AudioDataProvider", "publish", it) }
                .subscribe()
    }

    override fun stop() {
        audioRecord.stop()

        audioDataPublisher.onComplete()
        subscription?.dispose()
        subscription = null
    }

    private fun sample() {
        audioRecord.read(audioData.yData.itemsArray, 0, minBufferSizeInShorts)

        val itemsArray = audioData.xData.itemsArray
        for (index in 0 until minBufferSizeInShorts)
            itemsArray[index] = time++

        audioDataPublisher.onNext(audioData)
    }

    override fun getAudioData(): Flowable<AudioData> = audioDataPublisher.toFlowable(BackpressureStrategy.BUFFER)

    override fun getBufferSize(): Int = minBufferSizeInShorts
}