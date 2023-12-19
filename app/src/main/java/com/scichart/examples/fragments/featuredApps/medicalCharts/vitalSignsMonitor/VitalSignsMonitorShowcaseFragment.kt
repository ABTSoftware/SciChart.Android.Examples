//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2021. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// VitalSignsMonitorShowcaseFragment.kt is part of SCICHART®, High Performance Scientific Charts
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

package com.scichart.examples.fragments.featuredApps.medicalCharts.kt.vitalSignsMonitor

import android.view.LayoutInflater
import android.view.View
import androidx.core.content.ContextCompat
import com.scichart.charting.layoutManagers.ChartLayoutState
import com.scichart.charting.layoutManagers.DefaultLayoutManager
import com.scichart.charting.layoutManagers.VerticalAxisLayoutStrategy
import com.scichart.charting.model.dataSeries.IDataSeries
import com.scichart.charting.model.dataSeries.XyDataSeries
import com.scichart.charting.visuals.axes.AutoRange.Never
import com.scichart.charting.visuals.axes.NumericAxis
import com.scichart.charting.visuals.renderableSeries.FastLineRenderableSeries
import com.scichart.charting.visuals.renderableSeries.IRenderableSeries
import com.scichart.charting.visuals.renderableSeries.XyRenderableSeriesBase
import com.scichart.charting.visuals.renderableSeries.XyScatterRenderableSeries
import com.scichart.charting.visuals.renderableSeries.paletteProviders.IStrokePaletteProvider
import com.scichart.charting.visuals.renderableSeries.paletteProviders.PaletteProviderBase
import com.scichart.core.model.IntegerValues
import com.scichart.data.model.DoubleRange
import com.scichart.drawing.utility.ColorUtil
import com.scichart.examples.R
import com.scichart.examples.databinding.ExampleVitalSignsMonitorFragmentBinding
import com.scichart.examples.fragments.base.ShowcaseExampleBaseFragment
import com.scichart.examples.fragments.featuredApps.medicalCharts.vitalSignsMonitor.DefaultVitalSignsDataProvider
import com.scichart.examples.fragments.featuredApps.medicalCharts.vitalSignsMonitor.EcgDataBatch
import com.scichart.examples.fragments.featuredApps.medicalCharts.vitalSignsMonitor.VitalSignsData
import com.scichart.examples.fragments.featuredApps.medicalCharts.vitalSignsMonitor.VitalSignsIndicatorsProvider
import com.scichart.examples.utils.scichartExtensions.*
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.math.max
import kotlin.math.roundToInt

class VitalSignsMonitorShowcaseFragment : ShowcaseExampleBaseFragment<ExampleVitalSignsMonitorFragmentBinding>() {
    private val ecgDataSeries = newDataSeries(FIFO_CAPACITY)
    private val ecgSweepDataSeries = newDataSeries(FIFO_CAPACITY)
    private val bloodPressureDataSeries = newDataSeries(FIFO_CAPACITY)
    private val bloodPressureSweepDataSeries = newDataSeries(FIFO_CAPACITY)
    private val bloodVolumeDataSeries = newDataSeries(FIFO_CAPACITY)
    private val bloodVolumeSweepDataSeries = newDataSeries(FIFO_CAPACITY)
    private val bloodOxygenationDataSeries = newDataSeries(FIFO_CAPACITY)
    private val bloodOxygenationSweepDataSeries = newDataSeries(FIFO_CAPACITY)

    private val lastEcgSweepDataSeries = newDataSeries(1)
    private val lastBloodPressureDataSeries = newDataSeries(1)
    private val lastBloodVolumeDataSeries = newDataSeries(1)
    private val lastBloodOxygenationSweepDataSeries = newDataSeries(1)

    private val indicatorsProvider = VitalSignsIndicatorsProvider()

    private val dataBatch = EcgDataBatch()

    override fun inflateBinding(inflater: LayoutInflater): ExampleVitalSignsMonitorFragmentBinding {
        return ExampleVitalSignsMonitorFragmentBinding.inflate(inflater)
    }

