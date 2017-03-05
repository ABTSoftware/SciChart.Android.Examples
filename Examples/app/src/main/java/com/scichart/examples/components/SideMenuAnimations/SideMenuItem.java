//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2017. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// SideMenuItem.java is part of the SCICHART® Examples. Permission is hereby granted
// to modify, create derivative works, distribute and publish any part of this source
// code whether for commercial, private or personal use.
//
// The SCICHART® examples are distributed in the hope that they will be useful, but
// without any warranty. It is provided "AS IS" without warranty of any kind, either
// expressed or implied.
//******************************************************************************

package com.scichart.examples.components.SideMenuAnimations;

import android.view.View;

public class SideMenuItem implements ISideMenuItem {
    private final View view;
    private final View.OnClickListener onClickListener;

    public SideMenuItem(View view, View.OnClickListener onClickListener) {
        this.view = view;
        this.onClickListener = onClickListener;
    }

    public View getView() {
        return this.view;
    }

    public void onClick() {
        if (this.onClickListener != null) {
            this.onClickListener.onClick(this.view);
        }
    }
}
