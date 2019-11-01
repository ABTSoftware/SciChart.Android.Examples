//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2019. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// CreateWaterfall3DChartFragment.java is part of the SCICHART® Examples. Permission is hereby granted
// to modify, create derivative works, distribute and publish any part of this source
// code whether for commercial, private or personal use.
//
// The SCICHART® examples are distributed in the hope that they will be useful, but
// without any warranty. It is provided "AS IS" without warranty of any kind, either
// expressed or implied.
//******************************************************************************

package com.scichart.examples.fragments.charts3d;

import android.app.Dialog;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;

import com.scichart.charting.visuals.axes.AutoRange;
import com.scichart.charting3d.model.dataSeries.waterfall.WaterfallDataSeries3D;
import com.scichart.charting3d.visuals.SciChartSurface3D;
import com.scichart.charting3d.visuals.axes.NumericAxis3D;
import com.scichart.charting3d.visuals.camera.Camera3D;
import com.scichart.charting3d.visuals.renderableSeries.data.GradientColorPalette;
import com.scichart.charting3d.visuals.renderableSeries.data.SolidColorBrushPalette;
import com.scichart.charting3d.visuals.renderableSeries.waterfall.WaterfallRenderableSeries3D;
import com.scichart.core.framework.UpdateSuspender;
import com.scichart.examples.R;
import com.scichart.examples.data.DataManager;
import com.scichart.examples.data.Radix2FFT;
import com.scichart.examples.fragments.base.ExampleBaseFragment;
import com.scichart.examples.utils.ItemSelectedListenerBase;
import com.scichart.examples.utils.ViewSettingsUtil;
import com.scichart.examples.utils.widgetgeneration.ImageViewWidget;
import com.scichart.examples.utils.widgetgeneration.Widget;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import butterknife.BindView;

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

public class CreateWaterfall3DChartFragment extends ExampleBaseFragment {
    private static final int POINTS_PER_SLICE = 128;
    private static final int SLICE_COUNT = 20;

    private final Radix2FFT transform = new Radix2FFT(POINTS_PER_SLICE);
    private final Random random = new Random();

    private final int[] fillColors = new int[] {Red, Orange, Yellow, GreenYellow, DarkGreen};
    private final float[] fillStops = new float[] {0, .25f, .5f, .75f, 1};

    private final GradientColorPalette gradientFillColorPalette = new GradientColorPalette(fillColors, fillStops);

    private final int[] strokeColors = new int[] {Crimson, DarkOrange, LimeGreen, LimeGreen};
    private final float[] strokeStops = new float[]{0, .33f, .67f, 1};

    private final GradientColorPalette gradientStrokeColorPalette = new GradientColorPalette(strokeColors, strokeStops);

    private final SolidColorBrushPalette transparentColorPalette = new SolidColorBrushPalette(Transparent);
    private final SolidColorBrushPalette solidStrokeColorPalette = new SolidColorBrushPalette(LimeGreen);
    private final SolidColorBrushPalette solidFillColorPalette = new SolidColorBrushPalette(0xAA00BFFF);

    private int currentFillColorPalette = 0; // by default YAxis
    private int currentStrokeColorPalette = 0; // by default YAxis

    @BindView(R.id.chart3d)
    SciChartSurface3D surface3d;

    @Override
    protected int getLayoutId() {
        return R.layout.example_single_chart3d_fragment;
    }