    override fun initExample(binding: ExampleVitalSignsMonitorFragmentBinding) {
        val dataProvider = DefaultVitalSignsDataProvider(requireContext())

        setUpChart(dataProvider)

        dataProvider.data.buffer(50, TimeUnit.MILLISECONDS).doOnNext { ecgData: List<VitalSignsData> ->
            if (ecgData.isEmpty()) return@doOnNext

            dataBatch.updateData(ecgData)

            binding.surface.suspendUpdates {
                val xValues = dataBatch.xValues

                ecgDataSeries.append(xValues, dataBatch.ecgHeartRateValuesA)
                ecgSweepDataSeries.append(xValues, dataBatch.ecgHeartRateValuesB)

                bloodPressureDataSeries.append(xValues, dataBatch.bloodPressureValuesA)
                bloodPressureSweepDataSeries.append(xValues, dataBatch.bloodPressureValuesB)

                bloodOxygenationDataSeries.append(xValues, dataBatch.bloodOxygenationA)
                bloodOxygenationSweepDataSeries.append(xValues, dataBatch.bloodOxygenationB)

                bloodVolumeDataSeries.append(xValues, dataBatch.bloodVolumeValuesA)
                bloodVolumeSweepDataSeries.append(xValues, dataBatch.bloodVolumeValuesB)

                val lastVitalSignsData = dataBatch.lastVitalSignsData
                val xValue = lastVitalSignsData.xValue

                lastEcgSweepDataSeries.append(xValue, lastVitalSignsData.ecgHeartRate)
                lastBloodPressureDataSeries.append(xValue, lastVitalSignsData.bloodPressure)
                lastBloodOxygenationSweepDataSeries.append(xValue, lastVitalSignsData.bloodOxygenation)
                lastBloodVolumeDataSeries.append(xValue, lastVitalSignsData.bloodVolume)
            }
        }.compose(bindToLifecycle()).subscribe()

        updateIndicators(0)
        Observable.interval(0, 1, TimeUnit.SECONDS, AndroidSchedulers.mainThread())
            .doOnNext(::updateIndicators)
            .compose(bindToLifecycle()).subscribe()
    }

    private fun setUpChart(dataProvider: DefaultVitalSignsDataProvider) {
        val context = requireContext()
        val heartRateColor = ContextCompat.getColor(context, R.color.heart_rate_color)
        val bloodPressureColor = ContextCompat.getColor(context, R.color.blood_pressure_color)
        val bloodVolumeColor = ContextCompat.getColor(context, R.color.blood_volume_color)
        val bloodOxygenation = ContextCompat.getColor(context, R.color.blood_oxygenation_color)

        binding.surface.theme = R.style.SciChart_NavyBlue

        binding.surface.suspendUpdates {
            xAxes { numericAxis {
                visibleRange = DoubleRange(0.0, 10.0)
                autoRange = Never
                drawMinorGridLines = false
                drawMajorBands = false
                visibility = View.GONE
            }}
            yAxes {
                axis(generateYAxis(ECG_ID, dataProvider.ecgHeartRateRange))
                axis(generateYAxis(BLOOD_PRESSURE_ID, dataProvider.bloodPressureRange))
                axis(generateYAxis(BLOOD_VOLUME_ID, dataProvider.bloodVolumeRange))
                axis(generateYAxis(BLOOD_OXYGENATION_ID, dataProvider.bloodOxygenationRange))
            }

            renderableSeries {
                rSeries(generateLineSeries(ECG_ID, ecgDataSeries, heartRateColor))
                rSeries(generateLineSeries(ECG_ID, ecgSweepDataSeries, heartRateColor))
                rSeries(generateScatterForLastAppendedPoint(ECG_ID, lastEcgSweepDataSeries))

                rSeries(generateLineSeries(BLOOD_PRESSURE_ID, bloodPressureDataSeries, bloodPressureColor))
                rSeries(generateLineSeries(BLOOD_PRESSURE_ID, bloodPressureSweepDataSeries, bloodPressureColor))
                rSeries(generateScatterForLastAppendedPoint(BLOOD_PRESSURE_ID, lastBloodPressureDataSeries))

                rSeries(generateLineSeries(BLOOD_VOLUME_ID, bloodVolumeDataSeries, bloodVolumeColor))
                rSeries(generateLineSeries(BLOOD_VOLUME_ID, bloodVolumeSweepDataSeries, bloodVolumeColor))
                rSeries(generateScatterForLastAppendedPoint(BLOOD_VOLUME_ID, lastBloodVolumeDataSeries))

                rSeries(generateLineSeries(BLOOD_OXYGENATION_ID, bloodOxygenationDataSeries, bloodOxygenation))
                rSeries(generateLineSeries(BLOOD_OXYGENATION_ID, bloodOxygenationSweepDataSeries, bloodOxygenation))
                rSeries(generateScatterForLastAppendedPoint(BLOOD_OXYGENATION_ID, lastBloodOxygenationSweepDataSeries))
            }

            layoutManager = DefaultLayoutManager.Builder()
                .setRightOuterAxesLayoutStrategy(RightAlignedOuterVerticallyStackedYAxisLayoutStrategy())
                .build()
        }
    }

