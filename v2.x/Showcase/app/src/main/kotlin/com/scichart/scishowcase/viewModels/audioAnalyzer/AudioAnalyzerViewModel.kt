//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2017. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// AudioAnalyzerViewModel.kt is part of the SCICHART® Showcases. Permission is hereby granted
// to modify, create derivative works, distribute and publish any part of this source
// code whether for commercial, private or personal use.
//
// The SCICHART® examples are distributed in the hope that they will be useful, but
// without any warranty. It is provided "AS IS" without warranty of any kind, either
// expressed or implied.
//******************************************************************************

package com.scichart.scishowcase.viewModels.audioAnalyzer

import android.content.Context
import com.scichart.core.model.DoubleValues
import com.scichart.scishowcase.model.audioAnalyzer.IAudioAnalyzerDataProvider
import com.scichart.scishowcase.utils.Radix2FFT
import com.scichart.scishowcase.viewModels.FragmentViewModelBase
import com.trello.rxlifecycle2.LifecycleProvider
import com.trello.rxlifecycle2.kotlin.bindToLifecycle

class AudioAnalyzerViewModel(context: Context, maxFrequency: Int, private val dataProvider: IAudioAnalyzerDataProvider) : FragmentViewModelBase(context) {
    private val bufferSize = dataProvider.getBufferSize()
    private val sampleRate = dataProvider.getSampleRate()
    private val audioStreamBufferSize = 500000

    private val fft = Radix2FFT(bufferSize)

    private val hzPerDataPoint = sampleRate.toDouble() / bufferSize
    private val fftSize = (maxFrequency / hzPerDataPoint).toInt()

    val audioStreamVM = AudioStreamViewModel(context, audioStreamBufferSize)
    val fftVM = FFTViewModel(context, fftSize, hzPerDataPoint)
    val spectrogramVM = SpectrogramViewModel(context, fftSize, audioStreamBufferSize / bufferSize)

    private val fftData = DoubleValues()

    override fun subscribe(lifecycleProvider: LifecycleProvider<*>) {
        super.subscribe(lifecycleProvider)

        dataProvider.getData().doOnNext {
            audioStreamVM.onNextAudioData(it)

            fft.run(it.yData, fftData)
            fftData.setSize(fftSize)

            fftVM.onNextFFT(fftData)
            spectrogramVM.onNextFFT(fftData)
        }.bindToLifecycle(lifecycleProvider).subscribe()
    }
}