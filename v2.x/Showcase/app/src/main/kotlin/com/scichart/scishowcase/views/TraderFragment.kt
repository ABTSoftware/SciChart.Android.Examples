//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2017. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// TraderFragment.kt is part of the SCICHART® Showcases. Permission is hereby granted
// to modify, create derivative works, distribute and publish any part of this source
// code whether for commercial, private or personal use.
//
// The SCICHART® examples are distributed in the hope that they will be useful, but
// without any warranty. It is provided "AS IS" without warranty of any kind, either
// expressed or implied.
//******************************************************************************

package com.scichart.scishowcase.views

import android.widget.ArrayAdapter
import com.github.pwittchen.reactivenetwork.library.rx2.ReactiveNetwork
import com.jakewharton.rxbinding2.widget.itemSelections
import com.scichart.scishowcase.R
import com.scichart.scishowcase.application.ExampleDefinition
import com.scichart.scishowcase.databinding.TraderFragmentBinding
import com.scichart.scishowcase.model.trader.DefaultTradePointProvider
import com.scichart.scishowcase.model.trader.StubTradePointsProvider
import com.scichart.scishowcase.model.trader.TradeConfig
import com.scichart.scishowcase.model.trader.TraderDataProvider
import com.scichart.scishowcase.viewModels.trader.TraderViewModel
import com.trello.rxlifecycle2.kotlin.bindToLifecycle
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Function3
import io.reactivex.schedulers.Schedulers

@ExampleDefinition("SciTrader", "Custom Description")
class TraderFragment : BindingFragmentBase<TraderFragmentBinding, TraderViewModel>() {
    override fun getLayoutId(): Int = R.layout.trader_fragment

    override fun onCreateViewModel(): TraderViewModel {
        val resources = activity.resources

        val stockSymbols = resources.getStringArray(R.array.stockSymbols)
        val intervals = resources.getStringArray(R.array.intervals)
        val periods = resources.getStringArray(R.array.periods)

        val stockSymbolAdapter = ArrayAdapter<String>(activity!!, R.layout.spinner_item_layout, R.id.spinnerItemText, stockSymbols)
        val periodAdapter = ArrayAdapter<String>(activity!!, R.layout.spinner_item_layout, R.id.spinnerItemText, periods)
        val intervalAdapter = ArrayAdapter<String>(activity!!, R.layout.spinner_item_layout, R.id.spinnerItemText, intervals)

        binding.stockSymbol.adapter = stockSymbolAdapter
        binding.period.adapter = periodAdapter
        binding.interval.adapter = intervalAdapter

        binding.stockSymbol.setSelection(0)
        binding.period.setSelection(0)
        binding.interval.setSelection(0)

        val stockSymbolsValues = resources.getStringArray(R.array.stockSymbolsValues)
        val periodsValues = resources.getStringArray(R.array.periodsValues)
        val intervalsValues = resources.getIntArray(R.array.intervalsValues)

        val stockSymbolObservable = binding.stockSymbol.itemSelections().map { stockSymbolsValues[it] }
        val periodObservable = binding.period.itemSelections().map { periodsValues[it] }
        val intervalObservable = binding.interval.itemSelections().map { intervalsValues[it] }

        val tradeConfigObservable: Observable<TradeConfig> = Observable.combineLatest(stockSymbolObservable, periodObservable, intervalObservable, Function3(::TradeConfig))

        val connectivityObservable = ReactiveNetwork.observeNetworkConnectivity(context)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .bindToLifecycle(this)

        val dataProvider = TraderDataProvider(tradeConfigObservable, connectivityObservable, DefaultTradePointProvider(connectivityObservable), StubTradePointsProvider(context))
        return TraderViewModel(activity, dataProvider)
    }
}