//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2017. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// StubAudioAnalyzerDataProvider.kt is part of the SCICHART® Showcases. Permission is hereby granted
// to modify, create derivative works, distribute and publish any part of this source
// code whether for commercial, private or personal use.
//
// The SCICHART® examples are distributed in the hope that they will be useful, but
// without any warranty. It is provided "AS IS" without warranty of any kind, either
// expressed or implied.
//******************************************************************************

package com.scichart.scishowcase.model.audioAnalyzer

import com.scichart.scishowcase.model.DataProviderBase
import java.util.*
import java.util.concurrent.TimeUnit

class StubAudioAnalyzerDataProvider(private val bufferSizeInShorts: Int = 2048) : DataProviderBase<AudioData>(20L, TimeUnit.MILLISECONDS), IAudioAnalyzerDataProvider {

    var time = 0L

    val audioData = AudioData(bufferSizeInShorts)

    private val provider = AggregateYValueProvider(arrayOf(
            FrequencySinewaveYValueProvider(8000.0, 0.0, 0.0, 1.0, 0.0000005),
            NoisySinewaveYValueProvider(8000.0, 0.0, 0.000032, 200.0),
            NoisySinewaveYValueProvider(6000.0, 0.0, 0.000016, 100.0),
            NoisySinewaveYValueProvider(4000.0, 0.0, 0.000064, 100.0)
    ))

    override fun onNext(): AudioData {
        val xItemsArray = audioData.xData.itemsArray
        val yItemsArray = audioData.yData.itemsArray

        for (index in 0 until bufferSizeInShorts) {
            xItemsArray[index] = time++
            yItemsArray[index] = provider.getYValueForIndex(time)
        }

        return audioData
    }

    override fun getBufferSize(): Int = bufferSizeInShorts

    override fun getSampleRate(): Int = 44100

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