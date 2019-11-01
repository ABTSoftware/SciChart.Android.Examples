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
import android.databinding.Bindable
import android.databinding.ObservableBoolean
import android.graphics.PointF
import android.util.Log
import android.widget.Toast
import com.scichart.charting.modifiers.OnAnnotationCreatedListener
import com.scichart.data.model.DoubleRange
import com.scichart.scishowcase.BR
import com.scichart.scishowcase.R
import com.scichart.scishowcase.model.trader.TraderDataProvider
import com.scichart.scishowcase.viewModels.FragmentViewModelBase
import com.trello.rxlifecycle2.LifecycleProvider
import com.trello.rxlifecycle2.kotlin.bindToLifecycle
import io.reactivex.subjects.PublishSubject

class TraderViewModel(context: Context, private val dataProvider: TraderDataProvider) : FragmentViewModelBase(context) {
    private val sharedXRange = DoubleRange()
    private val onAnnotationCreatedListener = OnAnnotationCreatedListener {
        switchAnnotationCreationState(false)
    }

    private val ma50PublishSubject = PublishSubject.create<Boolean>()!!
    private val ma100PublishSubject = PublishSubject.create<Boolean>()!!

    var point: PointF? = null
        @Bindable
        get() = field
        set(value) {
            field = value
            notifyPropertyChanged(BR.point)
        }

    var showMovingAverage50: Boolean = true
        @Bindable
        get() = field
        set(value) {
            field = value
            ma50PublishSubject.onNext(value)
            notifyPropertyChanged(BR.showMovingAverage50)
        }

    var showMovingAverage100: Boolean = true
        @Bindable
        get() = field
        set(value) {
            field = value
            ma100PublishSubject.onNext(value)
            notifyPropertyChanged(BR.showMovingAverage100)
        }

    var showRsiPanel: ObservableBoolean = ObservableBoolean(true)
    var showMacdPanel: ObservableBoolean = ObservableBoolean(true)
    var showAxisMarkers: ObservableBoolean = ObservableBoolean(true)

    var contextMenuEnabled: ObservableBoolean = ObservableBoolean(true)

    var themeId: Int = R.style.SciChart_SciChartv4DarkStyle
        @Bindable
        get() = field
        set(value) {
            field = value
            notifyPropertyChanged(BR.themeId)
        }

    val stockVM = StockChartViewModel(context, sharedXRange, ma50PublishSubject, ma100PublishSubject, onAnnotationCreatedListener)
    val rsiVM = RsiViewModel(context, sharedXRange, onAnnotationCreatedListener)
    val macdVM = MacdViewModel(context, sharedXRange, onAnnotationCreatedListener)

    override fun subscribe(lifecycleProvider: LifecycleProvider<*>) {
        super.subscribe(lifecycleProvider)

        dataProvider.getData().bindToLifecycle(lifecycleProvider)
                .subscribe({
                    stockVM.setData(it)
                    rsiVM.setData(it)
                    macdVM.setData(it)
                }, {
                    Log.e("TraderDataProvider", it.message!!)
                })
    }

    fun changeTheme() {
        themeId = (if (themeId == R.style.SciChart_SciChartv4DarkStyle) R.style.SciChart_Bright_Spark else R.style.SciChart_SciChartv4DarkStyle)
    }

    fun createAnnotation(annotationType: Int) {
        this.point = null
        switchAnnotationCreationState(true)
        changeAnnotationType(annotationType)
    }

    fun switchAnnotationCreationState(isEnabled: Boolean) {
        contextMenuEnabled.set(!isEnabled)

        stockVM.switchAnnotationCreationState(isEnabled)
        rsiVM.switchAnnotationCreationState(isEnabled)
        macdVM.switchAnnotationCreationState(isEnabled)
    }

    private fun changeAnnotationType(annotationType: Int) {
        stockVM.changeAnnotationType(annotationType)
        rsiVM.changeAnnotationType(annotationType)
        macdVM.changeAnnotationType(annotationType)
    }
}