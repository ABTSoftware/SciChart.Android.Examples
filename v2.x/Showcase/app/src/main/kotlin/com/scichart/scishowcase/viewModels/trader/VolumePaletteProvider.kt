//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2017. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// VolumePaletteProvider.kt is part of the SCICHART® Showcases. Permission is hereby granted
// to modify, create derivative works, distribute and publish any part of this source
// code whether for commercial, private or personal use.
//
// The SCICHART® Showcases are distributed in the hope that they will be useful, but
// without any warranty. It is provided "AS IS" without warranty of any kind, either
// expressed or implied.
//******************************************************************************

package com.scichart.scishowcase.viewModels.trader

import com.scichart.charting.visuals.renderableSeries.FastCandlestickRenderableSeries
import com.scichart.charting.visuals.renderableSeries.FastColumnRenderableSeries
import com.scichart.charting.visuals.renderableSeries.data.OhlcRenderPassData
import com.scichart.charting.visuals.renderableSeries.paletteProviders.IFillPaletteProvider
import com.scichart.charting.visuals.renderableSeries.paletteProviders.IStrokePaletteProvider
import com.scichart.charting.visuals.renderableSeries.paletteProviders.PaletteProviderBase
import com.scichart.core.model.IntegerValues

class VolumePaletteProvider(private val stockSeries: FastCandlestickRenderableSeries, private val upColor: Int, private val downColor: Int) : PaletteProviderBase<FastColumnRenderableSeries>(FastColumnRenderableSeries::class.java), IStrokePaletteProvider, IFillPaletteProvider {
    private val colors = IntegerValues()

    override fun update() {
        val stockRenderPassData = stockSeries.currentRenderPassData
        if(stockRenderPassData is OhlcRenderPassData) {
            val size = stockRenderPassData.pointsCount()
            colors.setSize(size)

            val openValues = stockRenderPassData.openValues.itemsArray
            val closeValues = stockRenderPassData.closeValues.itemsArray

            val itemsArray = colors.itemsArray
            for (i in 0..size-1) {
                itemsArray[i] = if (closeValues[i] >= openValues[i]) upColor else downColor
            }
        }
    }

    override fun getFillColors(): IntegerValues = colors

    override fun getStrokeColors(): IntegerValues = colors
}
