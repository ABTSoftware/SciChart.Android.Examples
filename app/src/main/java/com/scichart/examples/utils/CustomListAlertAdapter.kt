//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2022. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// CustomListAlertAdapter.kt is part of SCICHART®, High Performance Scientific Charts
// For full terms and conditions of the license, see http://www.scichart.com/scichart-eula/
//
// This source code is protected by international copyright law. Unauthorized
// reproduction, reverse-engineering, or distribution of all or any portion of
// this source code is strictly prohibited.
//
// This source code contains confidential and proprietary trade secrets of
// SciChart Ltd., and should at no time be copied, transferred, sold,
// distributed or made available without express written permission.
//******************************************************************************

package com.scichart.examples.utils

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.scichart.examples.R

class CustomListAlertAdapter(
    val index: Int,
    val onClick: (index: Int) -> Unit
) :
    RecyclerView.Adapter<CustomListAlertAdapter.CustomListAlertViewHolder>() {

    private var itemList = emptyList<String>()
    private var selectedIndex = index

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomListAlertViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.layout_custom_alert_item, parent, false)
        return CustomListAlertViewHolder(view)
    }

    override fun onBindViewHolder(holder: CustomListAlertViewHolder, position: Int) {
        val currentItem = itemList.getOrNull(position)
        currentItem?.let {
            holder.textTv.text = it

            holder.checkIv.isVisible = position == selectedIndex

            holder.view.setOnClickListener { _ ->
                val temp = selectedIndex
                selectedIndex = position
                onClick(selectedIndex)
                notifyItemChanged(temp)
                notifyItemChanged(position)
            }
        }
    }

    override fun getItemCount(): Int = itemList.size

    fun submitList(newList: List<String>){
        itemList = newList
        notifyDataSetChanged()
    }

    class CustomListAlertViewHolder(
        val view: View
    ) : RecyclerView.ViewHolder(view) {

        val textTv: TextView = view.findViewById(R.id.custom_alert_item_tv)
        val checkIv: ImageView = view.findViewById(R.id.custom_alert_item_iv)

    }
}