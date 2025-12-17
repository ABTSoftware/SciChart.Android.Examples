//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2021. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// CustomModifierFragment.java is part of SCICHART®, High Performance Scientific Charts
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

import android.graphics.PointF;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;

import androidx.annotation.NonNull;

import com.scichart.charting.model.AnnotationCollection;
import com.scichart.charting.model.dataSeries.OhlcDataSeries;
import com.scichart.charting.modifiers.AnnotationCreationModifier;
import com.scichart.charting.modifiers.DefaultAnnotationFactory;
import com.scichart.charting.modifiers.GestureModifierBase;
import com.scichart.charting.modifiers.OnAnnotationCreatedListener;
import com.scichart.charting.visuals.ISciChartSurface;
import com.scichart.charting.visuals.SciChartSurface;
import com.scichart.charting.visuals.annotations.AnnotationCoordinateMode;
import com.scichart.charting.visuals.annotations.HorizontalAnchorPoint;
import com.scichart.charting.visuals.annotations.IAnnotation;
import com.scichart.charting.visuals.annotations.TextAnnotation;
import com.scichart.charting.visuals.annotations.TradeMarkerAnnotation;
import com.scichart.charting.visuals.annotations.VerticalAnchorPoint;
import com.scichart.charting.visuals.axes.IAxis;
import com.scichart.charting.visuals.renderableSeries.IRenderableSeries;
import com.scichart.core.framework.IUpdateSuspender;
import com.scichart.core.utility.ViewUtil;
import com.scichart.examples.components.SpinnerStringAdapter;
import com.scichart.examples.data.MarketDataService;
import com.scichart.examples.data.PriceSeries;
import com.scichart.examples.databinding.ExampleCreateAnnotationsDynamicallyFragmentBinding;
import com.scichart.examples.fragments.base.ExampleBaseFragment;
import com.scichart.examples.utils.ItemSelectedListenerBase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class CustomModifierFragment extends ExampleBaseFragment<ExampleCreateAnnotationsDynamicallyFragmentBinding> implements OnAnnotationCreatedListener {

    private final AnnotationCreationModifier annotationCreationModifier = new AnnotationCreationModifier();
    private TradeMarkerGestureModifier tradeMarkerModifier;
    private TextAnnotation instructionAnnotation;
    private final List<TextAnnotation> divAnnotations = new ArrayList<>();
    private int selectedAnnotationType = 0; // 0=Box, 1=Line, 2=TradeMarker

    @NonNull
    @Override
    protected ExampleCreateAnnotationsDynamicallyFragmentBinding inflateBinding(@NonNull LayoutInflater inflater) {
        return ExampleCreateAnnotationsDynamicallyFragmentBinding.inflate(inflater);
    }

    @Override
    protected void initExample(ExampleCreateAnnotationsDynamicallyFragmentBinding binding) {
        final Spinner annotationTypeSelector = binding.annotationTypeSelector;
        
        // Create custom array with Box, Line, and Trade Marker
        List<String> annotationTypes = Arrays.asList("BoxAnnotation", "LineAnnotation", "TradeMarker");
        annotationTypeSelector.setAdapter(new SpinnerStringAdapter(getActivity(), annotationTypes));
        annotationTypeSelector.setSelection(0);
        
        annotationTypeSelector.setOnItemSelectedListener(new ItemSelectedListenerBase() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedAnnotationType = position;
                updateModifiers(binding.surface);
            }
        });

        final OhlcDataSeries<Date, Double> dataSeries = sciChartBuilder.newOhlcDataSeries(Date.class, Double.class).build();

        final MarketDataService marketDataService = new MarketDataService(Calendar.getInstance().getTime(), 5, 5);
        final PriceSeries data = marketDataService.getHistoricalData(200);

        dataSeries.append(data.getDateData(), data.getOpenData(), data.getHighData(), data.getLowData(), data.getCloseData());

        final SciChartSurface surface = binding.surface;
        
        // Use a subset of data for better zoom - show last 50 points
        final int totalPoints = dataSeries.getCount();
        final int startIndex = Math.max(0, totalPoints - 50);
        final int visibleStart = startIndex;
        final int visibleEnd = totalPoints;
        
        // Create divergence indicator data for mountain series
        final List<Integer> divAnnotationXPositions = new ArrayList<>();
        
        // Generate smooth oscillator-like data for mountain series
        for (int i = 0; i < totalPoints; i++) {
            // Mark some positions for "Div" annotations
            if (i >= startIndex && i % 25 == 0) {
                divAnnotationXPositions.add(i);
            }
        }
        
        // Add candlestick series (uses default Y-axis)
        Collections.addAll(surface.getRenderableSeries(), sciChartBuilder.newCandlestickSeries()
                .withDataSeries(dataSeries)
                .withOpacity(0.4f)
                .build());
        
        // Set visible range on X-axis to show only last 50 points
        Collections.addAll(surface.getXAxes(), sciChartBuilder.newCategoryDateAxis()
                .withVisibleRange(visibleStart, visibleEnd)
                .withGrowBy(0, 0.1)
                .build());

        // Calculate Y-axis range from visible data for better zoom
        double minPrice = Double.MAX_VALUE;
        double maxPrice = Double.MIN_VALUE;
        for (int i = startIndex; i < totalPoints; i++) {
            final double low = dataSeries.getLowValues().get(i);
            final double high = dataSeries.getHighValues().get(i);
            minPrice = Math.min(minPrice, low);
            maxPrice = Math.max(maxPrice, high);
        }
        // Add some padding
        final double priceRange = maxPrice - minPrice;
        final double yMin = minPrice - priceRange * 0.1;
        final double yMax = maxPrice + priceRange * 0.1;
        
        // Primary Y-axis for candlestick chart (default axis)
        Collections.addAll(surface.getYAxes(), sciChartBuilder.newNumericAxis()
                .withVisibleRange(yMin, yMax)
                .withGrowBy(0d, 0.1d)
                .build());
        
        // Add "Div" text annotations at specific X positions
        // Using RelativeY coordinate mode: X follows data, Y is relative (0-1)
        for (final int xPos : divAnnotationXPositions) {
            final TextAnnotation divAnnotation = sciChartBuilder.newTextAnnotation()
                    .withX1(xPos)
                    .withY1(0.95) // Position near bottom (relative Y coordinate)
                    .withCoordinateMode(AnnotationCoordinateMode.RelativeY)
                    .withText("Div")
                    .withFontStyle(12, 0xFFFFFFFF)
                    .withVerticalAnchorPoint(VerticalAnchorPoint.Bottom)
                    .withHorizontalAnchorPoint(HorizontalAnchorPoint.Center)
                    .build();
            divAnnotations.add(divAnnotation);
            surface.getAnnotations().add(divAnnotation);
        }

        // Setup annotation factory for Box and Line
        DefaultAnnotationFactory annotationFactory = new DefaultAnnotationFactory();
        annotationFactory.setFactoryForAnnotationType(DefaultAnnotationFactory.BOX_ANNOTATION, (parentSurface, annotationType) -> 
            sciChartBuilder.newBoxAnnotation().withBackgroundColor(0x6600cc00).build());
        annotationFactory.setFactoryForAnnotationType(DefaultAnnotationFactory.LINE_ANNOTATION, (parentSurface, annotationType) -> 
            sciChartBuilder.newLineAnnotation().build());

        annotationCreationModifier.setAnnotationFactory(annotationFactory);
        annotationCreationModifier.setAnnotationCreationListener(this);

        // Create trade marker modifier
        tradeMarkerModifier = new TradeMarkerGestureModifier();
        tradeMarkerModifier.setAnnotationCreationListener(this);

        updateModifiers(surface);

        binding.deleteAnnotation.setOnClickListener(v -> {
            final AnnotationCollection annotations = surface.getAnnotations();
            for (int i = annotations.size() - 1; i >= 0; i--) {
                final IAnnotation annotation = annotations.get(i);
                if (annotation.isSelected()) {
                    annotations.remove(i);
                }
            }
        });
    }

    private void updateModifiers(SciChartSurface surface) {
        // Remove all modifiers first
        surface.getChartModifiers().clear();
        
        // Remove instruction annotation if exists
        if (instructionAnnotation != null) {
            surface.getAnnotations().remove(instructionAnnotation);
            instructionAnnotation = null;
        }

        if (selectedAnnotationType == 2) {
            // Trade Marker selected - use gesture modifier and show instructions
            surface.getChartModifiers().add(sciChartBuilder.newModifierGroup().withModifier(tradeMarkerModifier).build());
            showTradeMarkerInstructions(surface);
        } else {
            // Box or Line selected - use annotation creation modifier
            annotationCreationModifier.setAnnotationType(selectedAnnotationType == 0 ? DefaultAnnotationFactory.BOX_ANNOTATION : DefaultAnnotationFactory.LINE_ANNOTATION);
            surface.getChartModifiers().add(sciChartBuilder.newModifierGroup().withModifier(annotationCreationModifier).build());
        }
    }

    private void showTradeMarkerInstructions(SciChartSurface surface) {
        instructionAnnotation = sciChartBuilder.newTextAnnotation()
                .withX1(0.01)
                .withY1(0.05)
                .withCoordinateMode(AnnotationCoordinateMode.Relative)
                .withText("- Tap to place a Buy marker\n- Double-tap to place a Sell marker")
                .withFontStyle(14, 0xFFFFFFFF)
                .withVerticalAnchorPoint(VerticalAnchorPoint.Top)
                .withHorizontalAnchorPoint(HorizontalAnchorPoint.Left)
                .build();
        
        final IUpdateSuspender suspender = surface.suspendUpdates();
        try {
            surface.getAnnotations().add(instructionAnnotation);
        } finally {
            suspender.dispose();
        }
    }

    @Override
    public void onAnnotationCreated(@NonNull IAnnotation newAnnotation) {
        newAnnotation.setIsEditable(true);
    }

    /**
     * Gesture modifier for creating trade markers with tap (buy) and double-tap (sell)
     */
    private class TradeMarkerGestureModifier extends GestureModifierBase {
        private final PointF touchPoint = new PointF();
        private OnAnnotationCreatedListener listener;

        public void setAnnotationCreationListener(OnAnnotationCreatedListener listener) {
            this.listener = listener;
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            createTradeMarker(e, true);
            return true;
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            createTradeMarker(e, false);
            return true;
        }

        private void createTradeMarker(MotionEvent e, boolean isBuy) {
            final ISciChartSurface parentSurface = getParentSurface();
            if (!(parentSurface instanceof SciChartSurface)) return;
            
            final SciChartSurface surface = (SciChartSurface) parentSurface;

            // Convert touch coordinates to chart coordinates
            touchPoint.set(e.getX(), e.getY());
            ViewUtil.translatePoint(surface.getView(), touchPoint, getModifierSurface());

            // Get axes (use default axes)
            final IAxis xAxis = surface.getXAxes().get(0);
            final IAxis yAxis = surface.getYAxes().get(0);

            if (xAxis == null || yAxis == null) return;

            // Convert to data coordinates
            final float x;
            if (xAxis.isHorizontalAxis()) {
                x = touchPoint.x;
            } else {
                x = touchPoint.y;
            }

            // Get the raw X value from touch
            final Comparable<?> rawXValue = xAxis.getDataValue(x);

            // Round to nearest integer to snap to candlestick center
            final int xIndex = Math.round(((Number) rawXValue).floatValue());

            // Use the rounded index as the final X value
            final Comparable<?> xValue = xIndex;

            // Get the candlestick data series from the renderable series
            OhlcDataSeries<?, ?> ohlcDataSeries = null;
            for (int i = 0; i < surface.getRenderableSeries().size(); i++) {
                final IRenderableSeries rs = surface.getRenderableSeries().get(i);
                if (rs.getDataSeries() instanceof OhlcDataSeries) {
                    ohlcDataSeries = (OhlcDataSeries<?, ?>) rs.getDataSeries();
                    break;
                }
            }

            // Get the Y value from the candlestick (low for buy, high for sell)
            final Comparable<?> yValue;
            if (ohlcDataSeries != null) {
                // Clamp index to valid range
                final int clampedIndex = Math.max(0, Math.min(xIndex, ohlcDataSeries.getCount() - 1));

                // Get low value for buy markers (bottom of candlestick) or high value for sell markers (top of candlestick)
                if (isBuy) {
                    yValue = ohlcDataSeries.getLowValues().get(clampedIndex);
                } else {
                    yValue = ohlcDataSeries.getHighValues().get(clampedIndex);
                }
            } else {
                // Fallback: use Y coordinate from touch if no data series found
                final float y = xAxis.isHorizontalAxis() ? touchPoint.y : touchPoint.x;
                yValue = yAxis.getDataValue(y);
            }

            final TradeMarkerAnnotation marker = new TradeMarkerAnnotation(
                    surface.getContext(),
                    xValue,
                    yValue,
                    isBuy/*,
                    quantity,
                    price,
                    change*/
            );

            // Add to surface
            final IUpdateSuspender suspender = surface.suspendUpdates();
            try {
                surface.getAnnotations().add(marker);
            } finally {
                suspender.dispose();
            }

            // Notify listener
            if (listener != null) {
                listener.onAnnotationCreated(marker);
            }
        }
    }
}

