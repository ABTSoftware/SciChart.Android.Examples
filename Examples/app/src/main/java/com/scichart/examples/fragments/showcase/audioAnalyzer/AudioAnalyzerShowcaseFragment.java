//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2020. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// AudioAnalyzerShowcaseFragment.java is part of the SCICHART® Examples. Permission is hereby granted
// to modify, create derivative works, distribute and publish any part of this source
// code whether for commercial, private or personal use.
//
// The SCICHART® examples are distributed in the hope that they will be useful, but
// without any warranty. It is provided "AS IS" without warranty of any kind, either
// expressed or implied.
//******************************************************************************

package com.scichart.examples.fragments.showcase.audioAnalyzer;

import android.util.Log;

import com.scichart.charting.model.dataSeries.UniformHeatmapDataSeries;
import com.scichart.charting.model.dataSeries.XyDataSeries;
import com.scichart.charting.numerics.coordinateCalculators.ICoordinateCalculator;
import com.scichart.charting.visuals.SciChartSurface;
import com.scichart.charting.visuals.axes.AutoRange;
import com.scichart.charting.visuals.axes.AxisAlignment;
import com.scichart.charting.visuals.axes.AxisTitleOrientation;
import com.scichart.charting.visuals.axes.AxisTitlePlacement;
import com.scichart.charting.visuals.axes.NumericAxis;
import com.scichart.charting.visuals.renderableSeries.ColorMap;
import com.scichart.charting.visuals.renderableSeries.FastColumnRenderableSeries;
import com.scichart.charting.visuals.renderableSeries.FastLineRenderableSeries;
import com.scichart.charting.visuals.renderableSeries.FastUniformHeatmapRenderableSeries;
import com.scichart.charting.visuals.renderableSeries.data.ISeriesRenderPassData;
import com.scichart.charting.visuals.renderableSeries.data.XyRenderPassData;
import com.scichart.charting.visuals.renderableSeries.paletteProviders.IFillPaletteProvider;
import com.scichart.charting.visuals.renderableSeries.paletteProviders.IStrokePaletteProvider;
import com.scichart.charting.visuals.renderableSeries.paletteProviders.PaletteProviderBase;
import com.scichart.core.framework.UpdateSuspender;
import com.scichart.core.model.DoubleValues;
import com.scichart.core.model.IntegerValues;
import com.scichart.core.utility.NumberUtil;
import com.scichart.examples.R;
import com.scichart.examples.data.Radix2FFT;
import com.scichart.examples.fragments.base.ShowcaseExampleBaseFragment;

import butterknife.BindView;

import static com.scichart.drawing.utility.ColorUtil.*;

public class AudioAnalyzerShowcaseFragment extends ShowcaseExampleBaseFragment {

    private static final int AUDIO_STREAM_BUFFER_SIZE = 500000;
    private static final int MAX_FREQUENCY = 10000;

    @Override
    protected int getLayoutId() {
        return R.layout.example_audio_analyzer_fragment;
    }

    @BindView(R.id.audioStreamChart)
    SciChartSurface audioStreamChart;

    @BindView(R.id.fftChart)
    SciChartSurface fftChart;

    @BindView(R.id.spectrogramChart)
    SciChartSurface spectrogramChart;

    private final IAudioAnalyzerDataProvider dataProvider = createDateProvider();

    private final int bufferSize = dataProvider.getBufferSize();
    private final int sampleRate = dataProvider.getSampleRate();

    private final Radix2FFT fft = new Radix2FFT(bufferSize);

    private final double hzPerDataPoint = (double)sampleRate / bufferSize;
    private final int fftSize = (int)(MAX_FREQUENCY / hzPerDataPoint);
    private final int fftCount = AUDIO_STREAM_BUFFER_SIZE / bufferSize;
    private final int fftValuesCount = fftSize * fftCount;
    private final int fftOffsetValueCount = fftValuesCount - fftSize;

    private final DoubleValues fftData = new DoubleValues();
    private final DoubleValues spectrogramValues = new DoubleValues(fftValuesCount);

    private final XyDataSeries<Long, Short> audioDS = new XyDataSeries<>(Long.class, Short.class);
    private final XyDataSeries<Double, Double> fftDS = new XyDataSeries<>(Double.class, Double.class);
    private final UniformHeatmapDataSeries<Long, Long, Double> spectrogramDS = new UniformHeatmapDataSeries<>(Long.class, Long.class, Double.class, fftSize, fftCount);

