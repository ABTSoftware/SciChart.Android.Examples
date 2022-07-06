//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2021. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// SciChartDSLs.kt is part of SCICHART®, High Performance Scientific Charts
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

import android.content.Context
import android.graphics.Bitmap
import com.scichart.charting.model.PieRenderableSeriesCollection
import com.scichart.charting.model.PieSegmentCollection
import com.scichart.charting.model.RenderableSeriesCollection
import com.scichart.charting.model.dataSeries.OhlcDataSeries
import com.scichart.charting.model.dataSeries.UniformHeatmapDataSeries
import com.scichart.charting.model.dataSeries.XyDataSeries
import com.scichart.charting.model.dataSeries.XyyDataSeries
import com.scichart.charting.modifiers.*
import com.scichart.charting.visuals.SciChartSurface
import com.scichart.charting.visuals.SciPieChartSurface
import com.scichart.charting.visuals.annotations.*
import com.scichart.charting.visuals.axes.*
import com.scichart.charting.visuals.legend.SciChartLegend
import com.scichart.charting.visuals.pointmarkers.*
import com.scichart.charting.visuals.pointmarkers.SpritePointMarker.ISpritePointMarkerDrawer
import com.scichart.charting.visuals.renderableSeries.*
import com.scichart.charting3d.model.RenderableSeries3DCollection
import com.scichart.charting3d.model.dataSeries.freeSurface.CustomSurfaceDataSeries3D
import com.scichart.charting3d.model.dataSeries.freeSurface.CustomSurfaceDataSeries3D.UVFunc
import com.scichart.charting3d.model.dataSeries.freeSurface.CustomSurfaceDataSeries3D.ValueFunc
import com.scichart.charting3d.model.dataSeries.freeSurface.CylindroidDataSeries3D
import com.scichart.charting3d.model.dataSeries.freeSurface.EllipsoidDataSeries3D
import com.scichart.charting3d.model.dataSeries.freeSurface.PolarDataSeries3D
import com.scichart.charting3d.model.dataSeries.grid.UniformGridDataSeries3D
import com.scichart.charting3d.model.dataSeries.waterfall.WaterfallDataSeries3D
import com.scichart.charting3d.model.dataSeries.xyz.XyzDataSeries3D
import com.scichart.charting3d.modifiers.*
import com.scichart.charting3d.visuals.SciChartSurface3D
import com.scichart.charting3d.visuals.axes.DateAxis3D
import com.scichart.charting3d.visuals.axes.LogarithmicNumericAxis3D
import com.scichart.charting3d.visuals.axes.NumericAxis3D
import com.scichart.charting3d.visuals.camera.ICameraController
import com.scichart.charting3d.visuals.pointMarkers.*
import com.scichart.charting3d.visuals.renderableSeries.IRenderableSeries3D
import com.scichart.charting3d.visuals.renderableSeries.columns.ColumnRenderableSeries3D
import com.scichart.charting3d.visuals.renderableSeries.freeSurface.FreeSurfaceRenderableSeries3D
import com.scichart.charting3d.visuals.renderableSeries.impulse.ImpulseRenderableSeries3D
import com.scichart.charting3d.visuals.renderableSeries.pointLine.PointLineRenderableSeries3D
import com.scichart.charting3d.visuals.renderableSeries.scatter.ScatterRenderableSeries3D
import com.scichart.charting3d.visuals.renderableSeries.surfaceMesh.SurfaceMeshRenderableSeries3D
import com.scichart.charting3d.visuals.renderableSeries.waterfall.WaterfallRenderableSeries3D
import com.scichart.core.framework.ISuspendable
import com.scichart.core.framework.UpdateSuspender
import com.scichart.core.observable.ObservableCollection

inline fun <T : ISuspendable> T.suspendUpdates(crossinline block: T.() -> Unit) {
    UpdateSuspender.using(this) {
        block()
    }
}

data class CollectionContext<T>(val collection: ObservableCollection<T>, val context: Context)

// region DataSeries
inline fun <reified TX : Comparable<TX>, reified TY : Comparable<TY>> IRenderableSeries.xyDataSeries(
    seriesName: String? = null,
    init: XyDataSeries<TX, TY>.() -> Unit = {}
) {
    dataSeries = XyDataSeries<TX, TY>(seriesName).apply(init)
}

