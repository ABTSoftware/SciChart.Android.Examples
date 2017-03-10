package com.scichart.scishowcase.application

import com.scichart.scishowcase.views.AudioAnalyzerFragment
import com.scichart.scishowcase.views.EcgFragment
import com.scichart.scishowcase.views.SingleChartFragment

object ExampleManager {
    val examples: List<Example> = listOf(SingleChartFragment::class, AudioAnalyzerFragment::class, EcgFragment::class)
            .map(::Example)
            .sortedBy { it.title }
            .toList()

    fun getExampleByTitle(exampleTitle: String) = examples.firstOrNull { it.title == exampleTitle }
}