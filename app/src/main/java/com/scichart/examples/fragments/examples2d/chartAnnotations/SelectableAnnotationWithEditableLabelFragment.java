//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2021. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// SelectableAnnotationWithEditableLabelFragment.java is part of SCICHART®, High Performance Scientific Charts
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

package com.scichart.examples.fragments.examples2d.chartAnnotations;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.ColorInt;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;

import com.scichart.charting.model.dataSeries.IXyDataSeries;
import com.scichart.charting.modifiers.AxisDragModifierBase;
import com.scichart.charting.modifiers.OnAnnotationCreatedListener;
import com.scichart.charting.numerics.coordinateCalculators.ICoordinateCalculator;
import com.scichart.charting.visuals.SciChartSurface;
import com.scichart.charting.visuals.annotations.BoxAnnotation;
import com.scichart.charting.visuals.annotations.EllipseResizingGrip;
import com.scichart.charting.visuals.annotations.IAnnotation;
import com.scichart.charting.visuals.annotations.LineAnnotation;
import com.scichart.charting.visuals.annotations.OnAnnotationDragListener;
import com.scichart.charting.visuals.annotations.OnAnnotationSelectionChangeListener;
import com.scichart.charting.visuals.annotations.TextAnnotation;
import com.scichart.charting.visuals.axes.IAxis;
import com.scichart.charting.visuals.pointmarkers.EllipsePointMarker;
import com.scichart.charting.visuals.pointmarkers.IPointMarker;
import com.scichart.charting.visuals.pointmarkers.TrianglePointMarker;
import com.scichart.charting.visuals.renderableSeries.XyScatterRenderableSeries;
import com.scichart.core.framework.UpdateSuspender;

