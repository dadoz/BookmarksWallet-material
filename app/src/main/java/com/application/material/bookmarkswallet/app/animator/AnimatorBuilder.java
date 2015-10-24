package com.application.material.bookmarkswallet.app.animator;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;

import java.lang.ref.WeakReference;

/**
 * Created by davide on 24/10/15.
 */
public class AnimatorBuilder {
    private static final String TRANSLATION_Y = "translationY";
    private static final String ALPHA = "alpha";
    private static final long START_DELAY = 300;
    private final int duration;

    /**
     *
     * @param contextWeakReference
     * @return
     */
    public static AnimatorBuilder getInstance(WeakReference<Context> contextWeakReference) {
        return new AnimatorBuilder(contextWeakReference.get().
                getResources().getInteger(android.R.integer.config_mediumAnimTime));
    }

    private AnimatorBuilder(int duration) {
        this.duration = duration;
    }

    /**
     *
     * @param view
     * @param endY
     * @param startY
     * @param isDelaySet
     * @return
     */
    public Animator getYTranslation(@NonNull View view, float startY, float endY, boolean isDelaySet) {
        Animator animator = ObjectAnimator.ofFloat(view, TRANSLATION_Y, startY, endY);
        animator.setDuration(duration);
        animator.setStartDelay(isDelaySet ? START_DELAY : 0);
        return animator;
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
     * @param view
     * @param isDelaySet
     */
    public Animator buildShowAnimator(@NonNull View view, boolean isDelaySet) {
        Animator animator = buildAlphaAnimator(view, 0f, 1f);
        animator.setStartDelay(isDelaySet ? START_DELAY : 0);
        return  animator;
    }

    /**
     *
     * @return
     * @param view
     * @param isDelaySet
     */
    public Animator buildHideAnimator(@NonNull View view, boolean isDelaySet) {
        Animator animator = buildAlphaAnimator(view, 1f, 0f);
        animator.setStartDelay(isDelaySet ? START_DELAY : 0);
        return  animator;
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
}
