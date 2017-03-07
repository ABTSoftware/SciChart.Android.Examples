//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2017. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// BindingAdapters.kt is part of the SCICHART® Showcases. Permission is hereby granted
// to modify, create derivative works, distribute and publish any part of this source
// code whether for commercial, private or personal use.
//
// The SCICHART® examples are distributed in the hope that they will be useful, but
// without any warranty. It is provided "AS IS" without warranty of any kind, either
// expressed or implied.
//******************************************************************************

package com.scichart.scishowcase.utils

import android.databinding.BindingAdapter
import android.support.v7.widget.RecyclerView
import com.scichart.charting.layoutManagers.DefaultLayoutManager
import com.scichart.charting.layoutManagers.IAxisLayoutStrategy
import com.scichart.charting.model.AxisCollection
import com.scichart.charting.model.RenderableSeriesCollection
import com.scichart.charting.visuals.SciChartSurface
import com.scichart.core.framework.UpdateSuspender
import com.scichart.scishowcase.viewModels.ChartViewModel

@BindingAdapter("app:configuration")
fun configureRecyclerView(recyclerView: RecyclerView, configuration: RecyclerConfiguration) {
    recyclerView.layoutManager = configuration.layoutManager
    recyclerView.itemAnimator = configuration.itemAnimator
    recyclerView.adapter = configuration.adapter
}

@BindingAdapter("scichart:xAxes")
fun configureSurfaceXAxes(surface: SciChartSurface, xAxes: AxisCollection?) {
    surface.xAxes = xAxes
}

@BindingAdapter("scichart:yAxes")
fun configureSurfaceYAxes(surface: SciChartSurface, yAxes: AxisCollection?) {
    surface.yAxes = yAxes
}

@BindingAdapter("scichart:renderableSeries")
fun configureSurfaceRenderableSeries(surface: SciChartSurface, renderableSeries: RenderableSeriesCollection?) {
    surface.renderableSeries = renderableSeries
}

@BindingAdapter("scichart:leftOuterAxesLayoutStrategy")
fun configureSurfaceLeftOuterAxesLayoutStrategy(surface: SciChartSurface, axisLayoutManager: IAxisLayoutStrategy) {
    val layoutManager: DefaultLayoutManager = DefaultLayoutManager.Builder().setLeftOuterAxesLayoutStrategy(axisLayoutManager).build()
    surface.layoutManager = layoutManager
}

@BindingAdapter("scichart:viewModel")
fun configureSurfaceViewModel(surface: SciChartSurface, viewModel: ChartViewModel) {
    UpdateSuspender.using(surface, {
        surface.xAxes = viewModel.xAxes
        surface.yAxes = viewModel.yAxes
        surface.renderableSeries = viewModel.renderableSeries
        surface.annotations = viewModel.annotations
    })
}