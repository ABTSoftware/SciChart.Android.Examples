//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2017. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// MultiplePieDonutChartFragment.java is part of the SCICHART® Examples. Permission is hereby granted
// to modify, create derivative works, distribute and publish any part of this source
// code whether for commercial, private or personal use.
//
// The SCICHART® examples are distributed in the hope that they will be useful, but
// without any warranty. It is provided "AS IS" without warranty of any kind, either
// expressed or implied.
//******************************************************************************

package com.scichart.examples.fragments;

import com.scichart.charting.modifiers.PieChartTooltipModifier;
import com.scichart.charting.visuals.SciPieChartSurface;
import com.scichart.charting.visuals.legend.SciChartLegend;
import com.scichart.charting.visuals.renderableSeries.IPieRenderableSeries;
import com.scichart.examples.R;
import com.scichart.examples.fragments.base.ExampleBaseFragment;

import java.util.Collections;

import butterknife.BindView;

public class MultiplePieDonutChartFragment extends ExampleBaseFragment {
    @BindView(R.id.pieChart)
    SciPieChartSurface pieChartSurface;

    @BindView(R.id.pieChartLegend)
    SciChartLegend legend;

    @Override
    protected int getLayoutId() {
        return R.layout.example_single_pie_chart_with_legend_fragment;
    }

    @Override
    protected void initExample() {
        final IPieRenderableSeries pieSeries = sciChartBuilder.newPieSeries().withSeriesName("HowPeopleTravel").withSegments(
                sciChartBuilder.newPieSegment().withValue(34).withTitle("Ecologic").withRadialGradientColors(0xff84BC3D, 0xff5B8829).build(),
                sciChartBuilder.newPieSegment().withValue(34.4).withTitle("Municipal").withRadialGradientColors(0xffe04a2f, 0xffB7161B).build(),
                sciChartBuilder.newPieSegment().withValue(31.6).withTitle("Personal").withRadialGradientColors(0xff4AB6C1, 0xff2182AD).build()
        ).build();

        final IPieRenderableSeries donutSeries = sciChartBuilder.newDonutSeries().withSeriesName("DetailedGroup").withSegments(
                sciChartBuilder.newPieSegment().withValue(28.8).withTitle("Walking").withRadialGradientColors(0xff84BC3D, 0xff5B8829).build(),
                sciChartBuilder.newPieSegment().withValue(5.2).withTitle("Bicycle").withRadialGradientColors(0xff84BC3D, 0xff5B8829).build(),

                sciChartBuilder.newPieSegment().withValue(12.3).withTitle("Metro").withRadialGradientColors(0xffe04a2f, 0xffB7161B).build(),
                sciChartBuilder.newPieSegment().withValue(3.5).withTitle("Tram").withRadialGradientColors(0xffe04a2f, 0xffB7161B).build(),
                sciChartBuilder.newPieSegment().withValue(5.9).withTitle("Rail").withRadialGradientColors(0xffe04a2f, 0xffB7161B).build(),
                sciChartBuilder.newPieSegment().withValue(9.7).withTitle("Bus").withRadialGradientColors(0xffe04a2f, 0xffB7161B).build(),
                sciChartBuilder.newPieSegment().withValue(3.0).withTitle("Taxi").withRadialGradientColors(0xffe04a2f, 0xffB7161B).build(),

                sciChartBuilder.newPieSegment().withValue(23.2).withTitle("Car").withRadialGradientColors(0xff4AB6C1, 0xff2182AD).build(),
                sciChartBuilder.newPieSegment().withValue(3.1).withTitle("Motorcycle").withRadialGradientColors(0xff4AB6C1, 0xff2182AD).build(),
                sciChartBuilder.newPieSegment().withValue(5.3).withTitle("Other").withRadialGradientColors(0xff4AB6C1, 0xff2182AD).build()
        ).build();

        Collections.addAll(pieChartSurface.getRenderableSeries(), pieSeries, donutSeries);
        Collections.addAll(pieChartSurface.getChartModifiers(), sciChartBuilder.newLegendModifier(legend).withShowCheckBoxes(false).withSourceSeries(pieSeries).build(), new PieChartTooltipModifier());

        pieSeries.animate(800);
        donutSeries.animate(800);
    }
}