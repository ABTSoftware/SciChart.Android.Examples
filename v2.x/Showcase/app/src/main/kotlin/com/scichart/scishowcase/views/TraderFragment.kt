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

import android.net.NetworkInfo
import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.design.widget.CoordinatorLayout
import android.support.design.widget.Snackbar
import android.support.v7.widget.ActionMenuView
import android.support.v7.widget.Toolbar
import android.view.*
import android.view.animation.Animation
import android.view.animation.OvershootInterpolator
import android.view.animation.TranslateAnimation
import android.widget.ArrayAdapter
import android.widget.TextView
import com.github.pwittchen.reactivenetwork.library.rx2.ReactiveNetwork
import com.jakewharton.rxbinding2.view.clicks
import com.jakewharton.rxbinding2.widget.itemSelections
import com.scichart.scishowcase.R
import com.scichart.scishowcase.application.ExampleDefinition
import com.scichart.scishowcase.databinding.TraderFragmentBinding
import com.scichart.scishowcase.model.trader.DefaultTradePointProvider
import com.scichart.scishowcase.model.trader.StubTradePointsProvider
import com.scichart.scishowcase.model.trader.TradeConfig
import com.scichart.scishowcase.model.trader.TraderDataProvider
import com.scichart.scishowcase.utils.PrefManager
import com.scichart.scishowcase.viewModels.trader.TraderViewModel
import com.trello.rxlifecycle2.kotlin.bindToLifecycle
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Function3
import io.reactivex.schedulers.Schedulers
import tourguide.tourguide.Overlay
import tourguide.tourguide.Pointer
import tourguide.tourguide.ToolTip
import tourguide.tourguide.TourGuide


@ExampleDefinition("SciTrader", "Custom Description")
class TraderFragment : BindingFragmentBase<TraderFragmentBinding, TraderViewModel>() {
    private lateinit var snackbar: Snackbar

    override fun getLayoutId(): Int = R.layout.trader_fragment

    override fun onCreateViewModel(): TraderViewModel {
        setHasOptionsMenu(true)
        val context = requireContext()

        val resources = context.resources

        binding.stockSymbol.adapter = ArrayAdapter<String>(activity!!, R.layout.spinner_item_layout, R.id.spinnerItemText, resources.getStringArray(R.array.stockSymbols))
        binding.period.adapter = ArrayAdapter<String>(activity!!, R.layout.spinner_item_layout, R.id.spinnerItemText, resources.getStringArray(R.array.periods))
        binding.interval.adapter = ArrayAdapter<String>(activity!!, R.layout.spinner_item_layout, R.id.spinnerItemText, resources.getStringArray(R.array.intervals))

        binding.stockSymbol.setSelection(0)
        binding.period.setSelection(0)
        binding.interval.setSelection(0)

        binding.longTouchListenerView.clickSubject.doOnNext {
            if (viewModel.point != null) {
                viewModel.point = null
            }
        }.subscribe()

        binding.longTouchListenerView.longPressSubject.doOnNext { viewModel.point = it }.subscribe()

        val stockSymbolObservable = binding.stockSymbol.itemSelections().map { resources.getStringArray(R.array.stockSymbolsValues)[it] }
        val periodObservable = binding.period.itemSelections().map { resources.getStringArray(R.array.periodsValues)[it] }
        val intervalObservable = binding.interval.itemSelections().map { resources.getIntArray(R.array.intervalsValues)[it] }

        val tradeConfigObservable: Observable<TradeConfig> = Observable.combineLatest(stockSymbolObservable, periodObservable, intervalObservable, Function3(::TradeConfig))

        val connectivityObservable = ReactiveNetwork.observeNetworkConnectivity(context)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .bindToLifecycle(this)

        connectivityObservable.doOnNext { if (it.state != NetworkInfo.State.CONNECTED) snackbar.show() else snackbar.dismiss() }.subscribe()

        val dataProvider = TraderDataProvider(tradeConfigObservable, connectivityObservable, DefaultTradePointProvider(connectivityObservable), StubTradePointsProvider(context))
        return TraderViewModel(context, dataProvider)
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        val activity = requireActivity()

        activity.menuInflater.inflate(R.menu.trader_menu, menu)

        menu!!.findItem(R.id.settings)!!.clicks().doOnNext {
            TraderDialogFragment(booleanArrayOf(
                    viewModel.showMovingAverage50,
                    viewModel.showMovingAverage100,
                    viewModel.showRsiPanel.get(),
                    viewModel.showMacdPanel.get(),
                    viewModel.showAxisMarkers.get())) {
                if (it.size >= 5) {
                    viewModel.showMovingAverage50 = it[0]
                    viewModel.showMovingAverage100 = it[1]
                    viewModel.showRsiPanel.set(it[2])
                    viewModel.showMacdPanel.set(it[3])
                    viewModel.showMovingAverage50 = it[4]
                }
            }.show(activity.fragmentManager, "trader_dialog")
        }.subscribe()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        snackbar = Snackbar.make(binding.longTouchListenerView, "No Internet Connection", Snackbar.LENGTH_INDEFINITE).apply {
            val snackBarLayout = this.view as Snackbar.SnackbarLayout
            snackBarLayout.layoutParams.width = AppBarLayout.LayoutParams.MATCH_PARENT
            (snackBarLayout.findViewById<TextView>(android.support.design.R.id.snackbar_text)).apply {
                setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_warning_48px, 0, 0, 0)
                compoundDrawablePadding = resources.getDimensionPixelOffset(R.dimen.design_snackbar_padding_horizontal)
            }
            snackBarLayout.viewTreeObserver.addOnGlobalLayoutListener {
                if (snackBarLayout.layoutParams is CoordinatorLayout.LayoutParams) {
                    (snackBarLayout.layoutParams as CoordinatorLayout.LayoutParams).behavior = DisableSwipeBehavior()
                }
            }
        }

