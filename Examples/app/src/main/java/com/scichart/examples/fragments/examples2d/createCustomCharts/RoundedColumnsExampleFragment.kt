//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2021. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// RoundedColumnsExampleFragment.kt is part of SCICHART®, High Performance Scientific Charts
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

import android.view.animation.OvershootInterpolator
import com.scichart.charting.model.RenderableSeriesCollection
import com.scichart.charting.visuals.SciChartSurface
import com.scichart.charting.visuals.renderableSeries.FastColumnRenderableSeries
import com.scichart.charting.visuals.renderableSeries.data.ColumnRenderPassData
import com.scichart.charting.visuals.renderableSeries.data.ISeriesRenderPassData
import com.scichart.charting.visuals.renderableSeries.hitTest.ColumnHitProvider
import com.scichart.charting.visuals.renderableSeries.hitTest.NearestColumnPointProvider
import com.scichart.core.model.FloatValues
import com.scichart.data.model.DoubleRange
import com.scichart.drawing.common.IAssetManager2D
import com.scichart.drawing.common.IRenderContext2D
import com.scichart.examples.fragments.base.ExampleSingleChartBaseFragment
import com.scichart.examples.utils.scichartExtensions.*

class RoundedColumnsExampleFragment : ExampleSingleChartBaseFragment() {
    override fun initExample(surface: SciChartSurface) {
        surface.suspendUpdates {
            xAxes { numericAxis { growBy = DoubleRange(0.1, 0.1)} }
            yAxes { numericAxis { growBy = DoubleRange(0.2, 0.2)} }

            renderableSeries {
                roundedColumnsRenderableSeries {
                    xyDataSeries<Int, Int> {
                        val yValues = intArrayOf(50, 35, 61, 58, 50, 50, 40, 53, 55, 23, 45, 12, 59, 60)
                        for (i in yValues.indices) {
                            append(i, yValues[i])
                        }
                    }
                    fillBrushStyle = SolidBrushStyle(0xFF3CF3A6)

                    scaleAnimation { interpolator = OvershootInterpolator(); duration = 1500 }
                }
            }

            chartModifiers { defaultModifiers() }
        }
    }

    /**
     * A CustomRenderableSeries example which draws Rounded Columns
     */
    class RoundedColumnsRenderableSeries : FastColumnRenderableSeries(ColumnRenderPassData(), ColumnHitProvider(), NearestColumnPointProvider()) {
        private val topEllipseBuffer = FloatValues()
        private val rectsBuffer = FloatValues()
        private val bottomEllipseBuffer = FloatValues()

        override fun disposeCachedData() {
            super.disposeCachedData()

            topEllipseBuffer.disposeItems()
            rectsBuffer.disposeItems()
            bottomEllipseBuffer.disposeItems()
        }

        override fun internalDraw(renderContext: IRenderContext2D, assetManager: IAssetManager2D, renderPassData: ISeriesRenderPassData) {
            // Don't draw transparent series
            if (opacity == 0f) return

            val fillBrush = fillBrushStyle
            if (fillBrush == null || !fillBrush.isVisible) return

            val rpd = renderPassData as ColumnRenderPassData
            val diameter = rpd.columnPixelWidth
            updateDrawingBuffers(rpd, diameter, rpd.zeroLineCoord)

            val brush = assetManager.createBrush(fillBrush)
            renderContext.fillRects(rectsBuffer.itemsArray, 0, rectsBuffer.size(), brush)
            renderContext.drawEllipses(topEllipseBuffer.itemsArray, 0, topEllipseBuffer.size(), diameter, diameter, brush)
            renderContext.drawEllipses(bottomEllipseBuffer.itemsArray, 0, bottomEllipseBuffer.size(), diameter, diameter, brush)
        }

        private fun updateDrawingBuffers(renderPassData: ColumnRenderPassData, columnPixelWidth: Float, zeroLine: Float) {
            val halfWidth = columnPixelWidth / 2

            topEllipseBuffer.setSize(renderPassData.pointsCount() * 2)
            rectsBuffer.setSize(renderPassData.pointsCount() * 4)
            bottomEllipseBuffer.setSize(renderPassData.pointsCount() * 2)

            val topArray = topEllipseBuffer.itemsArray
            val rectsArray = rectsBuffer.itemsArray
            val bottomArray = bottomEllipseBuffer.itemsArray

            val xCoordsArray = renderPassData.xCoords.itemsArray
            val yCoordsArray = renderPassData.yCoords.itemsArray
            for (i in 0 until renderPassData.pointsCount()) {
                val x = xCoordsArray[i]
                val y = yCoordsArray[i]

                topArray[i * 2] = x
                topArray[i * 2 + 1] = y - halfWidth

                rectsArray[i * 4] = x - halfWidth
                rectsArray[i * 4 + 1] = y - halfWidth
                rectsArray[i * 4 + 2] = x + halfWidth
                rectsArray[i * 4 + 3] = zeroLine + halfWidth

                bottomArray[i * 2] = x
                bottomArray[i * 2 + 1] = zeroLine + halfWidth
            }
        }
    }

    fun RenderableSeriesCollection.roundedColumnsRenderableSeries(init: RoundedColumnsRenderableSeries.() -> Unit) {
        add(RoundedColumnsRenderableSeries().apply(init))
    }
}