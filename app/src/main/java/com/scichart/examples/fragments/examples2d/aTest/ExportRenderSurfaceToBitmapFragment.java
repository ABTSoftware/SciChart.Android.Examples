//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2021. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// ExportRenderSurfaceToBitmapFragment.java is part of SCICHART®, High Performance Scientific Charts
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

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.scichart.charting.model.dataSeries.IXyDataSeries;
import com.scichart.charting.themes.ThemeManager;
import com.scichart.charting.visuals.SciChartSurface;
import com.scichart.charting.visuals.axes.IAxis;
import com.scichart.charting.visuals.renderableSeries.FastLineRenderableSeries;
import com.scichart.charting.visuals.rendering.SciChartSurfaceExportUtil;
import com.scichart.core.framework.UpdateSuspender;
import com.scichart.data.model.DoubleRange;
import com.scichart.drawing.canvas.RenderSurface;
import com.scichart.drawing.opengl.GLTextureView;
import com.scichart.drawing.opengl.RenderSurfaceGL;
import com.scichart.drawing.utility.ColorUtil;
import com.scichart.examples.R;
import com.scichart.examples.components.SpinnerStringAdapter;
import com.scichart.examples.data.DataManager;
import com.scichart.examples.data.DoubleSeries;
import com.scichart.examples.databinding.ExampleTestExportToBitmapBinding;
import com.scichart.examples.fragments.base.ExampleBaseFragment;

import java.util.Collections;

public class ExportRenderSurfaceToBitmapFragment extends ExampleBaseFragment<ExampleTestExportToBitmapBinding> {

    private final static int BLACK_STEEL = 0;
    private final static int BRIGHT_SPARK = 1;
    private final static int CHROME = 2;
    private final static int ELECTRIC = 3;
    private final static int EXPRESSION_DARK = 4;
    private final static int EXPRESSION_LIGHT = 5;
    private final static int OSCILLOSCOPE = 6;
    private final static int SCI_CHART_V4_DARK = 7;
    private final static int BERRY_BLUE = 8;

    private String renderSurface;
    private int themeId;

    @NonNull
    @Override
    protected ExampleTestExportToBitmapBinding inflateBinding(@NonNull LayoutInflater inflater) {
        return ExampleTestExportToBitmapBinding.inflate(inflater);
    }

    @NonNull
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        binding.renderSurfaceTypeSpinner.setAdapter(new SpinnerStringAdapter(getActivity(), R.array.render_surface_types));
        binding.renderSurfaceTypeSpinner.setSelection(1);

        binding.themeSelector.setAdapter(new SpinnerStringAdapter(getActivity(), R.array.style_list));
        binding.themeSelector.setSelection(7);

