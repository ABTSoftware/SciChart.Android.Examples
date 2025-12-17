//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2021. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// CustomModifierFragment.kt is part of SCICHART®, High Performance Scientific Charts
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

package com.scichart.examples.fragments.examples2d.chartAnnotations.kt

import android.graphics.Color
import android.graphics.PointF
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.AdapterView
import android.widget.Spinner
import com.scichart.charting.model.AnnotationCollection
import com.scichart.charting.model.dataSeries.OhlcDataSeries
import com.scichart.charting.modifiers.AnnotationCreationModifier
import com.scichart.charting.modifiers.DefaultAnnotationFactory
import com.scichart.charting.modifiers.GestureModifierBase
import com.scichart.charting.modifiers.OnAnnotationCreatedListener
import com.scichart.charting.visuals.SciChartSurface
import com.scichart.charting.visuals.annotations.AnnotationCoordinateMode
import com.scichart.charting.visuals.annotations.HorizontalAnchorPoint
import com.scichart.charting.visuals.annotations.IAnnotation
import com.scichart.charting.visuals.annotations.TextAnnotation
import com.scichart.charting.visuals.annotations.TradeMarkerAnnotation
import com.scichart.charting.visuals.annotations.VerticalAnchorPoint
import com.scichart.charting.visuals.axes.IAxis
import com.scichart.core.framework.IUpdateSuspender
import com.scichart.core.utility.ViewUtil
import com.scichart.data.model.DoubleRange
import com.scichart.examples.components.SpinnerStringAdapter
import com.scichart.examples.data.MarketDataService
import com.scichart.examples.data.PriceSeries
import com.scichart.examples.databinding.ExampleCreateAnnotationsDynamicallyFragmentBinding
import com.scichart.examples.fragments.base.ExampleBaseFragment
import com.scichart.examples.utils.ItemSelectedListenerBase
import com.scichart.examples.utils.scichartExtensions.*
import java.util.*
import kotlin.math.max
import kotlin.math.min

class CustomModifierFragment : ExampleBaseFragment<ExampleCreateAnnotationsDynamicallyFragmentBinding>(), OnAnnotationCreatedListener {

    private val annotationCreationModifier = AnnotationCreationModifier()
    private var tradeMarkerModifier: TradeMarkerGestureModifier? = null
    private var instructionAnnotation: TextAnnotation? = null
    private val divAnnotations = mutableListOf<TextAnnotation>()
    private var selectedAnnotationType = 0 // 0=Box, 1=Line, 2=TradeMarker

    override fun inflateBinding(inflater: LayoutInflater): ExampleCreateAnnotationsDynamicallyFragmentBinding {
        return ExampleCreateAnnotationsDynamicallyFragmentBinding.inflate(inflater)
    }

    override fun initExample(binding: ExampleCreateAnnotationsDynamicallyFragmentBinding) {
        val annotationTypeSelector: Spinner = binding.annotationTypeSelector
        
        // Create custom array with Box, Line, and Trade Marker
        val annotationTypes: List<String> = listOf("BoxAnnotation", "LineAnnotation", "TradeMarker")
        annotationTypeSelector.adapter = SpinnerStringAdapter(activity, annotationTypes)
        annotationTypeSelector.setSelection(0)
        
        annotationTypeSelector.onItemSelectedListener = object : ItemSelectedListenerBase() {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                selectedAnnotationType = position
                updateModifiers(binding.surface)
            }
        }

        val ohlcDataSeries: OhlcDataSeries<Date, Double> = sciChartBuilder.newOhlcDataSeries(Date::class.java, Double::class.javaObjectType).build()

        val marketDataService = MarketDataService(Calendar.getInstance().time, 5, 5)
        val data: PriceSeries = marketDataService.getHistoricalData(200)

        ohlcDataSeries.append(data.dateData, data.openData, data.highData, data.lowData, data.closeData)

        val surface: SciChartSurface = binding.surface
        
        // Use a subset of data for better zoom - show last 50 points
        val totalPoints = ohlcDataSeries.count
        val startIndex = max(0, totalPoints - 50)
        val visibleStart = startIndex.toDouble()
        val visibleEnd = totalPoints.toDouble()
        
        // Create divergence indicator data for mountain series
        val divAnnotationXPositions = mutableListOf<Int>()
        
