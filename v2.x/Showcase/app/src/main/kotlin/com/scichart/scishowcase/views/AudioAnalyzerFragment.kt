//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2017. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// AudioAnalyzerFragment.kt is part of the SCICHART® Showcases. Permission is hereby granted
// to modify, create derivative works, distribute and publish any part of this source
// code whether for commercial, private or personal use.
//
// The SCICHART® examples are distributed in the hope that they will be useful, but
// without any warranty. It is provided "AS IS" without warranty of any kind, either
// expressed or implied.
//******************************************************************************

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

    override fun onCreateViewModel(): AudioAnalyzerViewModel = AudioAnalyzerViewModel(activity, 10000, createDataProvider())

    private fun createDataProvider(): IAudioAnalyzerDataProvider {
        try {
            return DefaultAudioAnalyzerDataProvider()
        } catch (e: Exception) {
            Log.i("AudioAnalyzerFragment", "Initialization of DefaultAudioAnalyzerDataProvider failed. Using stub implementation instead", e)
            return StubAudioAnalyzerDataProvider()
        }
    }
}