inline fun <reified TX : Comparable<TX>, reified TY : Comparable<TY>> IRenderableSeries.xyyDataSeries(
    seriesName: String? = null,
    init: XyyDataSeries<TX, TY>.() -> Unit = {}
) {
    dataSeries = XyyDataSeries<TX, TY>(seriesName).apply(init)
}

inline fun <reified TX : Comparable<TX>, reified TY : Comparable<TY>> IRenderableSeries.ohlcDataSeries(
    seriesName: String? = null,
    init: OhlcDataSeries<TX, TY>.() -> Unit = {}
) {
    dataSeries = OhlcDataSeries<TX, TY>(seriesName).apply(init)
}

inline fun <reified TX : Comparable<TX>, reified TY : Comparable<TY>, reified TZ : Comparable<TZ>> FastUniformHeatmapRenderableSeries.uniformHeatmapDataSeries(
    xSize: Int, ySize: Int,
    seriesName: String? = null,
    init: UniformHeatmapDataSeries<TX, TY, TZ>.() -> Unit = {}
) {
    dataSeries = UniformHeatmapDataSeries<TX, TY, TZ>(xSize, ySize, seriesName).apply(init)
}
// endregion

// region Axes
fun SciChartSurface.xAxes(clearCollection: Boolean = false, init: CollectionContext<IAxis>.() -> Unit) {
    if (clearCollection) xAxes.clear()
    CollectionContext<IAxis>(xAxes, context).init()
}
fun SciChartSurface.yAxes(clearCollection: Boolean = false, init: CollectionContext<IAxis>.() -> Unit) {
    if (clearCollection) yAxes.clear()
    CollectionContext<IAxis>(yAxes, context).init()
}

fun <T : IAxis> CollectionContext<T>.axis(axis: T) { collection.add(axis) }
fun CollectionContext<IAxis>.numericAxis(init: NumericAxis.() -> Unit = {}) = collection.add(NumericAxis(context).apply(init))
fun CollectionContext<IAxis>.dateAxis(init: DateAxis.() -> Unit = {}) = collection.add(DateAxis(context).apply(init))
fun CollectionContext<IAxis>.categoryDateAxis(init: CategoryDateAxis.() -> Unit = {}) = collection.add(CategoryDateAxis(context).apply(init))
fun CollectionContext<IAxis>.logarithmicAxis(init: LogarithmicNumericAxis.() -> Unit = {}) = collection.add(LogarithmicNumericAxis(context).apply(init))
fun CollectionContext<IAxis>.indexDateAxis(init: IndexDateAxis.() -> Unit = {}) = collection.add(IndexDateAxis(context).apply(init))
// endregion

// region Renderable Series
fun SciChartSurface.renderableSeries(init: RenderableSeriesCollection.() -> Unit) {
    renderableSeries.init()
}
fun <T : IRenderableSeries> RenderableSeriesCollection.rSeries(series: T) { add(series) }
fun RenderableSeriesCollection.fastCandlestickRenderableSeries(init: FastCandlestickRenderableSeries.() -> Unit) { add(FastCandlestickRenderableSeries().apply(init)) }
fun RenderableSeriesCollection.fastOhlcRenderableSeries(init: FastOhlcRenderableSeries.() -> Unit) { add(FastOhlcRenderableSeries().apply(init)) }
fun RenderableSeriesCollection.fastLineRenderableSeries(init: FastLineRenderableSeries.() -> Unit) { add(FastLineRenderableSeries().apply(init)) }
fun RenderableSeriesCollection.xyScatterRenderableSeries(init: XyScatterRenderableSeries.() -> Unit) { add(XyScatterRenderableSeries().apply(init)) }
fun RenderableSeriesCollection.fastBubbleRenderableSeries(init: FastBubbleRenderableSeries.() -> Unit) { add(FastBubbleRenderableSeries().apply(init)) }
fun RenderableSeriesCollection.fastColumnRenderableSeries(init: FastColumnRenderableSeries.() -> Unit) { add(FastColumnRenderableSeries().apply(init)) }
fun RenderableSeriesCollection.fastImpulseRenderableSeries(init: FastImpulseRenderableSeries.() -> Unit) { add(FastImpulseRenderableSeries().apply(init)) }
fun RenderableSeriesCollection.fastBandRenderableSeries(init: FastBandRenderableSeries.() -> Unit) { add(FastBandRenderableSeries().apply(init)) }
fun RenderableSeriesCollection.fastMountainRenderableSeries(init: FastMountainRenderableSeries.() -> Unit) { add(FastMountainRenderableSeries().apply(init)) }
fun RenderableSeriesCollection.fastErrorBarsRenderableSeries(init: FastErrorBarsRenderableSeries.() -> Unit) { add(FastErrorBarsRenderableSeries().apply(init)) }
fun RenderableSeriesCollection.fastFixedErrorBarsRenderableSeries(init: FastFixedErrorBarsRenderableSeries.() -> Unit) { add(FastFixedErrorBarsRenderableSeries().apply(init)) }
fun RenderableSeriesCollection.fastUniformHeatmapRenderableSeries(init: FastUniformHeatmapRenderableSeries.() -> Unit) { add(FastUniformHeatmapRenderableSeries().apply(init)) }
fun RenderableSeriesCollection.splineLineRenderableSeries(init: SplineLineRenderableSeries.() -> Unit) { add(SplineLineRenderableSeries().apply(init)) }
fun RenderableSeriesCollection.splineBandRenderableSeries(init: SplineBandRenderableSeries.() -> Unit) { add(SplineBandRenderableSeries().apply(init)) }
fun RenderableSeriesCollection.splineMountainRenderableSeries(init: SplineMountainRenderableSeries.() -> Unit) { add(SplineMountainRenderableSeries().apply(init)) }

