//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2017. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// ErrorBarsChartFragment.java is part of the SCICHART® Examples. Permission is hereby granted
// to modify, create derivative works, distribute and publish any part of this source
// code whether for commercial, private or personal use.
//
// The SCICHART® examples are distributed in the hope that they will be useful, but
// without any warranty. It is provided "AS IS" without warranty of any kind, either
// expressed or implied.
//******************************************************************************

package com.scichart.examples.fragments;

import android.app.Dialog;
import android.view.View;
import android.widget.SeekBar;

import com.scichart.charting.model.dataSeries.HlDataSeries;
import com.scichart.charting.model.dataSeries.XyDataSeries;
import com.scichart.charting.visuals.SciChartSurface;
import com.scichart.charting.visuals.axes.IAxis;
import com.scichart.charting.visuals.axes.NumericAxis;
import com.scichart.charting.visuals.pointmarkers.EllipsePointMarker;
import com.scichart.charting.visuals.renderableSeries.ErrorDirection;
import com.scichart.charting.visuals.renderableSeries.ErrorType;
import com.scichart.charting.visuals.renderableSeries.FastErrorBarsRenderableSeries;
import com.scichart.charting.visuals.renderableSeries.IRenderableSeries;
import com.scichart.core.framework.UpdateSuspender;
import com.scichart.core.model.DoubleValues;
import com.scichart.drawing.common.PenStyle;
import com.scichart.examples.R;
import com.scichart.examples.data.DataManager;
import com.scichart.examples.data.DoubleSeries;
import com.scichart.examples.fragments.base.ExampleBaseFragment;
import com.scichart.examples.utils.SeekBarChangeListenerBase;
import com.scichart.examples.utils.ViewSettingsUtil;
import com.scichart.examples.utils.widgetgeneration.ImageViewWidget;
import com.scichart.examples.utils.widgetgeneration.Widget;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import butterknife.Bind;

public class ErrorBarsChartFragment extends ExampleBaseFragment {
    @Bind(R.id.chart)
    SciChartSurface surface;

    @Override
    public boolean showDefaultModifiersInToolbar() {
        return false;
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

    @Override
    protected int getLayoutId() {
        return R.layout.example_single_chart_fragment;
    }

    @Override
    protected void initExample() {
        final IAxis xAxis = new NumericAxis(getActivity());
        final IAxis yAxis = new NumericAxis(getActivity());

        final HlDataSeries<Double, Double> dataSeries0 = new HlDataSeries<>(Double.class, Double.class);
        final HlDataSeries<Double, Double> dataSeries1 = new HlDataSeries<>(Double.class, Double.class);

        DoubleSeries data = DataManager.getInstance().getFourierSeries(1.0, 0.1, 5.0, 5.15, 5000);
        fillSeries(dataSeries0, data, 1.0);
        fillSeries(dataSeries1, data, 1.3);

        final int color = 0xFFC6E6FF;
        final IRenderableSeries errorBars0 = sciChartBuilder.newErrorBarsSeries()
                .withDataSeries(dataSeries0)
                .withStrokeStyle(color, 1f)
                .withErrorDirection(ErrorDirection.Vertical)
                .withErrorType(ErrorType.Absolute)
                .build();
        final IRenderableSeries lineSeries = sciChartBuilder.newLineSeries()
                .withDataSeries(dataSeries0)
                .withStrokeStyle(color, 1f)
                .withPointMarker(sciChartBuilder.newPointMarker(new EllipsePointMarker()).withSize(5, 5).withFill(color).build())
                .build();

        final IRenderableSeries errorBars1 = sciChartBuilder.newErrorBarsSeries()
                .withDataSeries(dataSeries1)
                .withStrokeStyle(color, 1f)
                .withErrorDirection(ErrorDirection.Vertical)
                .withErrorType(ErrorType.Absolute)
                .build();
        final IRenderableSeries scatterSeries = sciChartBuilder.newScatterSeries()
                .withDataSeries(dataSeries1)
                .withPointMarker(sciChartBuilder.newPointMarker(new EllipsePointMarker()).withSize(7, 7).withFill(0x00FFFFFF).withStroke(color, 1f).build())
                .build();

        UpdateSuspender.using(surface, new Runnable() {
            @Override
            public void run() {
                Collections.addAll(surface.getXAxes(), xAxis);
                Collections.addAll(surface.getYAxes(), yAxis);
                Collections.addAll(surface.getRenderableSeries(), lineSeries, scatterSeries, errorBars0, errorBars1);
                Collections.addAll(surface.getChartModifiers(), sciChartBuilder.newModifierGroupWithDefaultModifiers().build());
            }
        });
    }

    private void fillSeries(HlDataSeries<Double, Double> dataSeries, DoubleSeries sourceData, final double scale) {
        final DoubleValues xValues = sourceData.xValues;
        final DoubleValues yValues = sourceData.yValues;

        final Random random = new Random();
        for (int i = 0; i < xValues.size(); i++) {
            final double y = yValues.get(i) * scale;
            dataSeries.append(xValues.get(i), y, random.nextDouble() * 0.2, random.nextDouble() * 0.2);
        }
    }

    private void openSettingsDialog() {
        final Dialog dialog = ViewSettingsUtil.createSettingsPopup(getActivity(), R.layout.example_error_bars_chart_popop_layout);

        ViewSettingsUtil.setUpSeekBar(dialog, R.id.data_point_width_seek_bar, 50, new SeekBarChangeListenerBase() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                onDataPointWidthChanged(progress);
            }
        });

        ViewSettingsUtil.setUpSeekBar(dialog, R.id.stroke_thickness_seek_bar, 1, new SeekBarChangeListenerBase() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                onStrokeThicknessChanged(progress + 1);
            }
        });

        dialog.show();
    }

    private void onDataPointWidthChanged(final int dataPointWidth) {
        UpdateSuspender.using(surface, new Runnable() {
            @Override
            public void run() {
                ((FastErrorBarsRenderableSeries) surface.getRenderableSeries().get(0)).setDataPointWidth(dataPointWidth / 100f);
                ((FastErrorBarsRenderableSeries) surface.getRenderableSeries().get(2)).setDataPointWidth(dataPointWidth / 100f);
            }
        });
    }

    private void onStrokeThicknessChanged(final int strokeThickness) {
        UpdateSuspender.using(surface, new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < surface.getRenderableSeries().size(); i++) {
                    IRenderableSeries renderableSeries = surface.getRenderableSeries().get(i);
                    final PenStyle currentStyle = renderableSeries.getStrokeStyle();
                    renderableSeries.setStrokeStyle(sciChartBuilder.newPen()
                            .withColor(currentStyle.getColor())
                            .withAntiAliasing(currentStyle.antiAliasing)
                            .withThickness(strokeThickness)
                            .build());
                }
            }
        });
    }
}