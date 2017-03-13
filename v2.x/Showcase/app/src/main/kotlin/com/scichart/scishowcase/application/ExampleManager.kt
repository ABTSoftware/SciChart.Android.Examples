//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2017. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// ExampleManager.kt is part of the SCICHART® Showcases. Permission is hereby granted
// to modify, create derivative works, distribute and publish any part of this source
// code whether for commercial, private or personal use.
//
// The SCICHART® examples are distributed in the hope that they will be useful, but
// without any warranty. It is provided "AS IS" without warranty of any kind, either
// expressed or implied.
//******************************************************************************

package com.scichart.scishowcase.application

import com.scichart.scishowcase.views.AudioAnalyzerFragment
import com.scichart.scishowcase.views.DashboardFragment
import com.scichart.scishowcase.views.EcgFragment
import com.scichart.scishowcase.views.TraderFragment

object ExampleManager {
    val examples: List<Example> = listOf(AudioAnalyzerFragment::class.java, EcgFragment::class.java, DashboardFragment::class.java, TraderFragment::class.java)
            .map(::Example)
            .sortedBy { it.title }
            .toList()

    fun getExampleByTitle(exampleTitle: String) = examples.firstOrNull { it.title == exampleTitle }
}