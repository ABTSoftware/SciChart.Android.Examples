//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2017. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// TraderDialogFragment.kt is part of the SCICHART® Showcases. Permission is hereby granted
// to modify, create derivative works, distribute and publish any part of this source
// code whether for commercial, private or personal use.
//
// The SCICHART® Showcases are distributed in the hope that they will be useful, but
// without any warranty. It is provided "AS IS" without warranty of any kind, either
// expressed or implied.
//******************************************************************************

package com.scichart.scishowcase.views

import android.app.AlertDialog
import android.app.Dialog
import android.app.DialogFragment
import android.os.Bundle
import com.scichart.scishowcase.R

class TraderDialogFragment(private val selectedItems: BooleanArray, private val dialogListener: (selectedItems: BooleanArray) -> Unit) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(activity).apply {
            setTitle("Configure Charts")
            setMultiChoiceItems(R.array.traderOptions, selectedItems, { _, which, isChecked ->
                selectedItems[which] = isChecked
            })
            setPositiveButton("OK", { _, _ ->
                dialogListener.invoke(selectedItems)
            })
            setNegativeButton("Cancel", null)
        }.create()
    }
}