        val prefManager = PrefManager(requireContext())
        if (prefManager.showTraderIntroGuide) {
            createAndRunTourGuide()
            prefManager.showTraderIntroGuide = false
        }
    }

    private fun createAndRunTourGuide() {
        val activity = requireActivity()

        val finishedGuide = TourGuide.init(activity)
                .setToolTip(ToolTip()
                        .setTitle("Well Done !!!")
                        .setDescription("Tap somewhere to proceed to the SciChart trader")
                        .setEnterAnimation(createTranslateAnimation())
                        .setGravity(Gravity.TOP or Gravity.CENTER))
        finishedGuide.setOverlay(Overlay()
                .setHoleRadius(0)
                .setOnClickListener {
                    finishedGuide.cleanUp()
                })

        val contextMenuGuide = TourGuide.init(activity)
                .setToolTip(ToolTip()
                        .setDescription("Long press to open context menu")
                        .setEnterAnimation(createTranslateAnimation())
                        .setGravity(Gravity.START or Gravity.TOP))
        contextMenuGuide.setOverlay(Overlay()
                .setOnClickListener {
                    contextMenuGuide.cleanUp()
                    finishedGuide.playOn(binding.splitterTop)
                })
                .setPointer(Pointer())

        val splitterGuide = TourGuide.init(activity)
                .setToolTip(ToolTip()
                        .setDescription("Drag the separator to resize chart panes.")
                        .setEnterAnimation(createTranslateAnimation())
                        .setGravity(Gravity.CENTER or Gravity.TOP))
        splitterGuide.setOverlay(Overlay()
                .setStyle(Overlay.Style.Rectangle)
                .setOnClickListener {
                    splitterGuide.cleanUp()
                    contextMenuGuide.playOn(binding.contextMenuPlaceholder)
                })

        val settingsGuide = TourGuide.init(activity)
                .setToolTip(ToolTip()
                        .setDescription("Open settings to setup indicators")
                        .setEnterAnimation(createTranslateAnimation())
                        .setGravity(Gravity.START or Gravity.CENTER))
        settingsGuide.setOverlay(Overlay()
                .setOnClickListener {
                    settingsGuide.cleanUp()
                    splitterGuide.playOn(binding.splitterTop)
                })
                .setPointer(Pointer())

        val welcomeGuide: TourGuide = TourGuide.init(activity)
                .setToolTip(ToolTip()
                        .setTitle("Welcome to SciChart Trader!")
                        .setDescription("Tap anywhere to proceed...")
                        .setEnterAnimation(createTranslateAnimation())
                        .setGravity(Gravity.TOP or Gravity.CENTER))
        welcomeGuide.setOverlay(Overlay()
                .setHoleRadius(0)
                .setOnClickListener {
                    welcomeGuide.cleanUp()

                    val toolbar = activity.findViewById<Toolbar>(R.id.appToolbar)
                    val childAt = (toolbar.getChildAt(2) as ActionMenuView)
                    settingsGuide.playOn(childAt)
                })

        activity.window.decorView.post {
            welcomeGuide.playOn(binding.splitterTop)
        }
    }

    private fun createTranslateAnimation(): Animation {
        return TranslateAnimation(-400f, 0f, 0f, 0f).apply {
            duration = 500
            fillAfter = true
            interpolator = OvershootInterpolator()
        }
    }
}