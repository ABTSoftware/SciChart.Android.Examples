//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2019. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// AccessiblePinchZoomModifier.java is part of the SCICHART® Examples. Permission is hereby granted
// to modify, create derivative works, distribute and publish any part of this source
// code whether for commercial, private or personal use.
//
// The SCICHART® examples are distributed in the hope that they will be useful, but
// without any warranty. It is provided "AS IS" without warranty of any kind, either
// expressed or implied.
//******************************************************************************

package com.scichart.examples.fragments.testing.accessibility;

import android.view.ScaleGestureDetector;

import com.scichart.charting.modifiers.PinchZoomModifier;

public class AccessiblePinchZoomModifier extends PinchZoomModifier {

    @Override
    public boolean onScaleBegin(ScaleGestureDetector detector) {
        getParentSurface().getView().announceForAccessibility("Begin pinch zoom scaling");

        return super.onScaleBegin(detector);
    }

    @Override
    public void onScaleEnd(ScaleGestureDetector detector) {
        getParentSurface().getView().announceForAccessibility("End pinch zoom scaling");

        super.onScaleEnd(detector);
    }
}
