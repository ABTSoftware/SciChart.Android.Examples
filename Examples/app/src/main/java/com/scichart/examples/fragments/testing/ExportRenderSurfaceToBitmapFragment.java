//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2017. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// ExportRenderSurfaceToBitmapFragment.java is part of the SCICHART® Examples. Permission is hereby granted
// to modify, create derivative works, distribute and publish any part of this source
// code whether for commercial, private or personal use.
//
// The SCICHART® examples are distributed in the hope that they will be useful, but
// without any warranty. It is provided "AS IS" without warranty of any kind, either
// expressed or implied.
//******************************************************************************

package com.scichart.examples.fragments.testing;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;

import com.scichart.charting.model.dataSeries.IXyDataSeries;
import com.scichart.charting.themes.ThemeManager;
import com.scichart.charting.visuals.SciChartSurface;
import com.scichart.charting.visuals.axes.IAxis;
import com.scichart.charting.visuals.renderableSeries.FastLineRenderableSeries;
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
import com.scichart.examples.fragments.base.ExampleBaseFragment;
import com.scichart.examples.utils.ItemSelectedListenerBase;

import java.util.Collections;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import butterknife.Bind;
import butterknife.OnItemSelected;

public class ExportRenderSurfaceToBitmapFragment extends ExampleBaseFragment {

    private final static int BLACK_STEEL = 0;
    private final static int BRIGHT_SPARK = 1;
    private final static int CHROME = 2;
    private final static int ELECTRIC = 3;
    private final static int EXPRESSION_DARK = 4;
    private final static int EXPRESSION_LIGHT = 5;
    private final static int OSCILLOSCOPE = 6;
    private final static int SCI_CHART_V4_DARK = 7;
    private final static int BERRY_BLUE = 8;

    @Bind(R.id.chart)
    SciChartSurface surface;

    @Bind(R.id.chartImage)
    ImageView imageView;

    @Bind(R.id.exportChart)
    Button exportChartButton;

    @Bind(R.id.renderSurfaceTypeSpinner)
    Spinner renderSurfaceTypeSpinner;

    @Bind(R.id.themeSelector)
    Spinner themeSelector;

    @Override
    protected int getLayoutId() {
        return R.layout.example_test_export_to_bitmap;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        exportChartButton.setOnClickListener(imageButtonClickListener);

        final SpinnerStringAdapter adapter = new SpinnerStringAdapter(getActivity(), R.array.render_surface_types);
        renderSurfaceTypeSpinner.setAdapter(adapter);

        themeSelector.setAdapter(new SpinnerStringAdapter(getActivity(), R.array.style_list));
        themeSelector.setSelection(7);
        themeSelector.setOnItemSelectedListener(new ItemSelectedListenerBase() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                setTheme(position);
            }
        });

        return view;
    }

    final View.OnClickListener imageButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Future<Bitmap> bitmapFuture = surface.exportToBitmap();
            try {
                Bitmap bitmap = bitmapFuture.get();
                imageView.setImageBitmap(bitmap);
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
    };

    @Override
    protected void initExample() {
        final DoubleSeries fourierSeries = DataManager.getInstance().getFourierSeries(1.0, 0.1, 5000);

        final IXyDataSeries<Double, Double> dataSeries = sciChartBuilder.newXyDataSeries(Double.class, Double.class).build();
        dataSeries.append(fourierSeries.xValues, fourierSeries.yValues);

        final IAxis xAxis = sciChartBuilder.newNumericAxis().withGrowBy(new DoubleRange(0.1d, 0.1d)).build();
        final IAxis yAxis = sciChartBuilder.newNumericAxis().withGrowBy(new DoubleRange(0.1d, 0.1d)).build();

        final FastLineRenderableSeries rSeries = sciChartBuilder.newLineSeries().withDataSeries(dataSeries).withStrokeStyle(0xFF279B27).build();

        UpdateSuspender.using(surface, new Runnable() {
            @Override
            public void run() {
                Collections.addAll(surface.getXAxes(), xAxis);
                Collections.addAll(surface.getYAxes(), yAxis);
                Collections.addAll(surface.getRenderableSeries(), rSeries);
                Collections.addAll(surface.getChartModifiers(), sciChartBuilder.newModifierGroupWithDefaultModifiers().build());
                Collections.addAll(surface.getAnnotations(),
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
                                .withHorizontalGravity(Gravity.RIGHT)
                                .build(),
                        sciChartBuilder.newHorizontalLineAnnotation()
                                .withPosition(7d, 2.8d)
                                .withIsEditable(true)
                                .withStroke(2, ColorUtil.Orange)
                                .build());
            }
        });
    }

    @OnItemSelected(R.id.renderSurfaceTypeSpinner)
    public void onRenderSurfaceTypeSelected(int position) {
        final String itemAtPosition = (String) renderSurfaceTypeSpinner.getItemAtPosition(position);
        if (itemAtPosition.contains("Canvas")) {
            surface.setRenderSurface(new RenderSurface(getActivity()));
        } else if (itemAtPosition.contains("OpenGL")) {
            surface.setRenderSurface(new RenderSurfaceGL(getActivity()));
        } else if (itemAtPosition.contains("Texture")) {
            surface.setRenderSurface(new GLTextureView(getActivity()));
        }
    }

    private void setTheme(int position) {
        int themeId;
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

        surface.setTheme(themeId);
    }
}