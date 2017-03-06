package com.scichart.scishowcase.viewModels.ecg

import android.databinding.ObservableField
import android.databinding.ObservableInt

class EcgBloodVolumeViewModel() {
    val bloodVolumeValue = ObservableField<String>()
    val svBar1Value = ObservableInt()
    val svBar2Value = ObservableInt()

}