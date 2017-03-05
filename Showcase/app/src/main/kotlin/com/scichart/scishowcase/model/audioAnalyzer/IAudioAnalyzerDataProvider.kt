package com.scichart.scishowcase.model.audioAnalyzer

import io.reactivex.Flowable

interface IAudioAnalyzerDataProvider {
    fun start()
    fun stop()

    fun getAudioData(): Flowable<AudioData>
    fun getBufferSize(): Int
}