        // Calculate Y-axis range from visible data for better zoom
        var minPrice = Double.MAX_VALUE
        var maxPrice = Double.MIN_VALUE
        for (i in startIndex until totalPoints) {
            val low = ohlcDataSeries.lowValues[i]
            val high = ohlcDataSeries.highValues[i]
            minPrice = min(minPrice, low)
            maxPrice = max(maxPrice, high)
        }
        // Add some padding
        val priceRange = maxPrice - minPrice
        val yMin = minPrice - priceRange * 0.1
        val yMax = maxPrice + priceRange * 0.1
        
        // Mark positions for "Div" annotations
        for (i in startIndex until totalPoints) {
            if (i % 25 == 0) {
                divAnnotationXPositions.add(i)
            }
        }
        
        surface.suspendUpdates {
            xAxes {
                categoryDateAxis {
                    visibleRange = DoubleRange(visibleStart, visibleEnd)
                    growBy = DoubleRange(0.0, 0.1)
                }
            }
            yAxes {
                // Primary Y-axis for candlestick chart (default axis)
                numericAxis {
                    visibleRange = DoubleRange(yMin, yMax)
                    growBy = DoubleRange(0.0, 0.1)
                }
            }
            renderableSeries {
                // Candlestick series (uses default Y-axis)
                fastCandlestickRenderableSeries {
                    dataSeries = ohlcDataSeries
                    opacity = 0.4f
                }
            }
        }
        
        // Add "Div" text annotations at specific X positions
        // Using RelativeY coordinate mode: X follows data, Y is relative (0-1)
        for (xPos in divAnnotationXPositions) {
            val divAnnotation = sciChartBuilder.newTextAnnotation()
                    .withX1(xPos)
                    .withY1(0.95) // Position near bottom (relative Y coordinate)
                    .withCoordinateMode(AnnotationCoordinateMode.RelativeY)
                    .withText("Div")
                    .withFontStyle(12f, Color.WHITE)
                    .withVerticalAnchorPoint(VerticalAnchorPoint.Bottom)
                    .withHorizontalAnchorPoint(HorizontalAnchorPoint.Center)
                    .build()
            divAnnotations.add(divAnnotation)
            surface.annotations.add(divAnnotation)
        }

        // Setup annotation factory for Box and Line
        val annotationFactory = DefaultAnnotationFactory()
        annotationFactory.setFactoryForAnnotationType(DefaultAnnotationFactory.BOX_ANNOTATION) { parentSurface, annotationType -> 
            sciChartBuilder.newBoxAnnotation().withBackgroundColor(0x6600cc00).build()
        }
        annotationFactory.setFactoryForAnnotationType(DefaultAnnotationFactory.LINE_ANNOTATION) { parentSurface, annotationType -> 
            sciChartBuilder.newLineAnnotation().build()
        }

        annotationCreationModifier.annotationFactory = annotationFactory
        annotationCreationModifier.setAnnotationCreationListener(this)

        // Create trade marker modifier
        tradeMarkerModifier = TradeMarkerGestureModifier()
        tradeMarkerModifier?.setAnnotationCreationListener(this)

        updateModifiers(surface)

