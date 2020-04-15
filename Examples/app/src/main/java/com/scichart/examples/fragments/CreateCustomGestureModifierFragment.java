//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2020. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// CreateCustomGestureModifier.java is part of the SCICHART® Examples. Permission is hereby granted
// to modify, create derivative works, distribute and publish any part of this source
// code whether for commercial, private or personal use.
//
// The SCICHART® examples are distributed in the hope that they will be useful, but
// without any warranty. It is provided "AS IS" without warranty of any kind, either
// expressed or implied.
//******************************************************************************

package com.scichart.examples.fragments;

import android.content.Context;
import android.graphics.PointF;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.view.animation.DecelerateInterpolator;

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
import com.scichart.examples.R;
import com.scichart.examples.data.DataManager;
import com.scichart.examples.data.DoubleSeries;
import com.scichart.examples.fragments.base.ExampleBaseFragment;

import java.util.Collections;

import butterknife.BindView;

public class CreateCustomGestureModifierFragment extends ExampleBaseFragment {
    @Override
    protected int getLayoutId() {
        return R.layout.example_single_chart_fragment;
    }

    @BindView(R.id.chart)
    SciChartSurface surface;

    @Override
    protected void initExample() {
        final IAxis xBottomAxis = sciChartBuilder.newNumericAxis().withGrowBy(0.1d, 0.1d).build();
        final IAxis yRightAxis = sciChartBuilder.newNumericAxis().withGrowBy(0.1d, 0.1d).build();

        final DoubleSeries ds1Points = DataManager.getInstance().getDampedSinewave(1.0, 0.05, 50, 5);
        final IXyDataSeries<Double, Double> dataSeries = sciChartBuilder.newXyDataSeries(Double.class, Double.class).build();
        dataSeries.append(ds1Points.xValues, ds1Points.yValues);

        final EllipsePointMarker pointMarker = sciChartBuilder.newPointMarker(new EllipsePointMarker())
                .withSize(10, 10)
                .withStroke(ColorUtil.argb(0xFF, 0x00, 0x66, 0xFF), 1)
                .withFill(ColorUtil.argb(0xFF, 0x00, 0x66, 0xFF))
                .build();

        final FastImpulseRenderableSeries rSeries = sciChartBuilder.newImpulseSeries()
                .withDataSeries(dataSeries)
                .withXAxisId(xBottomAxis.getAxisId())
                .withYAxisId(yRightAxis.getAxisId())
                .withStrokeStyle(ColorUtil.argb(0xFF, 0x00, 0x66, 0xFF))
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

        UpdateSuspender.using(surface, new Runnable() {
            @Override
            public void run() {
                Collections.addAll(surface.getXAxes(), xBottomAxis);
                Collections.addAll(surface.getYAxes(), yRightAxis);
                Collections.addAll(surface.getRenderableSeries(), rSeries);
                Collections.addAll(surface.getAnnotations(), annotation);
                Collections.addAll(surface.getChartModifiers(), new CustomZoomGestureModifier());

                sciChartBuilder.newAnimator(rSeries).withWaveTransformation().withInterpolator(new DecelerateInterpolator()).withDuration(3000).withStartDelay(350).start();
            }
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

            if(context == null) return;

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
            if(isZoomEnabled  && motionEvent.getAction() == MotionEvent.ACTION_MOVE) {
                onScrollInYDirection(motionEvent.getY());
            }
        }

        private void onScrollInYDirection(float y) {
            final float distance = Math.abs(y - start.y);

            if(distance < touchSlop || Math.abs(y - lastY) < 1f) return;

            this.isScrolling = true;

            final float prevDistance = Math.abs(lastY - start.y);
            final double diff = prevDistance > 0 ? distance/prevDistance - 1 : 0;

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
            if(isScrolling) {
                isScrolling = isZoomEnabled = false;
                start.set(Float.NaN, Float.NaN);
                lastY = Float.NaN;
            }
        }
    }
}
