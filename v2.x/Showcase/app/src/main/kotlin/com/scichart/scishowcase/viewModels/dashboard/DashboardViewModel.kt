package com.scichart.scishowcase.viewModels.dashboard

import android.content.Context
import com.scichart.charting.model.AxisCollection
import com.scichart.charting.model.RenderableSeriesCollection
import com.scichart.charting.visuals.axes.AutoRange
import com.scichart.charting.visuals.axes.NumericAxis
import com.scichart.charting.visuals.renderableSeries.FastLineRenderableSeries
import com.scichart.core.framework.ISuspendable
import com.scichart.core.framework.UpdateSuspender
import com.scichart.drawing.common.SolidPenStyle
import com.scichart.drawing.utility.ColorUtil
import com.scichart.scishowcase.model.IDataProvider
import com.scichart.scishowcase.model.dashboard.DashboardData
import com.scichart.scishowcase.utils.XyDataSeries
import com.scichart.scishowcase.utils.dip
import com.scichart.scishowcase.viewModels.FragmentViewModelBase
import com.trello.rxlifecycle2.LifecycleProvider
import com.trello.rxlifecycle2.kotlin.bindToLifecycle

class DashboardViewModel(context: Context, private val dataProvider: IDataProvider<DashboardData>, private val suspendable: ISuspendable) : FragmentViewModelBase(context) {

    val xAxes: AxisCollection = AxisCollection()
    val yAxes: AxisCollection = AxisCollection()
    val renderableSeries: RenderableSeriesCollection = RenderableSeriesCollection()

    private val dataSeries = XyDataSeries<Long, Long>()

    init {
        xAxes.add(NumericAxis(context).apply { autoRange = AutoRange.Always })
        yAxes.add(NumericAxis(context).apply { autoRange = AutoRange.Always })

        val lineThickness = context.dip(1f)

        renderableSeries.add(FastLineRenderableSeries().apply {
            dataSeries = this@DashboardViewModel.dataSeries
            strokeStyle = SolidPenStyle(ColorUtil.Grey, true, lineThickness, null)
        })
    }

    override fun subscribe(lifecycleProvider: LifecycleProvider<*>) {
        super.subscribe(lifecycleProvider)

        dataProvider.getData().doOnNext {
            UpdateSuspender.using(suspendable, {
                dataSeries.append(it.time, it.memoryUsage)
            })
        }.bindToLifecycle(lifecycleProvider).subscribe()
    }
}