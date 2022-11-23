//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2021. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// SparkLinesChartsFragment.kt is part of SCICHART®, High Performance Scientific Charts
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

package com.scichart.examples.fragments.examples2d.multiChart.kt

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.scichart.charting.model.dataSeries.IXyDataSeries
import com.scichart.charting.visuals.axes.AutoRange.Always
import com.scichart.charting.visuals.renderableSeries.IRenderableSeries
import com.scichart.data.model.DoubleRange
import com.scichart.drawing.utility.ColorUtil
import com.scichart.examples.R
import com.scichart.examples.data.DoubleSeries
import com.scichart.examples.data.RandomWalkGenerator
import com.scichart.examples.databinding.ExampleSparkLinesFragmentBinding
import com.scichart.examples.databinding.ExampleSparkLinesItemViewBinding
import com.scichart.examples.fragments.base.ExampleBaseFragment
import com.scichart.examples.utils.scichartExtensions.*
import java.text.DecimalFormat
import java.util.*

class SparkLinesChartsFragment : ExampleBaseFragment<ExampleSparkLinesFragmentBinding>() {
    override fun inflateBinding(inflater: LayoutInflater): ExampleSparkLinesFragmentBinding {
        return ExampleSparkLinesFragmentBinding.inflate(inflater)
    }

    override fun initExample(binding: ExampleSparkLinesFragmentBinding) {
        val recyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(context)

        val dataSet = arrayOfNulls<SparkLineItemModel>(100)
        for (i in dataSet.indices) {
            val generator = RandomWalkGenerator()
            dataSet[i] = getRandomItem(i, generator.getRandomWalkSeries(50))
        }

        recyclerView.adapter = SparkLinesAdapter(dataSet)
    }

    private fun getRandomItem(index: Int, data: DoubleSeries): SparkLineItemModel {
        val dataSeries = XyDataSeries<Double, Double>().apply {
            append(data.xValues, data.yValues)
        }
        return SparkLineItemModel(dataSeries, String.format(Locale.getDefault(), "Item #%d", index))
    }

    class SparkLineItemModel(val dataSeries: IXyDataSeries<Double, Double>, val itemName: String) {
        val itemValue: Double

        init {
            val yValues = dataSeries.yValues
            val size = yValues.size
            itemValue = yValues[size - 1] / yValues[size - 2] - 1
        }
    }

    class SparkLinesItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = ExampleSparkLinesItemViewBinding.bind(itemView)
        private val format = DecimalFormat("0.##%")

        init {
            binding.surface.suspendUpdates {
                renderableSeriesAreaBorderStyle = SolidPenStyle(ColorUtil.Transparent)

                xAxes {
                    numericAxis {
                        growBy = DoubleRange(0.05, 0.05)
                        autoRange = Always
                        drawMajorBands = false
                        drawLabels = false
                        drawMinorTicks = false
                        drawMajorTicks = false
                        drawMajorGridLines = false
                        drawMinorGridLines = false
                    }
                }
                yAxes {
                    numericAxis {
                        growBy = DoubleRange(0.1, 0.1)
                        autoRange = Always
                        drawMajorBands = false
                        drawLabels = false
                        drawMinorTicks = false
                        drawMajorTicks = false
                        drawMajorGridLines = false
                        drawMinorGridLines = false
                    }
                }
                renderableSeries {
                    fastLineRenderableSeries { strokeStyle = SolidPenStyle(ColorUtil.SteelBlue) }
                }
            }
        }

        fun bindItemModel(itemModel: SparkLineItemModel) {
            binding.itemName.text = itemModel.itemName

            setValue(itemModel.itemValue)

            val renderableSeries: IRenderableSeries = binding.surface.renderableSeries[0]
            renderableSeries.dataSeries = itemModel.dataSeries
        }

        private fun setValue(value: Double) {
            val formattedValue = format.format(value)
            val sb = StringBuilder()

            val itemValue: TextView = binding.itemValue
            if (value > 0) {
                sb.append("\u21D1 ")
                itemValue.setTextColor(ColorUtil.argb(0xFF, 0x34, 0xc1, 0x9c))
            } else {
                sb.append("\u21D3 ")
                itemValue.setTextColor(ColorUtil.Red)
            }

            sb.append(formattedValue)
            itemValue.text = sb
        }
    }

    class SparkLinesAdapter(private val dataSet: Array<SparkLineItemModel?>) : RecyclerView.Adapter<SparkLinesItemViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SparkLinesItemViewHolder {
            val v = LayoutInflater.from(parent.context).inflate(R.layout.example_spark_lines_item_view, parent, false)
            return SparkLinesItemViewHolder(v)
        }

        override fun onBindViewHolder(holder: SparkLinesItemViewHolder, position: Int) {
            dataSet[position]?.let { holder.bindItemModel(it) }
        }

        override fun getItemCount(): Int {
            return dataSet.size
        }
    }
}