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

import android.animation.Animator
import android.animation.FloatEvaluator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.animation.DecelerateInterpolator
import com.scichart.charting.model.dataSeries.XyDataSeries
import com.scichart.charting.visuals.animations.AnimationsHelper
import com.scichart.charting.visuals.animations.BaseRenderPassDataTransformation
import com.scichart.charting.visuals.animations.TransformationHelpers
import com.scichart.charting.visuals.renderableSeries.StackedColumnRenderableSeries
import com.scichart.charting.visuals.renderableSeries.data.StackedColumnRenderPassData
import com.scichart.core.model.FloatValues
import com.scichart.data.model.DoubleRange
import com.scichart.data.model.ISciList
import com.scichart.examples.R
import com.scichart.examples.databinding.ExampleAnimatingStackedColumnChartFragmentBinding
import com.scichart.examples.fragments.base.ExampleBaseFragment
import com.scichart.examples.utils.scichartExtensions.*
import com.scichart.examples.utils.widgetgeneration.ImageViewWidget
import com.scichart.examples.utils.widgetgeneration.Widget
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit

class AnimatingStackedColumnChartFragment : ExampleBaseFragment<ExampleAnimatingStackedColumnChartFragmentBinding>() {
    private val scheduledExecutorService = Executors.newSingleThreadScheduledExecutor()
    private lateinit var schedule: ScheduledFuture<*>

    @Volatile
    private var isRunning = true

    private val dataSeries1 = XyDataSeries<Double, Double>()
    private val dataSeries2 = XyDataSeries<Double, Double>()

    private val rSeries1 = StackedColumnRenderableSeries()
    private val rSeries2 = StackedColumnRenderableSeries()

    private var animator1 = createAnimator(rSeries1)
    private var animator2 = createAnimator(rSeries2)

    override fun inflateBinding(inflater: LayoutInflater): ExampleAnimatingStackedColumnChartFragmentBinding {
        return ExampleAnimatingStackedColumnChartFragmentBinding.inflate(inflater)
    }

    override fun showDefaultModifiersInToolbar(): Boolean { return false }

    override fun getToolbarItems(): List<Widget> = ArrayList<Widget>().apply {
        add(ImageViewWidget.Builder().setId(R.drawable.example_toolbar_play).setListener { isRunning = true }.build())
        add(ImageViewWidget.Builder().setId(R.drawable.example_toolbar_pause).setListener { isRunning = false }.build())
    }

    override fun initExample(binding: ExampleAnimatingStackedColumnChartFragmentBinding) {
        binding.refreshData.setOnClickListener {
            if (isRunning) {
                if (schedule != null) {
                    schedule.cancel(true)
                }
                schedule = createSchedule()
            } else {
                refreshData()
            }
        }

        configureRenderableSeries(rSeries1, dataSeries1, 0xff47bde6)
        configureRenderableSeries(rSeries2, dataSeries2, 0xffae418d)

        fillWithInitialData()

        binding.surface.suspendUpdates {
            xAxes { numericAxis { growBy = DoubleRange(0.1, 0.1) } }
            yAxes {
                numericAxis {
                    growBy = DoubleRange(0.1, 0.1)
                    visibleRange = DoubleRange(0.0, MAX_Y_VALUE * 2)
                }
            }
            renderableSeries {
                verticallyStackedColumnsCollection {
                    add(rSeries1)
                    add(rSeries2)
                }
            }
        }

        schedule = createSchedule()
    }

    private fun configureRenderableSeries(rSeries: StackedColumnRenderableSeries, dataSeries: XyDataSeries<*, *>, fillColor: Long) {
        rSeries.dataSeries = dataSeries
        rSeries.fillBrushStyle = SolidBrushStyle(fillColor)
        rSeries.strokeStyle = SolidPenStyle(fillColor, 1f)
    }

    private fun fillWithInitialData() {
        binding.surface.suspendUpdates {
            for (i in 0 until X_VALUES_COUNT) {
                dataSeries1.append(i.toDouble(), getRandomYValue())
                dataSeries2.append(i.toDouble(), getRandomYValue())
            }
        }
    }

    private fun createSchedule() : ScheduledFuture<*> {
        return scheduledExecutorService.scheduleWithFixedDelay(insertRunnable, 0, TIME_INTERVAL, TimeUnit.MILLISECONDS)
    }

