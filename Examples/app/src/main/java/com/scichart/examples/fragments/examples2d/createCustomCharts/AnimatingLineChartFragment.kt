//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2021. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// AnimatingLineChartFragment.kt is part of SCICHART®, High Performance Scientific Charts
// For full terms and conditions of the license, see http://www.scichart.com/scichart-eula/
//
// This source code is protected by international copyright law. Unauthorized
// reproduction, reverse-engineering, or distribution of all or any portion of
// this source code is strictly prohibited.
//
// This source code contains confidential and proprietary trade secrets of
// SciChart Ltd., and should at no time be copied, transferred, sold,
// distributed or made available without express written permission.
//******************************************************************************

package com.scichart.examples.fragments.examples2d.createCustomCharts.kt

import android.animation.Animator
import android.animation.ValueAnimator
import android.animation.ValueAnimator.AnimatorUpdateListener
import android.os.Bundle
import android.view.animation.DecelerateInterpolator
import com.scichart.charting.model.dataSeries.IDataSeries
import com.scichart.charting.visuals.SciChartSurface
import com.scichart.charting.visuals.axes.AutoRange.Always
import com.scichart.charting.visuals.axes.AutoRange.Never
import com.scichart.charting.visuals.renderableSeries.FastLineRenderableSeries
import com.scichart.charting.visuals.renderableSeries.data.ISeriesRenderPassData
import com.scichart.charting.visuals.renderableSeries.data.LineRenderPassData
import com.scichart.charting.visuals.rendering.RenderPassState
import com.scichart.core.framework.UpdateSuspender
import com.scichart.core.utility.Dispatcher
import com.scichart.data.model.DoubleRange
import com.scichart.data.model.ISciList
import com.scichart.data.numerics.ResamplingMode
import com.scichart.data.numerics.pointresamplers.IPointResamplerFactory
import com.scichart.drawing.common.IAssetManager2D
import com.scichart.examples.R
import com.scichart.examples.fragments.base.ExampleSingleChartBaseFragment
import com.scichart.examples.utils.scichartExtensions.*
import com.scichart.examples.utils.widgetgeneration.ImageViewWidget
import com.scichart.examples.utils.widgetgeneration.Widget
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit

class AnimatingLineChartFragment : ExampleSingleChartBaseFragment() {

    private val scheduledExecutorService = Executors.newSingleThreadScheduledExecutor()
    private lateinit var schedule: ScheduledFuture<*>

    private val ds1 = XyDataSeries<Double, Double>().apply { fifoCapacity = FIFO_CAPACITY }
    private val xVisibleRange = DoubleRange(-GROW_BY, VISIBLE_RANGE_MAX + GROW_BY)
    private var t = 0.0
    private var yValue = 0.0

    @Volatile
    private var isRunning = true

    override fun getToolbarItems(): List<Widget> = ArrayList<Widget>().apply {
        add(ImageViewWidget.Builder().setId(R.drawable.example_toolbar_play).setListener { isRunning = true }.build())
        add(ImageViewWidget.Builder().setId(R.drawable.example_toolbar_pause).setListener { isRunning = false }.build())
        add(ImageViewWidget.Builder().setId(R.drawable.example_toolbar_stop).setListener {
            isRunning = false
            UpdateSuspender.using(binding.surface) { ds1.clear() }
        }.build())
    }

    override fun initExample(surface: SciChartSurface) {
        surface.suspendUpdates {
            xAxes { numericAxis { visibleRange = xVisibleRange; autoRange = Never } }
            yAxes { numericAxis { growBy = DoubleRange(0.1, 0.1); autoRange = Always } }

            renderableSeries {
                rSeries(AnimatingLineRenderableSeries().apply {
                    dataSeries = ds1
                    strokeStyle = SolidPenStyle(0xFF4083B7, 3f)
                })
            }
        }

        schedule = scheduledExecutorService.scheduleWithFixedDelay(insertRunnable, 0, TIME_INTERVAL, TimeUnit.MILLISECONDS)
    }

