//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2017. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// SplineLineRenderableSeries.java is part of the SCICHART® Examples. Permission is hereby granted
// to modify, create derivative works, distribute and publish any part of this source
// code whether for commercial, private or personal use.
//
// The SCICHART® examples are distributed in the hope that they will be useful, but
// without any warranty. It is provided "AS IS" without warranty of any kind, either
// expressed or implied.
//******************************************************************************

package com.scichart.examples.fragments.splineLineSeries;

import android.content.Context;
import android.util.DisplayMetrics;

import com.scichart.charting.model.dataSeries.IDataSeries;
import com.scichart.charting.visuals.pointmarkers.IPointMarker;
import com.scichart.charting.visuals.renderableSeries.FastLineRenderableSeries;
import com.scichart.charting.visuals.renderableSeries.ISeriesDrawingManager;
import com.scichart.charting.visuals.renderableSeries.XyRenderableSeriesBase;
import com.scichart.charting.visuals.renderableSeries.data.ISeriesRenderPassData;
import com.scichart.charting.visuals.renderableSeries.data.LineRenderPassData;
import com.scichart.charting.visuals.renderableSeries.data.XyRenderPassData;
import com.scichart.charting.visuals.renderableSeries.hitTest.CompositeHitProvider;
import com.scichart.charting.visuals.renderableSeries.hitTest.IHitProvider;
import com.scichart.charting.visuals.renderableSeries.hitTest.INearestPointProvider;
import com.scichart.charting.visuals.renderableSeries.hitTest.LerpXySeriesInfoProvider;
import com.scichart.charting.visuals.renderableSeries.hitTest.LineHitProvider;
import com.scichart.charting.visuals.renderableSeries.hitTest.NearestXyPointProvider;
import com.scichart.charting.visuals.renderableSeries.hitTest.PointMarkerHitProvider;
import com.scichart.core.framework.SmartPropertyBoolean;
import com.scichart.core.framework.SmartPropertyInteger;
import com.scichart.core.model.FloatValues;
import com.scichart.core.utility.SciChartDebugLogger;
import com.scichart.drawing.common.DrawingContextFactory;
import com.scichart.drawing.common.IAssetManager2D;
import com.scichart.drawing.common.IDrawingContext;
import com.scichart.drawing.common.IPen2D;
import com.scichart.drawing.common.IRenderContext2D;
import com.scichart.drawing.common.PenStyle;
import com.scichart.extensions.builders.PenStyleBuilder.SolidPenStyleBuilder;

/**
 * A CustomRenderableSeries example which uses a Cubic Spline algorithm to smooth the points in a FastLineRenderableSeries
 */
public class SplineLineRenderableSeries extends XyRenderableSeriesBase {

    protected final SmartPropertyBoolean isSplineEnabledProperty = new SmartPropertyBoolean(new SmartPropertyBoolean.IPropertyChangeListener() {
        @Override
        public void onPropertyChanged(boolean oldValue, boolean newValue) {
            invalidateElement();
        }
    }, true);

    protected final SmartPropertyInteger upSampleFactorProperty = new SmartPropertyInteger(new SmartPropertyInteger.IPropertyChangeListener() {
        @Override
        public void onPropertyChanged(int oldValue, int newValue) {
            invalidateElement();
        }
    }, 10);

    private final FloatValues splineXCoords = new FloatValues();
    private final FloatValues splineYCoords = new FloatValues();

    /**
     * Creates a new instance of {@link FastLineRenderableSeries} class
     */
    public SplineLineRenderableSeries() {
        this(new LineRenderPassData(), new CompositeHitProvider(new PointMarkerHitProvider(), new LineHitProvider()), new NearestXyPointProvider());
    }

    /**
     * Creates a new instance of {@link XyRenderableSeriesBase} class
     *
     * @param currentRenderPassData The render pass data instance
     * @param hitProvider           The hit provider instance
     * @param nearestPointProvider  The nearest point provider instance
     */
    protected SplineLineRenderableSeries(XyRenderPassData currentRenderPassData, IHitProvider hitProvider, INearestPointProvider nearestPointProvider) {
        super(currentRenderPassData, hitProvider, nearestPointProvider);

        setSeriesInfoProvider(new LerpXySeriesInfoProvider());
    }

    public boolean getIsSplineEnabled() {
        return isSplineEnabledProperty.getValue();
    }

    public void setIsSplineEnabled(boolean isSplineEnabled) {
        isSplineEnabledProperty.setStrongValue(isSplineEnabled);
    }

    public int getUpSampleFactor() {
        return upSampleFactorProperty.getValue();
    }

    public void setUpSampleFactor(int upSampleFactor) {
        upSampleFactorProperty.setStrongValue(upSampleFactor);
    }

