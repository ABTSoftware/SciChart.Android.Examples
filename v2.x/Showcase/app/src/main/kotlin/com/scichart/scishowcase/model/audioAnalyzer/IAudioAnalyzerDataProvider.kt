package com.scichart.scishowcase.model.audioAnalyzer

import com.scichart.scishowcase.model.IDataProvider

interface IAudioAnalyzerDataProvider : IDataProvider<AudioData>{
    fun getBufferSize(): Int
}

