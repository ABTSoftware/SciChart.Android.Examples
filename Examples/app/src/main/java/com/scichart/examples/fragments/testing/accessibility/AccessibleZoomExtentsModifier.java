//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2019. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// AccessibleZoomExtentsModifier.java is part of the SCICHART® Examples. Permission is hereby granted
// to modify, create derivative works, distribute and publish any part of this source
// code whether for commercial, private or personal use.
//
// The SCICHART® examples are distributed in the hope that they will be useful, but
// without any warranty. It is provided "AS IS" without warranty of any kind, either
// expressed or implied.
//******************************************************************************

package com.scichart.examples.fragments.testing.accessibility;

import android.view.View;

import com.scichart.charting.modifiers.ZoomExtentsModifier;

public class AccessibleZoomExtentsModifier extends ZoomExtentsModifier {
    @Override
    protected void performZoom() {
        final View view = getParentSurface().getView();

        view.announceForAccessibility("Performing zoom extents");

        super.performZoom();
    }
}
