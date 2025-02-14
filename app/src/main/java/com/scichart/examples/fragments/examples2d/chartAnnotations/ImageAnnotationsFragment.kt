//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2021. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// ImageAnnotationsFragment.kt is part of SCICHART®, High Performance Scientific Charts
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

package com.scichart.examples.fragments.examples2d.chartAnnotations.kt

import android.graphics.*
import android.view.View
import android.widget.ImageView
import com.scichart.charting.model.AnnotationCollection
import com.scichart.charting.visuals.SciChartSurface
import com.scichart.charting.visuals.annotations.*
import com.scichart.charting.visuals.axes.AxisAlignment
import com.scichart.data.model.DoubleRange
import com.scichart.examples.R
import com.scichart.examples.fragments.base.ExampleSingleChartBaseFragment
import com.scichart.examples.utils.imageannotation.CarSales
import com.scichart.examples.utils.scichartExtensions.*

class ImageAnnotationsFragment : ExampleSingleChartBaseFragment() {

    val data = CarSales.getData()

    override fun showDefaultModifiersInToolbar(): Boolean = false

    override fun initExample(surface: SciChartSurface) {
        surface.theme = R.style.SciChart_Image_Annotation

        val carDataSeries = sciChartBuilder.newXyDataSeries(Double::class.javaObjectType, Double::class.javaObjectType)
            .build()
        val annotationCollection = AnnotationCollection()

        data.forEachIndexed { index, carSalesModel ->
            carDataSeries.append(index.toDouble(), carSalesModel.carsSold.toDouble())

            val imageAnnotation = sciChartBuilder.newImageAnnotation()
                .withX1(index - 0.15)
                .withY1(carSalesModel.carsSold.toDouble() + 1000000)
                .withDesiredSize(30, 24)
                .withImage(carSalesModel.icon)
                .withContentMode(ImageView.ScaleType.FIT_START)
                .build()

            annotationCollection.add(imageAnnotation)
        }

        val titleAnnotation = sciChartBuilder.newTextAnnotation()
            .withX1(-1)
            .withY1(8000000)
            .withText("Top 10 car markets")
            .withFontStyle(Typeface.DEFAULT_BOLD, 22f, Color.WHITE)
            .build()
        annotationCollection.add(titleAnnotation)

        val descriptionAnnotation = sciChartBuilder.newTextAnnotation()
            .withX1(5.5)
            .withY1(9000000)
            .withText("The image annotation can be \nplaced in front or behind the \ngrid lines and can either \nbe fixed or moved along\nwith the chart.")
            .withFontStyle(Typeface.DEFAULT_BOLD, 22f, Color.WHITE)
            .build()
        annotationCollection.add(descriptionAnnotation)


        surface.suspendUpdates {
            xAxes { numericAxis {
                growBy = DoubleRange(0.08,0.08)
                axisAlignment = AxisAlignment.Left
                visibility = View.GONE
                drawMajorGridLines = false
                drawMinorGridLines = false
            }}
            yAxes { numericAxis {
                growBy = DoubleRange(0.0,0.15)
                axisAlignment = AxisAlignment.Bottom
                flipCoordinates = true
                drawMajorGridLines = false
                drawMinorGridLines = false
            }}

            renderableSeries {
                fastColumnRenderableSeries {
                    dataSeries = carDataSeries
                    strokeStyle = SolidPenStyle(0xff1f6f6f, 0.4f)
                    fillBrushStyle = SolidBrushStyle(0xff1f6f6f)
                    dataPointWidth = 0.7
                }
            }

            annotations {
                annotations = annotationCollection
            }
            chartModifiers { defaultModifiers() }
        }
    }
}