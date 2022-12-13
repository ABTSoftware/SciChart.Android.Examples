//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2022. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// TextLabelFormatter.kt is part of SCICHART®, High Performance Scientific Charts
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

import com.scichart.charting.numerics.labelProviders.LabelFormatterBase
import com.scichart.charting.visuals.axes.INumericAxis

class TextLabelFormatter: LabelFormatterBase<INumericAxis>() {
    private var labelList: List<String> = emptyList()

    override fun update(iAxisCore: INumericAxis?) {}
    override fun formatLabel(dataValue: Double): CharSequence {
        // return a formatting string for tick labels
        return labelList.getOrNull(dataValue.toInt()) ?: ""
    }

    override fun formatCursorLabel(dataValue: Double): CharSequence {
        // return a formatting string for modifiers' axis labels
        return formatLabel(dataValue)
    }

    fun setList(newList: List<String>){
        labelList = newList
    }
}