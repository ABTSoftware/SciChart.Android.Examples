//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2021. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// MountainChartFillFragment.java is part of SCICHART®, High Performance Scientific Charts
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

package com.scichart.examples.fragments.examples2d.aTest;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;

import androidx.annotation.NonNull;

import com.scichart.charting.model.dataSeries.XyDataSeries;
import com.scichart.charting.visuals.SciChartSurface;
import com.scichart.charting.visuals.axes.AxisAlignment;
import com.scichart.charting.visuals.axes.IAxis;
import com.scichart.charting.visuals.renderableSeries.FastMountainRenderableSeries;
import com.scichart.data.model.DoubleRange;
import com.scichart.drawing.common.BrushStyle;
import com.scichart.drawing.common.LinearGradientBrushStyle;
import com.scichart.drawing.common.RadialGradientBrushStyle;
import com.scichart.drawing.common.SolidBrushStyle;
import com.scichart.drawing.common.TextureBrushStyle;
import com.scichart.drawing.utility.ColorUtil;
import com.scichart.examples.R;
import com.scichart.examples.components.SpinnerStringAdapter;
import com.scichart.examples.databinding.ExampleMountainChartFillFragmentBinding;
import com.scichart.examples.fragments.base.ExampleBaseFragment;

import java.util.Collections;

public class MountainChartFillFragment extends ExampleBaseFragment<ExampleMountainChartFillFragmentBinding> {

    private FastMountainRenderableSeries mountainRenderableSeries;

    private Bitmap texture;

    private IAxis xAxis, yAxis;

    @NonNull
    @Override
    protected ExampleMountainChartFillFragmentBinding inflateBinding(@NonNull LayoutInflater inflater) {
        return ExampleMountainChartFillFragmentBinding.inflate(inflater);
    }

    @Override
    protected void initExample(ExampleMountainChartFillFragmentBinding binding) {
        texture = BitmapFactory.decodeResource(getResources(), R.drawable.example_scichartlogo);

        final Spinner fillSpinner = binding.fillList;
        final SpinnerStringAdapter seriesTypeAdapter = new SpinnerStringAdapter(getActivity(), R.array.fill_list);
        fillSpinner.setAdapter(seriesTypeAdapter);
        fillSpinner.setSelection(0);
        fillSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                BrushStyle brushStyle = null;
                switch (position) {
                    case 0:
                        brushStyle = new SolidBrushStyle(ColorUtil.argb(0xEE, 0xFF, 0xC9, 0xA8));
                        break;
                    case 1:
                        brushStyle = new LinearGradientBrushStyle(0, 0, 1, 1, ColorUtil.argb(0xEE, 0xFF, 0xC9, 0xA8), ColorUtil.argb(0xEE, 0x13, 0x24, 0xA5));
                        break;
                    case 2:
                        brushStyle = new RadialGradientBrushStyle(0.5f, 0.5f, 0.25f, 0.5f, ColorUtil.argb(0xEE, 0xFF, 0xC9, 0xA8), ColorUtil.argb(0xEE, 0x13, 0x24, 0xA5));
                        break;
                    case 3:
                        brushStyle = new TextureBrushStyle(texture);
                        break;
                }

                mountainRenderableSeries.setAreaStyle(brushStyle);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        binding.rotate.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                xAxis.setAxisAlignment(AxisAlignment.Right);
                yAxis.setAxisAlignment(AxisAlignment.Bottom);
            } else {
                xAxis.setAxisAlignment(AxisAlignment.Bottom);
                yAxis.setAxisAlignment(AxisAlignment.Right);
            }
        });

        xAxis = sciChartBuilder.newNumericAxis().withGrowBy(new DoubleRange(0.1d, 0.1d)).build();
        yAxis = sciChartBuilder.newNumericAxis().withGrowBy(new DoubleRange(0.1d, 0.1d)).build();

        final XyDataSeries<Double, Double> dataSeries = new XyDataSeries<>(Double.class, Double.class);
        dataSeries.append(new Double[]{0d, 2d, 4d, 6d, 8d, 10d, 12d, 14d, 16d, 18d, 20d,}, new Double[]{0d, 5d, -5d, -10d, 10d, 3d, 0d, -4d, -12d, 4d, 15d, 10d});
        mountainRenderableSeries = sciChartBuilder.newMountainSeries().withDataSeries(dataSeries).withStrokeStyle(ColorUtil.White, 3f, false).build();

        final SciChartSurface surface = binding.surface;
        Collections.addAll(surface.getXAxes(), xAxis);
        Collections.addAll(surface.getYAxes(), yAxis);
        Collections.addAll(surface.getRenderableSeries(), mountainRenderableSeries);
        Collections.addAll(surface.getRenderableSeries(), mountainRenderableSeries);
        Collections.addAll(surface.getChartModifiers(), sciChartBuilder.newModifierGroupWithDefaultModifiers().build());

        surface.zoomExtents();
    }
}
