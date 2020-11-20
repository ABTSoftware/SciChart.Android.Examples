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

import android.view.LayoutInflater;

import androidx.viewbinding.ViewBinding;

import com.scichart.charting.modifiers.PieSegmentSelectionModifier;
import com.scichart.charting.visuals.SciPieChartSurface;
import com.scichart.charting.visuals.legend.SciChartLegend;
import com.scichart.charting.visuals.renderableSeries.IPieRenderableSeries;
import com.scichart.examples.R;
import com.scichart.examples.databinding.ExampleSinglePieChartWithLegendFragmentBinding;
import com.scichart.examples.fragments.base.ExampleBaseFragment;

import java.util.Collections;

public class PieChartFragment extends ExampleBaseFragment<ExampleSinglePieChartWithLegendFragmentBinding> {

    @Override
    protected ExampleSinglePieChartWithLegendFragmentBinding inflateBinding(LayoutInflater inflater) {
        return ExampleSinglePieChartWithLegendFragmentBinding.inflate(inflater);
    }

    @Override
    protected void initExample(ExampleSinglePieChartWithLegendFragmentBinding binding) {
        final IPieRenderableSeries pieSeries = sciChartBuilder.newPieSeries().withSegments(
                sciChartBuilder.newPieSegment().withValue(40).withTitle("Green").withRadialGradientColors(0xff84BC3D, 0xff5B8829).build(),
                sciChartBuilder.newPieSegment().withValue(10).withTitle("Red").withRadialGradientColors(0xffe04a2f, 0xffB7161B).build(),
                sciChartBuilder.newPieSegment().withValue(20).withTitle("Blue").withRadialGradientColors(0xff4AB6C1, 0xff2182AD).build(),
                sciChartBuilder.newPieSegment().withValue(15).withTitle("Yellow").withRadialGradientColors(0xffFFFF00, 0xfffed325).build()
        ).build();

        final SciPieChartSurface pieChart = binding.pieChart;
        Collections.addAll(pieChart.getRenderableSeries(), pieSeries);
        Collections.addAll(pieChart.getChartModifiers(), sciChartBuilder.newLegendModifier(binding.pieChartLegend).withSourceSeries(pieSeries).build(), new PieSegmentSelectionModifier());

        pieSeries.animate(800);
    }
}