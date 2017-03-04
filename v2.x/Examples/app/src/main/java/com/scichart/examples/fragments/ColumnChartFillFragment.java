//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2017. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// ColumnChartFillFragment.java is part of the SCICHART® Examples. Permission is hereby granted
// to modify, create derivative works, distribute and publish any part of this source
// code whether for commercial, private or personal use.
//
// The SCICHART® examples are distributed in the hope that they will be useful, but
// without any warranty. It is provided "AS IS" without warranty of any kind, either
// expressed or implied.
//******************************************************************************

package com.scichart.examples.fragments;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.Spinner;
import android.widget.ToggleButton;

import com.scichart.charting.model.dataSeries.XyDataSeries;
import com.scichart.charting.visuals.SciChartSurface;
import com.scichart.charting.visuals.axes.AxisAlignment;
import com.scichart.charting.visuals.axes.IAxis;
import com.scichart.charting.visuals.renderableSeries.FastColumnRenderableSeries;
import com.scichart.data.model.DoubleRange;
import com.scichart.drawing.common.BrushStyle;
import com.scichart.drawing.common.LinearGradientBrushStyle;
import com.scichart.drawing.common.RadialGradientBrushStyle;
import com.scichart.drawing.common.SolidBrushStyle;
import com.scichart.drawing.common.TextureBrushStyle;
import com.scichart.drawing.common.TextureMappingMode;
import com.scichart.drawing.utility.ColorUtil;
import com.scichart.examples.R;
import com.scichart.examples.components.SpinnerStringAdapter;
import com.scichart.examples.fragments.base.ExampleBaseFragment;

import java.util.Collections;

import butterknife.Bind;
import butterknife.OnClick;
import butterknife.OnItemSelected;

public class ColumnChartFillFragment extends ExampleBaseFragment {

    @Bind(R.id.chart)
    SciChartSurface surface;

    @Bind(R.id.fillList)
    Spinner fillSpinner;

    @Bind(R.id.textureMappingModesList)
    Spinner textureMappingModesSpinner;

    @Bind(R.id.rotate)
    ToggleButton rotateChartButton;

    private FastColumnRenderableSeries rs;

    private Bitmap texture;

    private IAxis xAxis, yAxis;

    @Override
    protected int getLayoutId() {
        return R.layout.example_column_chart_fill_fragment;
    }

    @Override
    protected void initExample() {
        texture = BitmapFactory.decodeResource(getResources(), R.drawable.scichartlogo);

        final SpinnerStringAdapter seriesTypeAdapter = new SpinnerStringAdapter(getActivity(), R.array.fill_list);
        fillSpinner.setAdapter(seriesTypeAdapter);
        fillSpinner.setSelection(0);

        final SpinnerStringAdapter seriesMappingAdapter = new SpinnerStringAdapter(getActivity(), R.array.texture_mapping_mode_list);
        textureMappingModesSpinner.setAdapter(seriesMappingAdapter);
        textureMappingModesSpinner.setSelection(0);

        xAxis = sciChartBuilder.newNumericAxis().withGrowBy(new DoubleRange(0.1d, 0.1d)).build();
        yAxis = sciChartBuilder.newNumericAxis().withGrowBy(new DoubleRange(0.1d, 0.1d)).build();

        final XyDataSeries<Double, Double> dataSeries = new XyDataSeries<>(Double.class, Double.class);
        dataSeries.append(new Double[]{0d, 2d, 4d, 6d, 8d, 10d}, new Double[]{1d, 5d, -5d, -10d, 10d, 3d});
        rs = sciChartBuilder.newColumnSeries().withDataSeries(dataSeries).withStrokeStyle(ColorUtil.White, 3f, false).build();

        surface.getChartModifiers().add(sciChartBuilder.newModifierGroupWithDefaultModifiers().build());

        Collections.addAll(surface.getXAxes(), xAxis);
        Collections.addAll(surface.getYAxes(), yAxis);
        Collections.addAll(surface.getRenderableSeries(), rs);

        surface.zoomExtents();
    }

    @OnItemSelected(R.id.textureMappingModesList)
    public void onTextureMappingModeSelected(int position) {
        switch (position){
            case 0:
                rs.setFillBrushMappingMode(TextureMappingMode.PerScreen);
                break;
            case 1:
                rs.setFillBrushMappingMode(TextureMappingMode.PerPrimitive);
                break;
        }
    }

    @OnItemSelected(R.id.fillList)
    public void onFillStyleSelected(int position) {
        BrushStyle brushStyle = null;
        switch (position) {
            case 0:
                brushStyle = new SolidBrushStyle(ColorUtil.argb(0xEE, 0xFF, 0xC9, 0xA8));
                break;

            case 1:
                brushStyle = new LinearGradientBrushStyle(0.25f, 0.25f, 0.75f, 0.75f, ColorUtil.argb(0xEE, 0xFF, 0xC9, 0xA8), ColorUtil.argb(0xEE, 0x13, 0x24, 0xA5));
                break;

            case 2:
                brushStyle = new RadialGradientBrushStyle(0.5f, 0.5f, 0.25f, 0.5f, ColorUtil.argb(0xEE, 0xFF, 0xC9, 0xA8), ColorUtil.argb(0xEE, 0x13, 0x24, 0xA5));
                break;

            case 3:
                brushStyle = new TextureBrushStyle(texture);
                break;
        }

        rs.setFillBrushStyle(brushStyle);
    }

    @OnClick(R.id.rotate)
    public void onRotateButtonClicked(ToggleButton button){
        if(button.isChecked()){
            xAxis.setAxisAlignment(AxisAlignment.Right);
            yAxis.setAxisAlignment(AxisAlignment.Bottom);
        } else{
            xAxis.setAxisAlignment(AxisAlignment.Bottom);
            yAxis.setAxisAlignment(AxisAlignment.Right);
        }
    }


}
