//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2017. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// RecyclerConfiguration.kt is part of the SCICHART® Showcases. Permission is hereby granted
// to modify, create derivative works, distribute and publish any part of this source
// code whether for commercial, private or personal use.
//
// The SCICHART® examples are distributed in the hope that they will be useful, but
// without any warranty. It is provided "AS IS" without warranty of any kind, either
// expressed or implied.
//******************************************************************************

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