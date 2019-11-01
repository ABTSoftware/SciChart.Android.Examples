//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2018. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// AnimationsTestFragment.java is part of the SCICHART® Examples. Permission is hereby granted
// to modify, create derivative works, distribute and publish any part of this source
// code whether for commercial, private or personal use.
//
// The SCICHART® examples are distributed in the hope that they will be useful, but
// without any warranty. It is provided "AS IS" without warranty of any kind, either
// expressed or implied.
//******************************************************************************

package com.scichart.examples.fragments.testing;

import android.animation.Animator;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;

import com.scichart.charting.model.dataSeries.HlDataSeries;
import com.scichart.charting.model.dataSeries.OhlcDataSeries;
import com.scichart.charting.model.dataSeries.XyDataSeries;
import com.scichart.charting.model.dataSeries.XyyDataSeries;
import com.scichart.charting.model.dataSeries.XyzDataSeries;
import com.scichart.charting.visuals.SciChartSurface;
import com.scichart.charting.visuals.axes.AutoRange;
import com.scichart.charting.visuals.axes.IAxis;
import com.scichart.charting.visuals.pointmarkers.EllipsePointMarker;
import com.scichart.charting.visuals.renderableSeries.FastColumnRenderableSeries;
import com.scichart.charting.visuals.renderableSeries.FastFixedErrorBarsRenderableSeries;
import com.scichart.charting.visuals.renderableSeries.FastLineRenderableSeries;
import com.scichart.charting.visuals.renderableSeries.FastMountainRenderableSeries;
import com.scichart.charting.visuals.renderableSeries.HlRenderableSeriesBase;
import com.scichart.charting.visuals.renderableSeries.IRenderableSeries;
import com.scichart.charting.visuals.renderableSeries.OhlcRenderableSeriesBase;
import com.scichart.charting.visuals.renderableSeries.XyRenderableSeriesBase;
import com.scichart.charting.visuals.renderableSeries.XyyRenderableSeriesBase;
import com.scichart.charting.visuals.renderableSeries.XyzRenderableSeriesBase;
import com.scichart.core.framework.UpdateSuspender;
import com.scichart.drawing.common.SolidPenStyle;
import com.scichart.drawing.utility.ColorUtil;
import com.scichart.examples.R;
import com.scichart.examples.components.SpinnerStringAdapter;
import com.scichart.examples.fragments.base.ExampleBaseFragment;
import com.scichart.examples.fragments.splineLineSeries.SplineLineRenderableSeries;
import com.scichart.examples.utils.ItemSelectedListenerBase;
import com.scichart.extensions.builders.AnimatorBuilderBase;

import java.util.Collections;
import java.util.Random;

import butterknife.BindView;

public class AnimationsTestFragment extends ExampleBaseFragment {
    private static final int COLUMN = 0;
    private static final int LINE = 1;
    private static final int SPLINE_LINE = 2;
    private static final int IMPULSE = 3;
    private static final int MOUNTAIN = 4;
    private static final int XY_SCATTER = 5;
    private static final int ERROR_BARS = 6;
    private static final int FIXED_ERROR_BARS = 7;
    private static final int BUBBLE = 8;
    private static final int BAND = 9;
    private static final int OHLC = 10;
    private static final int CANDLESTICK = 11;
    private static final int STACKED_COLUMN = 12;
    private static final int STACKED_MOUNTAIN = 13;

    private static final int POINTS_COUNT = 25;

    private final Random random = new Random();

    @BindView(R.id.scale)
    Button scale;
    @BindView(R.id.wave)
    Button wave;
    @BindView(R.id.sweep)
    Button sweep;
    @BindView(R.id.translateX)
    Button translateX;
    @BindView(R.id.translateY)
    Button translateY;

    @BindView(R.id.seriesSelector)
    Spinner seriesSelector;

    @BindView(R.id.chart)
    SciChartSurface surface;

    private Animator scaleAnimator;
    private Animator waveAnimator;
    private Animator sweepAnimator;
    private Animator translateXAnimator;
    private Animator translateYAnimator;

    @Override
    protected int getLayoutId() {
        return R.layout.example_animations_test_fragment;
    }

