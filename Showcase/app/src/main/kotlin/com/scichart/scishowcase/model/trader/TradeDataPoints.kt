//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2017. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// TradeDataPoints.kt is part of the SCICHART® Showcases. Permission is hereby granted
// to modify, create derivative works, distribute and publish any part of this source
// code whether for commercial, private or personal use.
//
// The SCICHART® Showcases are distributed in the hope that they will be useful, but
// without any warranty. It is provided "AS IS" without warranty of any kind, either
// expressed or implied.
//******************************************************************************

package com.scichart.scishowcase.model.trader

import com.scichart.core.model.DateValues
import com.scichart.core.model.DoubleValues

class TradeDataPoints {
    var xValues = DateValues()

    val openValues = DoubleValues()
    val highValues = DoubleValues()
    val lowValues = DoubleValues()
    val closeValues = DoubleValues()

    val volumeValues = DoubleValues()

    fun append(x: Long, open: Double, high: Double, low: Double, close: Double, volume: Double) {
        xValues.addTime(x)
        openValues.add(open)
        highValues.add(high)
        lowValues.add(low)
        closeValues.add(close)
        volumeValues.add(volume)
    }

    fun clear() {
        xValues.clear()
        openValues.clear()
        highValues.clear()
        lowValues.clear()
        closeValues.clear()
        volumeValues.clear()
    }
}