    @Override
    protected void disposeCachedData() {
        super.disposeCachedData();

        splineXCoords.disposeItems();
        splineYCoords.disposeItems();
    }

    @Override
    protected void internalDraw(IRenderContext2D renderContext, IAssetManager2D assetManager, ISeriesRenderPassData renderPassData) {
        // Don't draw transparent series
        final float opacity = getOpacity();
        if (opacity == 0) return;

        final PenStyle strokeStyle = getStrokeStyle();
        if (strokeStyle == null || !strokeStyle.isVisible()) return;

        final LineRenderPassData currentRenderPassData = (LineRenderPassData) renderPassData;

        computeSplineSeries(splineXCoords, splineYCoords, currentRenderPassData, getIsSplineEnabled(), getUpSampleFactor());

        IDrawingContext linesStripDrawingContext = DrawingContextFactory.LINES_STRIP_DRAWING_CONTEXT;

        IPen2D pen = assetManager.createPen(strokeStyle, opacity);

        final boolean digitalLine = currentRenderPassData.isDigitalLine;
        final boolean closeGaps = currentRenderPassData.closeGaps;

        final ISeriesDrawingManager drawingManager = getServices().getService(ISeriesDrawingManager.class);
        drawingManager.beginDraw(renderContext, currentRenderPassData);

        drawingManager.iterateLines(linesStripDrawingContext, pen, splineXCoords, splineYCoords, digitalLine, closeGaps);

        drawingManager.endDraw();

        drawPointMarkers(renderContext, assetManager, currentRenderPassData.xCoords, currentRenderPassData.yCoords);
    }

    /**
     * Cubic Spline interpolation: http://www.codeproject.com/Articles/560163/Csharp-Cubic-Spline-Interpolation
     */
    private static void computeSplineSeries(FloatValues splineXCoords, FloatValues splineYCoords, LineRenderPassData currentRenderPassData, boolean isSplineEnabled, int upSampleFactor) {
        if (!isSplineEnabled) return;

        // Spline enabled
        final int size = currentRenderPassData.pointsCount();
        final int splineSize = size * upSampleFactor;

        splineXCoords.setSize(splineSize);
        splineYCoords.setSize(splineSize);

        final float[] x = currentRenderPassData.xCoords.getItemsArray();
        final float[] y = currentRenderPassData.yCoords.getItemsArray();

        final float[] xs = splineXCoords.getItemsArray();
        float stepSize = (x[size - 1] - x[0]) / (splineSize - 1);

        // set spline xCoords
        for (int i = 0; i < splineSize; i++) {
            xs[i] = x[0] + i * stepSize;
        }
        float[] ys = new float[0];

        try {
            CubicSpline cubicSpline = new CubicSpline();
            ys = cubicSpline.fitAndEval(x, y, size, xs, Float.NaN, Float.NaN, false);
        } catch (Exception e) {
            SciChartDebugLogger.instance().handleException(e);
        }

        // copy spline yCoords
        System.arraycopy(ys, 0, splineYCoords.getItemsArray(), 0, splineSize);
    }

    public static class SplineLineRenderableSeriesBuilder {
        private final DisplayMetrics displayMetrics;
        private final SplineLineRenderableSeries renderableSeries;

        public SplineLineRenderableSeriesBuilder(Context context) {
            this.displayMetrics = context.getResources().getDisplayMetrics();
            this.renderableSeries = new SplineLineRenderableSeries();
        }

        public SplineLineRenderableSeriesBuilder withDataSeries(IDataSeries dataSeries) {
            renderableSeries.setDataSeries(dataSeries);
            return this;
        }

        public SplineLineRenderableSeriesBuilder withStrokeStyle(int seriesColor, float strokeThickness, boolean antiAliasing) {
            renderableSeries.setStrokeStyle(new SolidPenStyleBuilder(displayMetrics).withThickness(strokeThickness).withColor(seriesColor).withAntiAliasing(antiAliasing).build());
            return this;
        }

        public SplineLineRenderableSeriesBuilder withPointMarker(IPointMarker pointMarker) {
            renderableSeries.setPointMarker(pointMarker);
            return this;
        }

        public SplineLineRenderableSeriesBuilder withUpSampleFactor(int upSampleFactor) {
            this.renderableSeries.setUpSampleFactor(upSampleFactor);
            return this;
        }

        public SplineLineRenderableSeriesBuilder withIsSplineEnabled(boolean isSplineEnabled) {
            this.renderableSeries.setIsSplineEnabled(isSplineEnabled);
            return this;
        }

        public SplineLineRenderableSeries build() {
            return renderableSeries;
        }
    }
}