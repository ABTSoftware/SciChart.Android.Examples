//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2021. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// AudioAnalyzerShowcaseFragment.kt is part of SCICHART®, High Performance Scientific Charts
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

package com.scichart.examples.fragments.featuredApps.scientificCharts.kt.audioAnalyzer

import android.util.Log
import android.view.LayoutInflater
import com.scichart.charting.visuals.SciChartSurface
import com.scichart.charting.visuals.axes.AutoRange.Always
import com.scichart.charting.visuals.axes.AxisAlignment.Bottom
import com.scichart.charting.visuals.axes.AxisAlignment.Left
import com.scichart.charting.visuals.axes.AxisTitleOrientation.Horizontal
import com.scichart.charting.visuals.axes.AxisTitlePlacement.Right
import com.scichart.charting.visuals.axes.AxisTitlePlacement.Top
import com.scichart.charting.visuals.axes.ScientificNotation
import com.scichart.charting.visuals.renderableSeries.ColorMap
import com.scichart.charting.visuals.renderableSeries.FastColumnRenderableSeries
import com.scichart.charting.visuals.renderableSeries.data.XyRenderPassData
import com.scichart.charting.visuals.renderableSeries.paletteProviders.IFillPaletteProvider
import com.scichart.charting.visuals.renderableSeries.paletteProviders.IStrokePaletteProvider
import com.scichart.charting.visuals.renderableSeries.paletteProviders.PaletteProviderBase
import com.scichart.core.model.DoubleValues
import com.scichart.core.model.IntegerValues
import com.scichart.core.utility.NumberUtil
import com.scichart.data.model.DoubleRange
import com.scichart.drawing.utility.ColorUtil.*
import com.scichart.examples.R
import com.scichart.examples.data.Radix2FFT
import com.scichart.examples.databinding.ExampleAudioAnalyzerFragmentBinding
import com.scichart.examples.fragments.base.ShowcaseExampleBaseFragment
import com.scichart.examples.fragments.featuredApps.scientificCharts.audioAnalyzer.AudioData
import com.scichart.examples.fragments.featuredApps.scientificCharts.audioAnalyzer.DefaultAudioAnalyzerDataProvider
import com.scichart.examples.fragments.featuredApps.scientificCharts.audioAnalyzer.IAudioAnalyzerDataProvider
import com.scichart.examples.fragments.featuredApps.scientificCharts.audioAnalyzer.StubAudioAnalyzerDataProvider
import com.scichart.examples.utils.scichartExtensions.*

class AudioAnalyzerShowcaseFragment : ShowcaseExampleBaseFragment<ExampleAudioAnalyzerFragmentBinding>() {
    private val fftData = DoubleValues()
    private val spectrogramValues = DoubleValues(fftValuesCount)

    private val audioDS = XyDataSeries<Long, Short>().apply { fifoCapacity = AUDIO_STREAM_BUFFER_SIZE }
    private val historyDS = XyDataSeries<Long, Short>().apply { fifoCapacity = AUDIO_STREAM_BUFFER_SIZE * 200 }
    private val fftDS = XyDataSeries<Double, Double>().apply { fifoCapacity = fftSize }
    private val spectrogramDS = UniformHeatmapDataSeries<Long, Long, Double>(fftSize, fftCount)

    override fun inflateBinding(inflater: LayoutInflater): ExampleAudioAnalyzerFragmentBinding {
        return ExampleAudioAnalyzerFragmentBinding.inflate(inflater)
    }

