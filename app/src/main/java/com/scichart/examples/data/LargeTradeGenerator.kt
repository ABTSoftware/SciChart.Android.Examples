//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2022. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// TradeDataGenerator.kt is part of SCICHART®, High Performance Scientific Charts
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

package com.scichart.examples.data

import java.util.*
import kotlin.random.Random

data class LargeTrade(
    val price: Double,
    val volume: Double,
)

data class LargeTradeBar(
    val date: Date,
    val largeTrades: List<LargeTrade>
)

class LargeTradeGenerator(
    private val maxLargeTradesPerCandle: Int = 4,
    private val minLargeTradeVolume: Double = 20.0,
    private val maxLargeTradeVolume: Double = 80.0
) {

    fun generatePricesSeriesWithLargeTrades(priceSeries: PriceSeries): List<LargeTradeBar> {
        return priceSeries.map { priceBar ->
            val largeTrades = (0 until Random.nextInt(maxLargeTradesPerCandle))
                .map { generateRandomLargeTradeFor(priceBar) }

            LargeTradeBar(priceBar.date, largeTrades)
        }
    }

    private fun generateRandomLargeTradeFor(priceBar: PriceBar): LargeTrade {
        val price = Random.nextDouble(priceBar.low, priceBar.high)
        val volume = Random.nextDouble(minLargeTradeVolume, maxLargeTradeVolume)

        return LargeTrade(price, volume)
    }
}
