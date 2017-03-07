//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2017. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// EcgFragment.kt is part of the SCICHART® Showcases. Permission is hereby granted
// to modify, create derivative works, distribute and publish any part of this source
// code whether for commercial, private or personal use.
//
// The SCICHART® examples are distributed in the hope that they will be useful, but
// without any warranty. It is provided "AS IS" without warranty of any kind, either
// expressed or implied.
//******************************************************************************

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