fun RenderableSeriesCollection.verticallyStackedMountainsCollection(init: VerticallyStackedMountainsCollection.() -> Unit) { add(VerticallyStackedMountainsCollection().apply(init)) }
fun VerticallyStackedMountainsCollection.stackedMountainRenderableSeries(init: StackedMountainRenderableSeries.() -> Unit) { add(StackedMountainRenderableSeries().apply(init)) }

fun RenderableSeriesCollection.horizontallyStackedColumnsCollection(init: HorizontallyStackedColumnsCollection.() -> Unit) { add(HorizontallyStackedColumnsCollection().apply(init)) }
fun RenderableSeriesCollection.verticallyStackedColumnsCollection(init: VerticallyStackedColumnsCollection.() -> Unit) { add(VerticallyStackedColumnsCollection().apply(init)) }
fun VerticallyStackedColumnsCollection.stackedColumnRenderableSeries(init: StackedColumnRenderableSeries.() -> Unit) { add(StackedColumnRenderableSeries().apply(init)) }
fun HorizontallyStackedColumnsCollection.stackedColumnRenderableSeries(init: StackedColumnRenderableSeries.() -> Unit) { add(StackedColumnRenderableSeries().apply(init)) }
fun HorizontallyStackedColumnsCollection.verticallyStackedColumnsCollection(init: VerticallyStackedColumnsCollection.() -> Unit) { add(VerticallyStackedColumnsCollection().apply(init)) }
// endregion

// region PointMarkers
fun IRenderableSeries.ellipsePointMarker(init: EllipsePointMarker.() -> Unit) { pointMarker = EllipsePointMarker().apply(init) }
fun IRenderableSeries.squarePointMarker(init: SquarePointMarker.() -> Unit) { pointMarker = SquarePointMarker().apply(init) }
fun IRenderableSeries.trianglePointMarker(init: TrianglePointMarker.() -> Unit) { pointMarker = TrianglePointMarker().apply(init) }
fun IRenderableSeries.crossPointMarker(init: CrossPointMarker.() -> Unit) { pointMarker = CrossPointMarker().apply(init) }
fun IRenderableSeries.spritePointMarker(pointMarkerDrawer: ISpritePointMarkerDrawer, init: SpritePointMarker.() -> Unit) { pointMarker = SpritePointMarker(pointMarkerDrawer).apply(init) }
// endregion

// region Chart Modifiers
fun SciChartSurface.chartModifiers(init: CollectionContext<IChartModifier>.() -> Unit) {
    CollectionContext<IChartModifier>(chartModifiers, context).apply(init)
}
fun CollectionContext<IChartModifier>.modifierGroup(context: Context, init: CollectionContext<IChartModifier>.() -> Unit): ModifierGroup {
    return ModifierGroup().apply {
        CollectionContext<IChartModifier>(childModifiers, context).apply(init)
        collection.add(this)
    }
}

