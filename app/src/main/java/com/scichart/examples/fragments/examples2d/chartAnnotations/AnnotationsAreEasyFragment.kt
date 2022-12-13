//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2021. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// LineChartFragment.kt is part of SCICHART®, High Performance Scientific Charts
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

import android.content.Context
import android.graphics.*
import android.view.Gravity
import android.view.View
import com.scichart.charting.visuals.SciChartSurface
import com.scichart.charting.visuals.annotations.*
import com.scichart.data.model.DoubleRange
import com.scichart.drawing.common.FontStyle
import com.scichart.drawing.utility.ColorUtil
import com.scichart.examples.R
import com.scichart.examples.fragments.base.ExampleSingleChartBaseFragment
import com.scichart.examples.utils.scichartExtensions.*

class AnnotationsAreEasyFragment : ExampleSingleChartBaseFragment() {

    override fun showDefaultModifiersInToolbar(): Boolean = false

    override fun initExample(surface: SciChartSurface) {
        surface.theme = R.style.SciChart_NavyBlue

        surface.suspendUpdates {
            xAxes { numericAxis {
                visibleRange = DoubleRange(0.0, 10.0)
                growBy = DoubleRange(0.1, 0.1)
                textFormatting = "0.0#"
            }}
            yAxes { numericAxis {
                visibleRange = DoubleRange(0.0, 10.0)
                growBy = DoubleRange(0.1, 0.1)
                textFormatting = "0.0#"
            }}

            annotations {
                // Watermark
                textAnnotation {
                    x1 = 0.5; y1 = 0.5
                    fontStyle = FontStyle(Typeface.DEFAULT_BOLD, 42f, 0x22FFFFFF, true)
                    coordinateMode = AnnotationCoordinateMode.Relative
                    horizontalAnchorPoint = HorizontalAnchorPoint.Center
                    verticalAnchorPoint = VerticalAnchorPoint.Center
                    text = "Create \n Watermarks"
                    textGravity = Gravity.CENTER
                }
                // Text annotations
                textAnnotation {
                    x1 = 0.3; y1 = 9.7
                    fontStyle = FontStyle(24f, ColorUtil.White)
                    text = "Annotations are Easy!"
                }
                textAnnotation {
                    x1 = 1.0; y1 = 9.0
                    fontStyle = FontStyle(10f, ColorUtil.White)
                    text = "You can create text"
                }
                // Text with Anchor Points
                textAnnotation {
                    x1 = 5.0; y1 = 8.0
                    horizontalAnchorPoint = HorizontalAnchorPoint.Center
                    verticalAnchorPoint = VerticalAnchorPoint.Bottom
                    text = "Anchor Center (X1, Y1)"
                }
                textAnnotation {
                    x1 = 5.0; y1 = 8.0
                    horizontalAnchorPoint = HorizontalAnchorPoint.Right
                    verticalAnchorPoint = VerticalAnchorPoint.Top
                    text = "Anchor Right"
                }
                textAnnotation {
                    x1 = 5.0; y1 = 8.0
                    horizontalAnchorPoint = HorizontalAnchorPoint.Left
                    verticalAnchorPoint = VerticalAnchorPoint.Top
                    text = "or Anchor Left"
                }
                // Line and LineArrow annotation
                textAnnotation {
                    text = "Draw Lines with \nor without arrows"
                    x1 = 0.3; y1 = 6.1
                    fontStyle = FontStyle(12f, ColorUtil.White)
                    verticalAnchorPoint = VerticalAnchorPoint.Bottom
                }
                lineAnnotation {
                    x1 = 1.0; y1 = 4.0; x2 = 2.0; y2 = 6.0
                    stroke = SolidPenStyle(0xFF68bcae, 2f)
                }
                lineArrowAnnotation {
                    x1 = 1.2; y1 = 3.8; x2 = 2.5; y2 = 6.0
                    stroke = SolidPenStyle(0xFF68bcae, 2f)
                    headLength = 4f
                    headWidth = 8f
                }
                // Boxes
                textAnnotation {
                    text = "Draw Boxes"
                    x1 = 3.5; y1 = 6.1
                    fontStyle = FontStyle(12f, ColorUtil.White)
                    verticalAnchorPoint = VerticalAnchorPoint.Bottom
                }
                boxAnnotation {
                    x1 = 3.5; y1 = 4.0; x2 = 5.0; y2 = 5.0
                    setBackgroundResource(R.drawable.example_box_annotation_background_1)
                }
                boxAnnotation {
                    x1 = 4.0; y1 = 4.5; x2 = 5.5; y2 = 5.5
                    setBackgroundResource(R.drawable.example_box_annotation_background_2)
                }
                boxAnnotation {
                    x1 = 4.5; y1 = 5.0; x2 = 6.0; y2 = 6.0
                    setBackgroundResource(R.drawable.example_box_annotation_background_3)
                }
                // Custom Shapes
                textAnnotation {
                    text = "Or Custom Shapes"
                    x1 = 7.0; y1 = 6.1
                    fontStyle = FontStyle(12f, ColorUtil.White)
                    verticalAnchorPoint = VerticalAnchorPoint.Bottom
                }
                customAnnotation {
                    x1 = 8.0; y1 = 5.5
                    setContentId(R.layout.example_custom_annotation_view)
                }
                customAnnotation {
                    x1 = 8.0; y1 = 5.5
                    setContentView(CustomView2(context))
                }
                // Horizontal Lines
                horizontalLineAnnotation {
                    x1 = 5.0; y1 = 3.2
                    horizontalGravity = Gravity.END
                    stroke = SolidPenStyle(0xFF47bde6, 2f)
                    annotationLabels {
                        annotationLabel {
                            labelPlacement = LabelPlacement.TopLeft
                            text = "Right Aligned, with text on left"
                        }
                    }
                }
                horizontalLineAnnotation {
                    x1 = 7.0; y1 = 2.8
                    stroke = SolidPenStyle(0xFF47bde6, 2f)
                    annotationLabels {
                        annotationLabel { labelPlacement = LabelPlacement.Axis }
                    }
                }
                // Vertical Lines
                verticalLineAnnotation {
                    x1 = 9.0; y1 = 4.0
                    verticalGravity = Gravity.BOTTOM
                    stroke = SolidPenStyle(0xFFae418d, 2f)
                    annotationLabels {
                        annotationLabel { labelPlacement = LabelPlacement.Auto }
                    }
                }
                verticalLineAnnotation {
                    x1 = 9.5; y1 = 3.0
                    stroke = SolidPenStyle(0xFFae418d, 2f)
                    annotationLabels {
                        annotationLabel { labelPlacement = LabelPlacement.Auto }
                        annotationLabel {
                            labelPlacement = LabelPlacement.TopRight
                            text = "Bottom-aligned"
                            rotationAngle = 90f
                        }
                    }
                }
            }
            chartModifiers { defaultModifiers() }
        }
    }

    class CustomView2(context: Context?) : View(context) {
        private val path = Path()
        private val paintFill = Paint()
        private val paintStroke = Paint()

        override fun onDraw(canvas: Canvas) {
            super.onDraw(canvas)

            canvas.drawPath(path, paintFill)
            canvas.drawPath(path, paintStroke)
        }

        init {
            paintFill.style = Paint.Style.FILL
            paintFill.color = FILL_COLOR
            paintStroke.style = Paint.Style.STROKE
            paintStroke.color = STROKE_COLOR
            path.moveTo(0f, 15f)
            path.lineTo(10f, 15f)
            path.lineTo(10f, 0f)
            path.lineTo(20f, 0f)
            path.lineTo(20f, 15f)
            path.lineTo(30f, 15f)
            path.lineTo(15f, 30f)
            path.lineTo(0f, 15f)
            minimumHeight = 50
            minimumWidth = 50
        }

        companion object {
            private val FILL_COLOR = Color.parseColor("#57B22020")
            private val STROKE_COLOR = Color.parseColor("#FF990000")
        }
    }
}