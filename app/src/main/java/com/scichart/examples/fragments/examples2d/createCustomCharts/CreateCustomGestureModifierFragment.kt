//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2021. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// CreateCustomGestureModifierFragment.kt is part of SCICHART®, High Performance Scientific Charts
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

import android.graphics.PointF
import android.view.MotionEvent
import android.view.ViewConfiguration
import android.view.animation.DecelerateInterpolator
import com.scichart.charting.modifiers.GestureModifierBase
import com.scichart.charting.visuals.SciChartSurface
import com.scichart.charting.visuals.annotations.AnnotationCoordinateMode.Relative
import com.scichart.charting.visuals.annotations.HorizontalAnchorPoint.Center
import com.scichart.charting.visuals.annotations.VerticalAnchorPoint.Top
import com.scichart.charting.visuals.axes.IAxis
import com.scichart.core.IServiceContainer
import com.scichart.core.utility.touch.ModifierTouchEventArgs
import com.scichart.data.model.DoubleRange
import com.scichart.examples.data.DataManager
import com.scichart.examples.fragments.base.ExampleSingleChartBaseFragment
import com.scichart.examples.utils.scichartExtensions.*
import kotlin.math.abs

class CreateCustomGestureModifierFragment : ExampleSingleChartBaseFragment() {
    override fun initExample(surface: SciChartSurface) {
        val ds1Points = DataManager.getInstance().getDampedSinewave(1.0, 0.05, 50, 5)

        surface.suspendUpdates {
            xAxes { numericAxis { growBy = DoubleRange(0.1, 0.1) } }
            yAxes { numericAxis { growBy = DoubleRange(0.2, 0.2) } }

            renderableSeries {
                fastImpulseRenderableSeries {
                    xyDataSeries<Double, Double> {
                        append(ds1Points.xValues, ds1Points.yValues)
                    }
                    ellipsePointMarker {
                        setSize(10)
                        strokeStyle = SolidPenStyle(0xFF0066FF)
                        fillStyle = SolidBrushStyle(0xFF0066FF)
                    }
                    strokeStyle = SolidPenStyle(0xFF0066FF)

                    waveAnimation { interpolator = DecelerateInterpolator() }
                }
            }

            chartModifiers { modifier(CustomZoomGestureModifier()) }
            annotations {
                textAnnotation {
                    x1 = 0.5; y1 = 0.0
                    coordinateMode = Relative
                    verticalAnchorPoint = Top
                    horizontalAnchorPoint = Center
                    text = "Double Tap and pan vertically to Zoom In/Out.\nDouble tap to Zoom Extents."
                }
            }
        }
    }

    private class CustomZoomGestureModifier : GestureModifierBase() {
        private var isScrolling = false
        private var isZoomEnabled = false

        private var touchSlop = 0f
        private val start = PointF()
        private var lastY = 0f

        override fun attachTo(services: IServiceContainer) {
            super.attachTo(services)

            val context = context ?: return
            touchSlop = (ViewConfiguration.get(context).scaledTouchSlop * 2).toFloat()
        }

        override fun onDoubleTap(e: MotionEvent): Boolean {
            start.set(e.x, e.y)
            lastY = e.y
            isZoomEnabled = true

            return true
        }

        override fun onTouch(args: ModifierTouchEventArgs) {
            super.onTouch(args)

            val motionEvent = args.e
            if (isZoomEnabled && motionEvent.action == MotionEvent.ACTION_MOVE) {
                onScrollInYDirection(motionEvent.y)
            }
        }

        private fun onScrollInYDirection(y: Float) {
            val distance = abs(y - start.y)
            if (distance < touchSlop || abs(y - lastY) < 1f) return

            isScrolling = true

            val prevDistance = abs(lastY - start.y)
            val diff = if (prevDistance > 0) (distance / prevDistance - 1).toDouble() else 0.toDouble()
            growBy(start, xAxis, diff)

            lastY = y
        }

        // zoom axis relative to the start point using fraction
        private fun growBy(point: PointF, axis: IAxis, fraction: Double) {
            val size = axis.axisViewportDimension
            val coord = size - point.y

            val minFraction = coord / size * fraction
            val maxFraction = (1 - coord / size) * fraction

            axis.zoomBy(minFraction, maxFraction)
        }

        override fun onUp(e: MotionEvent) {
            // need to disable zoom after finishing scrolling
            if (isScrolling) {
                isZoomEnabled = false
                isScrolling = isZoomEnabled
                start[Float.NaN] = Float.NaN
                lastY = Float.NaN
            }
        }
    }
}