fun <T : IChartModifier> CollectionContext<T>.modifier(chartModifier: T) = collection.add(chartModifier)
fun <T : IChartModifier> ModifierGroup.modifier(chartModifier: T) = childModifiers.add(chartModifier)

fun CollectionContext<IChartModifier>.zoomPanModifier(init: ZoomPanModifier.() -> Unit = {}) = collection.add(ZoomPanModifier().apply(init))
fun CollectionContext<IChartModifier>.zoomExtentsModifier(init: ZoomExtentsModifier.() -> Unit = {}) = collection.add(ZoomExtentsModifier().apply(init))
fun CollectionContext<IChartModifier>.pinchZoomModifier(init: PinchZoomModifier.() -> Unit = {}) = collection.add(PinchZoomModifier().apply(init))
fun CollectionContext<IChartModifier>.rubberBandXyZoomModifier(init: RubberBandXyZoomModifier.() -> Unit = {}) = collection.add(RubberBandXyZoomModifier().apply(init))
fun CollectionContext<IChartModifier>.cursorModifier(init: CursorModifier.() -> Unit = {}) = collection.add(CursorModifier().apply(init))
fun CollectionContext<IChartModifier>.rolloverModifier(init: RolloverModifier.() -> Unit = {}) = collection.add(RolloverModifier().apply(init))
fun CollectionContext<IChartModifier>.tooltipModifier(init: TooltipModifier.() -> Unit = {}) = collection.add(TooltipModifier().apply(init))
fun CollectionContext<IChartModifier>.xAxisDragModifier(init: XAxisDragModifier.() -> Unit = {}) = collection.add(XAxisDragModifier().apply(init))
fun CollectionContext<IChartModifier>.yAxisDragModifier(init: YAxisDragModifier.() -> Unit = {}) = collection.add(YAxisDragModifier().apply(init))
fun CollectionContext<IChartModifier>.seriesValueModifier(init: SeriesValueModifier.() -> Unit = {}) = collection.add(SeriesValueModifier().apply(init))
fun CollectionContext<IChartModifier>.legendModifier(init: LegendModifier.() -> Unit = {}) = collection.add(LegendModifier(context).apply(init))
fun CollectionContext<IChartModifier>.seriesSelectionModifier(init: SeriesSelectionModifier.() -> Unit = {}) = collection.add(SeriesSelectionModifier().apply(init))

fun CollectionContext<IChartModifier>.defaultModifiers() {
    val modifierGroup = ModifierGroup().apply {
        childModifiers.add(PinchZoomModifier())
        childModifiers.add(ZoomPanModifier().apply { receiveHandledEvents = true })
        childModifiers.add(ZoomExtentsModifier())
    }
    collection.add(modifierGroup)
}
// endregion

// region Annotations
fun SciChartSurface.annotations(init: CollectionContext<IAnnotation>.() -> Unit) {
    CollectionContext<IAnnotation>(annotations, context).init()
}
fun <T : IAnnotation> CollectionContext<IAnnotation>.annotation(annotation: T) { collection.add(annotation) }
fun CollectionContext<IAnnotation>.textAnnotation(init: TextAnnotation.() -> Unit) = collection.add(TextAnnotation(context).apply(init))
fun CollectionContext<IAnnotation>.axisMarkerAnnotation(init: AxisMarkerAnnotation.() -> Unit) = collection.add(AxisMarkerAnnotation(context).apply(init))
fun CollectionContext<IAnnotation>.lineAnnotation(init: LineAnnotation.() -> Unit) = collection.add(LineAnnotation(context).apply(init))
fun CollectionContext<IAnnotation>.lineArrowAnnotation(init: LineArrowAnnotation.() -> Unit) = collection.add(LineArrowAnnotation(context).apply(init))
fun CollectionContext<IAnnotation>.boxAnnotation(init: BoxAnnotation.() -> Unit) = collection.add(BoxAnnotation(context).apply(init))
fun CollectionContext<IAnnotation>.customAnnotation(init: CustomAnnotation.() -> Unit) = collection.add(CustomAnnotation(context).apply(init))
fun CollectionContext<IAnnotation>.horizontalLineAnnotation(init: HorizontalLineAnnotation.() -> Unit) = collection.add(HorizontalLineAnnotation(context).apply(init))
fun CollectionContext<IAnnotation>.verticalLineAnnotation(init: VerticalLineAnnotation.() -> Unit) = collection.add(VerticalLineAnnotation(context).apply(init))

