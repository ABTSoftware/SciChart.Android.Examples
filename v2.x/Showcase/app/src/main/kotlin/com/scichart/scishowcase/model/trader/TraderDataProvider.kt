//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2017. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// TraderDataProvider.kt is part of the SCICHART® Showcases. Permission is hereby granted
// to modify, create derivative works, distribute and publish any part of this source
// code whether for commercial, private or personal use.
//
// The SCICHART® Showcases are distributed in the hope that they will be useful, but
// without any warranty. It is provided "AS IS" without warranty of any kind, either
// expressed or implied.
//******************************************************************************

package com.scichart.scishowcase.model.trader

import android.net.NetworkInfo
import com.github.pwittchen.reactivenetwork.library.rx2.Connectivity
import com.scichart.scishowcase.model.IDataProvider
import io.reactivex.Observable
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

class TraderDataProvider(private val tradeConfigObservable: Observable<TradeConfig>,
                         private val connectivityObservable: Observable<Connectivity>,
                         private val defaultProvider: ITradePointsProvider,
                         private val stubProvider: ITradePointsProvider) : IDataProvider<TradeDataPoints> {

    override fun getData(): Observable<TradeDataPoints> {
        val tradeConfigObservable = tradeConfigObservable.switchMap { tradeConfig -> Observable.interval(0, 1, TimeUnit.MINUTES).map { tradeConfig } }

        val providerObservable = connectivityObservable.map { if (it.state == NetworkInfo.State.CONNECTED) defaultProvider else stubProvider }

        return Observable.combineLatest<TradeConfig, ITradePointsProvider, Pair<TradeConfig, ITradePointsProvider>>(tradeConfigObservable, providerObservable, BiFunction { tradeConfig, provider -> Pair(tradeConfig, provider) })
                .observeOn(Schedulers.io()) // need to execute network requests from non UI thread
                .map { (tradeConfig, provider) -> provider.getTradePoints(tradeConfig) }
    }
}