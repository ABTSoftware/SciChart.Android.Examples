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
import android.view.animation.DecelerateInterpolator;
import android.widget.SeekBar;

import androidx.annotation.NonNull;

import com.scichart.charting.model.dataSeries.IXyzDataSeries;
import com.scichart.charting.model.dataSeries.IXyzDataSeriesValues;
import com.scichart.charting.modifiers.RubberBandXyZoomModifier;
import com.scichart.charting.modifiers.ZoomExtentsModifier;
import com.scichart.charting.visuals.SciChartSurface;
import com.scichart.charting.visuals.axes.IAxis;
import com.scichart.charting.visuals.renderableSeries.FastBubbleRenderableSeries;
import com.scichart.charting.visuals.renderableSeries.FastLineRenderableSeries;
import com.scichart.charting.visuals.renderableSeries.SplineLineRenderableSeries;
import com.scichart.charting.visuals.renderableSeries.XyRenderableSeriesBase;
import com.scichart.charting.visuals.renderableSeries.XyzRenderableSeriesBase;
import com.scichart.charting.visuals.renderableSeries.data.XyRenderPassData;
import com.scichart.charting.visuals.renderableSeries.data.XyzRenderPassData;
import com.scichart.charting.visuals.renderableSeries.paletteProviders.IFillPaletteProvider;
import com.scichart.charting.visuals.renderableSeries.paletteProviders.IPointMarkerPaletteProvider;
import com.scichart.charting.visuals.renderableSeries.paletteProviders.PaletteProviderBase;
import com.scichart.core.IServiceContainer;
import com.scichart.core.framework.UpdateSuspender;
import com.scichart.core.model.DoubleValues;
import com.scichart.core.model.IntegerValues;
import com.scichart.drawing.common.SolidBrushStyle;
import com.scichart.drawing.utility.ColorUtil;
import com.scichart.examples.R;
import com.scichart.examples.data.DataManager;
import com.scichart.examples.data.TradeData;
import com.scichart.examples.fragments.base.ExampleSingleChartBaseFragment;
import com.scichart.examples.utils.Constant;
import com.scichart.examples.utils.SeekBarChangeListenerBase;
import com.scichart.examples.utils.ViewSettingsUtil;
import com.scichart.examples.utils.interpolator.DefaultInterpolator;
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
        final IAxis xAxis = sciChartBuilder.newNumericAxis().withGrowBy(0d, 0.1d).build();
        final IAxis yAxis = sciChartBuilder.newNumericAxis().withGrowBy(0d, 0.1d).build();

        final IXyzDataSeries<Double, Double, Double> dataSeries = sciChartBuilder.newXyzDataSeries(Double.class, Double.class, Double.class).build();

//        List<TradeData> tradeTicks = DataManager.getInstance().getTradeTicks(getActivity());
        int prevYValue = 0;
        for (int i = 0; i < 20; i++) {
            double curYValue = Math.sin(i) * 10 + 5;
            double size = Math.sin(i) * 60 + 3;

            dataSeries.append((double) i, prevYValue + curYValue, size);

            prevYValue += curYValue;
        }

        final SplineLineRenderableSeries lineSeries = sciChartBuilder.newSplineLineSeries().withDataSeries(dataSeries).withStrokeStyle(0xffE4F5FC, 2f).build();

        final FastBubbleRenderableSeries rSeries = sciChartBuilder.newBubbleSeries()
                .withDataSeries(dataSeries)
                .withAutoZRange(false)
                .withPaletteProvider(new BubblePaletteProvider(XyzRenderableSeriesBase.class))
                .build();

        UpdateSuspender.using(surface, () -> {
            Collections.addAll(surface.getXAxes(), xAxis);
            Collections.addAll(surface.getYAxes(), yAxis);
            Collections.addAll(surface.getRenderableSeries(), lineSeries, rSeries);
            Collections.addAll(surface.getChartModifiers(), new RubberBandXyZoomModifier(), new ZoomExtentsModifier());

            sciChartBuilder.newAnimator(lineSeries).withSweepTransformation().withInterpolator(DefaultInterpolator.getInterpolator()).withDuration(Constant.ANIMATION_DURATION).withStartDelay(Constant.ANIMATION_START_DELAY).start();
            sciChartBuilder.newAnimator(rSeries).withSweepTransformation().withInterpolator(DefaultInterpolator.getInterpolator()).withDuration(Constant.ANIMATION_DURATION).withStartDelay(Constant.ANIMATION_START_DELAY).start();
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

    class BubblePaletteProvider extends PaletteProviderBase<XyzRenderableSeriesBase> implements IPointMarkerPaletteProvider, IFillPaletteProvider {

        private IntegerValues colors = new IntegerValues();

        /**
         * Creates a new instance of {@link PaletteProviderBase} class
         *
         * @param renderableSeriesType The type of supported renderable series
         */
        protected BubblePaletteProvider(Class<XyzRenderableSeriesBase> renderableSeriesType) {
            super(renderableSeriesType);
        }


        @Override
        public void update() {
            final XyzRenderableSeriesBase renderableSeries = this.renderableSeries;
            final XyzRenderPassData currentRenderPassData = (XyzRenderPassData) renderableSeries.getCurrentRenderPassData();
            final DoubleValues xValues = currentRenderPassData.xValues;

            final int size = currentRenderPassData.pointsCount();
            colors.setSize(size);

            final int[] colorsArray = colors.getItemsArray();
            final double[] valuesArray = xValues.getItemsArray();

            for (int i = 0; i < size; i++) {
                final double value = valuesArray[i];
                if(value >= 9 && value <= 12){
                    colorsArray[i] = 0x87F48420;
                } else {
                    colorsArray[i] = 0x8750C7E0;
                }
            }
        }

        @Override
        public IntegerValues getPointMarkerColors() {
            return colors;
        }

        @Override
        public IntegerValues getFillColors() {
            return colors;
        }
    }
}