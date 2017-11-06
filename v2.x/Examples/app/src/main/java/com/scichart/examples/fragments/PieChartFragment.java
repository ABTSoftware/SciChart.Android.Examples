//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2017. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// PieChartFragment.java is part of the SCICHART® Examples. Permission is hereby granted
// to modify, create derivative works, distribute and publish any part of this source
// code whether for commercial, private or personal use.
//
// The SCICHART® examples are distributed in the hope that they will be useful, but
// without any warranty. It is provided "AS IS" without warranty of any kind, either
// expressed or implied.
//******************************************************************************

package com.scichart.examples.fragments;

import android.graphics.Color;

import com.scichart.charting.modifiers.PieChartLegendModifier;
import com.scichart.charting.modifiers.PieSegmentSelectionModifier;
import com.scichart.charting.visuals.SciPieChartSurface;
import com.scichart.charting.visuals.renderableSeries.DonutRenderableSeries;
import com.scichart.charting.visuals.renderableSeries.IPieRenderableSeries;
import com.scichart.charting.visuals.renderableSeries.IPieSegment;
import com.scichart.charting.visuals.renderableSeries.PieRenderableSeries;
import com.scichart.charting.visuals.renderableSeries.PieSegment;
import com.scichart.charting.visuals.renderableSeries.SegmentStyleBase;
import com.scichart.drawing.common.BrushStyle;
import com.scichart.drawing.common.RadialGradientBrushStyle;
import com.scichart.drawing.common.SolidBrushStyle;
import com.scichart.examples.R;
import com.scichart.examples.fragments.base.ExampleBaseFragment;

import java.util.Collections;

import butterknife.BindView;

public class PieChartFragment extends ExampleBaseFragment {
    @BindView(R.id.pieChart)
    SciPieChartSurface pieChartSurface;

    @Override
    protected int getLayoutId() {
        return R.layout.example_single_pie_chart_fragment;
    }

    @Override
    protected void initExample() {
        final PieSegmentSelectionModifier segmentSelectionModifier = new PieSegmentSelectionModifier();
        segmentSelectionModifier.setSelectedSegmentStyle(new SegmentStyleBase<IPieSegment>(IPieSegment.class) {
            private final BrushStyle selectedFillStyle = new SolidBrushStyle(Color.BLUE);

            private static final String FILL_STYLE = "FillStyle";

            @Override
            protected void applyStyleInternal(IPieSegment segmentToStyle) {
                putPropertyValue(segmentToStyle, FILL_STYLE, segmentToStyle.getFillStyle());
                segmentToStyle.setFillStyle(selectedFillStyle);
            }

            @Override
            protected void discardStyleInternal(IPieSegment segmentToStyle) {
                segmentToStyle.setFillStyle(getPropertyValue(segmentToStyle, FILL_STYLE, BrushStyle.class));
            }
        });

        final PieSegment pieSegment1 = new PieSegment();
        pieSegment1.setValue(40);
        pieSegment1.setTitle("Green");
        pieSegment1.setFillStyle(new RadialGradientBrushStyle(0.5f, 0.5f, 0.5f, 0.5f, new int[]{Color.RED, Color.YELLOW}, new float[]{0f, 1f}));

        final PieSegment pieSegment2 = new PieSegment();
        pieSegment2.setValue(10);
        pieSegment2.setTitle("Red");
        pieSegment2.setFillStyle(new RadialGradientBrushStyle(0.5f, 0.5f, 0.5f, 0.5f, new int[]{0xffe04a2f, 0xffB7161B}, new float[]{0.6f, 0.95f}));

        final PieSegment pieSegment3 = new PieSegment();
        pieSegment3.setValue(20);
        pieSegment3.setTitle("Blue");
        pieSegment3.setFillStyle(new RadialGradientBrushStyle(0.5f, 0.5f, 0.5f, 0.5f, new int[]{0xff4AB6C1, 0xff2182AD}, new float[]{0.6f, 0.95f}));

        final PieSegment pieSegment4 = new PieSegment();
        pieSegment4.setValue(15);
        pieSegment4.setTitle("Yellow");
        pieSegment4.setFillStyle(new RadialGradientBrushStyle(0.5f, 0.5f, 0.5f, 0.5f, new int[]{0xffFFFF00, 0xfffed325}, new float[]{0.6f, 0.95f}));

        final IPieRenderableSeries pieSeries1 = new PieRenderableSeries();
        pieSeries1.setSelectedSegmentOffset(0f);
        Collections.addAll(pieSeries1.getSegmentsCollection(), pieSegment1, pieSegment2, pieSegment3, pieSegment4);

        final IPieRenderableSeries pieSeries2 = createDonutSeries();

//        Collections.addAll(pieChartSurface.getRenderableSeries(), pieSeries1);
        Collections.addAll(pieChartSurface.getRenderableSeries(), pieSeries1, pieSeries2);
//        Collections.addAll(pieChartSurface.getChartModifiers(), new PieChartTooltipModifier());

        final PieChartLegendModifier legendModifier = new PieChartLegendModifier(getActivity());
        legendModifier.setSourceSeries(pieSeries2);

        Collections.addAll(pieChartSurface.getChartModifiers(), legendModifier);
    }

    private IPieRenderableSeries createDonutSeries() {
        final PieSegment pieSegment1 = new PieSegment();
        pieSegment1.setValue(40);
        pieSegment1.setTitle("Green");
        pieSegment1.setFillStyle(new RadialGradientBrushStyle(0.5f, 0.5f, 0.5f, 0.5f, new int[]{Color.RED, Color.YELLOW}, new float[]{0f, 1f}));

        final PieSegment pieSegment2 = new PieSegment();
        pieSegment2.setValue(10);
        pieSegment2.setTitle("Red");
        pieSegment2.setFillStyle(new RadialGradientBrushStyle(0.5f, 0.5f, 0.5f, 0.5f, new int[]{0xffe04a2f, 0xffB7161B}, new float[]{0.6f, 0.95f}));

        final PieSegment pieSegment3 = new PieSegment();
        pieSegment3.setValue(20);
        pieSegment3.setTitle("Blue");
        pieSegment3.setFillStyle(new RadialGradientBrushStyle(0.5f, 0.5f, 0.5f, 0.5f, new int[]{0xff4AB6C1, 0xff2182AD}, new float[]{0.6f, 0.95f}));

        final PieSegment pieSegment4 = new PieSegment();
        pieSegment4.setValue(15);
        pieSegment4.setTitle("Yellow");
        pieSegment4.setFillStyle(new RadialGradientBrushStyle(0.5f, 0.5f, 0.5f, 0.5f, new int[]{0xffFFFF00, 0xfffed325}, new float[]{0.6f, 0.95f}));

        final IPieRenderableSeries pieSeries = new DonutRenderableSeries();
        Collections.addAll(pieSeries.getSegmentsCollection(), pieSegment1, pieSegment2, pieSegment3, pieSegment4);

        return pieSeries;
    }
}