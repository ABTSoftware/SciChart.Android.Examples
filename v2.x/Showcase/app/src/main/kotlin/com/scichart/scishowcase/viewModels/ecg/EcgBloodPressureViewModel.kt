package com.scichart.scishowcase.viewModels.ecg

import android.databinding.ObservableField
import android.databinding.ObservableInt

class EcgBloodPressureViewModel() {
    val bloodPressure = ObservableField<String>()
    val bloodPressureBarValue = ObservableInt()
}