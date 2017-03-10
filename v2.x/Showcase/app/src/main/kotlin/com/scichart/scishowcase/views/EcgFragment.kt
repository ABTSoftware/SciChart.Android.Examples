package com.scichart.scishowcase.views

import com.scichart.scishowcase.R
import com.scichart.scishowcase.application.ExampleDefinition
import com.scichart.scishowcase.databinding.EcgFragmentBinding
import com.scichart.scishowcase.model.ecg.DefaultEcgDataProvider
import com.scichart.scishowcase.viewModels.ecg.EcgViewModel

@ExampleDefinition("Sci-ECG", "Custom Description")
class EcgFragment : BindingFragmentBase<EcgFragmentBinding, EcgViewModel>() {

    override fun getLayoutId() = R.layout.ecg_fragment

    override fun onCreateViewModel() = EcgViewModel(activity, DefaultEcgDataProvider(activity), binding.chart)
}