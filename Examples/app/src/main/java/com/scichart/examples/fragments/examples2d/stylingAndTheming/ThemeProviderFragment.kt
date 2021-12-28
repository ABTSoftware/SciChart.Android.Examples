//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2021. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// ThemeProviderFragment.kt is part of SCICHART®, High Performance Scientific Charts
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

package com.scichart.examples.fragments.examples2d.stylingAndTheming.kt

import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import com.scichart.charting.modifiers.CursorModifier
import com.scichart.charting.modifiers.ModifierGroup
import com.scichart.charting.themes.ThemeManager
import com.scichart.charting.visuals.axes.AutoRange
import com.scichart.charting.visuals.axes.AxisAlignment
import com.scichart.data.model.DoubleRange
import com.scichart.examples.R
import com.scichart.examples.components.SpinnerStringAdapter
import com.scichart.examples.data.DataManager
import com.scichart.examples.databinding.ExampleThemeProviderChartFragmentBinding
import com.scichart.examples.fragments.base.ExampleBaseFragment
import com.scichart.examples.utils.BillionsLabelProvider
import com.scichart.examples.utils.ItemSelectedListenerBase
import com.scichart.examples.utils.ThousandsLabelProvider
import com.scichart.examples.utils.ViewSettingsUtil
import com.scichart.examples.utils.interpolator.ElasticOutInterpolator
import com.scichart.examples.utils.scichartExtensions.*
import com.scichart.examples.utils.widgetgeneration.ImageViewWidget
import com.scichart.examples.utils.widgetgeneration.Widget
import java.util.*

class ThemeProviderFragment : ExampleBaseFragment<ExampleThemeProviderChartFragmentBinding>() {
    private lateinit var cursorModifier: CursorModifier
    private lateinit var zoomingModifiers: ModifierGroup

    override fun inflateBinding(inflater: LayoutInflater): ExampleThemeProviderChartFragmentBinding {
        return ExampleThemeProviderChartFragmentBinding.inflate(inflater)
    }

    override fun initExample(binding: ExampleThemeProviderChartFragmentBinding) {
        binding.themeSelector.run {
            adapter = SpinnerStringAdapter(activity, R.array.style_list)
            setSelection(7)
            onItemSelectedListener = object : ItemSelectedListenerBase() {
                override fun onItemSelected(parent: AdapterView<*>?, view: View, position: Int, id: Long) {
                    setTheme(position)
                }
            }
        }

        val dataManager = DataManager.getInstance()
        val priceBars = dataManager.getPriceDataIndu(context)

        binding.surface.suspendUpdates {
            xAxes {
                numericAxis {
                    growBy = DoubleRange(0.1, 0.1)
                    visibleRange = DoubleRange(150.0, 180.0)
                }
            }
            yAxes {
                numericAxis {
                    axisId = "PrimaryAxisId"
                    axisAlignment = AxisAlignment.Right
                    autoRange = AutoRange.Always
                    growBy = DoubleRange(0.1, 0.1)
                    drawMajorTicks = false
                    drawMinorTicks = false
                    labelProvider = ThousandsLabelProvider()
                }
                numericAxis {
                    axisId = "SecondaryAxisId"
                    axisAlignment = AxisAlignment.Left
                    autoRange = AutoRange.Always
                    growBy = DoubleRange(0.0, 3.0)
                    drawMajorTicks = false
                    drawMinorTicks = false
                    labelProvider = BillionsLabelProvider()
                }
            }
            renderableSeries {
                fastMountainRenderableSeries {
                    yAxisId = "PrimaryAxisId"
                    xyDataSeries<Double, Double>("Mountain Series") {
                        append(priceBars.indexesAsDouble, dataManager.offset(priceBars.lowData, -1000.0))
                    }

                    scaleAnimation { zeroLine = 10500.0; interpolator = ElasticOutInterpolator() }
                }
                fastCandlestickRenderableSeries {
                    yAxisId = "PrimaryAxisId"
                    ohlcDataSeries<Double, Double>("Candlestick Series") {
                        append(priceBars.indexesAsDouble, priceBars.openData, priceBars.highData, priceBars.lowData, priceBars.closeData)
                    }

                    scaleAnimation { zeroLine = 11700.0; interpolator = ElasticOutInterpolator() }
                }
                fastLineRenderableSeries {
                    yAxisId = "PrimaryAxisId"
                    xyDataSeries<Double, Double>("Line Series") {
                        append(priceBars.indexesAsDouble, dataManager.computeMovingAverage(priceBars.closeData, 50))
                    }

                    scaleAnimation { zeroLine = 12250.0; interpolator = ElasticOutInterpolator() }
                }
                fastColumnRenderableSeries {
                    yAxisId = "SecondaryAxisId"
                    xyDataSeries<Double, Long>("Column Series") {
                        append(priceBars.indexesAsDouble, priceBars.volumeData)
                    }

                    scaleAnimation { zeroLine = 10500.0; interpolator = ElasticOutInterpolator() }
                }
            }

            chartModifiers {
                zoomingModifiers = modifierGroup(context) {
                    pinchZoomModifier { receiveHandledEvents = true }
                    zoomPanModifier { receiveHandledEvents = true }
                    isEnabled = false
                }

                legendModifier { setShowCheckboxes(false) }
                modifier(cursorModifier)
                modifier(zoomingModifiers)
                zoomExtentsModifier()
            }
        }
    }

    private fun setTheme(position: Int) {
        val themeId = when (position) {
            BLACK_STEEL -> R.style.SciChart_BlackSteel
            BRIGHT_SPARK -> R.style.SciChart_Bright_Spark
            CHROME -> R.style.SciChart_ChromeStyle
            ELECTRIC -> R.style.SciChart_ElectricStyle
            EXPRESSION_DARK -> R.style.SciChart_ExpressionDarkStyle
            EXPRESSION_LIGHT -> R.style.SciChart_ExpressionLightStyle
            OSCILLOSCOPE -> R.style.SciChart_OscilloscopeStyle
            SCI_CHART_V4_DARK -> R.style.SciChart_SciChartv4DarkStyle
            BERRY_BLUE -> R.style.SciChart_BerryBlue
            else -> ThemeManager.DEFAULT_THEME
        }

        binding.surface.theme = themeId
    }

    override fun getToolbarItems(): List<Widget> = ArrayList<Widget>().apply {
        add(ImageViewWidget.Builder().setId(R.drawable.example_toolbar_settings).setListener { openSettingsDialog() }.build())
    }

    private fun openSettingsDialog() {
        val dialog = ViewSettingsUtil.createSettingsPopup(activity, R.layout.example_theme_provider_popup_layout)

        ViewSettingsUtil.setUpRadioButton(dialog, R.id.cursor_modifier_radio_button, cursorModifier)
        ViewSettingsUtil.setUpRadioButton(dialog, R.id.zoom_modifier_radio_button, zoomingModifiers)

        dialog.show()
    }

    companion object {
        private const val BLACK_STEEL = 0
        private const val BRIGHT_SPARK = 1
        private const val CHROME = 2
        private const val ELECTRIC = 3
        private const val EXPRESSION_DARK = 4
        private const val EXPRESSION_LIGHT = 5
        private const val OSCILLOSCOPE = 6
        private const val SCI_CHART_V4_DARK = 7
        private const val BERRY_BLUE = 8
    }
}