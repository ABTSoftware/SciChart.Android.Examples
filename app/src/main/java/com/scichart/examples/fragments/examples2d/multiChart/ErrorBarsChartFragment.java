//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2021. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// ErrorBarsChartFragment.java is part of SCICHART®, High Performance Scientific Charts
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

package com.scichart.examples.fragments.examples2d.multiChart;

import android.app.Dialog;
import android.view.animation.LinearInterpolator;
import android.widget.SeekBar;

import androidx.annotation.NonNull;

import com.scichart.charting.model.dataSeries.HlDataSeries;
import com.scichart.charting.visuals.SciChartSurface;
import com.scichart.charting.visuals.pointmarkers.EllipsePointMarker;
import com.scichart.charting.visuals.renderableSeries.ErrorDirection;
import com.scichart.charting.visuals.renderableSeries.ErrorType;
import com.scichart.charting.visuals.renderableSeries.FastErrorBarsRenderableSeries;
import com.scichart.charting.visuals.renderableSeries.FastLineRenderableSeries;
import com.scichart.charting.visuals.renderableSeries.IRenderableSeries;
import com.scichart.charting.visuals.renderableSeries.XyScatterRenderableSeries;
import com.scichart.core.framework.UpdateSuspender;
import com.scichart.core.model.DoubleValues;
import com.scichart.drawing.common.PenStyle;
import com.scichart.examples.R;
import com.scichart.examples.data.DataManager;
import com.scichart.examples.data.DoubleSeries;
import com.scichart.examples.fragments.base.ExampleSingleChartBaseFragment;
import com.scichart.examples.utils.SeekBarChangeListenerBase;
import com.scichart.examples.utils.ViewSettingsUtil;
import com.scichart.examples.utils.interpolator.DefaultInterpolator;
import com.scichart.examples.utils.interpolator.ElasticOutInterpolator;
import com.scichart.examples.utils.widgetgeneration.ImageViewWidget;
import com.scichart.examples.utils.widgetgeneration.Widget;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class ErrorBarsChartFragment extends ExampleSingleChartBaseFragment {

    @Override
    public boolean showDefaultModifiersInToolbar() {
        return false;
    }

    @NonNull
    @Override
    public List<Widget> getToolbarItems() {
        return new ArrayList<Widget>() {{
                add(new ImageViewWidget.Builder().setId(R.drawable.example_toolbar_settings).setListener(v -> openSettingsDialog()).build());
        }};
    }

    @Override
    protected void initExample(@NonNull SciChartSurface surface) {
        surface.setTheme(R.style.SciChart_NavyBlue);

        final HlDataSeries<Double, Double> dataSeries0 = new HlDataSeries<>(Double.class, Double.class);
        final HlDataSeries<Double, Double> dataSeries1 = new HlDataSeries<>(Double.class, Double.class);

        DoubleSeries data = DataManager.getInstance().getFourierSeries(1.0, 0.1, 5.0, 5.15, 5000);
        fillSeries(dataSeries0, data, 1.0);
        fillSeries(dataSeries1, data, 1.3);

        final int color = 0xFFC6E6FF;
        final FastErrorBarsRenderableSeries errorBars0 = sciChartBuilder.newErrorBarsSeries()
                .withDataSeries(dataSeries0)
                .withStrokeStyle(color, 1f)
                .withErrorDirection(ErrorDirection.Vertical)
                .withErrorType(ErrorType.Absolute)
                .build();
        final FastLineRenderableSeries lineSeries = sciChartBuilder.newLineSeries()
                .withDataSeries(dataSeries0)
                .withStrokeStyle(color, 1f)
                .withPointMarker(sciChartBuilder.newPointMarker(new EllipsePointMarker()).withSize(5, 5).withFill(color).build())
                .build();

        final FastErrorBarsRenderableSeries errorBars1 = sciChartBuilder.newErrorBarsSeries()
                .withDataSeries(dataSeries1)
                .withStrokeStyle(color, 1f)
                .withErrorDirection(ErrorDirection.Vertical)
                .withErrorType(ErrorType.Absolute)
                .build();
        final XyScatterRenderableSeries scatterSeries = sciChartBuilder.newScatterSeries()
                .withDataSeries(dataSeries1)
                .withPointMarker(sciChartBuilder.newPointMarker(new EllipsePointMarker()).withSize(7, 7).withFill(0x00FFFFFF).withStroke(color, 1f).build())
                .build();

        UpdateSuspender.using(surface, () -> {
            Collections.addAll(surface.getXAxes(), sciChartBuilder.newNumericAxis().build());
            Collections.addAll(surface.getYAxes(), sciChartBuilder.newNumericAxis().build());
            Collections.addAll(surface.getRenderableSeries(), lineSeries, scatterSeries, errorBars0, errorBars1);
            Collections.addAll(surface.getChartModifiers(), sciChartBuilder.newModifierGroupWithDefaultModifiers().build());

            sciChartBuilder.newAnimator(errorBars0).withWaveTransformation(2.5).withInterpolator(DefaultInterpolator.getInterpolator()).withDuration(500).withStartDelay(100).start();
//            sciChartBuilder.newAnimator(lineSeries).withSweepTransformation().withInterpolator(new LinearInterpolator()).withDuration(3000).withStartDelay(350).start();
            sciChartBuilder.newAnimator(errorBars1).withScaleTransformation(3.5).withInterpolator(DefaultInterpolator.getInterpolator()).withDuration(500).withStartDelay(600).start();
//            sciChartBuilder.newAnimator(scatterSeries).withSweepTransformation().withInterpolator(new LinearInterpolator()).withDuration(3000).withStartDelay(350).start();
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
        final SciChartSurface surface = binding.surface;
        UpdateSuspender.using(surface, () -> {
            ((FastErrorBarsRenderableSeries) surface.getRenderableSeries().get(2)).setDataPointWidth(dataPointWidth / 100f);
            ((FastErrorBarsRenderableSeries) surface.getRenderableSeries().get(3)).setDataPointWidth(dataPointWidth / 100f);
        });
    }

    private void onStrokeThicknessChanged(final int strokeThickness) {
        final SciChartSurface surface = binding.surface;
        UpdateSuspender.using(surface, () -> {
            for (int i = 0; i < surface.getRenderableSeries().size(); i++) {
                IRenderableSeries renderableSeries = surface.getRenderableSeries().get(i);
                final PenStyle currentStyle = renderableSeries.getStrokeStyle();
                renderableSeries.setStrokeStyle(sciChartBuilder.newPen()
                        .withColor(currentStyle.getColor())
                        .withAntiAliasing(currentStyle.antiAliasing)
                        .withThickness(strokeThickness)
                        .build());
            }
        });
    }
}