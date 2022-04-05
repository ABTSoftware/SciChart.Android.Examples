//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2021. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// SciChartExtensions.kt is part of SCICHART®, High Performance Scientific Charts
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

package com.scichart.examples.utils.scichartExtensions

import android.content.res.Resources
import android.graphics.drawable.ColorDrawable
import android.util.TypedValue
import androidx.annotation.ColorInt
import com.scichart.charting.model.dataSeries.*
import com.scichart.charting.visuals.axes.IAxis
import com.scichart.charting.visuals.pointmarkers.IPointMarker
import com.scichart.charting3d.model.dataSeries.freeSurface.CustomSurfaceDataSeries3D
import com.scichart.charting3d.model.dataSeries.freeSurface.CustomSurfaceDataSeries3D.UVFunc
import com.scichart.charting3d.model.dataSeries.freeSurface.CustomSurfaceDataSeries3D.ValueFunc
import com.scichart.charting3d.model.dataSeries.freeSurface.CylindroidDataSeries3D
import com.scichart.charting3d.model.dataSeries.freeSurface.EllipsoidDataSeries3D
import com.scichart.charting3d.model.dataSeries.freeSurface.PolarDataSeries3D
import com.scichart.charting3d.model.dataSeries.grid.UniformGridDataSeries3D
import com.scichart.charting3d.model.dataSeries.waterfall.WaterfallDataSeries3D
import com.scichart.charting3d.model.dataSeries.xyz.XyzDataSeries3D
import com.scichart.drawing.common.*
import com.scichart.drawing.common.SolidBrushStyle
import com.scichart.extensions.builders.SciChartBuilder
import com.scichart.extensions.builders.base.FontStyleBuilder
import kotlin.math.round
import kotlin.math.roundToInt

fun Long.drawable(): ColorDrawable = ColorDrawable(this.toInt())

// region DataSeries
@Suppress("FunctionName")
inline fun <reified TX : Comparable<TX>, reified TY : Comparable<TY>> XyDataSeries(seriesName: String? = null): XyDataSeries<TX, TY> {
    return XyDataSeries(TX::class.javaObjectType, TY::class.javaObjectType).apply { this.seriesName = seriesName }
}

@Suppress("FunctionName")
inline fun <reified TX : Comparable<TX>, reified TY : Comparable<TY>> XyyDataSeries(seriesName: String? = null): XyyDataSeries<TX, TY> {
    return XyyDataSeries(TX::class.javaObjectType, TY::class.javaObjectType).apply { this.seriesName = seriesName }
}
@Suppress("FunctionName")
inline fun <reified TX : Comparable<TX>, reified TY : Comparable<TY>, reified TZ : Comparable<TZ>> XyzDataSeries(seriesName: String? = null): XyzDataSeries<TX, TY, TZ> {
    return XyzDataSeries(TX::class.javaObjectType, TY::class.javaObjectType, TZ::class.javaObjectType).apply { this.seriesName = seriesName }
}

@Suppress("FunctionName")
inline fun <reified TX : Comparable<TX>, reified TY : Comparable<TY>> HlDataSeries(seriesName: String? = null): HlDataSeries<TX, TY> {
    return HlDataSeries(TX::class.javaObjectType, TY::class.javaObjectType).apply { this.seriesName = seriesName }
}

@Suppress("FunctionName")
inline fun <reified TX : Comparable<TX>, reified TY : Comparable<TY>> OhlcDataSeries(seriesName: String? = null): OhlcDataSeries<TX, TY> {
    return OhlcDataSeries(TX::class.javaObjectType, TY::class.javaObjectType).apply { this.seriesName = seriesName }
}

@Suppress("FunctionName")
inline fun <reified TX : Comparable<TX>, reified TY : Comparable<TY>, reified TZ : Comparable<TZ>> UniformHeatmapDataSeries(xSize: Int, ySize: Int, seriesName: String? = null) : UniformHeatmapDataSeries<TX, TY, TZ> {
    return UniformHeatmapDataSeries(TX::class.javaObjectType, TY::class.javaObjectType, TZ::class.javaObjectType, xSize, ySize).apply { this.seriesName = seriesName }
}
// endregion

// region DataSeries3D
@Suppress("FunctionName")
inline fun <reified TX : Comparable<TX>, reified TY : Comparable<TY>, reified TZ : Comparable<TZ>> XyzDataSeries3D(seriesName: String? = null): XyzDataSeries3D<TX, TY, TZ> {
    return XyzDataSeries3D(TX::class.javaObjectType, TY::class.javaObjectType, TZ::class.javaObjectType).apply { this.seriesName = seriesName }
}

@Suppress("FunctionName")
inline fun <reified TX : Comparable<TX>, reified TY : Comparable<TY>, reified TZ : Comparable<TZ>> WaterfallDataSeries3D(
    xSize: Int, zSize: Int,
    seriesName: String? = null
): WaterfallDataSeries3D<TX, TY, TZ> {
    return WaterfallDataSeries3D(TX::class.javaObjectType, TY::class.javaObjectType, TZ::class.javaObjectType, xSize, zSize).apply { this.seriesName = seriesName }
}

