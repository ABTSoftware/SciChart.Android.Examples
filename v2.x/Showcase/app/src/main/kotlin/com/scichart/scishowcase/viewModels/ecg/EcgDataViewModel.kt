package com.scichart.scishowcase.viewModels.ecg

import android.databinding.ObservableField

class EcgDataViewModel(val rightText: CharSequence, val leftText: CharSequence, val color: Int) {
    val value = ObservableField<CharSequence>()
}