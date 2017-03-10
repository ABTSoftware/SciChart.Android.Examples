package com.scichart.scishowcase.views

import com.scichart.scishowcase.R
import com.scichart.scishowcase.databinding.HomePageFragmentBinding
import com.scichart.scishowcase.viewModels.HomePageViewModel

class HomePageFragment : BindingFragmentBase<HomePageFragmentBinding, HomePageViewModel>() {

    override fun getLayoutId(): Int {
        return R.layout.home_page_fragment
    }

    override fun onCreateViewModel(): HomePageViewModel {
        return HomePageViewModel(activity, this)
    }
}