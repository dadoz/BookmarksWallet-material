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
import com.application.material.bookmarkswallet.app.manager.StatusManager;

import java.lang.ref.WeakReference;

public class SearchResultPresenter implements ViewTreeObserver.OnGlobalLayoutListener {
    private static final long MIN_DELAY = 1000;
    private static View resultLayout;
    private static View mainLayout;
    private static View frameLayout;
    private static WeakReference<Context> context;
    private final int yellow;
    private final int grey;
    private int viewHeight = 0;
    private View searchCardViewLayout;
    private View searchButton;

    public SearchResultPresenter(WeakReference<Context> ctx) {
        context = ctx;
        yellow = ContextCompat.getColor(context.get(), R.color.yellow_400);
        grey = ContextCompat.getColor(context.get(), R.color.grey_50);
    }

    /**
     *
     */
    public void showResultView() {
        searchButton.setVisibility(View.GONE);
        animateViews(true);

    }

    /**
     *
     */
    public void hideResultView() {
        showSearchView(true);
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
        searchCardViewLayout = mainLayout.findViewById(R.id.addBookmarkCardLayoutId);
        searchButton = mainLayout.findViewById(R.id.addBookmarkSearchButtonId);
        setMinHeight();
    }

    /**
     *
     */
    private void setMinHeight() {
        frameLayout.getViewTreeObserver().addOnGlobalLayoutListener(this);
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
                .getYTranslation(searchCardViewLayout, startY, endY, 0);
    }

    /**
     *
     * @param slideToTop
     */
    public Animator slideToTopResultLayout(boolean slideToTop) {
        int startY = slideToTop ? viewHeight : 0;
        int endY = slideToTop? 0 : viewHeight;
        Animator animator = AnimatorBuilder.getInstance(context).getYTranslation(resultLayout, startY, endY, 0);
        animator.setStartDelay(slideToTop ? MIN_DELAY : 0);
        return animator;
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
        int startColor = isToBeColored ? grey : yellow;
        int endColor = isToBeColored ? yellow : grey;
        return AnimatorBuilder.getInstance(context).buildColorAnimator(frameLayout, startColor, endColor);
    }

    @Override
    public void onGlobalLayout() {
        boolean isViewHeightToBeUpdate = viewHeight == 0 ||
                viewHeight < frameLayout.getMeasuredHeight();
        if (isViewHeightToBeUpdate) {
            viewHeight = frameLayout.getMeasuredHeight();
            if (StatusManager.getInstance().isOnResultMode()) {
                showResultOnConfigChanged();
                return;
            }
            setInitialPosition(resultLayout, viewHeight);
        }

    }

    /**
     *
     */
    private void showResultOnConfigChanged() {
        setInitialPosition(searchCardViewLayout, viewHeight);
        setInitialPosition(resultLayout, 0);
        frameLayout.setBackgroundColor(yellow);
        searchButton.setVisibility(View.GONE);
        showSearchView(false);
    }

    /**
     *
     * @param isVisible
     */
    private void showSearchView(boolean isVisible) {
        searchCardViewLayout.setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }
}