    @Override
    protected void initExample() {
        initAudioStreamChart();
        initFFTChart();
        initSpectrogramChart();

        dataProvider.getData().doOnNext(audioData -> {
            audioDS.append(audioData.xData, audioData.yData);

            fft.run(audioData.yData, fftData);
            fftData.setSize(fftSize);

            fftDS.updateRangeYAt(0, fftData);

            final double[] spectrogramItems = spectrogramValues.getItemsArray();
            final double[] fftItems = fftData.getItemsArray();

            System.arraycopy(spectrogramItems, fftSize, spectrogramItems, 0, fftOffsetValueCount);
            System.arraycopy(fftItems, 0, spectrogramItems, fftOffsetValueCount, fftSize);

            spectrogramDS.updateZValues(spectrogramValues);
        }).compose(bindToLifecycle()).subscribe();
    }

    private void initAudioStreamChart() {
        final NumericAxis xAxis = sciChartBuilder.newNumericAxis()
                .withAutoRangeMode(AutoRange.Always)
                .withDrawLabels(false)
                .withDrawMinorTicks(false)
                .withDrawMajorTicks(false)
                .withDrawMajorBands(false)
                .withDrawMinorGridLines(false)
                .withDrawMajorGridLines(false)
                .build();

        final NumericAxis yAxis = sciChartBuilder.newNumericAxis()
                .withVisibleRange(Short.MIN_VALUE, Short.MAX_VALUE)
                .withDrawLabels(false)
                .withDrawMinorTicks(false)
                .withDrawMajorTicks(false)
                .withDrawMajorBands(false)
                .withDrawMinorGridLines(false)
                .withDrawMajorGridLines(false)
                .build();

        audioDS.setFifoCapacity(AUDIO_STREAM_BUFFER_SIZE);

        final FastLineRenderableSeries rs = sciChartBuilder.newLineSeries().withDataSeries(audioDS)
                .withStrokeStyle(Grey, 1f)
                .build();

        UpdateSuspender.using(audioStreamChart, () -> {
            audioStreamChart.getXAxes().add(xAxis);
            audioStreamChart.getYAxes().add(yAxis);
            audioStreamChart.getRenderableSeries().add(rs);
        });
    }

    private void initFFTChart() {
        final NumericAxis xAxis = sciChartBuilder.newNumericAxis()
                .withDrawMajorBands(false)
                .withMaxAutoTicks(5)
                .withAxisTitle("Hz")
                .withAxisTitlePlacement(AxisTitlePlacement.Right)
                .withAxisTitleOrientation(AxisTitleOrientation.Horizontal)
                .build();

        final NumericAxis yAxis = sciChartBuilder.newNumericAxis()
                .withAxisAlignment(AxisAlignment.Left)
                .withVisibleRange(-30, 70)
                .withGrowBy(0.1, 0.1)
                .withDrawMinorTicks(false)
                .withDrawMinorGridLines(false)
                .withDrawMajorBands(false)
                .withAxisTitle("dB")
                .withAxisTitlePlacement(AxisTitlePlacement.Top)
                .withAxisTitleOrientation(AxisTitleOrientation.Horizontal)
                .build();

        fftDS.setFifoCapacity(fftSize);
        for (int i = 0; i < fftSize; i++) {
            fftDS.append(i * hzPerDataPoint, 0d);
        }

        final FastColumnRenderableSeries rs = sciChartBuilder.newColumnSeries()
                .withDataSeries(fftDS)
                .withPaletteProvider(new FFTPaletteProvider())
                .withZeroLine(-30) // set zero line equal to VisibleRange.Min
                .build();

        UpdateSuspender.using(fftChart, () -> {
            fftChart.getXAxes().add(xAxis);
            fftChart.getYAxes().add(yAxis);
            fftChart.getRenderableSeries().add(rs);
        });
    }