fun LineAnnotationWithLabelsBase.annotationLabels(init: CollectionContext<AnnotationLabel>.() -> Unit) {
    CollectionContext<AnnotationLabel>(annotationLabels, context).init()
}
fun CollectionContext<AnnotationLabel>.annotationLabel(init: AnnotationLabel.() -> Unit) = collection.add(AnnotationLabel(context).apply(init))
// endregion

// region Pie and Donut Series
fun SciPieChartSurface.renderableSeries(init: PieRenderableSeriesCollection.() -> Unit) = renderableSeries.init()
fun <T : IPieRenderableSeries> PieRenderableSeriesCollection.rSeries(series: T) = add(series)
fun PieRenderableSeriesCollection.pieRenderableSeries(init: PieRenderableSeries.() -> Unit) = add(PieRenderableSeries().apply(init))
fun PieRenderableSeriesCollection.donutRenderableSeries(init: DonutRenderableSeries.() -> Unit) = add(DonutRenderableSeries().apply(init))

fun PieDonutRenderableSeriesBase.segmentsCollection(init: PieSegmentCollection.() -> Unit) = segmentsCollection.init()
fun PieSegmentCollection.pieSegment(init: PieSegment.() -> Unit) = add(PieSegment().apply(init))

fun SciPieChartSurface.chartModifiers(init: CollectionContext<IPieChartModifier>.() -> Unit) {
    CollectionContext<IPieChartModifier>(chartModifiers, context).apply(init)
}
fun <T : IPieChartModifier> CollectionContext<T>.modifier(chartModifier: T) = collection.add(chartModifier)
fun CollectionContext<IPieChartModifier>.pieSegmentSelectionModifier(init: PieSegmentSelectionModifier.() -> Unit = {}) = collection.add(PieSegmentSelectionModifier().apply(init))
fun CollectionContext<IPieChartModifier>.pieChartTooltipModifier(init: PieChartTooltipModifier.() -> Unit = {}) = collection.add(PieChartTooltipModifier().apply(init))
fun CollectionContext<IPieChartModifier>.pieChartLegendModifier(init: PieChartLegendModifier.() -> Unit = {}) = collection.add(PieChartLegendModifier(context).apply(init))
fun CollectionContext<IPieChartModifier>.pieChartLegendModifier(legend: SciChartLegend, init: PieChartLegendModifier.() -> Unit = {}) = collection.add(PieChartLegendModifier(legend).apply(init))

// endregion

// ---- 3D ----

// region Axes 3D
fun numericAxis3D(init: NumericAxis3D.() -> Unit = {}): NumericAxis3D { return NumericAxis3D().apply(init) }
fun dateAxis3D(init: DateAxis3D.() -> Unit = {}): DateAxis3D { return DateAxis3D().apply(init) }
fun logarithmicNumericAxis3D(init: LogarithmicNumericAxis3D.() -> Unit = {}): LogarithmicNumericAxis3D { return LogarithmicNumericAxis3D().apply(init) }
// endregion

// region DataSeries 3D
inline fun <reified TX : Comparable<TX>, reified TY : Comparable<TY>, reified TZ : Comparable<TZ>> IRenderableSeries3D.xyzDataSeries3D(
    seriesName: String? = null,
    init: XyzDataSeries3D<TX, TY, TZ>.() -> Unit = {}
) {
    dataSeries = XyzDataSeries3D<TX, TY, TZ>(seriesName).apply(init)
}

inline fun <reified TX : Comparable<TX>, reified TY : Comparable<TY>, reified TZ : Comparable<TZ>> IRenderableSeries3D.waterfallDataSeries3D(
    xSize: Int, zSize: Int,
    seriesName: String? = null,
    init: WaterfallDataSeries3D<TX, TY, TZ>.() -> Unit = {}
) {
    dataSeries = WaterfallDataSeries3D<TX, TY, TZ>(xSize, zSize, seriesName).apply(init)
}

