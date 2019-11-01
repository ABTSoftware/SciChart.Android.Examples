//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2019. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// CreateRealTimeWaterfall3DChartFragment.java is part of the SCICHART® Examples. Permission is hereby granted
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

import com.scichart.charting3d.model.dataSeries.waterfall.WaterfallDataSeries3D;
import com.scichart.charting3d.visuals.SciChartSurface3D;
import com.scichart.charting3d.visuals.axes.NumericAxis3D;
import com.scichart.charting3d.visuals.camera.Camera3D;
import com.scichart.charting3d.visuals.renderableSeries.data.GradientColorPalette;
import com.scichart.charting3d.visuals.renderableSeries.data.SolidColorBrushPalette;
import com.scichart.charting3d.visuals.renderableSeries.waterfall.WaterfallRenderableSeries3D;
import com.scichart.core.framework.UpdateSuspender;
import com.scichart.core.model.DoubleValues;
import com.scichart.examples.R;
import com.scichart.examples.data.DataManager;
import com.scichart.examples.fragments.base.ExampleBaseFragment;
import com.scichart.examples.utils.ItemSelectedListenerBase;
import com.scichart.examples.utils.ViewSettingsUtil;
import com.scichart.examples.utils.widgetgeneration.ImageViewWidget;
import com.scichart.examples.utils.widgetgeneration.Widget;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

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

public class CreateRealTimeWaterfall3DChartFragment extends ExampleBaseFragment {
    private static final int POINTS_PER_SLICE = 128;
    private static final int SLICE_COUNT = 10;

    private final int[] fillColors = new int[] {Red, Orange, Yellow, GreenYellow, DarkGreen};
    private final float[] fillStops = new float[] {0, .4f, .5f, .6f, 1};

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

    private int tick = 0;

    private final WaterfallDataSeries3D<Double, Double, Double> waterfallDataSeries = new WaterfallDataSeries3D<>(Double.class, Double.class, Double.class, POINTS_PER_SLICE, SLICE_COUNT);

    private final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
    private ScheduledFuture<?> schedule;

    @Override
    protected int getLayoutId() {
        return R.layout.example_single_chart3d_fragment;
    }

    @Override
    protected void initExample() {
        final Camera3D camera = sciChart3DBuilder.newCamera3D().build();

        final NumericAxis3D xAxis = sciChart3DBuilder.newNumericAxis3D().build();
        final NumericAxis3D yAxis = sciChart3DBuilder.newNumericAxis3D().build();
        final NumericAxis3D zAxis = sciChart3DBuilder.newNumericAxis3D().build();

        final DataManager dataManager = DataManager.getInstance();

        final List<DoubleValues> fftValues = dataManager.loadFFT(getActivity());

        waterfallDataSeries.pushRow(fftValues.get(0));

        waterfallDataSeries.setStartX(10d);
        waterfallDataSeries.setStepX(1d);

        waterfallDataSeries.setStartZ(25d);
        waterfallDataSeries.setStepZ(10d);

        final WaterfallRenderableSeries3D waterfall = sciChart3DBuilder.newWaterfallSeries3D()
                .withDataSeries(waterfallDataSeries)
                .withStrokeThicknes(1f)
                .withSliceThickness(5f)
                .withYColorMapping(gradientFillColorPalette)
                .withYStrokeColorMapping(gradientStrokeColorPalette)
                .build();

        UpdateSuspender.using(surface3d, new Runnable() {
            @Override
            public void run() {
                surface3d.getWorldDimensions().assign(200, 100, 200);

                surface3d.setCamera(camera);

                surface3d.setXAxis(xAxis);
                surface3d.setYAxis(yAxis);
                surface3d.setZAxis(zAxis);

                Collections.addAll(surface3d.getRenderableSeries(), waterfall);

                surface3d.getChartModifiers().add(sciChart3DBuilder.newModifierGroupWithDefaultModifiers().build());
            }
        });

        schedule = scheduledExecutorService.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                UpdateSuspender.using(surface3d, new Runnable() {
                    @Override
                    public void run() {
                        final int index = tick++ % fftValues.size();

                        waterfallDataSeries.pushRow(fftValues.get(index));
                    }
                });
            }
        }, 0, 25, TimeUnit.MILLISECONDS);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if (schedule != null)
            schedule.cancel(true);
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
                    rs.setSliceThickness(5f);
                } else {
                    rs.setSliceThickness(0f);
                }
            }
        });

        dialog.show();
    }
}
