//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2021. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// CreateWaterfall3DChartFragment.java is part of SCICHART®, High Performance Scientific Charts
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

package com.scichart.examples.fragments.examples3d.basicChartTypes;

import static com.scichart.charting.visuals.axes.AutoRange.Always;
import static com.scichart.drawing.utility.ColorUtil.Blue;
import static com.scichart.drawing.utility.ColorUtil.Crimson;
import static com.scichart.drawing.utility.ColorUtil.DarkGreen;
import static com.scichart.drawing.utility.ColorUtil.DarkOrange;
import static com.scichart.drawing.utility.ColorUtil.GreenYellow;
import static com.scichart.drawing.utility.ColorUtil.LimeGreen;
import static com.scichart.drawing.utility.ColorUtil.Orange;
import static com.scichart.drawing.utility.ColorUtil.Red;
import static com.scichart.drawing.utility.ColorUtil.Transparent;
import static com.scichart.drawing.utility.ColorUtil.Yellow;

import android.app.Dialog;
import android.view.View;
import android.widget.AdapterView;

import androidx.annotation.NonNull;

import com.scichart.charting3d.model.dataSeries.waterfall.WaterfallDataSeries3D;
import com.scichart.charting3d.visuals.SciChartSurface3D;
import com.scichart.charting3d.visuals.axes.NumericAxis3D;
import com.scichart.charting3d.visuals.renderableSeries.data.GradientColorPalette;
import com.scichart.charting3d.visuals.renderableSeries.data.SolidColorBrushPalette;
import com.scichart.charting3d.visuals.renderableSeries.waterfall.WaterfallRenderableSeries3D;
import com.scichart.core.framework.UpdateSuspender;
import com.scichart.examples.R;
import com.scichart.examples.data.DataManager;
import com.scichart.examples.data.Radix2FFT;
import com.scichart.examples.fragments.base.ExampleSingleChart3DBaseFragment;
import com.scichart.examples.utils.ItemSelectedListenerBase;
import com.scichart.examples.utils.ViewSettingsUtil;
import com.scichart.examples.utils.widgetgeneration.ImageViewWidget;
import com.scichart.examples.utils.widgetgeneration.Widget;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Waterfall3DChartFragment extends ExampleSingleChart3DBaseFragment {
    private static final int POINTS_PER_SLICE = 128;
    private static final int SLICE_COUNT = 20;

    private final Random random = new Random();
    private final Radix2FFT transform = new Radix2FFT(POINTS_PER_SLICE);

    private final GradientColorPalette gradientFillColorPalette = new GradientColorPalette(
            new int[]{Red, Orange, Yellow, GreenYellow, DarkGreen},
            new float[]{0, .25f, .5f, .75f, 1});

    private final GradientColorPalette gradientStrokeColorPalette = new GradientColorPalette(
            new int[]{Crimson, DarkOrange, LimeGreen, LimeGreen},
            new float[]{0, .33f, .67f, 1});

    private final SolidColorBrushPalette transparentColorPalette = new SolidColorBrushPalette(Transparent);
    private final SolidColorBrushPalette solidStrokeColorPalette = new SolidColorBrushPalette(LimeGreen);
    private final SolidColorBrushPalette solidFillColorPalette = new SolidColorBrushPalette(0xAA00BFFF);

    private int currentFillColorPalette = 0; // by default YAxis
    private int currentStrokeColorPalette = 0; // by default YAxis

    final WaterfallRenderableSeries3D rSeries = sciChart3DBuilder.newWaterfallSeries3D()
            .withStrokeThickness(1f)
            .withSliceThickness(0f)
            .withYColorMapping(gradientFillColorPalette)
            .withYStrokeColorMapping(gradientStrokeColorPalette)
            .withOpacity(0.8f)
            .build();

    @Override
    protected void initExample(@NonNull SciChartSurface3D surface3d) {
        final NumericAxis3D xAxis = sciChart3DBuilder.newNumericAxis3D().build();
        final NumericAxis3D yAxis = sciChart3DBuilder.newNumericAxis3D().build();
        final NumericAxis3D zAxis = sciChart3DBuilder.newNumericAxis3D().withAutoRangeMode(Always).build();

        final WaterfallDataSeries3D<Double, Double, Double> ds = new WaterfallDataSeries3D<>(Double.class, Double.class, Double.class, POINTS_PER_SLICE, SLICE_COUNT);
        ds.setStartX(10d);
        ds.setStepX(1d);
        ds.setStartZ(1d);

        fillDataSeries(ds);
        rSeries.setDataSeries(ds);

        UpdateSuspender.using(surface3d, () -> {
            surface3d.setXAxis(xAxis);
            surface3d.setYAxis(yAxis);
            surface3d.setZAxis(zAxis);

            surface3d.getRenderableSeries().add(rSeries);

            surface3d.getChartModifiers().add(sciChart3DBuilder.newModifierGroup()
                    .withPinchZoomModifier3D().build()
                    .withOrbitModifier3D().withReceiveHandledEvents(true).build()
                    .withZoomExtentsModifier3D().build()
                    .withVertexSelectionModifier().withReceiveHandledEvents(true).build()
                    .build());
        });
    }

    private void fillDataSeries(WaterfallDataSeries3D<Double, Double, Double> ds) {
        final DataManager dataManager = DataManager.getInstance();
        final int count = POINTS_PER_SLICE * 2;

        final double[] re = new double[count];
        final double[] im = new double[count];
        for (int sliceIndex = 0; sliceIndex < SLICE_COUNT; sliceIndex++) {
            for (int i = 0; i < count; i++) {
                re[i] = 2d * Math.sin(Math.PI * i / 10) +
                        5d * Math.sin(Math.PI * i / 5) +
                        2d * dataManager.getRandomDouble();
                im[i] = -10d;
            }

            transform.run(re, im);

            final double scaleCoef = Math.pow(1.5, sliceIndex * 0.3) / Math.pow(1.5, SLICE_COUNT * 0.3);

            for (int pointIndex = 0; pointIndex < POINTS_PER_SLICE; pointIndex++) {
                final double reValue = re[pointIndex];
                final double imValue = im[pointIndex];
                final double mag = Math.sqrt(reValue * reValue + imValue * imValue);

                double yVal = (random.nextInt(10) + 10) * Math.log10(mag / POINTS_PER_SLICE);
                yVal = (yVal < -25 || yVal > -5)
                        ? (yVal < -25) ? -25 : random.nextInt(9) - 6
                        : yVal;

                ds.updateYAt(pointIndex, sliceIndex, -yVal * scaleCoef);
            }
        }
    }

    @NonNull
    @Override
    public List<Widget> getToolbarItems() {
        return new ArrayList<Widget>() {{
                add(new ImageViewWidget.Builder().setId(R.drawable.example_toolbar_settings).setListener(v -> openSettingsDialog()).build());
        }};
    }

    private void openSettingsDialog() {
        final Dialog dialog = ViewSettingsUtil.createSettingsPopup(getActivity(), R.layout.example_waterfall_3d_popup_layout);

        ViewSettingsUtil.setUpSpinner(dialog, R.id.strokePaletteSelector, R.array.stroke_color_palette_list, currentStrokeColorPalette, new ItemSelectedListenerBase() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0: // YAxis
                        currentStrokeColorPalette = 0;
                        rSeries.setYStrokeColorMapping(gradientStrokeColorPalette);
                        rSeries.setZStrokeColorMapping(null);
                        break;
                    case 1: // ZAxis
                        currentStrokeColorPalette = 1;
                        rSeries.setYStrokeColorMapping(null);
                        rSeries.setZStrokeColorMapping(gradientStrokeColorPalette);
                        break;
                    case 2: // Solid
                        currentStrokeColorPalette = 2;
                        rSeries.setYStrokeColorMapping(solidStrokeColorPalette);
                        rSeries.setZStrokeColorMapping(solidStrokeColorPalette);
                        break;
                    case 3: // None
                        currentStrokeColorPalette = 3;
                        rSeries.setYStrokeColorMapping(transparentColorPalette);
                        rSeries.setZStrokeColorMapping(transparentColorPalette);
                        break;
                }
            }
        });

        ViewSettingsUtil.setUpSpinner(dialog, R.id.fillPaletteSelector, R.array.fill_color_palette_list, currentFillColorPalette, new ItemSelectedListenerBase() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0: // YAxis
                        currentFillColorPalette = 0;
                        rSeries.setYColorMapping(gradientFillColorPalette);
                        rSeries.setZColorMapping(null);
                        break;
                    case 1: // ZAxis
                        currentFillColorPalette = 1;
                        rSeries.setYColorMapping(null);
                        rSeries.setZColorMapping(gradientFillColorPalette);
                        break;
                    case 2: // Solid
                        currentFillColorPalette = 2;
                        rSeries.setYColorMapping(solidFillColorPalette);
                        rSeries.setZColorMapping(solidFillColorPalette);
                        break;
                    case 3: // None
                        currentFillColorPalette = 3;
                        rSeries.setYColorMapping(transparentColorPalette);
                        rSeries.setZColorMapping(transparentColorPalette);
                        break;
                }
            }
        });

        ViewSettingsUtil.setUpCheckBox(dialog, R.id.showPointMarkers, rSeries.getPointMarker() != null, (buttonView, isChecked) -> {
            rSeries.setPointMarker(isChecked
                    ? sciChart3DBuilder.newSpherePointMarker3D().withFill(Blue).withSize(5f).build()
                    : null);
        });

        ViewSettingsUtil.setUpCheckBox(dialog, R.id.isVolumetric, rSeries.getSliceThickness() > 0, (buttonView, isChecked) -> {
            rSeries.setSliceThickness(isChecked ? 10f: 0f);
        });

        dialog.show();
    }
}
