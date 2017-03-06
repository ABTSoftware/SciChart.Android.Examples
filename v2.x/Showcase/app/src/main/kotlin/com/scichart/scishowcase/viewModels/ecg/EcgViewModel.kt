package com.scichart.scishowcase.viewModels.ecg

import android.content.Context
import android.support.v4.content.ContextCompat
import android.view.View
import com.scichart.charting.layoutManagers.ChartLayoutState
import com.scichart.charting.layoutManagers.IAxisLayoutStrategy
import com.scichart.charting.layoutManagers.VerticalAxisLayoutStrategy
import com.scichart.charting.model.AxisCollection
import com.scichart.charting.model.RenderableSeriesCollection
import com.scichart.charting.model.dataSeries.IDataSeries
import com.scichart.charting.visuals.axes.AutoRange
import com.scichart.charting.visuals.axes.AxisAlignment
import com.scichart.charting.visuals.axes.NumericAxis
import com.scichart.charting.visuals.renderableSeries.FastLineRenderableSeries
import com.scichart.charting.visuals.renderableSeries.XyRenderableSeriesBase
import com.scichart.charting.visuals.renderableSeries.paletteProviders.IStrokePaletteProvider
import com.scichart.charting.visuals.renderableSeries.paletteProviders.PaletteProviderBase
import com.scichart.core.framework.ISuspendable
import com.scichart.core.framework.UpdateSuspender
import com.scichart.core.model.DoubleValues
import com.scichart.core.model.IntegerValues
import com.scichart.data.model.DoubleRange
import com.scichart.drawing.common.SolidPenStyle
import com.scichart.drawing.utility.ColorUtil
import com.scichart.scishowcase.R
import com.scichart.scishowcase.model.ecg.EcgData
import com.scichart.scishowcase.model.ecg.IEcgDataProvider
import com.scichart.scishowcase.model.ecg.TraceAOrB
import com.scichart.scishowcase.utils.XyDataSeries
import com.scichart.scishowcase.utils.dip
import com.scichart.scishowcase.utils.init
import com.scichart.scishowcase.viewModels.FragmentViewModelBase
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class EcgViewModel(context: Context, private val dataProvider: IEcgDataProvider, private val suspendable: ISuspendable) : FragmentViewModelBase(context) {

    private val FIFO_CAPACITY = 7850

    private val heartRateColor = ContextCompat.getColor(context, R.color.heart_rate_color)
    private val bloodPressureColor = ContextCompat.getColor(context, R.color.blood_pressure_color)
    private val bloodVolumeColor = ContextCompat.getColor(context, R.color.blood_volume_color)
    private val bloodOxygenation = ContextCompat.getColor(context, R.color.blood_oxygenation_color)

    val xAxes: AxisCollection = AxisCollection()
    val yAxes: AxisCollection = AxisCollection()
    val yAxisLayoutStrategy: IAxisLayoutStrategy = LeftAlignedOuterVerticallyStackedYAxisLayoutStrategy()
    val renderableSeries: RenderableSeriesCollection = RenderableSeriesCollection()

    val hrVM = EcgHeartRateViewModel()
    val bloodPressureVM = EcgBloodPressureViewModel()
    val bloodVolumeVM = EcgBloodVolumeViewModel()
    val bloodOxygenationVM = EcgBloodOxygenationViewModel()

    private val random = Random()
    private val hrValues = arrayOf("67", "69", "72", "74")
    private val bloodPressureValues = arrayOf("120/70", "115/70", "115/75", "120/75")
    private val bloodPresssureBarValues = arrayOf(5, 6, 7)
    private val bloodVolumeValues = arrayOf("13.1", "13.2", "13.3", "13.0")
    private val bloodVolumeBarValues = arrayOf(9, 10, 11)
    private val bloodOxygenationValues = arrayOf("93", "95", "96", "97")

    private val ecgDataSeries = XyDataSeries<Double, Double>().init { fifoCapacity = FIFO_CAPACITY }
    private val ecgSweepDataSeries = XyDataSeries<Double, Double>().init { fifoCapacity = FIFO_CAPACITY }
    private val bloodPressureDataSeries = XyDataSeries<Double, Double>().init { fifoCapacity = FIFO_CAPACITY }
    private val bloodPressureSweepDataSeries = XyDataSeries<Double, Double>().init { fifoCapacity = FIFO_CAPACITY }
    private val bloodVolumeDataSeries = XyDataSeries<Double, Double>().init { fifoCapacity = FIFO_CAPACITY }
    private val bloodVolumeSweepDataSeries = XyDataSeries<Double, Double>().init { fifoCapacity = FIFO_CAPACITY }
    private val bloodOxygenationDataSeries = XyDataSeries<Double, Double>().init { fifoCapacity = FIFO_CAPACITY }
    private val bloodOxygenationSweepDataSeries = XyDataSeries<Double, Double>().init { fifoCapacity = FIFO_CAPACITY }

    private val dataBatch = EcgDataBatch()
    private var dataSeriesSubscription: Disposable? = null
    private var labelsUpdateSubscription: Disposable? = null
    private var heartRateSubscription: Disposable? = null
    private val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

    init {
        xAxes.add(NumericAxis(context).init {
            visibleRange = DoubleRange(0.0, 10.0)
            autoRange = AutoRange.Never
            drawMinorGridLines = false
            drawMajorBands = false
            visibility = View.GONE
        })

        yAxes.add(generateAxis(context, "ecgId"))
        yAxes.add(generateAxis(context, "bloodPressureId"))
        yAxes.add(generateAxis(context, "bloodVolumeId"))
        yAxes.add(generateAxis(context, "bloodOxygenationId"))

        val lineThickness = context.dip(1f)

        renderableSeries.add(generateRenderableSeries("ecgId", ecgDataSeries, SolidPenStyle(heartRateColor, true, lineThickness, null)))
        renderableSeries.add(generateRenderableSeries("ecgId", ecgSweepDataSeries, SolidPenStyle(heartRateColor, true, lineThickness, null)))
        renderableSeries.add(generateRenderableSeries("bloodPressureId", bloodPressureDataSeries, SolidPenStyle(bloodPressureColor, true, lineThickness, null)))
        renderableSeries.add(generateRenderableSeries("bloodPressureId", bloodPressureSweepDataSeries, SolidPenStyle(bloodPressureColor, true, lineThickness, null)))
        renderableSeries.add(generateRenderableSeries("bloodVolumeId", bloodVolumeDataSeries, SolidPenStyle(bloodVolumeColor, true, lineThickness, null)))
        renderableSeries.add(generateRenderableSeries("bloodVolumeId", bloodVolumeSweepDataSeries, SolidPenStyle(bloodVolumeColor, true, lineThickness, null)))
        renderableSeries.add(generateRenderableSeries("bloodOxygenationId", bloodOxygenationDataSeries, SolidPenStyle(bloodOxygenation, true, lineThickness, null)))
        renderableSeries.add(generateRenderableSeries("bloodOxygenationId", bloodOxygenationSweepDataSeries, SolidPenStyle(bloodOxygenation, true, lineThickness, null)))
    }

    override fun onResume() {
        super.onResume()

        dataProvider.start()
        dataSeriesSubscription = dataProvider.getEcgData().buffer(50, TimeUnit.MILLISECONDS).doOnNext {
            dataBatch.updateData(it)

            UpdateSuspender.using(suspendable, {
                val xValues = dataBatch.xValues

                ecgDataSeries.append(xValues, dataBatch.ecgHeartRateValuesA)
                ecgSweepDataSeries.append(xValues, dataBatch.ecgHeartRateValuesB)

                bloodPressureDataSeries.append(xValues, dataBatch.bloodPressureValuesA)
                bloodPressureSweepDataSeries.append(xValues, dataBatch.bloodPressureValuesB)

                bloodVolumeDataSeries.append(xValues, dataBatch.bloodVolumeValuesA)
                bloodVolumeSweepDataSeries.append(xValues, dataBatch.bloodVolumeValuesB)

                bloodOxygenationDataSeries.append(xValues, dataBatch.bloodOxygenationA)
                bloodOxygenationSweepDataSeries.append(xValues, dataBatch.bloodOxygenationB)
            })
        }.subscribe()

        labelsUpdateSubscription = Observable.interval(0, 5, TimeUnit.SECONDS, AndroidSchedulers.mainThread())
                .doOnNext { updateVMValues() }
                .subscribe()

        heartRateSubscription = Observable.interval(0, 1, TimeUnit.SECONDS, AndroidSchedulers.mainThread()).doOnNext {
            val isVisible = hrVM.heartRateIconVisibility.get() == View.VISIBLE
            val visibility = if (isVisible) View.INVISIBLE else View.VISIBLE
            hrVM.heartRateIconVisibility.set(visibility)
        }.subscribe()
    }


    private fun generateAxis(context: Context, id: String): NumericAxis {
        return NumericAxis(context).init {
            axisId = id
            autoRange = AutoRange.Always
            growBy = DoubleRange(.05, .05)
            axisAlignment = AxisAlignment.Left
            drawMajorGridLines = false
            drawMinorGridLines = false
            drawMajorBands = false
            visibility = View.GONE
        }
    }

    private fun generateRenderableSeries(yAxisId: String, dataSeries: IDataSeries<*, *>, strokeStyle: SolidPenStyle): FastLineRenderableSeries {
        return FastLineRenderableSeries().init {
            this.dataSeries = dataSeries
            this.strokeStyle = strokeStyle
            this.yAxisId = yAxisId
            this.paletteProvider = DimTracePaletteProvider()
        }
    }

    private fun updateVMValues() {
        hrVM.bpmValue.set(hrValues[random.nextInt(hrValues.size)])
        bloodPressureVM.bloodPressure.set(bloodPressureValues[random.nextInt(bloodPressureValues.size)])
        bloodVolumeVM.bloodVolumeValue.set(bloodVolumeValues[random.nextInt(bloodVolumeValues.size)])
        bloodOxygenationVM.spoValue.set(bloodOxygenationValues[random.nextInt(bloodOxygenationValues.size)])

        bloodPressureVM.bloodPressureBarValue.set(bloodPresssureBarValues[random.nextInt(bloodPresssureBarValues.size)])
        bloodVolumeVM.svBar1Value.set(bloodVolumeBarValues[random.nextInt(bloodVolumeBarValues.size)])
        bloodVolumeVM.svBar2Value.set(bloodVolumeBarValues[random.nextInt(bloodVolumeBarValues.size)])
        bloodOxygenationVM.spoClockValue.set(getTimeString())
    }

    private fun getTimeString(): String = timeFormat.format(Calendar.getInstance().time)

    override fun onPause() {
        super.onPause()

        dataProvider.stop()

        dataSeriesSubscription?.dispose()
        dataSeriesSubscription = null

        labelsUpdateSubscription?.dispose()
        labelsUpdateSubscription = null

        heartRateSubscription?.dispose()
        heartRateSubscription = null
    }

    private class LeftAlignedOuterVerticallyStackedYAxisLayoutStrategy : VerticalAxisLayoutStrategy() {
        override fun measureAxes(availableWidth: Int, availableHeight: Int, chartLayoutState: ChartLayoutState) {
            for (axis in axes) {
                axis.updateAxisMeasurements()

                chartLayoutState.leftOuterAreaSize = Math.max(getRequiredAxisSize(axis.axisLayoutState), chartLayoutState.leftOuterAreaSize)
            }
        }

        override fun layoutAxes(left: Int, top: Int, right: Int, bottom: Int) {
            val height = bottom - top
            val axisSize = height / axes.size

            var topPlacement = top
            for (axis in axes) {
                val bottomPlacement = topPlacement + axisSize

                axis.layoutArea(right - getRequiredAxisSize(axis.axisLayoutState), topPlacement, right, bottomPlacement)

                topPlacement = bottomPlacement
            }
        }
    }

    private class DimTracePaletteProvider : PaletteProviderBase<XyRenderableSeriesBase>(XyRenderableSeriesBase::class.javaObjectType), IStrokePaletteProvider {
        private val colors = IntegerValues()

        private val startOpacity = 0.2 // fade out start opacity
        private val endOpacity = 1.0 // fade out end opacity
        private val opacityDiff = endOpacity - startOpacity

        override fun getStrokeColors(): IntegerValues = colors

        override fun update() {
            val size = renderableSeries.currentRenderPassData.pointsCount()
            colors.setSize(size)

            val defaultColor = renderableSeries.strokeStyle.color
            val colorsArray = colors.itemsArray

            val doubleSize = size.toDouble()

            for (i in 0..size - 1) {
                val fraction = i / doubleSize

                val opacity = (startOpacity + fraction * opacityDiff).toFloat()

                colorsArray[i] = ColorUtil.argb(defaultColor, opacity)
            }
        }
    }

    private class EcgDataBatch {
        val xValues = DoubleValues()

        val ecgHeartRateValuesA = DoubleValues()
        val bloodPressureValuesA = DoubleValues()
        val bloodVolumeValuesA = DoubleValues()
        val bloodOxygenationA = DoubleValues()

        val ecgHeartRateValuesB = DoubleValues()
        val bloodPressureValuesB = DoubleValues()
        val bloodVolumeValuesB = DoubleValues()
        val bloodOxygenationB = DoubleValues()

        fun updateData(ecgDataList: List<EcgData>) {
            xValues.clear()
            ecgHeartRateValuesA.clear()
            ecgHeartRateValuesB.clear()
            bloodPressureValuesA.clear()
            bloodPressureValuesB.clear()
            bloodVolumeValuesA.clear()
            bloodVolumeValuesB.clear()
            bloodOxygenationA.clear()
            bloodOxygenationB.clear()

            for ((xValue, ecgHeartRate, bloodPressure, bloodVolume, bloodOxygenation, currentTrace) in ecgDataList) {
                xValues.add(xValue)

                if (currentTrace == TraceAOrB.TraceA) {
                    ecgHeartRateValuesA.add(ecgHeartRate)
                    bloodPressureValuesA.add(bloodPressure)
                    bloodVolumeValuesA.add(bloodVolume)
                    bloodOxygenationA.add(bloodOxygenation)

                    ecgHeartRateValuesB.add(Double.NaN)
                    bloodPressureValuesB.add(Double.NaN)
                    bloodVolumeValuesB.add(Double.NaN)
                    bloodOxygenationB.add(Double.NaN)
                } else {
                    ecgHeartRateValuesA.add(Double.NaN)
                    bloodPressureValuesA.add(Double.NaN)
                    bloodVolumeValuesA.add(Double.NaN)
                    bloodOxygenationA.add(Double.NaN)

                    ecgHeartRateValuesB.add(ecgHeartRate)
                    bloodPressureValuesB.add(bloodPressure)
                    bloodVolumeValuesB.add(bloodVolume)
                    bloodOxygenationB.add(bloodOxygenation)
                }
            }
        }
    }
}