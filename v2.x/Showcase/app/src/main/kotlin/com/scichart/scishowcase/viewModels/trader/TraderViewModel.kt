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
import android.util.Log
import android.widget.Toast
import com.scichart.scishowcase.model.trader.TraderDataProvider
import com.scichart.scishowcase.viewModels.FragmentViewModelBase
import com.trello.rxlifecycle2.LifecycleProvider
import com.trello.rxlifecycle2.kotlin.bindToLifecycle

class TraderViewModel(context: Context, private val dataProvider: TraderDataProvider) : FragmentViewModelBase(context) {
    val stockVM = StockChartViewModel(context)
    val rsiVM = RsiViewModel(context)
    val macdVM = MacdViewModel(context)

    override fun subscribe(lifecycleProvider: LifecycleProvider<*>) {
        super.subscribe(lifecycleProvider)

        dataProvider.getData().bindToLifecycle(lifecycleProvider)
                .subscribe({
                    stockVM.setData(it)
                    rsiVM.setData(it)
                    macdVM.setData(it)
                }, {
                    Log.e("TraderDataProvider", it.message!!)
                    Toast.makeText(context, it.message, Toast.LENGTH_LONG).show()
                })
    }
}