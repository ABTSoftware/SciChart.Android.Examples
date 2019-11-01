//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2019. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// AccessibilityHelper.java is part of the SCICHART® Examples. Permission is hereby granted
// to modify, create derivative works, distribute and publish any part of this source
// code whether for commercial, private or personal use.
//
// The SCICHART® examples are distributed in the hope that they will be useful, but
// without any warranty. It is provided "AS IS" without warranty of any kind, either
// expressed or implied.
//******************************************************************************

package com.scichart.examples.fragments.testing.accessibility;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat;
import androidx.customview.widget.ExploreByTouchHelper;
import android.view.View;

import com.scichart.charting.visuals.SciChartSurface;

import java.util.ArrayList;
import java.util.List;

public class AccessibilityHelper extends ExploreByTouchHelper {
    public final ArrayList<INode> nodes = new ArrayList<>();

    public AccessibilityHelper(@NonNull SciChartSurface surface) {
        super(surface);
    }

    @Override
    protected int getVirtualViewAt(float x, float y) {
        for (int i = 0, size = nodes.size(); i < size; i++) {
            final INode node = nodes.get(i);

            if(node.contains(x, y)) {
                return node.getId();
            }
        }

        return View.NO_ID;
    }

    @Override
    protected void getVisibleVirtualViews(List<Integer> virtualViewIds) {
        for (int i = 0, size = nodes.size(); i < size; i++) {
            final INode node = nodes.get(i);

            virtualViewIds.add(node.getId());
        }
    }

    @Override
    protected void onPopulateNodeForVirtualView(int virtualViewId, @NonNull AccessibilityNodeInfoCompat accessibilityNodeInfoCompat) {
        for (int i = 0, size = nodes.size(); i < size; i++) {
            final INode node = nodes.get(i);
            if(node.getId() == virtualViewId) {
                node.initAccessibilityNodeInfo(accessibilityNodeInfoCompat.unwrap());
            }
        }
    }

    @Override
    protected boolean onPerformActionForVirtualView(int virtualViewId, int action, @Nullable Bundle arguments) {
        // no need to override this without using some custom actions
        return false;
    }
}
