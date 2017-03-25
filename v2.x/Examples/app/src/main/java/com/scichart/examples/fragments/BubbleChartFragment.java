//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2017. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// BubbleChartFragment.java is part of the SCICHART® Examples. Permission is hereby granted
// to modify, create derivative works, distribute and publish any part of this source
// code whether for commercial, private or personal use.
//
// The SCICHART® examples are distributed in the hope that they will be useful, but
// without any warranty. It is provided "AS IS" without warranty of any kind, either
// expressed or implied.
//******************************************************************************

package com.scichart.examples.fragments;

import android.app.Dialog;
import android.graphics.Color;
import android.view.View;
import android.widget.SeekBar;

import com.scichart.charting.model.dataSeries.IXyzDataSeries;
import com.scichart.charting.modifiers.RubberBandXyZoomModifier;
import com.scichart.charting.modifiers.ZoomExtentsModifier;
import com.scichart.charting.visuals.SciChartSurface;
import com.scichart.charting.visuals.axes.IAxis;
import com.scichart.charting.visuals.renderableSeries.FastBubbleRenderableSeries;
import com.scichart.charting.visuals.renderableSeries.FastLineRenderableSeries;
import com.scichart.core.framework.UpdateSuspender;
import com.scichart.data.model.DoubleRange;
import com.scichart.drawing.common.SolidBrushStyle;
import com.scichart.examples.R;
import com.scichart.examples.data.DataManager;
import com.scichart.examples.data.TradeData;
import com.scichart.examples.fragments.base.ExampleBaseFragment;
import com.scichart.examples.utils.SeekBarChangeListenerBase;
import com.scichart.examples.utils.ViewSettingsUtil;
import com.scichart.examples.utils.widgetgeneration.ImageViewWidget;
import com.scichart.examples.utils.widgetgeneration.Widget;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import butterknife.Bind;

public class BubbleChartFragment extends ExampleBaseFragment {

    @Bind(R.id.chart)
    SciChartSurface surface;

    private final int minSeekBarValue = 5;
    private int zScaleFactor = 30;

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
        final IAxis xAxis = sciChartBuilder.newDateAxis().withGrowBy(0d, 0.1d).build();
        final IAxis yAxis = sciChartBuilder.newNumericAxis().withGrowBy(0d, 0.1d).build();

        final IXyzDataSeries<Date, Double, Double> dataSeries = sciChartBuilder.newXyzDataSeries(Date.class, Double.class, Double.class).build();

        List<TradeData> tradeTicks = DataManager.getInstance().getTradeTicks(getActivity());
        for (int i = 0; i < tradeTicks.size(); i++) {
            TradeData tradeData = tradeTicks.get(i);
            dataSeries.append(tradeData.getTradeDate(), tradeData.getTradePrice(), tradeData.getTradeSize());
        }

        final FastLineRenderableSeries lineSeries = sciChartBuilder.newLineSeries().withDataSeries(dataSeries).withStrokeStyle(0xffff3333, 2f).build();

        final FastBubbleRenderableSeries bubbleSeries = sciChartBuilder.newBubbleSeries()
                .withDataSeries(dataSeries)
                .withZScaleFactor(zScaleFactor / 10f)
                .withBubbleBrushStyle(new SolidBrushStyle(0x77CCCCCC))
                .withStrokeStyle(0xFFCCCCCC, 2f, true)
                .withAutoZRange(false)
                .build();

        UpdateSuspender.using(surface, new Runnable() {
            @Override
            public void run() {
                Collections.addAll(surface.getXAxes(), xAxis);
                Collections.addAll(surface.getYAxes(), yAxis);
                Collections.addAll(surface.getRenderableSeries(), lineSeries, bubbleSeries);
                Collections.addAll(surface.getChartModifiers(), new RubberBandXyZoomModifier());
                Collections.addAll(surface.getChartModifiers(), new ZoomExtentsModifier());
            }
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
        UpdateSuspender.using(surface, new Runnable() {
            @Override
            public void run() {
                FastBubbleRenderableSeries rSeries = (FastBubbleRenderableSeries) surface.getRenderableSeries().get(1);
                rSeries.setZScaleFactor(zScaleFactor / 10f);
            }
        });
    }
}