//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2017. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// CreateAnnotationsDynamicallyFragment.java is part of the SCICHART® Examples. Permission is hereby granted
// to modify, create derivative works, distribute and publish any part of this source
// code whether for commercial, private or personal use.
//
// The SCICHART® examples are distributed in the hope that they will be useful, but
// without any warranty. It is provided "AS IS" without warranty of any kind, either
// expressed or implied.
//******************************************************************************

package com.scichart.examples.fragments;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Spinner;

import com.scichart.charting.model.AnnotationCollection;
import com.scichart.charting.model.dataSeries.OhlcDataSeries;
import com.scichart.charting.modifiers.AnnotationCreationModifier;
import com.scichart.charting.modifiers.DefaultAnnotationFactory;
import com.scichart.charting.modifiers.IAnnotationFactory;
import com.scichart.charting.modifiers.OnAnnotationCreatedListener;
import com.scichart.charting.visuals.ISciChartSurface;
import com.scichart.charting.visuals.SciChartSurface;
import com.scichart.charting.visuals.annotations.IAnnotation;
import com.scichart.examples.R;
import com.scichart.examples.components.SpinnerStringAdapter;
import com.scichart.examples.data.MarketDataService;
import com.scichart.examples.data.PriceSeries;
import com.scichart.examples.fragments.base.ExampleBaseFragment;
import com.scichart.examples.utils.ItemSelectedListenerBase;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

import butterknife.BindView;
import butterknife.OnClick;

public class CreateAnnotationsDynamicallyFragment extends ExampleBaseFragment implements OnAnnotationCreatedListener {
    @BindView(R.id.chart)
    SciChartSurface surface;

    @BindView(R.id.annotationTypeSelector)
    Spinner annotationTypeSelector;

    private final AnnotationCreationModifier annotationCreationModifier = new AnnotationCreationModifier();

    @Override
    protected int getLayoutId() {
        return R.layout.example_create_annotations_dynamically_fragment;
    }

    @Override
    protected void initExample() {
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

        Collections.addAll(surface.getRenderableSeries(), sciChartBuilder.newCandlestickSeries().withDataSeries(dataSeries).build());
        Collections.addAll(surface.getXAxes(), sciChartBuilder.newCategoryDateAxis().build());
        Collections.addAll(surface.getYAxes(), sciChartBuilder.newNumericAxis().withVisibleRange(30d, 37d).build());

        DefaultAnnotationFactory annotationFactory = new DefaultAnnotationFactory();
        annotationFactory.setFactoryForAnnotationType(DefaultAnnotationFactory.BOX_ANNOTATION, new IAnnotationFactory() {
            @NonNull
            @Override
            public IAnnotation createAnnotation(@NonNull ISciChartSurface parentSurface, int annotationType) {
                return sciChartBuilder.newBoxAnnotation().withBackgroundColor(0x6600cc00).build();
            }
        });
        annotationFactory.setFactoryForAnnotationType(DefaultAnnotationFactory.CUSTOM_ANNOTATION, new IAnnotationFactory() {
            @NonNull
            @Override
            public IAnnotation createAnnotation(@NonNull ISciChartSurface parentSurface, int annotationType) {
                final ImageView annotationContent = new ImageView(getActivity());
                annotationContent.setImageDrawable(getResources().getDrawable(R.drawable.example_scichartlogo));
                return sciChartBuilder.newCustomAnnotation().withContent(annotationContent).build();
            }
        });
        annotationFactory.setFactoryForAnnotationType(DefaultAnnotationFactory.TEXT_ANNOTATION, new IAnnotationFactory() {
            @NonNull
            @Override
            public IAnnotation createAnnotation(@NonNull ISciChartSurface parentSurface, int annotationType) {
                return sciChartBuilder.newTextAnnotation().withText("!!! Your text here !!!").build();
            }
        });

        annotationCreationModifier.setAnnotationFactory(annotationFactory);
        annotationCreationModifier.setAnnotationCreationListener(this);

        surface.getChartModifiers().add(sciChartBuilder.newModifierGroup().withModifier(annotationCreationModifier).build());
    }

    @OnClick(R.id.deleteAnnotation)
    void deleteSelectedAnnotation() {
        final AnnotationCollection annotations = surface.getAnnotations();
        for (int i = annotations.size() - 1; i >= 0; i--) {
            final IAnnotation annotation = annotations.get(i);
            if (annotation.isSelected())
                annotations.remove(i);
        }
    }

    @Override
    public void onAnnotationCreated(@NonNull IAnnotation newAnnotation) {
        newAnnotation.setIsEditable(true);
    }
}
