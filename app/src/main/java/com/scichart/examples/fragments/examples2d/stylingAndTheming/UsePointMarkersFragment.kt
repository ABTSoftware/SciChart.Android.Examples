//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2021. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// UsePointMarkersFragment.kt is part of SCICHART®, High Performance Scientific Charts
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

package com.scichart.examples.fragments.examples2d.stylingAndTheming.kt

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import androidx.annotation.DrawableRes
import androidx.core.content.res.ResourcesCompat
import com.scichart.charting.model.dataSeries.IXyDataSeries
import com.scichart.charting.visuals.SciChartSurface
import com.scichart.charting.visuals.pointmarkers.SpritePointMarker.ISpritePointMarkerDrawer
import com.scichart.data.model.DoubleRange
import com.scichart.drawing.utility.ColorUtil
import com.scichart.examples.R
import com.scichart.examples.fragments.base.ExampleSingleChartBaseFragment
import com.scichart.examples.utils.Constant
import com.scichart.examples.utils.scichartExtensions.*
import java.util.*

class UsePointMarkersFragment : ExampleSingleChartBaseFragment() {
    override fun initExample(surface: SciChartSurface) {
        surface.suspendUpdates {
            xAxes { numericAxis { growBy = DoubleRange(0.1, 0.1) } }
            yAxes { numericAxis { growBy = DoubleRange(0.1, 0.1) } }

            renderableSeries {
                fastLineRenderableSeries {
                    xyDataSeries<Double, Double> { fillDataSeries(this) }
                    strokeStyle = SolidPenStyle(ColorUtil.LightBlue, 2f)
                    ellipsePointMarker {
                        setSize(15)
                        fillStyle = SolidBrushStyle(0x990077ff)
                        strokeStyle = SolidPenStyle(ColorUtil.LightBlue, 2f)
                    }

                    opacityAnimation {
                        startDelay = Constant.ANIMATION_START_DELAY
                        duration = Constant.ANIMATION_DURATION
                    }
                }
                fastLineRenderableSeries {
                    xyDataSeries<Double, Double> { fillDataSeries(this, 1.0) }
                    strokeStyle = SolidPenStyle(ColorUtil.Red, 2f)
                    squarePointMarker {
                        setSize(20)
                        fillStyle = SolidBrushStyle(0x99ff0000)
                        strokeStyle = SolidPenStyle(ColorUtil.Red, 2f)
                    }

                    opacityAnimation {
                        startDelay = Constant.ANIMATION_START_DELAY
                        duration = Constant.ANIMATION_DURATION
                    }
                }
                fastLineRenderableSeries {
                    xyDataSeries<Double, Double> { fillDataSeries(this, 2.5) }
                    strokeStyle = SolidPenStyle(ColorUtil.Yellow, 2f)
                    trianglePointMarker {
                        setSize(20)
                        fillStyle = SolidBrushStyle(0xffffdd00)
                        strokeStyle = SolidPenStyle(0xffff6600, 2f)
                    }

                    opacityAnimation {
                        startDelay = Constant.ANIMATION_START_DELAY
                        duration = Constant.ANIMATION_DURATION
                    }
                }
                fastLineRenderableSeries {
                    xyDataSeries<Double, Double> { fillDataSeries(this, 4.0) }
                    strokeStyle = SolidPenStyle(ColorUtil.Magenta, 2f)
                    crossPointMarker {
                        setSize(25)
                        strokeStyle = SolidPenStyle(ColorUtil.Magenta, 4f)
                    }

                    opacityAnimation {
                        startDelay = Constant.ANIMATION_START_DELAY
                        duration = Constant.ANIMATION_DURATION
                    }
                }
                fastLineRenderableSeries {
                    xyDataSeries<Double, Double> { fillDataSeries(this, 5.5) }
                    strokeStyle = SolidPenStyle(ColorUtil.Wheat, 2f)
                    spritePointMarker(CustomPointMarkerDrawer(this@suspendUpdates.context, R.drawable.example_weather_storm)) {
                        setSize(15)
                        fillStyle = SolidBrushStyle(0x990077ff)
                        strokeStyle = SolidPenStyle(ColorUtil.LightBlue, 2f)
                    }

                    opacityAnimation {
                        startDelay = Constant.ANIMATION_START_DELAY
                        duration = Constant.ANIMATION_DURATION
                    }
                }
            }
            chartModifiers { defaultModifiers() }
        }
    }

    private fun fillDataSeries(dataSeries: IXyDataSeries<Double, Double>, offset: Double = 0.0) {
        for (i in 0 until dataSize) {
            dataSeries.append(i.toDouble(), offset + rnd.nextDouble())
        }
        dataSeries.updateYAt(7, Double.NaN)
    }

    private class CustomPointMarkerDrawer(context: Context, @DrawableRes drawableId: Int) : ISpritePointMarkerDrawer {
        private val drawable = ResourcesCompat.getDrawable(context.resources, drawableId, null)
        override fun onDraw(canvas: Canvas, stroke: Paint, fill: Paint) {
            drawable?.run {
                setBounds(0, 0, canvas.width, canvas.height)
                draw(canvas)
            }
        }
    }

    companion object {
        val rnd = Random()
        const val dataSize = 15
    }
}