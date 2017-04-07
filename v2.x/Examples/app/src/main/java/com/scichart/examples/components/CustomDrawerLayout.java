//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2017. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// CustomDrawerLayout.java is part of the SCICHART® Examples. Permission is hereby granted
// to modify, create derivative works, distribute and publish any part of this source
// code whether for commercial, private or personal use.
//
// The SCICHART® examples are distributed in the hope that they will be useful, but
// without any warranty. It is provided "AS IS" without warranty of any kind, either
// expressed or implied.
//******************************************************************************

package com.scichart.examples.components;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

import com.scichart.core.common.Action1;
import com.scichart.examples.R;
import com.scichart.examples.components.SideMenuAnimations.ISideMenuItem;
import com.scichart.examples.components.SideMenuAnimations.IViewAnimatorListener;
import com.scichart.examples.components.SideMenuAnimations.SideMenuItem;
import com.scichart.examples.components.SideMenuAnimations.ViewAnimator;
import com.scichart.examples.utils.widgetgeneration.SeparatorWidget;
import com.scichart.examples.utils.widgetgeneration.Widget;

import java.util.ArrayList;
import java.util.List;

public class CustomDrawerLayout extends DrawerLayout implements IViewAnimatorListener {

    private final List<SideMenuItem> sideMenuItemsList = new ArrayList<>();
    private final SeparatorWidget separator = new SeparatorWidget.Builder().build();

    private ViewAnimator viewAnimator;
    private LinearLayout rightDrawerPanel;
    private Action1<Boolean> setViewsClickableAction;

    public CustomDrawerLayout(Context context) {
        this(context, null);
    }

    public CustomDrawerLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomDrawerLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        setScrimColor(Color.TRANSPARENT);
    }

    public boolean isAnimating() {
        return viewAnimator.isAnimating();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle((Activity) getContext(), this, R.string.toolbar_drawer_open, R.string.toolbar_drawer_close) {
            @Override
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                rightDrawerPanel.removeAllViews();
                rightDrawerPanel.invalidate();
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                if (slideOffset > 0.6 && !viewAnimator.isAnimating()) {
                    if (!isDrawerOpen(Gravity.RIGHT)) {
                        viewAnimator.showMenuContent();
                    }
                }
            }
        };
        addDrawerListener(drawerToggle);

        rightDrawerPanel = (LinearLayout) findViewById(R.id.right_drawer);
        viewAnimator = new ViewAnimator<>(sideMenuItemsList, this, this);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return super.onInterceptTouchEvent(ev) && !viewAnimator.isAnimating();
    }

    public void onMenuItemClick() {
        if (!viewAnimator.isAnimating()) {
            if (isDrawerOpen(Gravity.RIGHT)) {
                viewAnimator.hideMenuContent();
            } else {
                openDrawer(Gravity.RIGHT);
            }
        }
    }

    public void setViewsClickableAction(Action1<Boolean> setViewsClickableAction) {
        this.setViewsClickableAction = setViewsClickableAction;
    }

    public void addMenuItem(Widget widget) {
        sideMenuItemsList.add(new SideMenuItem(widget.createView(getContext()), widget.getOnClickListener()));
    }

    public void addMenuSeparator() {
        sideMenuItemsList.add(new SideMenuItem(separator.createView(getContext()), null));
    }

    public void clearMenuItems() {
        sideMenuItemsList.clear();
    }

    @Override
    public void setViewsClickable(boolean isEnabled) {
        setViewsClickableAction.execute(isEnabled);
    }

    @Override
    public void addViewToContainer(View view) {
        rightDrawerPanel.addView(view);
    }

    @Override
    public void onMenuItemSelected(ISideMenuItem sideMenuItem) {
        sideMenuItem.onClick();
    }
}