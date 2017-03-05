package com.scichart.scishowcase.viewModels

import android.content.Context
import android.support.v4.app.Fragment
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import com.scichart.scishowcase.BR
import com.scichart.scishowcase.R
import com.scichart.scishowcase.application.Example
import com.scichart.scishowcase.application.ExampleManager
import com.scichart.scishowcase.model.RecyclerBindingAdapter
import com.scichart.scishowcase.utils.RecyclerConfiguration
import com.scichart.scishowcase.views.HomePageFragment

class HomePageViewModel(context: Context, private val fragment: HomePageFragment) : FragmentViewModelBase(context) {

    val recyclerConfiguration = RecyclerConfiguration()

    init {
        initRecycler()
    }

    private fun initRecycler() {
        recyclerConfiguration.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        recyclerConfiguration.itemAnimator = DefaultItemAnimator()
        recyclerConfiguration.adapter = getAdapter()
    }

    private fun getAdapter(): RecyclerBindingAdapter<Example> {
        val adapter = RecyclerBindingAdapter(R.layout.home_page_item_layout, BR.viewModel, ExampleManager.examples)

        adapter.itemClickFlowable.doOnEach {
            fragment.fragmentManager.beginTransaction().replace(R.id.fragment_container, it?.value?.exampleType?.java?.newInstance() as Fragment).addToBackStack(null).commit()
        }.subscribe()

        return adapter
    }
}