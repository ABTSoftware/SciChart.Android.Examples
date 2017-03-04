package com.scichart.scishowcase.views

import android.util.Log
import com.scichart.scishowcase.R
import com.scichart.scishowcase.application.ExampleDefinition
import com.scichart.scishowcase.databinding.AudioAnalyzerFragmentBinding
import com.scichart.scishowcase.model.audioAnalyzer.DefaultAudioAnalyzerDataProvider
import com.scichart.scishowcase.model.audioAnalyzer.IAudioAnalyzerDataProvider
import com.scichart.scishowcase.model.audioAnalyzer.StubAudioAnalyzerDataProvider
import com.scichart.scishowcase.viewModels.audioAnalyzer.AudioAnalyzerViewModel

@ExampleDefinition("Audio Analyzer", "Custom Description")
class AudioAnalyzerFragment : BindingFragmentBase<AudioAnalyzerFragmentBinding, AudioAnalyzerViewModel>() {

    override fun getLayoutId(): Int = R.layout.audio_analyzer_fragment

    override fun onCreateViewModel(): AudioAnalyzerViewModel = AudioAnalyzerViewModel(activity, createDataProvider())

    private fun createDataProvider(): IAudioAnalyzerDataProvider {
        try {
            return DefaultAudioAnalyzerDataProvider()
        } catch (e: Exception) {
            Log.i("AudioAnalyzerFragment", "Initialization of DefaultAudioAnalyzerDataProvider failed. Using stub implementation instead", e)
            return StubAudioAnalyzerDataProvider()
        }
    }
}