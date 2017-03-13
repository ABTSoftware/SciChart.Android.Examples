//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2017. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// TraderViewModel.kt is part of the SCICHART® Showcases. Permission is hereby granted
// to modify, create derivative works, distribute and publish any part of this source
// code whether for commercial, private or personal use.
//
// The SCICHART® examples are distributed in the hope that they will be useful, but
// without any warranty. It is provided "AS IS" without warranty of any kind, either
// expressed or implied.
//******************************************************************************

package com.scichart.scishowcase.viewModels.trader

import android.content.Context
import com.scichart.scishowcase.model.trader.TradeConfig
import com.scichart.scishowcase.model.trader.TraderDataProvider
import com.scichart.scishowcase.viewModels.FragmentViewModelBase
import com.trello.rxlifecycle2.LifecycleProvider
import com.trello.rxlifecycle2.kotlin.bindToLifecycle
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers

class TraderViewModel(context: Context, private val dataProvider: TraderDataProvider, private val tradeConfigObservable: Observable<TradeConfig>) : FragmentViewModelBase(context) {
    val stockVM = StockChartViewModel(context)

    override fun subscribe(lifecycleProvider: LifecycleProvider<*>) {
        super.subscribe(lifecycleProvider)

        tradeConfigObservable.observeOn(Schedulers.computation()).doOnNext {
            val data = dataProvider.getData(it)
            stockVM.setData(data)
        }.bindToLifecycle(lifecycleProvider).subscribe()
    }
}