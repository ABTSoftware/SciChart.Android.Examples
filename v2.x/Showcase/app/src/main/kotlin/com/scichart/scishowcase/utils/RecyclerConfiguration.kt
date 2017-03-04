package com.scichart.scishowcase.utils

import android.databinding.BaseObservable
import android.databinding.Bindable
import android.support.v7.widget.RecyclerView
import com.scichart.scishowcase.BR

class RecyclerConfiguration : BaseObservable() {

    @Bindable
    var layoutManager: RecyclerView.LayoutManager? = null
        set(value) {
            field = value
            notifyPropertyChanged(BR.layoutManager)
        }

    @Bindable
    var itemAnimator: RecyclerView.ItemAnimator? = null
        set(value) {
            field = value
            notifyPropertyChanged(BR.itemAnimator)
        }

    @Bindable
    var adapter: RecyclerView.Adapter<*>? = null
        set(value) {
            field = value
            notifyPropertyChanged(BR.adapter)
        }
}