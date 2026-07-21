//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2026. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// Chart3DTest2Activity.kt is part of SCICHART®, High Performance Scientific Charts
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

package com.scichart.examples.fragments.examples3d.aTest.kt

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.scichart.charting3d.visuals.SciChartSurface3D
import com.scichart.data.model.DoubleRange
import com.scichart.examples.R
import com.scichart.examples.data.DataManager
import com.scichart.examples.utils.scichartExtensions.*
import com.scichart.extensions3d.builders.SciChart3DBuilder

class Chart3DTest2Activity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chart_3d_test_2)

        SciChart3DBuilder.init(this)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }
        toolbar.setNavigationOnClickListener { onBackPressed() }

        val surface3d = findViewById<SciChartSurface3D>(R.id.surface3d)
        initChart(surface3d)
    }

    private fun initChart(surface3d: SciChartSurface3D) {
        val dataManager = DataManager.getInstance()

        surface3d.suspendUpdates {
            xAxis = numericAxis3D { growBy = DoubleRange(0.1, 0.1) }
            yAxis = numericAxis3D { growBy = DoubleRange(0.1, 0.1) }
            zAxis = numericAxis3D { growBy = DoubleRange(0.1, 0.1) }

            renderableSeries {
                scatterRenderableSeries3D {
                    ellipsePointMarker3D {
                        fill = 0xFFFF5722.toInt() // orange accent color
                        size = 3f
                    }
                    xyzDataSeries3D<Double, Double, Double> {
                        for (i in 0..999) {
                            val x = dataManager.getGaussianRandomNumber(10.0, 2.0)
                            val y = dataManager.getGaussianRandomNumber(10.0, 2.0)
                            val z = dataManager.getGaussianRandomNumber(10.0, 2.0)
                            append(x, y, z)
                        }
                    }
                }
            }

            chartModifiers { defaultModifiers3D() }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        SciChart3DBuilder.dispose()
    }
}