    private val insertRunnable = Runnable {
        if (!isRunning) return@Runnable

        refreshData()
    }

    private fun refreshData() {
        activity?.runOnUiThread {
            animator1.cancel()
            animator2.cancel()

            binding.surface.suspendUpdates {
                for (i in 0 until X_VALUES_COUNT) {
                    dataSeries1.updateYAt(i, getRandomYValue())
                    dataSeries2.updateYAt(i, getRandomYValue())
                }
            }

            animator1.start()
            animator2.start()
        }
    }

    private fun getRandomYValue() : Double {
        return random.nextDouble() * MAX_Y_VALUE
    }

    private fun createAnimator(rSeries: StackedColumnRenderableSeries): Animator {
        return AnimationsHelper.createAnimator(
            rSeries,
            UpdatedPointTransformation(),
            ANIMATION_DURATION,
            0,
            DecelerateInterpolator(),
            FloatEvaluator(),
            0f, 1f
        )
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        isRunning = false
        outState.putParcelable("xValues1", dataSeries1.xValues)
        outState.putParcelable("yValues1", dataSeries1.yValues)
        outState.putParcelable("xValues2", dataSeries2.xValues)
        outState.putParcelable("yValues2", dataSeries2.yValues)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        savedInstanceState?.run {
            val xValues1: ISciList<Double> = getParcelable("xValues1")!!
            val yValues1: ISciList<Double> = getParcelable("yValues1")!!
            dataSeries1.append(xValues1, yValues1)

            val xValues2: ISciList<Double> = getParcelable("xValues2")!!
            val yValues2: ISciList<Double> = getParcelable("yValues2")!!
            dataSeries2.append(xValues2, yValues2)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        schedule.cancel(true)
    }

    companion object {
        private const val TIME_INTERVAL: Long = 1000
        private const val ANIMATION_DURATION: Long = 500
        private const val X_VALUES_COUNT = 12
        private const val MAX_Y_VALUE = 100.0

        private val random = Random()
    }

    private class UpdatedPointTransformation: BaseRenderPassDataTransformation<StackedColumnRenderPassData>(StackedColumnRenderPassData::class.java) {
        private val startYCoordinates = FloatValues()
        private val startPrevSeriesYCoordinates = FloatValues()

        private val originalYCoordinates = FloatValues()
        private val originalPrevSeriesYCoordinates = FloatValues()

        override fun saveOriginalData() {
            if (!renderPassData.isValid) return

            TransformationHelpers.copyData(renderPassData.yCoords, originalYCoordinates)
            TransformationHelpers.copyData(renderPassData.prevSeriesYCoords, originalPrevSeriesYCoordinates)
        }

        override fun applyTransformation() {
            if (!renderPassData.isValid) return

            val count = renderPassData.pointsCount()

            if (startPrevSeriesYCoordinates.size() != count || startYCoordinates.size() != count ||
                originalYCoordinates.size() != count || originalPrevSeriesYCoordinates.size() != count
            ) return

            for (i in 0 until count) {
                val startYCoord = startYCoordinates.get(i)
                val originalYCoordinate = originalYCoordinates.get(i)
                val additionalY = startYCoord + (originalYCoordinate - startYCoord) * currentTransformationValue

                val startPrevSeriesYCoords = startPrevSeriesYCoordinates.get(i)
                val originalPrevSeriesYCoordinate = originalPrevSeriesYCoordinates.get(i)
                val additionalPrevSeriesY =
                    startPrevSeriesYCoords + (originalPrevSeriesYCoordinate - startPrevSeriesYCoords) * currentTransformationValue

                renderPassData.yCoords.set(i, additionalY)
                renderPassData.prevSeriesYCoords.set(i, additionalPrevSeriesY)
            }
        }

        override fun discardTransformation() {
            TransformationHelpers.copyData(originalYCoordinates, renderPassData.yCoords)
            TransformationHelpers.copyData(originalPrevSeriesYCoordinates, renderPassData.prevSeriesYCoords)
        }

        override fun onInternalRenderPassDataChanged() {
            applyTransformation()
        }

        override fun onAnimationEnd() {
            super.onAnimationEnd()

            TransformationHelpers.copyData(originalYCoordinates, startYCoordinates)
            TransformationHelpers.copyData(originalPrevSeriesYCoordinates, startPrevSeriesYCoordinates)
        }
    }
}
