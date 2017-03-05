package com.scichart.scishowcase.utils

import com.scichart.charting.model.dataSeries.UniformHeatmapDataSeries
import com.scichart.charting.model.dataSeries.XyDataSeries

inline fun <T> T.init(init: T.() -> Unit): T {
    this.init()
    return this
}

inline fun <reified TX : Comparable<TX>, reified TY : Comparable<TY>> XyDataSeries(): XyDataSeries<TX, TY> {
    return XyDataSeries(TX::class.javaObjectType, TY::class.javaObjectType)
}

inline fun <reified TX : Comparable<TX>, reified TY : Comparable<TY>, reified TZ : Comparable<TZ>> UniformHeatmapDataSeries(xSize: Int, ySize: Int): UniformHeatmapDataSeries<TX, TY, TZ> {
    return UniformHeatmapDataSeries(TX::class.javaObjectType, TY::class.javaObjectType, TZ::class.javaObjectType, xSize, ySize)
}
