package com.scichart.scishowcase.model

import android.databinding.BindingAdapter
import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.support.annotation.IdRes
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.scichart.scishowcase.utils.RecyclerConfiguration
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.subjects.PublishSubject

class RecyclerBindingAdapter<T>(private val holderLayout: Int, private @IdRes val variableId: Int, private var items: List<T>) : RecyclerView.Adapter<RecyclerBindingAdapter.BindingHolder>() {

    private val itemClickPublishSubject: PublishSubject<T> = PublishSubject.create()

    val itemClickFlowable: Flowable<T>
        get() = itemClickPublishSubject.toFlowable(BackpressureStrategy.BUFFER)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerBindingAdapter.BindingHolder {
        val v = LayoutInflater.from(parent.context).inflate(holderLayout, parent, false)
        return BindingHolder(v)
    }

    override fun onBindViewHolder(holder: RecyclerBindingAdapter.BindingHolder, position: Int) {
        val item = items[position]

        holder.binding.root.setOnClickListener {
            itemClickPublishSubject.onNext(item)
        }
        holder.binding.setVariable(variableId, item)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    class BindingHolder(v: View) : RecyclerView.ViewHolder(v) {
        val binding: ViewDataBinding = DataBindingUtil.bind<ViewDataBinding>(v)
    }
}

