package com.scichart.scishowcase.views

import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.os.Bundle
import android.support.annotation.LayoutRes
import android.support.annotation.Nullable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.scichart.scishowcase.BR
import com.scichart.scishowcase.viewModels.FragmentViewModelBase
import com.trello.rxlifecycle2.components.support.RxFragment

abstract class BindingFragmentBase<TBinding : ViewDataBinding, TViewModel : FragmentViewModelBase> : RxFragment() {

    protected lateinit var binding: TBinding
    protected lateinit var viewModel: TViewModel

    override fun onCreateView(inflater: LayoutInflater?, @Nullable container: ViewGroup?, @Nullable savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate<TBinding>(inflater, getLayoutId(), container, false)

        viewModel = onCreateViewModel()

        viewModel.subscribe(this)

        binding.setVariable(BR.viewModel, viewModel)
        binding.executePendingBindings()

        return binding.root
    }

    @LayoutRes
    protected abstract fun getLayoutId(): Int

    protected abstract fun onCreateViewModel(): TViewModel
}