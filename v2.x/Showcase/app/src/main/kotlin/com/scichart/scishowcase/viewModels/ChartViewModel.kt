package com.scichart.scishowcase.viewModels

import android.content.Context
import com.scichart.charting.model.AnnotationCollection
import com.scichart.charting.model.AxisCollection
import com.scichart.charting.model.RenderableSeriesCollection

open class ChartViewModel(val context: Context) {
    val xAxes = AxisCollection()
    val yAxes = AxisCollection()
    val renderableSeries = RenderableSeriesCollection()
    val annotations = AnnotationCollection()
}