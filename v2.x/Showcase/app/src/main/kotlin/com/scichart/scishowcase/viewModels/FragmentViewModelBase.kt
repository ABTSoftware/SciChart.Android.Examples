package com.scichart.scishowcase.viewModels

import android.content.Context
import android.databinding.BaseObservable
import com.trello.rxlifecycle2.LifecycleProvider

abstract class FragmentViewModelBase(protected val context: Context) : BaseObservable() {
    open fun subscribe(lifecycleProvider: LifecycleProvider<*>) { }
}