inline fun <reified TX : Comparable<TX>, reified TY : Comparable<TY>, reified TZ : Comparable<TZ>> IRenderableSeries3D.customSurfaceDataSeries3D(
    uSize: Int, vSize: Int,
    radialDistanceFunc: UVFunc, azimuthalAngleFunc: UVFunc, polarAngleFunc: UVFunc,
    xFunc: ValueFunc<TX>, yFunc: ValueFunc<TY>, zFunc: ValueFunc<TZ>,
    uMin: Double, uMax: Double, vMin: Double, vMax: Double,
    seriesName: String? = null,
    init: CustomSurfaceDataSeries3D<TX, TY, TZ>.() -> Unit = {}
) {
    dataSeries = CustomSurfaceDataSeries3D(
        uSize, vSize,
        radialDistanceFunc, azimuthalAngleFunc, polarAngleFunc,
        xFunc, yFunc, zFunc,
        uMin, uMax, vMin, vMax,
        seriesName
    ).apply(init)
}

inline fun <reified TXZ : Comparable<TXZ>, reified TY : Comparable<TY>> IRenderableSeries3D.cylindroidDataSeries3D(
    uSize: Int, vSize: Int,
    seriesName: String? = null,
    init: CylindroidDataSeries3D<TXZ, TY>.() -> Unit = {}
) {
    dataSeries = CylindroidDataSeries3D<TXZ, TY>(uSize, vSize, seriesName).apply(init)
}

inline fun <reified TXYZ : Comparable<TXYZ>> IRenderableSeries3D.ellipsoidDataSeries3D(
    uSize: Int, vSize: Int,
    seriesName: String? = null,
    init: EllipsoidDataSeries3D<TXYZ>.() -> Unit = {}
) {
    dataSeries = EllipsoidDataSeries3D<TXYZ>(uSize, vSize, seriesName).apply(init)
}

inline fun <reified TPolar : Comparable<TPolar>, reified THeight: Comparable<THeight>> IRenderableSeries3D.polarDataSeries3D(
    uSize: Int, vSize: Int,
    uMin: Double, uMax: Double,
    seriesName: String? = null,
    init: PolarDataSeries3D<TPolar, THeight>.() -> Unit = {}
) {
    dataSeries = PolarDataSeries3D<TPolar, THeight>(uSize, vSize, uMin, uMax, seriesName).apply(init)
}

inline fun <reified TX : Comparable<TX>, reified TY : Comparable<TY>, reified TZ : Comparable<TZ>> IRenderableSeries3D.uniformGridDataSeries3D(
    xSize: Int, zSize: Int,
    seriesName: String? = null,
    init: UniformGridDataSeries3D<TX, TY, TZ>.() -> Unit = {}
) {
    dataSeries = UniformGridDataSeries3D<TX, TY, TZ>(xSize, zSize, seriesName).apply(init)
}
// endregion

// region RenderableSeries 3D
fun SciChartSurface3D.renderableSeries(init: RenderableSeries3DCollection.() -> Unit) {
    renderableSeries.init()
}
fun <T : IRenderableSeries3D> RenderableSeries3DCollection.rSeries(series: T) { add(series) }
fun RenderableSeries3DCollection.waterfallRenderableSeries3D(init: WaterfallRenderableSeries3D.() -> Unit) { add(WaterfallRenderableSeries3D().apply(init)) }
fun RenderableSeries3DCollection.pointLineRenderableSeries3D(init: PointLineRenderableSeries3D.() -> Unit) { add(PointLineRenderableSeries3D().apply(init)) }
fun RenderableSeries3DCollection.scatterRenderableSeries3D(init: ScatterRenderableSeries3D.() -> Unit) { add(ScatterRenderableSeries3D().apply(init)) }
fun RenderableSeries3DCollection.freeSurfaceRenderableSeries3D(init: FreeSurfaceRenderableSeries3D.() -> Unit) { add(FreeSurfaceRenderableSeries3D().apply(init)) }
fun RenderableSeries3DCollection.surfaceMeshRenderableSeries3D(init: SurfaceMeshRenderableSeries3D.() -> Unit) { add(SurfaceMeshRenderableSeries3D().apply(init)) }
fun RenderableSeries3DCollection.columnRenderableSeries3D(init: ColumnRenderableSeries3D.() -> Unit) { add(ColumnRenderableSeries3D().apply(init)) }
fun RenderableSeries3DCollection.impulseRenderableSeries3D(init: ImpulseRenderableSeries3D.() -> Unit) { add(ImpulseRenderableSeries3D().apply(init)) }
// endregion

