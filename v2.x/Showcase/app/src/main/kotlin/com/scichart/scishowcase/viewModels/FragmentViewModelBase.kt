package com.scichart.scishowcase.viewModels

import android.content.Context
import android.databinding.BaseObservable

abstract class FragmentViewModelBase(protected val context: Context) : BaseObservable() {
    open fun onCreateView() { }

    open fun onDestroyView() { }

    open fun onResume() { }

    open fun onPause() { }
}