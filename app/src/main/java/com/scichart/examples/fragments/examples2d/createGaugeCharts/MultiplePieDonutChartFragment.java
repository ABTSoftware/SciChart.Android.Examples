//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2021. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// MultiplePieDonutChartFragment.java is part of SCICHART®, High Performance Scientific Charts
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

package com.scichart.examples.fragments.examples2d.createGaugeCharts;

import android.view.LayoutInflater;

import androidx.annotation.NonNull;

import com.scichart.charting.modifiers.PieChartTooltipModifier;
import com.scichart.charting.visuals.SciPieChartSurface;
import com.scichart.charting.visuals.renderableSeries.IPieRenderableSeries;
import com.scichart.examples.databinding.ExampleSinglePieChartWithLegendFragmentBinding;
import com.scichart.examples.fragments.base.ExampleBaseFragment;
import com.scichart.examples.utils.Constant;

import java.util.Collections;

public class MultiplePieDonutChartFragment extends ExampleBaseFragment<ExampleSinglePieChartWithLegendFragmentBinding> {

    @NonNull
    @Override
    protected ExampleSinglePieChartWithLegendFragmentBinding inflateBinding(@NonNull LayoutInflater inflater) {
        return ExampleSinglePieChartWithLegendFragmentBinding.inflate(inflater);
    }

    @Override
    protected void initExample(ExampleSinglePieChartWithLegendFragmentBinding binding) {
        final IPieRenderableSeries pieSeries = sciChartBuilder.newPieSeries().withSeriesName("HowPeopleTravel").withSegments(
                sciChartBuilder.newPieSegment().withValue(34).withTitle("Ecologic").withRadialGradientColors(0xff68bcae, 0xff68bcae).build(),
                sciChartBuilder.newPieSegment().withValue(34.4).withTitle("Municipal").withRadialGradientColors(0xffe97064, 0xffe97064).build(),
                sciChartBuilder.newPieSegment().withValue(31.6).withTitle("Personal").withRadialGradientColors(0xff47bde6, 0xff47bde6).build()
        ).build();

        final IPieRenderableSeries donutSeries = sciChartBuilder.newDonutSeries().withSeriesName("DetailedGroup").withSegments(
                sciChartBuilder.newPieSegment().withValue(28.8).withTitle("Walking").withRadialGradientColors(0xff68bcae, 0xff68bcae).build(),
                sciChartBuilder.newPieSegment().withValue(5.2).withTitle("Bicycle").withRadialGradientColors(0xff68bcae, 0xff68bcae).build(),

                sciChartBuilder.newPieSegment().withValue(12.3).withTitle("Metro").withRadialGradientColors(0xffe97064, 0xffe97064).build(),
                sciChartBuilder.newPieSegment().withValue(3.5).withTitle("Tram").withRadialGradientColors(0xffe97064, 0xffe97064).build(),
                sciChartBuilder.newPieSegment().withValue(5.9).withTitle("Rail").withRadialGradientColors(0xffe97064, 0xffe97064).build(),
                sciChartBuilder.newPieSegment().withValue(9.7).withTitle("Bus").withRadialGradientColors(0xffe97064, 0xffe97064).build(),
                sciChartBuilder.newPieSegment().withValue(3.0).withTitle("Taxi").withRadialGradientColors(0xffe97064, 0xffe97064).build(),

                sciChartBuilder.newPieSegment().withValue(23.2).withTitle("Car").withRadialGradientColors(0xff47bde6, 0xff47bde6).build(),
                sciChartBuilder.newPieSegment().withValue(3.1).withTitle("Motorcycle").withRadialGradientColors(0xff47bde6, 0xff47bde6).build(),
                sciChartBuilder.newPieSegment().withValue(5.3).withTitle("Other").withRadialGradientColors(0xff47bde6, 0xff47bde6).build()
        ).build();

        final SciPieChartSurface pieChart = binding.pieChart;
        Collections.addAll(pieChart.getRenderableSeries(), pieSeries, donutSeries);
        Collections.addAll(pieChart.getChartModifiers(), sciChartBuilder.newLegendModifier(binding.pieChartLegend).withShowCheckBoxes(false).withSourceSeries(pieSeries).build(), new PieChartTooltipModifier());

        pieSeries.animate(Constant.ANIMATION_DURATION);
        donutSeries.animate(Constant.ANIMATION_DURATION);
    }
}