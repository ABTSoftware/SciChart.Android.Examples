//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2022. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// SharedPaletteProvider.kt is part of SCICHART®, High Performance Scientific Charts
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

package com.scichart.examples.utils.populationpyramid

import com.scichart.charting.visuals.renderableSeries.XyRenderableSeriesBase
import com.scichart.charting.visuals.renderableSeries.data.XyRenderPassData
import com.scichart.charting.visuals.renderableSeries.paletteProviders.IFillPaletteProvider
import com.scichart.charting.visuals.renderableSeries.paletteProviders.IPointMarkerPaletteProvider
import com.scichart.charting.visuals.renderableSeries.paletteProviders.IStrokePaletteProvider
import com.scichart.charting.visuals.renderableSeries.paletteProviders.PaletteProviderBase
import com.scichart.core.model.IntegerValues


class SharedPaletteProvider(
    private val lowerLimit: Double, private val upperLimit: Double
) : PaletteProviderBase<XyRenderableSeriesBase>(XyRenderableSeriesBase::class.java),
    IFillPaletteProvider, IStrokePaletteProvider, IPointMarkerPaletteProvider {
    private val strokeColors = IntegerValues()
    private val fillColors = IntegerValues()

    override fun update() {
        val y1 = lowerLimit
        val y2 = upperLimit

        val minimum = Math.min(y1, y2)
        val maximum = Math.max(y1, y2)

        val renderPassData = renderableSeries!!.currentRenderPassData as XyRenderPassData
        val size = renderPassData.pointsCount()
        strokeColors.setSize(size)
        fillColors.setSize(size)

        val yValues = renderPassData.xValues
        for (i in 0 until size) {
            val value = yValues[i]
            if (value > maximum) {
                strokeColors[i] = -0x100
                fillColors[i] = -0x66000100
            } else if (value < minimum) {
                strokeColors[i] = -0x10000
                fillColors[i] = -0x10000
            } else {
                strokeColors[i] = -0xff0100
                fillColors[i] = -0x66ff0100
            }
        }
    }

    override fun getStrokeColors(): IntegerValues {
        return strokeColors
    }

    override fun getFillColors(): IntegerValues {
        return fillColors
    }

    override fun getPointMarkerColors(): IntegerValues {
        return fillColors
    }
}