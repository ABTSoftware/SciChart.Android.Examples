//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2022. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// PopulationPyramidUtil.kt is part of SCICHART®, High Performance Scientific Charts
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

import android.app.Activity
import java.io.BufferedReader
import java.io.InputStreamReader

data class PopData(
    val year: String,
    val population: List<Double?>
)

class PopulationPyramidUtil {

    companion object {
        fun readCsv(fileName: String, isMale: Boolean, activity: Activity): List<PopData> {
            val inputStream = InputStreamReader(activity.assets.open(fileName))
            val reader = BufferedReader(inputStream)

            val total: StringBuilder = StringBuilder()
            var line: String?

            val popData = mutableListOf<PopData>()

            while (reader.readLine().also { line = it } != null) {
                total.append(line).append('\n')
                line?.let {
                    val data = it.split(Regex(","))
                    val pop = PopData(
                        year = data.getOrNull(0) ?: "",
                        population = data.drop(1).map { p ->
                            if (isMale) {
                                p.toDoubleOrNull() ?: 0.0
                            } else {
                                (p.toDoubleOrNull() ?: 0.0) * -1.0
                            }
                        }
                    )
                    popData.add(pop)
                }
            }

            return popData
        }
    }
}