package com.scichart.scishowcase.viewModels.audioAnalyzer

import android.content.Context
import android.util.Log
import com.scichart.core.model.DoubleValues
import com.scichart.core.utility.DoubleUtil
import com.scichart.scishowcase.model.audioAnalyzer.IAudioAnalyzerDataProvider
import com.scichart.scishowcase.utils.Radix2FFT
import com.scichart.scishowcase.viewModels.FragmentViewModelBase
import com.trello.rxlifecycle2.LifecycleProvider
import com.trello.rxlifecycle2.kotlin.bindToLifecycle
import io.reactivex.disposables.Disposable

class AudioAnalyzerViewModel(context: Context, private val dataProvider: IAudioAnalyzerDataProvider) : FragmentViewModelBase(context) {
    private val bufferSize = dataProvider.getBufferSize()
    private val audioStreamBufferSize = 500000

    private val fft = Radix2FFT(bufferSize)
    private val fftSize = fft.fftSize

    val audioStreamVM = AudioStreamViewModel(context, audioStreamBufferSize)
    val fftVM = FFTViewModel(context, fftSize)
    val spectrogramVM = SpectrogramViewModel(context, fftSize, audioStreamBufferSize / bufferSize)

    private val fftData = DoubleValues()

    override fun subscribe(lifecycleProvider: LifecycleProvider<*>) {
        super.subscribe(lifecycleProvider)

        dataProvider.getData().doOnNext {
            audioStreamVM.onNextAudioData(it)

            fft.run(it.yData, fftData)

            fftVM.onNextFFT(fftData)
            spectrogramVM.onNextFFT(fftData)
        }.bindToLifecycle(lifecycleProvider).subscribe()
    }
}