    @Override
    protected void initExample() {
        seriesSelector.setAdapter(new SpinnerStringAdapter(getActivity(), R.array.series_list));
        seriesSelector.setSelection(0);
        seriesSelector.setOnItemSelectedListener(new ItemSelectedListenerBase() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectSeries(position);
            }
        });

        final IAxis xAxis = sciChartBuilder.newNumericAxis().withGrowBy(0.1d, 0.1d).withAutoRangeMode(AutoRange.Always).build();
        final IAxis yAxis = sciChartBuilder.newNumericAxis().withGrowBy(0.1d, 0.1d).withAutoRangeMode(AutoRange.Always).build();

        UpdateSuspender.using(surface, new Runnable() {
            @Override
            public void run() {
                Collections.addAll(surface.getXAxes(), xAxis);
                Collections.addAll(surface.getYAxes(), yAxis);
                Collections.addAll(surface.getRenderableSeries(), initXySeries(sciChartBuilder.newColumnSeries().build()));
                Collections.addAll(surface.getChartModifiers(), sciChartBuilder.newModifierGroupWithDefaultModifiers().build());
            }
        });

        scale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scaleAnimator.cancel();
                scaleAnimator.start();
            }
        });

        wave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                waveAnimator.cancel();
                waveAnimator.start();
            }
        });

        sweep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sweepAnimator != null) {
                    sweepAnimator.cancel();
                    sweepAnimator.start();
                }
            }
        });

        translateX.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                translateXAnimator.cancel();
                translateXAnimator.start();
            }
        });

        translateY.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                translateYAnimator.cancel();
                translateYAnimator.start();
            }
        });
    }

    private void selectSeries(int position) {
        final IRenderableSeries rSeries;
        switch (position) {
            case COLUMN:
                rSeries = initXySeries(new FastColumnRenderableSeries());
                break;
            case LINE:
                rSeries = initXySeries(new FastLineRenderableSeries());
                break;
            case SPLINE_LINE:
                final SplineLineRenderableSeries series = new SplineLineRenderableSeries();
                series.setStrokeStyle(new SolidPenStyle(ColorUtil.LimeGreen, true, 2f, null));
                rSeries = initXySeries(series);
                break;
            case IMPULSE:
                rSeries = initXySeries(sciChartBuilder.newImpulseSeries()
                        .withPointMarker(sciChartBuilder.newPointMarker(new EllipsePointMarker())
                                .withSize(10, 10)
                                .withStroke(ColorUtil.SteelBlue, 1)
                                .withFill(ColorUtil.SteelBlue)
                                .build())
                        .withStrokeStyle(ColorUtil.SteelBlue, 1)
                        .build());
                break;
            case MOUNTAIN:
                rSeries = initXySeries(new FastMountainRenderableSeries());
                break;
            case XY_SCATTER:
                rSeries = initXySeries(sciChartBuilder.newScatterSeries()
                        .withPointMarker(sciChartBuilder.newPointMarker(new EllipsePointMarker())
                                .withSize(10, 10)
                                .withStroke(ColorUtil.SteelBlue, 1)
                                .withFill(ColorUtil.SteelBlue)
                                .build())
                        .build());
                break;
            case ERROR_BARS:
                rSeries = initHlSeries(sciChartBuilder.newErrorBarsSeries().withStrokeStyle(ColorUtil.SteelBlue).build());
                break;
            case FIXED_ERROR_BARS:
                rSeries = initFixedErrorBarsSeries(sciChartBuilder.newFixedErrorBarsSeries().withStrokeStyle(ColorUtil.SteelBlue).build());
                break;
            case BUBBLE:
                rSeries = initXyzSeries(sciChartBuilder.newBubbleSeries().withAutoZRange(false).withZScaleFactor(100).build());
                break;
            case BAND:
                rSeries = initXyySeries(sciChartBuilder.newBandSeries().build());
                break;
            case OHLC:
                rSeries = initOhlcSeries(sciChartBuilder.newOhlcSeries().build());
                break;
            case CANDLESTICK:
                rSeries = initOhlcSeries(sciChartBuilder.newCandlestickSeries().build());
                break;

            case STACKED_COLUMN:
            case STACKED_MOUNTAIN:
            default:
                rSeries = null;
        }

        UpdateSuspender.using(surface, new Runnable() {
            @Override
            public void run() {
                surface.getRenderableSeries().remove(0);
                surface.getRenderableSeries().add(rSeries);
            }
        });
    }

    private IRenderableSeries initXySeries(XyRenderableSeriesBase series) {
        final XyDataSeries<Double, Double> dataSeries = sciChartBuilder.newXyDataSeries(Double.class, Double.class).build();

        double[] randomWalk = getRandomWalk(0d);
        for (int i = 0; i < POINTS_COUNT; i++) {
            dataSeries.append((double) i, randomWalk[i]);
        }
        series.setDataSeries(dataSeries);

        initAnimators(sciChartBuilder.newAnimator(series));
        sweepAnimator = sciChartBuilder.newAnimator(series).withSweepTransformation().build();

        return series;
    }

    private IRenderableSeries initFixedErrorBarsSeries(FastFixedErrorBarsRenderableSeries series) {
        final XyDataSeries<Double, Double> dataSeries = sciChartBuilder.newXyDataSeries(Double.class, Double.class).build();

        double[] randomWalk = getRandomWalk(0d);
        for (int i = 0; i < POINTS_COUNT; i++) {
            dataSeries.append((double) i, randomWalk[i]);
        }
        series.setDataSeries(dataSeries);

        initAnimators(sciChartBuilder.newAnimator(series));
        sweepAnimator = sciChartBuilder.newAnimator(series).withSweepTransformation().build();

        return series;
    }

    private IRenderableSeries initXyySeries(XyyRenderableSeriesBase series) {
        final XyyDataSeries<Double, Double> dataSeries = sciChartBuilder.newXyyDataSeries(Double.class, Double.class).build();

        double[] randomWalkY = getRandomWalk(1d);
        double[] randomWalkY1 = getRandomWalk(-1d);
        for (int i = 0; i < POINTS_COUNT; i++) {
            dataSeries.append((double) i, randomWalkY[i], randomWalkY1[i]);
        }
        series.setDataSeries(dataSeries);

        initAnimators(sciChartBuilder.newAnimator(series));
        sweepAnimator = sciChartBuilder.newAnimator(series).withSweepTransformation().build();

        return series;
    }

    private IRenderableSeries initXyzSeries(XyzRenderableSeriesBase series) {
        final XyzDataSeries<Double, Double, Double> dataSeries = sciChartBuilder.newXyzDataSeries(Double.class, Double.class, Double.class).build();

        double[] randomWalkY = getRandomWalk(0d);
        double[] randomWalkZ = getRandomWalk(0d);
        for (int i = 0; i < POINTS_COUNT; i++) {
            dataSeries.append((double) i, randomWalkY[i], randomWalkZ[i]);
        }
        series.setDataSeries(dataSeries);

        initAnimators(sciChartBuilder.newAnimator(series));
        sweepAnimator = sciChartBuilder.newAnimator(series).withSweepTransformation().build();

        return series;
    }

    private IRenderableSeries initHlSeries(HlRenderableSeriesBase series) {
        final HlDataSeries<Double, Double> dataSeries = new HlDataSeries<>(Double.class, Double.class);

        double[] randomWalkY = getRandomWalk(0d);
        for (int i = 0; i < POINTS_COUNT; i++) {
            dataSeries.append((double) i, randomWalkY[i], randomWalkY[i] + random.nextDouble() - 0.5, randomWalkY[i] + random.nextDouble() + 0.5);
        }
        series.setDataSeries(dataSeries);

        initAnimators(sciChartBuilder.newAnimator(series));
        sweepAnimator = null;

        return series;
    }

    private IRenderableSeries initOhlcSeries(OhlcRenderableSeriesBase series) {
        final OhlcDataSeries<Double, Double> dataSeries = new OhlcDataSeries<>(Double.class, Double.class);

        double[] randomWalkY = getRandomWalk(0d);
        for (int i = 0; i < POINTS_COUNT; i++) {
            dataSeries.append((double) i,
                    randomWalkY[i] + random.nextDouble(),
                    randomWalkY[i] + random.nextDouble() + 0.5,
                    randomWalkY[i] + random.nextDouble() - 0.5,
                    randomWalkY[i] + random.nextDouble());
        }
        series.setDataSeries(dataSeries);

        initAnimators(sciChartBuilder.newAnimator(series));
        sweepAnimator = null;

        return series;
    }

    private void initAnimators(AnimatorBuilderBase.RenderPassDataAnimatorBuilder<?> builder) {
        scaleAnimator = builder.withScaleTransformation().build();
        waveAnimator = builder.withWaveTransformation().build();
        translateXAnimator = builder.withTranslateXTransformation(-1000).build();
        translateYAnimator = builder.withTranslateYTransformation(-1000).build();
    }

    private double[] getRandomWalk(double valueShift) {
        double randomWalk = 1d;
        final double[] yBuffer = new double[POINTS_COUNT];

        for (int i = 0; i < POINTS_COUNT; i++) {
            randomWalk += (random.nextDouble() - 0.498);
            yBuffer[i] = randomWalk + valueShift;
        }

        return yBuffer;
    }
}