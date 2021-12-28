//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2021. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// BubbleChartFragment.java is part of SCICHART®, High Performance Scientific Charts
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

package com.scichart.examples.fragments.examples2d.basicChartTypes;

import android.app.Dialog;
import android.widget.SeekBar;

import androidx.annotation.NonNull;

import com.scichart.charting.model.dataSeries.IXyzDataSeries;
import com.scichart.charting.modifiers.RubberBandXyZoomModifier;
import com.scichart.charting.modifiers.ZoomExtentsModifier;
import com.scichart.charting.visuals.SciChartSurface;
import com.scichart.charting.visuals.axes.IAxis;
import com.scichart.charting.visuals.renderableSeries.FastBubbleRenderableSeries;
import com.scichart.charting.visuals.renderableSeries.FastLineRenderableSeries;
import com.scichart.core.framework.UpdateSuspender;
import com.scichart.drawing.common.SolidBrushStyle;
import com.scichart.examples.R;
import com.scichart.examples.data.DataManager;
import com.scichart.examples.data.TradeData;
import com.scichart.examples.fragments.base.ExampleSingleChartBaseFragment;
import com.scichart.examples.utils.SeekBarChangeListenerBase;
import com.scichart.examples.utils.ViewSettingsUtil;
import com.scichart.examples.utils.interpolator.ElasticOutInterpolator;
import com.scichart.examples.utils.widgetgeneration.ImageViewWidget;
import com.scichart.examples.utils.widgetgeneration.Widget;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class BubbleChartFragment extends ExampleSingleChartBaseFragment {

    private final int minSeekBarValue = 5;
    private int zScaleFactor = 30;

    @NonNull
    @Override
    public List<Widget> getToolbarItems() {
        return new ArrayList<Widget>() {{
            add(new ImageViewWidget.Builder().setId(R.drawable.example_toolbar_settings).setListener(v -> openSettingsDialog()).build());
        }};
    }

    @Override
    protected void initExample(@NonNull SciChartSurface surface) {
        final IAxis xAxis = sciChartBuilder.newDateAxis().withGrowBy(0d, 0.1d).build();
        final IAxis yAxis = sciChartBuilder.newNumericAxis().withGrowBy(0d, 0.1d).build();

        final IXyzDataSeries<Date, Double, Double> dataSeries = sciChartBuilder.newXyzDataSeries(Date.class, Double.class, Double.class).build();

        List<TradeData> tradeTicks = DataManager.getInstance().getTradeTicks(getActivity());
        for (int i = 0; i < tradeTicks.size(); i++) {
            TradeData tradeData = tradeTicks.get(i);
            dataSeries.append(tradeData.getTradeDate(), tradeData.getTradePrice(), tradeData.getTradeSize());
        }

        final FastLineRenderableSeries lineSeries = sciChartBuilder.newLineSeries().withDataSeries(dataSeries).withStrokeStyle(0xffff3333, 2f).build();

        final FastBubbleRenderableSeries rSeries = sciChartBuilder.newBubbleSeries()
                .withDataSeries(dataSeries)
                .withZScaleFactor(zScaleFactor / 10f)
                .withBubbleBrushStyle(new SolidBrushStyle(0x77CCCCCC))
                .withStrokeStyle(0xFFCCCCCC, 2f, true)
                .withAutoZRange(false)
                .build();

        UpdateSuspender.using(surface, () -> {
            Collections.addAll(surface.getXAxes(), xAxis);
            Collections.addAll(surface.getYAxes(), yAxis);
            Collections.addAll(surface.getRenderableSeries(), lineSeries, rSeries);
            Collections.addAll(surface.getChartModifiers(), new RubberBandXyZoomModifier(), new ZoomExtentsModifier());

            sciChartBuilder.newAnimator(lineSeries).withScaleTransformation(10600d).withInterpolator(new ElasticOutInterpolator()).withDuration(3000).withStartDelay(350).start();
            sciChartBuilder.newAnimator(rSeries).withScaleTransformation(10600d).withInterpolator(new ElasticOutInterpolator()).withDuration(3000).withStartDelay(350).start();
        });
    }

    private void openSettingsDialog() {
        final Dialog dialog = ViewSettingsUtil.createSettingsPopup(getActivity(), R.layout.example_bubble_chart_popop_layout);

        ViewSettingsUtil.setUpSeekBar(dialog, R.id.z_scale_seek_bar, zScaleFactor, new SeekBarChangeListenerBase() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                zScaleFactor = progress + minSeekBarValue;
                onZScaleFactorChanged();
            }
        });

        dialog.show();
    }

    private void onZScaleFactorChanged() {
        final SciChartSurface surface = binding.surface;
        UpdateSuspender.using(surface, () -> {
            FastBubbleRenderableSeries rSeries = (FastBubbleRenderableSeries) surface.getRenderableSeries().get(1);
            rSeries.setZScaleFactor(zScaleFactor / 10f);
        });
    }
}