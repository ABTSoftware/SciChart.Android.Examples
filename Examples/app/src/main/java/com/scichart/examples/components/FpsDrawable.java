//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2017. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// FpsDrawable.java is part of the SCICHART® Examples. Permission is hereby granted
// to modify, create derivative works, distribute and publish any part of this source
// code whether for commercial, private or personal use.
//
// The SCICHART® examples are distributed in the hope that they will be useful, but
// without any warranty. It is provided "AS IS" without warranty of any kind, either
// expressed or implied.
//******************************************************************************

package com.scichart.examples.components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;
import android.view.View;

import com.scichart.charting.model.dataSeries.IDataSeries;
import com.scichart.charting.visuals.SciChartSurface;
import com.scichart.charting.visuals.renderableSeries.IRenderableSeries;
import com.scichart.charting.visuals.rendering.RenderedMessage;
import com.scichart.core.common.Action1;
import com.scichart.core.utility.SciChartDebugLogger;
import com.scichart.core.utility.Stopwatch;
import com.scichart.core.utility.messaging.IEventAggregator;
import com.scichart.core.utility.messaging.MessageSubscriptionToken;
import com.scichart.examples.data.MovingAverage;

public class FpsDrawable extends Drawable {
    private Stopwatch fpsWatch = new Stopwatch();
    private MovingAverage fpsAverage = new MovingAverage(50);

    private long pointCount;
    private double currentFps;

    private final Paint paint = new Paint();
    private final Paint background = new Paint();

    private MessageSubscriptionToken token;
    private View viewToDrawOn;

    public FpsDrawable(Context context) {
        paint.setColor(Color.BLACK);
        paint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 18f, context.getResources().getDisplayMetrics()));

        background.setColor(0xAA919191);
    }

    public void setTargets(final SciChartSurface targetSurface, final View viewToDrawOn) {
        // dispose previous subscription
        if (this.token != null) {
            try {
                this.token.close();
            } catch (Exception e) {
                SciChartDebugLogger.instance().handleException(e);
            } finally {
                this.token = null;
            }
        }

        // remove overlay from previous view
        if (this.viewToDrawOn != null) {
            this.viewToDrawOn.getOverlay().remove(this);
        }

        this.viewToDrawOn = viewToDrawOn;

        if (targetSurface != null && viewToDrawOn != null) {
            // attach overlay to new view
            this.viewToDrawOn.getOverlay().add(this);

            // subscribe on target surface RenderedMessage
            final IEventAggregator eventAggregator = targetSurface.getServices().getService(IEventAggregator.class);
            this.token = eventAggregator.subscribe(RenderedMessage.class, new Action1<RenderedMessage>() {
                @Override
                public void execute(RenderedMessage arg) {
                    update(targetSurface);
                    viewToDrawOn.postInvalidate();
                }
            });
        }
    }

    public final boolean hasTargets() {
        return token != null;
    }

    @Override
    public void draw(Canvas canvas) {
        final String text = String.format("FPS: %.0f   Point Count: %d", currentFps, pointCount);
        final float width = paint.measureText(text);
        final float height = paint.getTextSize();

        canvas.drawRect(5, 5, width+10, height+10, background);
        canvas.drawText(text, 5, height+5, paint);
    }

    private void update(SciChartSurface targetSurface){
        fpsWatch.stop();

        double fps = 1000d / (double) fpsWatch.getElapsedTime();
        double fpsAverageBefore = fpsAverage.getCurrent();

        if (!Double.isInfinite(fps)) {
            fpsAverage.push(fps);
        }

        double fpsAverageAfter = fpsAverage.getCurrent();

        if (Math.abs(fpsAverageAfter - fpsAverageBefore) >= 0.1) {
            currentFps = fpsAverage.getCurrent();
        }

        pointCount = calculatePointCount(targetSurface);
        fpsWatch.reset();
        fpsWatch.start();
    }

    private long calculatePointCount(SciChartSurface sciChartSurface) {
        long result = 0;
        for (IRenderableSeries renderableSeries : sciChartSurface.getRenderableSeries()) {
            IDataSeries dataSeries = renderableSeries.getDataSeries();
            result += dataSeries.getCount();
        }
        return result;
    }

    public void setColor(int color){
        paint.setColor(color);
    }

    @Override
    public void setAlpha(int alpha) {
        paint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
        paint.setColorFilter(cf);
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }
}