        binding.deleteAnnotation.setOnClickListener { v ->
            val annotations: AnnotationCollection = surface.annotations
            for (i in annotations.size - 1 downTo 0) {
                val annotation: IAnnotation = annotations[i]
                if (annotation.isSelected) {
                    annotations.removeAt(i)
                }
            }
        }
    }

    private fun updateModifiers(surface: SciChartSurface) {
        // Remove all modifiers first
        surface.chartModifiers.clear()
        
        // Remove instruction annotation if exists
        if (instructionAnnotation != null) {
            surface.annotations.remove(instructionAnnotation)
            instructionAnnotation = null
        }

        if (selectedAnnotationType == 2) {
            // Trade Marker selected - use gesture modifier and show instructions
            surface.chartModifiers.add(sciChartBuilder.newModifierGroup().withModifier(tradeMarkerModifier!!).build())
            showTradeMarkerInstructions(surface)
        } else {
            // Box or Line selected - use annotation creation modifier
            annotationCreationModifier.annotationType = if (selectedAnnotationType == 0) DefaultAnnotationFactory.BOX_ANNOTATION else DefaultAnnotationFactory.LINE_ANNOTATION
            surface.chartModifiers.add(sciChartBuilder.newModifierGroup().withModifier(annotationCreationModifier).build())
        }
    }

    private fun showTradeMarkerInstructions(surface: SciChartSurface) {
        instructionAnnotation = sciChartBuilder.newTextAnnotation()
                .withX1(0.01)
                .withY1(0.05)
                .withCoordinateMode(AnnotationCoordinateMode.Relative)
                .withText("- Tap to place a Buy marker\n- Double-tap to place a Sell marker")
                .withFontStyle(14f, Color.WHITE)
                .withVerticalAnchorPoint(VerticalAnchorPoint.Top)
                .withHorizontalAnchorPoint(HorizontalAnchorPoint.Left)
                .build()
        
        val suspender: IUpdateSuspender = surface.suspendUpdates()
        try {
            surface.annotations.add(instructionAnnotation)
        } finally {
            suspender.dispose()
        }
    }

    override fun onAnnotationCreated(newAnnotation: IAnnotation) {
        newAnnotation.setIsEditable(true)
    }

    /**
     * Gesture modifier for creating trade markers with tap (buy) and double-tap (sell)
     */
    private inner class TradeMarkerGestureModifier : GestureModifierBase() {
        private val touchPoint = PointF()
        private var listener: OnAnnotationCreatedListener? = null

        fun setAnnotationCreationListener(listener: OnAnnotationCreatedListener?) {
            this.listener = listener
        }

        override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
            createTradeMarker(e, true)
            return true
        }

        override fun onDoubleTap(e: MotionEvent): Boolean {
            createTradeMarker(e, false)
            return true
        }

        private fun createTradeMarker(e: MotionEvent, isBuy: Boolean) {
            val parentSurface = parentSurface
            if (parentSurface == null || parentSurface !is SciChartSurface) return

            val surface = parentSurface

            // Convert touch coordinates to chart coordinates
            touchPoint.set(e.x, e.y)
            ViewUtil.translatePoint(surface.view, touchPoint, modifierSurface)

            // Get axes (use default axes)
            val xAxis: IAxis = surface.xAxes[0]
            val yAxis: IAxis = surface.yAxes[0]

            // Convert to data coordinates
            val x: Float = if (xAxis.isHorizontalAxis) {
                touchPoint.x
            } else {
                touchPoint.y
            }

            // Get the raw X value from touch
            val rawXValue = xAxis.getDataValue(x)

            // Round to nearest integer to snap to candlestick center
            val xIndex = kotlin.math.round((rawXValue as? Number)?.toFloat() ?: 0f).toInt()

            // Use the rounded index as the final X value
            val xValue: Comparable<*> = xIndex

            // Get the candlestick data series from the renderable series
            val ohlcDataSeries = surface.renderableSeries
                    .firstOrNull { it.dataSeries is OhlcDataSeries<*, *> }
                    ?.dataSeries as? OhlcDataSeries<*, *>

            // Get the Y value from the candlestick (low for buy, high for sell)
            val yValue: Comparable<*> = if (ohlcDataSeries != null) {
                // Clamp index to valid range
                val clampedIndex = max(0, min(xIndex, ohlcDataSeries.count - 1))

                // Get low value for buy markers (bottom of candlestick) or high value for sell markers (top of candlestick)
                if (isBuy) {
                    ohlcDataSeries.lowValues[clampedIndex] as Comparable<*>
                } else {
                    ohlcDataSeries.highValues[clampedIndex] as Comparable<*>
                }
            } else {
                // Fallback: use Y coordinate from touch if no data series found
                val y: Float = if (xAxis.isHorizontalAxis) touchPoint.y else touchPoint.x
                yAxis.getDataValue(y)
            }

            // Create trade marker
            val marker = TradeMarkerAnnotation(
                    surface.context,
                    xValue,
                    yValue,
                    isBuy/*,
                    quantity,
                    price,
                    change*/
            )

            // Add to surface
            val suspender: IUpdateSuspender = surface.suspendUpdates()
            try {
                surface.annotations.add(marker)
            } finally {
                suspender.dispose()
            }

            // Notify listener
            listener?.onAnnotationCreated(marker)
        }
    }
}
