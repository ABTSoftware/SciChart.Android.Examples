//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2021. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// CreateCustomGestureModifierFragment.java is part of SCICHART®, High Performance Scientific Charts
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

package com.scichart.examples.fragments.examples2d.createCustomCharts;

import android.content.Context;
import android.graphics.PointF;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.view.animation.DecelerateInterpolator;

import androidx.annotation.NonNull;

import com.scichart.charting.model.dataSeries.IXyDataSeries;
import com.scichart.charting.modifiers.GestureModifierBase;
import com.scichart.charting.visuals.SciChartSurface;
import com.scichart.charting.visuals.annotations.AnnotationCoordinateMode;
import com.scichart.charting.visuals.annotations.HorizontalAnchorPoint;
import com.scichart.charting.visuals.annotations.TextAnnotation;
import com.scichart.charting.visuals.annotations.VerticalAnchorPoint;
import com.scichart.charting.visuals.axes.IAxis;
import com.scichart.charting.visuals.pointmarkers.EllipsePointMarker;
import com.scichart.charting.visuals.renderableSeries.FastImpulseRenderableSeries;
import com.scichart.core.IServiceContainer;
import com.scichart.core.framework.UpdateSuspender;
import com.scichart.core.utility.touch.ModifierTouchEventArgs;
import com.scichart.drawing.utility.ColorUtil;
import com.scichart.examples.data.DataManager;
import com.scichart.examples.data.DoubleSeries;
import com.scichart.examples.fragments.base.ExampleSingleChartBaseFragment;
import com.scichart.examples.utils.Constant;
import com.scichart.examples.utils.interpolator.DefaultInterpolator;

import java.util.Collections;

public class CreateCustomGestureModifierFragment extends ExampleSingleChartBaseFragment {

    @Override
    protected void initExample(@NonNull SciChartSurface surface) {
        final IAxis xBottomAxis = sciChartBuilder.newNumericAxis().withGrowBy(0.1d, 0.1d).build();
        final IAxis yRightAxis = sciChartBuilder.newNumericAxis().withGrowBy(0.1d, 0.1d).build();

        final DoubleSeries ds1Points = DataManager.getInstance().getDampedSinewave(1.0, 0.05, 50, 5);
        final IXyDataSeries<Double, Double> dataSeries = sciChartBuilder.newXyDataSeries(Double.class, Double.class).build();
        dataSeries.append(ds1Points.xValues, ds1Points.yValues);

        final EllipsePointMarker pointMarker = sciChartBuilder.newPointMarker(new EllipsePointMarker())
                .withSize(10, 10)
                .withStroke(0xFFe97064, 1)
                .withFill(0xFFe97064)
                .build();

        final FastImpulseRenderableSeries rSeries = sciChartBuilder.newImpulseSeries()
                .withDataSeries(dataSeries)
                .withStrokeStyle(0xFFe97064)
                .withPointMarker(pointMarker)
                .build();

        final TextAnnotation annotation = sciChartBuilder.newTextAnnotation()
                .withText("Double Tap and pan vertically to Zoom In/Out.\nDouble tap to Zoom Extents.")
                .withX1(0.5)
                .withY1(0.0)
                .withCoordinateMode(AnnotationCoordinateMode.Relative)
                .withVerticalAnchorPoint(VerticalAnchorPoint.Top)
                .withHorizontalAnchorPoint(HorizontalAnchorPoint.Center)
                .build();

        UpdateSuspender.using(surface, () -> {
            Collections.addAll(surface.getXAxes(), xBottomAxis);
            Collections.addAll(surface.getYAxes(), yRightAxis);
            Collections.addAll(surface.getRenderableSeries(), rSeries);
            Collections.addAll(surface.getAnnotations(), annotation);
            Collections.addAll(surface.getChartModifiers(), new CustomZoomGestureModifier());

            sciChartBuilder.newAnimator(rSeries).withWaveTransformation().withInterpolator(DefaultInterpolator.getInterpolator()).withDuration(Constant.ANIMATION_DURATION).withStartDelay(Constant.ANIMATION_START_DELAY).start();
        });
    }

    private static class CustomZoomGestureModifier extends GestureModifierBase {
        private boolean isScrolling = false;
        private boolean isZoomEnabled = false;

        private float touchSlop;
        private final PointF start = new PointF();
        private float lastY;

        @Override
        public void attachTo(IServiceContainer services) {
            super.attachTo(services);

            final Context context = getContext();
            if (context == null) return;

            this.touchSlop = ViewConfiguration.get(context).getScaledTouchSlop() * 2;
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            start.set(e.getX(), e.getY());
            lastY = e.getY();
            isZoomEnabled = true;

            return true;
        }

        @Override
        public void onTouch(ModifierTouchEventArgs args) {
            super.onTouch(args);

            final MotionEvent motionEvent = args.e;
            if (isZoomEnabled && motionEvent.getAction() == MotionEvent.ACTION_MOVE) {
                onScrollInYDirection(motionEvent.getY());
            }
        }

        private void onScrollInYDirection(float y) {
            final float distance = Math.abs(y - start.y);
            if (distance < touchSlop || Math.abs(y - lastY) < 1f) return;

            this.isScrolling = true;

            final float prevDistance = Math.abs(lastY - start.y);
            final double diff = prevDistance > 0 ? distance / prevDistance - 1 : 0;

            growBy(start, getXAxis(), diff);

            this.lastY = y;
        }

        // zoom axis relative to the start point using fraction
        private void growBy(PointF point, IAxis axis, double fraction) {
            final int size = axis.getAxisViewportDimension();
            final float coord = size - point.y;

            double minFraction = (coord / size) * fraction;
            double maxFraction = (1 - coord / size) * fraction;

            axis.zoomBy(minFraction, maxFraction);
        }

        @Override
        protected void onUp(MotionEvent e) {
            // need to disable zoom after finishing scrolling
            if (isScrolling) {
                isScrolling = isZoomEnabled = false;
                start.set(Float.NaN, Float.NaN);
                lastY = Float.NaN;
            }
        }
    }
}