    override fun initExample(binding: ExampleAudioAnalyzerFragmentBinding) {
        binding.audioStreamChart.theme = R.style.SciChart_NavyBlue;
        binding.fftChart.theme = R.style.SciChart_NavyBlue;
        binding.spectrogramChart.theme = R.style.SciChart_NavyBlue;

        initAudioStreamChart(binding.audioStreamChart)
        initFFTChart(binding.fftChart)
        initSpectrogramChart(binding.spectrogramChart)

        dataProvider.data.doOnNext { audioData: AudioData ->
            audioDS.append(audioData.xData, audioData.yData)
            historyDS.append(audioData.xData, audioData.yData)

            fft.run(audioData.yData, fftData)
            fftData.setSize(fftSize)
            fftDS.updateRangeYAt(0, fftData)

            val spectrogramItems: DoubleArray = spectrogramValues.itemsArray
            val fftItems: DoubleArray = fftData.itemsArray

            System.arraycopy(spectrogramItems, fftSize, spectrogramItems, 0, fftOffsetValueCount)
            System.arraycopy(fftItems, 0, spectrogramItems, fftOffsetValueCount, fftSize)

            spectrogramDS.updateZValues(spectrogramValues)
        }.compose(bindToLifecycle()).subscribe()
    }

    private fun initAudioStreamChart(surface: SciChartSurface) {
        surface.suspendUpdates {
            xAxes {
                numericAxis {
                    axisId = "audio"
                    autoRange = Always
                    drawLabels = false
                    drawMajorBands = false
                    drawMinorTicks = false
                    drawMajorTicks = false
                    drawMinorGridLines = false
                    drawMajorGridLines = false
                }
                numericAxis {
                    axisId = "history"
                    autoRange = Always
                    drawLabels = false
                    drawMajorBands = false
                    drawMinorTicks = false
                    drawMajorTicks = false
                    drawMinorGridLines = false
                    drawMajorGridLines = false
                }

            }
            yAxes { numericAxis {
                visibleRange = DoubleRange(-2048.0,2048.0)
//                visibleRange = DoubleRange(Short.MIN_VALUE.toDouble(), Short.MAX_VALUE.toDouble())
                drawLabels = false
                drawMajorBands = false
                drawMinorTicks = false
                drawMajorTicks = false
                drawMinorGridLines = false
                drawMajorGridLines = false
            }}
            renderableSeries {
                fastLineRenderableSeries {
                    xAxisId = "history"
                    dataSeries = historyDS
                    strokeStyle = SolidPenStyle(0xFF1B89AA)
                }
                splineLineRenderableSeries {
                    xAxisId = "audio"
                    dataSeries = audioDS
                    strokeStyle = SolidPenStyle(0xFF4FBEE6)
                }
            }
        }
    }

    private fun initFFTChart(surface: SciChartSurface) {
        surface.suspendUpdates {
            xAxes { logarithmicAxis {
                logarithmicBase = 10.0
                textFormatting = "#"
                scientificNotation = ScientificNotation.None
                drawMajorBands = false
                maxAutoTicks = 4
                axisTitle = "Hz"
                axisTitlePlacement = Right
                axisTitleOrientation = Horizontal
            }}
            yAxes { numericAxis {
                axisAlignment = Left
                visibleRange = DoubleRange(-30.0, 70.0)
                growBy = DoubleRange(0.1, 0.1)
                drawMinorTicks = false
                drawMinorGridLines = false
                drawMajorBands = false
                axisTitle = "dB"
                axisTitlePlacement = Top
                axisTitleOrientation = Horizontal
            }}
            renderableSeries {
                fastMountainRenderableSeries {
                    dataSeries = fftDS.apply {
                        for (i in 0 until fftSize) {
                            append((i+1) * hzPerDataPoint, 0.0)
                        }
                    }
                    strokeStyle = SolidPenStyle(0xFF36B8E6, 1f)
                    zeroLineY = -30.0
                }
            }
        }
    }