    private val insertRunnable = Runnable {
        if (!isRunning) return@Runnable

        binding.surface.suspendUpdates {
            yValue += random.nextDouble() - 0.5
            ds1.append(t, yValue)

            t += ONE_OVER_TIME_INTERVAL
            if (t > VISIBLE_RANGE_MAX) {
                xVisibleRange.setMinMax(
                    xVisibleRange.min + ONE_OVER_TIME_INTERVAL,
                    xVisibleRange.max + ONE_OVER_TIME_INTERVAL
                )
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        isRunning = false
        outState.putDouble("time", t)
        outState.putDouble("yValue", yValue)
        outState.putDouble("xVisibleRangeMin", xVisibleRange.minAsDouble)
        outState.putDouble("xVisibleRangeMax", xVisibleRange.maxAsDouble)
        outState.putParcelable("xValues1", ds1.xValues)
        outState.putParcelable("yValues1", ds1.yValues)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        savedInstanceState?.run {
            t = getDouble("time")
            yValue = getDouble("yValue")
            val xVisibleRangeMin = getDouble("xVisibleRangeMin")
            val xVisibleRangeMax = getDouble("xVisibleRangeMax")
            xVisibleRange.setMinMaxDouble(xVisibleRangeMin, xVisibleRangeMax)
            val xValues1: ISciList<Double> = getParcelable("xValues1")!!
            val yValues1: ISciList<Double> = getParcelable("yValues1")!!
            ds1.append(xValues1, yValues1)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        schedule?.cancel(true)
    }

    private class AnimatingLineRenderableSeries : FastLineRenderableSeries(), Animator.AnimatorListener, AnimatorUpdateListener {
        private var fromX = 0.0
        private var fromY = 0.0
        private var toX = 0.0
        private var toY = 0.0

        private val animator = ValueAnimator.ofFloat(START_VALUE, END_VALUE).apply {
            interpolator = DecelerateInterpolator()
            duration = TIME_INTERVAL
            addUpdateListener(this@AnimatingLineRenderableSeries)
            addListener(this@AnimatingLineRenderableSeries)
        }

        @Volatile
        private var animatedFraction = 0f

        @Volatile
        private var isUpdatesAllowed = false

        @Throws(Exception::class)
        override fun internalUpdateRenderPassData(renderPassDataToUpdate: ISeriesRenderPassData, dataSeries: IDataSeries<*, *>?, resamplingMode: ResamplingMode, factory: IPointResamplerFactory) {
            super.internalUpdateRenderPassData(renderPassDataToUpdate, dataSeries, resamplingMode, factory)

            // can't animate series with less than 2 points
            if (renderPassDataToUpdate.pointsCount() < 2) return

            val lineRenderPassData = renderPassDataToUpdate as LineRenderPassData
            val xValues = lineRenderPassData.xValues
            val yValues = lineRenderPassData.yValues

            val pointsCount = lineRenderPassData.pointsCount()
            fromX = xValues[pointsCount - 2]
            fromY = yValues[pointsCount - 2]
            toX = xValues[pointsCount - 1]
            toY = yValues[pointsCount - 1]

            // need to replace last point to prevent jumping of line because
            // animation runs from UI thread so there could be delay with animation start
            // so chart may render original render pass data few times before animation starts
            xValues[pointsCount - 1] = fromX
            yValues[pointsCount - 1] = fromY

            // do not update render pass data until animation starts
            isUpdatesAllowed = false
            Dispatcher.postOnUiThread {
                if (animator.isRunning) {
                    animator.cancel()
                }
                animator.start()
            }
        }

        override fun internalUpdate(assetManager: IAssetManager2D, renderPassState: RenderPassState) {
            super.internalUpdate(assetManager, renderPassState)

            if (!isUpdatesAllowed) return

            val currentRenderPassData = currentRenderPassData as LineRenderPassData
            val x = fromX + (toX - fromX) * animatedFraction
            val y = interpolateLinear(x, fromX, fromY, toX, toY)

            val indexToSet = currentRenderPassData.pointsCount() - 1
            currentRenderPassData.xValues[indexToSet] = x
            currentRenderPassData.yValues[indexToSet] = y

            val xCoord = currentRenderPassData.xCoordinateCalculator.getCoordinate(x)
            val yCoord = currentRenderPassData.yCoordinateCalculator.getCoordinate(y)

            currentRenderPassData.xCoords[indexToSet] = xCoord
            currentRenderPassData.yCoords[indexToSet] = yCoord
        }

        override fun onAnimationStart(animation: Animator) {
            // allow updated of render pass data after animation starts
            isUpdatesAllowed = true

            animatedFraction = START_VALUE
            invalidateElement()
        }

        override fun onAnimationEnd(animation: Animator) {
            animatedFraction = END_VALUE
            invalidateElement()
        }

        override fun onAnimationCancel(animation: Animator) {
            animatedFraction = START_VALUE
            invalidateElement()
        }

        override fun onAnimationRepeat(animation: Animator) {}

        override fun onAnimationUpdate(animation: ValueAnimator) {
            animatedFraction = animation.animatedFraction
            invalidateElement()
        }

        init {
            animator.interpolator = DecelerateInterpolator()
            animator.duration = TIME_INTERVAL
            animator.addUpdateListener(this)
            animator.addListener(this)
        }

        private fun interpolateLinear(x: Double, x1: Double, y1: Double, x2: Double, y2: Double): Double {
            return y1 + (y2 - y1) * (x - x1) / (x2 - x1)
        }

        companion object {
            private const val START_VALUE = 0f
            private const val END_VALUE = 1f
        }
    }

    companion object {
        private const val FIFO_CAPACITY = 50
        private const val TIME_INTERVAL: Long = 1000
        private const val ONE_OVER_TIME_INTERVAL = 1.0 / TIME_INTERVAL
        private const val VISIBLE_RANGE_MAX = FIFO_CAPACITY * ONE_OVER_TIME_INTERVAL
        private const val GROW_BY = VISIBLE_RANGE_MAX * 0.1

        private val random = Random()
    }
}