        return view;
    }

    @Override
    protected void initExample(ExampleTestExportToBitmapBinding binding) {
        initSurface(binding.surface);

        binding.exportChart.setOnClickListener(v -> binding.chartImage.setImageBitmap(binding.surface.exportToBitmap()));

        binding.exportChartInMemory.setOnClickListener(v -> {
            final SciChartSurface sciChartSurface = new SciChartSurface(getActivity());

            setRenderSurface(sciChartSurface, renderSurface);
            sciChartSurface.setTheme(themeId);

            initSurface(sciChartSurface);

            SciChartSurfaceExportUtil.prepareSurfaceForExport(sciChartSurface, 800, 600);

            binding.chartImage.setImageBitmap(sciChartSurface.exportToBitmap());
        });

        binding.renderSurfaceTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                renderSurface = (String) parent.getItemAtPosition(position);

                setRenderSurface(binding.surface, renderSurface);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        binding.themeSelector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case BLACK_STEEL:
                        themeId = R.style.SciChart_BlackSteel;
                        break;
                    case BRIGHT_SPARK:
                        themeId = R.style.SciChart_Bright_Spark;
                        break;
                    case CHROME:
                        themeId = R.style.SciChart_ChromeStyle;
                        break;
                    case ELECTRIC:
                        themeId = R.style.SciChart_ElectricStyle;
                        break;
                    case EXPRESSION_DARK:
                        themeId = R.style.SciChart_ExpressionDarkStyle;
                        break;
                    case EXPRESSION_LIGHT:
                        themeId = R.style.SciChart_ExpressionLightStyle;
                        break;
                    case OSCILLOSCOPE:
                        themeId = R.style.SciChart_OscilloscopeStyle;
                        break;
                    case SCI_CHART_V4_DARK:
                        themeId = R.style.SciChart_SciChartv4DarkStyle;
                        break;
                    case BERRY_BLUE:
                        themeId = R.style.SciChart_BerryBlue;
                        break;
                    default:
                        themeId = ThemeManager.DEFAULT_THEME;
                        break;
                }

                binding.surface.setTheme(themeId);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void initSurface(final SciChartSurface sciChartSurface) {
        final DoubleSeries fourierSeries = DataManager.getInstance().getFourierSeries(1.0, 0.1, 5000);

        final IXyDataSeries<Double, Double> dataSeries = sciChartBuilder.newXyDataSeries(Double.class, Double.class).build();
        dataSeries.append(fourierSeries.xValues, fourierSeries.yValues);

        final IAxis xAxis = sciChartBuilder.newNumericAxis().withGrowBy(new DoubleRange(0.1d, 0.1d)).build();
        final IAxis yAxis = sciChartBuilder.newNumericAxis().withGrowBy(new DoubleRange(0.1d, 0.1d)).build();

        final FastLineRenderableSeries rSeries = sciChartBuilder.newLineSeries().withDataSeries(dataSeries).withStrokeStyle(0xFF279B27).build();

        UpdateSuspender.using(sciChartSurface, () -> {
            Collections.addAll(sciChartSurface.getXAxes(), xAxis);
            Collections.addAll(sciChartSurface.getYAxes(), yAxis);
            Collections.addAll(sciChartSurface.getRenderableSeries(), rSeries);
            Collections.addAll(sciChartSurface.getChartModifiers(), sciChartBuilder.newModifierGroupWithDefaultModifiers().build());
            Collections.addAll(sciChartSurface.getAnnotations(),
                    sciChartBuilder.newTextAnnotation()
                            .withX1(0.3)
                            .withY1(0)
                            .withFontStyle(24, ColorUtil.White)
                            .withText("Annotations are Easy!")
                            .build(),
                    sciChartBuilder.newHorizontalLineAnnotation()
                            .withX1(4).withY1(-2)
                            .withIsEditable(true)
                            .withStroke(2, ColorUtil.Blue)
                            .withHorizontalGravity(Gravity.END)
                            .build(),
                    sciChartBuilder.newHorizontalLineAnnotation()
                            .withPosition(7d, 2.8d)
                            .withIsEditable(true)
                            .withStroke(2, ColorUtil.Orange)
                            .build(),
                    sciChartBuilder.newLineAnnotation()
                            .withPosition(2d, 0d, 8d, 2d)
                            .withIsEditable(true)
                            .withStroke(2, ColorUtil.DarkRed)
                            .build(),
                    sciChartBuilder.newAxisMarkerAnnotation()
                            .withY1(2.8d)
                            .withIsEditable(true)
                            .withBackgroundColor(ColorUtil.Orange)
                            .build());
        });
    }

    private static void setRenderSurface(SciChartSurface surface, String renderSurface) {
        final Context context = surface.getContext();

        if (renderSurface.contains("Canvas")) {
            surface.setRenderSurface(new RenderSurface(context));
        } else if (renderSurface.contains("OpenGL")) {
            surface.setRenderSurface(new RenderSurfaceGL(context));
        } else if (renderSurface.contains("Texture")) {
            surface.setRenderSurface(new GLTextureView(context));
        }
    }
}