    private fun initSpectrogramChart(surface: SciChartSurface) {
        spectrogramValues.setSize(fftOffsetValueCount)

        surface.suspendUpdates {
            xAxes { numericAxis {
                autoRange = Always
                drawLabels = false
                drawMajorBands = false
                drawMinorTicks = false
                drawMajorTicks = false
                drawMinorGridLines = false
                drawMajorGridLines = false
                axisAlignment = Left
                flipCoordinates = true
            }}
            yAxes { numericAxis {
                autoRange = Always
                drawLabels = false
                drawMajorBands = false
                drawMinorTicks = false
                drawMajorTicks = false
                drawMinorGridLines = false
                drawMajorGridLines = false
                axisAlignment = Bottom
                flipCoordinates = true
            }}
            renderableSeries {
                fastUniformHeatmapRenderableSeries {
                    dataSeries = spectrogramDS
                    minimum = -30.0
                    maximum = 70.0
                    colorMap = ColorMap(
                        intArrayOf(0xFF000000.toInt(),
                            0xFF000000.toInt(),
                            0xFF800080.toInt(), 0xFFFF0000.toInt(), 0xFFFFFF00.toInt(), 0xFFFFFFFF.toInt()
                        ),
                        floatArrayOf(0f, 0.0001f, 0.25f, 0.50f, 0.75f, 1f)
                    )
                }
            }
        }
    }

    private class FFTPaletteProvider : PaletteProviderBase<FastColumnRenderableSeries>(FastColumnRenderableSeries::class.java), IFillPaletteProvider, IStrokePaletteProvider {
        private val colors = IntegerValues()
        private val minColor = Green
        private val maxColor = Red

        // RGB channel values for min color
        private val minColorRed = red(minColor)
        private val minColorGreen = green(minColor)
        private val minColorBlue = blue(minColor)

        // RGB channel values for max color
        private val maxColorRed = red(maxColor)
        private val maxColorGreen = green(maxColor)
        private val maxColorBlue = blue(maxColor)

        private val diffRed = maxColorRed - minColorRed
        private val diffGreen = maxColorGreen - minColorGreen
        private val diffBlue = maxColorBlue - minColorBlue

        override fun getFillColors(): IntegerValues = colors
        override fun getStrokeColors(): IntegerValues = colors

        override fun update() {
            val currentRenderPassData = renderableSeries!!.currentRenderPassData
            val xyRenderPassData = currentRenderPassData as XyRenderPassData

            val yCalc = xyRenderPassData.yCoordinateCalculator
            val min = yCalc.minAsDouble
            val max = yCalc.maxAsDouble
            val diff = max - min

            val yValues = xyRenderPassData.yValues
            val size = xyRenderPassData.pointsCount()

            colors.setSize(size)

            val yItems = yValues.itemsArray
            val colorItems = colors.itemsArray
            for (i in 0 until size) {
                val yValue = yItems[i]
                val fraction = (yValue - min) / diff

                val red = lerp(minColorRed, diffRed, fraction)
                val green = lerp(minColorGreen, diffGreen, fraction)
                val blue = lerp(minColorBlue, diffBlue, fraction)

                colorItems[i] = rgb(red, green, blue)
            }
        }

        companion object {
            private fun lerp(minColor: Int, diffColor: Int, fraction: Double): Int {
                val intepolatedValue = minColor + fraction * diffColor
                return NumberUtil.constrain(intepolatedValue, 0.0, 255.0).toInt()
            }
        }
    }

    companion object {
        private const val HISTORY_AUDIO_STREAM_BUFFER_SIZE = 500000
        private const val AUDIO_STREAM_BUFFER_SIZE = 2048
        private const val MAX_FREQUENCY = 10000

        private val dataProvider = createDateProvider()
        private val bufferSize = dataProvider.bufferSize
        private val sampleRate = dataProvider.sampleRate

        private val hzPerDataPoint = sampleRate.toDouble() / bufferSize
        private val fftSize = (MAX_FREQUENCY / hzPerDataPoint).toInt()
        private val fftCount = HISTORY_AUDIO_STREAM_BUFFER_SIZE / bufferSize
        private val fftValuesCount = fftSize * fftCount
        private val fftOffsetValueCount = fftValuesCount - fftSize

        private val fft = Radix2FFT(bufferSize)

        private fun createDateProvider(): IAudioAnalyzerDataProvider {
            return try {
                DefaultAudioAnalyzerDataProvider()
            } catch (ex: Exception) {
                Log.d("AudioAnalyzer", "Initialization of DefaultAudioAnalyzerDataProvider failed. Using stub implementation instead", ex)
                StubAudioAnalyzerDataProvider()
            }
        }
    }
}