// region PointMarkers 3D
fun IRenderableSeries3D.spherePointMarker3D(init: SpherePointMarker3D.() -> Unit = {}) { pointMarker = SpherePointMarker3D().apply(init) }
fun IRenderableSeries3D.pyramidPointMarker3D(init: PyramidPointMarker3D.() -> Unit = {}) { pointMarker = PyramidPointMarker3D().apply(init) }
fun IRenderableSeries3D.cylinderPointMarker3D(init: CylinderPointMarker3D.() -> Unit = {}) { pointMarker = CylinderPointMarker3D().apply(init) }
fun IRenderableSeries3D.cubePointMarker3D(init: CubePointMarker3D.() -> Unit = {}) { pointMarker = CubePointMarker3D().apply(init) }

fun IRenderableSeries3D.ellipsePointMarker3D(init: EllipsePointMarker3D.() -> Unit = {}) { pointMarker = EllipsePointMarker3D().apply(init) }
fun IRenderableSeries3D.quadPointMarker3D(init: QuadPointMarker3D.() -> Unit = {}) { pointMarker = QuadPointMarker3D().apply(init) }
fun IRenderableSeries3D.trianglePointMarker3D(init: TrianglePointMarker3D.() -> Unit = {}) { pointMarker = TrianglePointMarker3D().apply(init) }
fun IRenderableSeries3D.pixelPointMarker3D(init: PixelPointMarker3D.() -> Unit = {}) { pointMarker = PixelPointMarker3D().apply(init) }
fun IRenderableSeries3D.customPointMarker3D(texture: Bitmap, init: CustomPointMarker3D.() -> Unit = {}) { pointMarker = CustomPointMarker3D(texture).apply(init) }
// endregion

// region ChartModifiers 3D
fun SciChartSurface3D.chartModifiers(init: CollectionContext<IChartModifier3D>.() -> Unit) {
    CollectionContext<IChartModifier3D>(chartModifiers, context).apply(init)
}
fun CollectionContext<IChartModifier3D>.modifierGroup3D(context: Context, init: CollectionContext<IChartModifier3D>.() -> Unit) {
    ModifierGroup3D().apply {
        CollectionContext<IChartModifier3D>(childModifiers, context).apply(init)
        collection.add(this)
    }
}

fun <T : IChartModifier3D> CollectionContext<T>.modifier(chartModifier: T) = collection.add(chartModifier)
fun CollectionContext<IChartModifier3D>.zoomExtentsModifier3D(init: ZoomExtentsModifier3D.() -> Unit = {}) { collection.add(ZoomExtentsModifier3D().apply(init)) }
fun ZoomExtentsModifier3D.resetTarget(x: Float, y: Float, z: Float) { resetTarget.assign(x, y, z) }
fun ZoomExtentsModifier3D.resetPosition(x: Float, y: Float, z: Float) { resetPosition.assign(x, y, z) }
fun CollectionContext<IChartModifier3D>.orbitModifier3D(init: OrbitModifier3D.() -> Unit = {}) { collection.add(OrbitModifier3D().apply(init)) }
fun CollectionContext<IChartModifier3D>.pinchZoomModifier3D(init: PinchZoomModifier3D.() -> Unit = {}) { collection.add(PinchZoomModifier3D().apply(init)) }
fun CollectionContext<IChartModifier3D>.tooltipModifier3D(init: TooltipModifier3D.() -> Unit = {}) { collection.add(TooltipModifier3D().apply(init)) }
fun CollectionContext<IChartModifier3D>.vertexSelectionModifier3D(init: VertexSelectionModifier3D.() -> Unit = {}) { collection.add(VertexSelectionModifier3D().apply(init)) }
fun CollectionContext<IChartModifier3D>.legendModifier3D(init: LegendModifier3D.() -> Unit = {}) { collection.add(LegendModifier3D(context).apply(init)) }

fun CollectionContext<IChartModifier3D>.defaultModifiers3D() {
    modifierGroup3D(context) {
        pinchZoomModifier3D()
        orbitModifier3D { receiveHandledEvents = true; executeOnPointerCount = 1 }
        zoomExtentsModifier3D()
    }
}
// endregion

// region Camera
fun SciChartSurface3D.camera(init: ICameraController.() -> Unit) {
    camera.apply(init)
}
fun ICameraController.target(x: Float, y: Float, z: Float) { this.target.assign(x, y, z) }
fun ICameraController.position(x: Float, y: Float, z: Float) { this.position.assign(x, y, z) }
//
