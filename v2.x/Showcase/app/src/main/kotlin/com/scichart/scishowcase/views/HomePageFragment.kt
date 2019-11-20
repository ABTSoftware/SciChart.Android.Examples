//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2017. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// HomePageFragment.kt is part of the SCICHART® Showcases. Permission is hereby granted
// to modify, create derivative works, distribute and publish any part of this source
// code whether for commercial, private or personal use.
//
// The SCICHART® examples are distributed in the hope that they will be useful, but
// without any warranty. It is provided "AS IS" without warranty of any kind, either
// expressed or implied.
//******************************************************************************

package com.scichart.scishowcase.views

import com.scichart.scishowcase.R
import com.scichart.scishowcase.databinding.HomePageFragmentBinding
import com.scichart.scishowcase.viewModels.HomePageViewModel

class HomePageFragment : BindingFragmentBase<HomePageFragmentBinding, HomePageViewModel>() {

    override fun getLayoutId(): Int {
        return R.layout.home_page_fragment
    }

    override fun onCreateViewModel(): HomePageViewModel {
        return HomePageViewModel(requireContext(), this)
    }
}