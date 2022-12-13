//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2021. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// HitTestDataPointsFragment.kt is part of SCICHART®, High Performance Scientific Charts
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

package com.scichart.examples.fragments.examples2d.tooltipsAndHitTest.kt

import android.annotation.SuppressLint
import android.graphics.PointF
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.Toast
import com.scichart.charting.visuals.SciChartSurface
import com.scichart.charting.visuals.axes.AxisAlignment.Bottom
import com.scichart.charting.visuals.axes.AxisAlignment.Left
import com.scichart.charting.visuals.renderableSeries.hitTest.HitTestInfo
import com.scichart.data.model.DoubleRange
import com.scichart.drawing.utility.ColorUtil
import com.scichart.examples.R
import com.scichart.examples.fragments.base.ExampleSingleChartBaseFragment
import com.scichart.examples.utils.scichartExtensions.*

@SuppressLint("ClickableViewAccessibility")
class HitTestDataPointsFragment: ExampleSingleChartBaseFragment(), View.OnTouchListener {
    private var toast: Toast? = null

    private val touchPoint = PointF()
    private val hitTestInfo = HitTestInfo()

    override fun initExample(surface: SciChartSurface) {
        surface.theme = R.style.SciChart_NavyBlue

        val xValues = listOf(0.0, 1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0)
        val yValues = listOf(0.0, 0.1, 0.3, 0.5, 0.4, 0.35, 0.3, 0.25, 0.2, 0.1, 0.05)

        surface.suspendUpdates {
            xAxes { numericAxis { axisAlignment = Bottom } }
            yAxes { numericAxis { axisAlignment = Left; growBy = DoubleRange(0.0, 0.1) } }

            renderableSeries {
                fastLineRenderableSeries {
                    xyDataSeries<Double, Double>("Line Series") {
                        append(xValues, yValues)
                    }
                    strokeStyle = SolidPenStyle(ColorUtil.SteelBlue, 3f)
                    ellipsePointMarker {
                        setSize(30)
                        fillStyle = SolidBrushStyle(ColorUtil.SteelBlue)
                        strokeStyle = SolidPenStyle(ColorUtil.Lavender, 2f)
                    }

                    scaleAnimation { interpolator = DecelerateInterpolator() }
                }
                fastMountainRenderableSeries {
                    xyDataSeries<Double, Double>("Mountain Series") {
                        append(xValues, yValues.map { it * 0.7 })
                    }
                    areaStyle = SolidBrushStyle(ColorUtil.LightSteelBlue)
                    strokeStyle = SolidPenStyle(ColorUtil.SteelBlue, 2f)

                    scaleAnimation { interpolator = DecelerateInterpolator() }
                }
                fastColumnRenderableSeries {
                    xyDataSeries<Double, Double>("Column Series") {
                        append(xValues, yValues.map { it * 0.5 })
                    }

                    scaleAnimation { interpolator = DecelerateInterpolator() }
                }
                fastCandlestickRenderableSeries {
                    ohlcDataSeries<Double, Double>("Candlestick Series") {
                        append(
                            xValues,
                            yValues.map { it + 0.5 },
                            yValues.map { it + 1.0 },
                            yValues.map { it + 0.3 },
                            yValues.map { it + 0.7 }
                        )
                    }

                    scaleAnimation { interpolator = DecelerateInterpolator(); zeroLine = 0.3 }
                }
            }
        }

        surface.setOnTouchListener(this)
    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        val surface = v as SciChartSurface

        event?.run { touchPoint.set(x, y) }
        surface.translatePoint(touchPoint, surface.renderableSeriesArea)

        val stringBuilder = StringBuilder()

        stringBuilder.append(String.format("Touch at: (%.1f, %.1f)", touchPoint.x, touchPoint.y))

        for (renderableSeries in surface.renderableSeries) {
            renderableSeries.hitTest(hitTestInfo, touchPoint.x, touchPoint.y, 30f)
            stringBuilder.append(String.format("\n%s - %s", renderableSeries.javaClass.simpleName, java.lang.Boolean.toString(hitTestInfo.isHit)))
        }

        toast?.run { cancel() }

        toast = Toast.makeText(activity, null, Toast.LENGTH_SHORT).apply {
            setGravity(Gravity.TOP or Gravity.CENTER, 0, 0)
            setText(stringBuilder.toString())
            show()
        }

        return true
    }
}