package com.scichart.scishowcase.views

import android.databinding.DataBindingUtil
import android.graphics.Color
import android.os.Bundle
import android.support.annotation.Nullable
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.scichart.charting.visuals.axes.NumericAxis
import com.scichart.charting.visuals.renderableSeries.FastLineRenderableSeries
import com.scichart.drawing.common.SolidPenStyle
import com.scichart.scishowcase.R
import com.scichart.scishowcase.databinding.SingleChartFragmentBinding
import com.scichart.scishowcase.application.ExampleDefinition
import com.scichart.scishowcase.utils.init
import com.scichart.scishowcase.utils.XyDataSeries

@ExampleDefinition("Single Chart", "Custom Description")
class SingleChartFragment : Fragment() {
    private var binding: SingleChartFragmentBinding? = null

    override fun onCreateView(inflater: LayoutInflater?, @Nullable container: ViewGroup?, @Nullable savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate<SingleChartFragmentBinding>(inflater, getLayoutId(), container, false)

        initExample()

        return binding!!.root
    }

    private fun initExample() {

        val xAxis = NumericAxis(context)
        val yAxis = NumericAxis(context)

        val xyDataSeries = XyDataSeries<Double, Double>()
        for (index in 1..10) {
            xyDataSeries.append(index.toDouble(), (index * index).toDouble())
        }

        val rSeries = FastLineRenderableSeries().init {
            dataSeries = xyDataSeries
            strokeStyle = SolidPenStyle(Color.CYAN, true, 2f, null)
        }

        binding!!.chart.xAxes.add(xAxis)
        binding!!.chart.yAxes.add(yAxis)
        binding!!.chart.renderableSeries.add(rSeries)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        val viewModel = onCreateViewModel()
//
//        binding!!.setVariable(getBindingResourceViewModel(), viewModel)
//        binding!!.executePendingBindings()
    }

    fun getLayoutId(): Int {
        return R.layout.single_chart_fragment
    }
}