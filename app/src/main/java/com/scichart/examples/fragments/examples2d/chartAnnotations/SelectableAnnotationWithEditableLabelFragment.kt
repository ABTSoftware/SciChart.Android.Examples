//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2024. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// SelectableAnnotationWithEditableLabelFragment.kt is part of SCICHART®, High Performance Scientific Charts
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

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.scichart.charting.modifiers.AxisDragModifierBase
import com.scichart.charting.modifiers.OnAnnotationCreatedListener
import com.scichart.charting.visuals.annotations.EllipseResizingGrip
import com.scichart.charting.visuals.annotations.IAnnotation
import com.scichart.charting.visuals.annotations.LineAnnotation
import com.scichart.charting.visuals.annotations.OnAnnotationDragListener
import com.scichart.charting.visuals.annotations.OnAnnotationSelectionChangeListener
import com.scichart.charting.visuals.annotations.TextAnnotation
import com.scichart.charting.visuals.pointmarkers.EllipsePointMarker
import com.scichart.charting.visuals.pointmarkers.IPointMarker
import com.scichart.charting.visuals.pointmarkers.TrianglePointMarker
import com.scichart.charting.visuals.renderableSeries.XyScatterRenderableSeries
import com.scichart.data.model.DoubleRange
import com.scichart.examples.R
import com.scichart.examples.data.RandomWalkGenerator
import com.scichart.examples.databinding.FragmentDynamicLineAnnotationWithLabelBinding
import com.scichart.examples.fragments.base.ExampleBaseFragment
import com.scichart.examples.utils.interpolator.DefaultInterpolator
import com.scichart.examples.utils.scichartExtensions.SolidBrushStyle
import com.scichart.examples.utils.scichartExtensions.SolidPenStyle
import com.scichart.examples.utils.scichartExtensions.chartModifiers
import com.scichart.examples.utils.scichartExtensions.numericAxis
import com.scichart.examples.utils.scichartExtensions.pinchZoomModifier
import com.scichart.examples.utils.scichartExtensions.rSeries
import com.scichart.examples.utils.scichartExtensions.renderableSeries
import com.scichart.examples.utils.scichartExtensions.setSize
import com.scichart.examples.utils.scichartExtensions.suspendUpdates
import com.scichart.examples.utils.scichartExtensions.waveAnimation
import com.scichart.examples.utils.scichartExtensions.xAxes
import com.scichart.examples.utils.scichartExtensions.xAxisDragModifier
import com.scichart.examples.utils.scichartExtensions.xyDataSeries
import com.scichart.examples.utils.scichartExtensions.yAxes
import com.scichart.examples.utils.scichartExtensions.yAxisDragModifier
import com.scichart.examples.utils.scichartExtensions.zoomExtentsModifier
import java.util.Random


