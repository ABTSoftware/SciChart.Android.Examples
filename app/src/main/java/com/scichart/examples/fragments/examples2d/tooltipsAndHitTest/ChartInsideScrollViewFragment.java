//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2021. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// ChartInsideScrollViewFragment.java is part of SCICHART®, High Performance Scientific Charts
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

package com.scichart.examples.fragments.examples2d.tooltipsAndHitTest;

import android.view.LayoutInflater;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;

import com.scichart.charting.model.dataSeries.IOhlcDataSeries;
import com.scichart.charting.model.dataSeries.OhlcDataSeries;
import com.scichart.charting.visuals.axes.AutoRange;
import com.scichart.charting.visuals.axes.IAxis;
import com.scichart.charting.visuals.renderableSeries.FastCandlestickRenderableSeries;
import com.scichart.core.framework.UpdateSuspender;
import com.scichart.examples.data.DataManager;
import com.scichart.examples.data.PriceSeries;
import com.scichart.examples.databinding.ExampleChartInsideScrollViewFragmentBinding;
import com.scichart.examples.fragments.base.ExampleBaseFragment;

import java.util.Collections;
import java.util.Date;

public class ChartInsideScrollViewFragment extends ExampleBaseFragment<ExampleChartInsideScrollViewFragmentBinding> {

    @NonNull
    @Override
    protected ExampleChartInsideScrollViewFragmentBinding inflateBinding(@NonNull LayoutInflater inflater) {
        return ExampleChartInsideScrollViewFragmentBinding.inflate(inflater);
    }

    @Override
    protected void initExample(@NonNull ExampleChartInsideScrollViewFragmentBinding binding) {
        PriceSeries priceSeries = DataManager.getInstance().getPriceDataIndu(getActivity());
        int size = priceSeries.size();

        final IAxis xAxis = sciChartBuilder.newCategoryDateAxis().withVisibleRange(size - 30, size).withGrowBy(0, 0.1).build();
        final IAxis yAxis = sciChartBuilder.newNumericAxis().withGrowBy(0d, 0.1d).build();

        IOhlcDataSeries<Date, Double> dataSeries = new OhlcDataSeries<>(Date.class, Double.class);
        dataSeries.append(priceSeries.getDateData(), priceSeries.getOpenData(), priceSeries.getHighData(), priceSeries.getLowData(), priceSeries.getCloseData());

        final FastCandlestickRenderableSeries rSeries = sciChartBuilder.newCandlestickSeries()
                .withStrokeUp(0xFF00AA00)
                .withFillUpColor(0x8800AA00)
                .withStrokeDown(0xFFFF0000)
                .withFillDownColor(0x88FF0000)
                .withDataSeries(dataSeries)
                .build();

        UpdateSuspender.using(binding.surface, () -> {
            Collections.addAll(binding.surface.getXAxes(), xAxis);
            Collections.addAll(binding.surface.getYAxes(), yAxis);
            Collections.addAll(binding.surface.getRenderableSeries(), rSeries);
            Collections.addAll(binding.surface.getChartModifiers(),
                    sciChartBuilder.newModifierGroup()
                            .withZoomPanModifier().build()
                            .withPinchZoomModifier().build()
                            .withZoomExtentsModifier().build()
                            .build()
            );
        });

        binding.chartInsideScrollViewToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                binding.surface.setDisallowInterceptTouchEvent(isChecked);
            }
        });
    }
}