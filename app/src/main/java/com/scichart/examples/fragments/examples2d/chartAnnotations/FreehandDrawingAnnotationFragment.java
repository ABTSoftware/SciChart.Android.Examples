//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2026. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// BrushAnnotationFragment.java is part of SCICHART®, High Performance Scientific Charts
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

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.SeekBar;

import androidx.annotation.NonNull;

import com.scichart.charting.model.AnnotationCollection;
import com.scichart.charting.model.dataSeries.OhlcDataSeries;
import com.scichart.charting.modifiers.FreehandDrawingModifier;
import com.scichart.charting.modifiers.OnAnnotationCreatedListener;
import com.scichart.charting.modifiers.PinchZoomModifier;
import com.scichart.charting.modifiers.ZoomExtentsModifier;
import com.scichart.charting.modifiers.ZoomPanModifier;
import com.scichart.charting.visuals.SciChartSurface;
import com.scichart.charting.visuals.annotations.IAnnotation;
import com.scichart.examples.data.MarketDataService;
import com.scichart.examples.data.PriceSeries;
import com.scichart.examples.databinding.ExampleFreehandAnnotationFragmentBinding;
import com.scichart.examples.fragments.base.ExampleBaseFragment;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

public class FreehandDrawingAnnotationFragment extends ExampleBaseFragment<ExampleFreehandAnnotationFragmentBinding> implements OnAnnotationCreatedListener {

    private final FreehandDrawingModifier freehandDrawingModifier = new FreehandDrawingModifier();
    private final ZoomPanModifier zoomPanModifier = new ZoomPanModifier();
    private final PinchZoomModifier pinchZoomModifier = new PinchZoomModifier();
    private final ZoomExtentsModifier zoomExtentsModifier = new ZoomExtentsModifier();

    private boolean isDrawingMode = true;

    @NonNull
    @Override
    protected ExampleFreehandAnnotationFragmentBinding inflateBinding(@NonNull LayoutInflater inflater) {
        return ExampleFreehandAnnotationFragmentBinding.inflate(inflater);
    }

    @Override
    protected void initExample(ExampleFreehandAnnotationFragmentBinding binding) {
        final OhlcDataSeries<Date, Double> dataSeries = sciChartBuilder.newOhlcDataSeries(Date.class, Double.class).build();

        final MarketDataService marketDataService = new MarketDataService(Calendar.getInstance().getTime(), 5, 5);
        final PriceSeries data = marketDataService.getHistoricalData(200);

        dataSeries.append(data.getDateData(), data.getOpenData(), data.getHighData(), data.getLowData(), data.getCloseData());

        final SciChartSurface surface = binding.surface;
        Collections.addAll(surface.getRenderableSeries(), sciChartBuilder.newCandlestickSeries().withDataSeries(dataSeries).withOpacity(0.4f).build());
        Collections.addAll(surface.getXAxes(), sciChartBuilder.newCategoryDateAxis().build());
        Collections.addAll(surface.getYAxes(), sciChartBuilder.newNumericAxis().withVisibleRange(30d, 37d).build());

        // Drawing modifier starts enabled
        freehandDrawingModifier.setAnnotationCreationListener(this);
        freehandDrawingModifier.setIsEnabled(true);

        binding.thicknessSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                freehandDrawingModifier.setBrushThickness(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        binding.colorWhite.setOnClickListener(v -> selectColor(v, Color.WHITE));
        binding.colorYellow.setOnClickListener(v -> selectColor(v, Color.YELLOW));
        binding.colorGreen.setOnClickListener(v -> selectColor(v, Color.GREEN));
        binding.colorBlue.setOnClickListener(v -> selectColor(v, Color.BLUE));

        // Set initial selection
        selectColor(binding.colorWhite, Color.WHITE);

        binding.drawingModeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> setDrawingMode(isChecked, binding));

        zoomPanModifier.setIsEnabled(!isDrawingMode);
        pinchZoomModifier.setIsEnabled(!isDrawingMode);
        zoomExtentsModifier.setIsEnabled(!isDrawingMode);

        surface.getChartModifiers().add(sciChartBuilder.newModifierGroup()
                .withModifier(freehandDrawingModifier)
                .withModifier(zoomPanModifier)
                .withModifier(pinchZoomModifier)
                .withModifier(zoomExtentsModifier)
                .build());

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

    @Override
    public void onAnnotationCreated(@NonNull IAnnotation newAnnotation) {
        newAnnotation.setIsEditable(!isDrawingMode);
    }

    private void selectColor(View view, int color) {
        binding.colorWhite.setBackground(getColorDrawable(Color.WHITE, view == binding.colorWhite));
        binding.colorYellow.setBackground(getColorDrawable(Color.YELLOW, view == binding.colorYellow));
        binding.colorGreen.setBackground(getColorDrawable(Color.GREEN, view == binding.colorGreen));
        binding.colorBlue.setBackground(getColorDrawable(Color.BLUE, view == binding.colorBlue));

        freehandDrawingModifier.setBrushColor(color);
    }

    private Drawable getColorDrawable(int color, boolean isSelected) {
        GradientDrawable gd = new GradientDrawable();
        gd.setColor(color);
        gd.setShape(GradientDrawable.RECTANGLE);
        if (isSelected) {
            gd.setStroke(4, Color.MAGENTA);
        }
        return gd;
    }

    /**
     * Toggles between Drawing Mode and Selection Mode.
     * Drawing Mode ON: drawing modifier is active, all existing annotations become non-editable/non-selectable.
     * Drawing Mode OFF: drawing modifier is disabled, all existing annotations become editable/selectable.
     */
    private void setDrawingMode(boolean drawingMode, ExampleFreehandAnnotationFragmentBinding binding) {
        isDrawingMode = drawingMode;
        freehandDrawingModifier.setIsEnabled(drawingMode);

        zoomPanModifier.setIsEnabled(!drawingMode);
        pinchZoomModifier.setIsEnabled(!drawingMode);
        zoomExtentsModifier.setIsEnabled(!drawingMode);

        final SciChartSurface surface = binding.surface;
        final AnnotationCollection annotations = surface.getAnnotations();
        for (int i = 0; i < annotations.size(); i++) {
            annotations.get(i).setIsEditable(!drawingMode);
        }
    }
}
