//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2021. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// CreateRealTimeWaterfall3DChartFragment.java is part of SCICHART®, High Performance Scientific Charts
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

package com.scichart.examples.fragments.examples3d.realtime3DCharts;

import android.app.Dialog;
import android.view.View;
import android.widget.AdapterView;

import com.scichart.charting3d.model.dataSeries.waterfall.WaterfallDataSeries3D;
import com.scichart.charting3d.visuals.SciChartSurface3D;
import com.scichart.charting3d.visuals.axes.NumericAxis3D;
import com.scichart.charting3d.visuals.renderableSeries.data.GradientColorPalette;
import com.scichart.charting3d.visuals.renderableSeries.data.SolidColorBrushPalette;
import com.scichart.charting3d.visuals.renderableSeries.waterfall.WaterfallRenderableSeries3D;
import com.scichart.core.framework.UpdateSuspender;
import com.scichart.core.model.DoubleValues;
import com.scichart.examples.R;
import com.scichart.examples.data.DataManager;
import com.scichart.examples.fragments.base.ExampleSingleChart3DBaseFragment;
import com.scichart.examples.utils.ItemSelectedListenerBase;
import com.scichart.examples.utils.ViewSettingsUtil;
import com.scichart.examples.utils.widgetgeneration.ImageViewWidget;
import com.scichart.examples.utils.widgetgeneration.Widget;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

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

import androidx.annotation.NonNull;

public class RealTimeWaterfall3DChartFragment extends ExampleSingleChart3DBaseFragment {
    private static final int POINTS_PER_SLICE = 128;
    private static final int SLICE_COUNT = 10;

    private final GradientColorPalette gradientFillColorPalette = new GradientColorPalette(
            new int[]{Red, Orange, Yellow, GreenYellow, DarkGreen},
            new float[]{0, .4f, .5f, .6f, 1}
    );

    private final GradientColorPalette gradientStrokeColorPalette = new GradientColorPalette(
            new int[]{Crimson, DarkOrange, LimeGreen, LimeGreen},
            new float[]{0, .33f, .67f, 1}
    );

    private final SolidColorBrushPalette transparentColorPalette = new SolidColorBrushPalette(Transparent);
    private final SolidColorBrushPalette solidStrokeColorPalette = new SolidColorBrushPalette(LimeGreen);
    private final SolidColorBrushPalette solidFillColorPalette = new SolidColorBrushPalette(0xAA00BFFF);

    private int currentFillColorPalette = 0; // by default YAxis
    private int currentStrokeColorPalette = 0; // by default YAxis
    private int tick = 0;

    private final WaterfallDataSeries3D<Double, Double, Double> waterfallDataSeries = new WaterfallDataSeries3D<>(Double.class, Double.class, Double.class, POINTS_PER_SLICE, SLICE_COUNT);

    private final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
    private ScheduledFuture<?> schedule;

    @Override
    protected void initExample(@NonNull SciChartSurface3D surface3d) {
        final NumericAxis3D xAxis = sciChart3DBuilder.newNumericAxis3D().build();
        final NumericAxis3D yAxis = sciChart3DBuilder.newNumericAxis3D().build();
        final NumericAxis3D zAxis = sciChart3DBuilder.newNumericAxis3D().build();

        waterfallDataSeries.setStartX(10d);
        waterfallDataSeries.setStepX(1d);
        waterfallDataSeries.setStartZ(25d);
        waterfallDataSeries.setStepZ(10d);

        final DataManager dataManager = DataManager.getInstance();
        final List<DoubleValues> fftValues = dataManager.loadFFT(requireContext());
        waterfallDataSeries.pushRow(fftValues.get(0));

        final WaterfallRenderableSeries3D waterfall = sciChart3DBuilder.newWaterfallSeries3D()
                .withDataSeries(waterfallDataSeries)
                .withStrokeThickness(1f)
                .withSliceThickness(5f)
                .withYColorMapping(gradientFillColorPalette)
                .withYStrokeColorMapping(gradientStrokeColorPalette)
                .build();

        UpdateSuspender.using(surface3d, () -> {
            surface3d.setXAxis(xAxis);
            surface3d.setYAxis(yAxis);
            surface3d.setZAxis(zAxis);
            surface3d.getRenderableSeries().add(waterfall);
            surface3d.getChartModifiers().add(sciChart3DBuilder.newModifierGroupWithDefaultModifiers().build());

            surface3d.getWorldDimensions().assign(200, 100, 200);
        });

        schedule = scheduledExecutorService.scheduleWithFixedDelay(() -> UpdateSuspender.using(surface3d, () -> {
            final int index = tick++ % fftValues.size();
            waterfallDataSeries.pushRow(fftValues.get(index));
        }), 0, 25, TimeUnit.MILLISECONDS);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if (schedule != null) {
            schedule.cancel(true);
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
        final SciChartSurface3D surface3d = binding.surface3d;
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
                        rs.setYColorMapping(gradientStrokeColorPalette);
                        rs.setZColorMapping(null);
                        break;
                    case 1: // ZAxis
                        currentFillColorPalette = 1;
                        rs.setYColorMapping(null);
                        rs.setZColorMapping(gradientStrokeColorPalette);
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

        ViewSettingsUtil.setUpCheckBox(dialog, R.id.showPointMarkers, rs.getPointMarker() != null, (buttonView, isChecked) -> {
            if (isChecked) {
                rs.setPointMarker(sciChart3DBuilder.newSpherePointMarker3D().withFill(Blue).withSize(5f).build());
            } else {
                rs.setPointMarker(null);
            }
        });

        ViewSettingsUtil.setUpCheckBox(dialog, R.id.isVolumetric, rs.getSliceThickness() > 0, (buttonView, isChecked) -> {
            if (isChecked) {
                rs.setSliceThickness(5f);
            } else {
                rs.setSliceThickness(0f);
            }
        });

        dialog.show();
    }
}