    @Override
    protected void initExample() {
        final Camera3D camera = sciChart3DBuilder.newCamera3D().build();

        final NumericAxis3D xAxis = sciChart3DBuilder.newNumericAxis3D().build();
        final NumericAxis3D yAxis = sciChart3DBuilder.newNumericAxis3D().build();
        final NumericAxis3D zAxis = sciChart3DBuilder.newNumericAxis3D().withAutoRangeMode(AutoRange.Always).build();

        final WaterfallDataSeries3D<Double, Double, Double> ds = new WaterfallDataSeries3D<>(Double.class, Double.class, Double.class, POINTS_PER_SLICE, SLICE_COUNT);
        ds.setStartX(10d);
        ds.setStepX(1d);

        ds.setStartZ(1d);

        fillDataSeries(ds);

        final WaterfallRenderableSeries3D rs = sciChart3DBuilder.newWaterfallSeries3D()
                .withDataSeries(ds)
                .withStrokeThicknes(1f)
                .withSliceThickness(0f)
                .withYColorMapping(gradientFillColorPalette)
                .withYStrokeColorMapping(gradientStrokeColorPalette)
                .withOpacity(0.8f)
                .build();

        UpdateSuspender.using(surface3d, new Runnable() {
            @Override
            public void run() {
                surface3d.setCamera(camera);

                surface3d.setXAxis(xAxis);
                surface3d.setYAxis(yAxis);
                surface3d.setZAxis(zAxis);

                surface3d.getRenderableSeries().add(rs);

                surface3d.getChartModifiers().add(sciChart3DBuilder.newModifierGroup()
                        .withPinchZoomModifier3D().build()
                        .withOrbitModifier3D().withReceiveHandledEvents(true).build()
                        .withZoomExtentsModifier3D().build()
                        .withVertexSelectionModifier().withReceiveHandledEvents(true).build()
                        .build());
            }
        });
    }

    private void fillDataSeries(WaterfallDataSeries3D<Double, Double, Double> ds) {
        final int count = POINTS_PER_SLICE * 2;

        final double[] re = new double[count];
        final double[] im = new double[count];

        final DataManager dataManager = DataManager.getInstance();

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

    @Override
    public List<Widget> getToolbarItems() {
        return new ArrayList<Widget>() {
            {
                add(new ImageViewWidget.Builder().setId(R.drawable.example_toolbar_settings).setListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openSettingsDialog();
                    }
                }).build());
            }
        };
    }

    private void openSettingsDialog() {
        final WaterfallRenderableSeries3D rs = (WaterfallRenderableSeries3D) surface3d.getRenderableSeries().get(0);

        final Dialog dialog = ViewSettingsUtil.createSettingsPopup(getActivity(), R.layout.example_waterfall_3d_popup_layout);

        ViewSettingsUtil.setUpSpinner(dialog, R.id.strokePaletteSelector, R.array.stroke_color_palette_list, currentStrokeColorPalette, new ItemSelectedListenerBase() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0: // YAxis
                        currentStrokeColorPalette = 0;
                        rs.setYStrokeColorMapping(gradientStrokeColorPalette);
                        rs.setZStrokeColorMapping(null);
                        break;
                    case 1: // ZAxis
                        currentStrokeColorPalette = 1;
                        rs.setYStrokeColorMapping(null);
                        rs.setZStrokeColorMapping(gradientStrokeColorPalette);
                        break;
                    case 2: // Solid
                        currentStrokeColorPalette = 2;
                        rs.setYStrokeColorMapping(solidStrokeColorPalette);
                        rs.setZStrokeColorMapping(solidStrokeColorPalette);
                        break;
                    case 3: // None
                        currentStrokeColorPalette = 3;
                        rs.setYStrokeColorMapping(transparentColorPalette);
                        rs.setZStrokeColorMapping(transparentColorPalette);
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
                        rs.setYColorMapping(gradientFillColorPalette);
                        rs.setZColorMapping(null);
                        break;
                    case 1: // ZAxis
                        currentFillColorPalette = 1;
                        rs.setYColorMapping(null);
                        rs.setZColorMapping(gradientFillColorPalette);
                        break;
                    case 2: // Solid
                        currentFillColorPalette = 2;
                        rs.setYColorMapping(solidFillColorPalette);
                        rs.setZColorMapping(solidFillColorPalette);
                        break;
                    case 3: // None
                        currentFillColorPalette = 3;
                        rs.setYColorMapping(transparentColorPalette);
                        rs.setZColorMapping(transparentColorPalette);
                        break;
                }
            }
        });

        ViewSettingsUtil.setUpCheckBox(dialog, R.id.showPointMarkers, rs.getPointMarker() != null, new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    rs.setPointMarker(sciChart3DBuilder.newSpherePointMarker3D().withFill(Blue).withSize(5f).build());
                } else {
                    rs.setPointMarker(null);
                }
            }
        });

        ViewSettingsUtil.setUpCheckBox(dialog, R.id.isVolumetric, rs.getSliceThickness() > 0, new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    rs.setSliceThickness(10f);
                } else {
                    rs.setSliceThickness(0f);
                }
            }
        });

        dialog.show();
    }
}
