package com.scichart.scishowcase.model.audioAnalyzer

import android.util.Log
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import java.util.*
import java.util.concurrent.TimeUnit

class StubAudioAnalyzerDataProvider : IAudioAnalyzerDataProvider {
    val bufferSizeInShorts = 2048
    val interval = 20L

    val audioDataPublisher: PublishSubject<AudioData> = PublishSubject.create<AudioData>()

    var subscription: Disposable? = null
    var time = 0L

    val audioData = AudioData(bufferSizeInShorts)

    private val provider = AggregateYValueProvider(arrayOf(
            FrequencySinewaveYValueProvider(8000.0, 0.0, 0.0, 1.0, 0.0000005),
            NoisySinewaveYValueProvider(8000.0, 0.0, 0.000032, 200.0),
            NoisySinewaveYValueProvider(6000.0, 0.0, 0.000016, 100.0),
            NoisySinewaveYValueProvider(4000.0, 0.0, 0.000064, 100.0)
    ))

    override fun start() {
        subscription = Observable
                .interval(interval, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.computation())
                .doOnNext { sample() }
                .doOnError { Log.e("AudioDataProvider", "publish", it) }
                .subscribe()
    }

    private fun sample() {
        val xItemsArray = audioData.xData.itemsArray
        val yItemsArray = audioData.yData.itemsArray

        for (index in 0 until bufferSizeInShorts) {
            xItemsArray[index] = time++
            yItemsArray[index] = provider.getYValueForIndex(time)
        }

        audioDataPublisher.onNext(audioData)
    }

    override fun stop() {
        audioDataPublisher.onComplete()

        subscription?.dispose()
        subscription = null
    }

    override fun getAudioData(): Flowable<AudioData> = audioDataPublisher.toFlowable(BackpressureStrategy.BUFFER)

    override fun getBufferSize(): Int = bufferSizeInShorts

    private interface IYValueProvider {
        fun getYValueForIndex(index: Long): Short
    }

    private class NoisySinewaveYValueProvider(private val amplitude: Double, private val phase: Double, private val freq: Double, private val noiseAmplitude: Double) : IYValueProvider {
        private val random = Random()
        override fun getYValueForIndex(index: Long): Short {
            val wn = 2 * Math.PI * freq
            return (amplitude * Math.sin(index * wn + phase) + (random.nextDouble() - 0.5) * noiseAmplitude).toShort()
        }
    }

    private class FrequencySinewaveYValueProvider(private val amplitude: Double, private val phase: Double, private val minFrequency: Double, private val maxFrequency: Double, private val step: Double) : IYValueProvider {
        private var frequency = minFrequency

        override fun getYValueForIndex(index: Long): Short {
            frequency = if(frequency <= maxFrequency) frequency + step else minFrequency

            val wn = 2 * Math.PI * frequency
            return (amplitude * Math.sin(index * wn + phase)).toShort()
        }
    }

    private class AggregateYValueProvider(val providers: Array<IYValueProvider>) : IYValueProvider {
        override fun getYValueForIndex(index: Long): Short {
            return providers.sumByDouble { it.getYValueForIndex(index).toDouble() }.toShort()
        }

    }
}