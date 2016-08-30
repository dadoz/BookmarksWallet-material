package com.application.material.bookmarkswallet.app.presenter;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewTreeObserver;

import com.application.material.bookmarkswallet.app.R;
import com.application.material.bookmarkswallet.app.animator.AnimatorBuilder;

import java.lang.ref.WeakReference;

public class SearchResultPresenter {
    private static View resultLayout;
    private static View mainLayout;
    private static View frameLayout;
    private static WeakReference<Context> context;
    private int viewHeight = 0;

    public SearchResultPresenter(WeakReference<Context> ctx) {
        context = ctx;
    }

    /**
     *
     */
    public void showResultView() {
        mainLayout.findViewById(R.id.addBookmarkSearchButtonId).setVisibility(View.GONE);
        animateViews(true);

    }

    /**
     *
     */
    public void hideResultView() {
        animateViews(false);
    }

    /**
     *
     */
    private void animateViews(boolean isShowing) {
        Animator animator1 = slideToBottomMainLayout(isShowing);
        Animator animator2 = slideToTopResultLayout(isShowing);
        ValueAnimator animator3 = setBackgroundColorToFrameLayout(isShowing);

        AnimatorSet animatorSet = new AnimatorSet();
        if (isShowing) {
            animatorSet.playSequentially(animator3, animator1, animator2);
            animatorSet.start();
            return;
        }

        animatorSet.playSequentially(animator2, animator1, animator3);
        animatorSet.start();
    }

    /**
     *
     * @param views
     */
    public void init(View[] views) {
        mainLayout = views[0];
        resultLayout = views[1];
        frameLayout = views[2];
        setMinHeight();
    }

    /**
     *
     */
    private void setMinHeight() {
        final ViewTreeObserver.OnGlobalLayoutListener victim = new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (viewHeight == 0 ||
                        viewHeight < frameLayout.getMeasuredHeight()) {
                    viewHeight = frameLayout.getMeasuredHeight();
                    setInitialPosition(resultLayout, viewHeight);
                }
            }
        };
        frameLayout.getViewTreeObserver().addOnGlobalLayoutListener(victim);
    }

    /**
     *
     * @param isSlidingBottom
     * @return
     */
    private Animator slideToBottomMainLayout(boolean isSlidingBottom) {
        int startY = isSlidingBottom ? 0 : viewHeight;
        int endY = isSlidingBottom ? viewHeight : 0;
        return AnimatorBuilder.getInstance(context)
                .getYTranslation(mainLayout.findViewById(R.id.addBookmarkCardLayoutId), startY, endY, 0);
    }

    /**
     *
     * @param slideToTop
     */
    public Animator slideToTopResultLayout(boolean slideToTop) {
        int startY = slideToTop ? viewHeight : 0;
        int endY = slideToTop? 0 : viewHeight;
        return AnimatorBuilder.getInstance(context).getYTranslation(resultLayout, startY, endY, 0);

    }

    /**
     *
     * @param view
     * @param offset
     */
    void setInitialPosition(View view, int offset) {
        view.setTranslationY(offset);
    }

    /**
     *
     * @param isToBeColored
     */
    public ValueAnimator setBackgroundColorToFrameLayout(boolean isToBeColored) {
        int yellow = ContextCompat.getColor(context.get(), R.color.yellow_400);
        int grey = ContextCompat.getColor(context.get(), R.color.grey_50);
        int startColor = isToBeColored ? grey : yellow;
        int endColor = isToBeColored ? yellow : grey;
        return AnimatorBuilder.getInstance(context).buildColorAnimator(frameLayout, startColor, endColor);
    }
}
