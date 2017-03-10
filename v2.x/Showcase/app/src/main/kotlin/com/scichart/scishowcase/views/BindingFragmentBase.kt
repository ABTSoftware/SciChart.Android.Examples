package com.scichart.scishowcase.views

import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.os.Bundle
import android.support.annotation.LayoutRes
import android.support.annotation.Nullable
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.scichart.scishowcase.BR
import com.scichart.scishowcase.viewModels.FragmentViewModelBase

abstract class BindingFragmentBase<TBinding : ViewDataBinding, TViewModel : FragmentViewModelBase> : Fragment() {

    protected lateinit var binding: TBinding
    protected lateinit var viewModel: TViewModel

    override fun onCreateView(inflater: LayoutInflater?, @Nullable container: ViewGroup?, @Nullable savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate<TBinding>(inflater, getLayoutId(), container, false)

        viewModel = onCreateViewModel()

        viewModel.onCreateView()

        binding.setVariable(BR.viewModel, viewModel)
        binding.executePendingBindings()

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()

        viewModel.onDestroyView()
    }

    @LayoutRes
    protected abstract fun getLayoutId(): Int

    protected abstract fun onCreateViewModel(): TViewModel

    override fun onResume() {
        super.onResume()

        viewModel.onResume()
    }

    override fun onPause() {
        super.onPause()

        viewModel.onPause()
    }
}