/*
 * Copyright (C) ${YEAR} Antonio Leiva
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.application.material.bookmarkswallet.app.animators;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import com.application.material.bookmarkswallet.app.R;
import com.application.material.bookmarkswallet.app.singleton.ActionbarSingleton;
import com.github.clans.fab.FloatingActionMenu;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.HashMap;

public class ScrollManager extends RecyclerView.OnScrollListener {

    private static final int MIN_SCROLL_TO_HIDE = 10;
    private final Activity mActivityRef;
    private final ActionbarSingleton mActionbarSingleton;
    private final int mAdsOffsetHeight;
    //    private final View infoView;
    private boolean hidden;
    private int accummulatedDy;
    private int totalDy;
    private int initialOffset;
    private HashMap<View, Direction> viewsToHide = new HashMap<View, Direction>();
    private HashMap<View, Direction> viewsNotToShow = new HashMap<View, Direction>();
    private View parentView;
    private View viewToBeMeasured;

    public static enum Direction {UP, DOWN}

    public ScrollManager(Activity activityRef, int adsOffsetHeight) {
        mActivityRef = activityRef;
        mActionbarSingleton = ActionbarSingleton.getInstance(mActivityRef);
        mAdsOffsetHeight = adsOffsetHeight;
//        infoView = view;
    }

    public void attach(RecyclerView recyclerView) {
        recyclerView.setOnScrollListener(this);
    }

    public void addView(View view, Direction direction) {
        viewsToHide.put(view, direction);
    }

    public void addViewNoDown(View view, Direction direction) {
        viewsNotToShow.put(view, direction);
    }

    public void setInitialOffset(int initialOffset) {
        this.initialOffset = initialOffset;
    }

    @Override public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        totalDy += dy;
        if (mActionbarSingleton.isEditMode() ||
                mActionbarSingleton.isSearchMode()) {
            return;
        }

        if (totalDy < initialOffset) {
            return;
        }

        if (dy > 0) {
            accummulatedDy = accummulatedDy > 0 ? accummulatedDy + dy : dy;
            if (accummulatedDy > MIN_SCROLL_TO_HIDE) {
                hideViews();
            }
        } else if (dy < 0) {
            accummulatedDy = accummulatedDy < 0 ? accummulatedDy + dy : dy;
            if (accummulatedDy < -MIN_SCROLL_TO_HIDE) {
                showViews();
            }
        }
    }

    public void hideViews() {
        if (!hidden) {
            hidden = true;
            for (View view : viewsToHide.keySet()) {
                hideView(view, viewsToHide.get(view));
            }
        }
    }

//    private void hideViewWithoutAnimation(View view) {
//        view.setVisibility(View.GONE);
//    }

    private void showViews() {
        if (hidden) {
            hidden = false;
            for (View view : viewsToHide.keySet()) {
                showView(view);
            }
        }
    }

    private void hideView(View view, Direction direction) {
        int offset = 0;
        switch (view.getId()) {
            case R.id.slidingLayerLayoutId:
                ((SlidingUpPanelLayout) view).setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
                return;
            case R.id.adViewId:
                offset = mAdsOffsetHeight;
                break;
        }

        //hide view
        int height = calculateTranslation(view);
        int translateY = direction == Direction.UP ? -height : height;
        runTranslateAnimation(view, translateY - offset, new AccelerateInterpolator(3));
//        runTranslateAnimationWrapper(view, direction, offset);
    }

    private void showView(View view) {
        switch (view.getId()) {
            case R.id.slidingLayerLayoutId:
                ((SlidingUpPanelLayout) view).setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                return;
        }

        runTranslateAnimation(view, 0, new DecelerateInterpolator(3));
    }

    public static int getTranslateY(View view , Direction direction, int offset) {
        int height = calculateTranslation(view);
        int translateY = direction == Direction.UP ? -height : height;
        return translateY - offset;
    }

    /**
     * Takes height + margins
     * @param view View to translate
     * @return translation in pixels
     */
    private static int calculateTranslation(View view) {
        int height = view.getHeight();

        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
        int margins = params.topMargin + params.bottomMargin;

        return height + margins;
    }

    private static int getDelay(int viewId, int translateY) {
        if (viewId == R.id.adViewId) {
            return 150;
        }

        return translateY == 0 ? 300 : 0;
    }

    public static void runTranslateAnimation(View view, final int translateY, Interpolator interpolator) {
        int delay = getDelay(view.getId(), translateY);
        Animator slideInAnimation = ObjectAnimator.ofFloat(view, "translationY", translateY);
        slideInAnimation.setDuration(view.getContext().getResources().getInteger(android.R.integer.config_mediumAnimTime));
        slideInAnimation.setInterpolator(interpolator);
        slideInAnimation.setStartDelay(delay);
        slideInAnimation.start();
/*        final int[] ended = new int[1];
        ended[0] = 1;

        slideInAnimation.addListener(new Animator.AnimatorListener() {

            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                boolean hideView = translateY != 0;

                if ((hideView && infoView.getVisibility() == View.GONE) ||
                        (! hideView && infoView.getVisibility() == View.VISIBLE)) {
                    return;
                }

                Log.e("TAG", "animation");
                infoView.setVisibility(View.GONE);
                infoView.setTop(infoView.getHeight());
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });*/
    }


}