    private void initSpectrogramChart() {
        spectrogramValues.setSize(fftValuesCount);

        final NumericAxis xAxis = sciChartBuilder.newNumericAxis()
                .withAutoRangeMode(AutoRange.Always)
                .withDrawLabels(false)
                .withDrawMinorTicks(false)
                .withDrawMajorTicks(false)
                .withDrawMajorBands(false)
                .withDrawMinorGridLines(false)
                .withDrawMajorGridLines(false)
                .withAxisAlignment(AxisAlignment.Left)
                .withFlipCoordinates(true)
                .build();

        final NumericAxis yAxis = sciChartBuilder.newNumericAxis()
                .withAutoRangeMode(AutoRange.Always)
                .withDrawLabels(false)
                .withDrawMinorTicks(false)
                .withDrawMajorTicks(false)
                .withDrawMajorBands(false)
                .withDrawMinorGridLines(false)
                .withDrawMajorGridLines(false)
                .withAxisAlignment(AxisAlignment.Bottom)
                .withFlipCoordinates(true)
                .build();

        final FastUniformHeatmapRenderableSeries rs = sciChartBuilder.newUniformHeatmap()
                .withDataSeries(spectrogramDS)
                .withMinimum(-30)
                .withMaximum(70)
                .withColorMap(new ColorMap(
                        new int[]{Transparent, DarkBlue, Purple, Red, Yellow, White},
                        new float[]{0f, 0.0001f, 0.25f, 0.50f, 0.75f, 1f}
                )).build();

        UpdateSuspender.using(spectrogramChart, () -> {
            spectrogramChart.getXAxes().add(xAxis);
            spectrogramChart.getYAxes().add(yAxis);
            spectrogramChart.getRenderableSeries().add(rs);
        });
    }

    private static IAudioAnalyzerDataProvider createDateProvider(){
        try {
            return new DefaultAudioAnalyzerDataProvider();
        } catch (Exception ex) {
            Log.d("AudioAnalyzer", "Initialization of DefaultAudioAnalyzerDataProvider failed. Using stub implementation instead", ex);
            return new StubAudioAnalyzerDataProvider();
        }
    }

    private static class FFTPaletteProvider extends PaletteProviderBase<FastColumnRenderableSeries> implements IFillPaletteProvider, IStrokePaletteProvider {
        private final IntegerValues colors = new IntegerValues();

        private final int minColor = Green;
        private final int maxColor = Red;

        // RGB chanel values for min color
        private final int minColorRed = red(minColor);
        private final int minColorGreen = green(minColor);
        private final int minColorBlue = blue(minColor);

        // RGB chanel values for max color
        private final int maxColorRed = red(maxColor);
        private final int maxColorGreen = green(maxColor);
        private final int maxColorBlue = blue(maxColor);

        private final int diffRed = maxColorRed - minColorRed;
        private final int diffGreen = maxColorGreen - minColorGreen;
        private final int diffBlue = maxColorBlue - minColorBlue;

        FFTPaletteProvider() {
            super(FastColumnRenderableSeries.class);
        }

        @Override
        public IntegerValues getFillColors() {
            return colors;
        }

        @Override
        public IntegerValues getStrokeColors() {
            return colors;
        }

        @Override
        public void update() {
            final ISeriesRenderPassData currentRenderPassData = renderableSeries.getCurrentRenderPassData();
            final XyRenderPassData xyRenderPassData = (XyRenderPassData) currentRenderPassData;

            final ICoordinateCalculator yCalc = xyRenderPassData.getYCoordinateCalculator();
            final double min = yCalc.getMinAsDouble();
            final double max = yCalc.getMaxAsDouble();
            final double diff = max - min;

            final DoubleValues yValues = xyRenderPassData.yValues;
            final int size = xyRenderPassData.pointsCount();

            colors.setSize(size);

            final double[] yItems = yValues.getItemsArray();
            final int[] colorItems = colors.getItemsArray();

            for (int i = 0; i < size; i++) {
                final double yValue = yItems[i];
                final double fraction = (yValue - min) / diff;

                final int red = lerp(minColorRed, diffRed, fraction);
                final int green = lerp(minColorGreen, diffGreen, fraction);
                final int blue = lerp(minColorBlue, diffBlue, fraction);

                colorItems[i] = rgb(red, green, blue);
            }
        }

        private static int lerp(int minColor, int diffColor, double fraction) {
            final double intepolatedValue = minColor + fraction * diffColor;
            return (int) NumberUtil.constrain(intepolatedValue, 0, 255);
        }
    }
}
