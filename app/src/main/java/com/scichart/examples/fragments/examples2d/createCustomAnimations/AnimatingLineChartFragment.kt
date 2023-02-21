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

package com.scichart.examples.fragments.examples2d.createCustomAnimations.kt

import android.animation.FloatEvaluator
import android.os.Bundle
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.DecelerateInterpolator
import com.scichart.charting.visuals.SciChartSurface
import com.scichart.charting.visuals.animations.AnimationsHelper
import com.scichart.charting.visuals.animations.BaseRenderPassDataTransformation
import com.scichart.charting.visuals.animations.TransformationHelpers
import com.scichart.charting.visuals.axes.AutoRange
import com.scichart.charting.visuals.renderableSeries.FastLineRenderableSeries
import com.scichart.charting.visuals.renderableSeries.data.LineRenderPassData
import com.scichart.core.framework.UpdateSuspender
import com.scichart.core.model.FloatValues
import com.scichart.data.model.DoubleRange
import com.scichart.data.model.ISciList
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

    @Volatile
    private var isRunning = true

    private val dataSeries = XyDataSeries<Double, Double>().apply { fifoCapacity = FIFO_CAPACITY }
    private val rSeries = FastLineRenderableSeries().apply {
        dataSeries = this@AnimatingLineChartFragment.dataSeries
        strokeStyle = SolidPenStyle(0xFF47bde6, 3f)
    }
    private val xVisibleRange = DoubleRange(-1.0, VISIBLE_RANGE_MAX)
    private var currentXValue = 0.0
    private var yValue = 0.0

    private val animator = AnimationsHelper.createAnimator(
        rSeries,
        AppendedPointTransformation(),
        ANIMATION_DURATION,
        0,
        AccelerateDecelerateInterpolator(),
        FloatEvaluator(),
        0f, 1f
    )

    override fun showDefaultModifiersInToolbar(): Boolean { return false }

    override fun getToolbarItems(): List<Widget> = ArrayList<Widget>().apply {
        add(ImageViewWidget.Builder().setId(R.drawable.example_toolbar_play).setListener { isRunning = true }.build())
        add(ImageViewWidget.Builder().setId(R.drawable.example_toolbar_pause).setListener { isRunning = false }.build())
        add(ImageViewWidget.Builder().setId(R.drawable.example_toolbar_stop).setListener {
            isRunning = false
            resetChart()
        }.build())
    }

    override fun initExample(surface: SciChartSurface) {
        surface.theme = R.style.SciChart_NavyBlue

        surface.suspendUpdates {
            xAxes {
                numericAxis {
                    autoRange = AutoRange.Never
                    visibleRange = xVisibleRange
                }
            }
            yAxes {
                numericAxis {
                    visibleRange = DoubleRange(0.0, MAX_Y_VALUE)
                    growBy = DoubleRange(0.1, 0.1)
                }
            }
            renderableSeries { rSeries(rSeries) }
        }

        addPointAnimated()
        schedule = scheduledExecutorService.scheduleWithFixedDelay(insertRunnable, 0, TIME_INTERVAL, TimeUnit.MILLISECONDS)
    }

    private val insertRunnable = Runnable {
        if (!isRunning) return@Runnable

        addPointAnimated()
    }

    private fun addPointAnimated() {
        activity?.runOnUiThread {
            animator.cancel()

            binding.surface.suspendUpdates {
                yValue = random.nextDouble() * MAX_Y_VALUE
                dataSeries.append(currentXValue, yValue)
            }

            animator.start()

            currentXValue += X_RANGE_STEP
            animateVisibleRangeIfNeeded()
        }
    }

    private fun animateVisibleRangeIfNeeded() {
        if (currentXValue > VISIBLE_RANGE_MAX) {
            val xAxis = binding.surface.xAxes[0]
            val newRange = DoubleRange(
                xAxis.visibleRange.minAsDouble + X_RANGE_STEP,
                xAxis.visibleRange.maxAsDouble + X_RANGE_STEP
            )
            xAxis.animateVisibleRangeTo(newRange, ANIMATION_DURATION)
        }
    }

    private fun resetChart() {
        UpdateSuspender.using(binding.surface) { dataSeries.clear() }
        currentXValue = 0.0
        binding.surface.xAxes.firstOrNull()?.animateVisibleRangeTo(DoubleRange(-1.0, VISIBLE_RANGE_MAX), ANIMATION_DURATION)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        isRunning = false
        outState.putDouble("currentXValue", currentXValue)
        outState.putDouble("yValue", yValue)
        outState.putDouble("xVisibleRangeMin", xVisibleRange.minAsDouble)
        outState.putDouble("xVisibleRangeMax", xVisibleRange.maxAsDouble)
        outState.putParcelable("xValues1", dataSeries.xValues)
        outState.putParcelable("yValues1", dataSeries.yValues)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        savedInstanceState?.run {
            currentXValue = getDouble("currentXValue")
            yValue = getDouble("yValue")
            val xVisibleRangeMin = getDouble("xVisibleRangeMin")
            val xVisibleRangeMax = getDouble("xVisibleRangeMax")
            xVisibleRange.setMinMaxDouble(xVisibleRangeMin, xVisibleRangeMax)
            val xValues1: ISciList<Double> = getParcelable("xValues1")!!
            val yValues1: ISciList<Double> = getParcelable("yValues1")!!
            dataSeries.append(xValues1, yValues1)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        schedule.cancel(true)
    }

    companion object {
        private const val FIFO_CAPACITY = 50
        private const val TIME_INTERVAL: Long = 400
        private const val ANIMATION_DURATION: Long = 200
        private const val X_RANGE_STEP = 1.0
        private const val VISIBLE_RANGE_MAX = 10.0
        private const val MAX_Y_VALUE = 100.0

        private val random = Random()
    }

    private class AppendedPointTransformation: BaseRenderPassDataTransformation<LineRenderPassData>(LineRenderPassData::class.java) {
        private val originalXCoordinates = FloatValues()
        private val originalYCoordinates = FloatValues()

        override fun saveOriginalData() {
            if (!renderPassData.isValid) return

            TransformationHelpers.copyData(renderPassData.xCoords, originalXCoordinates)
            TransformationHelpers.copyData(renderPassData.yCoords, originalYCoordinates)
        }

        override fun applyTransformation() {
            if (!renderPassData.isValid) return

            val count = renderPassData.pointsCount()

            val firstXStart = renderPassData.xCoordinateCalculator.getCoordinate(0.0)
            val xStart = if (count <= 1) firstXStart else originalXCoordinates.get(count - 2)
            val xFinish = originalXCoordinates.get(count - 1)
            val additionalX = xStart + (xFinish - xStart) * currentTransformationValue
            renderPassData.xCoords.set(count - 1, additionalX)

            val firstYStart = renderPassData.yCoordinateCalculator.getCoordinate(0.0)
            val yStart = if (count <= 1) firstYStart else originalYCoordinates.get(count - 2)
            val yFinish = originalYCoordinates.get(count - 1)
            val additionalY = yStart + (yFinish - yStart) * currentTransformationValue
            renderPassData.yCoords.set(count - 1, additionalY)
        }

        override fun discardTransformation() {
            TransformationHelpers.copyData(originalXCoordinates, renderPassData.xCoords)
            TransformationHelpers.copyData(originalYCoordinates, renderPassData.yCoords)
        }

        override fun onInternalRenderPassDataChanged() {
            applyTransformation()
        }
    }
}
