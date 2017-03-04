package com.scichart.scishowcase.viewModels.audioAnalyzer

import android.content.Context
import android.util.Log
import com.scichart.core.model.DoubleValues
import com.scichart.core.utility.DoubleUtil
import com.scichart.scishowcase.model.audioAnalyzer.IAudioAnalyzerDataProvider
import com.scichart.scishowcase.utils.FFT2
import com.scichart.scishowcase.viewModels.FragmentViewModelBase
import io.reactivex.disposables.Disposable

class AudioAnalyzerViewModel(context: Context, private val dataProvider: IAudioAnalyzerDataProvider) : FragmentViewModelBase(context) {
    private val bufferSize = dataProvider.getBufferSize()
    private val fftSize = bufferSize / 2 // FFT size = half of the buffer size because FFT is symmetric
    private val audioStreamBufferSize = 500000

    val audioStreamVM = AudioStreamViewModel(context, audioStreamBufferSize)
    val fftVM = FFTViewModel(context, fftSize)
    val spectrogramVM = SpectrogramViewModel(context, fftSize, audioStreamBufferSize / bufferSize)

    private var dataProviderSubscription: Disposable? = null
    private val fftData = DoubleValues()
    private val fft = FFT2(DoubleUtil.log(bufferSize.toDouble(), 2.0).toInt())

    override fun onResume() {
        super.onResume()

        dataProvider.start()

        dataProviderSubscription = dataProvider.getAudioData().doOnNext {
            audioStreamVM.onNextAudioData(it)

            fft.run(it.yData, fftData)

            fftVM.onNextFFT(fftData)
            spectrogramVM.onNextFFT(fftData)

        }.doOnError { Log.e("AudioAnalyzerVM", "append", it) }.subscribe()
    }

    override fun onPause() {
        super.onPause()

        dataProvider.stop()

        dataProviderSubscription?.dispose()
        dataProviderSubscription = null
    }
}


