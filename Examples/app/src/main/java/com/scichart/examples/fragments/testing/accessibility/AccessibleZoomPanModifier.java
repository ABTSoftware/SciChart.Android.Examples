//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2019. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// AccessibleZoomPanModifier.java is part of the SCICHART® Examples. Permission is hereby granted
// to modify, create derivative works, distribute and publish any part of this source
// code whether for commercial, private or personal use.
//
// The SCICHART® examples are distributed in the hope that they will be useful, but
// without any warranty. It is provided "AS IS" without warranty of any kind, either
// expressed or implied.
//******************************************************************************

package com.scichart.examples.fragments.testing.accessibility;

import android.view.MotionEvent;

import com.scichart.charting.modifiers.ZoomPanModifier;

public class AccessibleZoomPanModifier extends ZoomPanModifier {
    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float xDelta, float yDelta) {
        getParentSurface().getView().announceForAccessibility("Scrolling chart");
        return super.onScroll(e1, e2, xDelta, yDelta);
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        getParentSurface().getView().announceForAccessibility("Scrolling chart");
        return super.onFling(e1, e2, velocityX, velocityY);
    }
}
