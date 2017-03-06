package com.scichart.scishowcase.viewModels.ecg

import android.databinding.ObservableField
import android.databinding.ObservableInt

class EcgHeartRateViewModel() {
    val bpmValue = ObservableField<String>()
    val heartRateIconVisibility = ObservableInt()
}