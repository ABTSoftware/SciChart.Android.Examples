//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2026. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// TradingAnnotationsFragment.java is part of SCICHART®, High Performance Scientific Charts
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

import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;

import androidx.annotation.NonNull;

import com.scichart.charting.model.AnnotationCollection;
import com.scichart.charting.model.dataSeries.OhlcDataSeries;
import com.scichart.charting.modifiers.ExtendedLineAnnotationCreationModifier;
import com.scichart.charting.modifiers.FibonacciRetracementAnnotationCreationModifier;
import com.scichart.charting.modifiers.MeasureAnnotationCreationModifier;
import com.scichart.charting.modifiers.ModifierGroup;
import com.scichart.charting.modifiers.OnAnnotationCreatedListener;
import com.scichart.charting.modifiers.PitchforkAnnotationCreationModifier;
import com.scichart.charting.modifiers.StopLossTakeProfitAnnotationCreationModifier;
import com.scichart.charting.modifiers.XabcdAnnotationCreationModifier;
import com.scichart.charting.visuals.SciChartSurface;
import com.scichart.charting.visuals.annotations.ExtendedLineAnnotation;
import com.scichart.charting.visuals.annotations.IAnnotation;
import com.scichart.charting.visuals.annotations.tradingAnnotations.FibonacciRetracementAnnotation;
import com.scichart.charting.visuals.annotations.tradingAnnotations.MeasureAnnotation;
import com.scichart.charting.visuals.annotations.tradingAnnotations.PitchforkAnnotation;
import com.scichart.charting.visuals.annotations.tradingAnnotations.StopLossTakeProfitAnnotation;
import com.scichart.charting.visuals.annotations.tradingAnnotations.XabcdAnnotation;
import com.scichart.charting.visuals.renderableSeries.FastCandlestickRenderableSeries;
import com.scichart.examples.R;
import com.scichart.examples.components.SpinnerStringAdapter;
import com.scichart.examples.data.MarketDataService;
import com.scichart.examples.data.PriceSeries;
import com.scichart.examples.databinding.ExampleTradingAnnotationsFragmentBinding;
import com.scichart.examples.fragments.base.ExampleBaseFragment;
import com.scichart.examples.utils.ItemSelectedListenerBase;

import java.util.Calendar;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

public class TradingAnnotationsFragment extends ExampleBaseFragment<ExampleTradingAnnotationsFragmentBinding> implements OnAnnotationCreatedListener {

    private final XabcdAnnotationCreationModifier xabcdModifier = new XabcdAnnotationCreationModifier();
    private final PitchforkAnnotationCreationModifier pitchforkModifier = new PitchforkAnnotationCreationModifier();
    private final ExtendedLineAnnotationCreationModifier extendedLineModifier = new ExtendedLineAnnotationCreationModifier();
    private final FibonacciRetracementAnnotationCreationModifier fibonacciModifier = new FibonacciRetracementAnnotationCreationModifier();
    private final MeasureAnnotationCreationModifier measureModifier = new MeasureAnnotationCreationModifier();
    private final StopLossTakeProfitAnnotationCreationModifier stopLossTakeProfitModifier = new StopLossTakeProfitAnnotationCreationModifier();

    @NonNull
    @Override
    protected ExampleTradingAnnotationsFragmentBinding inflateBinding(@NonNull LayoutInflater inflater) {
        return ExampleTradingAnnotationsFragmentBinding.inflate(inflater);
    }

