package com.application.material.bookmarkswallet.app.presenter;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.RelativeLayout;

import com.application.material.bookmarkswallet.app.R;
import com.application.material.bookmarkswallet.app.animator.AnimatorBuilder;

import java.lang.ref.WeakReference;

public class SearchResultPresenter {
    private static View resultLayout;
    private static View mainLayout;
    private static View frameLayout;
    private static WeakReference<Context> context;
    private int viewHeight;

    public SearchResultPresenter(WeakReference<Context> ctx) {
        context = ctx;
    }

    /**
     *
     */
    public void showResultView() {
        mainLayout.findViewById(R.id.addBookmarkSearchButtonId).setVisibility(View.GONE);

        Animator animator1 = slideToBottomMainLayout(true);
        Animator animator2 = slideToTopResultLayout(true);
        ValueAnimator animator3 = setBackgroundColorToFrameLayout(true);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playSequentially(animator3, animator1, animator2);
        animatorSet.start();
    }



    /**
     *
     * @param views
     */
    public void hideResultView(View[] views) {
        init(views);
    }

    /**
     *
     * @param views
     */
    public void init(View[] views) {
        //mv on init
        mainLayout = views[0];
        resultLayout = views[1];
        frameLayout = views[2];
        viewHeight = 1500;//frameLayout.getHeight();
        setInitialPosition(resultLayout, viewHeight);
    }

    /**
     *
     * @param isSlidingBottom
     * @return
     */
    private Animator slideToBottomMainLayout(boolean isSlidingBottom) {
        int startY = isSlidingBottom ? 0 : viewHeight;
        int endY = isSlidingBottom ? viewHeight : 0;
        return AnimatorBuilder.getInstance(context).getYTranslation(mainLayout.findViewById(R.id.addBookmarkCardLayoutId), startY, endY, 0);
    }

    /**
     *
     * @param isMarginSet
     */
    public Animator slideToTopResultLayout(boolean isMarginSet) {
        int startY = isMarginSet ? viewHeight : 200;
        int endY = isMarginSet ? 200 : 0;
        return AnimatorBuilder.getInstance(context).getYTranslation(resultLayout, startY, endY, 0);

    }

    /**
     *
     */
    void setInitialMarginTop(View view, int margin) {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) resultLayout.getLayoutParams();
        params.topMargin = margin;
        view.setLayoutParams(params);
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
