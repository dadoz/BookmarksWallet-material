package com.application.material.bookmarkswallet.app.animator;

import android.animation.Animator;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;

/**
 * Created by davide on 24/10/15.
 */
public class AnimationBuilder {
    private static final String TRANSLATION_Y = "translationY";
    private static final String ALPHA = "alpha";
    private static final long START_DELAY = 300;
    private final int duration;

    /**
     *
     * @param context
     * @return
     */
    public static AnimationBuilder getInstance(Context context) {
        return new AnimationBuilder(context.getResources()
                .getInteger(android.R.integer.config_mediumAnimTime));
    }

    private AnimationBuilder(int duration) {
        this.duration = duration;
    }


    /**
     *
     * @param view
     * @param endY
     * @param startY
     * @param delay
     * @return
     */
    public Animator getYTranslation(@NonNull View view, float startY, float endY, int delay) {
        Animator animator = ObjectAnimator.ofFloat(view, TRANSLATION_Y, startY, endY);
        animator.setDuration(duration);
        animator.setStartDelay(delay != 0 ? START_DELAY + delay : 0);
        return animator;
    }

    /**
     *
     * @return
     * @param viewArray
     * @param isDelaySet
     */
    public Animator[] buildShowAnimator(@NonNull View[] viewArray, boolean isDelaySet) {
        Animator[] animatoArray = new Animator[viewArray.length];
        int i = 0;
        for (View view: viewArray) {
            animatoArray[i] = buildShowAnimator(view, isDelaySet);
            i++;
        }
        return animatoArray;
    }

    /**
     *
     * @return
     * @param viewArray
     * @param isDelaySet
     */
    public Animator[] buildHideAnimator(@NonNull View[] viewArray, boolean isDelaySet) {
        Animator[] animatoArray = new Animator[viewArray.length];
        int i = 0;
        for (View view: viewArray) {
            animatoArray[i] = buildHideAnimator(view, isDelaySet);
            i++;
        }
        return animatoArray;
    }

    /**
     *
     * @return
     * @param view
     * @param isDelaySet
     */
    public Animator buildShowAnimator(@NonNull View view, boolean isDelaySet) {
        Animator animator = buildAlphaAnimator(view, 0f, 1f);
        animator.setStartDelay(isDelaySet ? START_DELAY : 0);
        return animator;
    }

    /**
     *
     * @return
     * @param view
     * @param isDelaySet
     */
    public Animator buildHideAnimator(@NonNull View view, boolean isDelaySet) {
        Animator animator = buildAlphaAnimator(view, 1f, 0.1f);
        animator.setStartDelay(isDelaySet ? START_DELAY : 0);
        return animator;
    }

    /**
     *
     * @param view
     * @param endAlpha
     * @param startAlpha
     * @return
     */
    private Animator buildAlphaAnimator(@NonNull View view, float endAlpha, float startAlpha) {
        Animator animator = ObjectAnimator.ofFloat(view, ALPHA, startAlpha, endAlpha);
        animator.setDuration(duration);
        return animator;
    }

    /**
     *
     * @param view
     * @param colorFrom
     * @param colorTo
     * @return
     */
    public ValueAnimator buildColorAnimator(@NonNull final View view, int colorFrom, int colorTo) {
        ValueAnimator animator = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
        animator.setDuration(duration);
        animator.addUpdateListener(valueAnimator -> view.setBackgroundColor((int) valueAnimator.getAnimatedValue()));
        return animator;
    }

    public Animator buildCollapseAnimator(final View view, boolean isDelay) {
        Animator animation = getYTranslation(view, 0, -300, 0);
        animation.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }
            @Override
            public void onAnimationEnd(Animator animation) {
                view.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        return animation;
    }

    public Animator buildExpandAnimator(View view, boolean b) {
        Animator animation = getYTranslation(view, -300, 0, 0);
        animation.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                view.setVisibility(View.VISIBLE);
            }
            @Override
            public void onAnimationEnd(Animator animation) {
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        return animation;
    }

    /**
     *
     */
    public interface OnRevealAnimationListener {
        void omRevealAnimationEnd();
    }

//    /**
//     *
//     * @param fab
//     * @param collapsing
//     */
//    public void collapseViews(View fab, final boolean collapsing) {
//        Animator fabAnimator = collapsing ?
//                AnimationBuilder.getInstance(ctx)
//                        .buildHideAnimator(fab, false) :
//                AnimationBuilder.getInstance(ctx)
//                        .buildShowAnimator(fab, false);
//        fabAnimator.start();
//    }

}
