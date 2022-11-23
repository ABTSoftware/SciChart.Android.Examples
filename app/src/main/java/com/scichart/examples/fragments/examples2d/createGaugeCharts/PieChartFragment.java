//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2021. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// PieChartFragment.java is part of SCICHART®, High Performance Scientific Charts
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

import com.scichart.charting.modifiers.PieSegmentSelectionModifier;
import com.scichart.charting.visuals.SciPieChartSurface;
import com.scichart.charting.visuals.renderableSeries.IPieRenderableSeries;
import com.scichart.examples.databinding.ExampleSinglePieChartWithLegendFragmentBinding;
import com.scichart.examples.fragments.base.ExampleBaseFragment;

import java.util.Collections;

public class PieChartFragment extends ExampleBaseFragment<ExampleSinglePieChartWithLegendFragmentBinding> {

    @NonNull
    @Override
    protected ExampleSinglePieChartWithLegendFragmentBinding inflateBinding(@NonNull LayoutInflater inflater) {
        return ExampleSinglePieChartWithLegendFragmentBinding.inflate(inflater);
    }

    @Override
    protected void initExample(ExampleSinglePieChartWithLegendFragmentBinding binding) {
        final IPieRenderableSeries pieSeries = sciChartBuilder.newPieSeries().withSegments(
                sciChartBuilder.newPieSegment().withValue(40).withTitle("Oregon Blackberry Pie").withRadialGradientColors(0xff47bde6, 0xff47bde6).build(),
                sciChartBuilder.newPieSegment().withValue(10).withTitle("French Coconut Pie").withRadialGradientColors(0xffae418d, 0xffae418d).build(),
                sciChartBuilder.newPieSegment().withValue(20).withTitle("Rhubarb Custard Pie").withRadialGradientColors(0xff68bcae, 0xff68bcae).build(),
                sciChartBuilder.newPieSegment().withValue(15).withTitle("Lemon Chiffon Pie").withRadialGradientColors(0xffe97064, 0xffe97064).build()
        ).build();

        final SciPieChartSurface pieChart = binding.pieChart;
        Collections.addAll(pieChart.getRenderableSeries(), pieSeries);
        Collections.addAll(pieChart.getChartModifiers(), sciChartBuilder.newLegendModifier(binding.pieChartLegend).withSourceSeries(pieSeries).build(), new PieSegmentSelectionModifier());

        pieSeries.animate(800);
    }
}