class SelectableAnnotationWithEditableLabelFragment : ExampleBaseFragment<FragmentDynamicLineAnnotationWithLabelBinding>() ,
    OnAnnotationCreatedListener {


    private val random = Random()
    private val lineToTextMap = mutableMapOf<LineAnnotation, TextAnnotation>()
    private var lineAnnotation: LineAnnotation? = null
    private var isDrawingMode = false
    private var selectedAnnotation: IAnnotation? = null
    override fun initExample(binding: FragmentDynamicLineAnnotationWithLabelBinding) {
        val rSeries1 = getScatterRenderableSeries(TrianglePointMarker(), 0xFF47bde6, false)

        addHelperText()

        binding.surface.suspendUpdates {
            xAxes { numericAxis { growBy = DoubleRange(0.1, 0.1) }}
            yAxes { numericAxis { growBy = DoubleRange(0.1, 0.1) }}
            renderableSeries {
                rSeries(rSeries1)
            }
            chartModifiers {
                zoomExtentsModifier()
                pinchZoomModifier()
                xAxisDragModifier { receiveHandledEvents = true }
                yAxisDragModifier { dragMode = AxisDragModifierBase.AxisDragMode.Pan }
            }

            rSeries1.waveAnimation { interpolator = DefaultInterpolator.getInterpolator(); duration = 500; startDelay = 100 }

            val addButton: Button = binding.addLineAnnotation // Assume this button is in your layout
            addButton.setOnClickListener {
                isDrawingMode = true
                addButton.text = "Drawing Mode: ON"
            }

            setDefaultData()

            // Set touch listener for drawing lines
            binding.surface.setOnTouchListener { _, event ->
                if (!isDrawingMode) return@setOnTouchListener false

                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        startDrawingLine(event.x, event.y)
                    }
                    MotionEvent.ACTION_MOVE -> {
                        updateLineEnd(event.x, event.y)
                    }
                    MotionEvent.ACTION_UP -> {
                        finishDrawingLine(true)
                        isDrawingMode = false
                        addButton.text = "Add Line"
                    }
                }
                true
            }
        }
    }

    private fun addHelperText() {
        val textAnnotation = sciChartBuilder.newTextAnnotation()
            .withText(getString(R.string.dynamic_line_annotation_helper_text))
            .withX1(0).withY1(2.4)
            .withFontStyle(14f, Color.WHITE)
            .build()
        binding.surface.annotations.add(textAnnotation)
    }

    private fun setDefaultData() {
        val xCoordinateCalculator = binding.surface.xAxes[0].currentCoordinateCalculator
        val yCoordinateCalculator = binding.surface.yAxes[0].currentCoordinateCalculator

        val x1 = xCoordinateCalculator.getCoordinate(40.0)
        val x2 = xCoordinateCalculator.getCoordinate(90.0)

        val y1 = yCoordinateCalculator.getCoordinate(1.8)
        val y2 = yCoordinateCalculator.getCoordinate(0.0)

        startDrawingLine(x1, y1)
        updateLineEnd(x2, y2)
        finishDrawingLine(false)
    }

    private fun getScatterRenderableSeries(marker: IPointMarker, color: Long, negative: Boolean): XyScatterRenderableSeries {
        val dataSeriesName = if (marker is EllipsePointMarker)
            if (negative) "Negative Ellipse" else "Positive Ellipse"
        else if (negative) "Negative" else "Positive"

        val biasRandom = Random(100)
        val randomWalkSeries =
            RandomWalkGenerator(100).setBias(biasRandom.nextDouble() / 100).getRandomWalkSeries(100)

        return XyScatterRenderableSeries().apply {
            xyDataSeries<Double, Double> {
                seriesName = dataSeriesName
                append(randomWalkSeries.xValues, randomWalkSeries.yValues)
            }
            strokeStyle = SolidPenStyle(color)
            pointMarker = marker.apply {
                setSize(6)
                strokeStyle = SolidPenStyle(0xFFFFFFFF, 0.1f)
                fillStyle = SolidBrushStyle(color)
            }
        }
    }

    private fun getRandom(min: Double, max: Double): Double {
        return min + (max - min) * random.nextDouble()
    }

    override fun inflateBinding(inflater: LayoutInflater): FragmentDynamicLineAnnotationWithLabelBinding {
        return FragmentDynamicLineAnnotationWithLabelBinding.inflate(inflater)
    }
    private fun startDrawingLine(x: Float, y: Float) {
        // Get the coordinate calculator for the X and Y axes
        val xCoordinateCalculator = binding.surface.xAxes[0].currentCoordinateCalculator
        val yCoordinateCalculator = binding.surface.yAxes[0].currentCoordinateCalculator

        // Convert pixel coordinates to data values
        val xValue = xCoordinateCalculator.getDataValue(x.toDouble().toFloat())
        val yValue = yCoordinateCalculator.getDataValue(y.toDouble().toFloat())

        // Create and add the line annotation
        lineAnnotation = LineAnnotation(requireContext()).apply {
            this.x1 = xValue
            this.y1 = yValue
            this.x2 = xValue
            this.y2 = yValue
            stroke = SolidPenStyle(Color.RED)
        }
        lineAnnotation?.resizingGrip = EllipseResizingGrip(
            0x00FFFFFF, 0f, 0x00FFFFFF, 0f
        )
        binding.surface.annotations.add(lineAnnotation)
    }


    private fun updateLineEnd(x: Float, y: Float) {
        lineAnnotation?.let {
            val xCoordinateCalculator = binding.surface.xAxes[0].currentCoordinateCalculator
            val yCoordinateCalculator = binding.surface.yAxes[0].currentCoordinateCalculator

            // Convert pixel coordinates to data values
            val xValue = xCoordinateCalculator.getDataValue(x.toDouble().toFloat())
            val yValue = yCoordinateCalculator.getDataValue(y.toDouble().toFloat())
            it.x2 = xValue
            it.y2 = yValue
            onAnnotationCreated(lineAnnotation!!)
            binding.surface.invalidateElement()
        }
    }

    private fun finishDrawingLine(showDialog: Boolean) {
        lineAnnotation?.let { line ->
            // Clear current line to avoid further modifications
            lineAnnotation = null
            line?.setIsEditable(true)
            // Create and add placeholder text annotation
            val initialText = "+ Add text"
            val textAnnotation = addPlaceholderText(line, initialText)
            lineToTextMap[line] = textAnnotation

            // Set position with padding relative to the line
            setPositionRelativeToLine(textAnnotation, line)
            textAnnotation.setIsHidden(true)
            // Show text input dialog with the current text (if any)
            if(showDialog){
                showTextInputDialog(textAnnotation.text.toString()) { userText ->
                    if (userText.isNotEmpty()) {
                        // If user text is entered, set it
                        textAnnotation.text = userText
                    } else if (textAnnotation.text == initialText) {
                        // If the text annotation was still the placeholder, keep it hidden
                        textAnnotation.setIsHidden(true)
                    } else {
                        // If no text entered but was previously modified, keep the existing text
                        textAnnotation.text = textAnnotation.text
                    }
                }
            } else {
                line.isSelected = true
            }
        }
    }
    override fun onAnnotationCreated(newAnnotation: IAnnotation) {

        if (newAnnotation is LineAnnotation) {
            //  newAnnotation.isSelected =true;
            val initialText = "+ Add text"
            val textAnnotation = addPlaceholderText(newAnnotation,initialText)
            lineToTextMap[newAnnotation] = textAnnotation
            textAnnotation.setIsHidden(true)
            newAnnotation.setIsEditable(true)

            newAnnotation.setOnAnnotationSelectionChangeListener(object :
                OnAnnotationSelectionChangeListener {

                override fun onSelected(annotation: IAnnotation) {

                    deselectAllAnnotations()
                    selectedAnnotation = annotation
                    lineToTextMap[annotation]?.isActivated

                    lineToTextMap[annotation]?.setIsHidden(false)

                }

                override fun onUnselected(p0: IAnnotation) {
                    lineToTextMap[p0]?.setIsHidden(true)

                }
            })


            newAnnotation.setOnAnnotationDragListener(object : OnAnnotationDragListener {

                override fun onDragStarted(annotation: IAnnotation?) {
                    if (annotation is LineAnnotation) {
                        // Example: Log the drag start
                        newAnnotation.setIsEditable(false)
                    }
                }

                override fun onDragEnded(p0: IAnnotation?) {
                    newAnnotation.setIsEditable(true)
                    if (p0 is LineAnnotation) {
                        updateTextPosition(p0, lineToTextMap[p0]!!)
                    }
                }

                override fun onDragDelta(annotation: IAnnotation?, deltaX: Float, deltaY: Float) {
                    newAnnotation.setIsEditable(false)
                    if (annotation is LineAnnotation) {
                        // Example: Move the text annotation with the line annotation
                        lineToTextMap[annotation]?.let { textAnnotation ->
                            val lineAnnotation = annotation as LineAnnotation
                            setPositionRelativeToLine(textAnnotation, lineAnnotation)
                        }
                    }
                }
            })


        }
    }



    private fun updateTextPosition(lineAnnotation: LineAnnotation, textAnnotation: TextAnnotation) {
        setPositionRelativeToLine(textAnnotation, lineAnnotation)
    }
    private fun deselectAllAnnotations() {
        selectedAnnotation = null
        lineToTextMap.values.forEach { it.isHidden }
    }


    private fun addPlaceholderText(lineAnnotation: LineAnnotation, initialText: String): TextAnnotation {
        // Create and configure the text annotation
        val textAnnotation = TextAnnotation(requireContext()).apply {
            setText(initialText)

            // Set a click listener to show the text input dialog when the annotation is tapped
            setOnClickListener {
                showTextInputDialog(text.toString()) { userText ->
                    if (userText.isNotEmpty()) {
                        setText(userText)
                    } // If user input is empty, keep the existing text
                }
            }
        }

        // Position the text annotation relative to the line annotation
        setPositionRelativeToLine(textAnnotation, lineAnnotation)

        // Add the text annotation to the chart
        binding.surface.annotations.add(textAnnotation)

        return textAnnotation
    }

    private fun setPositionRelativeToLine(textAnnotation: TextAnnotation, lineAnnotation: LineAnnotation) {
        val x1 = lineAnnotation.x1 as Double
        val y1 = lineAnnotation.y1 as Double
        val x2 = lineAnnotation.x2 as Double
        val y2 = lineAnnotation.y2 as Double



        // Calculate the midpoint of the line
        val midX = (x1 + x2) / 2
        val midY = (y1 + y2) / 2

        // Start with a small padding

        // Calculate the angle of the line
        textAnnotation.x1 = midX
        textAnnotation.y1 = midY

        // Apply left padding to the text
        val leftPadding = 5.0  // Adjust this value to control the padding distance

        // Calculate the angle of the line
        val angle = Math.atan2(y2 - y1, x2 - x1)

        // Apply a left padding by moving the text slightly to the left along the line direction
        val offsetX = leftPadding * Math.cos(angle)


        // Adjust text position for left padding
        textAnnotation.x1 = midX + offsetX
        textAnnotation.y1 = midY
    }
    private fun showTextInputDialog(currentText: String, onTextEntered: (String) -> Unit) {
        val inflater = LayoutInflater.from(context)
        val dialogView: View = inflater.inflate(R.layout.example_show_add_text_popup_layout, null)
        val input = dialogView.findViewById<EditText>(R.id.editTextInput)

        // Set hint to "Enter Text" if the current text is "+ Add text"
        input.hint = if (currentText == "+ Add text") "Enter Text" else "Enter Text"

        // Set the initial text to the current text
        input.setText(if (currentText == "+ Add text") "" else currentText)

        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("Enter Text")
            .setView(dialogView)
            .setPositiveButton("OK") { _, _ ->
                onTextEntered(input.text.toString())
            }
            .setNegativeButton("Cancel") { _, _ ->
                onTextEntered(currentText) // If canceled, keep the existing text
            }
            .create()

        dialog.window!!.setBackgroundDrawable(
            ContextCompat.getDrawable(context!!, R.drawable.example_popup_background)
        )

        dialog.show()
    }

}