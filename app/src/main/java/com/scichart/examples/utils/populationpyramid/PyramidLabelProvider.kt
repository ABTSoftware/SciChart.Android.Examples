//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2022. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// PyramidLabelProvider.kt is part of SCICHART®, High Performance Scientific Charts
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

package com.scichart.examples.utils.populationpyramid

import com.scichart.charting.numerics.labelProviders.LabelFormatterBase
import com.scichart.charting.visuals.axes.INumericAxis

class PyramidLabelProvider: LabelFormatterBase<INumericAxis>()  {
    override fun update(iAxisCore: INumericAxis?) {}
    override fun formatLabel(dataValue: Double): CharSequence {
        // return a formatting string for tick labels
        return when(dataValue){
            0.0 -> "100+"
            1.0 -> "95-99"
            2.0 -> "90-94"
            3.0 -> "85-89"
            4.0 -> "80-84"
            5.0 -> "75-79"
            6.0 -> "70-74"
            7.0 -> "65-69"
            8.0 -> "60-64"
            9.0 -> "55-59"
            10.0 -> "50-54"
            11.0 -> "45-49"
            12.0 -> "40-44"
            13.0 -> "35-39"
            14.0 -> "30-34"
            15.0 -> "25-29"
            16.0 -> "20-24"
            17.0 -> "15-19"
            18.0 -> "10-14"
            19.0 -> "5-9"
            20.0 -> "0-4"
            else -> ""
        }
    }

    override fun formatCursorLabel(dataValue: Double): CharSequence {
        // return a formatting string for modifiers' axis labels
        return formatLabel(dataValue)
    }
}