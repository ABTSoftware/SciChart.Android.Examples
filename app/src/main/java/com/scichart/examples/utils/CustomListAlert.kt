//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2022. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// CustomListAlert.kt is part of SCICHART®, High Performance Scientific Charts
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

import android.content.Context
import android.view.LayoutInflater
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.scichart.examples.R
import com.scichart.examples.databinding.LayoutCustomAlertBinding

class CustomListAlert {

    companion object {

        fun showAlert(
            context: Context,
            list: List<String>,
            selectedIndex: Int,
            onSubmit: (index: Int) -> Unit
        ){
            var index = selectedIndex

            val alertDialogBuilder = AlertDialog.Builder(context, R.style.CustomAlertDialog)
            val dialogView = LayoutInflater.from(context).inflate(R.layout.layout_custom_alert, null)

            alertDialogBuilder.setView(dialogView)

            val listRv = dialogView.findViewById<RecyclerView>(R.id.custom_alert_rv)
            val cancelTv = dialogView.findViewById<TextView>(R.id.custom_alert_cancel)
            val submitTv = dialogView.findViewById<TextView>(R.id.custom_alert_submit)

            val adapter = CustomListAlertAdapter(index){ i ->
                index = i
            }
            adapter.submitList(list)

            listRv.layoutManager = LinearLayoutManager(context)
            listRv.adapter = adapter

            val alertDialog = alertDialogBuilder.show()
            alertDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

            cancelTv.setOnClickListener {
                alertDialog.dismiss()
            }

            submitTv.setOnClickListener {
                onSubmit(index)
                alertDialog.dismiss()
            }
        }
    }

}
