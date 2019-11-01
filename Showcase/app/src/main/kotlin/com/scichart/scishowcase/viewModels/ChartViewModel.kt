//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2017. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// ChartViewModel.kt is part of the SCICHART® Showcases. Permission is hereby granted
// to modify, create derivative works, distribute and publish any part of this source
// code whether for commercial, private or personal use.
//
// The SCICHART® examples are distributed in the hope that they will be useful, but
// without any warranty. It is provided "AS IS" without warranty of any kind, either
// expressed or implied.
//******************************************************************************

package com.scichart.scishowcase.viewModels

import android.content.Context
import com.scichart.charting.model.AnnotationCollection
import com.scichart.charting.model.AxisCollection
import com.scichart.charting.model.ChartModifierCollection
import com.scichart.charting.model.RenderableSeriesCollection
import com.scichart.charting.viewportManagers.DefaultViewportManager

open class ChartViewModel(val context: Context) {
    val xAxes = AxisCollection()
    val yAxes = AxisCollection()
    val renderableSeries = RenderableSeriesCollection()
    val annotations = AnnotationCollection()
    val chartModifiers = ChartModifierCollection()
    var viewportManager = DefaultViewportManager()
}