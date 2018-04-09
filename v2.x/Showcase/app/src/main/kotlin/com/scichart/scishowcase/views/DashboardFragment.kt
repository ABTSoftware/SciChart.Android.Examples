package com.scichart.scishowcase.views

import com.scichart.scishowcase.R
import com.scichart.scishowcase.application.ExampleDefinition
import com.scichart.scishowcase.databinding.DashboardFragmentBinding
import com.scichart.scishowcase.model.dashboard.DefaultDashboardDataProvider
import com.scichart.scishowcase.viewModels.dashboard.DashboardViewModel

@ExampleDefinition("Dashboard", "Custom Description")
class DashboardFragment : BindingFragmentBase<DashboardFragmentBinding, DashboardViewModel>() {

    override fun getLayoutId() = R.layout.dashboard_fragment

    override fun onCreateViewModel(): DashboardViewModel {
        val context = requireContext()
        return DashboardViewModel(context, DefaultDashboardDataProvider(context), binding.chart)
    }
}