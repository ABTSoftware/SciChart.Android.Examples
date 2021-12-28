//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2021. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// Style3DChartFragment.kt is part of SCICHART®, High Performance Scientific Charts
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

package com.scichart.examples.fragments.examples3d.style3DChart.kt

import com.scichart.charting3d.common.utils.FontUtil3D
import com.scichart.charting3d.visuals.SciChartSurface3D
import com.scichart.drawing.utility.ColorUtil.*
import com.scichart.examples.R
import com.scichart.examples.fragments.base.ExampleSingleChart3DBaseFragment
import com.scichart.examples.utils.scichartExtensions.*

class Style3DChartFragment : ExampleSingleChart3DBaseFragment() {
    override fun initExample(surface3d: SciChartSurface3D) {
        val font = "RobotoCondensed-BoldItalic"
        FontUtil3D.registerFont(String.format("/system/fonts/%s.ttf", font))

        surface3d.suspendUpdates {
            setBackgroundResource(R.drawable.example_custom_3d_chart_background)

            xAxis = numericAxis3D {
                minorsPerMajor = 5
                maxAutoTicks = 7
                textSize = 13f
                textColor = Lime
                textFont = font
                axisBandsStyle = SolidBrushStyle(DarkOliveGreen)
                majorTickLineStyle = SolidPenStyle(Lime)
                majorTickLineLength = 8f
                minorTickLineStyle = SolidPenStyle(MediumVioletRed)
                minorTickLineLength = 4f
                majorGridLineStyle = SolidPenStyle(Lime)
                minorGridLineStyle = SolidPenStyle(DarkViolet)
            }
            yAxis = numericAxis3D {
                minorsPerMajor = 5
                maxAutoTicks = 7
                textSize = 13f
                textColor = Firebrick
                textFont = font
                axisBandsStyle = SolidBrushStyle(Tomato)
                majorTickLineStyle = SolidPenStyle(Firebrick)
                majorTickLineLength = 8f
                minorTickLineStyle = SolidPenStyle(IndianRed)
                minorTickLineLength = 4f
                majorGridLineStyle = SolidPenStyle(DarkGreen)
                minorGridLineStyle = SolidPenStyle(DarkSkyBlue)
            }
            zAxis = numericAxis3D {
                minorsPerMajor = 5
                maxAutoTicks = 7
                textSize = 13f
                textColor = PaleVioletRed
                textFont = font
                axisBandsStyle = SolidBrushStyle(GreenYellow)
                majorTickLineStyle = SolidPenStyle(PaleVioletRed)
                majorTickLineLength = 8f
                minorTickLineStyle = SolidPenStyle(Chartreuse)
                minorTickLineLength = 4f
                majorGridLineStyle = SolidPenStyle(Beige)
                minorGridLineStyle = SolidPenStyle(Brown)
            }

            chartModifiers { defaultModifiers3D() }
        }
    }
}