    private fun updateIndicators(time: Long) {
        binding.heartRateIndicator.heartIcon.visibility = if (time % 2 == 0L) View.VISIBLE else View.INVISIBLE

        if (time % 5 == 0L) {
            indicatorsProvider.update()
            binding.heartRateIndicator.bpmValueLabel.text = indicatorsProvider.bpmValue

            binding.bloodPressureIndicator.bloodPressureValue.text = indicatorsProvider.bpValue
            binding.bloodPressureIndicator.bloodPressureBar.progress = indicatorsProvider.bpbValue

            binding.bloodVolumeIndicator.bloodVolumeValueLabel.text = indicatorsProvider.bvValue
            binding.bloodVolumeIndicator.svBar1.progress = indicatorsProvider.bvBar1Value
            binding.bloodVolumeIndicator.svBar2.progress = indicatorsProvider.bvBar2Value

            binding.bloodOxygenationIndicator.spoValueLabel.text = indicatorsProvider.spoValue
            binding.bloodOxygenationIndicator.spoClockLabel.text = indicatorsProvider.spoClockValue
        }
    }

    private fun generateYAxis(id: String, visibleRange: DoubleRange): NumericAxis {
        return NumericAxis(requireContext()).apply {
            axisId = id
            visibility = View.GONE
            this.visibleRange = visibleRange
            autoRange = Never
            drawMajorBands = false
            drawMinorGridLines = false
            drawMajorGridLines = false
        }
    }

    private fun generateLineSeries(yAxisId: String, ds: IDataSeries<*, *>, color: Int): IRenderableSeries {
        return FastLineRenderableSeries().apply {
            dataSeries = ds
            this.yAxisId = yAxisId
            this.strokeStyle = SolidPenStyle(color)
            paletteProvider = DimTracePaletteProvider()
        }
    }

    private fun generateScatterForLastAppendedPoint(yAxisId: String, ds: IDataSeries<*, *>): IRenderableSeries {
        return XyScatterRenderableSeries().apply {
            dataSeries = ds
            this.yAxisId = yAxisId
            ellipsePointMarker {
                setSize(4)
                fillStyle = SolidBrushStyle(ColorUtil.White)
                strokeStyle = SolidPenStyle(ColorUtil.White)
            }
        }
    }

    private fun newDataSeries(fifoCapacity: Int): XyDataSeries<Double, Double> {
        return XyDataSeries<Double, Double>().apply {
            this.fifoCapacity = fifoCapacity
            acceptsUnsortedData = true
        }
    }

    private class RightAlignedOuterVerticallyStackedYAxisLayoutStrategy : VerticalAxisLayoutStrategy() {
        override fun measureAxes(availableWidth: Int, availableHeight: Int, chartLayoutState: ChartLayoutState) {
            for (i in 0 until axes.size) {
                val axis = axes[i]
                axis.updateAxisMeasurements()

                chartLayoutState.rightOuterAreaSize = max(getRequiredAxisSize(axis.axisLayoutState), chartLayoutState.rightOuterAreaSize)
            }
        }

        override fun layoutAxes(left: Int, top: Int, right: Int, bottom: Int) {
            val size = axes.size
            val height = bottom - top

            val axisHeight = height / size
            var topPlacement = top

            for (i in 0 until size) {
                val axis = axes[i]
                val axisLayoutState = axis.axisLayoutState

                val bottomPlacement = (topPlacement + axisHeight).toFloat().roundToInt()
                axis.layoutArea(left, topPlacement, left + getRequiredAxisSize(axisLayoutState), bottomPlacement)

                topPlacement = bottomPlacement
            }
        }
    }

    private class DimTracePaletteProvider : PaletteProviderBase<XyRenderableSeriesBase>(XyRenderableSeriesBase::class.java), IStrokePaletteProvider {
        private val colors = IntegerValues()

        private val startOpacity = 0.2
        private val diffOpacity = 1 - startOpacity

        override fun getStrokeColors(): IntegerValues = colors

        override fun update() {
            val defaultColor = renderableSeries!!.strokeStyle.color
            val size = renderableSeries!!.currentRenderPassData.pointsCount()

            colors.setSize(size)

            val colorsArray = colors.itemsArray
            for (i in 0 until size) {
                val faction = i / size.toDouble()
                val opacity = (startOpacity + faction * diffOpacity).toFloat()

                colorsArray[i] = ColorUtil.argb(defaultColor, opacity)
            }
        }
    }

    companion object {
        private const val FIFO_CAPACITY = 7850
        private const val ECG_ID = "ecgId"
        private const val BLOOD_PRESSURE_ID = "bloodPressureId"
        private const val BLOOD_VOLUME_ID = "bloodVolumeId"
        private const val BLOOD_OXYGENATION_ID = "bloodOxygenationId"
    }
}