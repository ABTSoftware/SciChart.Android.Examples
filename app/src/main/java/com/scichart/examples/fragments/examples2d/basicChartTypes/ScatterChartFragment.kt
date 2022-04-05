//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2021. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// ScatterChartFragment.kt is part of SCICHART®, High Performance Scientific Charts
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

package com.scichart.examples.fragments.examples2d.basicChartTypes.kt

import android.view.animation.DecelerateInterpolator
import com.scichart.charting.modifiers.AxisDragModifierBase.AxisDragMode
import com.scichart.charting.visuals.SciChartSurface
import com.scichart.charting.visuals.pointmarkers.EllipsePointMarker
import com.scichart.charting.visuals.pointmarkers.IPointMarker
import com.scichart.charting.visuals.pointmarkers.TrianglePointMarker
import com.scichart.charting.visuals.renderableSeries.XyScatterRenderableSeries
import com.scichart.data.model.DoubleRange
import com.scichart.examples.fragments.base.ExampleSingleChartBaseFragment
import com.scichart.examples.utils.scichartExtensions.*
import java.util.*

class ScatterChartFragment : ExampleSingleChartBaseFragment() {
    private val random = Random()

    override fun initExample(surface: SciChartSurface) {
        val rSeries1 = getScatterRenderableSeries(TrianglePointMarker(), 0xFFFFEB01, false)
        val rSeries2 = getScatterRenderableSeries(EllipsePointMarker(), 0xFFffA300, false)
        val rSeries3 = getScatterRenderableSeries(TrianglePointMarker(), 0xFFff6501, true)
        val rSeries4 = getScatterRenderableSeries(EllipsePointMarker(), 0xFFffa300, true)

        surface.suspendUpdates {
            xAxes { numericAxis { growBy = DoubleRange(0.1, 0.1) }}
            yAxes { numericAxis { growBy = DoubleRange(0.1, 0.1) }}
            renderableSeries {
                rSeries(rSeries1)
                rSeries(rSeries2)
                rSeries(rSeries3)
                rSeries(rSeries4)
            }
            chartModifiers {
                zoomExtentsModifier()
                pinchZoomModifier()
                cursorModifier { receiveHandledEvents = true }
                xAxisDragModifier { receiveHandledEvents = true }
                yAxisDragModifier { dragMode = AxisDragMode.Pan }
            }

            rSeries1.waveAnimation { interpolator = DecelerateInterpolator(); duration = 3000; startDelay = 350 }
            rSeries2.waveAnimation { interpolator = DecelerateInterpolator(); duration = 3000; startDelay = 350 }
            rSeries3.waveAnimation { interpolator = DecelerateInterpolator(); duration = 3000; startDelay = 350 }
            rSeries4.waveAnimation { interpolator = DecelerateInterpolator(); duration = 3000; startDelay = 350 }
        }
    }

    private fun getScatterRenderableSeries(marker: IPointMarker, color: Long, negative: Boolean): XyScatterRenderableSeries {
        val dataSeriesName = if (marker is EllipsePointMarker)
            if (negative) "Negative Ellipse" else "Positive Ellipse"
            else if (negative) "Negative" else "Positive"

        return XyScatterRenderableSeries().apply {
            xyDataSeries<Int, Double> {
                seriesName = dataSeriesName
                for (i in 0 until 200) {
                    val time = if (i < 100) getRandom(0.0, i + 10.0) / 100 else getRandom(0.0, 200 - i + 10.0) / 100
                    val y = if (negative) -time * time * time else time * time * time
                    append(i, y)
                }
            }
            strokeStyle = SolidPenStyle(color)
            pointMarker = marker.apply {
                setSize(6)
                strokeStyle = SolidPenStyle(0xFFFFFFFF, 0.1f)
                fillStyle = SolidBrushStyle(color)
            }
        }
    }

    private fun getRandom(min: Double, max: Double): Double {
        return min + (max - min) * random.nextDouble()
    }
}