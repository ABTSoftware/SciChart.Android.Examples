//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2017. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// ViewAnimator.java is part of the SCICHART® Examples. Permission is hereby granted
// to modify, create derivative works, distribute and publish any part of this source
// code whether for commercial, private or personal use.
//
// The SCICHART® examples are distributed in the hope that they will be useful, but
// without any warranty. It is provided "AS IS" without warranty of any kind, either
// expressed or implied.
//******************************************************************************

package com.scichart.examples.components.SideMenuAnimations;

import android.os.Handler;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;

import com.scichart.examples.components.CustomDrawerLayout;

import java.util.ArrayList;
import java.util.List;

public class ViewAnimator<T extends ISideMenuItem> {
    private final int ANIMATION_DURATION = 200;

    private List<T> menuItemsList;

    private List<View> viewList = new ArrayList<>();
    private CustomDrawerLayout drawerLayout;
    private IViewAnimatorListener animatorListener;

    private volatile boolean isAnimating;

    public ViewAnimator(List<T> items, final CustomDrawerLayout drawerLayout, IViewAnimatorListener animatorListener) {
        this.menuItemsList = items;
        this.drawerLayout = drawerLayout;
        this.animatorListener = animatorListener;
    }

    public boolean isAnimating() {
        return isAnimating;
    }

    public void showMenuContent() {
        isAnimating = true;
        setViewsClickable(!isAnimating);

        viewList.clear();
        double size = menuItemsList.size();

        for (int i = 0; i < size; i++) {
            final int finalI = i;
            ISideMenuItem sideMenuItem = menuItemsList.get(i);
            View menuItemView = sideMenuItem.getView();

            menuItemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!isAnimating && !drawerLayout.isAnimating()) {
                        animatorListener.onMenuItemSelected(menuItemsList.get(finalI));
                        hideMenuContent();
                    }
                }
            });

            menuItemView.setVisibility(View.INVISIBLE);

            viewList.add(menuItemView);
            animatorListener.addViewToContainer(menuItemView);

            final double position = i;
            final double delay = 3 * ANIMATION_DURATION * (position / size);

            new Handler().postDelayed(new Runnable() {
                public void run() {
                    if (position < viewList.size()) {
                        animateView((int) position);
                    }
                    if (position == viewList.size() - 1) {
                        isAnimating = false;
                        setViewsClickable(!isAnimating);
                    }
                }
            }, (long) delay);
        }
    }

    private void animateView(int position) {
        final View view = viewList.get(position);
        view.setVisibility(View.VISIBLE);

        FlipAnimation rotation = createFlipAnimation(-90, 0, view.getWidth(), view.getHeight() / 2.0f);
        rotation.setAnimationListener(new FlipAnimationsListenerBase(view));

        view.startAnimation(rotation);
    }

    public void hideMenuContent() {
        animatorListener.setViewsClickable(false);
        setViewsClickable(false);

        double size = menuItemsList.size();
        for (int i = menuItemsList.size(); i >= 0; i--) {
            final double position = i;
            final double delay = 3 * ANIMATION_DURATION * (position / size);
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    if (position < viewList.size()) {
                        animateHideView((int) position);
                    }
                }
            }, (long) delay);
        }
    }

    private void setViewsClickable(boolean clickable) {
        animatorListener.setViewsClickable(clickable);
        for (View view : viewList) {
            view.setEnabled(clickable);
        }
    }

    private void animateHideView(final int position) {
        final View view = viewList.get(position);

        FlipAnimation rotation = createFlipAnimation(0, -90, view.getWidth(), view.getHeight() / 2.0f);
        rotation.setAnimationListener(new FlipAnimationsListenerBase(view) {
            @Override
            public void onAnimationEnd(Animation animation) {
                super.onAnimationEnd(animation);

                view.setVisibility(View.INVISIBLE);
                if (position == viewList.size() - 1) {
                    animatorListener.setViewsClickable(true);
                    drawerLayout.closeDrawers();
                }
            }
        });

        view.startAnimation(rotation);
    }

    private FlipAnimation createFlipAnimation(float fromDegrees, float toDegrees, float centerX, float centerY) {
        FlipAnimation animation = new FlipAnimation(fromDegrees, toDegrees, centerX, centerY);
        animation.setDuration(ANIMATION_DURATION);
        animation.setFillAfter(true);
        animation.setInterpolator(new AccelerateInterpolator());

        return animation;
    }
}