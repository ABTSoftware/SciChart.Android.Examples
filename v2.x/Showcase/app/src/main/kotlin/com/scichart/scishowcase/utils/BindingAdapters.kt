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

import android.animation.*
import android.databinding.BindingAdapter
import android.graphics.PointF
import android.support.constraint.ConstraintLayout
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.animation.AnticipateInterpolator
import android.view.animation.OvershootInterpolator
import com.ogaclejapan.arclayout.ArcLayout
import com.scichart.charting.layoutManagers.DefaultLayoutManager
import com.scichart.charting.layoutManagers.IAxisLayoutStrategy
import com.scichart.charting.model.AxisCollection
import com.scichart.charting.model.RenderableSeriesCollection
import com.scichart.charting.visuals.SciChartSurface
import com.scichart.core.framework.UpdateSuspender
import com.scichart.scishowcase.viewModels.ChartViewModel
import com.scichart.scishowcase.views.LongTouchListenerView

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
        surface.viewportManager = viewModel.viewportManager
        surface.xAxes = viewModel.xAxes
        surface.yAxes = viewModel.yAxes
        surface.renderableSeries = viewModel.renderableSeries
        surface.annotations = viewModel.annotations
        surface.chartModifiers = viewModel.chartModifiers
    })
}

@BindingAdapter("scichart:isLongTouchEnabled")
fun setLongTouchEnabled(view: LongTouchListenerView, isEnabled: Boolean) {
    view.isLongTouchEnabled = isEnabled
}

private var lastPoint: PointF? = null

@BindingAdapter("scichart:contextMenuPosition")
fun changeContextMenuVisibility(arcLayout: ArcLayout, point: PointF?) {
    if (point != null) {
        showMenu(arcLayout, point)
        lastPoint = point
    } else if (lastPoint != null) {
        hideMenu(arcLayout, lastPoint!!)
    }
}

private fun showMenu(arcLayout: ArcLayout, point: PointF) {
    val layoutParams = arcLayout.layoutParams as ConstraintLayout.LayoutParams

    layoutParams.rightMargin = (arcLayout.parent as ConstraintLayout).measuredWidth - point.x.toInt()
    layoutParams.bottomMargin = (arcLayout.parent as ConstraintLayout).measuredHeight - point.y.toInt()

    arcLayout.layoutParams = layoutParams
    arcLayout.visibility = View.VISIBLE

    val animList = ArrayList<Animator>()

    for (i in 0 until arcLayout.childCount) {
        animList.add(createShowItemAnimator(arcLayout.getChildAt(i), point))
    }

    val animSet = AnimatorSet()
    animSet.duration = 550
    animSet.interpolator = OvershootInterpolator()
    animSet.playTogether(animList)
    animSet.start()
}

private fun createShowItemAnimator(item: View, pointF: PointF): Animator {
    val dx = pointF.x - item.x
    val dy = pointF.y - item.y

    item.rotation = 0f
    item.translationX = dx
    item.translationY = dy

    return ObjectAnimator.ofPropertyValuesHolder(item,
            PropertyValuesHolder.ofFloat("rotation", 0f, 720f),
            PropertyValuesHolder.ofFloat("translationX", dx, 0f),
            PropertyValuesHolder.ofFloat("translationY", dy, 0f))
}

private fun hideMenu(arcLayout: ArcLayout, point: PointF) {
    val animList = ArrayList<Animator>()

    for (i in arcLayout.childCount - 1 downTo 0) {
        animList.add(createHideItemAnimator(arcLayout.getChildAt(i), point))
    }

    val animSet = AnimatorSet()
    animSet.duration = 550
    animSet.interpolator = AnticipateInterpolator()
    animSet.playTogether(animList)
    animSet.addListener(object : AnimatorListenerAdapter() {
        override fun onAnimationEnd(animation: Animator) {
            super.onAnimationEnd(animation)
            arcLayout.visibility = View.INVISIBLE
        }
    })
    animSet.start()
}

private fun createHideItemAnimator(item: View, pointF: PointF): Animator {
    val dx = pointF.x - item.x
    val dy = pointF.y - item.y

    val anim = ObjectAnimator.ofPropertyValuesHolder(item,
            PropertyValuesHolder.ofFloat("rotation", 720f, 0f),
            PropertyValuesHolder.ofFloat("translationX", 0f, dx),
            PropertyValuesHolder.ofFloat("translationY", 0f, dy)
    )
    anim.addListener(object : AnimatorListenerAdapter() {
        override fun onAnimationEnd(animation: Animator) {
            super.onAnimationEnd(animation)
            item.translationX = 0f
            item.translationY = 0f
        }
    })
    return anim
}