    @Override
    protected void initExample(ExampleTradingAnnotationsFragmentBinding binding) {
        binding.annotationTypeSelector.setAdapter(new SpinnerStringAdapter(getActivity(), R.array.trading_annotation_type_list));
        binding.annotationTypeSelector.setOnItemSelectedListener(new ItemSelectedListenerBase() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                disableAllModifiers();
                switch (position) {
                    case 0: xabcdModifier.setIsEnabled(true); break;
                    case 1: pitchforkModifier.setIsEnabled(true); break;
                    case 2: extendedLineModifier.setIsEnabled(true); break;
                    case 3: fibonacciModifier.setIsEnabled(true); break;
                    case 4: measureModifier.setIsEnabled(true); break;
                    case 5: stopLossTakeProfitModifier.setIsEnabled(true); break;
                }
            }
        });

        final XabcdAnnotation xabcdAnnotation = sciChartBuilder.newXabcdAnnotation()
                .withBasePoint(10, 30.6)
                .withBasePoint(30, 31.5)
                .withBasePoint(50, 30.3)
                .withBasePoint(70, 31.5)
                .withBasePoint(90, 30.6)
                .withIsEditable(true)
                .build();

        final PitchforkAnnotation pitchforkAnnotation = sciChartBuilder.newPitchforkAnnotation()
                .withBasePoint(100, 30.6)
                .withBasePoint(110, 31.5)
                .withBasePoint(130, 30.7)
                .withIsEditable(true)
                .build();

        final ExtendedLineAnnotation extendedLineAnnotation = sciChartBuilder.newExtendedLineAnnotation()
                .withExtendStart(true)
                .withExtendEnd(true)
                .withX1(70).withY1(33.3)
                .withX2(90).withY2(33.7)
                .withIsEditable(true)
                .build();

        final ArrayList<Double> fibonacciLevels = new ArrayList<>();
        Collections.addAll(fibonacciLevels, 0.0, 0.236, 0.382, 0.5, 0.618, 0.764, 1.0);

        final FibonacciRetracementAnnotation fibonacciRetracementAnnotation = sciChartBuilder.newFibonacciRetracementAnnotation()
                .withBasePoint(140, 30.4)
                .withBasePoint(160, 32.2)
                .withLevels(fibonacciLevels)
                .withLabelPlacement(FibonacciRetracementAnnotation.LabelPlacement.CENTER)
                .withLabelVerticalPosition(FibonacciRetracementAnnotation.LabelVerticalPosition.ABOVE)
                .withLabelFormat(FibonacciRetracementAnnotation.LabelFormat.RATIO)
                .withIsEditable(true)
                .build();

        final OhlcDataSeries<Date, Double> dataSeries = sciChartBuilder.newOhlcDataSeries(Date.class, Double.class).build();
        final MarketDataService marketDataService = new MarketDataService(Calendar.getInstance().getTime(), 5, 5);
        final PriceSeries data = marketDataService.getHistoricalData(200);

        dataSeries.append(data.getDateData(), data.getOpenData(), data.getHighData(), data.getLowData(), data.getCloseData());

        final FastCandlestickRenderableSeries candlestickSeries = sciChartBuilder.newCandlestickSeries().withDataSeries(dataSeries).withOpacity(0.4f).build();

        final MeasureAnnotation measureAnnotation = sciChartBuilder.newMeasureAnnotation()
                .withBasePoint(180, 30.6)
                .withBasePoint(195, 32.1)
                .withSnapSeries(candlestickSeries)
                .withIsEditable(true)
                .build();

        final StopLossTakeProfitAnnotation takeProfitAnnotation = sciChartBuilder.newStopLossTakeProfitAnnotation()
                .withBasePoint(165, 33.0)
                .withBasePoint(175, 34.2)
                .withStrokeDashArray(new float[]{6f, 3f})
                .withIsEditable(true)
                .build();

        final StopLossTakeProfitAnnotation stopLossAnnotation = sciChartBuilder.newStopLossTakeProfitAnnotation()
                .withBasePoint(165, 36.5)
                .withBasePoint(175, 35.3)
                .withStrokeDashArray(new float[]{6f, 3f})
                .withIsEditable(true)
                .build();

        final SciChartSurface surface = binding.surface;
        Collections.addAll(surface.getRenderableSeries(), candlestickSeries);
        Collections.addAll(surface.getXAxes(), sciChartBuilder.newCategoryDateAxis().build());
        Collections.addAll(surface.getYAxes(), sciChartBuilder.newNumericAxis().withVisibleRange(30d, 37d).build());
        Collections.addAll(surface.getAnnotations(), xabcdAnnotation, pitchforkAnnotation, extendedLineAnnotation, fibonacciRetracementAnnotation, measureAnnotation, takeProfitAnnotation, stopLossAnnotation);

        xabcdModifier.setAnnotationCreationListener(this);
        pitchforkModifier.setAnnotationCreationListener(this);
        extendedLineModifier.setAnnotationCreationListener(this);
        fibonacciModifier.setAnnotationCreationListener(this);
        fibonacciModifier.setLabelPlacement(FibonacciRetracementAnnotation.LabelPlacement.CENTER);
        fibonacciModifier.setLabelVerticalPosition(FibonacciRetracementAnnotation.LabelVerticalPosition.ABOVE);
        fibonacciModifier.setLabelFormat(FibonacciRetracementAnnotation.LabelFormat.RATIO);
        measureModifier.setAnnotationCreationListener(this);
        measureModifier.setSnapSeries(candlestickSeries);
        stopLossTakeProfitModifier.setAnnotationCreationListener(this);

        disableAllModifiers();
        xabcdModifier.setIsEnabled(true);

        surface.getChartModifiers().add(new ModifierGroup(xabcdModifier, pitchforkModifier, extendedLineModifier, fibonacciModifier, measureModifier, stopLossTakeProfitModifier));

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

    private void disableAllModifiers() {
        xabcdModifier.setIsEnabled(false);
        pitchforkModifier.setIsEnabled(false);
        extendedLineModifier.setIsEnabled(false);
        fibonacciModifier.setIsEnabled(false);
        measureModifier.setIsEnabled(false);
        stopLossTakeProfitModifier.setIsEnabled(false);
    }

    @Override
    public void onAnnotationCreated(@NonNull IAnnotation newAnnotation) {
        newAnnotation.setIsEditable(true);
    }
}
