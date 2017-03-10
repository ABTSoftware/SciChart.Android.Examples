package com.scichart.scishowcase.model.audioAnalyzer

import com.scichart.core.model.LongValues
import com.scichart.core.model.ShortValues

data class AudioData(val pointsCount: Int) {
    val xData = LongValues(pointsCount)
    val yData = ShortValues(pointsCount)

    init {
        xData.setSize(pointsCount)
        yData.setSize(pointsCount)
    }
}