import com.scichart.drawing.common.SolidPenStyle;
import com.scichart.examples.R;
import com.scichart.examples.data.DoubleSeries;
import com.scichart.examples.data.RandomWalkGenerator;
import com.scichart.examples.databinding.FragmentDynamicLineAnnotationWithLabelBinding;
import com.scichart.examples.fragments.base.ExampleBaseFragment;
import com.scichart.examples.utils.interpolator.DefaultInterpolator;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class SelectableAnnotationWithEditableLabelFragment extends ExampleBaseFragment<FragmentDynamicLineAnnotationWithLabelBinding>
        implements OnAnnotationCreatedListener {

    private final Random random = new Random();
    private final Map<LineAnnotation, TextAnnotation> lineToTextMap = new HashMap<>();
    private LineAnnotation lineAnnotation = null;
    private boolean isDrawingMode = false;
    private IAnnotation selectedAnnotation = null;

    @Override
    protected FragmentDynamicLineAnnotationWithLabelBinding inflateBinding(LayoutInflater inflater) {
        return FragmentDynamicLineAnnotationWithLabelBinding.inflate(inflater);
    }

    @Override
    protected void initExample(FragmentDynamicLineAnnotationWithLabelBinding binding) {
        final IAxis xAxis = sciChartBuilder.newNumericAxis().withGrowBy(0.1d, 0.1d).build();
        final IAxis yAxis = sciChartBuilder.newNumericAxis().withGrowBy(0.1d, 0.1d).build();
        XyScatterRenderableSeries rSeries1 = getScatterRenderableSeries(new TrianglePointMarker(), 0xFF47bde6, false);

        final SciChartSurface surface = binding.surface;

        addHelperText();

        UpdateSuspender.using(surface, () -> {
            Collections.addAll(surface.getXAxes(), xAxis);
            Collections.addAll(surface.getYAxes(), yAxis);
            Collections.addAll(surface.getRenderableSeries(), rSeries1);
            Collections.addAll(surface.getChartModifiers(), sciChartBuilder.newModifierGroup()
                    .withZoomExtentsModifier().build()
                    .withPinchZoomModifier().build()
                    .withXAxisDragModifier().withReceiveHandledEvents(true).build()
                    .withYAxisDragModifier().withDragMode(AxisDragModifierBase.AxisDragMode.Pan).build()
                    .build());

            sciChartBuilder.newAnimator(rSeries1).withWaveTransformation().withInterpolator(DefaultInterpolator.getInterpolator()).withDuration(500).withStartDelay(100).start();

            Button addButton = binding.addLineAnnotation; // Assume this button is in your layout
            addButton.setOnClickListener(view -> {
                isDrawingMode = true;
                addButton.setText("Drawing Mode: ON");
            });

            setDefaultData();
            // Set touch listener for drawing lines
            binding.surface.setOnTouchListener((view, event) -> {
                if (!isDrawingMode) return false;

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        startDrawingLine(event.getX(), event.getY());
                        break;
                    case MotionEvent.ACTION_MOVE:
                        updateLineEnd(event.getX(), event.getY());
                        break;
                    case MotionEvent.ACTION_UP:
                        finishDrawingLine(true);
                        isDrawingMode = false;
                        addButton.setText("Add Line");
                        break;
                }
                return true;
            });
        });
    }

    private void addHelperText() {
        TextAnnotation textAnnotation = sciChartBuilder.newTextAnnotation()
                .withText(getString(R.string.dynamic_line_annotation_helper_text))
                .withX1(0).withY1(2.4)
                .withFontStyle(14, Color.WHITE)
                .build();
        binding.surface.getAnnotations().add(textAnnotation);
    }

    private void setDefaultData() {
        ICoordinateCalculator xCoordinateCalculator = binding.surface.getXAxes().get(0).getCurrentCoordinateCalculator();
        ICoordinateCalculator yCoordinateCalculator = binding.surface.getYAxes().get(0).getCurrentCoordinateCalculator();

        float x1 = xCoordinateCalculator.getCoordinate(40);
        float x2 = xCoordinateCalculator.getCoordinate(90);

        float y1 = yCoordinateCalculator.getCoordinate(1.8);
        float y2 = yCoordinateCalculator.getCoordinate(0);

        startDrawingLine(x1, y1);
        updateLineEnd(x2, y2);
        finishDrawingLine(false);
    }

    private XyScatterRenderableSeries getScatterRenderableSeries(IPointMarker pointMarker, @ColorInt int color, boolean negative) {
        final String seriesName = pointMarker instanceof EllipsePointMarker ?
                negative ? "Negative Ellipse" : "Positive Ellipse" :
                negative ? "Negative" : "Positive";

        final IXyDataSeries<Double, Double> dataSeries = sciChartBuilder.newXyDataSeries(Double.class, Double.class).withSeriesName(seriesName).build();

        final Random biasRandom = new Random(100);
        final DoubleSeries randomWalkSeries = new RandomWalkGenerator(100).setBias(biasRandom.nextDouble() / 100).getRandomWalkSeries(100);
        dataSeries.append(randomWalkSeries.xValues, randomWalkSeries.yValues);

        return sciChartBuilder.newScatterSeries()
                .withDataSeries(dataSeries)
                .withStrokeStyle(color)
                .withPointMarker(sciChartBuilder.newPointMarker(pointMarker)
                        .withSize(6, 6)
                        .withStroke(0xFFFFFFFF, 0.1f)
                        .withFill(color)
                        .build())
                .build();
    }

    private double getRandom(double min, double max) {
        return min + (max - min) * random.nextDouble();
    }

    private void startDrawingLine(float x, float y) {
        // Get the coordinate calculator for the X and Y axes
        ICoordinateCalculator xCoordinateCalculator = binding.surface.getXAxes().get(0).getCurrentCoordinateCalculator();
        ICoordinateCalculator yCoordinateCalculator = binding.surface.getYAxes().get(0).getCurrentCoordinateCalculator();

        // Convert pixel coordinates to data values
        double xValue = xCoordinateCalculator.getDataValue(x);
        double yValue = yCoordinateCalculator.getDataValue(y);
        float[] arrayTest = {1.0f,};

        // Create and add the line annotation
        lineAnnotation = new LineAnnotation(getContext());
        lineAnnotation.setX1(xValue);
        lineAnnotation.setY1(yValue);
        lineAnnotation.setX2(xValue);
        lineAnnotation.setY2(yValue);
        lineAnnotation.setStroke(new SolidPenStyle(Color.RED, true, 3f, null ));

        lineAnnotation.setResizingGrip(new EllipseResizingGrip(
                0x00FFFFFF, 0f, 0x00FFFFFF, 0f
        ));

        binding.surface.getAnnotations().add(lineAnnotation);
    }

    private void updateLineEnd(float x, float y) {
        if (lineAnnotation != null) {
            ICoordinateCalculator xCoordinateCalculator = binding.surface.getXAxes().get(0).getCurrentCoordinateCalculator();
            ICoordinateCalculator yCoordinateCalculator = binding.surface.getYAxes().get(0).getCurrentCoordinateCalculator();

            // Convert pixel coordinates to data values
            double xValue = xCoordinateCalculator.getDataValue(x);
            double yValue = yCoordinateCalculator.getDataValue(y);
            lineAnnotation.setX2(xValue);
            lineAnnotation.setY2(yValue);
            onAnnotationCreated(lineAnnotation);
            Context context = getContext();

            binding.surface.invalidateElement();
        }
    }

    private void finishDrawingLine(boolean showDialog) {
        // Clear current line to avoid further modifications
        if (lineAnnotation != null) {
            LineAnnotation line = lineAnnotation;

            // Clear current line to avoid further modifications
            lineAnnotation = null;
            line.setIsEditable(true);

            // Create and add placeholder text annotation
            String initialText = "+ Add text";
            TextAnnotation textAnnotation = addPlaceholderText(line, initialText);

            // Add the mapping from line to text annotation
            lineToTextMap.put(line, textAnnotation);

            // Set position with padding relative to the line
            setPositionRelativeToLine(textAnnotation, line);
            textAnnotation.setIsHidden(true);
            Context context = getContext();
            // Show text input dialog with the current text (if any)
            if(showDialog){
                showTextInputDialog(textAnnotation.getText().toString(),context, new OnTextEnteredListener() {
                    @Override
                    public void onTextEntered(String userText) {
                        if (!userText.isEmpty()) {
                            // If user text is entered, set it
                            textAnnotation.setText(userText);
                        } else if (textAnnotation.getText().equals(initialText)) {
                            // If the text annotation was still the placeholder, keep it hidden
                            textAnnotation.setIsHidden(true);
                        } else {
                            // If no text entered but was previously modified, keep the existing text
                            textAnnotation.setText(textAnnotation.getText());
                        }
                    }
                });
            } else {
                line.setSelected(true);
            }
        }
    }

    @Override
    public void onAnnotationCreated(IAnnotation newAnnotation) {
        if (newAnnotation instanceof LineAnnotation) {
            Log.d("SciChart", "MinY: " + ((LineAnnotation) newAnnotation).getY1() + ", MaxY: " + ((LineAnnotation) newAnnotation).getY1());
            String initialText = "+ Add text";
            TextAnnotation textAnnotation = addPlaceholderText((LineAnnotation) newAnnotation,initialText);
            lineToTextMap.put((LineAnnotation) newAnnotation, textAnnotation);
            textAnnotation.setIsHidden(true);
            ((LineAnnotation) newAnnotation).setIsEditable(true);

            ((LineAnnotation) newAnnotation).setOnAnnotationSelectionChangeListener(new OnAnnotationSelectionChangeListener() {
                @Override
                public void onSelected(IAnnotation annotation) {
                    deselectAllAnnotations();
                    selectedAnnotation = annotation;
                    lineToTextMap.get(annotation).isActivated();
                    lineToTextMap.get(annotation).setIsHidden(false);
                    Log.d("New Annotation", "Yes it is activated");
                }

                @Override
                public void onUnselected(IAnnotation annotation) {
                    lineToTextMap.get(annotation).setIsHidden(true);
                    Log.d("New Annotation", "Yes it is hidden");
                }
            });

            newAnnotation.setOnAnnotationDragListener(new OnAnnotationDragListener() {
                @Override
                public void onDragStarted(IAnnotation annotation) {
                    if (annotation instanceof LineAnnotation) {
                        // Example: Log the drag start
                        newAnnotation.setIsEditable(false);
                        Log.d("SciChart", "Drag started for LineAnnotation");
                    }
                }

                @Override
                public void onDragEnded(IAnnotation annotation) {
                    newAnnotation.setIsEditable(true);
                    if (annotation instanceof LineAnnotation) {
                        // Assuming lineToTextMap is defined elsewhere in your class
                        TextAnnotation textAnnotation = lineToTextMap.get(annotation);
                        if (textAnnotation != null) {
                            updateTextPosition((LineAnnotation) annotation, textAnnotation);


                        }
                    }
                }

                @Override
                public void onDragDelta(IAnnotation annotation, float deltaX, float deltaY) {
                    newAnnotation.setIsEditable(false);
                    if (annotation instanceof LineAnnotation) {
                        // Move the text annotation with the line annotation
                        TextAnnotation textAnnotation = lineToTextMap.get(annotation);
                        if (textAnnotation != null) {
                            setPositionRelativeToLine(textAnnotation, (LineAnnotation) annotation);
                        }
                    }
                }
            });
        }
    }

    private void deselectAllAnnotations() {
        for (TextAnnotation textAnnotation : lineToTextMap.values()) {
            textAnnotation.setIsHidden(true);
            textAnnotation.isActivated();
        }
    }

    private TextAnnotation addPlaceholderText(LineAnnotation lineAnnotation,String initialText) {
        TextAnnotation placeholderText = new TextAnnotation(requireContext());
        placeholderText.setText(initialText);

        // Set an OnClickListener to show the text input dialog
        placeholderText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show dialog and handle user input
                Context context = getContext();
                showTextInputDialog(placeholderText.getText().toString(),context, new OnTextEnteredListener() {
                    @Override
                    public void onTextEntered(String userText) {
                        if (!userText.isEmpty()) {
                            placeholderText.setText(userText);
                        } // If user input is empty, keep the existing text
                    }

                });
            }
        });

        // Position the text annotation relative to the line annotation as needed
        setPositionRelativeToLine(placeholderText, lineAnnotation);

        // Add the annotation to the surface
        binding.surface.getAnnotations().add(placeholderText);

        return placeholderText;
    }

    public interface OnTextEnteredListener {
        void onTextEntered(String userText);
    }

    private void showTextInputDialog(String currentText,Context context,OnTextEnteredListener onTextEnteredListener) {

        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate( R.layout.example_show_add_text_popup_layout, null);
//        EditText input = new EditText(requireContext());

        EditText input = dialogView.findViewById(R.id.editTextInput);
        input.setTextColor(Color.WHITE);
        input.setHint(currentText.equals("+ Add text") ? "Enter Text" : "Enter Text");

        // Set the initial text to the current text
        input.setText(currentText.equals("+ Add text") ? "" : currentText);
        AlertDialog dialog = new AlertDialog.Builder(context)
                .setTitle("Enter Text")
                .setView(dialogView)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        String userText = input.getText().toString();

                        onTextEnteredListener.onTextEntered(userText);  // Trigger the callback
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        onTextEnteredListener.onTextEntered(currentText); // If canceled, keep the existing text
                    }
                })
                .create();
        dialog.getWindow().setBackgroundDrawable(
                ContextCompat.getDrawable(context, R.drawable.example_popup_background) // Set your drawable here
        );
        dialog.show();
    }

    private void updateTextPosition(LineAnnotation lineAnnotation, TextAnnotation textAnnotation) {
        setPositionRelativeToLine(textAnnotation, lineAnnotation);
    }

    private void setPositionRelativeToLine(TextAnnotation textAnnotation, LineAnnotation lineAnnotation) {
        // Cast the x1, y1, x2, y2 values from lineAnnotation to Double
        double x1 = (Double) lineAnnotation.getX1();
        double y1 = (Double) lineAnnotation.getY1();
        double x2 = (Double) lineAnnotation.getX2();
        double y2 = (Double) lineAnnotation.getY2();

        // Calculate the midpoint of the line
        double midX = (x1 + x2) / 2;
        double midY = (y1 + y2) / 2;

        // Set the TextAnnotation's position to the midpoint
        textAnnotation.setX1(midX);
        textAnnotation.setY1(midY);

// Apply left padding to the text
        double leftPadding = 5.0;  // Adjust this value to control the padding distance

// Calculate the angle of the line
        double angle = Math.atan2(y2 - y1, x2 - x1);

// Apply left padding by moving the text slightly to the left along the line direction
        double offsetX = leftPadding * Math.cos(angle);

// Adjust text position for left padding
        textAnnotation.setX1(midX + offsetX);
    }

}