//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2021. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// CreateAnnotationsDynamicallyFragment.java is part of SCICHART®, High Performance Scientific Charts
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
import android.widget.ImageView;
import android.widget.Spinner;

import androidx.annotation.NonNull;

import com.scichart.charting.model.AnnotationCollection;
import com.scichart.charting.model.dataSeries.OhlcDataSeries;
import com.scichart.charting.modifiers.AnnotationCreationModifier;
import com.scichart.charting.modifiers.DefaultAnnotationFactory;
import com.scichart.charting.modifiers.OnAnnotationCreatedListener;
import com.scichart.charting.visuals.SciChartSurface;
import com.scichart.charting.visuals.annotations.IAnnotation;
import com.scichart.examples.R;
import com.scichart.examples.components.SpinnerStringAdapter;
import com.scichart.examples.data.MarketDataService;
import com.scichart.examples.data.PriceSeries;
import com.scichart.examples.databinding.ExampleCreateAnnotationsDynamicallyFragmentBinding;
import com.scichart.examples.fragments.base.ExampleBaseFragment;
import com.scichart.examples.utils.ItemSelectedListenerBase;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

public class CreateAnnotationsDynamicallyFragment extends ExampleBaseFragment<ExampleCreateAnnotationsDynamicallyFragmentBinding> implements OnAnnotationCreatedListener {

    private final AnnotationCreationModifier annotationCreationModifier = new AnnotationCreationModifier();

    @NonNull
    @Override
    protected ExampleCreateAnnotationsDynamicallyFragmentBinding inflateBinding(@NonNull LayoutInflater inflater) {
        return ExampleCreateAnnotationsDynamicallyFragmentBinding.inflate(inflater);
    }

    @Override
    protected void initExample(ExampleCreateAnnotationsDynamicallyFragmentBinding binding) {
        final Spinner annotationTypeSelector = binding.annotationTypeSelector;
        annotationTypeSelector.setAdapter(new SpinnerStringAdapter(getActivity(), R.array.annotation_type_list));
        annotationTypeSelector.setSelection(1);
        annotationTypeSelector.setOnItemSelectedListener(new ItemSelectedListenerBase() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                annotationCreationModifier.setAnnotationType(position);
            }
        });

        final OhlcDataSeries<Date, Double> dataSeries = sciChartBuilder.newOhlcDataSeries(Date.class, Double.class).build();

        final MarketDataService marketDataService = new MarketDataService(Calendar.getInstance().getTime(), 5, 5);
        final PriceSeries data = marketDataService.getHistoricalData(200);

        dataSeries.append(data.getDateData(), data.getOpenData(), data.getHighData(), data.getLowData(), data.getCloseData());

        final SciChartSurface surface = binding.surface;
        Collections.addAll(surface.getRenderableSeries(), sciChartBuilder.newCandlestickSeries().withDataSeries(dataSeries).withOpacity(0.4f).build());
        Collections.addAll(surface.getXAxes(), sciChartBuilder.newCategoryDateAxis().build());
        Collections.addAll(surface.getYAxes(), sciChartBuilder.newNumericAxis().withVisibleRange(30d, 37d).build());

        DefaultAnnotationFactory annotationFactory = new DefaultAnnotationFactory();
        annotationFactory.setFactoryForAnnotationType(DefaultAnnotationFactory.BOX_ANNOTATION, (parentSurface, annotationType) -> sciChartBuilder.newBoxAnnotation().withBackgroundColor(0x6600cc00).build());
        annotationFactory.setFactoryForAnnotationType(DefaultAnnotationFactory.CUSTOM_ANNOTATION, (parentSurface, annotationType) -> {
            final ImageView annotationContent = new ImageView(getActivity());
            annotationContent.setImageDrawable(getResources().getDrawable(R.drawable.example_scichartlogo));
            return sciChartBuilder.newCustomAnnotation().withContent(annotationContent).build();
        });
        annotationFactory.setFactoryForAnnotationType(DefaultAnnotationFactory.TEXT_ANNOTATION, (parentSurface, annotationType) -> sciChartBuilder.newTextAnnotation().withText("!!! Your text here !!!").build());

        annotationCreationModifier.setAnnotationFactory(annotationFactory);
        annotationCreationModifier.setAnnotationCreationListener(this);

        surface.getChartModifiers().add(sciChartBuilder.newModifierGroup().withModifier(annotationCreationModifier).build());

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
        newAnnotation.setIsEditable(true);
    }
}