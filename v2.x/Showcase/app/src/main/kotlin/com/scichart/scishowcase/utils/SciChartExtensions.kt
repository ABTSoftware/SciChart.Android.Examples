//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2017. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// SciChartExtensions.kt is part of the SCICHART® Showcases. Permission is hereby granted
// to modify, create derivative works, distribute and publish any part of this source
// code whether for commercial, private or personal use.
//
// The SCICHART® examples are distributed in the hope that they will be useful, but
// without any warranty. It is provided "AS IS" without warranty of any kind, either
// expressed or implied.
//******************************************************************************

package com.scichart.scishowcase.utils

import com.scichart.charting.model.dataSeries.OhlcDataSeries
import com.scichart.charting.model.dataSeries.UniformHeatmapDataSeries
import com.scichart.charting.model.dataSeries.XyDataSeries

inline fun <T> T.init(init: T.() -> Unit): T {
    this.init()
    return this
}

inline fun <reified TX : Comparable<TX>, reified TY : Comparable<TY>> XyDataSeries(): XyDataSeries<TX, TY> {
    return XyDataSeries(TX::class.javaObjectType, TY::class.javaObjectType)
}

inline fun <reified TX : Comparable<TX>, reified TY : Comparable<TY>> OhlcDataSeries(): OhlcDataSeries<TX, TY> {
    return OhlcDataSeries(TX::class.javaObjectType, TY::class.javaObjectType)
}

inline fun <reified TX : Comparable<TX>, reified TY : Comparable<TY>, reified TZ : Comparable<TZ>> UniformHeatmapDataSeries(xSize: Int, ySize: Int): UniformHeatmapDataSeries<TX, TY, TZ> {
    return UniformHeatmapDataSeries(TX::class.javaObjectType, TY::class.javaObjectType, TZ::class.javaObjectType, xSize, ySize)
}