@Suppress("FunctionName")
inline fun <reified TX : Comparable<TX>, reified TY : Comparable<TY>, reified TZ : Comparable<TZ>> CustomSurfaceDataSeries3D(
    uSize: Int, vSize: Int,
    radialDistanceFunc: UVFunc, azimuthalAngleFunc: UVFunc, polarAngleFunc: UVFunc,
    xFunc: ValueFunc<TX>, yFunc: ValueFunc<TY>, zFunc: ValueFunc<TZ>,
    uMin: Double, uMax: Double, vMin: Double, vMax: Double,
    seriesName: String? = null
): CustomSurfaceDataSeries3D<TX, TY, TZ> {
    return CustomSurfaceDataSeries3D(
        TX::class.javaObjectType, TY::class.javaObjectType, TZ::class.javaObjectType,
        uSize, vSize,
        radialDistanceFunc, azimuthalAngleFunc, polarAngleFunc,
        xFunc, yFunc, zFunc,
        uMin, uMax, vMin, vMax
    ).apply { this.seriesName = seriesName }
}

@Suppress("FunctionName")
inline fun <reified TXZ : Comparable<TXZ>, reified TY : Comparable<TY>> CylindroidDataSeries3D(
    uSize: Int, vSize: Int,
    seriesName: String? = null,
): CylindroidDataSeries3D<TXZ, TY> {
    return CylindroidDataSeries3D(TXZ::class.javaObjectType, TY::class.javaObjectType, uSize, vSize).apply { this.seriesName = seriesName }
}

@Suppress("FunctionName")
inline fun <reified TXYZ : Comparable<TXYZ>> EllipsoidDataSeries3D(
    uSize: Int, vSize: Int,
    seriesName: String? = null
): EllipsoidDataSeries3D<TXYZ> {
    return EllipsoidDataSeries3D(TXYZ::class.javaObjectType, uSize, vSize).apply { this.seriesName = seriesName }
}

@Suppress("FunctionName")
inline fun <reified TPolar : Comparable<TPolar>, reified THeight : Comparable<THeight>> PolarDataSeries3D(
    uSize: Int, vSize: Int,
    uMin: Double, uMax: Double,
    seriesName: String? = null
): PolarDataSeries3D<TPolar, THeight> {
    return PolarDataSeries3D(TPolar::class.javaObjectType, THeight::class.java, uSize, vSize, uMin, uMax).apply { this.seriesName = seriesName }
}

@Suppress("FunctionName")
inline fun <reified TX : Comparable<TX>, reified TY : Comparable<TY>, reified TZ : Comparable<TZ>> UniformGridDataSeries3D(xSize: Int, ySize: Int, seriesName: String? = null) : UniformGridDataSeries3D<TX, TY, TZ> {
    return UniformGridDataSeries3D(TX::class.javaObjectType, TY::class.javaObjectType, TZ::class.javaObjectType, xSize, ySize).apply { this.seriesName = seriesName }
}
// endregion

fun IAxis.setTextColor(@ColorInt color: Long) {
    tickLabelStyle = FontStyleBuilder(Resources.getSystem().displayMetrics).withTextSize(12f, TypedValue.COMPLEX_UNIT_SP).withTextColor(color.toInt()).build()
    titleStyle = FontStyleBuilder(Resources.getSystem().displayMetrics).withTextSize(18f, TypedValue.COMPLEX_UNIT_SP).withTextColor(color.toInt()).build()
}

// region Pen and Brush Styles
fun SolidPenStyle(color: Long, thickness: Float = 1f): SolidPenStyle {
    return SolidPenStyle(color.toInt(), thickness)
}

fun SolidPenStyle(@ColorInt color: Int, thickness: Float = 1f): SolidPenStyle {
    return SolidPenStyle(color, true, thickness.toDip(), null)
}

fun SolidPenStyle(@ColorInt color: Int, thickness: Float = 1f, antiAliasing: Boolean = true): SolidPenStyle {
    return SolidPenStyle(color, antiAliasing, thickness.toDip(), null)
}

fun SolidBrushStyle(color: Long): SolidBrushStyle {
    return SolidBrushStyle(color.toInt())
}

fun SolidBrushStyle(@ColorInt color: Int): SolidBrushStyle {
    return SolidBrushStyle(color)
}

fun LinearGradientBrushStyle(startColor: Long, endColor: Long): LinearGradientBrushStyle {
    return LinearGradientBrushStyle(startColor.toInt(), endColor.toInt())
}

fun LinearGradientBrushStyle(startColor: Int, endColor: Int): LinearGradientBrushStyle {
    return LinearGradientBrushStyle(0f, 0f, 0f, 1f, startColor, endColor)
}

fun RadialGradientBrushStyle(centerColor: Long, edgeColor: Long): RadialGradientBrushStyle {
    return RadialGradientBrushStyle(0.5f, 0.5f, 0.5f, 0.5f, centerColor.toInt(), edgeColor.toInt())
}

fun Float.toDip(): Float {
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this, Resources.getSystem().displayMetrics)
}

fun Float.toSp(): Float {
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, this, Resources.getSystem().displayMetrics)
}
// endregion

fun IPointMarker.setSize(size: Int) {
    val dipSize = size.toFloat().toDip().roundToInt()
    this.setSize(dipSize, dipSize)
}